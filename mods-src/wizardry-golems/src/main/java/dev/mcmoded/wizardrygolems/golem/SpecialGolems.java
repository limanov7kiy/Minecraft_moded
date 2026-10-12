package dev.mcmoded.wizardrygolems.golem;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.core.registry.EBRegistries;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.mcmoddev.golems.entity.GolemBase;
import dev.mcmoded.wizardrygolems.WizardryGolems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/// Особые способности големов Wizardry Golems (оригинал — классы `Entity*GolemMinion` WinDanesz). Тип голема —
/// id Extra Golems; поведение навешивается по нему: цели каста — при входе в мир, ауры — в тике.
public final class SpecialGolems {
    private SpecialGolems() {}

    /// Заклинания оригинала; id ищется в Redux (`ebwizardry`), затем в Wizardry Restored. Отсутствующие пропускаются.
    private record Caster(List<String> spells, float range, int baseCooldown) {}

    private static final Map<String, Caster> CASTERS = Map.of(
            "flame", new Caster(List.of("fire_breath", "fireskin", "firebolt", "disintegration", "ring_of_fire"), 15, 5),
            "winter", new Caster(List.of("blizzard", "frost_ray", "ice_charge", "ice_lance", "ice_spikes"), 15, 30),
            "spark", new Caster(List.of("chain_lightning", "lightning_arrow"), 15, 30),
            "lodestone", new Caster(List.of("lightning_arrow"), 15, 3),
            "maelstrom", new Caster(List.of("lightning_arrow", "lightning_ray"), 15, 5));

    static Optional<String> wgType(Object entity) {
        if (!(entity instanceof GolemBase golem)) return Optional.empty();
        return golem.getGolemId().filter(id -> id.getNamespace().equals(WizardryGolems.MOD_ID)).map(ResourceLocation::getPath);
    }

    static boolean isType(Object entity, String type) {
        if (!(entity instanceof GolemBase golem)) return false;
        return golem.getGolemId().map(id -> id.getPath().equals(type)).orElse(false);
    }

    static List<Spell> resolve(List<String> ids) {
        IForgeRegistry<Spell> reg = RegistryManager.ACTIVE.getRegistry(EBRegistries.SPELL);
        List<Spell> out = new ArrayList<>();
        for (String id : ids) {
            Spell s = reg.getValue(new ResourceLocation("ebwizardry", id));
            if (s == null) s = reg.getValue(new ResourceLocation("wizardry_restored", id));
            if (s != null) out.add(s);
        }
        return out;
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        wgType(event.getEntity()).map(CASTERS::get).ifPresent(c -> {
            GolemBase golem = (GolemBase) event.getEntity();
            List<Spell> spells = resolve(c.spells());
            golem.goalSelector.addGoal(1, new GolemSpellGoal(golem, () -> spells, 0.5, c.range(), c.baseCooldown(), 140));
        });
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity e = event.getEntity();
        if (e.level().isClientSide || !(e instanceof GolemBase golem)) return;
        if (isType(golem, "flame") && golem.tickCount % 20 == 0) {
            golem.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false));
        }
        if (isType(golem, "thunderstone") && golem.tickCount % 40 == 0) shockAura(golem);
        // Permafrost: оставляет след тающей мерзлоты (блок Permafrost Redux, тает через 5 с, как от заклинания)
        if (isType(golem, "permafrost") && golem.onGround() && golem.level().isEmptyBlock(golem.blockPosition())
                && golem.level().getBlockState(golem.blockPosition().below()).isFaceSturdy(golem.level(), golem.blockPosition().below(), net.minecraft.core.Direction.UP)) {
            var block = com.binaris.wizardry.setup.registries.EBBlocks.PERMAFROST.get();
            golem.level().setBlock(golem.blockPosition(), block.defaultBlockState(), 3);
            golem.level().scheduleTick(golem.blockPosition(), block, 100);
        }
        // Золотой голем лечит союзников (в оригинале задумано, но условие события было перевёрнуто — не срабатывало)
        if (isType(golem, "gold") && golem.getRandom().nextInt(100) == 0 && isMinion(golem)) {
            resolve(List.of("group_heal")).forEach(s -> s.cast(new EntityCastContext(golem.level(), golem,
                    InteractionHand.MAIN_HAND, 0, golem, new SpellModifiers())));
        }
    }

    /// Thunderstone: раз в 2 с бьёт током 4 ед. всех враждебных рядом и отбрасывает (оригинал: радиус 2 у малого, 1 у большого).
    private static void shockAura(GolemBase golem) {
        double r = golem.isBaby() ? 2 : 1;
        for (LivingEntity t : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(r))) {
            if (t == golem || !AllyDesignation.isValidTarget(golem, t)) continue;
            MagicDamageSource.causeMagicDamage(golem, t, 4, EBDamageSources.SHOCK);
            Vec3 d = t.position().subtract(golem.position()).multiply(1, 0, 1);
            if (d.lengthSqr() > 1e-4) {
                d = d.normalize().scale(1.5);
                t.setDeltaMovement(d.x, 0, d.z);
                t.hurtMarked = true;
            }
        }
    }

    /// Spark: удар — магический урон, игнорирующий броню (в оригинале `DamageSource.MAGIC` вместо обычного удара).
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof GolemBase golem) || !isType(golem, "spark")) return;
        if (event.getSource().getMsgId().equals("mob")) {
            event.setCanceled(true);
            MagicDamageSource.causeMagicDamage(golem, event.getEntity(), event.getAmount(), EBDamageSources.MAGIC);
        }
    }

    static boolean isMinion(GolemBase golem) {
        return com.binaris.wizardry.core.platform.Services.OBJECT_DATA.getMinionData(golem).isSummoned();
    }
}

package dev.mcmoded.wizardrygolems.spell;

import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import com.binaris.wizardry.core.platform.Services;
import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;
import java.util.function.Function;

/// Общая часть заклинаний Golemancy (оригинал — `spell/Golemancy.java` WinDanesz). В 1.12.2 на каждый тип голема был
/// свой класс-миньон; в Extra Golems 20.1 голем один (`GolemBase`), тип задаётся id из данных `golems/golem/*.json`,
/// а «миньонность» даёт `MinionData` Redux (владелец, время жизни, следование) — как у любого моба в `MinionSpell`.
public class Golemancy extends MinionSpell<GolemBase> {
    /// Что призвать: тип голема (id Extra Golems или наш `wizardrygolems:*`) и сколько штук.
    public record Summon(ResourceLocation type, int count, boolean baby) {
        public static Summon of(String type, int count) {
            return new Summon(new ResourceLocation(type), count, false);
        }

        /// Маленькие големы (в оригинале `isChild`): в Extra Golems — `setBaby`, как дети расколовшегося голема.
        public static Summon small(String type, int count) {
            return new Summon(new ResourceLocation(type), count, true);
        }
    }

    private final Function<CastContext, List<Summon>> plan;
    /// Множитель времени жизни от артефактов (оригинал — `getLifeTimeModifiers`, например кольцо огненной големантии ×2).
    private java.util.function.ToDoubleFunction<CastContext> lifetimeFactor = ctx -> 1;

    public Golemancy lifetimeFactor(java.util.function.ToDoubleFunction<CastContext> factor) {
        this.lifetimeFactor = factor;
        return this;
    }

    /// @param fallback тип для каста без игрока (раздатчик, NPC) — Redux тогда спавнит `MINION_COUNT` таких.
    /// @param plan     состав призыва для игрока/моба (артефакты и палочка могут его менять).
    public Golemancy(ResourceLocation fallback, Function<CastContext, List<Summon>> plan) {
        super(level -> GolemBase.create(level, fallback));
        this.plan = plan;
    }

    @Override
    protected boolean spawnMinions(CastContext ctx) {
        if (ctx.world().isClientSide) return true;
        int range = this.property(DefaultProperties.SUMMON_RADIUS);
        int spawned = 0;
        for (Summon s : plan.apply(ctx)) {
            for (int i = 0; i < s.count(); i++) {
                BlockPos pos = BlockUtil.findNearbyFloorSpace(ctx.caster(), range, range * 2);
                if (pos == null) return spawned > 0;
                spawnGolem(ctx, s, pos, spawned++);
            }
        }
        return spawned > 0;
    }

    /// Повторяет `MinionSpell.spawnMinions` Redux 0.8.9 для одного голема заданного типа.
    protected void spawnGolem(CastContext ctx, Summon summon, BlockPos pos, int index) {
        GolemBase golem = GolemBase.create(ctx.world(), summon.type());
        if (summon.baby()) golem.setBaby(true);
        golem.setCustomName(Component.translatable("entity.ebwizardry.minion_name", ctx.caster().getDisplayName(), golem.getDisplayName()));
        golem.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        MinionData data = Services.OBJECT_DATA.getMinionData(golem);
        data.setSummoned(true);
        data.setOwnerUUID(ctx.caster().getUUID());
        int lifetime = (int) (ctx.modifiers().get("ebwizardry.duration", this.property(DefaultProperties.MINION_LIFETIME))
                * lifetimeFactor.applyAsDouble(ctx));
        if (PermanentGolem.claim(ctx.caster(), golem)) lifetime = -1; // Ring of the Golemancer: без срока жизни
        setLifetime(golem, lifetime);
        if (summon.type().getPath().equals("furnace")) golem.setFuel(1600); // как в оригинале: печному — запас топлива
        data.setShouldFollowOwner(this.shouldFollowOwner);
        data.setShouldDeleteGoals(this.shouldDeleteBaseGoals);
        data.setSearchNearbyTargets(true);
        AttributeInstance attack = golem.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) attack.addPermanentModifier(new AttributeModifier("ebwizardry.potency",
                ctx.modifiers().getFactor("ebwizardry.potency") - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        AttributeInstance health = golem.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) health.addPermanentModifier(new AttributeModifier("ebwizardry.health_modifier",
                ctx.modifiers().getFactor("ebwizardry.health_modifier") - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        golem.setHealth(golem.getMaxHealth());
        if (ctx.world() instanceof ServerLevel server) {
            golem.finalizeSpawn(server, server.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
        }
        addMinionExtras(golem, ctx, index);
        ctx.world().addFreshEntity(golem);
    }
}

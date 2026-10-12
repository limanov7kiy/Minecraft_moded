package dev.mcmoded.wizardrygolems.golem;

import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.content.entity.construct.FrostSigilConstruct;
import com.binaris.wizardry.content.entity.construct.IceSpikeConstruct;
import com.binaris.wizardry.content.entity.projectile.IceShardEntity;
import com.binaris.wizardry.content.item.WandItem;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBAttributes;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.mcmoddev.golems.entity.GolemBase;
import dev.mcmoded.wizardrygolems.item.GolemItems;
import dev.mcmoded.wizardrygolems.spell.Golemancy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.UUID;

/// Эффекты артефактов Wizardry Golems на големах-миньонах (оригинал — `IEarthGolem`, `IFireGolem`, `IIceGolem`,
/// `ILightingGolem` WinDanesz). Стихию голема ставит `Golemancy` (`ELEMENT_TAG`), хозяин — `MinionData` Redux.
/// Вместо сущностей/эффектов, которых нет в Redux, — ближайшие аналоги (см. docs/wizardry-port.md).
public final class GolemArtifacts {
    private GolemArtifacts() {}

    private static final UUID POTENCY_ID = UUID.fromString("6b0d6c1e-6a52-4b0e-9a73-2f1f3c7a9e51");

    static String element(Entity e) {
        return e instanceof GolemBase g ? g.getPersistentData().getString(Golemancy.ELEMENT_TAG) : "";
    }

    static Player owner(GolemBase golem) {
        MinionData data = Services.OBJECT_DATA.getMinionData(golem);
        return data.isSummoned() && data.getOwner() instanceof Player p ? p : null;
    }

    static boolean has(Player p, RegistryObject<Item> artifact) {
        return p != null && ArtifactChannel.isEquipped(p, artifact.get());
    }

    static boolean wandOf(Player p, String element) {
        for (ItemStack s : List.of(p.getMainHandItem(), p.getOffhandItem())) {
            if (s.getItem() instanceof WandItem w && w.getElement().getLocation().getPath().equals(element)) return true;
        }
        return false;
    }

    static Spell spell(String ns, String id) {
        return net.minecraftforge.registries.RegistryManager.ACTIVE
                .getRegistry(com.binaris.wizardry.core.registry.EBRegistries.SPELL).getValue(new ResourceLocation(ns, id));
    }

    // ---- смерть голема ----
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof GolemBase golem) || !(golem.level() instanceof ServerLevel level)) return;
        String el = element(golem);
        Player p = owner(golem);
        if (el.isEmpty() || p == null) return;
        BlockPos pos = golem.blockPosition();
        switch (el) {
            case "earth" -> {
                // Gaia's Anger: 15% (описание — 10%) — землетрясение
                if (has(p, GolemItems.AMULET_GAIA) && level.random.nextFloat() < 0.15f) cast(spell("wizardry_restored", "earthquake"), golem);
                // Amulet of Brambles: ловушки-снейры на месте голема и вокруг
                if (has(p, GolemItems.AMULET_SNARE)) {
                    EntityType<?> snare = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("wizardry_restored", "snare"));
                    if (snare != null) {
                        spawnConstruct(snare, level, pos, p);
                        for (Direction d : Direction.Plane.HORIZONTAL) if (level.random.nextBoolean()) spawnConstruct(snare, level, pos.relative(d), p);
                    }
                }
                // Deathweed: в лесу (или 10%) — Forest's Curse
                if (has(p, GolemItems.AMULET_DEATHWEED) && (level.getBiome(pos).is(BiomeTags.IS_FOREST) || level.random.nextFloat() < 0.1f))
                    cast(spell("ebwizardry", "forests_curse"), golem);
            }
            case "fire" -> {
                if (has(p, GolemItems.AMULET_STEAMING_NETHERRACK)) {
                    float power = golem.getGolemId().map(id -> id.getPath().equals("furnace")).orElse(false) ? 3 : 1.3f;
                    level.explode(golem, golem.getX(), golem.getY() + 1, golem.getZ(), power, Level.ExplosionInteraction.NONE);
                }
            }
            case "ice" -> {
                if (has(p, GolemItems.CHARM_FROZEN_MARK)) {
                    for (Direction d : Direction.Plane.HORIZONTAL) {
                        BlockPos at = pos.relative(d, 2);
                        FrostSigilConstruct sigil = new FrostSigilConstruct(level);
                        sigil.setCaster(p);
                        sigil.setPos(at.getX() + 0.5, at.getY(), at.getZ() + 0.5);
                        level.addFreshEntity(sigil);
                    }
                }
                if (has(p, GolemItems.AMULET_BROKEN_ICE) && level.random.nextFloat() < 0.5f) {
                    for (int i = 0; i < 8; i++) {
                        double dx = level.random.nextDouble() - 0.5, dy = level.random.nextDouble() - 0.5, dz = level.random.nextDouble() - 0.5;
                        IceShardEntity shard = new IceShardEntity(level);
                        shard.setPos(golem.getX() + dx + Math.signum(dx) * golem.getBbWidth(), golem.getY() + golem.getBbHeight() / 2 + dy,
                                golem.getZ() + dz + Math.signum(dz) * golem.getBbWidth());
                        shard.setDeltaMovement(dx * 1.5, dy * 1.5, dz * 1.5);
                        shard.setOwner(p);
                        level.addFreshEntity(shard);
                    }
                }
            }
            case "lightning" -> {
                if (has(p, GolemItems.AMULET_RAGING_SKIES) && level.canSeeSky(pos.above())) {
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                    if (bolt != null) {
                        bolt.moveTo(golem.getX(), golem.getY(), golem.getZ());
                        bolt.setVisualOnly(false);
                        if (p instanceof net.minecraft.server.level.ServerPlayer sp) bolt.setCause(sp);
                        level.addFreshEntity(bolt);
                    }
                }
            }
            default -> {}
        }
    }

    private static void spawnConstruct(EntityType<?> type, ServerLevel level, BlockPos pos, Player owner) {
        if (!level.getBlockState(pos).canBeReplaced()) return;
        Entity e = type.create(level);
        if (e == null) return;
        if (e instanceof MagicConstructEntity c) c.setCaster(owner);
        e.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        level.addFreshEntity(e);
    }

    private static void cast(Spell spell, GolemBase golem) {
        if (spell != null) spell.cast(new EntityCastContext(golem.level(), golem, InteractionHand.MAIN_HAND, 0, null, new SpellModifiers()));
    }

    // ---- удар голема ----
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof GolemBase golem) || golem.level().isClientSide) return;
        String el = element(golem);
        Player p = owner(golem);
        if (el.isEmpty() || p == null) return;
        LivingEntity target = event.getEntity();
        var rnd = golem.getRandom();
        if (el.equals("ice")) {
            // Ring of Frostbite: 50% — Frost
            if (has(p, GolemItems.RING_FROSTBITE) && rnd.nextFloat() < 0.5f) target.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 100, 0));
            // Jagged Sapphire: 15% — ледяные шипы вокруг цели
            if (has(p, GolemItems.AMULET_JAGGED_SAPPHIRE) && rnd.nextFloat() < 0.15f && golem.level() instanceof ServerLevel level) {
                for (int i = 0; i < 5; i++) {
                    double x = target.getX() + 2 - rnd.nextFloat() * 4, z = target.getZ() + 2 - rnd.nextFloat() * 4;
                    int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) Math.floor(x), (int) Math.floor(z));
                    if (Math.abs(y - target.getY()) > 3) continue;
                    IceSpikeConstruct spike = new IceSpikeConstruct(level);
                    spike.setFacing(Direction.UP);
                    spike.setCaster(p);
                    spike.setLifetime(90);
                    spike.setPos(x, y, z);
                    level.addFreshEntity(spike);
                }
            }
            // Coldlink Belt: хозяину +0,5 HP за удар, если на нём Ice Shroud
            if (has(p, GolemItems.BELT_COLDLINK) && iceShroud(p)) p.heal(0.5f);
        }
        // Dried Mushroom Charm: грибные големы отравляют
        if (has(p, GolemItems.CHARM_DRIED_MUSHROOM) && golem.getGolemId().map(id -> id.getPath().endsWith("_mushroom")).orElse(false))
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
    }

    // ---- ауры (раз в 10 тиков) ----
    @SubscribeEvent
    public static void onGolemTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof GolemBase golem) || golem.level().isClientSide || golem.tickCount % 10 != 0) return;
        String el = element(golem);
        Player p = el.isEmpty() ? null : owner(golem);
        if (p == null) return;
        boolean near = golem.distanceTo(p) < 16;
        switch (el) {
            case "fire" -> {
                if (has(p, GolemItems.RING_FLAME_TRAIL) && golem.onGround() && golem.level().isEmptyBlock(golem.blockPosition())) {
                    golem.level().playSound(null, golem.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1, golem.getRandom().nextFloat() * 0.4f + 0.8f);
                    golem.level().setBlock(golem.blockPosition(), Blocks.FIRE.defaultBlockState(), 11);
                }
                if (has(p, GolemItems.CHARM_IFRIT_BOTTLE) && near) {
                    golem.addEffect(new MobEffectInstance(EBMobEffects.FIRESKIN.get(), 40, 0, true, true));
                    if (wandOf(p, "fire")) p.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, true, true));
                }
            }
            case "ice" -> {
                if (has(p, GolemItems.BODY_FROST_CLOAK) && near) {
                    setIceShroud(golem, 40);
                    if (wandOf(p, "ice")) setIceShroud(p, 40);
                }
            }
            case "lightning" -> {
                if (has(p, GolemItems.CHARM_STATIC_AURA) && near) {
                    golem.addEffect(new MobEffectInstance(EBMobEffects.STATIC_AURA.get(), 40, 0, true, true));
                    if (wandOf(p, "lightning")) p.addEffect(new MobEffectInstance(EBMobEffects.STATIC_AURA.get(), 40, 0, true, true));
                }
            }
            case "earth" -> {
                // Glisterring: дынный голем лечит хозяина, пока тот в бою
                if (has(p, GolemItems.RING_GLISTERING) && near && golem.getGolemId().map(id -> id.getPath().equals("melon")).orElse(false)
                        && p.tickCount - p.getLastHurtByMobTimestamp() < 200 && golem.tickCount % 40 == 0) p.heal(1);
            }
            default -> {}
        }
    }

    /// Fire Elemental Heart: рядом со своим огненным големом и с огненной палочкой — сила заклинаний +15%
    /// (в оригинале — эффект Empowerment, в Redux его нет; используется атрибут CAST_POTENCY, как в Wizardry Restored).
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player p = event.player;
        if (event.phase != TickEvent.Phase.END || p.level().isClientSide || p.tickCount % 10 != 0) return;
        AttributeInstance attr = p.getAttribute(EBAttributes.CAST_POTENCY.get());
        if (attr == null) return;
        boolean active = has(p, GolemItems.CHARM_FIRE_GOLEMANCY_POTENCY) && wandOf(p, "fire")
                && !p.level().getEntitiesOfClass(GolemBase.class, p.getBoundingBox().inflate(16),
                g -> "fire".equals(element(g)) && owner(g) == p).isEmpty();
        boolean present = attr.getModifier(POTENCY_ID) != null;
        if (active && !present) attr.addTransientModifier(new AttributeModifier(POTENCY_ID, "Fire golemancy potency", 0.15, AttributeModifier.Operation.MULTIPLY_TOTAL));
        else if (!active && present) attr.removeModifier(POTENCY_ID);
    }

    // Ice Shroud — состояние Wizardry Restored (persistentData `wizardry_restored`), если он установлен
    private static void setIceShroud(LivingEntity e, int ticks) {
        CompoundTag root = e.getPersistentData().getCompound("wizardry_restored");
        root.putLong("ice_shroud_until", e.level().getGameTime() + ticks);
        root.putInt("ice_shroud_frost", 100);
        e.getPersistentData().put("wizardry_restored", root);
    }

    private static boolean iceShroud(LivingEntity e) {
        CompoundTag root = e.getPersistentData().getCompound("wizardry_restored");
        return root.getLong("ice_shroud_until") > e.level().getGameTime();
    }
}

package dev.mcmoded.undeadrising.event;

import dev.mcmoded.undeadrising.entity.projectile.FurBombEntity;
import dev.mcmoded.undeadrising.item.FurWeaponItem;
import dev.mcmoded.undeadrising.item.VespaShieldItem;

import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.entity.MimicEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import dev.mcmoded.undeadrising.entity.ParasiteEntity;
import dev.mcmoded.undeadrising.entity.flying.VespaEntity;
import dev.mcmoded.undeadrising.entity.unburied.UnburiedEntity;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurParticles;
import dev.mcmoded.undeadrising.registry.FurTags;
import dev.mcmoded.undeadrising.util.FurSpawnUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/// Мобовые правила из общего обработчика событий оригинала (оригинал: Fish's Undead Rising (Fish),
/// `events/EventHandler`). Сюда же — такие правила для мобов следующих партий (Scarab, Lil Sludge и т. п.).
/// Шина FORGE.
public final class FurEntityEvents {
    private FurEntityEvents() {}

    /// pSpawnRate_Parasite (%), Parasite_SandSpawn (%), Parasite_InfestedAmpSpawns, pBeeConvertRate_Vespa (%) по умолчанию.
    public static final int PARASITE_SPAWN_RATE = 10, PARASITE_SAND_SPAWN = 2, PARASITE_INFESTED_AMP_SPAWNS = 4, BEE_CONVERT_RATE = 10;

    /// Из умершего заражённого (эффект «Заражение», паразит на нём или с шансом 10% — цель паразитов из тега)
    /// вылезают паразиты: вариант — как у сидевшего паразита, иначе по биому (сухой — пустынный, джунгли — джунглей),
    /// убитые веспой — личинки джунглей (у прирученной веспы — призванные).
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || entity.isInWaterOrBubble()) return;
        Entity rider = FurSpawnUtil.gotRiderEntity(entity.getPassengers(), FurEntities.PARASITE.get());
        MobEffectInstance infested = entity.getEffect(FurEffects.INFESTED.get());
        RandomSource random = level.random;
        boolean tagged = entity.getType().is(FurTags.PARASITE_TARGETS) && random.nextInt(100) < PARASITE_SPAWN_RATE;
        if (!tagged && rider == null && infested == null) return;

        int base = 3 + random.nextInt(3);
        int amp = infested != null ? Math.min(PARASITE_INFESTED_AMP_SPAWNS, infested.getAmplifier()) : 0;
        Entity killer = event.getSource().getDirectEntity();
        var biome = level.getBiome(entity.blockPosition());
        for (int i = 0; i < base + (amp - 1) * (1 + random.nextInt(3)); i++) {
            float ox = ((i % 2) - 0.5F) / 4.0F;
            float oz = ((i / 2) - 0.5F) / 4.0F;
            ParasiteEntity parasite = FurSpawnUtil.trySpawnEntity(FurEntities.PARASITE.get(), level,
                    BlockPos.containing(entity.getX() + ox, entity.getY() + 1.0, entity.getZ() + oz));
            if (parasite == null) continue;
            if (rider instanceof ParasiteEntity p) {
                parasite.setSkin(p.getSkin());
            } else if (biome.is(Tags.Biomes.IS_DRY)) {
                parasite.setSkin(1);
            } else if (biome.is(BiomeTags.IS_JUNGLE)) {
                parasite.setSkin(2);
            } else if (killer instanceof VespaEntity vespa) {
                parasite.setSkin(2);
                if (vespa.isTame()) parasite.setSummoned(true);
            } else {
                parasite.setSkin(0);
            }
        }
    }

    /// Разбитый песок в жаркой сухой песчаной местности Верхнего мира — с шансом 2% вылезает пустынный паразит.
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !event.getState().is(BlockTags.SAND)) return;
        var biome = level.getBiome(event.getPos());
        if (biome.is(Tags.Biomes.IS_HOT_OVERWORLD) && biome.is(Tags.Biomes.IS_DRY_OVERWORLD) && biome.is(Tags.Biomes.IS_SANDY)
                && level.random.nextInt(100) < PARASITE_SAND_SPAWN) {
            ParasiteEntity parasite = FurSpawnUtil.trySpawnEntity(FurEntities.PARASITE.get(), level, event.getPos().above());
            if (parasite != null) {
                parasite.setSkin(1);
                parasite.setDeltaMovement(parasite.getDeltaMovement().add(0.0, 0.4, 0.0));
            }
        }
    }

    /// Пчела, в которую ударила молния, с шансом 10% становится веспой (вариант 1).
    @SubscribeEvent
    public static void onLightning(EntityStruckByLightningEvent event) {
        if (!(event.getEntity() instanceof Bee bee) || !(bee.level() instanceof ServerLevel level) || level.getDifficulty() == Difficulty.PEACEFUL) return;
        if (level.random.nextInt(100) >= BEE_CONVERT_RATE) return;
        VespaEntity vespa = FurEntities.VESPA.get().create(level);
        if (vespa == null) return;
        vespa.moveTo(bee.blockPosition(), 0.0F, 0.0F);
        vespa.finalizeSpawn(level, level.getCurrentDifficultyAt(vespa.blockPosition()), MobSpawnType.CONVERSION, null, null);
        vespa.setSkin(1);
        vespa.setPersistenceRequired();
        level.addFreshEntity(vespa);
        bee.discard();
    }

    /// Частицы «Страха» над испуганными: у игроков — раз в 32 тика, у мобов — раз в 20.
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || !entity.hasEffect(FurEffects.FEAR.get())) return;
        boolean due = entity instanceof Player ? (level.getGameTime() & 31L) == 0L : entity.tickCount % 20 == 0;
        if (due) {
            RandomSource r = entity.getRandom();
            level.sendParticles(FurParticles.FEAR.get(), entity.getRandomX(1.0), entity.getRandomY() + 1.0, entity.getRandomZ(1.0), 15,
                    r.nextGaussian() * 0.02, r.nextGaussian() * 0.02, r.nextGaussian() * 0.02, 0.0);
        }
    }

    /// Доп. урон призванных Unburied (чары оружия призыва).
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof UnburiedEntity unburied) {
            event.setAmount(event.getAmount() + unburied.getBonusDamage(event.getEntity()));
        }
        FurWeaponItem.onHurt(event);
        FurBombEntity.onExplosionHurt(event);
    }

    /// Щит Vespa колет того, чей удар заблокирован.
    @SubscribeEvent
    public static void onShieldBlock(net.minecraftforge.event.entity.living.ShieldBlockEvent event) {
        VespaShieldItem.onBlock(event);
    }

    /// Вампиризм: хозяин призванного Unburied лечится на 5% нанесённого урона за уровень.
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (event.getSource().getDirectEntity() instanceof UnburiedEntity unburied && unburied.getLifestealLevel() > 0) {
            LivingEntity owner = unburied.getOwner();
            if (owner != null) owner.heal(event.getAmount() * unburied.getLifestealLevel() * 0.05F);
        }
    }

    /// Жители убегают от Unburied-семейства.
    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof Villager villager) {
            villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, UnburiedEntity.class, 8.0F, 0.8, 0.8));
        }
    }

    /// Миньоны, погибшие вместе с хозяином-мобом (тег `FUR_noLoot`), ничего не роняют.
    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (event.getEntity().getTags().contains("FUR_noLoot")) event.setCanceled(true);
    }

    /// Тотем бессмертия в инвентаре мимикраба спасает его от смерти (как у игрока).
    @SubscribeEvent
    public static void onMimicDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof MimicEntity mimic)) return;
        int slot = mimic.containsItem(Items.TOTEM_OF_UNDYING);
        if (slot == -1) return;
        mimic.setHealth(1.0F);
        mimic.removeAllEffects();
        mimic.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        mimic.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        mimic.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        mimic.level().broadcastEntityEvent(mimic, (byte) 35);
        event.setCanceled(true);
        mimic.inventory.getItem(slot).shrink(1);
    }

    /// pSpawnRate_DeathMimic по умолчанию (из 1000).
    public static final int DEATH_MIMIC_RATE = 250;

    /// С шансом 25% у места гибели игрока (если рядом нет других мимикрабов) из облака появляется мимикраб — и
    /// собирает выпавшие вещи. В 1.16.5 оригинал создавал облако, но забывал добавить мимикраба в мир; здесь он появляется.
    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent event) {
        if (event.isCanceled() || !(event.getEntity() instanceof Player player) || !(player.level() instanceof ServerLevel level)) return;
        RandomSource random = level.random;
        if (random.nextInt(1000) > DEATH_MIMIC_RATE) return;
        if (!level.getEntitiesOfClass(MimicEntity.class, new AABB(player.blockPosition()).inflate(16.0)).isEmpty()) return;
        int x = Mth.floor(player.getX() + random.nextInt(8) - 4.0);
        int z = Mth.floor(player.getZ() + random.nextInt(8) - 4.0);
        BlockPos spawn = new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z), z);
        AreaEffectCloud cloud = new AreaEffectCloud(level, x, spawn.getY(), z);
        cloud.setRadius(5.0F);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(5);
        cloud.setDuration(20);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
        cloud.setParticle(ParticleTypes.ENTITY_EFFECT);
        cloud.setFixedColor(7297852);
        level.addFreshEntity(cloud);
        MimicEntity mimic = FurEntities.MIMIC.get().create(level);
        if (mimic == null) return;
        mimic.setPersistenceRequired();
        mimic.moveTo(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(mimic);
        mimic.playAmbientSound();
    }
}

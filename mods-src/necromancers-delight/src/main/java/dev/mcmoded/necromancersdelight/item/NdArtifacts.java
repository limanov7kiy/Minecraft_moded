package dev.mcmoded.necromancersdelight.item;

import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import dev.mcmoded.necromancersdelight.effect.NdEffects;
import dev.mcmoded.necromancersdelight.entity.ManaLeech;
import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.entity.BoneWormEntity;
import dev.mcmoded.undeadrising.entity.ForsakenEntity;
import dev.mcmoded.undeadrising.entity.WetaEntity;
import dev.mcmoded.undeadrising.entity.flying.VespaEntity;
import dev.mcmoded.undeadrising.entity.projectile.CactusThornEntity;
import dev.mcmoded.undeadrising.entity.unburied.MummyEntity;
import dev.mcmoded.undeadrising.entity.unburied.UnburiedEntity;
import dev.mcmoded.undeadrising.registry.FurBannerPatterns;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

/// Эффекты артефактов Necromancer's Delight (оригинал — `NDEventHandler`, `ItemMaliceWard`, `ItemPestCrown`,
/// WinDanesz). «Нежить» — `MobType.UNDEAD`; призванные Redux зомби и скелеты — ванильные `Zombie`/`Husk` и
/// `Skeleton`/`Stray` с `MinionData` (в оригинале — `EntityZombieMinion`/`EntitySkeletonMinion`).
public final class NdArtifacts {
    private NdArtifacts() {}

    static boolean has(Player p, RegistryObject<Item> a) {
        return ArtifactChannel.isEquipped(p, a.get());
    }

    static boolean undead(Object e) {
        return e instanceof LivingEntity l && l.getMobType() == MobType.UNDEAD;
    }

    /// Хозяин-игрок призванного моба (или `null`).
    static Player summoner(Entity e) {
        if (!(e instanceof Mob mob)) return null;
        MinionData data = Services.OBJECT_DATA.getMinionData(mob);
        return data != null && data.isSummoned() && data.getOwner() instanceof Player p ? p : null;
    }

    /// Amulet of Tenacity / Vigour: полный иммунитет к замедлению / слабости.
    @SubscribeEvent
    public static void onEffect(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player p)) return;
        var eff = event.getEffectInstance().getEffect();
        if (eff == MobEffects.MOVEMENT_SLOWDOWN && has(p, NdItems.AMULET_SLOWNESS_IMMUNITY)
                || eff == MobEffects.WEAKNESS && has(p, NdItems.AMULET_WEAKNESS_IMMUNITY)) {
            event.setResult(Event.Result.DENY);
        }
    }

    // --- Frigidbone Ring: морозное зачарование призванного ледяного кинжала ---

    public static final String FREEZING_TAG = "necromancersdelight_freezing";

    public static void markFreezing(ItemStack stack) {
        stack.getOrCreateTag().putBoolean(FREEZING_TAG, true);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        Entity attacker = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        // Necromantic Ward: урон от нежити −15%.
        if (target instanceof Player p && undead(attacker) && has(p, NdItems.AMULET_NECROMANTIC_WARD)) {
            event.setAmount(event.getAmount() * 0.85f);
        }
        if (target.level().isClientSide || attacker != event.getSource().getDirectEntity()) return;
        // Frigidbone Ring: кинжал с меткой морозит цель (как Freezing Weapon Wizardry 1.12.2 I: Frost 10 с).
        if (attacker instanceof LivingEntity l) {
            ItemStack held = l.getMainHandItem();
            if (held.hasTag() && held.getTag().getBoolean(FREEZING_TAG)) target.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 200, 0));
        }
        // Summon Vespa: укус призванной веспы снимает заражение паразитами.
        if (attacker instanceof VespaEntity vespa && summoner(vespa) != null) target.removeEffect(FurEffects.INFESTED.get());
    }

    // --- Malice Ward, Crown of Pests ---

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player p = event.player;
        if (event.phase != TickEvent.Phase.END || p.level().isClientSide || p.tickCount % 20 != 0) return;
        // Malice Ward: нежить в радиусе 5 — Поглощение III на 10 с, откат 60 с (настройки оригинала по умолчанию).
        Item ward = NdItems.AMULET_MALICE_WARD.get();
        if (!p.getCooldowns().isOnCooldown(ward) && has(p, NdItems.AMULET_MALICE_WARD)
                && !p.level().getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(5), NdArtifacts::undead).isEmpty()) {
            p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
            p.getCooldowns().addCooldown(ward, 1200);
        }
        pestCrown(p);
    }

    public static final String WETA_UUID = "necromancersdelight_weta";
    /// Откат короны после призыва новой веты (`head_pest_crown_cooldown` оригинала).
    public static final int PEST_CROWN_COOLDOWN = 800;

    /// Crown of Pests: при каждом надевании держит рядом прирученную бессрочную вету-миньона; погибла — новая
    /// через {@link #PEST_CROWN_COOLDOWN} тиков.
    static void pestCrown(Player p) {
        Item crown = NdItems.HEAD_PEST_CROWN.get();
        if (p.getCooldowns().isOnCooldown(crown) || !has(p, NdItems.HEAD_PEST_CROWN) || !(p.level() instanceof ServerLevel level)) return;
        CompoundTag data = p.getPersistentData();
        if (data.hasUUID(WETA_UUID)) {
            Entity old = level.getEntity(data.getUUID(WETA_UUID));
            if (old != null && old.isAlive()) return;
        }
        BlockPos pos = BlockUtil.findNearbyFloorSpace(p, 2, 4);
        if (pos == null) return;
        WetaEntity weta = FurEntities.WETA.get().create(level);
        if (weta == null) return;
        weta.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, p.getYRot(), 0);
        weta.tame(p);
        NdMinions.summon(weta, p, -1);
        level.addFreshEntity(weta);
        data.putUUID(WETA_UUID, weta.getUUID());
        p.getCooldowns().addCooldown(crown, PEST_CROWN_COOLDOWN);
    }

    // --- Подмена и усиление призванных существ ---

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof Mob mob)) return;
        Player player = summoner(mob);
        if (player == null) return;

        // Book of Witchcraft: гриб из второй руки — реагент для призванного.
        if (has(player, NdItems.CHARM_MUSHROOM_MINION)) {
            ItemStack off = player.getOffhandItem();
            boolean used = true;
            if (off.is(FurItems.VEIL_SHROOM.get())) mob.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 600));
            else if (off.is(FurItems.CORDY_SHROOM.get())) mob.addEffect(new MobEffectInstance(NdEffects.POISONOUS_SPORE.get(), 600));
            else if (off.is(FurItems.BLOODTOOTH_SHROOM.get())) mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600));
            else if (off.is(FurItems.GLOWSHROOM.get())) {
                mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
                mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600));
            } else used = false;
            if (used && !player.getAbilities().instabuild) off.shrink(1);
        }
        // Cursed Scarab: призванная нежить покрыта роем саранчи (Locusts — не горит на солнце).
        if (has(player, NdItems.CHARM_SCARAB) && undead(mob)) {
            mob.addEffect(new MobEffectInstance(NdEffects.LOCUSTS.get(), Integer.MAX_VALUE, 0, false, false));
        }

        EntityType<?> type = mob.getType();
        boolean zombie = type == EntityType.ZOMBIE || type == EntityType.HUSK;
        boolean skeleton = type == EntityType.SKELETON || type == EntityType.STRAY;
        // Malevolent Ushabti: призванный зомби — мумия.
        if (zombie && has(player, NdItems.CHARM_MUMMY_MINION)) {
            MummyEntity mummy = FurEntities.MUMMY.get().create(mob.level());
            if (mummy != null) {
                NdMinions.replace(mob, mummy);
                mob.level().addFreshEntity(mummy);
                event.setCanceled(true);
                return;
            }
        }
        // Ring of Hatred: призванный скелет — Forsaken (щит, меч, лук или всадник на костяной лошади).
        if (skeleton && has(player, NdItems.RING_FORSAKEN)) {
            if (spawnForsaken(mob, player)) {
                event.setCanceled(true);
                return;
            }
        }
        // Fallen Legion's Ring / Ring of the Nameless: призванный зомби — Unburied; оба кольца — ещё и предмет снаряжения.
        int rings = (has(player, NdItems.RING_NAMELESS) ? 1 : 0) + (has(player, NdItems.RING_LEGION) ? 1 : 0);
        if (zombie && rings > 0) {
            UnburiedEntity unburied = FurEntities.UNBURIED.get().create(mob.level());
            if (unburied == null) return;
            NdMinions.replace(mob, unburied);
            if (rings == 2) {
                boolean helmets = ArtifactChannel.isEquipped(player, EBItems.CHARM_UNDEAD_HELMETS.get());
                switch (mob.getRandom().nextInt(helmets ? 4 : 5)) {
                    case 0 -> unburied.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SHOVEL));
                    case 1 -> unburied.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
                    case 2 -> unburied.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
                    case 3 -> unburied.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
                    default -> unburied.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
                }
                if (helmets) unburied.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
            }
            mob.level().addFreshEntity(unburied);
            event.setCanceled(true);
        }
    }

    private static boolean spawnForsaken(Mob skeleton, Player player) {
        var level = skeleton.level();
        ForsakenEntity forsaken = FurEntities.FORSAKEN.get().create(level);
        if (forsaken == null) return false;
        NdMinions.replace(skeleton, forsaken);
        int variant = level.random.nextInt(4);
        if (variant == 0) {
            forsaken.setItemSlot(EquipmentSlot.OFFHAND, forsakenShield());
            forsaken.getAttribute(Attributes.ARMOR).setBaseValue(4.0);
            forsaken.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.4);
            forsaken.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.22);
        }
        if (variant <= 1 || variant == 3) forsaken.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        if (variant == 2) forsaken.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        if (variant == 3) {
            forsaken.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.27);
            forsaken.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
        }
        forsaken.reassessWeaponGoal();
        level.addFreshEntity(forsaken);
        if (variant == 3) {
            SkeletonHorse horse = EntityType.SKELETON_HORSE.create(level);
            if (horse != null) {
                horse.moveTo(forsaken.getX(), forsaken.getY(), forsaken.getZ(), forsaken.getYRot(), 0);
                horse.invulnerableTime = 60;
                horse.setTamed(true);
                horse.setAge(0);
                NdMinions.summon(horse, player, NdMinions.data(skeleton).getLifetime());
                level.addFreshEntity(horse);
                forsaken.startRiding(horse);
            }
        }
        return true;
    }

    /// Чёрный щит с фиолетовым гербом Короля-скелета (оригинал — `getForsakenShield`).
    public static ItemStack forsakenShield() {
        ItemStack shield = new ItemStack(Items.SHIELD);
        CompoundTag be = new CompoundTag();
        ListTag patterns = new ListTag();
        CompoundTag p = new CompoundTag();
        p.putString("Pattern", FurBannerPatterns.SKELETONKING_HASH);
        p.putInt("Color", DyeColor.PURPLE.getId());
        patterns.add(p);
        be.put("Patterns", patterns);
        be.putInt("Base", DyeColor.BLACK.getId());
        shield.addTagElement("BlockEntityTag", be);
        return shield;
    }

    // --- Bone Bag ---

    public static final String PROGRESS = "progress";

    static ItemStack equipped(Player p, RegistryObject<Item> artifact) {
        for (ItemStack s : ArtifactChannel.getEquippedArtifacts(p)) if (s.is(artifact.get())) return s;
        return ItemStack.EMPTY;
    }

    /// Убитые скелеты и костяные лошади наполняют мешок на 25% (до 100%).
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player p) || p.level().isClientSide) return;
        if (!(event.getEntity() instanceof AbstractSkeleton || event.getEntity() instanceof SkeletonHorse)) return;
        ItemStack bag = equipped(p, NdItems.CHARM_BONE_BAG);
        if (!bag.isEmpty()) bag.getOrCreateTag().putInt(PROGRESS, Math.min(100, bag.getOrCreateTag().getInt(PROGRESS) + 25));
    }

    /// Полный мешок: следующее заклинание призыва вызывает ещё и Osvermis (бессрочно 800 т. × длительность).
    public static void onSpellPostCast(SpellCastEvent.Post event) {
        if (event.getLevel().isClientSide || !(event.getCaster() instanceof Player p) || event.getSpell().getType() != SpellTypes.MINION) return;
        ItemStack bag = equipped(p, NdItems.CHARM_BONE_BAG);
        if (bag.isEmpty() || !bag.hasTag() || bag.getTag().getInt(PROGRESS) < 100) return;
        BlockPos pos = BlockUtil.findNearbyFloorSpace(p, 5, 3);
        if (pos == null) return;
        BoneWormEntity worm = FurEntities.BONEWORM.get().create(p.level());
        if (worm == null) return;
        worm.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, p.getYRot(), 0);
        NdMinions.summon(worm, p, (int) (800 * event.getModifiers().getFactor(SpellModifiers.DURATION)));
        p.level().addFreshEntity(worm);
        bag.getTag().putInt(PROGRESS, 0);
    }

    /// Мана-пиявка на кастующем: заклинание вдвое дороже, с Spell-Sapping Venom — ещё и слабее на 40%.
    /// (В оригинале проверялось, не сидит ли кастующий *на* пиявке, — условие не срабатывало; здесь — по описанию.)
    public static void onSpellPreCast(SpellCastEvent.Pre event) {
        LivingEntity caster = event.getCaster();
        if (caster == null || caster.level().isClientSide) return;
        for (Entity passenger : caster.getPassengers()) {
            if (!(passenger instanceof ManaLeech leech)) continue;
            SpellModifiers m = event.getModifiers();
            m.multiply(SpellModifiers.COST, 2);
            if (leech.weakensSpells()) {
                for (String k : new String[]{SpellModifiers.POTENCY, SpellModifiers.BLAST, SpellModifiers.RANGE, SpellModifiers.DURATION}) m.multiply(k, 0.6f);
            }
            return;
        }
    }

    // --- Thornbelt ---

    /// Получив урон от существа, выпускает 16 кактусовых шипов по кругу; откат 15 с.
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player p) || p.level().isClientSide || event.getSource().getEntity() == null) return;
        Item belt = NdItems.BELT_THORNS.get();
        if (p.getCooldowns().isOnCooldown(belt) || !has(p, NdItems.BELT_THORNS)) return;
        for (int i = 0; i < 16; i++) {
            CactusThornEntity thorn = new CactusThornEntity(p.level(), p);
            thorn.pickup = AbstractArrow.Pickup.DISALLOWED;
            double angle = Math.toRadians(i * 22.5);
            thorn.shoot(Math.cos(angle) * 0.5, 0.0, Math.sin(angle) * 0.5, 1.0F, 0.0F);
            p.level().addFreshEntity(thorn);
        }
        p.getCooldowns().addCooldown(belt, 300);
    }
}

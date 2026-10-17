package dev.mcmoded.necromancersdelight.entity;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.binaris.wizardry.setup.registries.Elements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

/// Стихийная пиявка (оригинал — `EntityElementalLeechMinion`, WinDanesz): кусает уроном своей стихии и накладывает
/// её эффект на 2 с (огонь — поджог, лёд — Frost, молния — свечение, некромантия — иссушение, земля — яд,
/// волшебство — Containment, исцеление — Arcane Jammer, магия — замедление). Стихия — цвет текстуры.
public class ElementalLeech extends LeechMinion {
    /// Порядок стихий оригинала (`Element.values()`): индекс — номер текстуры `leech_<стихия>.png`.
    public static final List<Supplier<Element>> ELEMENTS = List.of(() -> Elements.MAGIC, () -> Elements.FIRE, () -> Elements.ICE,
            () -> Elements.LIGHTNING, () -> Elements.NECROMANCY, () -> Elements.EARTH, () -> Elements.SORCERY, () -> Elements.HEALING);
    private static final EntityDataAccessor<Integer> ELEMENT = SynchedEntityData.defineId(ElementalLeech.class, EntityDataSerializers.INT);

    public ElementalLeech(EntityType<? extends ElementalLeech> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LeechMinion.createAttributes().add(Attributes.MAX_HEALTH, 8.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ELEMENT, 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data, CompoundTag tag) {
        if (getElementIndex() == 0) setElementIndex(1 + random.nextInt(ELEMENTS.size() - 1));
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    public int getElementIndex() {
        return Math.floorMod(entityData.get(ELEMENT), ELEMENTS.size());
    }

    public void setElementIndex(int index) {
        entityData.set(ELEMENT, index);
    }

    public Element getElement() {
        return ELEMENTS.get(getElementIndex()).get();
    }

    public void setElement(Element element) {
        for (int i = 0; i < ELEMENTS.size(); i++) if (ELEMENTS.get(i).get() == element) setElementIndex(i);
    }

    public static int indexOf(Element element) {
        for (int i = 0; i < ELEMENTS.size(); i++) if (ELEMENTS.get(i).get() == element) return i;
        return 0;
    }

    private ResourceKey<DamageType> damageType() {
        return switch (getElementIndex()) {
            case 1 -> EBDamageSources.FIRE;
            case 2 -> EBDamageSources.FROST;
            case 3 -> EBDamageSources.SHOCK;
            case 4 -> EBDamageSources.WITHER;
            case 5 -> EBDamageSources.POISON;
            case 6 -> EBDamageSources.FORCE;
            case 7 -> EBDamageSources.RADIANT;
            default -> EBDamageSources.MAGIC;
        };
    }

    private MobEffectInstance effect() {
        return switch (getElementIndex()) {
            case 2 -> new MobEffectInstance(EBMobEffects.FROST.get(), 40, 0);
            case 3 -> new MobEffectInstance(MobEffects.GLOWING, 40, 0);
            case 4 -> new MobEffectInstance(MobEffects.WITHER, 40, 0);
            case 5 -> new MobEffectInstance(MobEffects.POISON, 40, 0);
            case 6 -> new MobEffectInstance(EBMobEffects.CONTAINMENT.get(), 40, 0);
            case 7 -> new MobEffectInstance(EBMobEffects.ARCANE_JAMMER.get(), 40, 0);
            default -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0);
        };
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!(target instanceof LivingEntity living)) return false;
        float dmg = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (!MagicDamageSource.causeMagicDamage(this, living, dmg, damageType())) return false;
        if (getElementIndex() == 1) living.setSecondsOnFire(2);
        else living.addEffect(effect());
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("element", getElementIndex());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("element")) setElementIndex(tag.getInt("element"));
    }
}

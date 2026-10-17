package dev.mcmoded.necromancersdelight.entity;

import com.binaris.wizardry.api.content.item.IManaItem;
import dev.mcmoded.necromancersdelight.item.NdItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/// Мана-пиявка (оригинал — `EntityManaLeechMinion`, WinDanesz): не кусает, а раз в секунду высасывает 5% маны из
/// палочки/хранилища маны в руках носителя. Хозяин с Leechlink Amulet получает половину украденного в свою палочку.
/// Пока носитель с пиявкой колдует — заклинания стоят вдвое дороже, а с Spell-Sapping Venom у хозяина
/// (`weakensSpells`) ещё и слабее на 40% (`NdArtifacts.onSpellPreCast`).
public class ManaLeech extends LeechMinion {
    private boolean weakensSpells;

    public ManaLeech(EntityType<? extends ManaLeech> type, Level level) {
        super(type, level);
    }

    public boolean weakensSpells() {
        return weakensSpells;
    }

    public void setWeakensSpells(boolean weakensSpells) {
        this.weakensSpells = weakensSpells;
    }

    @Override
    protected void attachedLogic(LivingEntity host) {
        if (host.tickCount % 20 == 0) doHurtTarget(host);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (target instanceof LivingEntity living) {
            for (InteractionHand hand : InteractionHand.values()) {
                if (living.getItemInHand(hand).getItem() instanceof IManaItem) stealMana(living, hand);
            }
        }
        return true;
    }

    private void stealMana(LivingEntity target, InteractionHand hand) {
        ItemStack source = target.getItemInHand(hand);
        IManaItem item = (IManaItem) source.getItem();
        int stolen = (int) (item.getMana(source) * 0.05);
        item.consumeMana(source, stolen, target);
        if (getCaster() instanceof Player caster && NdItems.has(caster, NdItems.AMULET_LEECHLINK)) {
            for (InteractionHand h : InteractionHand.values()) {
                ItemStack own = caster.getItemInHand(h);
                if (own.getItem() instanceof IManaItem mana) {
                    mana.rechargeMana(own, (int) (stolen * 0.5f));
                    break;
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("weakensSpells", weakensSpells);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("weakensSpells")) weakensSpells = tag.getBoolean("weakensSpells");
    }
}

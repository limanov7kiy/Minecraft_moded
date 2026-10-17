package dev.mcmoded.necromancersdelight.item;

import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

/// Миньоны артефактов: делает моба миньоном Redux (`MinionData`), как `MinionSpell`, — для подмены призванных
/// зомби/скелетов мобами Fish's Undead Rising и для отдельных призывов (Bone Bag, Crown of Pests).
/// В оригинале — `SummonedCreatureData` WizardryUtils.
public final class NdMinions {
    private NdMinions() {}

    public static MinionData data(Mob mob) {
        return Services.OBJECT_DATA.getMinionData(mob);
    }

    /// Миньон `owner` со сроком `lifetime` тиков (−1 — бессрочный), идёт за хозяином и ищет цели.
    public static void summon(Mob mob, LivingEntity owner, int lifetime) {
        mob.setCustomName(Component.translatable("entity.ebwizardry.minion_name", owner.getDisplayName(), mob.getDisplayName()));
        MinionData data = data(mob);
        data.setSummoned(true);
        data.setOwnerUUID(owner.getUUID());
        data.setLifetime(lifetime);
        data.setShouldFollowOwner(true);
        data.setShouldDeleteGoals(false);
        data.setSearchNearbyTargets(true);
    }

    /// Подмена: `replacement` получает хозяина, срок, место и усиления (модификаторы урона/здоровья Redux) от `original`.
    public static void replace(Mob original, Mob replacement) {
        MinionData from = data(original);
        replacement.moveTo(original.getX(), original.getY(), original.getZ(), original.getYRot(), original.getXRot());
        LivingEntity owner = from.getOwner();
        if (owner != null) summon(replacement, owner, from.getLifetime());
        else {
            data(replacement).setSummoned(true);
            data(replacement).setOwnerUUID(from.getOwnerUUID());
            data(replacement).setLifetime(from.getLifetime());
        }
        copyModifiers(original, replacement, Attributes.ATTACK_DAMAGE);
        copyModifiers(original, replacement, Attributes.MAX_HEALTH);
        replacement.setHealth(replacement.getMaxHealth());
    }

    private static void copyModifiers(Mob from, Mob to, Attribute attribute) {
        AttributeInstance a = from.getAttribute(attribute), b = to.getAttribute(attribute);
        if (a == null || b == null) return;
        a.getModifiers().forEach(m -> {
            if (!b.hasModifier(m)) b.addPermanentModifier(m);
        });
    }
}

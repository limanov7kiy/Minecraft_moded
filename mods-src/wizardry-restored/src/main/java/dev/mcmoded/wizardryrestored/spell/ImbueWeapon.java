package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.data.ImbuementEnchantData;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/// Зачарование оружия: первое оружие на хотбаре (меч — Острота, лук — Сила) на 45 секунд. Временные чары ведёт
/// механизм «наложений» Redux (тот же, что у пылающего оружия): по истечении чары снимаются сами.
public class ImbueWeapon extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        for (ItemStack stack : EntityUtil.getHotBarAndHandItems(ctx.caster())) {
            Enchantment ench = stack.getItem() instanceof SwordItem ? Enchantments.SHARPNESS
                    : stack.getItem() instanceof BowItem ? Enchantments.POWER_ARROWS : null;
            if (ench == null || EnchantmentHelper.getEnchantments(stack).containsKey(ench)) continue;
            if (ctx.world().isClientSide) return true;
            ImbuementEnchantData data = Services.OBJECT_DATA.getImbuementData(stack);
            if (data == null) continue;
            float potency = ctx.modifiers().getFactor(SpellModifiers.POTENCY);
            int level = Math.max(1, 2 + (int) ((potency - 1.0f) / EBServerConfig.POTENCY_INCREASE_PER_TIER.get() + 0.5f));
            stack.enchant(ench, level);
            data.addImbuement(ench, ctx.world().getGameTime() + (long) ctx.modifiers().get(SpellModifiers.DURATION, property(DefaultProperties.EFFECT_DURATION)));
            ((ServerLevel) ctx.world()).sendParticles(ParticleTypes.ENCHANT, ctx.caster().getX(), ctx.caster().getEyeY() - 0.4, ctx.caster().getZ(), 20, 0.5, 0.4, 0.5, 0.5);
            this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            return true;
        }
        return false;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.SORCERY, SpellTypes.UTILITY, SpellAction.IMBUE, 25, 0, 70)
                .add(DefaultProperties.EFFECT_DURATION, 900)
                .build();
    }
}

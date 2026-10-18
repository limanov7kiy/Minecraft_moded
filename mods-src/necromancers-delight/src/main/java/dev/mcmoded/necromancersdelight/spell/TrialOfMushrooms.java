package dev.mcmoded.necromancersdelight.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.undeadrising.registry.FurItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/// Trial of Mushrooms (оригинал — `TrialOfMushrooms`, WinDanesz): съесть гриб Fish's Undead Rising из второй руки —
/// гриб-покров: невидимость; кордицепс: яд II на 3 с + спешка и скорость; кровозуб: регенерация III;
/// светящийся: свечение и ночное зрение. Длительности и силы — свойства из json оригинала.
public class TrialOfMushrooms extends Spell {
    public static final SpellProperty<Integer> BLOODTOOTH_AMPLIFIER = SpellProperty.intProperty("bloodtooth_amplifier");
    public static final SpellProperty<Integer> BLOODTOOTH_DURATION = SpellProperty.intProperty("bloodtooth_duration");
    public static final SpellProperty<Integer> VEILSHROOM_AMPLIFIER = SpellProperty.intProperty("veilshroom_amplifier");
    public static final SpellProperty<Integer> VEILSHROOM_DURATION = SpellProperty.intProperty("veilshroom_duration");
    public static final SpellProperty<Integer> CORDYCEPS_AMPLIFIER = SpellProperty.intProperty("cordyceps_amplifier");
    public static final SpellProperty<Integer> CORDYCEPS_DURATION = SpellProperty.intProperty("cordyceps_duration");
    public static final SpellProperty<Integer> GLOWSHROOM_AMPLIFIER = SpellProperty.intProperty("glowshroom_amplifier");
    public static final SpellProperty<Integer> GLOWSHROOM_DURATION = SpellProperty.intProperty("glowshroom_duration");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        ItemStack off = caster.getOffhandItem();
        if (off.isEmpty()) {
            if (!ctx.world().isClientSide) caster.displayClientMessage(Component.translatable("spell.necromancersdelight.trial_of_mushrooms.missing_item"), false);
            return false;
        }
        if (off.is(FurItems.VEIL_SHROOM.get())) {
            caster.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, property(VEILSHROOM_DURATION)));
        } else if (off.is(FurItems.CORDY_SHROOM.get())) {
            caster.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 1));
            caster.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, property(CORDYCEPS_DURATION), property(CORDYCEPS_AMPLIFIER)));
            caster.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, property(CORDYCEPS_DURATION), property(CORDYCEPS_AMPLIFIER)));
        } else if (off.is(FurItems.BLOODTOOTH_SHROOM.get())) {
            caster.addEffect(new MobEffectInstance(MobEffects.REGENERATION, property(BLOODTOOTH_DURATION), property(BLOODTOOTH_AMPLIFIER)));
        } else if (off.is(FurItems.GLOWSHROOM.get())) {
            caster.addEffect(new MobEffectInstance(MobEffects.GLOWING, property(GLOWSHROOM_DURATION), property(GLOWSHROOM_AMPLIFIER)));
            caster.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, property(GLOWSHROOM_DURATION), property(GLOWSHROOM_AMPLIFIER)));
        } else {
            return false;
        }
        if (!caster.getAbilities().instabuild) off.shrink(1);
        playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.EARTH, SpellTypes.UTILITY, SpellAction.IMBUE, 15, 20, 20)
                .add(BLOODTOOTH_AMPLIFIER, 2).add(BLOODTOOTH_DURATION, 80)
                .add(VEILSHROOM_AMPLIFIER, 0).add(VEILSHROOM_DURATION, 320)
                .add(CORDYCEPS_AMPLIFIER, 0).add(CORDYCEPS_DURATION, 400)
                .add(GLOWSHROOM_AMPLIFIER, 0).add(GLOWSHROOM_DURATION, 500)
                .build();
    }
}

package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.BuffSpell;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;

/// Заклинания, целиком собранные из готовых механик Redux (бафф ванильными эффектами, призыв ванильного моба).
public final class BuffSpells {
    private BuffSpells() {
    }

    /// Железная плоть: сопротивление урону I + замедление (тяжёлая «железная» кожа).
    public static Spell ironflesh() {
        return new BuffSpell(0.6f, 0.6f, 0.6f, () -> MobEffects.DAMAGE_RESISTANCE, () -> MobEffects.MOVEMENT_SLOWDOWN).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellTypes.DEFENCE, SpellAction.POINT_UP, 30, 15, 100)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.DAMAGE_RESISTANCE), 600)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.DAMAGE_RESISTANCE), 1)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.MOVEMENT_SLOWDOWN), 600)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.MOVEMENT_SLOWDOWN), 0)
                        .build());
    }

    /// Алмазная плоть: сопротивление урону III + замедление.
    public static Spell diamondflesh() {
        return new BuffSpell(0.4f, 1.0f, 0.95f, () -> MobEffects.DAMAGE_RESISTANCE, () -> MobEffects.MOVEMENT_SLOWDOWN).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.MASTER, Elements.HEALING, SpellTypes.DEFENCE, SpellAction.POINT_UP, 100, 25, 300)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.DAMAGE_RESISTANCE), 600)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.DAMAGE_RESISTANCE), 2)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.MOVEMENT_SLOWDOWN), 600)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.MOVEMENT_SLOWDOWN), 1)
                        .build());
    }

    /// Неуязвимость духа: заклинатель невидим и неуязвим, но сам почти не может наносить урон и ломать блоки.
    public static Spell transience() {
        return new BuffSpell(0.9f, 0.9f, 1.0f, () -> MobEffects.DAMAGE_RESISTANCE, () -> MobEffects.INVISIBILITY,
                () -> MobEffects.WEAKNESS, () -> MobEffects.DIG_SLOWDOWN).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellTypes.BUFF, SpellAction.POINT_UP, 50, 15, 100)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.DAMAGE_RESISTANCE), 400)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.DAMAGE_RESISTANCE), 4)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.INVISIBILITY), 400)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.INVISIBILITY), 0)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.WEAKNESS), 400)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.WEAKNESS), 9)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.DIG_SLOWDOWN), 400)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.DIG_SLOWDOWN), 4)
                        .build());
    }

    /// Призрачный волк-спутник: остаётся, пока его не убьют.
    public static Spell summonSpiritWolf() {
        return new MinionSpell<>((level) -> new net.minecraft.world.entity.animal.Wolf(EntityType.WOLF, level)).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellTypes.MINION, SpellAction.SUMMON, 25, 0, 100)
                        .add(DefaultProperties.MINION_COUNT, 1)
                        .add(DefaultProperties.MINION_LIFETIME, -1)
                        .add(DefaultProperties.SUMMON_RADIUS, 2)
                        .build());
    }

    /// Призыв железного голема-помощника на 30 секунд.
    public static Spell summonIronGolem() {
        return new MinionSpell<>((level) -> new IronGolem(EntityType.IRON_GOLEM, level)).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.MASTER, Elements.SORCERY, SpellTypes.MINION, SpellAction.SUMMON, 175, 20, 400)
                        .add(DefaultProperties.MINION_COUNT, 1)
                        .add(DefaultProperties.MINION_LIFETIME, 600)
                        .add(DefaultProperties.SUMMON_RADIUS, 2)
                        .build());
    }
}

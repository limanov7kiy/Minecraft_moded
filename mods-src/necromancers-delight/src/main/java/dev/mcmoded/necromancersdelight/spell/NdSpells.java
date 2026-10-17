package dev.mcmoded.necromancersdelight.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.ProjectileSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.necromancersdelight.entity.NdBombs;

/// Заклинания Necromancer's Delight. Параметры — из `assets/necromancersdelight/spells/*.json` оригинала.
public final class NdSpells {
    private NdSpells() {}

    public static Spell stinkBomb() {
        return new ProjectileSpell<>(NdBombs::stink).assignProperties(SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellTypes.PROJECTILE, SpellAction.POINT, 25, 0, 25)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.DIRECT_DAMAGE, 4f)
                .add(DefaultProperties.SPLASH_DAMAGE, 2f)
                .add(DefaultProperties.EFFECT_RADIUS, 2)
                .add(DefaultProperties.EFFECT_DURATION, 300)
                .add(DefaultProperties.EFFECT_STRENGTH, 0)
                .build());
    }

    public static Spell frostBomb() {
        return new ProjectileSpell<>(NdBombs::frost).assignProperties(SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.ICE, SpellTypes.PROJECTILE, SpellAction.POINT, 30, 0, 25)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.DIRECT_DAMAGE, 4f)
                .add(DefaultProperties.SPLASH_DAMAGE, 2f)
                .add(DefaultProperties.EFFECT_RADIUS, 2)
                .add(DefaultProperties.EFFECT_DURATION, 150)
                .add(DefaultProperties.EFFECT_STRENGTH, 0)
                .build());
    }
}

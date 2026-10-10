package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.entity.living.IceWraith;
import com.binaris.wizardry.content.entity.living.ShadowWraith;
import com.binaris.wizardry.content.entity.living.StormElemental;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.ConstructRangedSpell;
import com.binaris.wizardry.content.spell.abstr.ConstructSpell;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.wizardryrestored.entity.ModEntities;
import net.minecraft.core.Direction;

/// Заклинания-конструкции этапа 3 на механике `ConstructSpell` Redux (каст игроком, существом, раздатчиком)
/// и призывы существ, которые в Redux уже есть, но без заклинаний.
public final class Constructs {
    private Constructs() {
    }

    public static Spell forcefield() {
        return new ConstructSpell<>(l -> new dev.mcmoded.wizardryrestored.entity.ForcefieldEntity(ModEntities.FORCEFIELD.get(), l), false)
                .assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellTypes.DEFENCE, SpellAction.POINT_UP, 45, 0, 200)
                        .add(DefaultProperties.DURATION, 600)
                        .add(DefaultProperties.EFFECT_RADIUS, 3)
                        .build());
    }

    public static Spell radiantTotem() {
        return new ConstructRangedSpell<>(l -> new dev.mcmoded.wizardryrestored.entity.RadiantTotemEntity(ModEntities.RADIANT_TOTEM.get(), l), false)
                .floor(true)
                .assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellTypes.CONSTRUCT, SpellAction.POINT, 40, 0, 300)
                        .add(DefaultProperties.RANGE, 5f)
                        .add(DefaultProperties.DURATION, 600)
                        .add(DefaultProperties.EFFECT_RADIUS, 5)
                        .add(DefaultProperties.HEALTH, 1f)
                        .add(DefaultProperties.DAMAGE, 2f)
                        .build());
    }

    public static Spell witheringTotem() {
        return new ConstructRangedSpell<>(l -> new dev.mcmoded.wizardryrestored.entity.WitheringTotemEntity(ModEntities.WITHERING_TOTEM.get(), l), false)
                .floor(true)
                .assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.NECROMANCY, SpellTypes.CONSTRUCT, SpellAction.POINT, 40, 0, 300)
                        .add(DefaultProperties.RANGE, 5f)
                        .add(DefaultProperties.DURATION, 600)
                        .add(DefaultProperties.EFFECT_RADIUS, 5)
                        .add(DefaultProperties.EFFECT_DURATION, 60)
                        .add(DefaultProperties.DAMAGE, 1f)
                        .build());
    }

    public static Spell blackHole() {
        return new ConstructRangedSpell<>(l -> new dev.mcmoded.wizardryrestored.entity.BlackHoleEntity(ModEntities.BLACK_HOLE.get(), l), false) {
            @Override
            protected void addConstructExtras(CastContext ctx, dev.mcmoded.wizardryrestored.entity.BlackHoleEntity construct, Direction side) {
                // Не в землю: поднимаем над точкой попадания
                if (side != null) construct.setPos(construct.position().add(side.getStepX() * 1.5, side.getStepY() * 1.5, side.getStepZ() * 1.5));
            }
        }.assignProperties(SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.SORCERY, SpellTypes.CONSTRUCT, SpellAction.POINT, 150, 20, 400)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.DURATION, 400)
                .add(DefaultProperties.EFFECT_RADIUS, 6)
                .add(DefaultProperties.DAMAGE, 3f)
                .build());
    }

    public static Spell snare() {
        return new ConstructRangedSpell<>(l -> new dev.mcmoded.wizardryrestored.entity.SnareEntity(ModEntities.SNARE.get(), l), false)
                .floor(true)
                .assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.NOVICE, Elements.EARTH, SpellTypes.CONSTRUCT, SpellAction.POINT, 10, 0, 20)
                        .add(DefaultProperties.RANGE, 10f)
                        .add(DefaultProperties.DURATION, 6000)
                        .add(DefaultProperties.DAMAGE, 3f)
                        .add(DefaultProperties.EFFECT_DURATION, 100)
                        .build());
    }

    public static Spell summonIceWraith() {
        return minion(new MinionSpell<>(l -> new IceWraith(EBEntities.ICE_WRAITH.get(), l)), SpellTiers.ADVANCED, Elements.ICE, 40, 10, 200, 600);
    }

    public static Spell summonShadowWraith() {
        return minion(new MinionSpell<>(l -> new ShadowWraith(EBEntities.SHADOW_WRAITH.get(), l)), SpellTiers.MASTER, Elements.NECROMANCY, 70, 20, 300, 900);
    }

    public static Spell summonStormElemental() {
        return minion(new MinionSpell<>(l -> new StormElemental(EBEntities.STORM_ELEMENTAL.get(), l)), SpellTiers.MASTER, Elements.LIGHTNING, 75, 20, 300, 900);
    }

    public static Spell summonPhoenix() {
        // Не flying(true): в Redux 0.8.9 он ставит летающих миньонов на абсолютную высоту y=2. Феникс взлетит сам.
        return minion(new MinionSpell<>(l -> new dev.mcmoded.wizardryrestored.entity.PhoenixEntity(ModEntities.PHOENIX.get(), l)),
                SpellTiers.MASTER, Elements.FIRE, 80, 20, 400, 900);
    }

    private static Spell minion(MinionSpell<?> spell, com.binaris.wizardry.api.content.spell.SpellTier tier,
                                com.binaris.wizardry.api.content.spell.Element element, int cost, int charge, int cooldown, int lifetime) {
        return spell.assignProperties(SpellProperties.builder()
                .assignBaseProperties(tier, element, SpellTypes.MINION, SpellAction.SUMMON, cost, charge, cooldown)
                .add(DefaultProperties.MINION_COUNT, 1)
                .add(DefaultProperties.MINION_LIFETIME, lifetime)
                .add(DefaultProperties.SUMMON_RADIUS, 2)
                .build());
    }

    static <T extends MagicConstructEntity> T configure(T construct, CastContext ctx, int lifetime) {
        if (ctx.caster() != null) construct.setCaster(ctx.caster());
        construct.setLifetime(lifetime);
        construct.damageMultiplier = ctx.modifiers().getFactor(com.binaris.wizardry.api.content.spell.internal.SpellModifiers.POTENCY);
        return construct;
    }
}

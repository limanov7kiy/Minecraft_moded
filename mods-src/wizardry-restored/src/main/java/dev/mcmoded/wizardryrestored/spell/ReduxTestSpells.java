package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import com.binaris.wizardry.content.spell.ice.SummonIceGiant;
import com.binaris.wizardry.content.spell.lightning.LightningPulse;
import com.binaris.wizardry.content.spell.sorcery.ConjureArmor;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;

/// Готовые заклинания Redux, которые он помечает «тестовыми» (SENSIBLE) и удаляет на выделенном сервере.
/// Здесь — те же классы Redux под нашим именем, без пометки, поэтому они есть и в сетевой игре.
/// Иконки — из ресурсов Redux (у conjure_armor её там нет — своя).
public final class ReduxTestSpells {
    private ReduxTestSpells() {
    }

    private static ResourceLocation reduxIcon(String name) {
        return new ResourceLocation("ebwizardry", "textures/spells/" + name + ".png");
    }

    public static Spell lightningPulse() {
        return new LightningPulse() {
            @Override
            public ResourceLocation getIcon() {
                return reduxIcon("lightning_pulse");
            }

            @Override
            protected SpellProperties properties() {
                return SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.LIGHTNING, SpellTypes.ATTACK, SpellAction.POINT_DOWN, 25, 0, 75)
                        .add(DefaultProperties.DAMAGE, 8.0F)
                        .add(DefaultProperties.EFFECT_RADIUS, 3)
                        .build();
            }
        };
    }

    public static Spell summonIceGiant() {
        return new SummonIceGiant() {
            @Override
            public ResourceLocation getIcon() {
                return reduxIcon("summon_ice_giant");
            }

            @Override
            protected SpellProperties properties() {
                return SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.MASTER, Elements.ICE, SpellTypes.MINION, SpellAction.SUMMON, 100, 20, 400)
                        .add(DefaultProperties.MINION_COUNT, 1)
                        .add(DefaultProperties.MINION_LIFETIME, 600)
                        .add(DefaultProperties.SUMMON_RADIUS, 2)
                        .build();
            }
        };
    }

    public static Spell conjureArmor() {
        return new ConjureArmor().assignProperties(SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellTypes.DEFENCE, SpellAction.SUMMON, 45, 10, 50)
                .add(DefaultProperties.ITEM_LIFETIME, 1800)
                .build());
    }

    public static Spell silverfishSwarm() {
        return new MinionSpell<>((level) -> new Silverfish(EntityType.SILVERFISH, level)) {
            @Override
            public ResourceLocation getIcon() {
                return reduxIcon("silverfish_swarm");
            }
        }.soundValues(1, 1.1f, 0.1f).assignProperties(SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.EARTH, SpellTypes.MINION, SpellAction.SUMMON, 80, 20, 300)
                .add(DefaultProperties.MINION_COUNT, 20)
                .add(DefaultProperties.MINION_LIFETIME, 600)
                .add(DefaultProperties.SUMMON_RADIUS, 3)
                .build());
    }
}

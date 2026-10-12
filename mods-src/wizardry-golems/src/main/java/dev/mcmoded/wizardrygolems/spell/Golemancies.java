package dev.mcmoded.wizardrygolems.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;

import java.util.List;

import static dev.mcmoded.wizardrygolems.spell.Golemancy.Summon;

/// Пять заклинаний Golemancy. Параметры — из `assets/wizardrygolems/spells/*.json` оригинала: advanced, стоимость 50,
/// подготовка 30, перезарядка 260, жизнь миньона 600 т., радиус 2. Состав по умолчанию — как в оригинале без артефактов
/// (артефакты и «старшие» големы мастер-палочки — следующие этапы, см. docs/wizardry-port.md).
public final class Golemancies {
    private Golemancies() {}

    public static Spell earth() {
        return golemancy("golems:clay", 3, Elements.EARTH);
    }

    public static Spell fire() {
        return golemancy("golems:nether_brick", 2, Elements.FIRE);
    }

    public static Spell ice() {
        return golemancy("golems:ice", 2, Elements.ICE);
    }

    public static Spell lightning() {
        // В оригинале — 5 маленьких Thunderstone-големов (свой тип, этап 2); пока — свой тип с текстурой оригинала.
        return golemancy(Summon.small("wizardrygolems:thunderstone", 5), Elements.LIGHTNING);
    }

    public static Spell healing() {
        // Золотой голем; лечение хозяина (IHealingGolem) — этап 2.
        return golemancy("golems:gold", 1, Elements.HEALING);
    }

    private static Spell golemancy(String type, int count, com.binaris.wizardry.api.content.spell.Element element) {
        return golemancy(Summon.of(type, count), element);
    }

    private static Spell golemancy(Summon summon, com.binaris.wizardry.api.content.spell.Element element) {
        int count = summon.count();
        Golemancy spell = new Golemancy(summon.type(), ctx -> List.of(summon));
        return spell.assignProperties(SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, element, SpellTypes.MINION, SpellAction.SUMMON, 50, 30, 260)
                .add(DefaultProperties.MINION_COUNT, count)
                .add(DefaultProperties.MINION_LIFETIME, 600)
                .add(DefaultProperties.SUMMON_RADIUS, 2)
                .build());
    }
}

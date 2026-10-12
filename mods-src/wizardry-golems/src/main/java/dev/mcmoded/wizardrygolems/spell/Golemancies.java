package dev.mcmoded.wizardrygolems.spell;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.item.WandItem;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.wizardrygolems.item.GolemItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

import static dev.mcmoded.wizardrygolems.spell.Golemancy.Summon;

/// Пять заклинаний Golemancy. Параметры — из `assets/wizardrygolems/spells/*.json` оригинала: advanced, стоимость 50,
/// подготовка 30, перезарядка 260, жизнь миньона 600 т., радиус 2. Состав призыва — как в `*Golemancy.spawnGolems`
/// оригинала: первый подходящий надетый артефакт меняет состав, «старшие» големы требуют мастер-палочку стихии.
public final class Golemancies {
    private Golemancies() {}

    public static Spell earth() {
        return golemancy("golems:clay", 3, Elements.EARTH, ctx -> {
            List<Summon> out = new ArrayList<>();
            Player p = player(ctx);
            if (p == null) return List.of(Summon.of("golems:clay", 3));
            if (has(p, GolemItems.RING_GLISTERING)) out.add(Summon.of("golems:melon", 1)); // дополнительно
            if (has(p, GolemItems.RING_ANCIENT_EMPEROR)) out.add(Summon.small("golems:terracotta", 12));
            else if (has(p, GolemItems.RING_ENGRAVED_CONCRETE)) out.add(Summon.of("wizardrygolems:concrete", 2));
            else if (has(p, GolemItems.RING_SANDSTONE)) out.add(Summon.of("golems:sandstone", 5));
            else if (has(p, GolemItems.RING_OBSIDIAN) && master(p, Elements.EARTH, "earth_golemancy")) out.add(Summon.of("golems:obsidian", 1));
            else if (has(p, GolemItems.RING_FOREST_GUARDIAN)) {
                out.add(Summon.of("golems:oak_log", 2));
                out.add(Summon.of("golems:leaves", 3));
            } else if (has(p, GolemItems.RING_GRASS)) out.add(Summon.of("wizardrygolems:strawthorns", 4));
            else if (has(p, GolemItems.CHARM_DRIED_MUSHROOM)) {
                out.add(Summon.of(p.getRandom().nextBoolean() ? "golems:red_mushroom" : "golems:brown_mushroom", 3));
            } else out.add(Summon.of("golems:clay", 3));
            return out;
        });
    }

    public static Spell fire() {
        return fireLifetime(golemancy("golems:nether_brick", 2, Elements.FIRE, ctx -> {
            Player p = player(ctx);
            if (p != null) {
                if (has(p, GolemItems.RING_SMOLDERING)) return List.of(Summon.of("golems:magma", 1));
                if (has(p, GolemItems.RING_CHARCOAL)) return List.of(Summon.of("golems:furnace", 2));
                if (has(p, GolemItems.RING_FLAME_GOLEM) && master(p, Elements.FIRE, "fire_golemancy"))
                    return List.of(Summon.of("wizardrygolems:flame", 1));
            }
            return List.of(Summon.of("golems:nether_brick", 2));
        }));
    }

    private static Spell fireLifetime(Spell spell) {
        return ((Golemancy) spell).lifetimeFactor(ctx -> {
            Player p = player(ctx);
            return p != null && has(p, GolemItems.RING_FIRE_GOLEM_DURATION) ? 2 : 1;
        });
    }

    public static Spell ice() {
        return golemancy("golems:ice", 2, Elements.ICE, ctx -> {
            Player p = player(ctx);
            if (p != null) {
                if (has(p, GolemItems.RING_SNOW_GOLEM)) return List.of(Summon.of("wizardrygolems:snow", 5));
                if (has(p, GolemItems.RING_WINTER_GOLEM) && master(p, Elements.ICE, "ice_golemancy"))
                    return List.of(Summon.of("wizardrygolems:winter", 1));
                if (has(p, GolemItems.HEAD_PERMAFROST_CROWN)) return List.of(Summon.of("wizardrygolems:permafrost", 2));
            }
            return List.of(Summon.of("golems:ice", 2));
        });
    }

    public static Spell lightning() {
        return golemancy("wizardrygolems:thunderstone", 2, Elements.LIGHTNING, ctx -> {
            Player p = player(ctx);
            if (p != null) {
                if (has(p, GolemItems.RING_LODESTONE_GOLEM)) return List.of(Summon.of("wizardrygolems:lodestone", 1));
                if (has(p, GolemItems.RING_SPARK_GOLEM)) return List.of(Summon.of("wizardrygolems:spark", 1));
                if (has(p, GolemItems.HEAD_MAELSTROM_GOLEM) && master(p, Elements.LIGHTNING, "lightning_golemancy"))
                    return List.of(Summon.of("wizardrygolems:maelstrom", 1));
                if (has(p, GolemItems.RING_ELECTRIC_SCATTER)) return List.of(Summon.small("wizardrygolems:thunderstone", 5));
            }
            return List.of(Summon.of("wizardrygolems:thunderstone", 2));
        });
    }

    public static Spell healing() {
        // В оригинале у Radiant Golemancy ветки артефактов скопированы из грозовой (опечатка) — оставлен только золотой голем.
        return golemancy("golems:gold", 1, Elements.HEALING, ctx -> List.of(Summon.of("golems:gold", 1)));
    }

    static Player player(CastContext ctx) {
        return ctx.caster() instanceof Player p ? p : null;
    }

    static boolean has(Player p, RegistryObject<Item> artifact) {
        return ArtifactChannel.isEquipped(p, artifact.get());
    }

    /// Мастер-палочка нужной стихии в любой руке; иначе — сообщение оригинала и обычный состав.
    static boolean master(Player p, Element element, String spell) {
        for (ItemStack s : List.of(p.getMainHandItem(), p.getOffhandItem())) {
            if (s.getItem() instanceof WandItem wand && wand.getElement() == element && wand.getTier(s) == SpellTiers.MASTER) return true;
        }
        p.displayClientMessage(Component.translatable("spell.wizardrygolems." + spell + ".low_wand_tier"), true);
        return false;
    }

    private static Spell golemancy(String fallback, int fallbackCount, Element element,
                                   java.util.function.Function<CastContext, List<Summon>> plan) {
        Golemancy spell = new Golemancy(new net.minecraft.resources.ResourceLocation(fallback), plan);
        return spell.assignProperties(SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, element, SpellTypes.MINION, SpellAction.SUMMON, 50, 30, 260)
                .add(DefaultProperties.MINION_COUNT, fallbackCount)
                .add(DefaultProperties.MINION_LIFETIME, 600)
                .add(DefaultProperties.SUMMON_RADIUS, 2)
                .build());
    }
}

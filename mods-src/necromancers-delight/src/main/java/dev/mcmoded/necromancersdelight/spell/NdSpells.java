package dev.mcmoded.necromancersdelight.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.ProjectileSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.api.content.item.IElementValue;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.content.spell.abstr.ConjureItemSpell;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import dev.mcmoded.necromancersdelight.entity.ElementalLeech;
import dev.mcmoded.necromancersdelight.entity.ManaLeech;
import dev.mcmoded.necromancersdelight.entity.NdBombs;
import dev.mcmoded.necromancersdelight.entity.NdEntities;
import dev.mcmoded.necromancersdelight.item.LeechCrystalAmulet;
import dev.mcmoded.necromancersdelight.item.NdArtifacts;
import dev.mcmoded.necromancersdelight.item.NdItems;
import dev.mcmoded.undeadrising.entity.SalamanderEntity;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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

    // --- ND-3: оружие-призыв (SpellDynamicConjuration оригинала → Redux ConjureItemSpell, предметы — порт FUR) ---

    private static Spell conjure(Item item, SpellTier tier, Element element, int cost, int lifetime) {
        return new ConjureItemSpell(item).assignProperties(SpellProperties.builder()
                .assignBaseProperties(tier, element, SpellTypes.UTILITY, SpellAction.IMBUE, cost, 20, 60)
                .add(DefaultProperties.ITEM_LIFETIME, lifetime)
                .build());
    }

    /// Bound Frozen Dagger: с Frigidbone Ring кинжал получает морозное зачарование на время жизни (`NdArtifacts`).
    public static Spell boundFrozenDagger() {
        return new ConjureItemSpell(FurItems.FROZEN_DAGGER.get()) {
            @Override
            protected ItemStack addItemExtras(PlayerCastContext ctx, ItemStack stack) {
                if (NdItems.has(ctx.caster(), NdItems.RING_FROZEN_DAGGER)) NdArtifacts.markFreezing(stack);
                return super.addItemExtras(ctx, stack);
            }
        }.assignProperties(SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.ICE, SpellTypes.UTILITY, SpellAction.IMBUE, 30, 20, 60)
                .add(DefaultProperties.ITEM_LIFETIME, 600)
                .build());
    }

    public static Spell boundBoneSword() {
        return conjure(FurItems.BONESWORD.get(), SpellTiers.ADVANCED, Elements.NECROMANCY, 40, 600);
    }

    public static Spell boundSpectralDagger() {
        return conjure(FurItems.SPECTRAL_DAGGER.get(), SpellTiers.NOVICE, Elements.NECROMANCY, 20, 600);
    }

    public static Spell boundVespaShield() {
        return conjure(FurItems.VESPA_SHIELD.get(), SpellTiers.APPRENTICE, Elements.EARTH, 50, 900);
    }

    // --- ND-3: бомбы FUR ---

    private static Spell bomb(java.util.function.Supplier<EntityType<dev.mcmoded.undeadrising.entity.projectile.FurBombEntity>> type,
                              SpellTier tier, Element element, int cost, int chargeup, int cooldown, float range) {
        return new ThrowBombSpell(type).assignProperties(SpellProperties.builder()
                .assignBaseProperties(tier, element, SpellTypes.PROJECTILE, SpellAction.POINT, cost, chargeup, cooldown)
                .add(DefaultProperties.RANGE, range)
                .build());
    }

    public static Spell holyHandGrenade() {
        return bomb(FurEntities.HOLY_GRENADE, SpellTiers.MASTER, Elements.HEALING, 99, 60, 160, 8f);
    }

    public static Spell ghostBomb() {
        return bomb(FurEntities.GHOSTBOMB, SpellTiers.ADVANCED, Elements.NECROMANCY, 65, 20, 200, 5f);
    }

    public static Spell wailOfTheBanshee() {
        return bomb(FurEntities.SONICBOMB, SpellTiers.MASTER, Elements.NECROMANCY, 50, 20, 120, 7f);
    }

    // --- ND-3: миньоны (мобы порта FUR и свои пиявки; «миньонность» — MinionData Redux) ---

    private static SpellProperties minionProps(SpellTier tier, Element element, int cost, int chargeup, int cooldown, int lifetime, int radius) {
        return SpellProperties.builder()
                .assignBaseProperties(tier, element, SpellTypes.MINION, SpellAction.SUMMON, cost, chargeup, cooldown)
                .add(DefaultProperties.MINION_LIFETIME, lifetime)
                .add(DefaultProperties.MINION_COUNT, 1)
                .add(DefaultProperties.SUMMON_RADIUS, radius)
                .build();
    }

    private static <T extends Mob> java.util.function.Function<Level, T> of(java.util.function.Supplier<? extends EntityType<T>> type) {
        return level -> type.get().create(level);
    }

    /// Summon Vespa (в оригинале — свой `EntityVespaMinion`: следует за хозяином, укусом снимает Infested — `NdArtifacts.onHurt`).
    public static Spell summonVespa() {
        return new MinionSpell<>(of(FurEntities.VESPA)).flying(true)
                .assignProperties(minionProps(SpellTiers.ADVANCED, Elements.EARTH, 80, 35, 200, 900, 4));
    }

    public static Spell conjureBanshee() {
        return new MinionSpell<>(of(FurEntities.BANSHEE)).flying(true)
                .assignProperties(minionProps(SpellTiers.MASTER, Elements.NECROMANCY, 100, 35, 200, 900, 4));
    }

    public static Spell conjureMycosis() {
        return new MinionSpell<>(of(FurEntities.MYCOSIS))
                .assignProperties(minionProps(SpellTiers.APPRENTICE, Elements.NECROMANCY, 40, 35, 120, 600, 4));
    }

    /// Summon Salamander: саламандра ещё и приручена кастующим игроком (на ней можно ездить, как в оригинале).
    public static Spell summonSalamander() {
        return new MinionSpell<SalamanderEntity>(of(FurEntities.SALAMANDER)) {
            @Override
            protected void addMinionExtras(SalamanderEntity minion, CastContext ctx, int alreadySpawned) {
                if (ctx.caster() instanceof Player p) minion.tame(p);
            }
        }.assignProperties(minionProps(SpellTiers.MASTER, Elements.FIRE, 100, 35, 200, 900, 3));
    }

    /// Пиявки, как в оригинале, за хозяином не ходят (иначе телепорт к хозяину снимает их с носителя).
    /// Summon Mana Leech: с Spell-Sapping Venom у хозяина пиявка ослабляет заклинания носителя.
    public static Spell summonManaLeech() {
        return new MinionSpell<ManaLeech>(of(NdEntities.MANA_LEECH)) {
            @Override
            protected void addMinionExtras(ManaLeech minion, CastContext ctx, int alreadySpawned) {
                if (ctx.caster() instanceof Player p && NdItems.has(p, NdItems.CHARM_SPELLVENOM)) minion.setWeakensSpells(true);
            }
        }.setShouldFollowOwner(false).soundValues(7, 0.6f, 0).assignProperties(minionProps(SpellTiers.ADVANCED, Elements.NECROMANCY, 50, 0, 200, 900, 3));
    }

    /// Summon Elemental Leech: стихия — стихия палочки в главной руке (иначе случайная), а Leech Crystal Amulet с
    /// привязанной стихией задаёт её всегда.
    public static Spell summonElementalLeech() {
        return new MinionSpell<ElementalLeech>(of(NdEntities.ELEMENTAL_LEECH)) {
            @Override
            protected void addMinionExtras(ElementalLeech minion, CastContext ctx, int alreadySpawned) {
                Element element = null;
                if (ctx.caster() != null && ctx.caster().getMainHandItem().getItem() instanceof IElementValue wand
                        && wand.getElement() != Elements.MAGIC) element = wand.getElement();
                if (ctx.caster() instanceof Player p) {
                    Element bound = LeechCrystalAmulet.equippedElement(p);
                    if (bound != null && bound != Elements.MAGIC) element = bound;
                }
                if (element != null) minion.setElement(element);
                else minion.setElementIndex(1 + minion.getRandom().nextInt(ElementalLeech.ELEMENTS.size() - 1));
            }
        }.setShouldFollowOwner(false).soundValues(7, 0.6f, 0).assignProperties(minionProps(SpellTiers.ADVANCED, Elements.MAGIC, 50, 30, 200, 400, 3));
    }
}

package dev.mcmoded.undeadrising.registry;

import dev.mcmoded.undeadrising.UndeadRising;
import dev.mcmoded.undeadrising.item.ForsakenStaffItem;
import dev.mcmoded.undeadrising.item.FrozenThighItem;
import dev.mcmoded.undeadrising.item.ShatteredIceItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/// Предметы порта (оригинал: Fish's Undead Rising (Fish), `init/FURItemRegistry`). Всё, что зарегистрировано здесь,
/// попадает во вкладку {@link FurTabs#MAIN} в порядке регистрации. Модель — `models/item/<id>.json`, текстура —
/// `textures/items/<id>.png` (папка `items`, как в оригинале). Яйца призыва — {@link #egg}, цвета из оригинала.
public final class FurItems {
    private FurItems() {}

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, UndeadRising.MOD_ID);

    // --- Добыча мобов ---
    /// Mycosis: гифы (грибница).
    public static final RegistryObject<Item> HYPHAE = simple("hyphae", new Item.Properties());
    /// Mycosis: ядовитые споры.
    public static final RegistryObject<Item> POISONSPORE = simple("poisonspore", new Item.Properties().rarity(Rarity.RARE));
    /// Mycosis (обычный): кордицепс. В оригинале — растение-блок; здесь пока простой предмет (блок не переносили).
    public static final RegistryObject<Item> CORDY_SHROOM = simple("cordy_shroom", new Item.Properties());
    /// Mycosis (светящийся, вариант 1): светящийся гриб. В оригинале — растение-блок; здесь пока простой предмет.
    public static final RegistryObject<Item> GLOWSHROOM = simple("glowshroom", new Item.Properties());
    /// Mummy: проклятая ткань.
    public static final RegistryObject<Item> CURSED_FABRIC = simple("cursed_fabric", new Item.Properties());
    /// Mummy: ушебти.
    public static final RegistryObject<Item> USHABTI = simple("ushabti", new Item.Properties());
    /// Mummy: кислотное сердце.
    public static final RegistryObject<Item> ACIDICHEART = simple("acidicheart", new Item.Properties().rarity(Rarity.RARE));
    /// Frigid: колотый лёд — пьётся, тушит огонь.
    public static final RegistryObject<Item> SHATTERED_ICE = ITEMS.register("shattered_ice", () -> new ShatteredIceItem(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(1.8F).alwaysEat().build())));
    /// Frigid: мёрзлое бедро — еда и дубина (замедление при ударе).
    public static final RegistryObject<Item> FROZENTHIGH = ITEMS.register("frozenthigh", () -> new FrozenThighItem(
            new Item.Properties().durability(64).food(new FoodProperties.Builder().nutrition(10).saturationMod(1.2F).meat()
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 4), 0.6F).build())));
    /// Forsaken: осколок воспоминаний.
    public static final RegistryObject<Item> HATRED_SHARD = simple("hatred_shard", new Item.Properties());
    /// Посох Forsaken — выпускает «Кольцо смерти».
    public static final RegistryObject<Item> FORSAKEN_STAFF = ITEMS.register("forsaken_staff", () -> new ForsakenStaffItem(new Item.Properties().durability(32)));

    // --- Яйца призыва (цвета — из оригинала) ---
    public static final RegistryObject<Item> SPAWN_EGG_UNBURIED = egg("unburied", FurEntities.UNBURIED, 13949370, 2698290);
    public static final RegistryObject<Item> SPAWN_EGG_MUMMY = egg("mummy", FurEntities.MUMMY, 15325870, 10125655);
    public static final RegistryObject<Item> SPAWN_EGG_MYCOSIS = egg("mycosis", FurEntities.MYCOSIS, 12378284, 8610589);
    public static final RegistryObject<Item> SPAWN_EGG_FRIGID = egg("frigid", FurEntities.FRIGID, 11526370, 5851215);
    public static final RegistryObject<Item> SPAWN_EGG_FORSAKEN = egg("forsaken", FurEntities.FORSAKEN, 12698049, 4802889);

    private static RegistryObject<Item> simple(String name, Item.Properties props) {
        return ITEMS.register(name, () -> new Item(props));
    }

    /// Яйцо `spawn_egg_<моб>` (id как в оригинале); модель — `models/item/spawn_egg_<моб>.json` (template_spawn_egg).
    public static RegistryObject<Item> egg(String mob, Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight) {
        return ITEMS.register("spawn_egg_" + mob, () -> new ForgeSpawnEggItem(type, background, highlight, new Item.Properties()));
    }
}

package dev.mcmoded.undeadrising.registry;

import dev.mcmoded.undeadrising.UndeadRising;
import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.item.BowlFoodTooltipItem;
import dev.mcmoded.undeadrising.item.CrownItem;
import dev.mcmoded.undeadrising.item.MoltenBeefItem;
import dev.mcmoded.undeadrising.item.SalamanderBucketItem;
import dev.mcmoded.undeadrising.item.ForsakenStaffItem;
import dev.mcmoded.undeadrising.item.TooltipItem;
import dev.mcmoded.undeadrising.item.FurThrowableItem;
import dev.mcmoded.undeadrising.item.FurWeaponItem;
import dev.mcmoded.undeadrising.item.VespaShieldItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
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
    /// Mycosis (обычный): кордицепс — гриб-растение {@link FurBlocks#CORDY_SHROOM}.
    public static final RegistryObject<Item> CORDY_SHROOM = block(FurBlocks.CORDY_SHROOM);
    /// Mycosis (светящийся, вариант 1): светящийся гриб {@link FurBlocks#GLOWSHROOM}.
    public static final RegistryObject<Item> GLOWSHROOM = block(FurBlocks.GLOWSHROOM);
    /// Кровозуб (багровый лес Незера) и вуалевик (грибные поля, леса) — для Trial of Mushrooms Necromancer's Delight.
    public static final RegistryObject<Item> BLOODTOOTH_SHROOM = block(FurBlocks.BLOODTOOTH_SHROOM);
    public static final RegistryObject<Item> VEIL_SHROOM = block(FurBlocks.VEIL_SHROOM);
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

    /// Vespa: отравленное жало.
    public static final RegistryObject<Item> POISONSTINGER = simple("poisonstinger", new Item.Properties());
    /// Vespa: панцирь.
    public static final RegistryObject<Item> VESPA_CARAPACE = simple("vespa_carapace", new Item.Properties());
    /// Vespa: яйцо паразита — съедобно, но с шансом 80% заражает.
    public static final RegistryObject<Item> PARASITE_OVUM = simple("parasite_ovum", new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(1).saturationMod(0.1F).effect(() -> new MobEffectInstance(FurEffects.INFESTED.get(), 240, 0), 0.8F).build()));
    /// Паразиты (добыча и «подобрать крадучись»): сырые — с шансом голод (джунглей — яд), жареный — сытная еда.
    public static final RegistryObject<Item> PARASITE_COMMON = ITEMS.register("parasite_item_common", () -> new TooltipItem(new Item.Properties()
            .food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F).build())));
    public static final RegistryObject<Item> PARASITE_DESERT = ITEMS.register("parasite_item_desert", () -> new TooltipItem(new Item.Properties()
            .food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F).build())));
    public static final RegistryObject<Item> PARASITE_JUNGLE = ITEMS.register("parasite_item_jungle", () -> new TooltipItem(new Item.Properties()
            .food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).effect(() -> new MobEffectInstance(MobEffects.POISON, 80, 0), 0.3F).build())));
    public static final RegistryObject<Item> PARASITE_COOKED = simple("parasite_item_cooked", new Item.Properties()
            .food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).build()));
    /// Banshee: эктоплазма.
    public static final RegistryObject<Item> ECTOPLASM = simple("ectoplasm", new Item.Properties());
    /// Banshee: голосовые связки.
    public static final RegistryObject<Item> SHRIEK_CORD = simple("shriek_cord", new Item.Properties().rarity(Rarity.RARE));
    /// Osvermis (редко): запятнанная корона короля. В оригинале — часть призыва Короля-скелета (через проклятую корону).
    public static final RegistryObject<Item> STAINED_KINGS_CROWN = ITEMS.register("stained_kings_crown",
            () -> new CrownItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1), 0));

    /// Salamander: раскалённое мясо — поджигает как огниво (тратится), топливо.
    public static final RegistryObject<Item> MOLTENBEEF = ITEMS.register("moltenbeef", () -> new MoltenBeefItem(new Item.Properties().fireResistant()));
    /// Salamander (редко): обугленный яйцеклад.
    public static final RegistryObject<Item> BURNTOVIPOSITOR = simple("burntovipositor", new Item.Properties().rarity(Rarity.RARE).fireResistant());
    /// Нимфа саламандры в ведре (прирученную нимфу — ведром лавы).
    public static final RegistryObject<Item> SALAMANDER_BUCKET = ITEMS.register("salamander_bucket", () -> new SalamanderBucketItem(new Item.Properties().stacksTo(1)));
    /// Приручение саламандры (1 к 3). В оригинале падает с Imp (не перенесён).
    public static final RegistryObject<Item> IMP_HORN = simple("imp_horn", new Item.Properties());
    /// Приручение саламандры (сразу); при еде — Immolation II на минуту. Рецепт оригинала требует сковороду (не перенесена).
    public static final RegistryObject<Item> KUNG_PAO_CHICKEN = ITEMS.register("kung_pao_chicken", () -> new BowlFoodTooltipItem(new Item.Properties()
            .food(new FoodProperties.Builder().nutrition(8).saturationMod(0.8F).alwaysEat().meat()
                    .effect(() -> new MobEffectInstance(FurEffects.IMMOLATION.get(), 1200, 1), 1.0F).build())));
    /// Weta: хитин (ещё — из панциря Vespa или клешни мимикраба).
    public static final RegistryObject<Item> CHITIN = simple("chitin", new Item.Properties());
    /// Weta (редко): жвало — перемалывается в костную муку.
    public static final RegistryObject<Item> WETA_JAW = simple("weta_jaw", new Item.Properties());
    /// Больная вета, объедая пшеницу, роняет больную пшеницу.
    public static final RegistryObject<Item> DISEASED_WHEAT = simple("diseased_wheat", new Item.Properties());
    /// Хлеб из больной пшеницы: им вету делают больной (нежить, удар — «Загрязнение»). Обращение жителя в зомби не перенесено.
    public static final RegistryObject<Item> DISEASED_BREAD = simple("diseased_bread", new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(5).saturationMod(0.6F)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 160, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 160, 1), 0.3F)
            .effect(() -> new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 1), 0.3F)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 320, 1), 0.3F)
            .effect(() -> new MobEffectInstance(FurEffects.INFESTED.get(), 320, 2), 0.3F).build()));
    /// Приручение веты (1 к 3). В оригинале падает с Undead Swine и пугал (не перенесены).
    public static final RegistryObject<Item> PLAGUED_PORKCHOP = simple("plagued_porkchop", new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(3).saturationMod(0.3F).meat().effect(() -> new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 0), 0.8F).build()));
    /// Приручение веты (сразу); при еде — Спешка на минуту. Рецепт оригинала требует сковороду (не перенесена).
    public static final RegistryObject<Item> GREEN_BACON_AND_EGGS = ITEMS.register("green_bacon_and_eggs", () -> new BowlFoodTooltipItem(new Item.Properties()
            .food(new FoodProperties.Builder().nutrition(10).saturationMod(1.2F).meat().alwaysEat()
                    .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 0), 1.0F).build())));
    /// Mimicrab: сырая клешня (с шансом тошнота), с горящего — сразу печёная.
    public static final RegistryObject<Item> MIMIC_CLAW = simple("mimic_claw", new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(3).saturationMod(0.3F).meat().effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 1200, 0), 0.3F).build()));
    public static final RegistryObject<Item> MIMIC_CLAW_COOKED = simple("mimic_claw_cooked", new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(8).saturationMod(0.8F).meat().build()));
    /// Приручение мимикраба (и его еда). В оригинале падает с Ptera (не перенесена).
    public static final RegistryObject<Item> PTERA_WING = simple("ptera_wing", new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(4).saturationMod(0.1F).meat().alwaysEat().effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 2), 0.8F).build()));
    public static final RegistryObject<Item> PTERA_WING_COOKED = simple("ptera_wing_cooked", new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(8).saturationMod(0.8F).meat().build()));
    /// Расплавленное сердце: делает прирученного мимикраба адским (огнестойкий, поджигает). В оригинале — с Moogma
    /// (не перенесена); своё свойство «−% урона от огня» здесь не перенесено.
    public static final RegistryObject<Item> MOOTENHEART = simple("mootenheart", new Item.Properties().rarity(Rarity.RARE));

    // --- Оружие и бомбы (их же призывают заклинания Necromancer's Delight) ---
    /// Кинжал изо льда: сам чинится в воде.
    public static final RegistryObject<Item> FROZEN_DAGGER = ITEMS.register("frozen_dagger", () -> new FurWeaponItem(Tiers.WOOD, 2, -2.4F,
            () -> Ingredient.of(SHATTERED_ICE.get()), new Item.Properties()));
    /// Костяной меч: +5% максимального здоровья цели к урону. Рецепт оригинала — из острого зуба (не перенесён).
    public static final RegistryObject<Item> BONESWORD = ITEMS.register("bonesword", () -> new FurWeaponItem(Tiers.IRON, 2, -2.4F,
            () -> Ingredient.of(net.minecraft.world.item.Items.BONE_BLOCK), new Item.Properties()));
    /// Призрачный кинжал: вечный, +2 урона живым. Рецепт оригинала — из слитка эктоплазмы (не перенесён).
    public static final RegistryObject<Item> SPECTRAL_DAGGER = ITEMS.register("spectral_dagger", () -> new FurWeaponItem(FurWeaponItem.SPECTRAL, -1, -2.4F,
            () -> Ingredient.of(ECTOPLASM.get()), new Item.Properties()));
    /// Щит Vespa: колет и отравляет того, чей удар заблокирован.
    public static final RegistryObject<Item> VESPA_SHIELD = ITEMS.register("vespa_shield", () -> new VespaShieldItem(new Item.Properties().durability(504)));
    /// Святая граната / призрачная / звуковая бомбы. Рецепты оригинала — из простой бомбы (не перенесена).
    public static final RegistryObject<Item> HOLY_GRENADE = ITEMS.register("holy_grenade", () -> new FurThrowableItem(FurEntities.HOLY_GRENADE, new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> GHOSTBOMB = ITEMS.register("ghostbomb", () -> new FurThrowableItem(FurEntities.GHOSTBOMB, new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> SONICBOMB = ITEMS.register("sonicbomb", () -> new FurThrowableItem(FurEntities.SONICBOMB, new Item.Properties().stacksTo(16)));
    /// Кактусовый шип (подбирается после выстрела шипом).
    public static final RegistryObject<Item> CACTUS_THORN = simple("cactus_thorn", new Item.Properties());

    // --- Яйца призыва (цвета — из оригинала) ---
    public static final RegistryObject<Item> SPAWN_EGG_UNBURIED = egg("unburied", FurEntities.UNBURIED, 13949370, 2698290);
    public static final RegistryObject<Item> SPAWN_EGG_MUMMY = egg("mummy", FurEntities.MUMMY, 15325870, 10125655);
    public static final RegistryObject<Item> SPAWN_EGG_MYCOSIS = egg("mycosis", FurEntities.MYCOSIS, 12378284, 8610589);
    public static final RegistryObject<Item> SPAWN_EGG_FRIGID = egg("frigid", FurEntities.FRIGID, 11526370, 5851215);
    public static final RegistryObject<Item> SPAWN_EGG_FORSAKEN = egg("forsaken", FurEntities.FORSAKEN, 12698049, 4802889);
    public static final RegistryObject<Item> SPAWN_EGG_PARASITE = egg("parasite", FurEntities.PARASITE, 11206638, 12320750);
    public static final RegistryObject<Item> SPAWN_EGG_VESPA = egg("vespa", FurEntities.VESPA, 8774164, 14299417);
    public static final RegistryObject<Item> SPAWN_EGG_BONEWORM = egg("boneworm", FurEntities.BONEWORM, 10000536, 4263438);
    public static final RegistryObject<Item> SPAWN_EGG_BANSHEE = egg("banshee", FurEntities.BANSHEE, 10659725, 3421754);
    public static final RegistryObject<Item> SPAWN_EGG_SALAMANDER = egg("salamander", FurEntities.SALAMANDER, 2491910, 16052546);
    public static final RegistryObject<Item> SPAWN_EGG_WETA = egg("weta", FurEntities.WETA, 8672054, 15387303);
    public static final RegistryObject<Item> SPAWN_EGG_MIMIC = egg("mimic", FurEntities.MIMIC, 14772479, 458752);

    private static RegistryObject<Item> block(RegistryObject<net.minecraft.world.level.block.Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new net.minecraft.world.item.BlockItem(block.get(), new Item.Properties()));
    }

    private static RegistryObject<Item> simple(String name, Item.Properties props) {
        return ITEMS.register(name, () -> new Item(props));
    }

    /// Яйцо `spawn_egg_<моб>` (id как в оригинале); модель — `models/item/spawn_egg_<моб>.json` (template_spawn_egg).
    public static RegistryObject<Item> egg(String mob, Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight) {
        return ITEMS.register("spawn_egg_" + mob, () -> new ForgeSpawnEggItem(type, background, highlight, new Item.Properties()));
    }
}

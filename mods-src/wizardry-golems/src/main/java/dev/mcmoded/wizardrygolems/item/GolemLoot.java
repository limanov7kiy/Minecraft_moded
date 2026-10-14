package dev.mcmoded.wizardrygolems.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

/// Артефакты Wizardry Golems в добыче. В оригинале они попадали в общие таблицы артефактов Wizardry по редкости;
/// в Redux это `ebwizardry:subsets/{uncommon,rare,epic}_artifacts` (одна выборка из N предметов). Таблицу Redux
/// не заменяем (её могут менять другие моды) — добавляем пул: наш артефакт с шансом n/(N+n), иначе пусто.
public final class GolemLoot {
    private GolemLoot() {}

    /// Число предметов в пуле Redux 0.8.9 для каждой редкости.
    private static final Map<String, Integer> REDUX_POOL = Map.of("uncommon", 23, "rare", 20, "epic", 10);

    @SubscribeEvent
    public static void onLoad(LootTableLoadEvent event) {
        ResourceLocation id = event.getName();
        if (!id.getNamespace().equals("ebwizardry") || !id.getPath().startsWith("subsets/") || !id.getPath().endsWith("_artifacts")) return;
        String rarity = id.getPath().substring("subsets/".length(), id.getPath().length() - "_artifacts".length());
        Integer redux = REDUX_POOL.get(rarity);
        if (redux == null) return;
        Rarity target = switch (rarity) {
            case "uncommon" -> Rarity.UNCOMMON;
            case "rare" -> Rarity.RARE;
            default -> Rarity.EPIC;
        };
        LootPool.Builder pool = LootPool.lootPool().name("wizardrygolems_artifacts").setRolls(ConstantValue.exactly(1));
        int n = 0;
        for (RegistryObject<Item> item : GolemItems.ARTIFACTS) {
            if (item.get().getRarity(item.get().getDefaultInstance()) != target) continue;
            pool.add(LootItem.lootTableItem(item.get()));
            n++;
        }
        if (n == 0) return;
        pool.add(EmptyLootItem.emptyItem().setWeight(redux));
        event.getTable().addPool(pool.build());
    }
}

package dev.mcmoded.disasterward;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;

@Mod(WardMod.MOD_ID)
public class WardMod {
    public static final String MOD_ID = "disaster_ward";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Block> WARD = BLOCKS.register("ward", WardBlock::new);
    public static final RegistryObject<Item> WARD_ITEM = ITEMS.register("ward",
            () -> new WardBlock.Item(WARD.get(), new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));

    public WardMod(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(WardMod::onTab);
        context.registerConfig(ModConfig.Type.COMMON, WardConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(WardEvents.class);
    }

    private static void onTab(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey() == CreativeModeTabs.OP_BLOCKS || e.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            e.accept(WARD_ITEM);
        }
    }
}

package dev.mcmoded.rulang;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

/**
 * Подключает наш русский ресурспак (assets из pack/resourcepacks-src/russian-moded, лежит в jar как resourcepacks/russian-moded)
 * как обязательный (нельзя выключить) и верхний (выше MPLOC/RTF+ и других паков игрока), независимо от options.txt.
 */
@Mod(RuLang.MOD_ID)
public class RuLang {
    public static final String MOD_ID = "ru_lang";
    private static final String PACK_ID = "mod_resources_ru_lang";

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void addPack(AddPackFindersEvent event) {
            if (event.getPackType() != PackType.CLIENT_RESOURCES) return;
            Path root = ModList.get().getModFileById(MOD_ID).getFile().findResource("resourcepacks/russian-moded");
            Pack.ResourcesSupplier supplier = name -> new PathPackResources(name, root, true);
            Pack.Info info = Pack.readPackInfo(PACK_ID, supplier);
            if (info == null) return;
            Pack pack = Pack.create(PACK_ID, Component.literal("Русификация сборки (всегда включена)"), true, supplier, info,
                    PackType.CLIENT_RESOURCES, Pack.Position.TOP, true, PackSource.BUILT_IN);
            event.addRepositorySource(consumer -> consumer.accept(pack));
        }
    }

    public RuLang() {}
}

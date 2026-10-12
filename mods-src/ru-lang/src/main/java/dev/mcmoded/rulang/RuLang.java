package dev.mcmoded.rulang;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Подключает наш русский ресурспак (assets из pack/resourcepacks-src/russian-moded, лежит в jar как resourcepacks/russian-moded)
 * как обязательный (нельзя выключить) и верхний (выше MPLOC/RTF+ и других паков игрока), независимо от options.txt.
 */
@Mod(RuLang.MOD_ID)
public class RuLang {
    public static final String MOD_ID = "ru_lang";
    public static final Logger LOGGER = LogUtils.getLogger();

    /** Русификационные ресурспаки сборки в порядке возрастания приоритета (id начинается с номера: репозиторий паков сортирует по id). */
    private static final String[][] PACKS = {
            {"ru_lang_1_just_some_translations", "just-some-translations.zip"},
            {"ru_lang_2_russian_delight", "russian-delight-1.0.zip"},
            {"ru_lang_3_rtf", "rtf-2.8.4.zip"},
            {"ru_lang_4_mploc", "mploc-mods-v43.zip"},
            {"ru_lang_5_rtf_extension", "rtf-extension-2.7.0.zip"},
    };
    private static final int PACK_FORMAT = 15;

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void addPack(AddPackFindersEvent event) {
            if (event.getPackType() != PackType.CLIENT_RESOURCES) return;
            // Все русские паки сборки — обязательные и закреплённые сверху, независимо от options.txt игрока (иначе у кого-то
            // они не включены или Minecraft отбрасывает их как «несовместимые» по pack_format). Приоритет по id (сортировка репозитория):
            // чужие паки < наш пак (ru_lang_9_moded) — он всегда последний.
            Path dir = FMLPaths.GAMEDIR.get().resolve("resourcepacks");
            for (String[] p : PACKS) {
                Path f = dir.resolve(p[1]);
                if (!Files.isRegularFile(f)) continue;
                Pack pack = make(p[0], "Русификация: " + p[1], name -> new FilePackResources(name, f.toFile(), false));
                event.addRepositorySource(consumer -> consumer.accept(pack));
            }
            Path root = ModList.get().getModFileById(MOD_ID).getFile().findResource("resourcepacks/russian-moded");
            Pack ours = make("ru_lang_9_moded", "Русификация сборки (всегда включена)", name -> new PathPackResources(name, root, true));
            event.addRepositorySource(consumer -> consumer.accept(ours));
        }

        private static Pack make(String id, String title, Pack.ResourcesSupplier supplier) {
            // формат подставляем свой (15), не читая pack.mcmeta: у MPLOC он 88, у JustSomeTranslations 34 — иначе пак «несовместим»
            Pack.Info info = new Pack.Info(Component.literal(title), PACK_FORMAT, FeatureFlagSet.of());
            return Pack.create(id, Component.literal(title), true, supplier, info, PackType.CLIENT_RESOURCES, Pack.Position.TOP, true, PackSource.BUILT_IN);
        }
    }

    public RuLang() {
        // самопроверка словаря литералов (виден в логе клиента); миксин на LiteralContents переводит китайские Component.literal
        net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            String direct = Literals.translate("可用于召唤鲍勃");
            // через Component.literal — проходит LiteralContents, т.е. работает ли миксин
            String viaMixin = Component.literal("可用于召唤鲍勃").getString();
            LOGGER.info("ru_lang: словарь литералов {} строк; самопроверка: словарь -> \"{}\"; Component.literal (миксин) -> \"{}\" [{}]",
                    Literals.size(), direct, viaMixin, viaMixin.equals(direct) && !viaMixin.equals("可用于召唤鲍勃") ? "OK" : "МИКСИН НЕ ПРИМЕНИЛСЯ");
        });
    }
}

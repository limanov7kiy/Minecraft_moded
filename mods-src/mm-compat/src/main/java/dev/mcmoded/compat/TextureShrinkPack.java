package dev.mcmoded.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/// R-021 (краш при запуске на слабых GPU): уменьшенные копии самых крупных текстур сборки
/// (сторона > 1024 → 512) подаются как встроенный ресурс-пак «mm_texfix», включённый принудительно
/// (required). Атлас текстур сборки разросся до ~274 Мп — за предел 16384² (268 Мп): стичер ModernFix
/// (faster_texture_stitching) на таких машинах выбрасывает StitcherException, клиент падает при запуске.
/// Оригиналы остаются в jar'ах модов; здесь — замены конкретных файлов отдельным паком поверх модов
/// (модовые ресурсы могли бы перекрываться из-за порядка загрузки). Анимированные текстуры пропущены.
/// Качество: текстура 512 при блоке 16px или на расстоянии незаметна. Откат — убрать регистрацию в {@link MmCompat}.
public final class TextureShrinkPack {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PACK_ID = "mm_texfix";
    private static final String CLASSPATH_PREFIX = "/packs/" + PACK_ID + "/";
    /// «namespace|путь после textures/» — файлы пакета packs/mm_texfix/ (список совпадает с содержимым).
    private static final List<String> SHRUNK = new ArrayList<>();
    private static final Map<String, List<String>> SHRUNK_BY_NS = new HashMap<>();
    private static final Set<String> NAMESPACES = new HashSet<>();

    static {
        SHRUNK.add("chaospersists|textures/entity/thekingtexture.png");
        SHRUNK.add("chaospersists|textures/entity/thequeentexture.png");
        SHRUNK.add("chaospersists|textures/entity/thequeentexture2.png");
        SHRUNK.add("scpfr|textures/block/atlas_woodcardboard_albedo.png");
        SHRUNK.add("scpfr|textures/block/whiteboard_albedo.png");
        SHRUNK.add("spore|textures/entity/adapted_hohl_head.png");
        SHRUNK.add("spore|textures/entity/eyes/graken_ship.png");
        SHRUNK.add("spore|textures/entity/graken_ship.png");
        SHRUNK.add("spore|textures/entity/hohl/adaptedwormsegment2.png");
        SHRUNK.add("spore|textures/entity/hohl/adaptedwormtail.png");
        SHRUNK.add("spore|textures/entity/hohl/hohl_head_adapted.png");
        SHRUNK.add("superbwarfare|textures/item/lod/bocek.png");
        for (String entry : SHRUNK) {
            int sep = entry.indexOf('|');
            String ns = entry.substring(0, sep);
            SHRUNK_BY_NS.computeIfAbsent(ns, k -> new ArrayList<>()).add(entry.substring(sep + 1));
            NAMESPACES.add(ns);
        }
    }

    private TextureShrinkPack() {
    }

    public static void register(net.minecraftforge.eventbus.api.IEventBus modBus) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }

        modBus.addListener(TextureShrinkPack::onAddPackFinders);
    }

    private static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }

        event.addRepositorySource(consumer -> {
            ShrunkPackResources resources = new ShrunkPackResources();
            Pack.Info info = Pack.readPackInfo(PACK_ID, id -> resources);
            if (info == null) {
                LOGGER.warn("mm_texfix: pack.mcmeta не найден — пак не добавлен");
                return;
            }

            // required=true: включён у всех и не отключается в UI (иначе клиент снова упадёт).
            consumer.accept(Pack.create(PACK_ID, Component.literal("MM: сжатые текстуры (R-021)"),
                true, id -> resources, info, PackType.CLIENT_RESOURCES, Pack.Position.TOP, false,
                PackSource.BUILT_IN));
            LOGGER.info("mm_compat: пакет mm_texfix добавлен ({} уменьшенных текстур, {} namespace)",
                SHRUNK.size(), NAMESPACES.size());
        });
    }

    /// Пак, читающий файлы из jar мода: packs/mm_texfix/ (pack.mcmeta + assets/<ns>/…).
    private static final class ShrunkPackResources extends AbstractPackResources {
        ShrunkPackResources() {
            super(PACK_ID, true);
        }

        private static InputStream open(String rel) {
            return TextureShrinkPack.class.getResourceAsStream(CLASSPATH_PREFIX + rel);
        }

        @Nullable
        @Override
        public IoSupplier<InputStream> getRootResource(String... parts) {
            InputStream in = open(String.join("/", parts));
            return in == null ? null : () -> in;
        }

        @Nullable
        @Override
        public IoSupplier<InputStream> getResource(PackType type, ResourceLocation id) {
            if (type != PackType.CLIENT_RESOURCES || !NAMESPACES.contains(id.getNamespace())) {
                return null;
            }

            InputStream in = open("assets/" + id.getNamespace() + "/" + id.getPath());
            return in == null ? null : () -> in;
        }

        @Override
        public void listResources(PackType type, String namespace, String prefix, PackResources.ResourceOutput output) {
            if (type != PackType.CLIENT_RESOURCES) {
                return;
            }

            for (String path : SHRUNK_BY_NS.getOrDefault(namespace, List.of())) {
                if (!path.startsWith(prefix)) {
                    continue;
                }

                InputStream in = open("assets/" + namespace + "/" + path);
                if (in != null) {
                    output.accept(new ResourceLocation(namespace, path), () -> in);
                }
            }
        }

        @Override
        public Set<String> getNamespaces(PackType type) {
            return type == PackType.CLIENT_RESOURCES ? NAMESPACES : Set.of();
        }

        @Override
        public void close() {
        }
    }
}

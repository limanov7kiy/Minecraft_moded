package dev.mcmoded.compat.mixin;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Пики кучи при генерации огромных шаблонов (поток 34, OOM у игрока). StructureTemplate.placeInWorld вызывается
 * для каждого чанка, который задевает структура, и отдаёт в processBlockInfos ВСЕ блоки шаблона: тот копирует
 * каждый (новый BlockPos + StructureBlockInfo + два списка), прогоняет процессоры и лишь потом placeInWorld
 * отбрасывает всё вне коробки чанка. У шаблонов Arknights-модов (bobsoriginiumdream, caerula_arbor) и Aether Villages
 * по 1–2 млн блоков (куб до 128³, почти весь — воздух), и каждый из ~64 чанков структуры на каждом потоке генерации
 * строил копию в 100+ МБ.
 * Здесь большие шаблоны (от FILTER_FROM блоков) заранее фильтруются по X/Z коробки размещения — те же блоки,
 * которые placeInWorld всё равно поставил бы (Y не трогаем: процессоры гравитации меняют высоту).
 * Имена SRG: placeInWorld = m_230328_, processBlockInfos — метод Forge (remap = false, как у других миксинов mm-compat).
 */
@Mixin(StructureTemplate.class)
public abstract class StructureTemplatePlaceMixin {
    @Unique private static final int FILTER_FROM = 65536;
    @Unique private static boolean mm_compat$logged;

    @ModifyArg(method = "m_230328_", remap = false, require = 1, index = 4,
        at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;processBlockInfos(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Ljava/util/List;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)Ljava/util/List;"))
    private List<StructureTemplate.StructureBlockInfo> mm_compat$onlyInBox(ServerLevelAccessor level, BlockPos offset, BlockPos pivot,
            StructurePlaceSettings settings, List<StructureTemplate.StructureBlockInfo> blocks, StructureTemplate template) {
        BoundingBox box = settings.getBoundingBox();
        if (box == null || blocks.size() < FILTER_FROM) return blocks;
        if (!mm_compat$logged) {
            mm_compat$logged = true;
            LogUtils.getLogger().info("mm_compat: большой шаблон структуры ({} блоков) — в чанк передаются только блоки его коробки", blocks.size());
        }
        int x0 = box.minX() - 1, x1 = box.maxX() + 1, z0 = box.minZ() - 1, z1 = box.maxZ() + 1;
        List<StructureTemplate.StructureBlockInfo> out = new ArrayList<>();
        for (StructureTemplate.StructureBlockInfo info : blocks) {
            BlockPos p = StructureTemplate.calculateRelativePosition(settings, info.pos());
            int x = p.getX() + offset.getX(), z = p.getZ() + offset.getZ();
            if (x >= x0 && x <= x1 && z >= z0 && z <= z1) out.add(info);
        }
        return out;
    }
}

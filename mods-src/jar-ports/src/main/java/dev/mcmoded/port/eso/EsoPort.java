package dev.mcmoded.port.eso;

import java.util.Locale;
import java.util.function.Function;
import net.minecraft.world.item.ArmorMaterial;

/**
 * Переходник для Enchanting System Overhaul 1.2.2 (CmdrJane, MIT): ESO обходит все предметы и у брони зовёт
 * ArmorMaterial.getName() (m_6082_), чтобы найти лимит чар материала. Часть модов (Dragon Magic and Relics,
 * Factions and Curiosities, Deep Aether, Ancient Aether и др.) помечает свой getName() как @OnlyIn(Dist.CLIENT) —
 * на выделенном сервере Forge вырезает метод, и вызов падает с AbstractMethodError, сервер не стартует.
 * Здесь — то же имя, а без метода — имя константы enum (или класса) в нижнем регистре: лимиты такого материала
 * берутся по умолчанию, если в config/eso/material-overrides нет записи под этим именем.
 */
public final class EsoPort {
    // через ссылку на метод, а не прямой вызов: JarPort переписывает и классы-переходники, и прямой
    // invokeinterface m_6082_ здесь превратился бы в вызов самого себя
    private static final Function<ArmorMaterial, String> GET_NAME = ArmorMaterial::getName;

    public static String materialName(ArmorMaterial material) {
        try {
            return GET_NAME.apply(material);
        } catch (AbstractMethodError e) {
            String n = material instanceof Enum<?> en ? en.name() : material.getClass().getSimpleName();
            return n.toLowerCase(Locale.ROOT);
        }
    }

    private EsoPort() {}
}

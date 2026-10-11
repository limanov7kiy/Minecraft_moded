/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/content/entity/evolved/EvolvedSpiderEntity.java (same mobs as Legacy core/entities/EntityEvolved*).
 * See CREDITS.md. Vanilla look for now (GC oxygen-gear layer — later); breathe without air via Ad Astra/Northstar tags.
 */
package dev.mcmoded.gcrevival.entity.evolved;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;

public class EvolvedSpider extends Spider {
    public EvolvedSpider(EntityType<? extends EvolvedSpider> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createAttributes();
    }
}

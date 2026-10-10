package dev.mcmoded.port.familiars;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

/**
 * Переходник для Alshanex's Familiars: io.redspace.ironsspellbooks.item.curios.SimpleAttributeCurio
 * удалён из Iron's Spells 3.16 (автор Iron431). Кюрио с одним модификатором атрибута поверх CurioBaseItem.
 */
public class SimpleAttributeCurio extends CurioBaseItem {
    private final Multimap<Attribute, AttributeModifier> attributes;

    public SimpleAttributeCurio(Properties properties, Attribute attribute, AttributeModifier modifier) {
        super(properties);
        this.attributes = ImmutableMultimap.of(attribute, modifier);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        return attributes;
    }
}

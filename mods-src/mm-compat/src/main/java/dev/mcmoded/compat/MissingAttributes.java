package dev.mcmoded.compat;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Расы Arknights (силы arks:*\/thought) дают атрибут the_massestravels:thought_load_limit из мода
 * The Masses Travels, которого нет в открытом доступе. Без атрибута Apoli пишет ошибку загрузки силы.
 * Регистрируем пустой атрибут (его никто не читает), только если самого мода нет; сами силы скрыты
 * переопределением в pack/kubejs/data/arks/powers.
 */
final class MissingAttributes {
    private static final String MOD = "the_massestravels";

    static void register(IEventBus modBus) {
        if (ModList.get().isLoaded(MOD)) return;
        DeferredRegister<Attribute> attributes = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MOD);
        RegistryObject<Attribute> thought = attributes.register("thought_load_limit",
            () -> new RangedAttribute("attribute.name." + MOD + ".thought_load_limit", 0, 0, 1024).setSyncable(true));
        attributes.register(modBus);
        modBus.addListener((EntityAttributeModificationEvent e) -> e.add(EntityType.PLAYER, thought.get()));
    }

    private MissingAttributes() {}
}

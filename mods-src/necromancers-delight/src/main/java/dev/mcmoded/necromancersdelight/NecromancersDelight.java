package dev.mcmoded.necromancersdelight;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.core.registry.EBRegistries;
import dev.mcmoded.necromancersdelight.entity.NdBombs;
import dev.mcmoded.necromancersdelight.spell.NdSpells;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Порт Wizardry - Necromancer's Delight (WinDanesz, 1.12.2, GPL-3.0) на 1.20.1 поверх Redux и нашего порта
/// Fish's Undead Rising (`mod_lavacow`). Namespace оригинала — `necromancersdelight`.
@Mod(NecromancersDelight.MOD_ID)
public class NecromancersDelight {
    public static final String MOD_ID = "necromancersdelight";

    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(EBRegistries.SPELL, MOD_ID);
    public static final DeferredRegister<net.minecraft.world.item.Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<net.minecraft.world.entity.EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<Spell> STINK_BOMB = SPELLS.register("stink_bomb", NdSpells::stinkBomb);
    public static final RegistryObject<Spell> FROST_BOMB = SPELLS.register("frost_bomb", NdSpells::frostBomb);
    public static final RegistryObject<Spell> CORRODE = SPELLS.register("corrode", dev.mcmoded.necromancersdelight.spell.Corrode::new);
    public static final RegistryObject<Spell> GLOWSHROOM = SPELLS.register("glowshroom", dev.mcmoded.necromancersdelight.spell.Glowshroom::new);

    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final RegistryObject<net.minecraft.world.level.block.Block> TEMP_GLOWSHROOM = BLOCKS.register("temporary_glowshroom",
            () -> new dev.mcmoded.necromancersdelight.block.TempGlowshroom(net.minecraft.world.level.block.state.BlockBehaviour.Properties
                    .copy(net.minecraft.world.level.block.Blocks.BROWN_MUSHROOM).lightLevel(s -> 15).noLootTable().noCollission().instabreak()));

    public NecromancersDelight() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        NdBombs.init();
        SPELLS.register(modBus);
        BLOCKS.register(modBus);
        dev.mcmoded.necromancersdelight.effect.NdEffects.EFFECTS.register(modBus);
        ITEMS.register(modBus);
        ENTITIES.register(modBus);
        modBus.addListener(NdBombs::onCreativeTab);
    }
}

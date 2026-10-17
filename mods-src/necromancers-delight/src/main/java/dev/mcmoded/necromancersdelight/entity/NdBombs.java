package dev.mcmoded.necromancersdelight.entity;

import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import dev.mcmoded.necromancersdelight.NecromancersDelight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static dev.mcmoded.necromancersdelight.NecromancersDelight.*;

/// Вонючая и морозная бомбы: сущности, предметы-гранаты (оригинал — `ItemStinkBomb`, `ItemFrostBomb`) и фабрики для
/// заклинаний. Soiled — эффект порта Fish's Undead Rising (`mod_lavacow:soiled`), Frost — эффект Redux.
public final class NdBombs {
    private NdBombs() {}

    public static RegistryObject<EntityType<NdBomb>> STINK_BOMB;
    public static RegistryObject<EntityType<NdBomb>> FROST_BOMB;
    public static RegistryObject<Item> STINK_BOMB_ITEM;
    public static RegistryObject<Item> FROST_BOMB_ITEM;

    static MobEffect soiled() {
        return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("mod_lavacow", "soiled"));
    }

    public static NdBomb stink(Level level) {
        return new NdBomb(STINK_BOMB.get(), level, STINK_BOMB_SPELL(), NdBombs::soiled, EBDamageSources.POISON, () -> STINK_BOMB_ITEM.get(), 0x804200);
    }

    public static NdBomb frost(Level level) {
        return new NdBomb(FROST_BOMB.get(), level, FROST_BOMB_SPELL(), () -> EBMobEffects.FROST.get(), EBDamageSources.FROST, () -> FROST_BOMB_ITEM.get(), 0xBFE6FF);
    }

    private static java.util.function.Supplier<com.binaris.wizardry.api.content.spell.Spell> STINK_BOMB_SPELL() {
        return NecromancersDelight.STINK_BOMB;
    }

    private static java.util.function.Supplier<com.binaris.wizardry.api.content.spell.Spell> FROST_BOMB_SPELL() {
        return NecromancersDelight.FROST_BOMB;
    }

    public static void init() {
        STINK_BOMB = ENTITIES.register("stink_bomb", () -> EntityType.Builder.<NdBomb>of((t, l) -> stink(l), MobCategory.MISC)
                .sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("stink_bomb"));
        FROST_BOMB = ENTITIES.register("frost_bomb", () -> EntityType.Builder.<NdBomb>of((t, l) -> frost(l), MobCategory.MISC)
                .sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("frost_bomb"));
        STINK_BOMB_ITEM = ITEMS.register("stink_bomb", () -> new Grenade(NdBombs::stink));
        FROST_BOMB_ITEM = ITEMS.register("frost_bomb", () -> new Grenade(NdBombs::frost));
    }

    /// Метаемая бомба: стак 16, откат 1 с (как в оригинале).
    static final class Grenade extends Item {
        private final java.util.function.Function<Level, NdBomb> factory;

        Grenade(java.util.function.Function<Level, NdBomb> factory) {
            super(new Properties().stacksTo(16));
            this.factory = factory;
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack stack = player.getItemInHand(hand);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), com.binaris.wizardry.setup.registries.EBSounds.ENTITY_SMOKE_BOMB_THROW.get(),
                    SoundSource.NEUTRAL, 0.5f, 0.4f / (level.random.nextFloat() * 0.4f + 0.8f));
            player.getCooldowns().addCooldown(this, 20);
            if (!level.isClientSide) {
                NdBomb bomb = factory.apply(level);
                bomb.setOwner(player);
                bomb.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                bomb.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1f, 1f);
                level.addFreshEntity(bomb);
            }
            if (!player.getAbilities().instabuild) stack.shrink(1);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
    }

    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().toString().equals("ebwizardry:ebwizardry")) {
            event.accept(STINK_BOMB_ITEM.get());
            event.accept(FROST_BOMB_ITEM.get());
        }
    }
}

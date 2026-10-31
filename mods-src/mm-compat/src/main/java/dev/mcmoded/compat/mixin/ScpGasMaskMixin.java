package dev.mcmoded.compat.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Один противогаз (поток 73, политика docs/dedup.md): противогаз Spore засчитывается проверками SCP: Ultimate (onneim)
 * так же, как его собственный {@code scp_ultimate:gasmask_helmet} — охрана SCP Guard (часть «маскировки» под класс D)
 * и защита от SCP-008. Процедуры MCreator сравнивают {@code getItemBySlot(HEAD).getItem() == GASMASK_HELMET.get()};
 * подменяем результат {@code getItem()} для маски Spore на противогаз SCP. Оверлей противогаза SCP (клиент) не трогаем —
 * у маски Spore свой вид. Противогаз SCP в сборке без крафта и лута (KubeJS dedup_list.js), уже существующий работает.
 */
@Pseudo
@Mixin(targets = {
        "net.mcreator.scpultimate.procedures.ScpGuardprocedure1Procedure",
        "net.mcreator.scpultimate.procedures.Playertickguard1Procedure",
        "net.mcreator.scpultimate.procedures.SCP008OnTickUpdate2Procedure"}, remap = false)
public abstract class ScpGasMaskMixin {
    @Unique private static Item mm_compat$spore;
    @Unique private static Item mm_compat$scp;

    @Redirect(method = "execute", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;m_41720_()Lnet/minecraft/world/item/Item;", remap = false))
    private static Item mm_compat$sporeMaskCounts(ItemStack stack) {
        Item item = stack.getItem();
        if (mm_compat$spore == null) {
            mm_compat$spore = BuiltInRegistries.ITEM.get(new ResourceLocation("spore", "gas_mask"));
            mm_compat$scp = BuiltInRegistries.ITEM.get(new ResourceLocation("scp_ultimate", "gasmask_helmet"));
        }
        return item == mm_compat$spore && item != Items.AIR ? mm_compat$scp : item;
    }
}

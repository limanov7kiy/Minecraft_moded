package dev.mcmoded.rulang.mixin;

import dev.mcmoded.rulang.Literals;
import net.minecraft.network.chat.contents.LiteralContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Единая точка всех Component.literal(...) (в т.ч. сообщения с сервера и NBT-тексты, которые клиент десериализует): китайская строка → русская по словарю. */
@Mixin(LiteralContents.class)
public abstract class LiteralContentsMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, remap = false)
    private static String ru_lang$translate(String text) {
        return Literals.translate(text);
    }
}

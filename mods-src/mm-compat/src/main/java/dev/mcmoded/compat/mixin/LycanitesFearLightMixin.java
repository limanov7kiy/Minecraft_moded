package dev.mcmoded.compat.mixin;

import com.lycanitesmobs.client.effect.FearVisualHandler;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Затемнение света от страха Lycanites Mobs (0.1.0-alpha, Lycanites/Nephrite) — перенос их LightTextureMixin.
 * У оригинала @Shadow-поле названо по официальным маппингам (lightTexture), а Mixin в собранной игре ищет поле
 * только по имени в классе (refmap к @Shadow-полям не применяется) — их миксин не применяется. Здесь SRG-имена
 * заданы явно (remap = false): updateLightTexture = m_109881_, upload = m_117985_, lightTexture = f_109870_.
 * Логика та же, вызовы — в их FearVisualHandler.
 */
@Mixin(LightTexture.class)
public abstract class LycanitesFearLightMixin {
    @Shadow(remap = false) @Final private DynamicTexture f_109870_;

    @Inject(method = "m_109881_", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;m_117985_()V", remap = false))
    private void mm_compat$dimLightForFear(float partialTick, CallbackInfo ci) {
        float blockDim = FearVisualHandler.getBlockDimFactor();
        float skyDim = FearVisualHandler.getSkyDimFactor();
        if (blockDim <= 0.0F && skyDim <= 0.0F) return;
        NativeImage pixels = f_109870_.getPixels();
        if (pixels == null) return;
        FearVisualHandler.saveCleanPixels(pixels, f_109870_);
        // блочный свет тянем к столбцу «без блочного света», небесный — к строке «без неба»
        if (blockDim > 0.0F)
            for (int sky = 0; sky < 16; sky++)
                for (int block = 1; block < 16; block++) dim(pixels, block, sky, pixels.getPixelRGBA(0, sky), blockDim);
        if (skyDim > 0.0F)
            for (int block = 0; block < 16; block++)
                for (int sky = 1; sky < 16; sky++) dim(pixels, block, sky, pixels.getPixelRGBA(block, 0), skyDim);
        FearVisualHandler.saveDimmedPixels(pixels);
    }

    private static void dim(NativeImage pixels, int x, int y, int ref, float factor) {
        int color = pixels.getPixelRGBA(x, y);
        int r = color & 0xFF, g = color >> 8 & 0xFF, b = color >> 16 & 0xFF, a = color >> 24 & 0xFF;
        r += (int) ((float) ((ref & 0xFF) - r) * factor);
        g += (int) ((float) ((ref >> 8 & 0xFF) - g) * factor);
        b += (int) ((float) ((ref >> 16 & 0xFF) - b) * factor);
        pixels.setPixelRGBA(x, y, a << 24 | b << 16 | g << 8 | r);
    }
}

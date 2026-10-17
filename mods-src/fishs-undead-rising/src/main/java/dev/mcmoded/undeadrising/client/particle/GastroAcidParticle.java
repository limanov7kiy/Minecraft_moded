package dev.mcmoded.undeadrising.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;

/// Брызги желудочного сока: тускло-красные капли, падают в воздухе, тают ко второй половине жизни.
/// Оригинал: Fish's Undead Rising (Fish), `client/particle/GastroAcidParticle`.
public class GastroAcidParticle extends SimpleAnimatedParticle {
    private GastroAcidParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz,
                               float red, float green, float blue, float offset, SpriteSet sprites) {
        super(level, x, y, z, sprites, 0.0F);
        float colorOffset = this.random.nextFloat() * offset;
        this.quadSize = 0.5F;
        this.setAlpha(1.0F);
        this.setColor(red, green - colorOffset, blue - colorOffset);
        this.lifetime = (int) (this.quadSize * 12.0F / (Math.random() * 0.8F + 0.2F));
        this.setSpriteFromAge(sprites);
        this.hasPhysics = false;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.setFadeColor(0);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.setSpriteFromAge(this.sprites);
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime);
        }
        this.move(this.xd, this.yd, this.zd);
        if (this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) this.yd -= 0.008F;
        this.xd *= 0.92F;
        this.yd *= 0.92F;
        this.zd *= 0.92F;
        if (this.onGround) {
            this.xd *= 0.7F;
            this.zd *= 0.7F;
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new GastroAcidParticle(level, x, y, z, dx, dy, dz, 0.56F, 0.42F, 0.42F, 0.37F, this.sprites);
        }
    }
}

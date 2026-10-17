package dev.mcmoded.undeadrising.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

/// Саранча вокруг мумии: мелкие частицы, медленно разлетаются.
/// Оригинал: Fish's Undead Rising (Fish), `client/particle/LocustSwarmParticle`.
public class LocustSwarmParticle extends TextureSheetParticle {
    private LocustSwarmParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.quadSize *= 0.2F + this.random.nextFloat() * 0.55F;
        this.lifetime = (int) (5.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
        this.roll = (float) Math.random() * (float) (Math.PI * 2.0 / 5.0);
        this.oRoll = this.roll;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float partialTick) {
        return this.quadSize * Mth.clamp((this.age + partialTick) / this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 1.015;
            this.yd *= 0.96F;
            this.zd *= 0.96F;
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            LocustSwarmParticle p = new LocustSwarmParticle(level, x, y, z);
            p.pickSprite(this.sprite);
            return p;
        }
    }
}

package com.hekaijun.inscribe.enchantment.sunder.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class SunderParticle extends TextureSheetParticle {
    private SunderParticle(ClientLevel level, double x, double y, double z,
                           double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.lifetime = 20;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.quadSize = 0.5F;
        this.alpha = 0.8F;
        this.xd = dx * 0.1D;
        this.yd = dy * 0.1D + 0.02D;
        this.zd = dz * 0.1D;
        this.setSprite(sprites.get(this.random));
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 0.8F * (1.0F - (float) this.age / (float) this.lifetime);
    }

    @Override
    public float getQuadSize(float partialTicks) {
        float progress = ((float) this.age + partialTicks) / (float) this.lifetime;
        return this.quadSize * (0.7F + 0.3F * progress);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new SunderParticle(level, x, y, z, dx, dy, dz, this.sprites);
        }
    }
}

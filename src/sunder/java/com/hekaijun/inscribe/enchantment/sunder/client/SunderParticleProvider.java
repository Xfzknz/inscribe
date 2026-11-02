package com.hekaijun.inscribe.enchantment.sunder.client;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.enchantment.sunder.SunderModule;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SunderParticleProvider {
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SunderModule.SUNDER_PARTICLE.get(), SunderParticle.Provider::new);
    }

    private SunderParticleProvider() {
    }
}

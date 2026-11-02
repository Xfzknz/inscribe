package com.hekaijun.inscribe.enchantment.sunder;

import java.util.List;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.core.InscribeModule;
import com.hekaijun.inscribe.core.LootBookHelper;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SunderModule implements InscribeModule {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Inscribe.MODID);
    public static final RegistryObject<Enchantment> SUNDER =
            ENCHANTMENTS.register("sunder", SunderEnchantment::new);

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Inscribe.MODID);
    public static final RegistryObject<SimpleParticleType> SUNDER_PARTICLE =
            PARTICLES.register("sunder", () -> new SimpleParticleType(false));

    @Override
    public String id() {
        return "sunder";
    }

    @Override
    public void register(IEventBus modBus) {
        ENCHANTMENTS.register(modBus);
        PARTICLES.register(modBus);
    }

    @Override
    public List<LootPoolEntryContainer.Builder<?>> lootEntries(ResourceLocation table) {
        return List.of(
                LootBookHelper.bookEntry(SUNDER.get(), 1, 3),
                LootBookHelper.bookEntry(SUNDER.get(), 2, 2),
                LootBookHelper.bookEntry(SUNDER.get(), 3, 1),
                LootBookHelper.bookEntry(SUNDER.get(), 4, 1)
        );
    }
}

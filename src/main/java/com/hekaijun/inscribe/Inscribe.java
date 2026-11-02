package com.hekaijun.inscribe;

import com.hekaijun.inscribe.core.InscribeNetwork;
import com.hekaijun.inscribe.core.ModuleLoader;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Inscribe.MODID)
public class Inscribe {
    public static final String MODID = "inscribe";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Inscribe() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModuleLoader.registerAll(modBus);
        InscribeNetwork.register();
        modBus.addListener(this::commonSetup);
        LOGGER.info("Inscribe {} loaded with modules [{}]", MODID, ModuleLoader.ids());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Inscribe modules: [{}]", ModuleLoader.ids());
    }
}

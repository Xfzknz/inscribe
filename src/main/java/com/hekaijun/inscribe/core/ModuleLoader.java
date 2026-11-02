package com.hekaijun.inscribe.core;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import com.hekaijun.inscribe.Inscribe;
import net.minecraftforge.eventbus.api.IEventBus;

public final class ModuleLoader {
    private static final List<InscribeModule> MODULES = ServiceLoader
            .load(InscribeModule.class, Inscribe.class.getClassLoader())
            .stream()
            .map(ServiceLoader.Provider::get)
            .sorted(Comparator.comparing(InscribeModule::id))
            .toList();

    public static List<InscribeModule> modules() {
        return MODULES;
    }

    public static void registerAll(IEventBus modBus) {
        MODULES.forEach(module -> module.register(modBus));
    }

    public static String ids() {
        return MODULES.stream().map(InscribeModule::id).collect(Collectors.joining(", "));
    }

    private ModuleLoader() {
    }
}

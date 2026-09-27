package net.dusty_dusty.bnb_core;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

public enum ModIntegration {
    createdeco,
    curios,
    metalbundles;

    public static final ModIntegration[] values = values();

    public final boolean loaded;

    public ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(name(), path);
    }

    ModIntegration() {
        loaded = ModList.get().isLoaded(name());
    }
}

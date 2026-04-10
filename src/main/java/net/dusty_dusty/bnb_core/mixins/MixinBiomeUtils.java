package net.dusty_dusty.bnb_core.mixins;

import com.chaosthedude.naturescompass.util.BiomeUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin( BiomeUtils.class )
public abstract class MixinBiomeUtils {

    /**
     * @author Ada Aster
     * @reason Lazy. Small Fix.
     */
    @Overwrite( remap = false )
    public static List<ResourceLocation> getAllowedBiomeKeys(Level level) {
        final List<ResourceLocation> biomeKeys = new ArrayList<ResourceLocation>();
        if (getBiomeRegistry(level).isPresent()) {
            for (Map.Entry<ResourceKey<Biome>, Biome> entry : getBiomeRegistry(level).get().entrySet()) {
                Biome biome;
                try {
                    biome = entry.getValue();
                } catch ( Exception ignored ) {
                    continue;
                }

                if (biome != null) {
                    Optional<ResourceLocation> optionalBiomeKey = getKeyForBiome(level, biome);
                    if (biome != null && optionalBiomeKey.isPresent() && !biomeKeyIsBlacklisted(level, optionalBiomeKey.get())) {
                        biomeKeys.add(optionalBiomeKey.get());
                    }
                }
            }
        }

        return biomeKeys;
    }

    @Shadow( remap = false )
    public static Optional<? extends Registry<Biome>> getBiomeRegistry(Level level) {
        return Optional.empty();
    }

    @Shadow( remap = false )
    public static Optional<ResourceLocation> getKeyForBiome(Level level, Biome biome) {
        return Optional.empty();
    }

    @Shadow( remap = false )
    public static boolean biomeKeyIsBlacklisted(Level level, ResourceLocation biomeKey) {
        return false;
    }
}

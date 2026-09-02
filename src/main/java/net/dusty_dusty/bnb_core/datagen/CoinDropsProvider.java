package net.dusty_dusty.bnb_core.datagen;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.cold_crops.data.CoinData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CoinDropsProvider implements DataProvider {

    protected final PackOutput.PathProvider coinDropsPathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;


    public CoinDropsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        this.coinDropsPathProvider = pOutput.createPathProvider(PackOutput.Target.DATA_PACK, "coin_drops");
        this.registries = pRegistries;
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput pOutput) {
        return this.registries.thenCompose(provider -> this.run(pOutput, provider));
    }


    public CompletableFuture<?> run(CachedOutput pOutput, HolderLookup.Provider pRegistries) {
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildCoinDrops((coinData, resourceLocation) -> {
            if (!set.add(resourceLocation)) {
                throw new IllegalStateException("Duplicate coin drop " + resourceLocation);
            } else {
                JsonElement jsonElement = CoinData.CODEC.encodeStart(JsonOps.INSTANCE, coinData).result().orElseThrow();
                list.add(DataProvider.saveStable(pOutput, jsonElement, this.coinDropsPathProvider.json(resourceLocation)));
            }
        },pRegistries);
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    protected void buildCoinDrops(BiConsumer<CoinData,ResourceLocation> consumer, HolderLookup.Provider pRegistries) {
            CoinDataBuilder.builder(Tags.EntityTypes.BOSSES)
                    .netheriteCoin()
                    .save(consumer, BnbCore.id("bosses"));
    }

        @Override
    public String getName() {
        return "Coin Drops";
    }
}

package net.dusty_dusty.bnb_core.cold_crops.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.Map;

public class CoinDrops extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    private Map<ResourceLocation, CoinData> data;

    public CoinDrops() {
        super(GSON, "coin_drops");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        ImmutableMap.Builder<ResourceLocation, CoinData> builder = ImmutableMap.builder();

        for(Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            try {
                CoinData coinData = fromJson(resourcelocation, GsonHelper
                        .convertToJsonObject(entry.getValue(), "top element"));
                if (coinData == null) {
                    LOGGER.info("Skipping loading coinData {} as it's serializer returned null", resourcelocation);
                    continue;
                }
                builder.put(resourcelocation, coinData);
            } catch (IllegalArgumentException | JsonParseException exception) {
                LOGGER.error("Parsing error loading coinData {}", resourcelocation, exception);
            }
        }

        this.data = builder.build();
        LOGGER.info("Loaded {} coinDatas", data.size());
    }

    public static CoinData fromJson(ResourceLocation pRecipeId,JsonElement pElement) {
        return CoinData.CODEC.parse(JsonOps.INSTANCE,pElement).resultOrPartial(LOGGER::error).orElseThrow();
    }

    public Map<ResourceLocation, CoinData> getData() {
        return data;
    }
}

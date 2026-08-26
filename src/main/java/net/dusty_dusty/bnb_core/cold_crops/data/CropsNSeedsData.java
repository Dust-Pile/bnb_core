package net.dusty_dusty.bnb_core.cold_crops.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.cold_crops.network.PacketChannel;
import net.dusty_dusty.bnb_core.cold_crops.network.SyncDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class CropsNSeedsData extends SimpleJsonResourceReloadListener {
    public Map<ResourceLocation , CropData> CROPS_MAP;
    public Map<Item, ResourceLocation> SEEDS_LIST; // Seed resloc string, block/crop resloc string
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public CropsNSeedsData() {
        super(GSON, "bnbcore");
    }

    public void setCropsMap(Map<ResourceLocation, CropData> cropsMap) {
        CROPS_MAP = cropsMap;
    }
    public void setSeedsList(Map<Item, ResourceLocation> seedsList) {
        SEEDS_LIST = seedsList;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        CROPS_MAP = new HashMap<>();
        SEEDS_LIST = new HashMap<>();
        elementMap.forEach((resourceLocation, jsonElement) -> {

            CropData cropData = new CropData(jsonElement);

            CROPS_MAP.put(resourceLocation, cropData);
            if (cropData.getSeedItem() != null) SEEDS_LIST.put(cropData.getSeedItem(), resourceLocation);
            else BnbCore.LOGGER.warn("NO VALID SEED ASSIGNED TO {}", resourceLocation);

            BnbCore.LOGGER.info("E: {} || {}", resourceLocation, jsonElement);
        });

        BnbCore.LOGGER.info("{} crop(s) with temp stats were added", CROPS_MAP.size());
        BnbCore.LOGGER.info("{} seed(s) with temp stats were added", SEEDS_LIST.size());

        //TODO this
            try {
                PacketChannel.sendToAllClients(new SyncDataPacket(CROPS_MAP));
            } catch (Exception ignored) {

            }
    }

    public void clear() {
        CROPS_MAP.clear();
        SEEDS_LIST.clear();
    }
}
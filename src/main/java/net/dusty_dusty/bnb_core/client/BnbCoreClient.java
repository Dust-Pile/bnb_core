package net.dusty_dusty.bnb_core.client;

import com.mojang.datafixers.util.Either;
import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.cold_crops.data.CropData;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.dusty_dusty.bnb_core.cold_crops.tooltip.ClientTempTooltipComponent;
import net.dusty_dusty.bnb_core.cold_crops.tooltip.TempTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.HashMap;
import java.util.Map;

public class BnbCoreClient {

    private static CropsNSeedsData cropsNSeedsData = new CropsNSeedsData();

    public static void init(IEventBus bus) {
        MinecraftForge.EVENT_BUS.addListener(BnbCoreClient::onTooltip);
        MinecraftForge.EVENT_BUS.addListener(BnbCoreClient::onPlayerLeave);
    }

    public static CropsNSeedsData getCropsNSeedsData() {
        return cropsNSeedsData;
    }

    public static void setMaps(Map<ResourceLocation, CropData> cropMap) {
        Map<Item,ResourceLocation> seedList = new HashMap<>();

        for (Map.Entry<ResourceLocation, CropData> entry : cropMap.entrySet()) {//recreating the list on the client saves bandwidth
            if (entry.getValue().getSeedItem() != null) {
                seedList.put(entry.getValue().getSeedItem(), entry.getKey());
            }
        }

        cropsNSeedsData.setCropsMap(cropMap);
        cropsNSeedsData.setSeedsList(seedList);
    }

    public static void onTooltip(RenderTooltipEvent.GatherComponents event) {

        Item resLoc = event.getItemStack().getItem();
        if (cropsNSeedsData.SEEDS_LIST.containsKey(resLoc)) {
            CropData data = cropsNSeedsData.CROPS_MAP.get(cropsNSeedsData.SEEDS_LIST.get(resLoc));
            event.getTooltipElements().add(1, Either.right(new TempTooltipComponent(data)));
        }
    }

    public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(TempTooltipComponent.class, ClientTempTooltipComponent::new);
    }

    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            //It's to save memory but is it really necessary
            BnbCore.LOGGER.info("Clearing unneeded data");
            BnbCore.getCropsAndSeedsData(event.getEntity().level()).clear();
        }
    }
}

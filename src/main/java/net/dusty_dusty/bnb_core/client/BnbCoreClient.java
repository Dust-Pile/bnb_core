package net.dusty_dusty.bnb_core.client;

import com.mojang.datafixers.util.Either;
import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.cold_crops.data.CropData;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.dusty_dusty.bnb_core.cold_crops.tooltip.ClientTempTooltipComponent;
import net.dusty_dusty.bnb_core.cold_crops.tooltip.TempTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class BnbCoreClient {

    private static CropsNSeedsData cropsNSeedsData = new CropsNSeedsData();

    public static void init(IEventBus bus) {

    }

    public static CropsNSeedsData getCropsNSeedsData() {
        return cropsNSeedsData;
    }

    public static void setCropsMap(Map<ResourceLocation, CropData> cropMap) {
        cropsNSeedsData.setCropsMap(cropMap);
    }

    public static void setSeedsList(Map<Item, ResourceLocation> seedsList) {
        cropsNSeedsData.setSeedsList(seedsList);
    }

    public static void onTooltip(RenderTooltipEvent.GatherComponents event) {

        ResourceLocation resLoc = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
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

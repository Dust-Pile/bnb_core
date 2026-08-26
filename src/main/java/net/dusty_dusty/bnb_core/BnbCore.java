package net.dusty_dusty.bnb_core;

import com.mojang.logging.LogUtils;
import com.seibel.distanthorizons.api.methods.events.DhApiEventRegister;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiChunkProcessingEvent;
import net.dusty_dusty.bnb_core.client.BnbCoreClient;
import net.dusty_dusty.bnb_core.cold_crops.ColdCrops;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.dusty_dusty.bnb_core.cold_crops.network.PacketChannel;
import net.dusty_dusty.bnb_core.lod_handling.DhBlockFixer;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import net.minecraftforge.eventbus.api.SubscribeEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BnbCore.MODID)
public class BnbCore
{
    public static final String MODID = "bnb_core";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean DEBUG;
    private static CropsNSeedsData serverCropsAndSeedsData;

    public BnbCore(FMLJavaModLoadingContext context )
    {
        Dist dist = FMLEnvironment.dist;
        DEBUG = !FMLEnvironment.production;
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener( this::commonSetup );

        MinecraftForge.EVENT_BUS.register( this );
//        EventManager.addListener( this::onSeasonChangeSTD );
//        EventManager.addListener( this::onSeasonChangeTROP );

        DhApiEventRegister.on( DhApiChunkProcessingEvent.class, new DhBlockFixer() );

        ColdCrops.initialize( context,dist );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(PacketChannel::register);
    }

    @SubscribeEvent
    public void jsonReading(AddReloadListenerEvent event) {
        serverCropsAndSeedsData = new CropsNSeedsData();
        event.addListener(serverCropsAndSeedsData);
    }

    public static CropsNSeedsData getCropsAndSeedsData(Level level) {
        if (level.isClientSide) {
            return BnbCoreClient.getCropsNSeedsData();
        }
        return serverCropsAndSeedsData;
    }

    public static CropsNSeedsData getCropsAndSeedsDataUnsafe() {
        return serverCropsAndSeedsData;
    }

//    public void onSeasonChangeSTD( SeasonChangedEvent.Standard event ) {
//        DhApi.Delayed.renderProxy.clearRenderDataCache();
//    }
//    public void onSeasonChangeTROP( SeasonChangedEvent.Tropical event ) {
//        DhApi.Delayed.renderProxy.clearRenderDataCache();
//    }
}

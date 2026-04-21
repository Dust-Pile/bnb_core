package net.dusty_dusty.bnb_core;

import com.mojang.logging.LogUtils;
import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.methods.events.DhApiEventRegister;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiChunkProcessingEvent;
import glitchcore.event.EventManager;
import net.dusty_dusty.bnb_core.cold_crops.ColdCrops;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.dusty_dusty.bnb_core.cold_crops.network.PacketChannel;
import net.dusty_dusty.bnb_core.lod_handling.DhBlockFixer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import sereneseasons.api.season.SeasonChangedEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BnbCore.MODID)
public class BnbCore
{
    public static final String MODID = "bnb_core";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BnbCore(FMLJavaModLoadingContext context )
    {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener( this::commonSetup );

        IEventBus ForgeEventBus = MinecraftForge.EVENT_BUS;
        ForgeEventBus.register( this );
//        EventManager.addListener( this::onSeasonChangeSTD );
//        EventManager.addListener( this::onSeasonChangeTROP );

        DhApiEventRegister.on( DhApiChunkProcessingEvent.class, new DhBlockFixer() );

        new ColdCrops().initialize( context );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(PacketChannel::register);
    }

    @SubscribeEvent
    public void jsonReading(AddReloadListenerEvent event) {
        event.addListener(CropsNSeedsData.instance);
    }

//    public void onSeasonChangeSTD( SeasonChangedEvent.Standard event ) {
//        DhApi.Delayed.renderProxy.clearRenderDataCache();
//    }
//    public void onSeasonChangeTROP( SeasonChangedEvent.Tropical event ) {
//        DhApi.Delayed.renderProxy.clearRenderDataCache();
//    }
}

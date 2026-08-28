package net.dusty_dusty.bnb_core;

import com.github.talrey.createdeco.CreateDecoMod;
import com.github.talrey.createdeco.ItemRegistry;
import com.mojang.logging.LogUtils;
import com.seibel.distanthorizons.api.methods.events.DhApiEventRegister;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiChunkProcessingEvent;
import net.dusty_dusty.bnb_core.accessories.PouchCurio;
import net.dusty_dusty.bnb_core.client.BnbCoreClient;
import net.dusty_dusty.bnb_core.cold_crops.ColdCrops;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.dusty_dusty.bnb_core.cold_crops.network.PacketChannel;
import net.dusty_dusty.bnb_core.datagen.BnbDatagen;
import net.dusty_dusty.bnb_core.lod_handling.DhBlockFixer;
import net.minecraft.world.item.Items;
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

import top.theillusivec4.curios.api.CuriosApi;

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
        modEventBus.addListener(BnbDatagen::gather);
        MinecraftForge.EVENT_BUS.addListener(this::jsonReading);
//        EventManager.addListener( this::onSeasonChangeSTD );
//        EventManager.addListener( this::onSeasonChangeTROP );

        DhApiEventRegister.on( DhApiChunkProcessingEvent.class, new DhBlockFixer() );

        ColdCrops.initialize( context,dist );
        if (dist.isClient()) {
            BnbCoreClient.init(modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CuriosApi.registerCurio(Items.BUNDLE, new PouchCurio());
        event.enqueueWork(PacketChannel::register);

        if (ModIntegration.createdeco.loaded) {
            CoinHandler.setup();
        }
    }

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

package net.dusty_dusty.bnb_core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.seibel.distanthorizons.api.methods.events.DhApiEventRegister;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiChunkProcessingEvent;
import fuzs.metalbundles.world.item.MetalBundleItem;
import net.dusty_dusty.bnb_core.coins.CoinHandler;
import net.dusty_dusty.bnb_core.cold_crops.data.CoinDrops;
import net.dusty_dusty.bnb_core.curios.PouchCurio;
import net.dusty_dusty.bnb_core.client.BnbCoreClient;
import net.dusty_dusty.bnb_core.cold_crops.ColdCrops;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.dusty_dusty.bnb_core.cold_crops.network.PacketChannel;
import net.dusty_dusty.bnb_core.datagen.BnbDatagen;
import net.dusty_dusty.bnb_core.lod_handling.DhBlockFixer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BnbCore.MODID)
public class BnbCore
{
    public static final String MODID = "bnb_core";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean DEBUG;
    private static CropsNSeedsData serverCropsAndSeedsData;
    private static CoinDrops coinDrops;

    public BnbCore(FMLJavaModLoadingContext context )
    {
        Dist dist = FMLEnvironment.dist;
        DEBUG = !FMLEnvironment.production;
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener( this::commonSetup );
        modEventBus.addListener(BnbDatagen::gather);
        MinecraftForge.EVENT_BUS.addListener(this::jsonReading);
        MinecraftForge.EVENT_BUS.addListener(this::onHurt);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
//        EventManager.addListener( this::onSeasonChangeSTD );
//        EventManager.addListener( this::onSeasonChangeTROP );

        DhApiEventRegister.on( DhApiChunkProcessingEvent.class, new DhBlockFixer() );

        ColdCrops.initialize( context,dist );
        if (dist.isClient()) {
            BnbCoreClient.init(modEventBus);
        }
    }

    public static ResourceLocation id(String s) {
        return new ResourceLocation(MODID, s);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CuriosApi.registerCurio(Items.BUNDLE, new PouchCurio());
        event.enqueueWork(PacketChannel::register);

        if (ModIntegration.createdeco.loaded) {
            CoinHandler.setup();
        }
    }

    private void commands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        UnitTests.register(dispatcher);
    }

    private void onHurt(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof Player player) {
            ListTag tag = target.getPersistentData().getList(CoinHandler.KILL_CREDIT, CompoundTag.TAG_STRING);
            tag.add(StringTag.valueOf(player.getUUID().toString()));
            target.getPersistentData().put(CoinHandler.KILL_CREDIT, tag);
        }
    }

    public void jsonReading(AddReloadListenerEvent event) {
        serverCropsAndSeedsData = new CropsNSeedsData();
        coinDrops = new CoinDrops();
        event.addListener(serverCropsAndSeedsData);
        event.addListener(coinDrops);
    }

    public static CropsNSeedsData getCropsAndSeedsData(Level level) {
        if (level.isClientSide) {
            return BnbCoreClient.getCropsNSeedsData();
        }
        return serverCropsAndSeedsData;
    }

    public static CoinDrops getCoinDrops(Level level) {
        if (level.isClientSide) {
            return null;
        }
        return coinDrops;
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

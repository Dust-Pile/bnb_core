package net.dusty_dusty.bnb_core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import com.seibel.distanthorizons.api.methods.events.DhApiEventRegister;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiChunkProcessingEvent;
import net.dusty_dusty.bnb_core.coins.CoinHandler;
import net.dusty_dusty.bnb_core.cold_crops.data.CoinDrops;
import net.dusty_dusty.bnb_core.curios.PouchCurio;
import net.dusty_dusty.bnb_core.client.BnbCoreClient;
import net.dusty_dusty.bnb_core.cold_crops.ColdCrops;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.dusty_dusty.bnb_core.cold_crops.network.PacketChannel;
import net.dusty_dusty.bnb_core.datagen.BnbDatagen;
import net.dusty_dusty.bnb_core.init.BNBCoreAttributes;
import net.dusty_dusty.bnb_core.lod_handling.DhBlockFixer;
import net.dusty_dusty.bnb_core.tags.BnbItemTags;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
    private static CoinDrops coinDrops;

    public BnbCore(FMLJavaModLoadingContext context )
    {
        Dist dist = FMLEnvironment.dist;
        DEBUG = !FMLEnvironment.production;
        IEventBus modEventBus = context.getModEventBus();
        BNBCoreAttributes.init(modEventBus);
        //BNBCoreEffects.init(modEventBus);
        //BNBCorePotions.init(modEventBus);
        modEventBus.addListener( this::commonSetup );
        modEventBus.addListener(BnbDatagen::gather);
        MinecraftForge.EVENT_BUS.addListener(this::jsonReading);
        MinecraftForge.EVENT_BUS.addListener(this::onDamage);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
        MinecraftForge.EVENT_BUS.addListener(this::getAttributes);
        MinecraftForge.EVENT_BUS.addListener(this::onHurt);
        MinecraftForge.EVENT_BUS.addListener(this::mobGriefing);
//        EventManager.addListener( this::onSeasonChangeSTD );
//        EventManager.addListener( this::onSeasonChangeTROP );

        DhApiEventRegister.on( DhApiChunkProcessingEvent.class, new DhBlockFixer() );

        ColdCrops.initialize( context,dist );
        if (dist.isClient()) {
            BnbCoreClient.init(modEventBus);
        }
    }



    private void getAttributes(ItemAttributeModifierEvent event) {
        if (ModIntegration.thesilverage.loaded) {
            ItemStack stack = event.getItemStack();
            EquipmentSlot slot = event.getSlotType();
            if (stack.is(BnbItemTags.SILVER_WEAPONS) && slot == EquipmentSlot.MAINHAND) {
                event.addModifier(BNBCoreAttributes.UNDEAD_DAMAGE.get(),BNBCoreAttributes.MAINHAND);
            }
            if (stack.is(BnbItemTags.SILVER_ARMOR) && stack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == slot) {
                event.addModifier(BNBCoreAttributes.UNDEAD_ARMOR.get(), BNBCoreAttributes.ARMOR_MODIFIERS[slot.getIndex()]);
                event.addModifier(BNBCoreAttributes.UNDEAD_ARMOR_TOUGHNESS.get(), BNBCoreAttributes.ARMOR_TOUGHNESS_MODIFIERS[slot.getIndex()]);
            }
        }
    }

    //I just want to prevent mob griefing for creeper and enderman.
    void mobGriefing(EntityMobGriefingEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EnderMan || entity instanceof Creeper) {
            event.setResult(EntityMobGriefingEvent.Result.DENY);
        }
    }

    public static ResourceLocation id(String s) {
        return ResourceLocation.fromNamespaceAndPath(MODID, s);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CuriosApi.registerCurio(Items.BUNDLE, new PouchCurio());
        event.enqueueWork(() -> {
            PacketChannel.register();
        });

        if (ModIntegration.createdeco.loaded) {
            CoinHandler.setup();
        }


    }

    private void commands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        UnitTests.register(dispatcher);
    }

    private void onHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();

        Entity entity = source.getEntity();
        LivingEntity target = event.getEntity();

        if (entity instanceof LivingEntity livingEntity && target.getMobType() == MobType.UNDEAD) {
            double value = livingEntity.getAttributeValue(BNBCoreAttributes.UNDEAD_DAMAGE.get());
            event.setAmount((float) (event.getAmount()+value));
        }

    }

    private void onDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof Player player) {
            ListTag tag = target.getPersistentData().getList(CoinHandler.KILL_CREDIT, CompoundTag.TAG_INT_ARRAY);
            tag.add(NbtUtils.createUUID(player.getUUID()));
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

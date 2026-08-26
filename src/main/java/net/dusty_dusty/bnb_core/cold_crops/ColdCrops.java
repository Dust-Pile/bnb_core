package net.dusty_dusty.bnb_core.cold_crops;

import com.momosoftworks.coldsweat.api.util.Temperature;
import com.momosoftworks.coldsweat.util.world.WorldHelper;
import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.client.BnbCoreClient;
import net.dusty_dusty.bnb_core.cold_crops.data.CropData;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

public class ColdCrops {

    public static void initialize(FMLJavaModLoadingContext context, Dist dist) {

        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(BnbCoreClient::registerTooltip);

        IEventBus ForgeEventBus = MinecraftForge.EVENT_BUS;
        ForgeEventBus.addListener(ColdCrops::onCropGrowth);
        ForgeEventBus.addListener(ColdCrops::onTreeGrowth);
        if (dist.isClient()) {
            ForgeEventBus.addListener(BnbCoreClient::onTooltip);
            ForgeEventBus.addListener(BnbCoreClient::onPlayerLeave);
        }
    }

    public static void onCropGrowth(BlockEvent.CropGrowEvent.Pre event) {
        if (event.getLevel() != null) {
            ResourceLocation blockResLoc = ForgeRegistries.BLOCKS.getKey(event.getState().getBlock());

            onTreeAndPlant((Level) event.getLevel(), blockResLoc ,event.getPos(), event);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static void onTreeGrowth(SaplingGrowTreeEvent event) {
        Level level = (Level) event.getLevel();
        CropsNSeedsData cropsNSeedsData = BnbCore.getCropsAndSeedsData(level);
        if (event.getLevel() != null) {
            ResourceLocation blockResLoc = ForgeRegistries.BLOCKS.getKey(event.getLevel().getBlockState(event.getPos()).getBlock());


            BlockPos blockPos = event.getPos();

            if (cropsNSeedsData.CROPS_MAP.containsKey(blockResLoc)) {
                double temp = WorldHelper.getTemperatureAt(level, blockPos);
                CropData data = cropsNSeedsData.CROPS_MAP.get(blockResLoc);

                if (data.isColder(temp, Temperature.Units.MC)) {
                    event.setResult(Event.Result.DENY);
                }
                data.onCold(temp, Temperature.Units.MC, (resourceLocation ->
                        level.setBlock(blockPos, resourceLocation.defaultBlockState(), 2)
                ));

                if (data.isWarmer(temp, Temperature.Units.MC)) {
                    event.setResult(Event.Result.DENY);
                }
                data.onHot(temp, Temperature.Units.MC, (resourceLocation ->
                        level.setBlock(blockPos, resourceLocation.defaultBlockState(), 2)
                ));
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private static void onTreeAndPlant(Level level, ResourceLocation blockResLoc, BlockPos blockPos, Event event) {
        CropsNSeedsData cropsNSeedsData = BnbCore.getCropsAndSeedsData(level);
        if (cropsNSeedsData.CROPS_MAP.containsKey(blockResLoc)) {
            double temp = WorldHelper.getTemperatureAt(level, blockPos);
            CropData data = cropsNSeedsData.CROPS_MAP.get(blockResLoc);

            double growOdds = data.getGrowOdds(temp, Temperature.Units.MC);
            double randomVal = level.random.nextDouble();
            if (data.isColder(temp, Temperature.Units.MC) || data.isWarmer(temp, Temperature.Units.MC)) {
                growOdds = Math.abs(growOdds)/1.5;
                randomVal *= growOdds + ((level.isNight() ? 0.25 : 1) * (level.isRaining() || level.isThundering() ? 0.25 : 1));
                // More leeway for nighttime and rain

                event.setResult(Event.Result.DENY);
                if (!witherPlant(randomVal, level, blockPos)) {

                    data.onHot(temp, Temperature.Units.MC, (resourceLocation ->
                            level.setBlock(blockPos,resourceLocation.defaultBlockState(), 2)
                    ));
                    data.onCold(temp, Temperature.Units.MC, (resourceLocation ->
                            level.setBlock(blockPos, resourceLocation.defaultBlockState(), 2)
                    ));
                }
            } else if (randomVal > growOdds) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    private static boolean witherPlant(double amount, Level level, BlockPos blockPos) {
        if ( level.random.nextDouble()*15 > amount ) {
            return true;
        }

        BlockState pState = level.getBlockState(blockPos);
        CropBlock block;
        try {
            block = (CropBlock) pState.getBlock();
        } catch (Exception notACrop) {
            return false;
        }

        int age = block.getAge(pState);
        if (age == 0) {
            // age 0 crops are less likely to wither.
            return level.random.nextDouble()*3 > 1;
        }

        level.setBlock(blockPos, block.getStateForAge(age - 1), 2);
        return true;
    }

}

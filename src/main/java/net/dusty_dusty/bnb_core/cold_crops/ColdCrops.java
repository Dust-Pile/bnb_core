package net.dusty_dusty.bnb_core.cold_crops;

import com.mojang.datafixers.util.Either;
import com.momosoftworks.coldsweat.api.util.Temperature;
import com.momosoftworks.coldsweat.util.world.WorldHelper;
import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.cold_crops.data.CropData;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.dusty_dusty.bnb_core.cold_crops.tooltip.ClientTempTooltipComponent;
import net.dusty_dusty.bnb_core.cold_crops.tooltip.TempTooltipComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;

public class ColdCrops {

    public ColdCrops() {}

    public void initialize( FMLJavaModLoadingContext context ) {

        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::registerTooltip);

        IEventBus ForgeEventBus = MinecraftForge.EVENT_BUS;
        ForgeEventBus.addListener(this::onCropGrowth);
        ForgeEventBus.addListener(this::onTreeGrowth);
        ForgeEventBus.addListener(this::onTooltip);
        ForgeEventBus.addListener(this::onPlayerLeave);
    }

    @SuppressWarnings("DataFlowIssue")
    public void onTooltip(RenderTooltipEvent.GatherComponents event) {

        String resLoc = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem()).toString();
        if (CropsNSeedsData.SEEDS_LIST.containsKey(resLoc)) {
            CropData data = CropsNSeedsData.CROPS_MAP.get(CropsNSeedsData.SEEDS_LIST.get(resLoc));
            event.getTooltipElements().add(1, Either.right(new TempTooltipComponent(data)));
        }
    }

    public void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(TempTooltipComponent.class, ClientTempTooltipComponent::new);
    }

    @SuppressWarnings("DataFlowIssue")
    public void onCropGrowth(BlockEvent.CropGrowEvent.Pre event) {
        if (event.getLevel() != null) {
            String blockResLoc = ForgeRegistries.BLOCKS.getKey(event.getState().getBlock()).toString();

            onTreeAndPlant((Level) event.getLevel(), blockResLoc ,event.getPos(), event);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public void onTreeGrowth(SaplingGrowTreeEvent event) {
        if (event.getLevel() != null) {
            String blockResLoc = ForgeRegistries.BLOCKS.getKey(event.getLevel().getBlockState(event.getPos()).getBlock()).toString();

            Level level = (Level) event.getLevel();
            BlockPos blockPos = event.getPos();

            if (CropsNSeedsData.CROPS_MAP.containsKey(blockResLoc)) {
                double temp = WorldHelper.getTemperatureAt(level, blockPos);
                CropData data = CropsNSeedsData.CROPS_MAP.get(blockResLoc);

                if (data.isColder(temp, Temperature.Units.MC)) {
                    event.setResult(Event.Result.DENY);
                }
                data.onCold(temp, Temperature.Units.MC, (resourceLocation ->
                        level.setBlock(blockPos, ForgeRegistries.BLOCKS.getValue(resourceLocation).defaultBlockState(), 2)
                ));

                if (data.isWarmer(temp, Temperature.Units.MC)) {
                    event.setResult(Event.Result.DENY);
                }
                data.onHot(temp, Temperature.Units.MC, (resourceLocation ->
                        level.setBlock(blockPos, ForgeRegistries.BLOCKS.getValue(resourceLocation).defaultBlockState(), 2)
                ));
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private void onTreeAndPlant(Level level, String blockResLoc, BlockPos blockPos, Event event) {
        if (CropsNSeedsData.CROPS_MAP.containsKey(blockResLoc)) {
            double temp = WorldHelper.getTemperatureAt(level, blockPos);
            CropData data = CropsNSeedsData.CROPS_MAP.get(blockResLoc);

            double growOdds = data.getGrowOdds(temp, Temperature.Units.MC);
            double randomVal = level.random.nextDouble();
            if (data.isColder(temp, Temperature.Units.MC) || data.isWarmer(temp, Temperature.Units.MC)) {
                growOdds = Math.abs(growOdds)/1.5;
                randomVal *= growOdds + ((level.isNight() ? 0.25 : 1) * (level.isRaining() || level.isThundering() ? 0.25 : 1));
                // More leeway for nighttime and rain

                event.setResult(Event.Result.DENY);
                if (!witherPlant(randomVal, level, blockPos)) {

                    data.onHot(temp, Temperature.Units.MC, (resourceLocation ->
                            level.setBlock(blockPos, ForgeRegistries.BLOCKS.getValue(resourceLocation).defaultBlockState(), 2)
                    ));
                    data.onCold(temp, Temperature.Units.MC, (resourceLocation ->
                            level.setBlock(blockPos, ForgeRegistries.BLOCKS.getValue(resourceLocation).defaultBlockState(), 2)
                    ));
                }
            } else if (randomVal > growOdds) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    private boolean witherPlant(double amount, Level level, BlockPos blockPos) {
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

    private void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            //It's to save memory but is it really necessary
            BnbCore.LOGGER.info("Clearing unneeded data");
            CropsNSeedsData.CROPS_MAP.clear();
            CropsNSeedsData.SEEDS_LIST.clear();
        }
    }
}

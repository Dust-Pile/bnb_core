package net.dusty_dusty.bnb_core.lod_handling;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiChunkProcessingEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import loaderCommon.forge.com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper;
import loaderCommon.forge.com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

public class DhBlockFixer extends DhApiChunkProcessingEvent {

    private static ServerLevelWrapper overworld;
    private static BlockStateWrapper oakLeaves;
    private static BlockStateWrapper rainbowBlock;

    private static Block floweringOak;
    private static Block rainbowBirchLeaves;

    @Override
    public void blockOrBiomeChangedDuringChunkProcessing(DhApiEventParam<EventParam> dhApiEventParam) {
        EventParam event = dhApiEventParam.value;

        if ( overworld == null ) {
            DhBlockFixer.load();
        }

        BlockState eventBlock;
        if ( event.currentBlock.getWrappedMcObject() == null ) {
            return;
        }

        eventBlock = ( (BlockState) event.currentBlock.getWrappedMcObject() );
        if ( eventBlock.is( floweringOak ) ) {
            event.setBlockOverride( oakLeaves );
        } else if ( eventBlock.is( rainbowBirchLeaves ) ) {
            event.setBlockOverride( rainbowBlock );
        }
    }

    public static void load() {
        overworld = ServerLevelWrapper.getWrapper(
                ServerLifecycleHooks.getCurrentServer().getLevel( ServerLevel.OVERWORLD ) );

        floweringOak = ForgeRegistries.BLOCKS.getValue(
                ResourceLocation.fromNamespaceAndPath( "biomesoplenty", "flowering_oak_leaves" )
        );
        rainbowBirchLeaves = ForgeRegistries.BLOCKS.getValue(
                ResourceLocation.fromNamespaceAndPath( "biomesoplenty", "rainbow_birch_leaves" )
        );

        oakLeaves = DhBlockFixer.wrap( Blocks.OAK_LEAVES.defaultBlockState() );
        rainbowBlock = DhBlockFixer.wrap( Blocks.MAGENTA_TERRACOTTA.defaultBlockState() );
    }

    private static BlockStateWrapper wrap( BlockState state ) {
        return BlockStateWrapper.fromBlockState( state,  overworld );
    }
}
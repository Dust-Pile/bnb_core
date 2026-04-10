package net.dusty_dusty.bnb_core.lod_handling;

import biomesoplenty.api.block.BOPBlocks;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiChunkProcessingEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import loaderCommon.forge.com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper;
import loaderCommon.forge.com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;

public class DhBlockFixer extends DhApiChunkProcessingEvent {

    private static ServerLevelWrapper overworld;
    private static BlockStateWrapper oakLeaves;
    private static BlockStateWrapper blueWool;

    @Override
    public void blockOrBiomeChangedDuringChunkProcessing(DhApiEventParam<EventParam> dhApiEventParam) {
        EventParam event = dhApiEventParam.value;

        if ( overworld == null ) {
            DhBlockFixer.load();
        }

        BlockState eventBlock = ( (BlockState) event.currentBlock.getWrappedMcObject() );
        if ( eventBlock.is( BOPBlocks.FLOWERING_OAK_LEAVES ) ) {
            event.setBlockOverride( oakLeaves );
        } else if ( eventBlock.is( BOPBlocks.RAINBOW_BIRCH_LEAVES ) ) {
            event.setBlockOverride( blueWool );
        }
    }

    public static void load() {
        overworld = ServerLevelWrapper.getWrapper(
                ServerLifecycleHooks.getCurrentServer().getLevel( ServerLevel.OVERWORLD ) );

        oakLeaves = DhBlockFixer.wrap( Blocks.OAK_LEAVES.defaultBlockState() );
        blueWool = DhBlockFixer.wrap(Blocks.BLUE_WOOL.defaultBlockState() );
    }

    private static BlockStateWrapper wrap( BlockState state ) {
        return BlockStateWrapper.fromBlockState( state,  overworld );
    }
}
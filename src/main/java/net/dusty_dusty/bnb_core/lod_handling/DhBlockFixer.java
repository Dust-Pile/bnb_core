package net.dusty_dusty.bnb_core.lod_handling;

import biomesoplenty.api.block.BOPBlocks;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiChunkProcessingEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;

public class DhBlockFixer extends DhApiChunkProcessingEvent {
    IDhApiBlockStateWrapper floweringOakLeaves = ( IDhApiBlockStateWrapper ) BOPBlocks.FLOWERING_OAK_LEAVES;

    @Override
    public void blockOrBiomeChangedDuringChunkProcessing(DhApiEventParam<EventParam> dhApiEventParam) {
        EventParam event = dhApiEventParam.value;

        if ( event.currentBlock.getSerialString().equals( floweringOakLeaves.getSerialString() ) ) {
            event.setBlockOverride( floweringOakLeaves );
        }
    }
}
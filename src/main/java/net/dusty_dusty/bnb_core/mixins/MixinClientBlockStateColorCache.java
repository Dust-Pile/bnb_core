package net.dusty_dusty.bnb_core.mixins;

import com.seibel.distanthorizons.core.logging.DhLogger;
import loaderCommon.forge.com.seibel.distanthorizons.common.wrappers.block.ClientBlockStateColorCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.dusty_dusty.bnb_core.BnbCore;

import java.util.List;

@Mixin( ClientBlockStateColorCache.class )
public abstract class MixinClientBlockStateColorCache {

    @Final
    private static RandomSource RANDOM;

    @Final
    @Shadow( remap = false )
    private BlockState blockState;

    /// Doesn't Work :/
//    @Inject( method = "getQuadsForDirection", at = @At( "RETURN" ), remap = false )
//    private void onGetQuadsForDirection( @Nullable Direction direction, CallbackInfoReturnable<List<BakedQuad>> cir ){
//        if ( this.blockState.getBlock() instanceof SlabBlock ) {
//            BlockState fullBlockState = this.blockState.setValue( SlabBlock.TYPE, SlabType.DOUBLE );
//            cir.setReturnValue(
//                    Minecraft.getInstance().getModelManager().getBlockModelShaper().
//                    getBlockModel( fullBlockState ).getQuads( fullBlockState, direction, RANDOM)
//            );
//        }
//    }

    @Final
    @Shadow( remap = false )
    private static DhLogger LOGGER;

    /**
     * @author Ada Aster
     * @reason Injector not working ;-;
     */
    @Overwrite( remap = false )
    private List<BakedQuad> getQuadsForDirection(@Nullable Direction direction) {
		List<BakedQuad> quads;
        BlockState blockState = this.blockState;

        if (this.blockState.getBlock() instanceof SlabBlock)
        {
            blockState = this.blockState.setValue( SlabBlock.TYPE, SlabType.DOUBLE );
        }
        quads = Minecraft.getInstance().getModelManager().getBlockModelShaper().
                getBlockModel( blockState ).getQuads( blockState, direction, RANDOM);

		return quads;
	}
}

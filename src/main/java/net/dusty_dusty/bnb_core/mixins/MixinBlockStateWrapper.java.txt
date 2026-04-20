package net.dusty_dusty.bnb_core.mixins;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiBlockMaterial;
import loaderCommon.forge.com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( BlockStateWrapper.class )
public abstract class MixinBlockStateWrapper {

    @Final
    public BlockState blockState;
    @Shadow( remap = false )
    private String serialString;

//    @Inject( method = "calculateEDhApiBlockMaterialId", at = @At( "RETURN" ), cancellable = true, remap = false )
//    private void oncalculateEDhApiBlockMaterialId( CallbackInfoReturnable<EDhApiBlockMaterial> cir ){
//        if (this.blockState == null)
//        {
//            return;
//        }
//
//        if ( serialString.contains("grass_slab") ) {
//            cir.setReturnValue( EDhApiBlockMaterial.GRASS );
//        }
//    }

    /**
     * @author Ada Aster
     * @reason Injector not working ;-;
     */
    @Overwrite( remap = false )
    private EDhApiBlockMaterial calculateEDhApiBlockMaterialId()
	{
		if (this.blockState == null)
		{
			return EDhApiBlockMaterial.AIR;
		}


		String serialString = this.getSerialString().toLowerCase();

		if (this.blockState.is(BlockTags.LEAVES)
			|| serialString.contains("bamboo")
			|| serialString.contains("cactus")
			|| serialString.contains("chorus_flower")
			|| serialString.contains("mushroom")
			)
		{
			return EDhApiBlockMaterial.LEAVES;
		}
		else if (this.blockState.is(Blocks.LAVA))
		{
			return EDhApiBlockMaterial.LAVA;
		}
		else if (this.isLiquid() || this.blockState.is(Blocks.WATER))
		{
			return EDhApiBlockMaterial.WATER;
		}
		else if (this.blockState.getSoundType() == SoundType.WOOD
				|| serialString.contains("root")
				|| this.blockState.getSoundType() == SoundType.CHERRY_WOOD

				)
		{
			return EDhApiBlockMaterial.WOOD;
		}
		else if (this.blockState.getSoundType() == SoundType.METAL

				|| this.blockState.getSoundType() == SoundType.COPPER


				)
		{
			return EDhApiBlockMaterial.METAL;
		}

        ///================///
        /// Edited Portion ///
        ///================///

		else if (
                serialString.contains("grass_block")
                || serialString.contains("grass_slab")
                )
		{
			return EDhApiBlockMaterial.GRASS;
		}

        ///====================///
        /// End Edited Portion ///
        ///====================///

		else if (
			serialString.contains("dirt")
			|| serialString.contains("gravel")
			|| serialString.contains("mud")
			|| serialString.contains("podzol")
			|| serialString.contains("mycelium")
			)
		{
			return EDhApiBlockMaterial.DIRT;
		}
		else if (this.blockState.getSoundType() == SoundType.DEEPSLATE
				|| this.blockState.getSoundType() == SoundType.DEEPSLATE_BRICKS
				|| this.blockState.getSoundType() == SoundType.DEEPSLATE_TILES
				|| this.blockState.getSoundType() == SoundType.POLISHED_DEEPSLATE
				|| serialString.contains("deepslate") )
		{
			return EDhApiBlockMaterial.DEEPSLATE;
		}
		else if (this.serialString.contains("snow"))
		{
			return EDhApiBlockMaterial.SNOW;
		}
		else if (serialString.contains("sand"))
		{
			return EDhApiBlockMaterial.SAND;
		}
		else if (serialString.contains("terracotta"))
		{
			return EDhApiBlockMaterial.TERRACOTTA;
		}
		else if (this.blockState.is(BlockTags.BASE_STONE_NETHER))
		{
			return EDhApiBlockMaterial.NETHER_STONE;
		}
		else if (serialString.contains("stone")
				|| serialString.contains("ore"))
		{
			return EDhApiBlockMaterial.STONE;
		}
		else if (this.blockState.getLightEmission() > 0)
		{
			return EDhApiBlockMaterial.ILLUMINATED;
		}
		else
		{
			return EDhApiBlockMaterial.UNKNOWN;
		}
	}

    @Shadow( remap = false )
    public abstract boolean isLiquid();

    @Shadow( remap = false )
    public abstract String getSerialString();

}

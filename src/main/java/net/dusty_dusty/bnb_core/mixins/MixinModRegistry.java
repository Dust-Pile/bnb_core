package net.dusty_dusty.bnb_core.mixins;

import fuzs.metalbundles.init.ModRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin( ModRegistry.class )
public abstract class MixinModRegistry {

    @ModifyConstant( method = "lambda$static$5", constant = @Constant( intValue = 4096 ), remap = false )
    private static int bnb$injectNetheriteCapacity( int value ) {
        return 512;
    }

    @ModifyConstant( method = "lambda$static$4", constant = @Constant( intValue = 2048 ), remap = false )
    private static int bnb$injectDiamondCapacity( int value ) {
        return 384;
    }

    @ModifyConstant( method = "lambda$static$3", constant = @Constant( intValue = 1024 ), remap = false )
    private static int bnb$injectGoldCapacity( int value ) {
        return 256;
    }

    @ModifyConstant( method = "lambda$static$2", constant = @Constant( intValue = 512 ) )
    private static int bnb$injectCopperCapacity( int value ) {
        return 128;
    }

    @ModifyConstant( method = "lambda$static$1", constant = @Constant( intValue = 128 ) )
    private static int bnb$injectIronCapacity( int value ) {
        return 96;
    }
}

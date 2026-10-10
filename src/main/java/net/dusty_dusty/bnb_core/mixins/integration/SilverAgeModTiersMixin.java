package net.dusty_dusty.bnb_core.mixins.integration;

import com.phantomwing.thesilverage.tool.ModTiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ModTiers.class)
public class SilverAgeModTiersMixin {

    @ModifyArg(method = "<clinit>",at = @At(value = "INVOKE", target =
            "Lnet/minecraftforge/common/ForgeTier;<init>(IIFFILnet/minecraft/tags/TagKey;Ljava/util/function/Supplier;)V"),index = 0)
    private static int changeTier(int old) {
        return 3;//diamond mining level (can break obsidian, netherite, etc.)
    }
}

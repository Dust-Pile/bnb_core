package net.dusty_dusty.bnb_core.mixins;

import com.simibubi.create.foundation.item.SmartInventory;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.CastingBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.CastingFluidTank;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Mixin;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingRecipe;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin( CastingBlockEntity.class )
public abstract class MixinCastingRecipe {

    @Shadow( remap = false )
    public SmartInventory moldInv;

    @Shadow( remap = false )
    public CastingFluidTank inputTank;

    @Redirect( method = "updateCasting",
            at = @At( value = "INVOKE", target = "Lfr/lucreeper74/createmetallurgy/content/blocks/casting/recipe/CastingRecipe;getResultItem(Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;" )
    )
    private ItemStack getResultItemProxy(
            CastingRecipe instance,
            RegistryAccess registryAccess
    ) {
        ItemStack resultItem = instance.getResultItem( registryAccess ).copy();
        ItemStack moldItem = this.moldInv.getItem( 0 );

        if ( moldItem.getItem() instanceof TieredItem
                && resultItem.getItem() instanceof TieredItem
                && moldItem.getTag() != null
        ) {
            resultItem.setTag( moldItem.getTag().copy() );
        }

        return resultItem;
    }
}

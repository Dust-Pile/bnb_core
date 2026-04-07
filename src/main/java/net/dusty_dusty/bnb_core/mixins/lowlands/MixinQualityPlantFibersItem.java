package net.dusty_dusty.bnb_core.mixins.lowlands;

import net.mcreator.lowlandsclothing.item.QualityPlantFibersItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin( QualityPlantFibersItem.class )
public abstract class MixinQualityPlantFibersItem extends Item {

    private MixinQualityPlantFibersItem(Properties pProperties) {
        super(pProperties);
    }

    /**
     * @author Ada Aster
     * @reason Lazy
     */
    @Overwrite( remap = false )
    public void m_7373_(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, level, list, flag);
        list.add(Component.literal("Rare Harvest on Grass using a Knife or Common Harvest on Tall Grass."));
    }
}

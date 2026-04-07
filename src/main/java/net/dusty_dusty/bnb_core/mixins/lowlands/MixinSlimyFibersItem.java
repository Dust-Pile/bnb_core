package net.dusty_dusty.bnb_core.mixins.lowlands;

import net.mcreator.lowlandsclothing.item.SlimyFibersItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin( SlimyFibersItem.class )
public abstract class MixinSlimyFibersItem extends Item {

    private MixinSlimyFibersItem(Properties pProperties) {
        super(pProperties);
    }

    /**
     * @author Ada Aster
     * @reason Lazy
     */
    @Overwrite( remap = false )
    public void m_7373_(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, level, list, flag);
        list.add(Component.literal("An Uncommon material harvested on lilypads and seaweeds with a Knife."));
    }
}

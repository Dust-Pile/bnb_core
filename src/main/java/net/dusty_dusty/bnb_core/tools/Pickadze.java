package net.dusty_dusty.bnb_core.tools;

import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;

public class Pickadze extends DiggerItem {
    public Pickadze(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super((float)pAttackDamageModifier, pAttackSpeedModifier, pTier, MineableTags.MINEABLE_WITH_PICKAXE, pProperties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction);
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return pState.is( MineableTags.MINEABLE_WITH_PICKADZE ) ? this.speed -0.5F : 1.0F;
    }
}

package net.dusty_dusty.bnb_core.tools;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings({"unused", "unchecked"})
public class Grubhoe extends DiggerItem {

    protected static final Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLABLES;

    public Grubhoe(Tier pTier, float pAttackDamageModifier, float pAttackSpeedModifier, Item.Properties pProperties) {
        super(pAttackDamageModifier, pAttackSpeedModifier, pTier, MineableTags.MINEABLE_WITH_GRUBHOE, pProperties);
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return pState.is( MineableTags.MINEABLE_WITH_GRUBHOE ) ? this.speed -0.5F : 1.0F;
    }

    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState toolModifiedState = level.getBlockState(blockpos).getToolModifiedState(pContext, ToolActions.HOE_TILL, false);
        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = toolModifiedState == null ? null : Pair.of((Predicate)(ctx) -> true, changeIntoState(toolModifiedState));
        if (pair == null) {
            return InteractionResult.PASS;
        } else {
            Predicate<UseOnContext> predicate = pair.getFirst();
            Consumer<UseOnContext> consumer = pair.getSecond();
            if (predicate.test(pContext)) {
                Player player = pContext.getPlayer();
                level.playSound(player, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!level.isClientSide) {
                    consumer.accept(pContext);
                    if (player != null) {
                        pContext.getItemInHand().hurtAndBreak(1, player, (p_150845_) -> p_150845_.broadcastBreakEvent(pContext.getHand()));
                    }
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public static Consumer<UseOnContext> changeIntoState(BlockState pState) {
        return (p_238241_) -> {
            p_238241_.getLevel().setBlock(p_238241_.getClickedPos(), pState, 11);
            p_238241_.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, p_238241_.getClickedPos(), Context.of(p_238241_.getPlayer(), pState));
        };
    }

    public static Consumer<UseOnContext> changeIntoStateAndDropItem(BlockState pState, ItemLike pItemToDrop) {
        return (p_238246_) -> {
            p_238246_.getLevel().setBlock(p_238246_.getClickedPos(), pState, 11);
            p_238246_.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, p_238246_.getClickedPos(), Context.of(p_238246_.getPlayer(), pState));
            Block.popResourceFromFace(p_238246_.getLevel(), p_238246_.getClickedPos(), p_238246_.getClickedFace(), new ItemStack(pItemToDrop));
        };
    }

    public static boolean onlyIfAirAbove(UseOnContext p_150857_) {
        return p_150857_.getClickedFace() != Direction.DOWN && p_150857_.getLevel().getBlockState(p_150857_.getClickedPos().above()).isAir();
    }

    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_HOE_ACTIONS.contains(toolAction);
    }

    static {
        TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.DIRT_PATH, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.DIRT, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.COARSE_DIRT, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.DIRT.defaultBlockState())), Blocks.ROOTED_DIRT, Pair.of((Predicate)(p_238242_) -> true, changeIntoStateAndDropItem(Blocks.DIRT.defaultBlockState(), Items.HANGING_ROOTS))));
    }
}

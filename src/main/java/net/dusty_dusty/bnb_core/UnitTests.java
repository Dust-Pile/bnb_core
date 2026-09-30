package net.dusty_dusty.bnb_core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dusty_dusty.bnb_core.coins.CoinHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;

public class UnitTests {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(BnbCore.MODID).requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.literal("unit_test")
                        .then(Commands.literal("coin_pouch_0")
                                .executes(UnitTests::coinPouch0)
                        )
                        .then(Commands.literal("coin_pouch_1")
                                .executes(UnitTests::coinPouch1)
                        )
                        .then(Commands.literal("coin_pouch_2")
                                .executes(UnitTests::coinPouch2)
                        )
                )
        );
    }

    //clears player inventory, then equips a pouch and drops 32 brass coins on them
    private static int coinPouch0(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        player.getInventory().clearContent();

        ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).orElseThrow(IllegalStateException::new);

        Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();

        ICurioStacksHandler curioStacksHandler = curios.get("pouch");

        for (int i = 0 ; i < curioStacksHandler.getSlots() ; i++) {
            curioStacksHandler.getStacks().setStackInSlot(i, ItemStack.EMPTY);
        }

        curioStacksHandler.getStacks().setStackInSlot(0, Items.BUNDLE.getDefaultInstance());

        ItemStack brasscoins = CoinHandler.BRASS_COIN.getDefaultInstance();

        brasscoins.setCount(32);

        ItemEntity itemEntity = new ItemEntity(player.level(),player.getX(),player.getY(),player.getZ(),brasscoins);

        player.level().addFreshEntity(itemEntity);

        return 1;
    }

    //clears player inventory, then equips a pouch and drops 35 brass coins on them
    private static int coinPouch1(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        player.getInventory().clearContent();

        ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).orElseThrow(IllegalStateException::new);

        Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();

        ICurioStacksHandler curioStacksHandler = curios.get("pouch");

        for (int i = 0 ; i < curioStacksHandler.getSlots() ; i++) {
            curioStacksHandler.getStacks().setStackInSlot(i, ItemStack.EMPTY);
        }

        curioStacksHandler.getStacks().setStackInSlot(0, Items.BUNDLE.getDefaultInstance());

        ItemStack brasscoins = CoinHandler.BRASS_COIN.getDefaultInstance();

        brasscoins.setCount(35);

        ItemEntity itemEntity = new ItemEntity(player.level(),player.getX(),player.getY(),player.getZ(),brasscoins);

        player.level().addFreshEntity(itemEntity);

        return 1;
    }

    //clears player inventory, then equips a pouch and drops 32 netherite coins on them
    private static int coinPouch2(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        //clear the coin pouch and inventory
        player.getInventory().clearContent();

        ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).orElseThrow(IllegalStateException::new);

        Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();

        ICurioStacksHandler curioStacksHandler = curios.get("pouch");

        for (int i = 0 ; i < curioStacksHandler.getSlots() ; i++) {
            curioStacksHandler.getStacks().setStackInSlot(i, ItemStack.EMPTY);
        }

        curioStacksHandler.getStacks().setStackInSlot(0, Items.BUNDLE.getDefaultInstance());

        ItemStack brasscoins = CoinHandler.NETHERITE_COIN.getDefaultInstance();

        brasscoins.setCount(32);

        ItemEntity itemEntity = new ItemEntity(player.level(),player.getX(),player.getY(),player.getZ(),brasscoins);

        player.level().addFreshEntity(itemEntity);

        return 1;
    }
}

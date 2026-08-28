package net.dusty_dusty.bnb_core;

import fuzs.metalbundles.world.item.MetalBundleItem;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoinHandler {


    public static final Object2IntMap<Item> COIN_EXCHANGE = new Object2IntArrayMap<>();


    public static void onLoot(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();

    }

    /**
     * @param inv      Player Inventory to add the item to
     * @param incoming the itemstack being picked up
     * @return if the item was completely picked up by the dank(s)
     */
    public static boolean interceptItem(Inventory inv, ItemStack incoming) {
        Player player = inv.player;
        if (player.level().isClientSide() || incoming.isEmpty()) {//thanks Hookshot
            return false;
        }

        if (!COIN_EXCHANGE.containsKey(incoming.getItem()) && incoming.getItem() != NETHERITE_COIN) {
            return false;
        }

        LazyOptional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);

        return curiosInventory.map(iCuriosItemHandler -> iCuriosItemHandler.findCurios("pouch"))
                .map(
                list -> {
                    for (SlotResult slotResult : list) {
                        ItemStack possibleBundle = slotResult.stack();
                        if ((possibleBundle.getItem() instanceof BundleItem ||
                                (ModIntegration.metalbundles.loaded && possibleBundle.getItem() instanceof MetalBundleItem)) && onItemPickup(player, incoming, possibleBundle)) {
                            return true;
                        }
                    }
                    return false;
                }
        ).orElse(false);
    }

    public static boolean onItemPickup(Player player, ItemStack pickup, ItemStack pouch) {
        CompoundTag tag = pouch.getOrCreateTag();

        ListTag itemsTag = tag.getList("Items", Tag.TAG_COMPOUND);

        List<ItemStack> itemsList = getBundleItems(itemsTag);

        //just add the item to the bundle
        if (pickup.getItem() == NETHERITE_COIN) {
            int capacity = getCapacity(pouch);
            int bundleItemCount = itemsList.stream().mapToInt(ItemStack::getCount).sum();
            int remaining = capacity - bundleItemCount;
            if (remaining > 0) {
                if (pickup.getCount() <= remaining) {//everything was picked up
                    addItemsToList(itemsList, pickup);
                    pickup.setCount(0);
                } else {
                    pickup.shrink(remaining);
                    addItemsToList(itemsList, pickup);
                }
            }
            setBundleItems(pouch,itemsList);
        } else {
            int stackValue = pickup.getCount() * CoinHandler.COIN_EXCHANGE.getInt(pickup.getItem());

            int bundleCoinValue = itemsList.stream().mapToInt(stack -> stack.getCount() * CoinHandler.COIN_EXCHANGE
                    .getInt(stack.getItem())).sum();

            List<ItemStack> nonCoinItemList = itemsList.stream().filter(stack -> !COIN_EXCHANGE.containsKey(stack.getItem())).toList();

            int nonCoinBundleItems = nonCoinItemList.stream().mapToInt(ItemStack::getCount).sum();

            int maxCoins = getCapacity(pouch) - nonCoinBundleItems;

            int totalCoinValue = bundleCoinValue + stackValue;

            List<ItemStack> mergedCoins = getMergedCoins(totalCoinValue);

            int mergedCoinCount = mergedCoins.stream().mapToInt(ItemStack::getCount).sum();
            if (mergedCoinCount <= maxCoins) {
                pickup.setCount(0);
            } else {
                int leftoverCoins = mergedCoinCount - maxCoins;
                pickup.setCount(leftoverCoins);
                removeItemsFromList(mergedCoins, pickup);
            }
            replaceCoinsInPouch(pouch, nonCoinItemList, mergedCoins);
        }
        return pickup.isEmpty();
    }

    static void removeItemsFromList(List<ItemStack> list,ItemStack stack) {
        int remainder = stack.getCount();
        for (ItemStack listItemStack : list) {
            if (listItemStack.getItem() == stack.getItem()) {
                if (stack.getCount() >= remainder) {
                    listItemStack.shrink(remainder);
                    remainder = 0;
                } else {
                    stack.setCount(0);
                    remainder -= listItemStack.getCount();
                }
                if (remainder == 0) {break;}
            }
        }
    }

    static void addItemsToList(List<ItemStack> list,ItemStack stack) {
        int remainder = stack.getCount();
        for (ItemStack listItemStack : list) {
            if (listItemStack.getItem() == stack.getItem()) {
                listItemStack.grow(remainder);
                return;
            }
        }
        list.add(stack.copy());
    }

    static int getCapacity(ItemStack stack) {
        if (stack.getItem() instanceof BundleItem) {
            return 64;
        }

        if (ModIntegration.metalbundles.loaded && stack.getItem() instanceof MetalBundleItem metalBundleItem) {
        return metalBundleItem.capacity;
        }
        return 0;
    }

    //replace all coin items, leave non-coin items alone
    static void replaceCoinsInPouch(ItemStack bundle,List<ItemStack> nonCoins,List<ItemStack> coins) {
        List<ItemStack> items = new ArrayList<>(nonCoins);
        items.addAll(coins);
        setBundleItems(bundle,items);
    }

    static void setBundleItems(ItemStack bundle, List<ItemStack> items) {
        ListTag itemsTag = new ListTag();

        for (ItemStack item : items) {
            if (item.isEmpty()) continue;
            itemsTag.add(item.save(new CompoundTag()));
        }

        bundle.getOrCreateTag().put("Items", itemsTag);
    }

    static List<ItemStack> getMergedCoins(int totalCoinValue) {
        int remainder = totalCoinValue;
        List<ItemStack> itemsList = new ArrayList<>();
        while (remainder > 0) {
            for (Map.Entry<Item, Integer> entry : COIN_EXCHANGE.entrySet()) {
                int count = remainder / entry.getValue();
                if (count > 0) {
                    itemsList.add(new ItemStack(entry.getKey(), count));
                }
                remainder = remainder % entry.getValue();
            }
        }
        return itemsList;
    }

    private static ArrayList<ItemStack> getBundleItems(@Nullable ListTag pStack) {
        if (pStack == null || pStack.isEmpty()) {return new ArrayList<>();}
        return pStack.stream().map(CompoundTag.class::cast).map(ItemStack::of)
                .collect(ArrayList::new, List::add, List::addAll);//make sure the list is mutable
    }

    //    'createdeco:copper_coin': NaN,
    //    'createdeco:zinc_coin': 4,
    //    'createdeco:brass_coin': 4,
    //    'createdeco:iron_coin': 6, //Is actually silver
    //    'createdeco:gold_coin': 8

    public static Item NETHERITE_COIN;

    public static void setup() {
        Registry<Item> registry = BuiltInRegistries.ITEM;

        NETHERITE_COIN = registry.get(ModIntegration.createdeco.id("netherite_coin"));

        CoinHandler.COIN_EXCHANGE.put(registry.get(ModIntegration.createdeco.id("gold_coin")),4*4*6*8);
        CoinHandler.COIN_EXCHANGE.put(registry.get(ModIntegration.createdeco.id("iron_coin")),4*4*6);
        CoinHandler.COIN_EXCHANGE.put(registry.get(ModIntegration.createdeco.id("brass_coin")),4*4);
        CoinHandler.COIN_EXCHANGE.put(registry.get(ModIntegration.createdeco.id("zinc_coin")),4);
        CoinHandler.COIN_EXCHANGE.put(registry.get(ModIntegration.createdeco.id("copper_coin")),1);

        MinecraftForge.EVENT_BUS.addListener(CoinHandler::onLoot);
    }
}

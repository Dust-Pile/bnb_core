package net.dusty_dusty.bnb_core.coins;

import com.mojang.datafixers.util.Pair;
import fuzs.metalbundles.world.item.MetalBundleItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.ModIntegration;
import net.dusty_dusty.bnb_core.coins.trades.*;
import net.dusty_dusty.bnb_core.cold_crops.data.CoinData;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.*;

public class CoinHandler {


    public static final Object2IntMap<Item> COIN_EXCHANGE = new Object2IntArrayMap<>();
    public static final String KILL_CREDIT = "bnbcore:kill_credit";

    public static void onLoot(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        Collection<ItemEntity> drops = event.getDrops();
        List<ServerPlayer> killCredit = getKillCredits(entity);
        for (CoinData coinData : BnbCore.getCoinDrops(entity.level()).getData().values()) {
            if (entity.getType().is(coinData.valid())) {
                double x = entity.getX();
                double y = entity.getY();
                double z = entity.getZ();
                if (coinData.netheriteCoin()) {
                    drops.add(new ItemEntity(entity.level(),x,y,z,NETHERITE_COIN.getDefaultInstance()));
                }
                int copperCoinCount = (int) (coinData.coinValue().sample(entity.getRandom()) * (1 + killCredit.size()/2f));
                if (copperCoinCount > 0) {
                    if (killCredit.size() >= 2) {
                        int splitCoinCount = copperCoinCount / killCredit.size();
                        List<ItemStack> coins = getMergedCoins(splitCoinCount);
                        for (ServerPlayer player : killCredit) {
                            for (ItemStack coin : coins) {
                                if (!player.addItem(coin)) {//try giving coin directly to player first
                                    drops.add(new ItemEntity(entity.level(), x, y, z, coin));
                                }
                            }
                        }
                    } else {
                        List<ItemStack> coins = getMergedCoins(copperCoinCount);
                        for (ItemStack coin : coins) {
                            drops.add(new ItemEntity(entity.level(), x, y, z, coin));
                        }
                    }
                }
            }
        }
    }

    static void modifyTrades(VillagerTradesEvent event) {
        long begin = System.nanoTime();
        //replace emerald trades with coins, default silver (iron)
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        VillagerProfession type = event.getType();
        for (Map.Entry<Integer, List<VillagerTrades.ItemListing>> entry : trades.entrySet()) {
            List<VillagerTrades.ItemListing> replacements = new ArrayList<>();
            for (Iterator<VillagerTrades.ItemListing> iterator = entry.getValue().iterator(); iterator.hasNext(); ) {
                VillagerTrades.ItemListing listing = iterator.next();
                if (listing instanceof VillagerTrades.DyedArmorForEmeralds dyedArmorForEmeralds) {
                    iterator.remove();
                    replacements.add(DyedArmorForCoins.create(dyedArmorForEmeralds));
                } else if (listing instanceof VillagerTrades.ItemsForEmeralds itemsForEmeralds) {
                    iterator.remove();
                    replacements.add(CoinsForItems.itemsForCoins(itemsForEmeralds));
                } else if (listing instanceof VillagerTrades.EmeraldForItems emeraldForItems) {
                    iterator.remove();
                    replacements.add(CoinsForItems.coinsForItems(emeraldForItems.item,IRON_COIN,emeraldForItems.cost,emeraldForItems.maxUses,
                            emeraldForItems.villagerXp));
                } else if (listing instanceof VillagerTrades.EmeraldsForVillagerTypeItem emeraldsForVillagerTypeItem) {
                    iterator.remove();
                    replacements.add(CoinsForVillagerTypeItem.create(emeraldsForVillagerTypeItem));

                } else if (listing instanceof VillagerTrades.EnchantBookForEmeralds enchantBookForEmeralds) {
                    iterator.remove();
                    replacements.add(EnchantBookForCoins.enchantBookForCoins(enchantBookForEmeralds));
                } else if (listing instanceof VillagerTrades.EnchantedItemForEmeralds enchantedItemForEmeralds) {
                    iterator.remove();
                    replacements.add(EnchantedItemForCoins.create(enchantedItemForEmeralds));
                } else if (listing instanceof VillagerTrades.ItemsAndEmeraldsToItems itemsAndEmeraldsToItems) {
                    iterator.remove();
                    replacements.add(CoinsForItems.itemsAndCoinsToItems(itemsAndEmeraldsToItems));
                } else if (listing instanceof VillagerTrades.TippedArrowForItemsAndEmeralds tippedArrowForItemsAndEmeralds) {
                    iterator.remove();
                    replacements.add(TippedArrowForItemsAndCoins.create(tippedArrowForItemsAndEmeralds));
                } else if (listing instanceof VillagerTrades.TreasureMapForEmeralds treasureMapForEmeralds) {
                    iterator.remove();
                    replacements.add(TreasureMapForCoins.create(treasureMapForEmeralds));
                } else if (!(listing instanceof CoinTrade)) {
                    BnbCore.LOGGER.warn("Unknown VillagerTrades type: " + listing.getClass().getName());
                }
            }
            entry.getValue().addAll(replacements);
        }
        long end = System.nanoTime();
        BnbCore.LOGGER.info((end - begin) / 1_000_000d + "ms");
    }

    static List<ServerPlayer> getKillCredits(LivingEntity entity) {
        CompoundTag tag = entity.getPersistentData();
        ListTag listTag = tag.getList(KILL_CREDIT, Tag.TAG_STRING);
        List<ServerPlayer> list = new ArrayList<>();
        MinecraftServer server = entity.getServer();
        for (Tag t : listTag) {
            String s = t.toString();
            UUID uuid = UUID.fromString(s);
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                list.add(player);
            }
        }
        return list;
    }

    /**
     * @param inv      Player Inventory to add the item to
     * @param incoming the itemstack being picked up
     * @return if the item was completely picked up by the pouch(es)
     */
    public static boolean interceptItem(Inventory inv, ItemStack incoming) {
        Player player = inv.player;
        if (player.level().isClientSide() || incoming.isEmpty()) {//thanks Hookshot
            return false;
        }

        if (!COIN_EXCHANGE.containsKey(incoming.getItem()) && !incoming.is(NETHERITE_COIN) && !incoming.is(NETHERITE_COINSTACK)) {
            return false;
        }

        LazyOptional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);

        return curiosInventory.map(iCuriosItemHandler -> iCuriosItemHandler.findCurios("pouch"))
                .map(
                list -> {
                    for (SlotResult slotResult : list) {
                        ItemStack possibleBundle = slotResult.stack();
                        if ((possibleBundle.getItem() instanceof BundleItem ||
                                (ModIntegration.metalbundles.loaded && possibleBundle.getItem() instanceof MetalBundleItem))
                                && onItemPickup(player, incoming, possibleBundle)) {
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

        List<ItemStack> bundleItems = getBundleItems(itemsTag);
        List<ItemStack> newItemList = new ArrayList<>();
        int capacity = getBundleCapacity(pouch);
        int stackValue;

        //just add the item to the bundle
        if (pickup.is(NETHERITE_COIN) || pickup.is(NETHERITE_COINSTACK)) {
            stackValue = pickup.getCount() * (pickup.is(NETHERITE_COINSTACK) ? 4 : 1);

            int bundleNetheriteCoinCount = countNetheriteCoins(bundleItems);

            newItemList.addAll(bundleItems.stream().filter(stack -> !(pickup.is(NETHERITE_COIN) || pickup.is(NETHERITE_COINSTACK)))
                    .toList());

            int maxNetheriteCoins = capacity - getItemWeight(newItemList);
            if (maxNetheriteCoins > 0) {
                List<ItemStack> mergedNetheriteCoins = getMergedNetheriteCoins(stackValue+bundleNetheriteCoinCount);
                int mergedNetheriteCoinCount = getItemWeight(mergedNetheriteCoins);
                if (mergedNetheriteCoinCount <= maxNetheriteCoins) {//everything was picked up
                    addItemsToList(newItemList, mergedNetheriteCoins);
                    pickup.setCount(0);
                } else {//some remain outside the bundle
                    pickup.shrink(maxNetheriteCoins);//set to number of coins that can't be picked up
                    removeItemFromList(mergedNetheriteCoins,pickup);//avoid duplication glitch
                    addItemsToList(newItemList, mergedNetheriteCoins);
                }
                setBundleItems(pouch,newItemList);
            }
        } else {
            stackValue = pickup.getCount() * CoinHandler.COIN_EXCHANGE.getInt(pickup.getItem());

            int bundleCoinValue = bundleItems.stream().mapToInt(stack -> stack.getCount() * CoinHandler.COIN_EXCHANGE
                    .getInt(stack.getItem())).sum();

            newItemList.addAll(bundleItems.stream().filter(stack -> !COIN_EXCHANGE.containsKey(stack.getItem())).toList());
            int maxCoins = capacity - getItemWeight(newItemList);
            if (maxCoins > 0) {
                int totalCoinValue = bundleCoinValue + stackValue;
                List<ItemStack> mergedCoins = getMergedCoins(totalCoinValue);
                int mergedCoinCount = getItemWeight(mergedCoins);
                if (mergedCoinCount <= maxCoins) {
                    addItemsToList(newItemList, mergedCoins);
                    pickup.setCount(0);
                } else {
                    pickup.shrink(maxCoins);
                    removeItemFromList(mergedCoins, pickup);
                    addItemsToList(newItemList,mergedCoins);
                }
                setBundleItems(pouch,newItemList);
            }
        }
        return pickup.isEmpty();
    }

    static int countNetheriteCoins(List<ItemStack> items) {
        return items.stream().filter(stack -> stack.is(NETHERITE_COINSTACK) || stack.is(NETHERITE_COINSTACK))
                .mapToInt(stack -> stack.is(NETHERITE_COINSTACK) ? 4 : 1).sum();
    }

    static void removeItemFromList(List<ItemStack> list, ItemStack stack) {
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

    static void addItemToList(List<ItemStack> list, ItemStack stack) {
        int remainder = stack.getCount();
        for (ItemStack listItemStack : list) {
            if (listItemStack.getItem() == stack.getItem()) {
                listItemStack.grow(remainder);
                return;
            }
        }
        list.add(stack.copy());
    }

    static void addItemsToList(List<ItemStack> list, List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            addItemToList(list, stack);
        }
    }

    static int getBundleCapacity(ItemStack stack) {
        if (stack.getItem() instanceof BundleItem) {
            return 64;
        }

        if (ModIntegration.metalbundles.loaded && stack.getItem() instanceof MetalBundleItem metalBundleItem) {
        return metalBundleItem.capacity;
        }
        return 0;
    }

    //consider supporting stacks other than 64?
    static int getItemWeight(List<ItemStack> stacks) {
        return stacks.stream().mapToInt(ItemStack::getCount).sum();
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
        List<ItemStack> coins = new ArrayList<>();
        while (remainder > 0) {
            for (int i = CONVERSIONS.size()-1; i >= 0; i--) {
                Item item = CONVERSIONS.get(i).coin;
                int coinValue = COIN_EXCHANGE.getInt(item);
                int count = remainder / coinValue;
                if (count > 0) {
                    coins.add(new ItemStack(item, count));
                }
                remainder = remainder % coinValue;
            }
        }

        List<ItemStack> coinStacks = new ArrayList<>();
        //merge into stacks if possible
        for (int i = 0; i < coins.size(); i++) {
            ItemStack stack = coins.get(i);
            if (stack.getCount() >=4) {
                int stackCount = stack.getCount()/4;
                coinStacks.add(new ItemStack(getStack(stack.getItem()),stackCount));
                stack.shrink(stackCount * 4);
            }
        }

        coins.addAll(coinStacks);


        return coins;
    }

    public static Item getStack(Item coin) {
        for (CoinValue coinValue : CONVERSIONS) {
            if (coinValue.coin == coin) {
                return coinValue.coinstack;
            }
        }
        throw new IllegalArgumentException("Cannot get stack for coin " + coin);//should never happen
    }


    static List<ItemStack> getMergedNetheriteCoins(int totalCoinValue) {
        List<ItemStack> itemsList = new ArrayList<>();
        int coinStacks = totalCoinValue / 4;
        int coins = totalCoinValue % 4;
        if (coins > 0) {
            itemsList.add(new ItemStack(NETHERITE_COIN,coins));
        }
        if (coinStacks > 0) {
            itemsList.add(new ItemStack(NETHERITE_COINSTACK,coinStacks));
        }
        return itemsList;
    }

    private static List<ItemStack> getBundleItems(@Nullable ListTag pStack) {
        if (pStack == null || pStack.isEmpty()) {return new ArrayList<>();}
        return pStack.stream().map(CompoundTag.class::cast).map(ItemStack::of)
                .toList();
    }



    //    'createdeco:copper_coin': NaN,
    //    'createdeco:zinc_coin': 4,
    //    'createdeco:brass_coin': 4,
    //    'createdeco:iron_coin': 6, //Is actually silver
    //    'createdeco:gold_coin': 8

    public static Item NETHERITE_COIN;
    public static Item COPPER_COIN;
    public static Item ZINC_COIN;
    public static Item BRASS_COIN;
    public static Item IRON_COIN;
    public static Item GOLD_COIN;

    public static Item NETHERITE_COINSTACK;
    public static Item COPPER_COINSTACK;
    public static Item ZINC_COINSTACK;
    public static Item BRASS_COINSTACK;
    public static Item IRON_COINSTACK;
    public static Item GOLD_COINSTACK;

    public static final int COPPER_PER_ZINC = 3;
    public static final int ZINC_PER_BRASS = 3;
    public static final int BRASS_PER_IRON = 6;
    public static final int IRON_PER_GOLD = 8;

    public static final List<CoinValue> CONVERSIONS = new ArrayList<>();

    public static void setup() {

        populate();

        int mult = 1;
        for (int i = 0; i < CONVERSIONS.size(); i++) {
            CoinValue pair = CONVERSIONS.get(i);
            mult *= pair.value;
            COIN_EXCHANGE.put(pair.coin, mult);
            COIN_EXCHANGE.put(pair.coinstack,mult * 4);
        }

        MinecraftForge.EVENT_BUS.addListener(CoinHandler::onLoot);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, CoinHandler::modifyTrades);
    }

    public record CoinValue(Item coin,Item coinstack,int value) {
        int stackValue() {
           return value * 4;
        }

        int coinValue() {
            return value;
        }
    }

    public static void populate() {
        Registry<Item> registry = BuiltInRegistries.ITEM;

        NETHERITE_COIN = registry.get(ModIntegration.createdeco.id("netherite_coin"));

        COPPER_COIN = registry.get(ModIntegration.createdeco.id("copper_coin"));
        ZINC_COIN = registry.get(ModIntegration.createdeco.id("zinc_coin"));
        BRASS_COIN = registry.get(ModIntegration.createdeco.id("brass_coin"));
        IRON_COIN = registry.get(ModIntegration.createdeco.id("iron_coin"));
        GOLD_COIN = registry.get(ModIntegration.createdeco.id("gold_coin"));

        NETHERITE_COINSTACK = registry.get(ModIntegration.createdeco.id("netherite_coinstack"));
        COPPER_COINSTACK = registry.get(ModIntegration.createdeco.id("copper_coinstack"));
        ZINC_COINSTACK = registry.get(ModIntegration.createdeco.id("zinc_coinstack"));
        BRASS_COINSTACK = registry.get(ModIntegration.createdeco.id("brass_coinstack"));
        IRON_COINSTACK = registry.get(ModIntegration.createdeco.id("iron_coinstack"));
        GOLD_COINSTACK = registry.get(ModIntegration.createdeco.id("gold_coinstack"));

        CONVERSIONS.add(new CoinValue(COPPER_COIN,COPPER_COINSTACK,1));
        CONVERSIONS.add(new CoinValue(ZINC_COIN,ZINC_COINSTACK,COPPER_PER_ZINC));
        CONVERSIONS.add(new CoinValue(BRASS_COIN,BRASS_COINSTACK,ZINC_PER_BRASS));
        CONVERSIONS.add(new CoinValue(IRON_COIN,IRON_COINSTACK,BRASS_PER_IRON));
        CONVERSIONS.add(new CoinValue(GOLD_COIN,GOLD_COINSTACK,IRON_PER_GOLD));
    }
}

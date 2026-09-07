package net.dusty_dusty.bnb_core.coins;

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
    public static Item COPPER_COIN;

    public static Item IRON_COIN;

    public static void setup() {
        Registry<Item> registry = BuiltInRegistries.ITEM;

        NETHERITE_COIN = registry.get(ModIntegration.createdeco.id("netherite_coin"));

        COPPER_COIN = registry.get(ModIntegration.createdeco.id("copper_coin"));
        IRON_COIN = registry.get(ModIntegration.createdeco.id("iron_coin"));

        CoinHandler.COIN_EXCHANGE.put(registry.get(ModIntegration.createdeco.id("gold_coin")),4*4*6*8);
        CoinHandler.COIN_EXCHANGE.put(IRON_COIN,4*4*6);
        CoinHandler.COIN_EXCHANGE.put(registry.get(ModIntegration.createdeco.id("brass_coin")),4*4);
        CoinHandler.COIN_EXCHANGE.put(registry.get(ModIntegration.createdeco.id("zinc_coin")),4);
        CoinHandler.COIN_EXCHANGE.put(COPPER_COIN,1);

        MinecraftForge.EVENT_BUS.addListener(CoinHandler::onLoot);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, CoinHandler::modifyTrades);
    }
}

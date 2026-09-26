package net.dusty_dusty.bnb_core.coins.trades;

import net.dusty_dusty.bnb_core.coins.CoinHandler;
import net.dusty_dusty.bnb_core.coins.CoinTrade;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

//gives a coin in exchange for items
public class CoinsForItems implements VillagerTrades.ItemListing, CoinTrade {

    private final ItemStack buying;
    private final ItemStack buyingB;
    private final ItemStack selling;
    private final int maxUses;
    private final int villagerXp;
    private final float priceMultiplier;

    public CoinsForItems(ItemStack buying, ItemStack buyingB, ItemStack selling, int pMaxUses, int pVillagerXp) {
        this.buying = buying;
        this.buyingB = buyingB;
        this.selling = selling;
        this.maxUses = pMaxUses;
        this.villagerXp = pVillagerXp;
        this.priceMultiplier = 0.05F;
    }

    public static CoinsForItems coinsForItems(ItemLike buying, ItemLike selling, int buyingAmount, int pMaxUses, int pVillagerXp) {
        return new CoinsForItems(new ItemStack(buying,buyingAmount), ItemStack.EMPTY, new ItemStack(selling), pMaxUses, pVillagerXp);
    }

    public static CoinsForItems itemsForCoins(VillagerTrades.ItemsForEmeralds itemsForEmeralds) {
        ItemStack coin = new ItemStack(CoinHandler.IRON_COIN, itemsForEmeralds.numberOfItems);
        //itemstack is what's being sold
        return new CoinsForItems(coin, ItemStack.EMPTY, itemsForEmeralds.itemStack,
                itemsForEmeralds.maxUses, itemsForEmeralds.villagerXp);
    }

    public static CoinsForItems itemsAndCoinsToItems(VillagerTrades.ItemsAndEmeraldsToItems itemsAndEmeraldsToItems) {

        return new CoinsForItems(new ItemStack(CoinHandler.IRON_COIN, itemsAndEmeraldsToItems.emeraldCost),
                new ItemStack(itemsAndEmeraldsToItems.fromItem.getItem(), itemsAndEmeraldsToItems.fromCount),
                new ItemStack(itemsAndEmeraldsToItems.toItem.getItem(), itemsAndEmeraldsToItems.toCount), itemsAndEmeraldsToItems.maxUses,
                itemsAndEmeraldsToItems.villagerXp);//, itemsAndEmeraldsToItems.priceMultiplier);

    }

    @Override
    public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
        return new MerchantOffer(buying, selling, this.maxUses, this.villagerXp, this.priceMultiplier);
    }
}

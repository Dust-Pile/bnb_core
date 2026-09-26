package net.dusty_dusty.bnb_core.coins.trades;

import net.dusty_dusty.bnb_core.coins.CoinHandler;
import net.dusty_dusty.bnb_core.coins.CoinTrade;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;
import java.util.stream.Collectors;

public class TippedArrowForItemsAndCoins implements VillagerTrades.ItemListing, CoinTrade {
    /** An ItemStack that can have potion effects written to it. */
    private final ItemStack toItem;
    private final int toCount;
    private final int emeraldCost;
    private final int maxUses;
    private final int villagerXp;
    private final Item fromItem;
    private final int fromCount;
    private final float priceMultiplier;

    public TippedArrowForItemsAndCoins(Item pFromItem, int pFromCount, Item pToItem, int pToCount, int pEmeraldCost, int pMaxUses, int pVillagerXp) {
        this.toItem = new ItemStack(pToItem);
        this.emeraldCost = pEmeraldCost;
        this.maxUses = pMaxUses;
        this.villagerXp = pVillagerXp;
        this.fromItem = pFromItem;
        this.fromCount = pFromCount;
        this.toCount = pToCount;
        this.priceMultiplier = 0.05F;
    }

    public static TippedArrowForItemsAndCoins create(VillagerTrades.TippedArrowForItemsAndEmeralds emeralds) {
        return new TippedArrowForItemsAndCoins(emeralds.fromItem,emeralds.fromCount,emeralds.toItem.getItem(),emeralds.toCount, emeralds.emeraldCost,
                emeralds.maxUses, emeralds.villagerXp);
    }

    public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
        ItemStack itemstack = new ItemStack(CoinHandler.IRON_COIN, this.emeraldCost);
        List<Potion> list = BuiltInRegistries.POTION.stream().filter((p_35804_) ->
                !p_35804_.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(p_35804_)).collect(Collectors.toList());
        Potion potion = list.get(pRandom.nextInt(list.size()));
        ItemStack itemstack1 = PotionUtils.setPotion(new ItemStack(this.toItem.getItem(), this.toCount), potion);
        return new MerchantOffer(itemstack, new ItemStack(this.fromItem, this.fromCount), itemstack1, this.maxUses, this.villagerXp, this.priceMultiplier);
    }
}

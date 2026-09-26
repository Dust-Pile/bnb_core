package net.dusty_dusty.bnb_core.coins.trades;

import net.dusty_dusty.bnb_core.coins.CoinHandler;
import net.dusty_dusty.bnb_core.coins.CoinTrade;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;

public class EnchantedItemForCoins implements VillagerTrades.ItemListing, CoinTrade {
    private final ItemStack itemStack;
    private final int baseEmeraldCost;
    private final int maxUses;
    private final int villagerXp;
    private final float priceMultiplier;

    public EnchantedItemForCoins(Item pItem, int pBaseEmeraldCost, int pMaxUses, int pVillagerXp) {
        this(pItem, pBaseEmeraldCost, pMaxUses, pVillagerXp, 0.05F);
    }

    public EnchantedItemForCoins(Item pItem, int pBaseEmeraldCost, int pMaxUses, int pVillagerXp, float pPriceMultiplier) {
        this.itemStack = new ItemStack(pItem);
        this.baseEmeraldCost = pBaseEmeraldCost;
        this.maxUses = pMaxUses;
        this.villagerXp = pVillagerXp;
        this.priceMultiplier = pPriceMultiplier;
    }

    public static EnchantedItemForCoins create(VillagerTrades.EnchantedItemForEmeralds enchantedItemForEmeralds) {
        return new EnchantedItemForCoins(enchantedItemForEmeralds.itemStack.getItem(), enchantedItemForEmeralds.baseEmeraldCost,
                enchantedItemForEmeralds.maxUses,enchantedItemForEmeralds.villagerXp, enchantedItemForEmeralds.priceMultiplier);
    }

    public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
        int i = 5 + pRandom.nextInt(15);
        ItemStack itemstack = EnchantmentHelper.enchantItem(pRandom, new ItemStack(this.itemStack.getItem()), i, false);
        int j = Math.min(this.baseEmeraldCost + i, 64);
        ItemStack itemstack1 = new ItemStack(CoinHandler.IRON_COIN, j);
        return new MerchantOffer(itemstack1, itemstack, this.maxUses, this.villagerXp, this.priceMultiplier);
    }
}

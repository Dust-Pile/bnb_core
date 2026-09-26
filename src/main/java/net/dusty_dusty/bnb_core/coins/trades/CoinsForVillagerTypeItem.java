package net.dusty_dusty.bnb_core.coins.trades;

import net.dusty_dusty.bnb_core.coins.CoinHandler;
import net.dusty_dusty.bnb_core.coins.CoinTrade;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

import javax.annotation.Nullable;
import java.util.Map;

public class CoinsForVillagerTypeItem implements VillagerTrades.ItemListing, CoinTrade {
    private final Map<VillagerType, Item> trades;
    private final int cost;
    private final int maxUses;
    private final int villagerXp;

    public CoinsForVillagerTypeItem(int pCost, int pMaxUses, int pVillagerXp, Map<VillagerType, Item> pTrades) {
        this.trades = pTrades;
        this.cost = pCost;
        this.maxUses = pMaxUses;
        this.villagerXp = pVillagerXp;
    }

    public static CoinsForVillagerTypeItem create(VillagerTrades.EmeraldsForVillagerTypeItem emeraldsForVillagerTypeItem) {
        return new CoinsForVillagerTypeItem(emeraldsForVillagerTypeItem.cost, emeraldsForVillagerTypeItem.maxUses, emeraldsForVillagerTypeItem.villagerXp, emeraldsForVillagerTypeItem.trades);
    }

    @Nullable
    public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
        if (pTrader instanceof VillagerDataHolder) {
            Item item = this.trades.get(((VillagerDataHolder) pTrader).getVillagerData().getType());
            if (item == null)
                return null; // FORGE: Account for modded villager types by returning null if there is no trade
            ItemStack itemstack = new ItemStack(item, this.cost);
            return new MerchantOffer(itemstack, new ItemStack(CoinHandler.IRON_COIN), this.maxUses, this.villagerXp, 0.05F);
        } else {
            return null;
        }
    }
}

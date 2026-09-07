package net.dusty_dusty.bnb_core.coins.trades;

import com.google.common.collect.Lists;
import net.dusty_dusty.bnb_core.coins.CoinHandler;
import net.dusty_dusty.bnb_core.coins.CoinTrade;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;

public class DyedArmorForCoins implements VillagerTrades.ItemListing, CoinTrade {
    private final Item item;
    private final int value;
    private final int maxUses;
    private final int villagerXp;

    public DyedArmorForCoins(Item pItem, int pValue) {
        this(pItem, pValue, 12, 1);
    }

    public DyedArmorForCoins(Item pItem, int pValue, int pMaxUses, int pVillagerXp) {
        this.item = pItem;
        this.value = pValue;
        this.maxUses = pMaxUses;
        this.villagerXp = pVillagerXp;
    }

    public static DyedArmorForCoins create(VillagerTrades.DyedArmorForEmeralds dyedArmorForEmeralds) {
        return new DyedArmorForCoins(dyedArmorForEmeralds.item,dyedArmorForEmeralds.value,dyedArmorForEmeralds.maxUses,dyedArmorForEmeralds.villagerXp);
    }

    public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
        ItemStack itemstack = new ItemStack(CoinHandler.IRON_COIN, this.value);
        ItemStack itemstack1 = new ItemStack(this.item);
        if (this.item instanceof DyeableArmorItem) {
            List<DyeItem> list = Lists.newArrayList();
            list.add(getRandomDye(pRandom));
            if (pRandom.nextFloat() > 0.7F) {
                list.add(getRandomDye(pRandom));
            }

            if (pRandom.nextFloat() > 0.8F) {
                list.add(getRandomDye(pRandom));
            }

            itemstack1 = DyeableLeatherItem.dyeArmor(itemstack1, list);
        }

        return new MerchantOffer(itemstack, itemstack1, this.maxUses, this.villagerXp, 0.2F);
    }

    private static DyeItem getRandomDye(RandomSource pRandom) {
        return DyeItem.byColor(DyeColor.byId(pRandom.nextInt(16)));
    }
}

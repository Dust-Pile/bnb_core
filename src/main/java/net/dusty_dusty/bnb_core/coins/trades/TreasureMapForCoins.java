package net.dusty_dusty.bnb_core.coins.trades;

import net.dusty_dusty.bnb_core.coins.CoinHandler;
import net.dusty_dusty.bnb_core.coins.CoinTrade;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;

public class TreasureMapForCoins implements CoinTrade, VillagerTrades.ItemListing {
    public final int emeraldCost;
    public final TagKey<Structure> destination;
    public final String displayName;
    public final MapDecoration.Type destinationType;
    public final int maxUses;
    public final int villagerXp;

    public TreasureMapForCoins(int pEmeraldCost, TagKey<Structure> pDestination, String pDisplayName, MapDecoration.Type pDestinationType, int pMaxUses, int pVillagerXp) {
        this.emeraldCost = pEmeraldCost;
        this.destination = pDestination;
        this.displayName = pDisplayName;
        this.destinationType = pDestinationType;
        this.maxUses = pMaxUses;
        this.villagerXp = pVillagerXp;
    }

    public static TreasureMapForCoins create(VillagerTrades.TreasureMapForEmeralds pEmeralds) {
        return new TreasureMapForCoins(pEmeralds.emeraldCost, pEmeralds.destination, pEmeralds.displayName, pEmeralds.destinationType,pEmeralds.maxUses, pEmeralds.villagerXp);
    }

    @Nullable
    public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
        if (!(pTrader.level() instanceof ServerLevel)) {
            return null;
        } else {
            ServerLevel serverlevel = (ServerLevel)pTrader.level();
            BlockPos blockpos = serverlevel.findNearestMapStructure(this.destination, pTrader.blockPosition(), 100, true);
            if (blockpos != null) {
                ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
                MapItem.renderBiomePreviewMap(serverlevel, itemstack);
                MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", this.destinationType);
                itemstack.setHoverName(Component.translatable(this.displayName));
                return new MerchantOffer(new ItemStack(CoinHandler.IRON_COIN, this.emeraldCost), new ItemStack(Items.COMPASS), itemstack, this.maxUses, this.villagerXp, 0.2F);
            } else {
                return null;
            }
        }
    }
}

package net.dusty_dusty.bnb_core.datagen;

import net.dusty_dusty.bnb_core.cold_crops.data.CoinData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityType;

import java.util.function.BiConsumer;

public class CoinDataBuilder {

    TagKey<EntityType<?>> valid;//change this to a holderset in 1.21.1
    IntProvider coinValue;
    boolean netheriteCoin;

    public CoinDataBuilder(TagKey<EntityType<?>> valid, IntProvider coinValue, boolean netheriteCoin) {
        this.valid = valid;
        this.coinValue = coinValue;
        this.netheriteCoin = netheriteCoin;
    }

    public static CoinDataBuilder builder(TagKey<EntityType<?>> valid) {
        return new CoinDataBuilder(valid,ConstantInt.of(1),false);
    }

    public CoinDataBuilder intProvider(IntProvider coinValue) {
        this.coinValue = coinValue;
        return this;
    }

    public CoinDataBuilder netheriteCoin() {
        netheriteCoin = true;
        return this;
    }


    public void save(BiConsumer<CoinData, ResourceLocation> output, ResourceLocation id) {
        output.accept(new CoinData(valid,coinValue,netheriteCoin), id);
    }
}

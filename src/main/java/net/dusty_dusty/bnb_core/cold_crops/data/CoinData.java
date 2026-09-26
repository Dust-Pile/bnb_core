package net.dusty_dusty.bnb_core.cold_crops.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityType;

public record CoinData(TagKey<EntityType<?>> valid, IntProvider coinValue, boolean netheriteCoin) {

    public static final Codec<CoinData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ENTITY_TYPE).fieldOf("valid").forGetter(CoinData::valid),
            IntProvider.CODEC.fieldOf("coin_value").forGetter(CoinData::coinValue),
            Codec.BOOL.fieldOf("netherite_coin").forGetter(CoinData::netheriteCoin)
    ).apply(instance, CoinData::new));
}

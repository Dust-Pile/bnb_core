package net.dusty_dusty.bnb_core.tags;

import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.ModIntegration;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class BnbEntityTypeTags {

    public static TagKey<EntityType<?>> mod(String s) {
        return TagKey.create(Registries.ENTITY_TYPE, BnbCore.id(s));
    }

}

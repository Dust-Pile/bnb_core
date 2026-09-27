package net.dusty_dusty.bnb_core.tags;

import net.dusty_dusty.bnb_core.BnbCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

@SuppressWarnings("unused")
public class BnbEntityTypeTags {
    public static TagKey<EntityType<?>> mod(String s) {
        return TagKey.create(Registries.ENTITY_TYPE, BnbCore.id(s));
    }
}

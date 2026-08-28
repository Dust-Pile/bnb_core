package net.dusty_dusty.bnb_core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BnbItemTags {

    public static final TagKey<Item> POUCH = curios("pouch");

    public static TagKey<Item> curios(String s) {
        return TagKey.create(Registries.ITEM,ModIntegration.curios.id(s));
    }

}

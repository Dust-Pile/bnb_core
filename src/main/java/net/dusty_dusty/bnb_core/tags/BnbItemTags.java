package net.dusty_dusty.bnb_core.tags;

import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.ModIntegration;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BnbItemTags {

    public static final TagKey<Item> POUCH = curios("pouch");
    public static final TagKey<Item> SILVER_WEAPONS = mod("silver_weapons");
    public static final TagKey<Item> SILVER_ARMOR = mod("silver_armor");

    public static TagKey<Item> curios(String s) {
        return TagKey.create(Registries.ITEM, ModIntegration.curios.id(s));
    }
    public static TagKey<Item> mod(String s) {
        return TagKey.create(Registries.ITEM, BnbCore.id(s));
    }

}

package net.dusty_dusty.bnb_core.tools;

//TagKey.create(Registries.BLOCK, new ResourceLocation(pName));

import net.dusty_dusty.bnb_core.BnbCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class MineableTags {
    public static final TagKey<Block> MINEABLE_WITH_GRUBHOE = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath( BnbCore.MODID, "mineable/grubhoe" ) );
    public static final TagKey<Block> MINEABLE_WITH_PICKADZE = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath( BnbCore.MODID, "mineable/pickadze" ) );
    public static final TagKey<Block> MINEABLE_WITH_PICKAXE = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath( BnbCore.MODID, "mineable/pickaxe" ) );
}

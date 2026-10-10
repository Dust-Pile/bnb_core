package net.dusty_dusty.bnb_core.datagen;

import com.phantomwing.thesilverage.item.ModItems;
import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.tags.BnbItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BnbItemTagsProvider extends ItemTagsProvider {
    public BnbItemTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, BnbCore.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(BnbItemTags.POUCH).add(Items.BUNDLE);
        tag(BnbItemTags.SILVER_WEAPONS).add(
                ModItems.SILVER_AXE.get(),
                ModItems.SILVER_HOE.get(),
                ModItems.SILVER_PICKAXE.get(),
                ModItems.SILVER_SHOVEL.get(),
                ModItems.SILVER_SWORD.get()
        );
        tag(BnbItemTags.SILVER_ARMOR).add(ModItems.SILVER_HELMET.get(),ModItems.SILVER_CHESTPLATE.get(),
                ModItems.SILVER_LEGGINGS.get(),ModItems.SILVER_BOOTS.get());
    }
}

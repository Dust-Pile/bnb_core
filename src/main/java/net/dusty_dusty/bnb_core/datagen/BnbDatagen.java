package net.dusty_dusty.bnb_core.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

public class BnbDatagen {

    public static void gather(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        var lookup = event.getLookupProvider();
        BlockTagsProvider blockTagsProvider = new BnbBlockTagsProvider(output, lookup, existingFileHelper);
        gen.addProvider(true,blockTagsProvider);
        gen.addProvider(true,new BnbItemTagsProvider(output, lookup,blockTagsProvider.contentsGetter(), existingFileHelper));
        gen.addProvider(true,new BnbCuriosDataProvider(event));
        gen.addProvider(true,new CoinDropsProvider(output,lookup));
        gen.addProvider(true,new BnbRecipeProvider(output));
    }
}

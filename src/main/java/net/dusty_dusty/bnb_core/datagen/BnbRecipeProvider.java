package net.dusty_dusty.bnb_core.datagen;

import com.mojang.datafixers.util.Pair;
import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.coins.CoinHandler;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

public class BnbRecipeProvider extends RecipeProvider {
    public BnbRecipeProvider(PackOutput pOutput) {
        super(pOutput);
        CoinHandler.populate();
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        for (int i = 0; i < CoinHandler.CONVERSIONS.size(); i++) {
            Pair<Item,Integer> pair = CoinHandler.CONVERSIONS.get(i);

            if (i < CoinHandler.CONVERSIONS.size() - 1) {
                //up conversion
                Pair<Item,Integer> pairAbove = CoinHandler.CONVERSIONS.get(i+1);
                ShapelessRecipeBuilder shapeless = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, pairAbove.getFirst());
                for (int j  = 0; j < pairAbove.getSecond(); j++) {
                    shapeless.requires(pair.getFirst());
                }

                ResourceLocation recipeId = BnbCore.id(RecipeBuilder.getDefaultRecipeId(pairAbove.getFirst()).getPath());

                shapeless.unlockedBy("has_coin",has(pair.getFirst()))
                        .save(pWriter,recipeId);
            }

            if (i > 0) {
                //down conversion
                Pair<Item,Integer> pairBelow = CoinHandler.CONVERSIONS.get(i-1);
                ShapelessRecipeBuilder shapeless = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, pairBelow.getFirst(),pair.getSecond());

                ResourceLocation recipeId = BnbCore.id(RecipeBuilder.getDefaultRecipeId(pairBelow.getFirst()).getPath());

                shapeless.requires(pair.getFirst()).unlockedBy("has_coin",has(pair.getFirst()))
                        .save(pWriter,recipeId.withSuffix("_reverse"));
            }
        }
    }
}

package net.dusty_dusty.bnb_core.datagen;

import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.coins.CoinHandler;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class BnbRecipeProvider extends RecipeProvider {
    public BnbRecipeProvider(PackOutput pOutput) {
        super(pOutput);
        CoinHandler.populate();
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        for (int i = 0; i < CoinHandler.CONVERSIONS.size(); i++) {
            CoinHandler.CoinValue pair = CoinHandler.CONVERSIONS.get(i);

            if (i < CoinHandler.CONVERSIONS.size() - 1) {
                //up conversion
                CoinHandler.CoinValue pairAbove = CoinHandler.CONVERSIONS.get(i+1);
                ShapelessRecipeBuilder shapeless = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, pairAbove.coin());
                ShapelessRecipeBuilder shapelessStack = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, pairAbove.coinstack());
                for (int j  = 0; j < pairAbove.value(); j++) {
                    shapeless.requires(pair.coin());
                    shapelessStack.requires(pair.coinstack());
                }

                ResourceLocation recipeId = BnbCore.id(RecipeBuilder.getDefaultRecipeId(pairAbove.coin()).getPath());
                ResourceLocation recipeIdStack = BnbCore.id(RecipeBuilder.getDefaultRecipeId(pairAbove.coinstack()).getPath());

                shapeless.unlockedBy("has_coin",has(pair.coin())).save(pWriter,recipeId);
                shapelessStack.unlockedBy("has_coinstack",has(pair.coinstack())).save(pWriter,recipeIdStack);

                //upstack conversion

            }

            if (i > 0) {
                //down conversion
                CoinHandler.CoinValue pairBelow = CoinHandler.CONVERSIONS.get(i-1);
                ShapelessRecipeBuilder shapeless = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, pairBelow.coin(),pair.value());
                ShapelessRecipeBuilder shapelessStack = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, pairBelow.coinstack(),pair.value());

                ResourceLocation recipeId = BnbCore.id(RecipeBuilder.getDefaultRecipeId(pairBelow.coin()).getPath());
                ResourceLocation recipeIdStack = BnbCore.id(RecipeBuilder.getDefaultRecipeId(pairBelow.coinstack()).getPath());

                shapeless.requires(pair.coin()).unlockedBy("has_coin",has(pair.coin()))
                        .save(pWriter,recipeId.withSuffix("_reverse"));
                shapelessStack.requires(pair.coinstack()).unlockedBy("has_coinstack",has(pair.coinstack()))
                        .save(pWriter,recipeIdStack.withSuffix("_reverse"));
            }
        }
    }
}

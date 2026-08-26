package com.cinecraft.chunkdeeds.recipe;

import com.cinecraft.chunkdeeds.ChunkDeedsMod;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipes {
    public static final RecipeSerializer<DeedCraftingRecipe> DEED_CRAFTING_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            ChunkDeedsMod.id("crafting_special_deed"),
            new SpecialRecipeSerializer<>(DeedCraftingRecipe::new)
    );

    public static void init() {
        // Trigger classloading
    }
}

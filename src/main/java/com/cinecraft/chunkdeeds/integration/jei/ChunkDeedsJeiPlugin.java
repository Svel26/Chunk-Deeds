package com.cinecraft.chunkdeeds.integration.jei;

import com.cinecraft.chunkdeeds.ChunkDeedsMod;
import com.cinecraft.chunkdeeds.config.ChunkDeedsConfig;
import com.cinecraft.chunkdeeds.item.ClaimDeedItem;
import com.cinecraft.chunkdeeds.item.ForceLoadDeedItem;
import com.cinecraft.chunkdeeds.item.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ChunkDeedsJeiPlugin implements IModPlugin {
    public static final Identifier PLUGIN_UID = ChunkDeedsMod.id("jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.CLAIM_DEED, new ISubtypeInterpreter<ItemStack>() {
            @Override
            public Object getSubtypeData(ItemStack ingredient, UidContext context) {
                return ClaimDeedItem.getDeedValue(ingredient);
            }

            @Override
            public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
                return String.valueOf(ClaimDeedItem.getDeedValue(ingredient));
            }
        });

        registration.registerSubtypeInterpreter(ModItems.FORCELOAD_DEED, new ISubtypeInterpreter<ItemStack>() {
            @Override
            public Object getSubtypeData(ItemStack ingredient, UidContext context) {
                return ForceLoadDeedItem.getDeedValue(ingredient);
            }

            @Override
            public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
                return String.valueOf(ForceLoadDeedItem.getDeedValue(ingredient));
            }
        });
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        ChunkDeedsConfig config = ChunkDeedsConfig.get();
        List<ItemStack> extra = new ArrayList<>();

        if (config.claimDeed.enabled) {
            int max = Math.min(8, config.claimDeed.maxDeedValue);
            for (int i = 1; i <= max; i++) {
                extra.add(ClaimDeedItem.createStack(i * Math.max(1, config.claimDeed.chunksPerItem)));
            }
        }

        if (config.forceLoadDeed.enabled) {
            int max = Math.min(8, config.forceLoadDeed.maxDeedValue);
            for (int i = 1; i <= max; i++) {
                extra.add(ForceLoadDeedItem.createStack(i * Math.max(1, config.forceLoadDeed.chunksPerItem)));
            }
        }

        if (!extra.isEmpty()) {
            registration.addExtraItemStacks(extra);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ChunkDeedsConfig config = ChunkDeedsConfig.get();
        List<RecipeEntry<CraftingRecipe>> recipes = new ArrayList<>();

        // 1. Dynamic Claim Deed Recipes for JEI
        if (config.claimDeed.enabled) {
            Item claimBase = config.getClaimBaseItem();
            Item claimMod = config.getClaimModifierItem();
            int chunksPerItem = Math.max(1, config.claimDeed.chunksPerItem);
            int maxDeedValue = Math.min(8, config.claimDeed.maxDeedValue);

            for (int count = 1; count <= maxDeedValue; count++) {
                int deedValue = count * chunksPerItem;
                DefaultedList<Ingredient> ingredients = DefaultedList.of();
                ingredients.add(Ingredient.ofItems(claimBase));
                for (int i = 0; i < count; i++) {
                    ingredients.add(Ingredient.ofItems(claimMod));
                }
                ItemStack result = ClaimDeedItem.createStack(deedValue);
                ShapelessRecipe recipe = new ShapelessRecipe("chunkdeeds_claims", CraftingRecipeCategory.MISC, result, ingredients);
                recipes.add(new RecipeEntry<>(ChunkDeedsMod.id("jei_claim_deed_" + count), recipe));
            }

            // Upgrading examples in JEI if enabled
            if (config.general.allowDeedUpgrades && maxDeedValue >= 2) {
                for (int baseCount = 1; baseCount < maxDeedValue; baseCount++) {
                    int baseVal = baseCount * chunksPerItem;
                    int addCount = 1;
                    int targetVal = baseVal + (addCount * chunksPerItem);
                    if (targetVal <= maxDeedValue * chunksPerItem) {
                        DefaultedList<Ingredient> upgradeIngredients = DefaultedList.of();
                        upgradeIngredients.add(Ingredient.ofStacks(ClaimDeedItem.createStack(baseVal)));
                        upgradeIngredients.add(Ingredient.ofItems(claimMod));
                        ItemStack upgradeResult = ClaimDeedItem.createStack(targetVal);
                        ShapelessRecipe upgradeRecipe = new ShapelessRecipe("chunkdeeds_claims_upgrade", CraftingRecipeCategory.MISC, upgradeResult, upgradeIngredients);
                        recipes.add(new RecipeEntry<>(ChunkDeedsMod.id("jei_claim_deed_upgrade_" + baseVal), upgradeRecipe));
                    }
                }
            }
        }

        // 2. Dynamic Force Load Deed Recipes for JEI
        if (config.forceLoadDeed.enabled) {
            Item forceBase = config.getForceLoadBaseItem();
            Item forceMod = config.getForceLoadModifierItem();
            int chunksPerItem = Math.max(1, config.forceLoadDeed.chunksPerItem);
            int maxDeedValue = Math.min(8, config.forceLoadDeed.maxDeedValue);

            for (int count = 1; count <= maxDeedValue; count++) {
                int deedValue = count * chunksPerItem;
                DefaultedList<Ingredient> ingredients = DefaultedList.of();
                ingredients.add(Ingredient.ofItems(forceBase));
                for (int i = 0; i < count; i++) {
                    ingredients.add(Ingredient.ofItems(forceMod));
                }
                ItemStack result = ForceLoadDeedItem.createStack(deedValue);
                ShapelessRecipe recipe = new ShapelessRecipe("chunkdeeds_forceload", CraftingRecipeCategory.MISC, result, ingredients);
                recipes.add(new RecipeEntry<>(ChunkDeedsMod.id("jei_forceload_deed_" + count), recipe));
            }

            if (config.general.allowDeedUpgrades && maxDeedValue >= 2) {
                for (int baseCount = 1; baseCount < maxDeedValue; baseCount++) {
                    int baseVal = baseCount * chunksPerItem;
                    int addCount = 1;
                    int targetVal = baseVal + (addCount * chunksPerItem);
                    if (targetVal <= maxDeedValue * chunksPerItem) {
                        DefaultedList<Ingredient> upgradeIngredients = DefaultedList.of();
                        upgradeIngredients.add(Ingredient.ofStacks(ForceLoadDeedItem.createStack(baseVal)));
                        upgradeIngredients.add(Ingredient.ofItems(forceMod));
                        ItemStack upgradeResult = ForceLoadDeedItem.createStack(targetVal);
                        ShapelessRecipe upgradeRecipe = new ShapelessRecipe("chunkdeeds_forceload_upgrade", CraftingRecipeCategory.MISC, upgradeResult, upgradeIngredients);
                        recipes.add(new RecipeEntry<>(ChunkDeedsMod.id("jei_forceload_deed_upgrade_" + baseVal), upgradeRecipe));
                    }
                }
            }
        }

        if (!recipes.isEmpty()) {
            registration.addRecipes(RecipeTypes.CRAFTING, recipes);
        }

        // 3. Information Pages in JEI
        registration.addItemStackInfo(
                new ItemStack(ModItems.CLAIM_DEED),
                Text.translatable("jei.chunkdeeds.claim_deed.info")
        );
        registration.addItemStackInfo(
                new ItemStack(ModItems.FORCELOAD_DEED),
                Text.translatable("jei.chunkdeeds.forceload_deed.info")
        );
    }
}

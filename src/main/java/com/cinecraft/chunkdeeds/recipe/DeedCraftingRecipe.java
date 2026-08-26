package com.cinecraft.chunkdeeds.recipe;

import com.cinecraft.chunkdeeds.config.ChunkDeedsConfig;
import com.cinecraft.chunkdeeds.item.ClaimDeedItem;
import com.cinecraft.chunkdeeds.item.ForceLoadDeedItem;
import com.cinecraft.chunkdeeds.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class DeedCraftingRecipe extends SpecialCraftingRecipe {
    public DeedCraftingRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    private static class RecipeMatchResult {
        boolean matches = false;
        ItemStack output = ItemStack.EMPTY;
    }

    private RecipeMatchResult evaluate(CraftingRecipeInput input) {
        RecipeMatchResult result = new RecipeMatchResult();
        int nonEmptyCount = input.getStackCount();
        if (nonEmptyCount < 2) {
            return result;
        }

        ChunkDeedsConfig config = ChunkDeedsConfig.get();
        Item claimBase = config.getClaimBaseItem();
        Item claimMod = config.getClaimModifierItem();
        Item forceBase = config.getForceLoadBaseItem();
        Item forceMod = config.getForceLoadModifierItem();

        ItemStack baseStack = ItemStack.EMPTY;
        int claimModCount = 0;
        int forceModCount = 0;
        int otherItems = 0;

        for (ItemStack stack : input.getStacks()) {
            if (stack == null || stack.isEmpty()) continue;

            Item item = stack.getItem();

            if (item == ModItems.CLAIM_DEED || item == ModItems.FORCELOAD_DEED) {
                if (baseStack.isEmpty() && stack.getCount() == 1) {
                    baseStack = stack;
                } else {
                    otherItems++;
                }
            } else if (item == claimBase && claimBase == forceBase) {
                if (baseStack.isEmpty() && stack.getCount() == 1) {
                    baseStack = stack;
                } else {
                    otherItems++;
                }
            } else if (item == claimBase) {
                if (baseStack.isEmpty() && stack.getCount() == 1) {
                    baseStack = stack;
                } else {
                    otherItems++;
                }
            } else if (item == forceBase) {
                if (baseStack.isEmpty() && stack.getCount() == 1) {
                    baseStack = stack;
                } else {
                    otherItems++;
                }
            } else if (item == claimMod) {
                if (stack.getCount() == 1) {
                    claimModCount++;
                } else {
                    otherItems++;
                }
            } else if (item == forceMod) {
                if (stack.getCount() == 1) {
                    forceModCount++;
                } else {
                    otherItems++;
                }
            } else {
                otherItems++;
            }
        }

        if (otherItems > 0 || baseStack.isEmpty()) {
            return result;
        }

        // 1. Claim Deed Crafting / Upgrading
        if (config.claimDeed.enabled && claimModCount > 0 && forceModCount == 0) {
            if (baseStack.isOf(claimBase)) {
                int totalDeedValue = claimModCount * config.claimDeed.chunksPerItem;
                if (totalDeedValue > 0 && totalDeedValue <= config.claimDeed.maxDeedValue) {
                    result.matches = true;
                    result.output = ClaimDeedItem.createStack(totalDeedValue);
                    return result;
                }
            } else if (config.general.allowDeedUpgrades && baseStack.isOf(ModItems.CLAIM_DEED)) {
                int currentValue = ClaimDeedItem.getDeedValue(baseStack);
                int totalDeedValue = currentValue + (claimModCount * config.claimDeed.chunksPerItem);
                if (totalDeedValue > currentValue && totalDeedValue <= config.claimDeed.maxDeedValue) {
                    result.matches = true;
                    result.output = ClaimDeedItem.createStack(totalDeedValue);
                    return result;
                }
            }
        }

        // 2. Force Load Deed Crafting / Upgrading
        if (config.forceLoadDeed.enabled && forceModCount > 0 && claimModCount == 0) {
            if (baseStack.isOf(forceBase)) {
                int totalDeedValue = forceModCount * config.forceLoadDeed.chunksPerItem;
                if (totalDeedValue > 0 && totalDeedValue <= config.forceLoadDeed.maxDeedValue) {
                    result.matches = true;
                    result.output = ForceLoadDeedItem.createStack(totalDeedValue);
                    return result;
                }
            } else if (config.general.allowDeedUpgrades && baseStack.isOf(ModItems.FORCELOAD_DEED)) {
                int currentValue = ForceLoadDeedItem.getDeedValue(baseStack);
                int totalDeedValue = currentValue + (forceModCount * config.forceLoadDeed.chunksPerItem);
                if (totalDeedValue > currentValue && totalDeedValue <= config.forceLoadDeed.maxDeedValue) {
                    result.matches = true;
                    result.output = ForceLoadDeedItem.createStack(totalDeedValue);
                    return result;
                }
            }
        }

        return result;
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        return evaluate(input).matches;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return evaluate(input).output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
        return ModRecipes.DEED_CRAFTING_SERIALIZER;
    }
}

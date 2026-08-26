package com.cinecraft.chunkdeeds.item;

import com.cinecraft.chunkdeeds.component.ModDataComponents;
import com.cinecraft.chunkdeeds.config.ChunkDeedsConfig;
import com.cinecraft.chunkdeeds.integration.FTBChunksIntegration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class ClaimDeedItem extends Item {
    public ClaimDeedItem() {
        super(new Item.Settings()
                .maxCount(64)
                .rarity(Rarity.RARE)
                .component(ModDataComponents.DEED_VALUE, 1));
    }

    public static int getDeedValue(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 1;
        Integer value = stack.get(ModDataComponents.DEED_VALUE);
        return value != null ? Math.max(1, value) : 1;
    }

    public static ItemStack createStack(int value) {
        ItemStack stack = new ItemStack(ModItems.CLAIM_DEED);
        stack.set(ModDataComponents.DEED_VALUE, Math.max(1, value));
        return stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int value = getDeedValue(stack);
        tooltip.add(Text.translatable("tooltip.chunkdeeds.grants_claims", value).formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("tooltip.chunkdeeds.redeem_instruction").formatted(Formatting.GRAY));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        int value = getDeedValue(stack);

        if (world.isClient()) {
            return TypedActionResult.success(stack);
        }

        if (!(user instanceof ServerPlayerEntity player)) {
            return TypedActionResult.pass(stack);
        }

        ChunkDeedsConfig config = ChunkDeedsConfig.get();

        if (!config.claimDeed.enabled) {
            player.sendMessage(Text.translatable("message.chunkdeeds.deed_disabled").formatted(Formatting.RED), true);
            return TypedActionResult.fail(stack);
        }

        if (!FTBChunksIntegration.isAvailable()) {
            player.sendMessage(Text.translatable("message.chunkdeeds.ftb_not_found").formatted(Formatting.RED), false);
            return TypedActionResult.fail(stack);
        }

        int maxTotal = config.claimDeed.maxTotalExtra;
        if (maxTotal > 0) {
            int currentExtra = FTBChunksIntegration.getExtraClaims(player);
            if (currentExtra + value > maxTotal) {
                player.sendMessage(Text.translatable("message.chunkdeeds.cap_reached_claims", currentExtra, maxTotal).formatted(Formatting.RED), true);
                return TypedActionResult.fail(stack);
            }
        }

        boolean success = FTBChunksIntegration.addExtraClaims(player, value);
        if (success) {
            ServerWorld serverWorld = (ServerWorld) world;

            if (config.general.playSounds) {
                serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 0.75f, 1.2f);
            }

            if (config.general.spawnParticles) {
                serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        16, 0.5, 0.5, 0.5, 0.1);
                serverWorld.spawnParticles(ParticleTypes.ENCHANT,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        24, 0.5, 0.5, 0.5, 0.5);
            }

            if (!player.isCreative() || config.general.consumeInCreative) {
                stack.decrement(1);
            }

            int newTotal = FTBChunksIntegration.getExtraClaims(player);
            player.sendMessage(Text.translatable("message.chunkdeeds.redeemed_claims", value, newTotal).formatted(Formatting.GREEN), false);

            return TypedActionResult.consume(stack);
        } else {
            player.sendMessage(Text.translatable("message.chunkdeeds.redeem_failed").formatted(Formatting.RED), false);
            return TypedActionResult.fail(stack);
        }
    }
}

package com.cinecraft.chunkdeeds.command;

import com.cinecraft.chunkdeeds.config.ChunkDeedsConfig;
import com.cinecraft.chunkdeeds.integration.FTBChunksIntegration;
import com.cinecraft.chunkdeeds.item.ClaimDeedItem;
import com.cinecraft.chunkdeeds.item.ForceLoadDeedItem;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Collections;

public class ChunkDeedsCommand {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("chunkdeeds");

            // /chunkdeeds reload
            root.then(CommandManager.literal("reload")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        ChunkDeedsConfig.load();
                        context.getSource().sendFeedback(() ->
                                Text.translatable("command.chunkdeeds.reload_success").formatted(Formatting.GREEN), true);
                        return 1;
                    }));

            // /chunkdeeds info [player]
            root.then(CommandManager.literal("info")
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player != null) {
                            sendPlayerInfo(context.getSource(), player);
                            return 1;
                        }
                        context.getSource().sendError(Text.translatable("command.chunkdeeds.player_only"));
                        return 0;
                    })
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                            .executes(context -> {
                                Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "targets");
                                for (ServerPlayerEntity p : players) {
                                    sendPlayerInfo(context.getSource(), p);
                                }
                                return players.size();
                            })));

            // /chunkdeeds give <targets> <claim|forceload> <amount> [count]
            root.then(CommandManager.literal("give")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                            .then(CommandManager.argument("type", StringArgumentType.word())
                                    .suggests((c, builder) -> {
                                        builder.suggest("claim");
                                        builder.suggest("forceload");
                                        return builder.buildFuture();
                                    })
                                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1, 1000))
                                            .executes(context -> giveDeeds(
                                                    context.getSource(),
                                                    EntityArgumentType.getPlayers(context, "targets"),
                                                    StringArgumentType.getString(context, "type"),
                                                    IntegerArgumentType.getInteger(context, "amount"),
                                                    1
                                            ))
                                            .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 64))
                                                    .executes(context -> giveDeeds(
                                                            context.getSource(),
                                                            EntityArgumentType.getPlayers(context, "targets"),
                                                            StringArgumentType.getString(context, "type"),
                                                            IntegerArgumentType.getInteger(context, "amount"),
                                                            IntegerArgumentType.getInteger(context, "count")
                                                    )))))));

            dispatcher.register(root);
        });
    }

    private static int giveDeeds(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String type, int amount, int count) {
        boolean isClaim = "claim".equalsIgnoreCase(type) || "claims".equalsIgnoreCase(type);
        boolean isForceLoad = "forceload".equalsIgnoreCase(type) || "force_load".equalsIgnoreCase(type) || "force".equalsIgnoreCase(type);

        if (!isClaim && !isForceLoad) {
            source.sendError(Text.translatable("command.chunkdeeds.invalid_type"));
            return 0;
        }

        for (ServerPlayerEntity player : targets) {
            ItemStack stack = isClaim ? ClaimDeedItem.createStack(amount) : ForceLoadDeedItem.createStack(amount);
            stack.setCount(count);

            boolean inserted = player.getInventory().insertStack(stack);
            if (!inserted) {
                player.dropItem(stack, false);
            }

            String deedTypeName = isClaim ? "Claim Deed" : "Force Load Deed";
            player.sendMessage(Text.translatable("command.chunkdeeds.received", count, deedTypeName, amount).formatted(Formatting.GOLD), false);
        }

        source.sendFeedback(() ->
                Text.translatable("command.chunkdeeds.given", count, isClaim ? "Claim" : "Force Load", amount, targets.size()).formatted(Formatting.GREEN), true);

        return targets.size();
    }

    private static void sendPlayerInfo(ServerCommandSource source, ServerPlayerEntity player) {
        if (!FTBChunksIntegration.isAvailable()) {
            source.sendFeedback(() -> Text.translatable("message.chunkdeeds.ftb_not_found").formatted(Formatting.RED), false);
            return;
        }

        int extraClaims = FTBChunksIntegration.getExtraClaims(player);
        int maxClaims = FTBChunksIntegration.getMaxClaims(player);
        int extraForce = FTBChunksIntegration.getExtraForceLoads(player);
        int maxForce = FTBChunksIntegration.getMaxForceLoads(player);

        source.sendFeedback(() -> Text.literal("=== FTB Chunks Info for " + player.getNameForScoreboard() + " ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("• Extra Claim Chunks: ").formatted(Formatting.YELLOW)
                .append(Text.literal(String.valueOf(extraClaims)).formatted(Formatting.GREEN))
                .append(Text.literal(" (Max Total Claims: " + maxClaims + ")").formatted(Formatting.GRAY)), false);
        source.sendFeedback(() -> Text.literal("• Extra Force-Loaded Chunks: ").formatted(Formatting.YELLOW)
                .append(Text.literal(String.valueOf(extraForce)).formatted(Formatting.AQUA))
                .append(Text.literal(" (Max Total Force: " + maxForce + ")").formatted(Formatting.GRAY)), false);
    }
}

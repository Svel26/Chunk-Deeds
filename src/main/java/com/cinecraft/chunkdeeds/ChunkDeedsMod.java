package com.cinecraft.chunkdeeds;

import com.cinecraft.chunkdeeds.command.ChunkDeedsCommand;
import com.cinecraft.chunkdeeds.component.ModDataComponents;
import com.cinecraft.chunkdeeds.config.ChunkDeedsConfig;
import com.cinecraft.chunkdeeds.integration.FTBChunksIntegration;
import com.cinecraft.chunkdeeds.item.ModItems;
import com.cinecraft.chunkdeeds.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChunkDeedsMod implements ModInitializer {
    public static final String MOD_ID = "chunkdeeds";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[ChunkDeeds] Initializing Chunk Deeds mod...");

        ChunkDeedsConfig.init();
        ModDataComponents.init();
        ModItems.init();
        ModRecipes.init();
        ChunkDeedsCommand.init();
        FTBChunksIntegration.init();

        LOGGER.info("[ChunkDeeds] Chunk Deeds initialization complete.");
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}

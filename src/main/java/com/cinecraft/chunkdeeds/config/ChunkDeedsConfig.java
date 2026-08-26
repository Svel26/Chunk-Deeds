package com.cinecraft.chunkdeeds.config;

import com.cinecraft.chunkdeeds.ChunkDeedsMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ChunkDeedsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("chunkdeeds.json");
    private static ChunkDeedsConfig INSTANCE = new ChunkDeedsConfig();

    public static class DeedConfig {
        public boolean enabled = true;
        public String baseItemId = "minecraft:paper";
        public String modifierItemId = "minecraft:diamond";
        public int chunksPerItem = 1;
        public int maxDeedValue = 8;
        public int maxTotalExtra = -1; // -1 = no cap

        public DeedConfig() {}

        public DeedConfig(boolean enabled, String baseItemId, String modifierItemId, int chunksPerItem, int maxDeedValue, int maxTotalExtra) {
            this.enabled = enabled;
            this.baseItemId = baseItemId;
            this.modifierItemId = modifierItemId;
            this.chunksPerItem = chunksPerItem;
            this.maxDeedValue = maxDeedValue;
            this.maxTotalExtra = maxTotalExtra;
        }
    }

    public static class GeneralConfig {
        public boolean playSounds = true;
        public boolean spawnParticles = true;
        public boolean allowDeedUpgrades = true;
        public boolean consumeInCreative = false;
        public boolean logRedemptions = true;
    }

    public DeedConfig claimDeed = new DeedConfig(true, "minecraft:paper", "minecraft:diamond", 1, 8, -1);
    public DeedConfig forceLoadDeed = new DeedConfig(true, "minecraft:paper", "minecraft:netherite_scrap", 1, 8, -1);
    public GeneralConfig general = new GeneralConfig();

    public static ChunkDeedsConfig get() {
        if (INSTANCE == null) {
            INSTANCE = new ChunkDeedsConfig();
        }
        return INSTANCE;
    }

    public static void init() {
        load();
    }

    public static void load() {
        File file = CONFIG_PATH.toFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                ChunkDeedsConfig loaded = GSON.fromJson(reader, ChunkDeedsConfig.class);
                if (loaded != null) {
                    INSTANCE = loaded;
                    ChunkDeedsMod.LOGGER.info("[ChunkDeeds] Configuration loaded successfully.");
                    return;
                }
            } catch (Exception e) {
                ChunkDeedsMod.LOGGER.error("[ChunkDeeds] Failed to load configuration, saving default.", e);
            }
        }
        INSTANCE = new ChunkDeedsConfig();
        save();
    }

    public static void save() {
        try {
            File file = CONFIG_PATH.toFile();
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(INSTANCE, writer);
            }
            ChunkDeedsMod.LOGGER.info("[ChunkDeeds] Configuration saved to {}", CONFIG_PATH);
        } catch (IOException e) {
            ChunkDeedsMod.LOGGER.error("[ChunkDeeds] Failed to save configuration.", e);
        }
    }

    public Item resolveItem(String identifierStr, Item fallback) {
        if (identifierStr == null || identifierStr.trim().isEmpty()) {
            return fallback;
        }
        Identifier id = Identifier.tryParse(identifierStr.trim());
        if (id != null && Registries.ITEM.containsId(id)) {
            return Registries.ITEM.get(id);
        }
        return fallback;
    }

    public Item getClaimBaseItem() {
        return resolveItem(claimDeed.baseItemId, Items.PAPER);
    }

    public Item getClaimModifierItem() {
        return resolveItem(claimDeed.modifierItemId, Items.DIAMOND);
    }

    public Item getForceLoadBaseItem() {
        return resolveItem(forceLoadDeed.baseItemId, Items.PAPER);
    }

    public Item getForceLoadModifierItem() {
        return resolveItem(forceLoadDeed.modifierItemId, Items.NETHERITE_SCRAP);
    }
}

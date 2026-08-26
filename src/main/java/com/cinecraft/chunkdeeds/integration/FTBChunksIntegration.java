package com.cinecraft.chunkdeeds.integration;

import com.cinecraft.chunkdeeds.ChunkDeedsMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.Method;
import java.util.UUID;

public class FTBChunksIntegration {
    private static boolean classesResolved = false;

    private static Method getInstanceMethod = null;
    private static Method getPersonalDataMethod = null;
    private static Method getOrCreateDataPlayerMethod = null;

    private static Method getExtraClaimChunksMethod = null;
    private static Method setExtraClaimChunksMethod = null;
    private static Method getMaxClaimChunksMethod = null;

    private static Method getExtraForceLoadChunksMethod = null;
    private static Method setExtraForceLoadChunksMethod = null;
    private static Method getMaxForceLoadChunksMethod = null;

    private static Method markDirtyMethod = null;
    private static Method updateLimitsMethod = null;
    private static Method sendPacketMethod = null;

    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("ftbchunks")) {
            resolveClasses();
        } else {
            ChunkDeedsMod.LOGGER.info("[ChunkDeeds] FTB Chunks is not installed. Deeds will require FTB Chunks to be installed on the server.");
        }
    }

    private static synchronized boolean resolveClasses() {
        if (classesResolved) return true;
        if (!FabricLoader.getInstance().isModLoaded("ftbchunks")) return false;

        try {
            Class<?> managerClass = Class.forName("dev.ftb.mods.ftbchunks.data.ClaimedChunkManagerImpl");
            getInstanceMethod = managerClass.getMethod("getInstance");
            getPersonalDataMethod = managerClass.getMethod("getPersonalData", UUID.class);

            for (Method m : managerClass.getMethods()) {
                if (m.getName().equals("getOrCreateData") && m.getParameterCount() == 1 && !m.getParameterTypes()[0].equals(UUID.class)) {
                    if (!m.getParameterTypes()[0].getName().contains("Team")) {
                        getOrCreateDataPlayerMethod = m;
                        break;
                    }
                }
            }

            Class<?> chunkTeamDataClass = Class.forName("dev.ftb.mods.ftbchunks.api.ChunkTeamData");
            getExtraClaimChunksMethod = chunkTeamDataClass.getMethod("getExtraClaimChunks");
            setExtraClaimChunksMethod = chunkTeamDataClass.getMethod("setExtraClaimChunks", int.class);
            getMaxClaimChunksMethod = chunkTeamDataClass.getMethod("getMaxClaimChunks");

            getExtraForceLoadChunksMethod = chunkTeamDataClass.getMethod("getExtraForceLoadChunks");
            setExtraForceLoadChunksMethod = chunkTeamDataClass.getMethod("setExtraForceLoadChunks", int.class);
            getMaxForceLoadChunksMethod = chunkTeamDataClass.getMethod("getMaxForceLoadChunks");

            try {
                Class<?> chunkTeamDataImplClass = Class.forName("dev.ftb.mods.ftbchunks.data.ChunkTeamDataImpl");
                markDirtyMethod = chunkTeamDataImplClass.getMethod("markDirty");
                updateLimitsMethod = chunkTeamDataImplClass.getMethod("updateLimits");
            } catch (Throwable t) {
                ChunkDeedsMod.LOGGER.debug("[ChunkDeeds] Could not resolve markDirty/updateLimits: {}", t.getMessage());
            }

            try {
                Class<?> sendPacketClass = Class.forName("dev.ftb.mods.ftbchunks.net.SendGeneralDataPacket");
                for (Method m : sendPacketClass.getMethods()) {
                    if (m.getName().equals("send") && m.getParameterCount() == 2) {
                        sendPacketMethod = m;
                        break;
                    }
                }
            } catch (Throwable t) {
                ChunkDeedsMod.LOGGER.debug("[ChunkDeeds] Could not resolve send packet method: {}", t.getMessage());
            }

            classesResolved = true;
            ChunkDeedsMod.LOGGER.info("[ChunkDeeds] FTB Chunks integration resolved successfully!");
            return true;
        } catch (Throwable t) {
            ChunkDeedsMod.LOGGER.warn("[ChunkDeeds] FTB Chunks classes could not be resolved: {}", t.getMessage());
            return false;
        }
    }

    public static boolean isAvailable() {
        if (!FabricLoader.getInstance().isModLoaded("ftbchunks")) {
            return false;
        }
        if (!classesResolved) {
            return resolveClasses();
        }
        return true;
    }

    private static Object getManager() {
        if (!isAvailable() || getInstanceMethod == null) return null;
        try {
            return getInstanceMethod.invoke(null);
        } catch (Throwable t) {
            ChunkDeedsMod.LOGGER.error("[ChunkDeeds] Error getting ClaimedChunkManager instance: {}", t.getMessage());
            return null;
        }
    }

    private static Object getPersonalData(ServerPlayerEntity player) {
        if (player == null) return null;
        try {
            Object mgr = getManager();
            if (mgr != null && getPersonalDataMethod != null) {
                return getPersonalDataMethod.invoke(mgr, player.getUuid());
            }
        } catch (Throwable t) {
            ChunkDeedsMod.LOGGER.error("[ChunkDeeds] Error getting personal chunk data for {}: {}", player.getNameForScoreboard(), t.getMessage());
        }
        return null;
    }

    private static Object getOrCreateTeamData(ServerPlayerEntity player) {
        if (player == null) return null;
        try {
            Object mgr = getManager();
            if (mgr != null && getOrCreateDataPlayerMethod != null) {
                return getOrCreateDataPlayerMethod.invoke(mgr, player);
            }
        } catch (Throwable t) {
            ChunkDeedsMod.LOGGER.error("[ChunkDeeds] Error getting team chunk data for {}: {}", player.getNameForScoreboard(), t.getMessage());
        }
        return null;
    }

    public static int getExtraClaims(ServerPlayerEntity player) {
        if (!isAvailable() || player == null) return 0;
        try {
            Object personalData = getPersonalData(player);
            if (personalData != null && getExtraClaimChunksMethod != null) {
                return (int) getExtraClaimChunksMethod.invoke(personalData);
            }
        } catch (Throwable t) {
            ChunkDeedsMod.LOGGER.error("[ChunkDeeds] Error querying extra claims for {}: {}", player.getNameForScoreboard(), t.getMessage());
        }
        return 0;
    }

    public static int getMaxClaims(ServerPlayerEntity player) {
        if (!isAvailable() || player == null) return 0;
        try {
            Object teamData = getOrCreateTeamData(player);
            if (teamData != null && getMaxClaimChunksMethod != null) {
                return (int) getMaxClaimChunksMethod.invoke(teamData);
            }
        } catch (Throwable t) {
            ChunkDeedsMod.LOGGER.error("[ChunkDeeds] Error querying max claims for {}: {}", player.getNameForScoreboard(), t.getMessage());
        }
        return 0;
    }

    public static boolean addExtraClaims(ServerPlayerEntity player, int amount) {
        if (!isAvailable() || player == null || amount <= 0) return false;
        try {
            Object personalData = getPersonalData(player);
            if (personalData == null || setExtraClaimChunksMethod == null) {
                ChunkDeedsMod.LOGGER.warn("[ChunkDeeds] Could not access personal chunk data for player {}", player.getNameForScoreboard());
                return false;
            }

            int current = getExtraClaims(player);
            int updated = Math.max(0, current + amount);
            setExtraClaimChunksMethod.invoke(personalData, updated);

            if (markDirtyMethod != null) {
                try {
                    markDirtyMethod.invoke(personalData);
                } catch (Throwable ignored) {}
            }

            Object teamData = getOrCreateTeamData(player);
            if (teamData != null && updateLimitsMethod != null) {
                try {
                    updateLimitsMethod.invoke(teamData);
                } catch (Throwable ignored) {}
            }

            if (teamData != null && sendPacketMethod != null) {
                try {
                    sendPacketMethod.invoke(null, teamData, player);
                } catch (Throwable ignored) {}
            }

            ChunkDeedsMod.LOGGER.info("[ChunkDeeds] Player {} added {} claim chunks. Total extra claims: {}", player.getNameForScoreboard(), amount, updated);
            return true;
        } catch (Throwable t) {
            ChunkDeedsMod.LOGGER.error("[ChunkDeeds] Error adding extra claims to player {}: {}", player.getNameForScoreboard(), t.getMessage(), t);
            return false;
        }
    }

    public static int getExtraForceLoads(ServerPlayerEntity player) {
        if (!isAvailable() || player == null) return 0;
        try {
            Object personalData = getPersonalData(player);
            if (personalData != null && getExtraForceLoadChunksMethod != null) {
                return (int) getExtraForceLoadChunksMethod.invoke(personalData);
            }
        } catch (Throwable t) {
            ChunkDeedsMod.LOGGER.error("[ChunkDeeds] Error querying extra forceload chunks for {}: {}", player.getNameForScoreboard(), t.getMessage());
        }
        return 0;
    }

    public static int getMaxForceLoads(ServerPlayerEntity player) {
        if (!isAvailable() || player == null) return 0;
        try {
            Object teamData = getOrCreateTeamData(player);
            if (teamData != null && getMaxForceLoadChunksMethod != null) {
                return (int) getMaxForceLoadChunksMethod.invoke(teamData);
            }
        } catch (Throwable t) {
            ChunkDeedsMod.LOGGER.error("[ChunkDeeds] Error querying max forceload chunks for {}: {}", player.getNameForScoreboard(), t.getMessage());
        }
        return 0;
    }

    public static boolean addExtraForceLoads(ServerPlayerEntity player, int amount) {
        if (!isAvailable() || player == null || amount <= 0) return false;
        try {
            Object personalData = getPersonalData(player);
            if (personalData == null || setExtraForceLoadChunksMethod == null) {
                ChunkDeedsMod.LOGGER.warn("[ChunkDeeds] Could not access personal chunk data for player {}", player.getNameForScoreboard());
                return false;
            }

            int current = getExtraForceLoads(player);
            int updated = Math.max(0, current + amount);
            setExtraForceLoadChunksMethod.invoke(personalData, updated);

            if (markDirtyMethod != null) {
                try {
                    markDirtyMethod.invoke(personalData);
                } catch (Throwable ignored) {}
            }

            Object teamData = getOrCreateTeamData(player);
            if (teamData != null && updateLimitsMethod != null) {
                try {
                    updateLimitsMethod.invoke(teamData);
                } catch (Throwable ignored) {}
            }

            if (teamData != null && sendPacketMethod != null) {
                try {
                    sendPacketMethod.invoke(null, teamData, player);
                } catch (Throwable ignored) {}
            }

            ChunkDeedsMod.LOGGER.info("[ChunkDeeds] Player {} added {} force load chunks. Total extra force load: {}", player.getNameForScoreboard(), amount, updated);
            return true;
        } catch (Throwable t) {
            ChunkDeedsMod.LOGGER.error("[ChunkDeeds] Error adding extra forceload chunks to player {}: {}", player.getNameForScoreboard(), t.getMessage(), t);
            return false;
        }
    }
}

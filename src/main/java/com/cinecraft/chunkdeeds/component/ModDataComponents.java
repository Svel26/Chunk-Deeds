package com.cinecraft.chunkdeeds.component;

import com.cinecraft.chunkdeeds.ChunkDeedsMod;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModDataComponents {
    public static final ComponentType<Integer> DEED_VALUE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            ChunkDeedsMod.id("deed_value"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .packetCodec(PacketCodecs.VAR_INT)
                    .build()
    );

    public static void init() {
        // Classloading trigger
    }
}

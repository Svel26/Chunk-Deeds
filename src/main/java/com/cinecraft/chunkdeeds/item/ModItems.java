package com.cinecraft.chunkdeeds.item;

import com.cinecraft.chunkdeeds.ChunkDeedsMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {
    public static final Item CLAIM_DEED = register("claim_deed", new ClaimDeedItem());
    public static final Item FORCELOAD_DEED = register("forceload_deed", new ForceLoadDeedItem());

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, ChunkDeedsMod.id(name), item);
    }

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(CLAIM_DEED);
            entries.add(FORCELOAD_DEED);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(CLAIM_DEED);
            entries.add(FORCELOAD_DEED);
        });
    }
}

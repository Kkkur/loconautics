/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Blocks
 */
package com.simibubi.create;

import com.simibubi.create.api.contraption.ContraptionMovementSetting;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.world.level.block.Blocks;

public class AllContraptionMovementSettings {
    public static void registerDefaults() {
        ContraptionMovementSetting.REGISTRY.register(Blocks.SPAWNER, () -> (ContraptionMovementSetting)((Object)((Object)AllConfigs.server().kinetics.spawnerMovement.get())));
        ContraptionMovementSetting.REGISTRY.register(Blocks.BUDDING_AMETHYST, () -> (ContraptionMovementSetting)((Object)((Object)AllConfigs.server().kinetics.amethystMovement.get())));
        ContraptionMovementSetting.REGISTRY.register(Blocks.OBSIDIAN, () -> (ContraptionMovementSetting)((Object)((Object)AllConfigs.server().kinetics.obsidianMovement.get())));
        ContraptionMovementSetting.REGISTRY.register(Blocks.CRYING_OBSIDIAN, () -> (ContraptionMovementSetting)((Object)((Object)AllConfigs.server().kinetics.obsidianMovement.get())));
        ContraptionMovementSetting.REGISTRY.register(Blocks.RESPAWN_ANCHOR, () -> (ContraptionMovementSetting)((Object)((Object)AllConfigs.server().kinetics.obsidianMovement.get())));
        ContraptionMovementSetting.REGISTRY.register(Blocks.REINFORCED_DEEPSLATE, () -> (ContraptionMovementSetting)((Object)((Object)AllConfigs.server().kinetics.reinforcedDeepslateMovement.get())));
    }
}

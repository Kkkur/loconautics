/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.minecraft.core.component.DataComponentPatch$Builder
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 */
package dev.ryanhcode.offroad.events;

import com.simibubi.create.AllBlocks;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity;
import dev.ryanhcode.offroad.content.components.TireLike;
import dev.ryanhcode.offroad.handlers.client.MultiMiningClientHandler;
import dev.ryanhcode.offroad.handlers.server.MultiMiningServerManager;
import dev.ryanhcode.offroad.index.OffroadDataComponents;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class OffroadCommonEvents {
    public static void modifyDefaultComponents(BiConsumer<ItemLike, Consumer<DataComponentPatch.Builder>> modify) {
        modify.accept((ItemLike)AllBlocks.FLYWHEEL, builder -> builder.set(OffroadDataComponents.TIRE, (Object)TireLike.FLYWHEEL));
        modify.accept((ItemLike)AllBlocks.LARGE_WATER_WHEEL, builder -> builder.set(OffroadDataComponents.TIRE, (Object)TireLike.LARGE_WATER_WHEEL));
        modify.accept((ItemLike)AllBlocks.CRUSHING_WHEEL, builder -> builder.set(OffroadDataComponents.TIRE, (Object)TireLike.CRUSHING_WHEEL));
        modify.accept((ItemLike)AllBlocks.WATER_WHEEL, builder -> builder.set(OffroadDataComponents.TIRE, (Object)TireLike.WATER_WHEEL));
        modify.accept((ItemLike)AllBlocks.MECHANICAL_ROLLER, builder -> builder.set(OffroadDataComponents.TIRE, (Object)TireLike.MECHANICAL_ROLLER));
    }

    public static void physicsTick(SubLevelPhysicsSystem physicsSystem, double timeStep) {
        ServerLevel level = physicsSystem.getLevel();
        WheelMountBlockEntity.applyAllBatchedForces(level, timeStep);
    }

    public static void tickLevelEvent(Level level) {
        if (!level.isClientSide) {
            MultiMiningServerManager.tick(level);
        } else {
            MultiMiningClientHandler.tick(level);
        }
    }
}

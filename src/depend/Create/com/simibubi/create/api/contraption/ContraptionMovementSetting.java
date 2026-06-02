/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.neoforged.neoforge.common.extensions.IBlockExtension
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.contraption;

import com.simibubi.create.api.registry.SimpleRegistry;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.jetbrains.annotations.Nullable;

public enum ContraptionMovementSetting {
    MOVABLE,
    NO_PICKUP,
    UNMOVABLE;

    public static final SimpleRegistry<Block, Supplier<ContraptionMovementSetting>> REGISTRY;

    @Nullable
    public static ContraptionMovementSetting get(BlockState state) {
        return ContraptionMovementSetting.get(state.getBlock());
    }

    @Nullable
    public static ContraptionMovementSetting get(Block block) {
        if (block instanceof MovementSettingProvider) {
            MovementSettingProvider provider = (MovementSettingProvider)block;
            return provider.getContraptionMovementSetting();
        }
        Supplier<ContraptionMovementSetting> supplier = REGISTRY.get(block);
        return supplier == null ? null : supplier.get();
    }

    public static boolean anyAre(Collection<StructureTemplate.StructureBlockInfo> blocks, ContraptionMovementSetting setting) {
        return blocks.stream().anyMatch(b -> ContraptionMovementSetting.get(b.state().getBlock()) == setting);
    }

    public static boolean isNoPickup(Collection<StructureTemplate.StructureBlockInfo> blocks) {
        return ContraptionMovementSetting.anyAre(blocks, NO_PICKUP);
    }

    static {
        REGISTRY = SimpleRegistry.create();
    }

    public static interface MovementSettingProvider
    extends IBlockExtension {
        public ContraptionMovementSetting getContraptionMovementSetting();
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.packager;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.logistics.packager.AllInventoryIdentifiers;
import java.util.Set;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface InventoryIdentifier {
    public static final SimpleRegistry<Block, Finder> REGISTRY = SimpleRegistry.create();

    public boolean contains(BlockFace var1);

    @Nullable
    public static InventoryIdentifier get(Level level, BlockFace face) {
        BlockState state = level.getBlockState(face.getPos());
        Finder finder = REGISTRY.get((StateHolder<Block, ?>)state);
        Finder toQuery = finder != null ? finder : AllInventoryIdentifiers::fallback;
        return toQuery.find(level, state, face);
    }

    @FunctionalInterface
    public static interface Finder {
        @Nullable
        public InventoryIdentifier find(Level var1, BlockState var2, BlockFace var3);
    }

    public record MultiFace(BlockPos pos, Set<Direction> sides) implements InventoryIdentifier
    {
        @Override
        public boolean contains(BlockFace face) {
            return this.pos.equals((Object)face.getPos()) && this.sides.contains(face.getFace());
        }
    }

    public record Bounds(BoundingBox bounds) implements InventoryIdentifier
    {
        @Override
        public boolean contains(BlockFace face) {
            return this.bounds.isInside((Vec3i)face.getPos());
        }
    }

    public record Pair(BlockPos first, BlockPos second) implements InventoryIdentifier
    {
        public Pair(BlockPos first, BlockPos second) {
            boolean isFirstLower = first.compareTo((Vec3i)second) < 0;
            this.first = isFirstLower ? first : second;
            this.second = isFirstLower ? second : first;
        }

        @Override
        public boolean contains(BlockFace face) {
            BlockPos pos = face.getPos();
            return this.first.equals((Object)pos) || this.second.equals((Object)pos);
        }
    }

    public record Single(BlockPos pos) implements InventoryIdentifier
    {
        @Override
        public boolean contains(BlockFace face) {
            return this.pos.equals((Object)face.getPos());
        }
    }
}

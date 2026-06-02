/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.util.entry.RegistryEntry
 *  com.tterrag.registrate.util.nullness.NonNullUnaryOperator
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.SignBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.behaviour.display;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDisplayTargets;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.utility.CreateLang;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class DisplayTarget {
    public static final SimpleRegistry<Block, DisplayTarget> BY_BLOCK = SimpleRegistry.create();
    public static final SimpleRegistry<BlockEntityType<?>, DisplayTarget> BY_BLOCK_ENTITY = SimpleRegistry.create();

    public abstract void acceptText(int var1, List<MutableComponent> var2, DisplayLinkContext var3);

    public abstract DisplayTargetStats provideStats(DisplayLinkContext var1);

    public AABB getMultiblockBounds(LevelAccessor level, BlockPos pos) {
        VoxelShape shape = level.getBlockState(pos).getShape((BlockGetter)level, pos);
        if (shape.isEmpty()) {
            return new AABB(pos);
        }
        return shape.bounds().move(pos);
    }

    public Component getLineOptionText(int line) {
        return CreateLang.translateDirect("display_target.line", line + 1);
    }

    public static void reserve(int line, BlockEntity target, DisplayLinkContext context) {
        if (line == 0) {
            return;
        }
        CompoundTag tag = target.getPersistentData();
        CompoundTag compound = tag.getCompound("DisplayLink");
        compound.putLong("Line" + line, context.blockEntity().getBlockPos().asLong());
        tag.put("DisplayLink", (Tag)compound);
    }

    public boolean isReserved(int line, BlockEntity target, DisplayLinkContext context) {
        CompoundTag tag = target.getPersistentData();
        CompoundTag compound = tag.getCompound("DisplayLink");
        if (!compound.contains("Line" + line)) {
            return false;
        }
        long l = compound.getLong("Line" + line);
        BlockPos reserved = BlockPos.of((long)l);
        if (!reserved.equals((Object)context.blockEntity().getBlockPos()) && AllBlocks.DISPLAY_LINK.has(target.getLevel().getBlockState(reserved))) {
            return true;
        }
        compound.remove("Line" + line);
        if (compound.isEmpty()) {
            tag.remove("DisplayLink");
        }
        return false;
    }

    public boolean requiresComponentSanitization() {
        return false;
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> displayTarget(RegistryEntry<DisplayTarget, ? extends DisplayTarget> target) {
        return builder -> (BlockBuilder)builder.onRegisterAfter(CreateRegistries.DISPLAY_TARGET, block -> BY_BLOCK.register((Block)block, (DisplayTarget)target.get()));
    }

    @Nullable
    public static DisplayTarget get(@Nullable ResourceLocation id) {
        if (id == null) {
            return null;
        }
        if (id.getNamespace().equals("create") && AllDisplayTargets.LEGACY_NAMES.containsKey(id.getPath())) {
            return (DisplayTarget)AllDisplayTargets.LEGACY_NAMES.get(id.getPath()).get();
        }
        return (DisplayTarget)CreateBuiltInRegistries.DISPLAY_TARGET.get(id);
    }

    @Nullable
    public static DisplayTarget get(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        DisplayTarget byBlock = BY_BLOCK.get((StateHolder<Block, ?>)state);
        if (byBlock != null) {
            return byBlock;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            return null;
        }
        DisplayTarget byBe = BY_BLOCK_ENTITY.get(be.getType());
        if (byBe != null) {
            return byBe;
        }
        return be instanceof SignBlockEntity ? (DisplayTarget)AllDisplayTargets.SIGN.get() : null;
    }
}

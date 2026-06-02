/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.createmod.catnip.nbt.NBTProcessors
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureEntityInfo
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.schematics;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllStructureProcessorTypes;
import java.util.Optional;
import net.createmod.catnip.nbt.NBTProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class SchematicProcessor
extends StructureProcessor {
    public static final SchematicProcessor INSTANCE = new SchematicProcessor();
    public static final MapCodec<SchematicProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

    private SchematicProcessor() {
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos pos, BlockPos anotherPos, StructureTemplate.StructureBlockInfo rawInfo, StructureTemplate.StructureBlockInfo info, StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        CompoundTag nbt;
        BlockEntity be;
        if (info.nbt() != null && info.state().hasBlockEntity() && (be = ((EntityBlock)info.state().getBlock()).newBlockEntity(info.pos(), info.state())) != null && (nbt = NBTProcessors.process((BlockState)info.state(), (BlockEntity)be, (CompoundTag)info.nbt(), (boolean)false)) != info.nbt()) {
            return new StructureTemplate.StructureBlockInfo(info.pos(), info.state(), nbt);
        }
        return info;
    }

    @Nullable
    public StructureTemplate.StructureEntityInfo processEntity(LevelReader world, BlockPos pos, StructureTemplate.StructureEntityInfo rawInfo, StructureTemplate.StructureEntityInfo info, StructurePlaceSettings settings, StructureTemplate template) {
        return EntityType.by((CompoundTag)info.nbt).flatMap(type -> {
            Entity e;
            if (world instanceof Level && (e = type.create((Level)world)) != null && !e.onlyOpCanSetNbt()) {
                return Optional.of(info);
            }
            return Optional.empty();
        }).orElse(null);
    }

    protected StructureProcessorType<?> getType() {
        return (StructureProcessorType)AllStructureProcessorTypes.SCHEMATIC.get();
    }
}

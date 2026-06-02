/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.BellBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.NoteBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.NoteBlockInstrument
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.entity;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import java.util.ArrayList;
import java.util.HashMap;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class ArrivalSoundQueue {
    public int offset;
    int min;
    int max;
    Multimap<Integer, BlockPos> sources = Multimaps.newMultimap(new HashMap(), ArrayList::new);

    public ArrivalSoundQueue() {
        this.min = Integer.MAX_VALUE;
        this.max = Integer.MIN_VALUE;
    }

    @Nullable
    public Integer firstTick() {
        return this.sources.isEmpty() ? null : Integer.valueOf(this.min + this.offset);
    }

    @Nullable
    public Integer lastTick() {
        return this.sources.isEmpty() ? null : Integer.valueOf(this.max + this.offset);
    }

    public boolean tick(CarriageContraptionEntity entity, int tick, boolean backwards) {
        if (!this.sources.containsKey((Object)(tick -= this.offset))) {
            return backwards ? tick > this.min : tick < this.max;
        }
        Contraption contraption = entity.getContraption();
        for (BlockPos blockPos : this.sources.get((Object)tick)) {
            ArrivalSoundQueue.play(entity, contraption.getBlocks().get(blockPos));
        }
        return backwards ? tick > this.min : tick < this.max;
    }

    public Pair<Boolean, Integer> getFirstWhistle(CarriageContraptionEntity entity) {
        Integer firstTick = this.firstTick();
        Integer lastTick = this.lastTick();
        if (firstTick == null || lastTick == null || firstTick > lastTick) {
            return null;
        }
        for (int i = firstTick.intValue(); i <= lastTick; ++i) {
            if (!this.sources.containsKey((Object)(i - this.offset))) continue;
            Contraption contraption = entity.getContraption();
            for (BlockPos blockPos : this.sources.get((Object)(i - this.offset))) {
                BlockState state;
                StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(blockPos);
                if (info == null || !((state = info.state()).getBlock() instanceof WhistleBlock) || info.nbt() == null) continue;
                int pitch = info.nbt().getInt("Pitch");
                WhistleBlock.WhistleSize size = (WhistleBlock.WhistleSize)((Object)state.getValue(WhistleBlock.SIZE));
                return Pair.of((Object)(size == WhistleBlock.WhistleSize.LARGE ? 1 : 0), (Object)((size == WhistleBlock.WhistleSize.SMALL ? 12 : 0) - pitch));
            }
        }
        return null;
    }

    public void serialize(CompoundTag tagIn) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Offset", this.offset);
        tag.put("Sources", (Tag)NBTHelper.writeCompoundList((Iterable)this.sources.entries(), e -> {
            CompoundTag c = new CompoundTag();
            c.putInt("Tick", ((Integer)e.getKey()).intValue());
            c.put("Pos", NbtUtils.writeBlockPos((BlockPos)((BlockPos)e.getValue())));
            return c;
        }));
        tagIn.put("SoundQueue", (Tag)tag);
    }

    public void deserialize(CompoundTag tagIn) {
        CompoundTag tag = tagIn.getCompound("SoundQueue");
        this.offset = tag.getInt("Offset");
        NBTHelper.iterateCompoundList((ListTag)tag.getList("Sources", 10), c -> this.add(c.getInt("Tick"), NBTHelper.readBlockPos((CompoundTag)c, (String)"Pos")));
    }

    public void add(int offset, BlockPos localPos) {
        this.sources.put((Object)offset, (Object)localPos);
        this.min = Math.min(offset, this.min);
        this.max = Math.max(offset, this.max);
    }

    public static boolean isPlayable(BlockState state) {
        if (state.getBlock() instanceof BellBlock) {
            return true;
        }
        if (state.getBlock() instanceof NoteBlock) {
            return true;
        }
        return state.getBlock() instanceof WhistleBlock;
    }

    public static void play(CarriageContraptionEntity entity, StructureTemplate.StructureBlockInfo info) {
        Block block;
        if (info == null) {
            return;
        }
        BlockState state = info.state();
        if (state.getBlock() instanceof BellBlock) {
            if (AllBlocks.HAUNTED_BELL.has(state)) {
                ArrivalSoundQueue.playSimple(entity, AllSoundEvents.HAUNTED_BELL_USE.getMainEvent(), 1.0f, 1.0f);
            } else {
                ArrivalSoundQueue.playSimple(entity, SoundEvents.BELL_BLOCK, 1.0f, 1.0f);
            }
        }
        if ((block = state.getBlock()) instanceof NoteBlock) {
            NoteBlock nb = (NoteBlock)block;
            float f = (float)Math.pow(2.0, (double)((Integer)state.getValue((Property)NoteBlock.NOTE) - 12) / 12.0);
            ArrivalSoundQueue.playSimple(entity, (SoundEvent)((NoteBlockInstrument)state.getValue((Property)NoteBlock.INSTRUMENT)).getSoundEvent().value(), 1.0f, f);
        }
        if (state.getBlock() instanceof WhistleBlock && info.nbt() != null) {
            int pitch = info.nbt().getInt("Pitch");
            WhistleBlock.WhistleSize size = (WhistleBlock.WhistleSize)((Object)state.getValue(WhistleBlock.SIZE));
            float f = (float)Math.pow(2.0, (double)((size == WhistleBlock.WhistleSize.SMALL ? 12 : 0) - pitch) / 12.0);
            ArrivalSoundQueue.playSimple(entity, (size == WhistleBlock.WhistleSize.LARGE ? AllSoundEvents.WHISTLE_TRAIN_LOW : AllSoundEvents.WHISTLE_TRAIN).getMainEvent(), 1.0f, f);
        }
    }

    private static void playSimple(CarriageContraptionEntity entity, SoundEvent event, float volume, float pitch) {
        entity.level().playSound(null, (Entity)entity, event, SoundSource.NEUTRAL, 5.0f * volume, pitch);
    }
}

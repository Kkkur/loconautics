/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.schematics.cannon;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.Arrays;
import java.util.Optional;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class LaunchedItem {
    public int totalTicks;
    public int ticksRemaining;
    public BlockPos target;
    public ItemStack stack;

    private LaunchedItem(BlockPos start, BlockPos target, ItemStack stack) {
        this(target, stack, LaunchedItem.ticksForDistance(start, target), LaunchedItem.ticksForDistance(start, target));
    }

    private static int ticksForDistance(BlockPos start, BlockPos target) {
        return (int)Math.max(10.0, Math.sqrt(Math.sqrt(target.distSqr((Vec3i)start))) * 4.0);
    }

    LaunchedItem() {
    }

    private LaunchedItem(BlockPos target, ItemStack stack, int ticksLeft, int total) {
        this.target = target;
        this.stack = stack;
        this.totalTicks = total;
        this.ticksRemaining = ticksLeft;
    }

    public boolean update(Level world) {
        if (this.ticksRemaining > 0) {
            --this.ticksRemaining;
            return false;
        }
        if (world.isClientSide) {
            return false;
        }
        this.place(world);
        return true;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag c = new CompoundTag();
        c.putInt("TotalTicks", this.totalTicks);
        c.putInt("TicksLeft", this.ticksRemaining);
        c.put("Stack", this.stack.saveOptional(registries));
        c.put("Target", NbtUtils.writeBlockPos((BlockPos)this.target));
        return c;
    }

    public static LaunchedItem fromNBT(CompoundTag c, HolderLookup.Provider registries, HolderGetter<Block> holderGetter) {
        ForBelt launched = c.contains("Length") ? new ForBelt() : (c.contains("BlockState") ? new ForBlockState() : new ForEntity());
        ((LaunchedItem)launched).readNBT(c, registries, holderGetter);
        return launched;
    }

    abstract void place(Level var1);

    void readNBT(CompoundTag c, HolderLookup.Provider registries, HolderGetter<Block> holderGetter) {
        this.target = NBTHelper.readBlockPos((CompoundTag)c, (String)"Target");
        this.ticksRemaining = c.getInt("TicksLeft");
        this.totalTicks = c.getInt("TotalTicks");
        this.stack = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)c.getCompound("Stack"));
    }

    public static class ForBelt
    extends ForBlockState {
        public int length;
        public BeltBlockEntity.CasingType[] casings;

        public ForBelt() {
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registries) {
            CompoundTag serializeNBT = super.serializeNBT(registries);
            serializeNBT.putInt("Length", this.length);
            serializeNBT.putIntArray("Casing", Arrays.stream(this.casings).map(Enum::ordinal).toList());
            return serializeNBT;
        }

        @Override
        void readNBT(CompoundTag nbt, HolderLookup.Provider registries, HolderGetter<Block> holderGetter) {
            this.length = nbt.getInt("Length");
            int[] intArray = nbt.getIntArray("Casing");
            this.casings = new BeltBlockEntity.CasingType[this.length];
            for (int i = 0; i < this.casings.length; ++i) {
                this.casings[i] = i >= intArray.length ? BeltBlockEntity.CasingType.NONE : BeltBlockEntity.CasingType.values()[Mth.clamp((int)intArray[i], (int)0, (int)(BeltBlockEntity.CasingType.values().length - 1))];
            }
            super.readNBT(nbt, registries, holderGetter);
        }

        public ForBelt(BlockPos start, BlockPos target, ItemStack stack, BlockState state, BeltBlockEntity.CasingType[] casings) {
            super(start, target, stack, state, null);
            this.casings = casings;
            this.length = casings.length;
        }

        @Override
        void place(Level world) {
            boolean isStart = this.state.getValue(BeltBlock.PART) == BeltPart.START;
            BlockPos offset = BeltBlock.nextSegmentPosition(this.state, BlockPos.ZERO, isStart);
            int i = this.length - 1;
            Direction.Axis axis = this.state.getValue(BeltBlock.SLOPE) == BeltSlope.SIDEWAYS ? Direction.Axis.Y : ((Direction)this.state.getValue(BeltBlock.HORIZONTAL_FACING)).getClockWise().getAxis();
            world.setBlockAndUpdate(this.target, (BlockState)AllBlocks.SHAFT.getDefaultState().setValue((Property)AbstractSimpleShaftBlock.AXIS, (Comparable)axis));
            BeltConnectorItem.createBelts(world, this.target, this.target.offset(offset.getX() * i, offset.getY() * i, offset.getZ() * i));
            for (int segment = 0; segment < this.length; ++segment) {
                BlockPos casingTarget;
                BlockEntity blockEntity;
                if (this.casings[segment] == BeltBlockEntity.CasingType.NONE || !((blockEntity = world.getBlockEntity(casingTarget = this.target.offset(offset.getX() * segment, offset.getY() * segment, offset.getZ() * segment))) instanceof BeltBlockEntity)) continue;
                BeltBlockEntity bbe = (BeltBlockEntity)blockEntity;
                bbe.setCasingType(this.casings[segment]);
            }
        }
    }

    public static class ForBlockState
    extends LaunchedItem {
        public BlockState state;
        public CompoundTag data;

        ForBlockState() {
        }

        public ForBlockState(BlockPos start, BlockPos target, ItemStack stack, BlockState state, CompoundTag data) {
            super(start, target, stack);
            this.state = state;
            this.data = data;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registries) {
            CompoundTag serializeNBT = super.serializeNBT(registries);
            serializeNBT.put("BlockState", (Tag)NbtUtils.writeBlockState((BlockState)this.state));
            if (this.data != null) {
                this.data.remove("x");
                this.data.remove("y");
                this.data.remove("z");
                this.data.remove("id");
                serializeNBT.put("Data", (Tag)this.data);
            }
            return serializeNBT;
        }

        @Override
        void readNBT(CompoundTag nbt, HolderLookup.Provider registries, HolderGetter<Block> holderGetter) {
            super.readNBT(nbt, registries, holderGetter);
            this.state = NbtUtils.readBlockState(holderGetter, (CompoundTag)nbt.getCompound("BlockState"));
            if (nbt.contains("Data", 10)) {
                this.data = nbt.getCompound("Data");
            }
        }

        @Override
        void place(Level world) {
            BlockHelper.placeSchematicBlock(world, this.state, this.target, this.stack, this.data);
        }
    }

    public static class ForEntity
    extends LaunchedItem {
        public Entity entity;
        private CompoundTag deferredTag;

        ForEntity() {
        }

        public ForEntity(BlockPos start, BlockPos target, ItemStack stack, Entity entity) {
            super(start, target, stack);
            this.entity = entity;
        }

        @Override
        public boolean update(Level world) {
            if (this.deferredTag != null && this.entity == null) {
                try {
                    Optional loadEntityUnchecked = EntityType.create((CompoundTag)this.deferredTag, (Level)world);
                    if (!loadEntityUnchecked.isPresent()) {
                        return true;
                    }
                    this.entity = (Entity)loadEntityUnchecked.get();
                }
                catch (Exception var3) {
                    return true;
                }
                this.deferredTag = null;
            }
            return super.update(world);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registries) {
            CompoundTag serializeNBT = super.serializeNBT(registries);
            if (this.entity != null) {
                serializeNBT.put("Entity", (Tag)this.entity.serializeNBT(registries));
            }
            return serializeNBT;
        }

        @Override
        void readNBT(CompoundTag nbt, HolderLookup.Provider registries, HolderGetter<Block> holderGetter) {
            super.readNBT(nbt, registries, holderGetter);
            if (nbt.contains("Entity")) {
                this.deferredTag = nbt.getCompound("Entity");
            }
        }

        @Override
        void place(Level world) {
            if (this.entity != null) {
                world.addFreshEntity(this.entity);
            }
        }
    }
}

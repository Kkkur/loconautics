/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.redstone.link;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public class LinkBehaviour
extends BlockEntityBehaviour
implements IRedstoneLinkable,
ClipboardCloneable {
    public static final BehaviourType<LinkBehaviour> TYPE = new BehaviourType();
    RedstoneLinkNetworkHandler.Frequency frequencyFirst = RedstoneLinkNetworkHandler.Frequency.EMPTY;
    RedstoneLinkNetworkHandler.Frequency frequencyLast = RedstoneLinkNetworkHandler.Frequency.EMPTY;
    ValueBoxTransform firstSlot;
    ValueBoxTransform secondSlot;
    Vec3 textShift;
    public boolean newPosition;
    private Mode mode;
    private IntSupplier transmission;
    private IntConsumer signalCallback;

    protected LinkBehaviour(SmartBlockEntity be, Pair<ValueBoxTransform, ValueBoxTransform> slots) {
        super(be);
        this.firstSlot = (ValueBoxTransform)slots.getLeft();
        this.secondSlot = (ValueBoxTransform)slots.getRight();
        this.textShift = Vec3.ZERO;
        this.newPosition = true;
    }

    public static LinkBehaviour receiver(SmartBlockEntity be, Pair<ValueBoxTransform, ValueBoxTransform> slots, IntConsumer signalCallback) {
        LinkBehaviour behaviour = new LinkBehaviour(be, slots);
        behaviour.signalCallback = signalCallback;
        behaviour.mode = Mode.RECEIVE;
        return behaviour;
    }

    public static LinkBehaviour transmitter(SmartBlockEntity be, Pair<ValueBoxTransform, ValueBoxTransform> slots, IntSupplier transmission) {
        LinkBehaviour behaviour = new LinkBehaviour(be, slots);
        behaviour.transmission = transmission;
        behaviour.mode = Mode.TRANSMIT;
        return behaviour;
    }

    public LinkBehaviour moveText(Vec3 shift) {
        this.textShift = shift;
        return this;
    }

    public void copyItemsFrom(LinkBehaviour behaviour) {
        if (behaviour == null) {
            return;
        }
        this.frequencyFirst = behaviour.frequencyFirst;
        this.frequencyLast = behaviour.frequencyLast;
    }

    @Override
    public boolean isListening() {
        return this.mode == Mode.RECEIVE;
    }

    @Override
    public int getTransmittedStrength() {
        return this.mode == Mode.TRANSMIT ? this.transmission.getAsInt() : 0;
    }

    @Override
    public void setReceivedStrength(int networkPower) {
        if (!this.newPosition) {
            return;
        }
        this.signalCallback.accept(networkPower);
    }

    public void notifySignalChange() {
        Create.REDSTONE_LINK_NETWORK_HANDLER.updateNetworkOf((LevelAccessor)this.getWorld(), this);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (this.getWorld().isClientSide) {
            return;
        }
        this.getHandler().addToNetwork((LevelAccessor)this.getWorld(), this);
        this.newPosition = true;
    }

    @Override
    public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() {
        return Couple.create((Object)this.frequencyFirst, (Object)this.frequencyLast);
    }

    @Override
    public void unload() {
        super.unload();
        if (this.getWorld().isClientSide) {
            return;
        }
        this.getHandler().removeFromNetwork((LevelAccessor)this.getWorld(), this);
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        nbt.put("FrequencyFirst", this.frequencyFirst.getStack().saveOptional(registries));
        nbt.put("FrequencyLast", this.frequencyLast.getStack().saveOptional(registries));
        nbt.putLong("LastKnownPosition", this.blockEntity.getBlockPos().asLong());
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        long positionKey;
        long positionInTag = this.blockEntity.getBlockPos().asLong();
        this.newPosition = positionInTag != (positionKey = nbt.getLong("LastKnownPosition"));
        super.read(nbt, registries, clientPacket);
        this.frequencyFirst = RedstoneLinkNetworkHandler.Frequency.of(ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)nbt.getCompound("FrequencyFirst")));
        this.frequencyLast = RedstoneLinkNetworkHandler.Frequency.of(ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)nbt.getCompound("FrequencyLast")));
    }

    public void setFrequency(boolean first, ItemStack stack) {
        boolean changed;
        stack = stack.copy();
        stack.setCount(1);
        ItemStack toCompare = first ? this.frequencyFirst.getStack() : this.frequencyLast.getStack();
        boolean bl = changed = !ItemStack.isSameItemSameComponents((ItemStack)stack, (ItemStack)toCompare);
        if (changed) {
            this.getHandler().removeFromNetwork((LevelAccessor)this.getWorld(), this);
        }
        if (first) {
            this.frequencyFirst = RedstoneLinkNetworkHandler.Frequency.of(stack);
        } else {
            this.frequencyLast = RedstoneLinkNetworkHandler.Frequency.of(stack);
        }
        if (!changed) {
            return;
        }
        this.blockEntity.sendData();
        this.getHandler().addToNetwork((LevelAccessor)this.getWorld(), this);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    private RedstoneLinkNetworkHandler getHandler() {
        return Create.REDSTONE_LINK_NETWORK_HANDLER;
    }

    public boolean testHit(Boolean first, Vec3 hit) {
        BlockState state = this.blockEntity.getBlockState();
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf((Vec3i)this.blockEntity.getBlockPos()));
        return (first != false ? this.firstSlot : this.secondSlot).testHit((LevelAccessor)this.getWorld(), this.getPos(), state, localHit);
    }

    @Override
    public boolean isAlive() {
        Level level = this.getWorld();
        BlockPos pos = this.getPos();
        if (this.blockEntity.isChunkUnloaded()) {
            return false;
        }
        if (this.blockEntity.isRemoved()) {
            return false;
        }
        if (!level.isLoaded(pos)) {
            return false;
        }
        return level.getBlockEntity(pos) == this.blockEntity;
    }

    @Override
    public BlockPos getLocation() {
        return this.getPos();
    }

    @Override
    public String getClipboardKey() {
        return "Frequencies";
    }

    @Override
    public boolean writeToClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        tag.put("First", this.frequencyFirst.getStack().saveOptional(registries));
        tag.put("Last", this.frequencyLast.getStack().saveOptional(registries));
        return true;
    }

    @Override
    public boolean readFromClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (!tag.contains("First") || !tag.contains("Last")) {
            return false;
        }
        if (simulate) {
            return true;
        }
        this.setFrequency(true, ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)tag.getCompound("First")));
        this.setFrequency(false, ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)tag.getCompound("Last")));
        return true;
    }

    static enum Mode {
        TRANSMIT,
        RECEIVE;

    }

    public static class SlotPositioning {
        Function<BlockState, Pair<Vec3, Vec3>> offsets;
        Function<BlockState, Vec3> rotation;
        float scale;

        public SlotPositioning(Function<BlockState, Pair<Vec3, Vec3>> offsetsForState, Function<BlockState, Vec3> rotationForState) {
            this.offsets = offsetsForState;
            this.rotation = rotationForState;
            this.scale = 1.0f;
        }

        public SlotPositioning scale(float scale) {
            this.scale = scale;
            return this;
        }
    }
}

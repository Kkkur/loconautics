/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.clipboard.ClipboardCloneable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.util.Tuple
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver;

import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import dev.simulated_team.simulated.content.blocks.redstone.AbstractLinkedReceiverBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver.ModulatingLinkedReceiverScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ModulatingLinkedReceiverBlockEntity
extends AbstractLinkedReceiverBlockEntity
implements ClipboardCloneable {
    public static int RANGE_LIMIT = 256;
    public int minRange = 8;
    public int maxRange = 64;
    private double distanceToClosest = 0.0;
    private double oldDistanceToClosest = 0.0;
    private double clientDistance = 0.0;
    private double clientOldDistance = 0.0;

    public ModulatingLinkedReceiverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(20);
    }

    public void lazyTick() {
        super.lazyTick();
        if (this.distanceToClosest != this.oldDistanceToClosest) {
            this.sendData();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            if (this.clientDistance == 0.0 && this.distanceToClosest != 0.0) {
                this.clientDistance = this.distanceToClosest;
                this.clientOldDistance = this.distanceToClosest;
            } else {
                double targetDelta = (this.distanceToClosest - this.clientDistance) / (double)this.lazyTickRate;
                double delta = Math.min(Math.abs(this.distanceToClosest - this.clientDistance), Math.abs(targetDelta));
                double sign = Mth.sign((double)targetDelta);
                this.clientOldDistance = this.clientDistance;
                this.clientDistance += delta * sign;
            }
        }
    }

    public double getClientDistance(float pt) {
        return Mth.lerp((double)pt, (double)this.clientOldDistance, (double)this.clientDistance);
    }

    @Override
    public void updateSignal() {
        this.oldDistanceToClosest = this.distanceToClosest;
        this.distanceToClosest = RANGE_LIMIT;
        super.updateSignal();
    }

    @Override
    public Tuple<Integer, Double> getSignalFromLink(Vec3 relativePosition, int transmittedStrength) {
        double distance = relativePosition.length();
        if (this.distanceToClosest > distance) {
            this.distanceToClosest = distance;
        }
        if (distance > (double)this.maxRange) {
            return new Tuple((Object)0, (Object)0.0);
        }
        if (this.minRange == this.maxRange) {
            return new Tuple((Object)transmittedStrength, (Object)distance);
        }
        double normalizedStrength = (distance - (double)this.maxRange) / (double)(this.minRange - this.maxRange);
        double strength = Math.min(14.0 * normalizedStrength + 1.0, (double)transmittedStrength);
        return new Tuple((Object)((int)Math.max(0.0, Math.min(15.0, strength))), (Object)distance);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("MinRange", this.minRange);
        compound.putInt("MaxRange", this.maxRange);
        compound.putDouble("DistanceToClosest", this.distanceToClosest);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        ModulatingLinkedReceiverScreen screen;
        Screen screen2;
        this.distanceToClosest = compound.getDouble("DistanceToClosest");
        if (!(clientPacket && (screen2 = Minecraft.getInstance().screen) instanceof ModulatingLinkedReceiverScreen && (screen = (ModulatingLinkedReceiverScreen)screen2).isThisBlock(this.getBlockPos()))) {
            this.minRange = compound.getInt("MinRange");
            this.maxRange = compound.getInt("MaxRange");
        }
        super.read(compound, registries, clientPacket);
    }

    public String getClipboardKey() {
        return "LinkRange";
    }

    public boolean writeToClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider provider, CompoundTag tag, Direction direction) {
        tag.putInt("minRange", this.minRange);
        tag.putInt("maxRange", this.maxRange);
        return true;
    }

    public boolean readFromClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider provider, CompoundTag tag, Player player, Direction direction, boolean simulate) {
        if (!tag.contains("minRange")) {
            return false;
        }
        if (simulate) {
            return true;
        }
        this.minRange = tag.getInt("minRange");
        this.maxRange = tag.getInt("maxRange");
        this.sendData();
        return true;
    }

    public double getDistanceToClosest() {
        return this.distanceToClosest;
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.transfer;

import com.google.common.base.Predicates;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.fluids.transfer.FluidSplashPacket;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class FluidManipulationBehaviour
extends BlockEntityBehaviour {
    BoundingBox affectedArea;
    BlockPos rootPos;
    boolean infinite;
    protected boolean counterpartActed;
    static final int searchedPerTick = 1024;
    static final int validationTimerMin = 160;
    List<BlockPosEntry> frontier;
    Set<BlockPos> visited;
    int revalidateIn;

    public FluidManipulationBehaviour(SmartBlockEntity be) {
        super(be);
        this.setValidationTimer();
        this.infinite = false;
        this.visited = new HashSet<BlockPos>();
        this.frontier = new ArrayList<BlockPosEntry>();
    }

    public boolean isInfinite() {
        return this.infinite;
    }

    public void counterpartActed() {
        this.counterpartActed = true;
    }

    protected int validationTimer() {
        int maxBlocks = this.maxBlocks();
        return maxBlocks < 0 ? 160 : Math.max(160, maxBlocks / 1024 + 1);
    }

    protected int setValidationTimer() {
        this.revalidateIn = this.validationTimer();
        return this.revalidateIn;
    }

    protected int setLongValidationTimer() {
        this.revalidateIn = this.validationTimer() * 2;
        return this.revalidateIn;
    }

    protected int maxRange() {
        return (Integer)AllConfigs.server().fluids.hosePulleyRange.get();
    }

    protected int maxBlocks() {
        return (Integer)AllConfigs.server().fluids.hosePulleyBlockThreshold.get();
    }

    protected boolean fillInfinite() {
        return (Boolean)AllConfigs.server().fluids.fillInfinite.get();
    }

    public void reset() {
        if (this.affectedArea != null) {
            this.scheduleUpdatesInAffectedArea();
        }
        this.affectedArea = null;
        this.setValidationTimer();
        this.frontier.clear();
        this.visited.clear();
        this.infinite = false;
    }

    @Override
    public void destroy() {
        this.reset();
        super.destroy();
    }

    protected void scheduleUpdatesInAffectedArea() {
        Level world = this.getWorld();
        BlockPos.betweenClosedStream((BlockPos)new BlockPos(this.affectedArea.minX() - 1, this.affectedArea.minY() - 1, this.affectedArea.minZ() - 1), (BlockPos)new BlockPos(this.affectedArea.maxX() + 1, this.affectedArea.maxY() + 1, this.affectedArea.maxZ() + 1)).forEach(pos -> {
            FluidState nextFluidState = world.getFluidState(pos);
            if (nextFluidState.isEmpty()) {
                return;
            }
            world.scheduleTick(pos, nextFluidState.getType(), world.getRandom().nextInt(5));
        });
    }

    protected int comparePositions(BlockPosEntry e1, BlockPosEntry e2) {
        Vec3 centerOfRoot = VecHelper.getCenterOf((Vec3i)this.rootPos);
        BlockPos pos2 = e2.pos;
        BlockPos pos1 = e1.pos;
        if (pos1.getY() != pos2.getY()) {
            return Integer.compare(pos2.getY(), pos1.getY());
        }
        int compareDistance = Integer.compare(e2.distance, e1.distance);
        if (compareDistance != 0) {
            return compareDistance;
        }
        return Double.compare(VecHelper.getCenterOf((Vec3i)pos2).distanceToSqr(centerOfRoot), VecHelper.getCenterOf((Vec3i)pos1).distanceToSqr(centerOfRoot));
    }

    protected Fluid search(Fluid fluid, List<BlockPosEntry> frontier, Set<BlockPos> visited, BiConsumer<BlockPos, Integer> add, boolean searchDownward) throws ChunkNotLoadedException {
        Level world = this.getWorld();
        int maxBlocks = this.maxBlocks();
        int maxRange = this.maxRange();
        int maxRangeSq = maxRange * maxRange;
        for (int i = 0; !(i >= 1024 || frontier.isEmpty() || visited.size() > maxBlocks && this.canDrainInfinitely(fluid)); ++i) {
            BlockPosEntry entry = frontier.remove(0);
            BlockPos currentPos = entry.pos;
            if (visited.contains(currentPos)) continue;
            visited.add(currentPos);
            if (!world.isLoaded(currentPos)) {
                throw new ChunkNotLoadedException();
            }
            FluidState fluidState = world.getFluidState(currentPos);
            if (fluidState.isEmpty()) continue;
            Fluid currentFluid = FluidHelper.convertToStill(fluidState.getType());
            if (fluid == null) {
                fluid = currentFluid;
            }
            if (!currentFluid.isSame(fluid)) continue;
            add.accept(currentPos, entry.distance);
            for (Direction side : Iterate.directions) {
                Fluid nextFluid;
                FluidState nextFluidState;
                if (!searchDownward && side == Direction.DOWN) continue;
                BlockPos offsetPos = currentPos.relative(side);
                if (!world.isLoaded(offsetPos)) {
                    throw new ChunkNotLoadedException();
                }
                if (visited.contains(offsetPos) || offsetPos.distSqr((Vec3i)this.rootPos) > (double)maxRangeSq || (nextFluidState = world.getFluidState(offsetPos)).isEmpty() || (nextFluid = nextFluidState.getType()) == FluidHelper.convertToFlowing(nextFluid) && side == Direction.UP && !VecHelper.onSameAxis((BlockPos)this.rootPos, (BlockPos)offsetPos, (Direction.Axis)Direction.Axis.Y)) continue;
                frontier.add(new BlockPosEntry(offsetPos, entry.distance + 1));
            }
        }
        return fluid;
    }

    protected void playEffect(Level world, BlockPos pos, Fluid fluid, boolean fillSound) {
        if (fluid == null) {
            return;
        }
        BlockPos splooshPos = pos == null ? this.blockEntity.getBlockPos() : pos;
        FluidStack stack = new FluidStack(fluid, 1);
        SoundEvent soundevent = fillSound ? FluidHelper.getFillSound(stack) : FluidHelper.getEmptySound(stack);
        world.playSound(null, splooshPos, soundevent, SoundSource.BLOCKS, 0.3f, 1.0f);
        if (world instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)world;
            CatnipServices.NETWORK.sendToClientsAround(serverLevel, (Vec3i)splooshPos, 10.0, (CustomPacketPayload)new FluidSplashPacket(splooshPos, stack));
        }
    }

    protected boolean canDrainInfinitely(Fluid fluid) {
        if (fluid == null) {
            return false;
        }
        return this.maxBlocks() != -1 && ((BottomlessFluidMode)AllConfigs.server().fluids.bottomlessFluidMode.get()).test(fluid);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.infinite) {
            NBTHelper.putMarker((CompoundTag)nbt, (String)"Infinite");
        }
        if (this.rootPos != null) {
            nbt.put("LastPos", NbtUtils.writeBlockPos((BlockPos)this.rootPos));
        }
        if (this.affectedArea != null) {
            nbt.put("AffectedAreaFrom", NbtUtils.writeBlockPos((BlockPos)new BlockPos(this.affectedArea.minX(), this.affectedArea.minY(), this.affectedArea.minZ())));
            nbt.put("AffectedAreaTo", NbtUtils.writeBlockPos((BlockPos)new BlockPos(this.affectedArea.maxX(), this.affectedArea.maxY(), this.affectedArea.maxZ())));
        }
        super.write(nbt, registries, clientPacket);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        this.infinite = nbt.contains("Infinite");
        if (nbt.contains("LastPos")) {
            this.rootPos = NBTHelper.readBlockPos((CompoundTag)nbt, (String)"LastPos");
        }
        if (nbt.contains("AffectedAreaFrom") && nbt.contains("AffectedAreaTo")) {
            this.affectedArea = BoundingBox.fromCorners((Vec3i)NBTHelper.readBlockPos((CompoundTag)nbt, (String)"AffectedAreaFrom"), (Vec3i)NBTHelper.readBlockPos((CompoundTag)nbt, (String)"AffectedAreaTo"));
        }
        super.read(nbt, registries, clientPacket);
    }

    public record BlockPosEntry(BlockPos pos, int distance) {
    }

    public static class ChunkNotLoadedException
    extends Exception {
        private static final long serialVersionUID = 1L;
    }

    public static enum BottomlessFluidMode implements Predicate<Fluid>
    {
        ALLOW_ALL((Predicate<Fluid>)Predicates.alwaysTrue()),
        DENY_ALL((Predicate<Fluid>)Predicates.alwaysFalse()),
        ALLOW_BY_TAG(AllTags.AllFluidTags.BOTTOMLESS_ALLOW::matches),
        DENY_BY_TAG((Predicate<Fluid>)Predicates.not(AllTags.AllFluidTags.BOTTOMLESS_DENY::matches));

        private final Predicate<Fluid> predicate;

        private BottomlessFluidMode(Predicate<Fluid> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(Fluid fluid) {
            return this.predicate.test(fluid);
        }
    }
}

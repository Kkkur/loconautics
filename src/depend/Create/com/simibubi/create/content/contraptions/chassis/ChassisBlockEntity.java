/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions.chassis;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllKeys;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.contraptions.chassis.AbstractChassisBlock;
import com.simibubi.create.content.contraptions.chassis.ChassisRangeDisplay;
import com.simibubi.create.content.contraptions.chassis.LinearChassisBlock;
import com.simibubi.create.content.contraptions.chassis.RadialChassisBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.BulkScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ChassisBlockEntity
extends SmartBlockEntity {
    ScrollValueBehaviour range;
    public int currentlySelectedRange;

    public ChassisBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        int max = (Integer)AllConfigs.server().kinetics.maxChassisRange.get();
        this.range = new ChassisScrollValueBehaviour((Component)CreateLang.translateDirect("contraptions.chassis.range", new Object[0]), this, new CenteredSideValueBoxTransform(), be -> ((ChassisBlockEntity)be).collectChassisGroup());
        this.range.requiresWrench();
        this.range.between(1, max);
        this.range.withClientCallback(i -> CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> ChassisRangeDisplay.display(this)));
        this.range.setValue(max / 2);
        this.range.withFormatter(s -> String.valueOf(this.currentlySelectedRange));
        behaviours.add(this.range);
        this.currentlySelectedRange = this.range.getValue();
    }

    @Override
    public void initialize() {
        super.initialize();
        if (this.getBlockState().getBlock() instanceof RadialChassisBlock) {
            this.range.setLabel((Component)CreateLang.translateDirect("contraptions.chassis.radius", new Object[0]));
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (clientPacket) {
            this.currentlySelectedRange = this.getRange();
        }
    }

    public int getRange() {
        return this.range.getValue();
    }

    public List<BlockPos> getIncludedBlockPositions(Direction forcedMovement, boolean visualize) {
        if (!(this.getBlockState().getBlock() instanceof AbstractChassisBlock)) {
            return Collections.emptyList();
        }
        return this.isRadial() ? this.getIncludedBlockPositionsRadial(forcedMovement, visualize) : this.getIncludedBlockPositionsLinear(forcedMovement, visualize);
    }

    protected boolean isRadial() {
        return this.level.getBlockState(this.worldPosition).getBlock() instanceof RadialChassisBlock;
    }

    public List<ChassisBlockEntity> collectChassisGroup() {
        LinkedList<BlockPos> frontier = new LinkedList<BlockPos>();
        ArrayList<ChassisBlockEntity> collected = new ArrayList<ChassisBlockEntity>();
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        frontier.add(this.worldPosition);
        while (!frontier.isEmpty()) {
            BlockPos current = (BlockPos)frontier.poll();
            if (visited.contains(current)) continue;
            visited.add(current);
            BlockEntity blockEntity = this.level.getBlockEntity(current);
            if (!(blockEntity instanceof ChassisBlockEntity)) continue;
            ChassisBlockEntity chassis = (ChassisBlockEntity)blockEntity;
            collected.add(chassis);
            visited.add(current);
            chassis.addAttachedChasses(frontier, visited);
        }
        return collected;
    }

    public boolean addAttachedChasses(Queue<BlockPos> frontier, Set<BlockPos> visited) {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof AbstractChassisBlock)) {
            return false;
        }
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)AbstractChassisBlock.AXIS);
        if (this.isRadial()) {
            for (int offset : new int[]{-1, 1}) {
                Direction direction = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
                BlockPos currentPos = this.worldPosition.relative(direction, offset);
                if (!this.level.isLoaded(currentPos)) {
                    return false;
                }
                BlockState neighbourState = this.level.getBlockState(currentPos);
                if (!AllBlocks.RADIAL_CHASSIS.has(neighbourState) || axis != neighbourState.getValue((Property)BlockStateProperties.AXIS) || visited.contains(currentPos)) continue;
                frontier.add(currentPos);
            }
            return true;
        }
        for (Direction offset : Iterate.directions) {
            BlockPos current = this.worldPosition.relative(offset);
            if (visited.contains(current)) continue;
            if (!this.level.isLoaded(current)) {
                return false;
            }
            BlockState neighbourState = this.level.getBlockState(current);
            if (!LinearChassisBlock.isChassis(neighbourState) || !LinearChassisBlock.sameKind(state, neighbourState) || neighbourState.getValue((Property)LinearChassisBlock.AXIS) != axis) continue;
            frontier.add(current);
        }
        return true;
    }

    private List<BlockPos> getIncludedBlockPositionsLinear(Direction forcedMovement, boolean visualize) {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        BlockState state = this.getBlockState();
        AbstractChassisBlock block = (AbstractChassisBlock)state.getBlock();
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)AbstractChassisBlock.AXIS);
        Direction facing = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        int chassisRange = visualize ? this.currentlySelectedRange : this.getRange();
        block0: for (int offset : new int[]{1, -1}) {
            if (offset == -1) {
                facing = facing.getOpposite();
            }
            boolean sticky = (Boolean)state.getValue((Property)block.getGlueableSide(state, facing));
            for (int i = 1; i <= chassisRange; ++i) {
                BlockPos current = this.worldPosition.relative(facing, i);
                BlockState currentState = this.level.getBlockState(current);
                if (forcedMovement != facing && !sticky || !BlockMovementChecks.isMovementNecessary(currentState, this.level, current) || BlockMovementChecks.isBrittle(currentState)) continue block0;
                positions.add(current);
                if (BlockMovementChecks.isNotSupportive(currentState, facing)) continue block0;
            }
        }
        return positions;
    }

    private List<BlockPos> getIncludedBlockPositionsRadial(Direction forcedMovement, boolean visualize) {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        BlockState state = this.level.getBlockState(this.worldPosition);
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)AbstractChassisBlock.AXIS);
        AbstractChassisBlock block = (AbstractChassisBlock)state.getBlock();
        int chassisRange = visualize ? this.currentlySelectedRange : this.getRange();
        for (Direction facing : Iterate.directions) {
            if (facing.getAxis() == axis || !((Boolean)state.getValue((Property)block.getGlueableSide(state, facing))).booleanValue()) continue;
            BlockPos startPos = this.worldPosition.relative(facing);
            LinkedList<BlockPos> localFrontier = new LinkedList<BlockPos>();
            HashSet<BlockPos> localVisited = new HashSet<BlockPos>();
            localFrontier.add(startPos);
            while (!localFrontier.isEmpty()) {
                BlockPos searchPos = (BlockPos)localFrontier.remove(0);
                BlockState searchedState = this.level.getBlockState(searchPos);
                if (localVisited.contains(searchPos) || !searchPos.closerThan((Vec3i)this.worldPosition, (double)((float)chassisRange + 0.5f)) || !BlockMovementChecks.isMovementNecessary(searchedState, this.level, searchPos) || BlockMovementChecks.isBrittle(searchedState)) continue;
                localVisited.add(searchPos);
                if (!searchPos.equals((Object)this.worldPosition)) {
                    positions.add(searchPos);
                }
                for (Direction offset : Iterate.directions) {
                    if (offset.getAxis() == axis || searchPos.equals((Object)this.worldPosition) && offset != facing || BlockMovementChecks.isNotSupportive(searchedState, offset)) continue;
                    localFrontier.add(searchPos.relative(offset));
                }
            }
        }
        return positions;
    }

    class ChassisScrollValueBehaviour
    extends BulkScrollValueBehaviour {
        public ChassisScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot, Function<SmartBlockEntity, List<? extends SmartBlockEntity>> groupGetter) {
            super(label, be, slot, groupGetter);
        }

        @Override
        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            ImmutableList rows = ImmutableList.of((Object)CreateLang.translateDirect("contraptions.chassis.distance", new Object[0]));
            ValueSettingsFormatter formatter = new ValueSettingsFormatter(vs -> new ValueSettingsBehaviour.ValueSettings(vs.row(), vs.value() + 1).format());
            return new ValueSettingsBoard(this.label, this.max - 1, 1, (List<Component>)rows, formatter);
        }

        @Override
        @OnlyIn(value=Dist.CLIENT)
        public void newSettingHovered(ValueSettingsBehaviour.ValueSettings valueSetting) {
            if (!((ChassisBlockEntity)ChassisBlockEntity.this).level.isClientSide) {
                return;
            }
            if (!AllKeys.ctrlDown()) {
                ChassisBlockEntity.this.currentlySelectedRange = valueSetting.value() + 1;
            } else {
                for (SmartBlockEntity smartBlockEntity : this.getBulk()) {
                    if (!(smartBlockEntity instanceof ChassisBlockEntity)) continue;
                    ChassisBlockEntity cbe = (ChassisBlockEntity)smartBlockEntity;
                    cbe.currentlySelectedRange = valueSetting.value() + 1;
                }
            }
            ChassisRangeDisplay.display(ChassisBlockEntity.this);
        }

        @Override
        public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings vs, boolean ctrlHeld) {
            super.setValueSettings(player, new ValueSettingsBehaviour.ValueSettings(vs.row(), vs.value() + 1), ctrlHeld);
        }

        @Override
        public ValueSettingsBehaviour.ValueSettings getValueSettings() {
            ValueSettingsBehaviour.ValueSettings vs = super.getValueSettings();
            return new ValueSettingsBehaviour.ValueSettings(vs.row(), vs.value() - 1);
        }

        @Override
        public String getClipboardKey() {
            return "Chassis";
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.observer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.compat.computercraft.events.TrainPassEvent;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.observer.TrackObserver;
import com.simibubi.create.content.trains.observer.TrackObserverBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class TrackObserverBlockEntity
extends SmartBlockEntity
implements TransformableBlockEntity,
Clearable {
    public TrackTargetingBehaviour<TrackObserver> edgePoint;
    private FilteringBehaviour filtering;
    public AbstractComputerBehaviour computerBehaviour;
    @Nullable
    public UUID passingTrainUUID;

    public TrackObserverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.TRACK_OBSERVER.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.edgePoint = new TrackTargetingBehaviour<TrackObserver>(this, EdgePointType.OBSERVER);
        behaviours.add(this.edgePoint);
        this.filtering = this.createFilter().withCallback(this::onFilterChanged);
        behaviours.add(this.filtering);
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
        this.filtering.setLabel(CreateLang.translateDirect("logistics.train_observer.cargo_filter", new Object[0]));
    }

    private void onFilterChanged(ItemStack newFilter) {
        if (this.level.isClientSide()) {
            return;
        }
        TrackObserver observer = this.getObserver();
        if (observer != null) {
            observer.setFilterAndNotify(this.level, newFilter);
        }
    }

    @Override
    public void tick() {
        BlockState blockState;
        super.tick();
        if (this.level.isClientSide()) {
            return;
        }
        boolean shouldBePowered = false;
        TrackObserver observer = this.getObserver();
        if (observer != null) {
            shouldBePowered = observer.isActivated();
        }
        if (this.isBlockPowered() == shouldBePowered) {
            return;
        }
        if (observer != null && this.computerBehaviour.hasAttachedComputer()) {
            if (shouldBePowered) {
                this.passingTrainUUID = observer.getCurrentTrain();
            }
            if (this.passingTrainUUID != null) {
                this.computerBehaviour.prepareComputerEvent(new TrainPassEvent(Create.RAILWAYS.trains.get(this.passingTrainUUID), shouldBePowered));
                if (!shouldBePowered) {
                    this.passingTrainUUID = null;
                }
            }
        }
        if ((blockState = this.getBlockState()).hasProperty((Property)TrackObserverBlock.POWERED)) {
            this.level.setBlock(this.worldPosition, (BlockState)blockState.setValue((Property)TrackObserverBlock.POWERED, (Comparable)Boolean.valueOf(shouldBePowered)), 3);
        }
        DisplayLinkBlock.notifyGatherers((LevelAccessor)this.level, this.worldPosition);
    }

    @Nullable
    public TrackObserver getObserver() {
        return this.edgePoint.getEdgePoint();
    }

    public ItemStack getFilter() {
        return this.filtering.getFilter();
    }

    public boolean isBlockPowered() {
        return this.getBlockState().getOptionalValue((Property)TrackObserverBlock.POWERED).orElse(false);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(Vec3.atLowerCornerOf((Vec3i)this.worldPosition), Vec3.atLowerCornerOf((Vec3i)this.edgePoint.getGlobalPosition())).inflate(2.0);
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        this.edgePoint.transform(be, transform);
    }

    public FilteringBehaviour createFilter() {
        return new FilteringBehaviour(this, new ValueBoxTransform(this){

            @Override
            public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
                TransformStack.of((PoseStack)ms).rotateXDegrees(90.0f);
            }

            @Override
            public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
                return new Vec3(0.5, 0.96875, 0.5);
            }
        });
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }

    public void clearContent() {
        this.filtering.setFilter(ItemStack.EMPTY);
    }
}

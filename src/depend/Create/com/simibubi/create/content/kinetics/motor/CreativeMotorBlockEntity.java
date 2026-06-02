/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package com.simibubi.create.content.kinetics.motor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlock;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CreativeMotorBlockEntity
extends GeneratingKineticBlockEntity {
    public static final int DEFAULT_SPEED = 16;
    public static final int MAX_SPEED = 256;
    public ScrollValueBehaviour generatedSpeed;
    public AbstractComputerBehaviour computerBehaviour;

    public CreativeMotorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.MOTOR.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        int max = 256;
        this.generatedSpeed = new KineticScrollValueBehaviour((Component)CreateLang.translateDirect("kinetics.creative_motor.rotation_speed", new Object[0]), this, new MotorValueBox());
        this.generatedSpeed.between(-max, max);
        this.generatedSpeed.value = 16;
        this.generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
        behaviours.add(this.generatedSpeed);
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!this.hasSource() || this.getGeneratedSpeed() > this.getTheoreticalSpeed()) {
            this.updateGeneratedRotation();
        }
    }

    @Override
    public float getGeneratedSpeed() {
        if (!AllBlocks.CREATIVE_MOTOR.has(this.getBlockState())) {
            return 0.0f;
        }
        return CreativeMotorBlockEntity.convertToDirection(this.generatedSpeed.getValue(), (Direction)this.getBlockState().getValue((Property)CreativeMotorBlock.FACING));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }

    static class MotorValueBox
    extends ValueBoxTransform.Sided {
        MotorValueBox() {
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)12.5);
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            Direction facing = (Direction)state.getValue((Property)CreativeMotorBlock.FACING);
            return super.getLocalOffset(level, pos, state).add(Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(-0.0625));
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
            Direction facing = (Direction)state.getValue((Property)CreativeMotorBlock.FACING);
            if (facing.getAxis() == Direction.Axis.Y) {
                return;
            }
            if (this.getSide() != Direction.UP) {
                return;
            }
            TransformStack.of((PoseStack)ms).rotateZDegrees(-AngleHelper.horizontalAngle((Direction)facing) + 180.0f);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            Direction facing = (Direction)state.getValue((Property)CreativeMotorBlock.FACING);
            if (facing.getAxis() != Direction.Axis.Y && direction == Direction.DOWN) {
                return false;
            }
            return direction.getAxis() != facing.getAxis();
        }
    }
}

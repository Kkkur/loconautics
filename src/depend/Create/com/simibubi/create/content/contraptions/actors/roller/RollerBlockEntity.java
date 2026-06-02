/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.contraptions.actors.roller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.actors.roller.RollerBlock;
import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RollerBlockEntity
extends SmartBlockEntity {
    private float manuallyAnimatedSpeed;
    public FilteringBehaviour filtering;
    public ScrollOptionBehaviour<RollingMode> mode;
    private boolean dontPropagate = false;

    public RollerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.filtering = new FilteringBehaviour(this, new RollerValueBox(this, 3));
        behaviours.add(this.filtering);
        this.mode = new ScrollOptionBehaviour<RollingMode>(RollingMode.class, (Component)CreateLang.translateDirect("contraptions.roller_mode", new Object[0]), this, new RollerValueBox(this, -3));
        behaviours.add(this.mode);
        this.filtering.setLabel(CreateLang.translateDirect("contraptions.mechanical_roller.pave_material", new Object[0]));
        this.filtering.withCallback(this::onFilterChanged);
        this.filtering.withPredicate(this::isValidMaterial);
        this.mode.withCallback(this::onModeChanged);
    }

    protected void onModeChanged(int mode) {
        this.shareValuesToAdjacent();
    }

    protected void onFilterChanged(ItemStack newFilter) {
        this.shareValuesToAdjacent();
    }

    protected boolean isValidMaterial(ItemStack newFilter) {
        if (newFilter.isEmpty()) {
            return true;
        }
        BlockState appliedState = RollerMovementBehaviour.getStateToPaveWith(newFilter);
        if (appliedState.isAir()) {
            return false;
        }
        if (appliedState.getBlock() instanceof EntityBlock) {
            return false;
        }
        if (appliedState.getBlock() instanceof StairBlock) {
            return false;
        }
        VoxelShape shape = appliedState.getShape((BlockGetter)this.level, this.worldPosition);
        if (shape.isEmpty() || !shape.bounds().equals((Object)Shapes.block().bounds())) {
            return false;
        }
        VoxelShape collisionShape = appliedState.getCollisionShape((BlockGetter)this.level, this.worldPosition);
        return !collisionShape.isEmpty();
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition).inflate(1.0);
    }

    public float getAnimatedSpeed() {
        return this.manuallyAnimatedSpeed;
    }

    public void setAnimatedSpeed(float speed) {
        this.manuallyAnimatedSpeed = speed;
    }

    public void searchForSharedValues() {
        BlockState blockState = this.getBlockState();
        Direction facing = blockState.getOptionalValue((Property)RollerBlock.FACING).orElse(Direction.SOUTH);
        for (int side : Iterate.positiveAndNegative) {
            BlockEntity blockEntity;
            BlockPos pos = this.worldPosition.relative(facing.getClockWise(), side);
            if (this.level.getBlockState(pos) != blockState || !((blockEntity = this.level.getBlockEntity(pos)) instanceof RollerBlockEntity)) continue;
            RollerBlockEntity otherRoller = (RollerBlockEntity)blockEntity;
            this.acceptSharedValues(otherRoller.mode.getValue(), otherRoller.filtering.getFilter());
            this.shareValuesToAdjacent();
            break;
        }
    }

    protected void acceptSharedValues(int mode, ItemStack filter) {
        this.dontPropagate = true;
        this.filtering.setFilter(filter.copy());
        this.mode.setValue(mode);
        this.dontPropagate = false;
        this.notifyUpdate();
    }

    public void shareValuesToAdjacent() {
        if (this.dontPropagate || this.level.isClientSide()) {
            return;
        }
        BlockState blockState = this.getBlockState();
        Direction facing = blockState.getOptionalValue((Property)RollerBlock.FACING).orElse(Direction.SOUTH);
        for (int side : Iterate.positiveAndNegative) {
            BlockEntity blockEntity;
            BlockPos pos;
            for (int i = 1; i < 100 && this.level.getBlockState(pos = this.worldPosition.relative(facing.getClockWise(), side * i)) == blockState && (blockEntity = this.level.getBlockEntity(pos)) instanceof RollerBlockEntity; ++i) {
                RollerBlockEntity otherRoller = (RollerBlockEntity)blockEntity;
                otherRoller.acceptSharedValues(this.mode.getValue(), this.filtering.getFilter());
            }
        }
    }

    private final class RollerValueBox
    extends ValueBoxTransform {
        private int hOffset;

        public RollerValueBox(RollerBlockEntity rollerBlockEntity, int hOffset) {
            this.hOffset = hOffset;
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            Direction facing = (Direction)state.getValue((Property)RollerBlock.FACING);
            float yRot = AngleHelper.horizontalAngle((Direction)facing) + 180.0f;
            ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot)).rotateXDegrees(90.0f);
        }

        @Override
        public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
            Vec3 offset = this.getLocalOffset(level, pos, state);
            if (offset == null) {
                return false;
            }
            return localHit.distanceTo(offset) < (double)(this.scale / 3.0f);
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            Direction facing = (Direction)state.getValue((Property)RollerBlock.FACING);
            float stateAngle = AngleHelper.horizontalAngle((Direction)facing) + 180.0f;
            return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)(8 + this.hOffset), (double)15.5, (double)11.0), (double)stateAngle, (Direction.Axis)Direction.Axis.Y);
        }
    }

    static enum RollingMode implements INamedIconOptions
    {
        TUNNEL_PAVE(AllIcons.I_ROLLER_PAVE),
        STRAIGHT_FILL(AllIcons.I_ROLLER_FILL),
        WIDE_FILL(AllIcons.I_ROLLER_WIDE_FILL);

        private String translationKey;
        private AllIcons icon;

        private RollingMode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "create.contraptions.roller_mode." + Lang.asId((String)this.name());
        }

        @Override
        public AllIcons getIcon() {
            return this.icon;
        }

        @Override
        public String getTranslationKey() {
            return this.translationKey;
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
 *  com.simibubi.create.content.equipment.clipboard.ClipboardCloneable
 *  com.simibubi.create.content.redstone.diodes.BrassDiodeScrollValueBehaviour
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour$StepContext
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.redstone.redstone_accumulator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.redstone.diodes.BrassDiodeScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.redstone.redstone_accumulator.RedstoneAccumulatorBlock;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class RedstoneAccumulatorBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation,
ClipboardCloneable {
    protected ScrollValueBehaviour inputDelay;
    protected int delayTicks = 0;
    protected int outputSignal = 0;
    protected LerpedFloat lerpedState = LerpedFloat.linear();

    public RedstoneAccumulatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void initialize() {
        super.initialize();
        this.updateSignal();
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.inputDelay = new BrassDiodeScrollValueBehaviour((Component)Component.translatable((String)"block.simulated.redstone_accumulator.input_delay"), (SmartBlockEntity)this, (ValueBoxTransform)new RedstoneAccumulatorValueBoxTransform());
        this.inputDelay.between(2, 72000);
        this.inputDelay.value = 10;
        this.inputDelay.withFormatter(this::format);
        this.inputDelay.withCallback(this::inputDelayChanged);
        behaviours.add((BlockEntityBehaviour)this.inputDelay);
    }

    public void tick() {
        super.tick();
        if (this.level == null) {
            return;
        }
        Direction facing = (Direction)this.getBlockState().getValue((Property)RedstoneAccumulatorBlock.FACING);
        boolean backSignal = (Boolean)this.getBlockState().getValue((Property)RedstoneAccumulatorBlock.POWERED);
        boolean sideSignal = (Boolean)this.getBlockState().getValue((Property)RedstoneAccumulatorBlock.SIDE_POWERED);
        if (backSignal && sideSignal) {
            return;
        }
        if (!backSignal && !sideSignal) {
            this.delayTicks = 0;
        }
        int tempSignal = this.outputSignal;
        if (this.delayTicks == this.inputDelay.value) {
            if (backSignal) {
                ++tempSignal;
                this.delayTicks = 0;
            } else if (sideSignal) {
                --tempSignal;
                this.delayTicks = 0;
            }
            if (tempSignal != this.outputSignal) {
                this.setOutputSignal(tempSignal);
            }
        } else {
            this.delayTicks = Math.min(this.delayTicks + 1, this.inputDelay.value);
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
            this.level.updateNeighborsAt(this.worldPosition.relative(facing), this.getBlockState().getBlock());
        }
        if (this.level.isClientSide) {
            this.lerpedState.tickChaser();
        }
        this.lerpedState.chase((double)this.outputSignal, 0.4, LerpedFloat.Chaser.EXP);
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        this.outputSignal = tag.getInt("OutputSignal");
        this.delayTicks = tag.getInt("DelayTicks");
        this.lerpedState.chase((double)this.outputSignal, 0.4, LerpedFloat.Chaser.EXP);
        super.read(tag, registries, clientPacket);
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.putInt("OutputSignal", this.outputSignal);
        tag.putInt("DelayTicks", this.delayTicks);
        super.write(tag, registries, clientPacket);
    }

    private void inputDelayChanged(Integer integer) {
        this.sendData();
    }

    public void updateSignal() {
        this.sendData();
    }

    public void setOutputSignal(int output) {
        boolean update = output != this.outputSignal;
        this.outputSignal = Mth.clamp((int)output, (int)0, (int)15);
        if (update) {
            this.updateFacingBlock(this.getBlockState().getBlock(), this.level);
        }
    }

    private void updateFacingBlock(Block block, Level levelIn) {
        levelIn.updateNeighborsAt(this.worldPosition, block);
        levelIn.updateNeighborsAt(this.worldPosition.relative(((Direction)this.getBlockState().getValue((Property)RedstoneAccumulatorBlock.FACING)).getOpposite()), block);
    }

    private int step(ScrollValueBehaviour.StepContext context) {
        int value = context.currentValue;
        if (!context.forward) {
            --value;
        }
        if (value < 20) {
            return 1;
        }
        if (value < 1200) {
            return 20;
        }
        return 1200;
    }

    private String format(int value) {
        if (value < 20) {
            return value + "t";
        }
        if (value < 1200) {
            return value / 20 + "s";
        }
        return value / 20 / 60 + "m";
    }

    public String getClipboardKey() {
        return "Block";
    }

    public boolean readFromClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider provider, CompoundTag tag, Player player, Direction direction, boolean simulate) {
        if (!tag.contains("Inverted")) {
            return false;
        }
        if (simulate) {
            return true;
        }
        BlockState blockState = this.getBlockState();
        if (((Boolean)blockState.getValue((Property)RedstoneAccumulatorBlock.INVERTED)).booleanValue() != tag.getBoolean("Inverted")) {
            this.level.setBlockAndUpdate(this.worldPosition, (BlockState)blockState.cycle((Property)RedstoneAccumulatorBlock.INVERTED));
        }
        return true;
    }

    public boolean writeToClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider provider, CompoundTag tag, Direction direction) {
        tag.putBoolean("Inverted", this.getBlockState().getOptionalValue((Property)RedstoneAccumulatorBlock.INVERTED).orElse(false).booleanValue());
        return true;
    }

    private static class RedstoneAccumulatorValueBoxTransform
    extends ValueBoxTransform {
        private RedstoneAccumulatorValueBoxTransform() {
        }

        public Vec3 getLocalOffset(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
            return new Vec3(0.5, (double)0.4125f, 0.5);
        }

        public void rotate(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, PoseStack poseStack) {
            float yRot = AngleHelper.horizontalAngle((Direction)((Direction)blockState.getValue((Property)RedstoneAccumulatorBlock.FACING))) + 180.0f;
            ((PoseTransformStack)TransformStack.of((PoseStack)poseStack).rotateYDegrees(yRot)).rotateXDegrees(90.0f);
        }
    }
}

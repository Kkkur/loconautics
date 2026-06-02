/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.AllSoundEvents
 *  com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity$SequenceContext
 *  com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour$ValueSettings
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  com.simibubi.create.foundation.utility.CreateLang
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.steering_wheel;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.behaviour.HoldTipBehaviour;
import dev.simulated_team.simulated.content.blocks.steering_wheel.NoParticleKineticEffectHandler;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlock;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimClickInteractions;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SteeringWheelBlockEntity
extends GeneratingKineticBlockEntity {
    public static final int RPM = 16;
    public boolean held = false;
    private int inUse = 0;
    public ScrollValueBehaviour angleInput;
    public float targetAngle = 0.0f;
    public float targetAngleToUpdate = 0.0f;
    private float angle = 0.0f;
    private float clientAngle = 0.0f;
    private float oldClientAngle = 0.0f;
    private double sequencedAngleLimit = 0.0;
    float generatedSpeed = 0.0f;
    float logicalSpeed = 0.0f;
    public BlockState material = Blocks.SPRUCE_PLANKS.defaultBlockState();
    private static final MutableComponent ROTATE_TIP = SimLang.translate("gui.hold_tip.hold_to_rotate", new Object[0]).component();
    private int lastPlayedIncrement = 0;

    public SteeringWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.effects = new NoParticleKineticEffectHandler((KineticBlockEntity)this);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new HoldTipBehaviour((SmartBlockEntity)this, SteeringWheelBlockEntity::holdTipGetter));
        this.angleInput = new SteeringWheelScrollValueBehaviour((SmartBlockEntity)this).between(1, 360);
        behaviours.add((BlockEntityBehaviour)this.angleInput);
        this.angleInput.value = 180;
    }

    public static MutableComponent holdTipGetter(Player player, BlockPos pos, BlockState state) {
        if (SteeringWheelBlock.lookingAtWheel(player, pos, 1.0f, state)) {
            return ROTATE_TIP;
        }
        return null;
    }

    public void startHolding() {
        this.held = true;
        this.notifyUpdate();
    }

    public void stopHolding() {
        this.held = false;
        this.notifyUpdate();
    }

    public float directionConvert(float val) {
        return -KineticBlockEntity.convertToDirection((float)val, (Direction)((Direction)this.getBlockState().getValue((Property)SteeringWheelBlock.FACING)));
    }

    public void updateTargetAngle(float absoluteTarget) {
        if (this.targetAngle == (absoluteTarget = Mth.clamp((float)absoluteTarget, (float)(-this.angleInput.getValue()), (float)this.angleInput.getValue()))) {
            return;
        }
        this.targetAngle = absoluteTarget;
        float relativeAngle = absoluteTarget - this.angle;
        if ((double)Math.abs(relativeAngle) < 0.001 && this.inUse <= 0) {
            this.generatedSpeed = 0.0f;
            this.updateGeneratedRotation();
            return;
        }
        float rotationSpeed = 16.0f * Math.signum(relativeAngle);
        if (rotationSpeed == 0.0f) {
            return;
        }
        float relativeValue = relativeAngle / rotationSpeed;
        if (relativeValue <= 0.0f && this.inUse <= 0) {
            this.generatedSpeed = 0.0f;
            this.updateGeneratedRotation();
            return;
        }
        double degreesPerTick = KineticBlockEntity.convertToAngular((float)rotationSpeed);
        this.inUse = (int)Math.ceil((double)relativeAngle / degreesPerTick) + 2;
        this.sequenceContext = new SequencedGearshiftBlockEntity.SequenceContext(SequencerInstructions.TURN_ANGLE, (double)relativeValue);
        this.sequencedAngleLimit = Math.abs(relativeAngle);
        this.logicalSpeed = rotationSpeed;
        Direction facing = (Direction)this.getBlockState().getValue((Property)SteeringWheelBlock.FACING);
        boolean floor = (Boolean)this.getBlockState().getValue((Property)SteeringWheelBlock.ON_FLOOR);
        this.generatedSpeed = (facing == Direction.NORTH || facing == Direction.WEST) == floor ? -this.logicalSpeed : this.logicalSpeed;
        this.updateGeneratedRotation();
    }

    protected void copySequenceContextFrom(KineticBlockEntity sourceBE) {
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.oldClientAngle = this.clientAngle;
            this.clientAngle = SimClickInteractions.STEERING_WHEEL_MANAGER.isBlockActive(this.getBlockPos()) ? this.targetAngleToUpdate : (this.clientAngle += (this.targetAngleToUpdate - this.clientAngle) * 0.25f);
        }
        if (this.getGeneratedSpeed() != 0.0f) {
            this.integrateAngle();
        }
        if (this.inUse > 0) {
            --this.inUse;
            if (this.inUse == 0 && !this.level.isClientSide) {
                this.sequenceContext = null;
                this.generatedSpeed = 0.0f;
                this.updateGeneratedRotation();
            }
        } else if (!this.level.isClientSide) {
            this.updateTargetAngle(this.targetAngleToUpdate);
        }
    }

    private void integrateAngle() {
        float angularSpeed = this.getAngularSpeed();
        if (this.sequencedAngleLimit >= 0.0) {
            angularSpeed = (float)Mth.clamp((double)angularSpeed, (double)(-this.sequencedAngleLimit), (double)this.sequencedAngleLimit);
            this.sequencedAngleLimit = Math.max(0.0, this.sequencedAngleLimit - (double)Math.abs(angularSpeed));
        }
        this.angle += angularSpeed;
    }

    public float getAngle() {
        return this.angle;
    }

    public float getAngularSpeed() {
        float speed = SteeringWheelBlockEntity.convertToAngular((float)this.getLogicalSpeed());
        if (this.getSpeed() == 0.0f || this.getLogicalSpeed() == 0.0f) {
            speed = 0.0f;
        }
        return speed;
    }

    public float getLogicalSpeed() {
        return this.inUse == 0 ? 0.0f : this.logicalSpeed;
    }

    public float getGeneratedSpeed() {
        return this.inUse == 0 ? 0.0f : this.generatedSpeed;
    }

    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("Angle", this.angle);
        compound.putFloat("TargetAngle", this.targetAngle);
        if (this.targetAngleToUpdate != this.targetAngle) {
            compound.putFloat("TargetAngleToUpdate", this.targetAngleToUpdate);
        }
        if (clientPacket) {
            compound.putBoolean("Held", this.held);
        }
        compound.putInt("InUse", this.inUse);
        compound.putDouble("SequencedAngleLimit", this.sequencedAngleLimit);
        compound.putFloat("GeneratedSpeed", this.generatedSpeed);
        compound.put("Material", (Tag)NbtUtils.writeBlockState((BlockState)this.material));
    }

    public void writeSafe(CompoundTag compound, HolderLookup.Provider registries) {
        super.writeSafe(compound, registries);
        compound.put("Material", (Tag)NbtUtils.writeBlockState((BlockState)this.material));
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.angle = compound.getFloat("Angle");
        if (clientPacket) {
            this.held = compound.getBoolean("Held");
        }
        if (!clientPacket || !SimClickInteractions.STEERING_WHEEL_MANAGER.isBlockActive(this.getBlockPos())) {
            this.targetAngle = compound.getFloat("TargetAngle");
            this.targetAngleToUpdate = compound.contains("TargetAngleToUpdate") ? compound.getFloat("TargetAngleToUpdate") : this.targetAngle;
        }
        this.inUse = compound.getInt("InUse");
        this.sequencedAngleLimit = compound.getDouble("SequencedAngleLimit");
        this.generatedSpeed = compound.getFloat("GeneratedSpeed");
        BlockState prevMaterial = this.material;
        if (!compound.contains("Material")) {
            return;
        }
        this.material = NbtUtils.readBlockState((HolderGetter)this.blockHolderGetter(), (CompoundTag)compound.getCompound("Material"));
        if (this.material.isAir()) {
            this.material = Blocks.SPRUCE_PLANKS.defaultBlockState();
        }
        if (clientPacket && prevMaterial != this.material) {
            this.redraw();
        }
    }

    public boolean shouldRenderShaft() {
        return true;
    }

    protected Block getStressConfigKey() {
        return (Block)SimBlocks.STEERING_WHEEL.get();
    }

    public float getRenderAngle(float partialTicks) {
        float renderAngle = Mth.lerp((float)partialTicks, (float)this.oldClientAngle, (float)this.clientAngle);
        Direction facing = (Direction)this.getBlockState().getValue((Property)SteeringWheelBlock.FACING);
        if (facing == Direction.NORTH || facing == Direction.WEST) {
            return (float)Math.toRadians(-renderAngle);
        }
        return (float)Math.toRadians(renderAngle);
    }

    public float getInteractionAngle(float partialTicks) {
        return Mth.lerp((float)partialTicks, (float)this.oldClientAngle, (float)this.clientAngle);
    }

    public AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(0.4);
    }

    public void tickAudio() {
        int playingIncrement;
        super.tickAudio();
        float renderAngle = this.getRenderAngle(0.0f);
        if ((double)Math.abs(Math.abs(this.angle) - (float)this.angleInput.getValue()) < 0.01) {
            renderAngle += (float)((double)Math.signum(this.angle) * 0.01);
        }
        if (this.lastPlayedIncrement != (playingIncrement = (int)Math.floor(Math.toDegrees(renderAngle) / 45.0))) {
            int spokeCrossed = playingIncrement;
            if (this.lastPlayedIncrement - playingIncrement > 0) {
                ++spokeCrossed;
            }
            if ((float)spokeCrossed != Math.signum(this.lastPlayedIncrement - playingIncrement) * 4.0f) {
                switch (spokeCrossed) {
                    case -4: 
                    case 4: {
                        AllSoundEvents.CRANKING.playAt(this.level, (Vec3i)this.worldPosition, 1.25f, 0.85f, true);
                        break;
                    }
                    case 0: {
                        AllSoundEvents.CRANKING.playAt(this.level, (Vec3i)this.worldPosition, 1.25f, 0.5f, true);
                        break;
                    }
                    default: {
                        AllSoundEvents.CRANKING.playAt(this.level, (Vec3i)this.worldPosition, 0.5f, 1.25f, true);
                    }
                }
            }
            this.lastPlayedIncrement = playingIncrement;
        }
    }

    private void redraw() {
        if (!this.isVirtual()) {
            this.requestModelDataUpdate();
        }
        if (this.hasLevel()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 16);
            this.level.getChunkSource().getLightEngine().checkBlock(this.worldPosition);
        }
    }

    public boolean isMaterialValid(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof BlockItem)) {
            return false;
        }
        BlockItem blockItem = (BlockItem)item;
        BlockState material = blockItem.getBlock().defaultBlockState();
        if (material == this.material) {
            return false;
        }
        return material.is(BlockTags.PLANKS);
    }

    public ItemInteractionResult applyMaterialIfValid(ItemStack stack) {
        Item item;
        if (this.isMaterialValid(stack) && (item = stack.getItem()) instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            if (this.level.isClientSide() && !this.isVirtual()) {
                return ItemInteractionResult.SUCCESS;
            }
            this.material = blockItem.getBlock().defaultBlockState();
            this.notifyUpdate();
            this.level.levelEvent(2001, this.worldPosition, Block.getId((BlockState)this.material));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static class SteeringWheelScrollValueBehaviour
    extends ScrollValueBehaviour {
        public SteeringWheelScrollValueBehaviour(SmartBlockEntity be) {
            super((Component)SimLang.translate("torsion_spring.angle_limit", new Object[0]).component(), be, (ValueBoxTransform)new SteeringWheelValueBoxTransform());
            this.withFormatter(v -> Math.abs(v) + CreateLang.translateDirect((String)"generic.unit.degrees", (Object[])new Object[0]).getString());
        }

        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            return new ValueSettingsBoard(this.label, 360, 45, (List)ImmutableList.of((Object)Component.literal((String)"\u27f3").withStyle(ChatFormatting.BOLD)), new ValueSettingsFormatter(this::formatValue));
        }

        public MutableComponent formatValue(ValueSettingsBehaviour.ValueSettings settings) {
            return SimLang.number(Math.max(1, Math.abs(settings.value()))).add(CreateLang.translateDirect((String)"generic.unit.degrees", (Object[])new Object[0])).component();
        }
    }

    private static class SteeringWheelValueBoxTransform
    extends ValueBoxTransform.Sided {
        private SteeringWheelValueBoxTransform() {
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction == ((Boolean)state.getValue((Property)SteeringWheelBlock.ON_FLOOR) != false ? Direction.UP : Direction.DOWN);
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.5);
        }

        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
            Direction facing = (Direction)state.getValue((Property)HorizontalDirectionalBlock.FACING);
            TransformStack.of((PoseStack)ms).rotateZDegrees(-AngleHelper.horizontalAngle((Direction)facing) + 180.0f);
        }
    }
}

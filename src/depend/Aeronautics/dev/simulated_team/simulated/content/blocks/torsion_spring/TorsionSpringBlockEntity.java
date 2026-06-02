/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.IRotate
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
 *  com.simibubi.create.infrastructure.config.AllConfigs
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.torsion_spring;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
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
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.torsion_spring.TorsionSpringBlock;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.mixin_interface.extra_kinetics.KineticBlockEntityExtension;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraBlockPos;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TorsionSpringBlockEntity
extends KineticBlockEntity
implements ExtraKinetics {
    private final Output springOutput;
    public ScrollValueBehaviour angleInput;
    protected double sequencedAngleLimit;

    public TorsionSpringBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.springOutput = new Output(blockEntityType, new ExtraBlockPos((Vec3i)blockPos), blockState, this);
        this.sequencedAngleLimit = -1.0;
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    public boolean isSpringStatic() {
        return this.springOutput.angle == this.springOutput.oldAngle;
    }

    public float interpolatedSpring(float pt) {
        return (float)(this.springOutput.oldAngle + (this.springOutput.angle - this.springOutput.oldAngle) * (double)pt);
    }

    public float getAngle() {
        return (float)this.springOutput.angle;
    }

    public void setAngle(float angle) {
        this.springOutput.angle = angle;
    }

    public void onSignalChanged() {
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.angleInput = new TorsionSpringScrollValueBehaviour((SmartBlockEntity)this).between(1, 360);
        behaviours.add((BlockEntityBehaviour)this.angleInput);
        this.angleInput.onlyActiveWhen(this::showValue);
        this.angleInput.setValue(90);
    }

    public boolean showValue() {
        return true;
    }

    public void tick() {
        super.tick();
        this.springOutput.tick();
    }

    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        this.sequencedAngleLimit = -1.0;
        if (this.sequenceContext != null && this.sequenceContext.instruction() == SequencerInstructions.TURN_ANGLE) {
            this.sequencedAngleLimit = this.sequenceContext.getEffectiveValue((double)this.getTheoreticalSpeed());
        }
        this.springOutput.updateParentSpeed(previousSpeed, this.getSpeed());
    }

    protected void copySequenceContextFrom(KineticBlockEntity sourceBE) {
        super.copySequenceContextFrom(sourceBE);
    }

    public float calculateAddedStressCapacity() {
        return 0.0f;
    }

    @Override
    public String getExtraKineticsSaveName() {
        return "TorsionSpringOutput";
    }

    @Override
    public KineticBlockEntity getExtraKinetics() {
        return this.springOutput;
    }

    @Override
    public boolean shouldConnectExtraKinetics() {
        return false;
    }

    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        if (this.sequencedAngleLimit >= 0.0) {
            compound.putDouble("SequencedAngleLimit", this.sequencedAngleLimit);
        }
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.sequencedAngleLimit = compound.contains("SequencedAngleLimit") ? compound.getDouble("SequencedAngleLimit") : -1.0;
    }

    public static class Output
    extends GeneratingKineticBlockEntity
    implements ExtraKinetics.ExtraKineticsBlockEntity {
        public static final IRotate CONFIG = new IRotate(){

            public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
                return face == state.getValue((Property)TorsionSpringBlock.FACING);
            }

            public Direction.Axis getRotationAxis(BlockState state) {
                return ((Direction)state.getValue((Property)TorsionSpringBlock.FACING)).getAxis();
            }
        };
        private final TorsionSpringBlockEntity parent;
        protected double oldAngle = 0.0;
        protected double angle = 0.0;
        private int rotationDurationTicks = 0;
        private int rotationProgressTicks = 0;
        private double sequencedAngleLimit = -1.0;
        private float lastSpringSpeed = 0.0f;
        private float generatedSpeed;
        private double targetAngle = 0.0;
        private State currentState = State.STOPPED;
        private float queuedSpeed;
        private int customValidationCountdown;

        public Output(BlockEntityType<?> type, ExtraBlockPos pos, BlockState state, TorsionSpringBlockEntity parentBlockEntity) {
            super(type, (BlockPos)pos, state);
            this.parent = parentBlockEntity;
        }

        @Override
        public Component getKey() {
            return SimLang.translate("extra_kinetics.torsion_output", new Object[0]).component();
        }

        public void initialize() {
            super.initialize();
            this.reActivateSource = true;
            this.updateSpeed = true;
        }

        public void tick() {
            boolean parentStopped;
            ((KineticBlockEntityExtension)((Object)this)).simulated$setValidationCountdown(Integer.MAX_VALUE);
            if (this.customValidationCountdown-- <= 0) {
                this.customValidationCountdown = (Integer)AllConfigs.server().kinetics.kineticValidationFrequency.get();
                this.customValidateKinetics();
            }
            this.generatedSpeed = this.queuedSpeed;
            super.tick();
            this.oldAngle = this.angle;
            if (this.rotationDurationTicks >= 0 && this.rotationProgressTicks <= this.rotationDurationTicks) {
                ++this.rotationProgressTicks;
                float angularSpeed = KineticBlockEntity.convertToAngular((float)this.speed);
                if (this.sequencedAngleLimit >= 0.0) {
                    angularSpeed = (float)Mth.clamp((double)angularSpeed, (double)(-this.sequencedAngleLimit), (double)this.sequencedAngleLimit);
                }
                if (this.sequencedAngleLimit >= 0.0) {
                    this.sequencedAngleLimit = Math.max(0.0, this.sequencedAngleLimit - (double)Math.abs(angularSpeed));
                }
                this.angle += (double)angularSpeed;
                this.level.updateNeighborsAt(this.getBlockPos(), this.getParentBlockEntity().getBlockState().getBlock());
                if (this.rotationProgressTicks == this.rotationDurationTicks) {
                    this.sequenceContext = null;
                    this.rotationProgressTicks = -1;
                    this.rotationDurationTicks = -1;
                    this.queuedSpeed = 0.0f;
                    this.reActivateSource = true;
                    this.updateSpeed = true;
                    this.currentState = State.STOPPED;
                }
            }
            boolean powered = (Boolean)this.getBlockState().getValue((Property)TorsionSpringBlock.POWERED);
            boolean bl = parentStopped = this.parent.getSpeed() == 0.0f;
            if (this.currentState == State.TURNING && parentStopped) {
                if (this.targetAngle != 0.0 || powered) {
                    this.stopTurning();
                }
            } else if (this.currentState == State.STOPPED && parentStopped && !powered) {
                if (this.targetAngle != 0.0) {
                    this.beginTurnTo(0.0);
                    SimAdvancements.REWIND_TIME.awardToNearby(this.parent.getBlockPos(), this.parent.getLevel());
                }
            } else if (this.currentState == State.TURNING) {
                double targetAngle = (float)this.parent.angleInput.getValue() * Math.signum(this.parent.getSpeed());
                if (this.targetAngle != targetAngle || this.lastSpringSpeed != this.generatedSpeed) {
                    this.stopTurning();
                }
            } else if (!parentStopped && this.currentState == State.STOPPED) {
                double targetAngle = (float)this.parent.angleInput.getValue() * Math.signum(this.lastSpringSpeed);
                this.beginTurnTo(targetAngle);
            }
        }

        private void customValidateKinetics() {
            if (this.hasSource()) {
                KineticBlockEntity sourceBE;
                if (!this.hasNetwork()) {
                    this.removeSource();
                    return;
                }
                if (!this.level.isLoaded(this.source)) {
                    return;
                }
                BlockEntity blockEntity = this.level.getBlockEntity(this.source);
                if (blockEntity instanceof ExtraKinetics) {
                    ExtraKinetics ek = (ExtraKinetics)blockEntity;
                    if (((KineticBlockEntityExtension)((Object)this)).simulated$getConnectedToExtraKinetics()) {
                        blockEntity = ek.getExtraKinetics();
                    }
                }
                KineticBlockEntity kineticBlockEntity = sourceBE = blockEntity instanceof KineticBlockEntity ? (KineticBlockEntity)blockEntity : null;
                if (sourceBE == null || sourceBE.getTheoreticalSpeed() == 0.0f) {
                    this.removeSource();
                    this.detachKinetics();
                }
            }
        }

        private void updateParentSpeed(float previousSpeed, float newParentSpeed) {
            if (newParentSpeed != 0.0f) {
                this.lastSpringSpeed = newParentSpeed;
            } else if (previousSpeed != 0.0f) {
                this.lastSpringSpeed = previousSpeed;
            }
        }

        private void stopTurning() {
            this.sequenceContext = null;
            this.rotationProgressTicks = -1;
            this.rotationDurationTicks = -1;
            this.sequencedAngleLimit = -1.0;
            this.targetAngle = Double.MAX_VALUE;
            this.reActivateSource = true;
            this.updateSpeed = true;
            this.queuedSpeed = 0.0f;
            this.currentState = State.STOPPED;
        }

        private void beginTurnTo(double targetAngle) {
            double relativeAngle = targetAngle - this.angle;
            if (relativeAngle == 0.0) {
                return;
            }
            if (this.currentState == State.TURNING && this.targetAngle == targetAngle) {
                return;
            }
            this.lastSpringSpeed = (float)((double)Math.abs(this.lastSpringSpeed) * Math.signum(relativeAngle));
            if (this.parent.sequencedAngleLimit >= 0.0) {
                relativeAngle = (float)Mth.clamp((double)relativeAngle, (double)(-this.parent.sequencedAngleLimit), (double)this.parent.sequencedAngleLimit);
            }
            this.detachKinetics();
            this.targetAngle = targetAngle;
            this.sequenceContext = new SequencedGearshiftBlockEntity.SequenceContext(SequencerInstructions.TURN_ANGLE, relativeAngle / (double)this.lastSpringSpeed);
            double degreesPerTick = KineticBlockEntity.convertToAngular((float)Math.abs(this.lastSpringSpeed));
            this.rotationDurationTicks = (int)Math.ceil(Math.abs(relativeAngle) / degreesPerTick) + 2;
            this.rotationProgressTicks = 0;
            this.sequencedAngleLimit = this.sequenceContext.getEffectiveValue((double)this.lastSpringSpeed);
            this.currentState = State.TURNING;
            this.generatedSpeed = this.queuedSpeed = this.lastSpringSpeed;
            this.reActivateSource = true;
            this.updateSpeed = true;
        }

        public float getGeneratedSpeed() {
            return this.generatedSpeed;
        }

        public float calculateStressApplied() {
            return 0.0f;
        }

        protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
            super.write(compound, registries, clientPacket);
            compound.putDouble("OldAngle", this.oldAngle);
            compound.putDouble("Angle", this.angle);
            compound.putDouble("TargetAngle", this.targetAngle);
            compound.putFloat("LastSpringSpeed", this.lastSpringSpeed);
            compound.putInt("CurrentState", this.currentState.ordinal());
            compound.putInt("RotationProgressTicks", this.rotationProgressTicks);
            compound.putInt("RotationDurationTicks", this.rotationDurationTicks);
            compound.putFloat("GeneratedSpeed", this.generatedSpeed);
            compound.putFloat("QueuedSpeed", this.queuedSpeed);
            if (this.sequencedAngleLimit >= 0.0) {
                compound.putDouble("SequencedAngleLimit", this.sequencedAngleLimit);
            }
        }

        protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
            super.read(compound, registries, clientPacket);
            this.oldAngle = compound.getDouble("OldAngle");
            this.angle = compound.getDouble("Angle");
            this.targetAngle = compound.getDouble("TargetAngle");
            this.lastSpringSpeed = compound.getFloat("LastSpringSpeed");
            this.sequencedAngleLimit = compound.contains("SequencedAngleLimit") ? compound.getDouble("SequencedAngleLimit") : -1.0;
            this.rotationProgressTicks = compound.getInt("RotationProgressTicks");
            this.rotationDurationTicks = compound.getInt("RotationDurationTicks");
            this.generatedSpeed = compound.getFloat("GeneratedSpeed");
            this.queuedSpeed = compound.getFloat("QueuedSpeed");
            if (compound.contains("CurrentState")) {
                this.currentState = State.values()[compound.getInt("CurrentState")];
            }
        }

        @Override
        public KineticBlockEntity getParentBlockEntity() {
            return this.parent;
        }

        private static enum State {
            STOPPED,
            TURNING;

        }
    }

    public static class TorsionSpringScrollValueBehaviour
    extends ScrollValueBehaviour {
        public TorsionSpringScrollValueBehaviour(SmartBlockEntity be) {
            super((Component)SimLang.translate("torsion_spring.angle_limit", new Object[0]).component(), be, (ValueBoxTransform)new TorsionSpringValueBox());
            this.withFormatter(v -> Math.max(1, v) + CreateLang.translateDirect((String)"generic.unit.degrees", (Object[])new Object[0]).getString());
        }

        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            return new ValueSettingsBoard(this.label, 360, 45, (List)ImmutableList.of((Object)Component.literal((String)"\u27f3").withStyle(ChatFormatting.BOLD)), new ValueSettingsFormatter(this::formatValue));
        }

        public MutableComponent formatValue(ValueSettingsBehaviour.ValueSettings settings) {
            return SimLang.number(Math.max(1, settings.value())).add(CreateLang.translateDirect((String)"generic.unit.degrees", (Object[])new Object[0])).component();
        }
    }

    public static class TorsionSpringValueBox
    extends ValueBoxTransform.Sided {
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.5);
        }

        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            return super.getLocalOffset(level, pos, state).add(Vec3.atLowerCornerOf((Vec3i)((Direction)state.getValue((Property)TorsionSpringBlock.FACING)).getNormal()).scale(-0.3125));
        }

        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            if (!this.getSide().getAxis().isHorizontal()) {
                TransformStack.of((PoseStack)ms).rotateY((AngleHelper.horizontalAngle((Direction)((Direction)state.getValue((Property)TorsionSpringBlock.FACING))) + 180.0f) * (float)Math.PI / 180.0f);
            }
            super.rotate(level, pos, state, ms);
        }

        public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
            Vec3 offset = this.getLocalOffset(level, pos, state);
            if (offset == null) {
                return false;
            }
            return localHit.distanceTo(offset) < (double)(this.scale / 1.5f);
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis() != ((Direction)state.getValue((Property)TorsionSpringBlock.FACING)).getAxis();
        }
    }
}

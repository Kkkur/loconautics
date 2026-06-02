/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.AssemblyException
 *  com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions
 *  com.simibubi.create.content.contraptions.bearing.BearingBlock
 *  com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.simpleRelays.ICogWheel
 *  com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour
 *  com.simibubi.create.foundation.gui.AllIcons
 *  com.simibubi.create.foundation.item.TooltipHelper
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.SubLevelAssemblyHelper
 *  dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor
 *  dev.ryanhcode.sable.api.physics.PhysicsPipeline
 *  dev.ryanhcode.sable.api.physics.PhysicsPipelineBody
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.rotary.RotaryConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.rotary.RotaryConstraintHandle
 *  dev.ryanhcode.sable.api.schematic.SubLevelSchematicSerializationContext
 *  dev.ryanhcode.sable.api.schematic.SubLevelSchematicSerializationContext$SchematicMapping
 *  dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.swivel_bearing;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.item.TooltipHelper;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.PhysicsPipelineBody;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.rotary.RotaryConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.rotary.RotaryConstraintHandle;
import dev.ryanhcode.sable.api.schematic.SubLevelSchematicSerializationContext;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.config.server.physics.SimPhysics;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlock;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.link_block.SwivelBearingPlateBlock;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.link_block.SwivelBearingPlateBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimAssemblyHelper;
import dev.simulated_team.simulated.util.SimLevelUtil;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraBlockPos;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SwivelBearingBlockEntity
extends KineticBlockEntity
implements ExtraKinetics,
IDisplayAssemblyExceptions,
BlockEntitySubLevelActor {
    private static final MutableComponent SCROLL_OPTION_TITLE = Component.translatable((String)"simulated.scroll_option.swivel_default_locked");
    @NotNull
    private final SwivelBearingCogwheelBlockEntity cogwheel;
    public boolean assembleNextTick = false;
    protected AssemblyException lastException;
    private double lastTargetAngleDegrees = 0.0;
    private double targetAngleDegrees = 0.0;
    private double sequencedAngleLimit = -1.0;
    @Nullable
    private UUID subLevelID;
    @Nullable
    private BlockPos swivelPlatePos;
    @Nullable
    private RotaryConstraintHandle handle;
    private boolean assembling;
    private ScrollOptionBehaviour<LockingSetting> lockedDefaultOption;

    public SwivelBearingBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.cogwheel = new SwivelBearingCogwheelBlockEntity(typeIn, pos, state, this);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.lockedDefaultOption = new ScrollOptionBehaviour(LockingSetting.class, (Component)SCROLL_OPTION_TITLE, (SmartBlockEntity)this, (ValueBoxTransform)new SelectionModeValueBox(this::isValidForOptionPanel));
        this.lockedDefaultOption.value = 1;
        behaviours.add((BlockEntityBehaviour)this.lockedDefaultOption);
    }

    private boolean isValidForOptionPanel(BlockState state, Direction direction) {
        Direction facing = (Direction)state.getValue((Property)SwivelBearingBlock.FACING);
        Direction.Axis currentAxis = facing.getAxis();
        return direction.getAxis() != currentAxis;
    }

    public void tick() {
        Level level = this.getLevel();
        super.tick();
        this.cogwheel.tick();
        if (level.isClientSide) {
            if (this.isTooFast()) {
                this.playGrindingEffect();
            }
            return;
        }
        if (this.assembleNextTick) {
            if (!this.isAssembled()) {
                this.assemble();
            } else {
                this.disassemble();
            }
        }
        SubLevel attached = this.getAttachedSubLevel();
        int bestSignal = this.level.getBestNeighborSignal(this.getBlockPos());
        boolean shouldLock = ((LockingSetting)this.lockedDefaultOption.get()).shouldLock(bestSignal);
        if (shouldLock && !this.isLocking()) {
            BlockState plateBlock;
            this.level.setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue((Property)BlockStateProperties.POWERED, (Comparable)Boolean.valueOf(true)));
            if (this.handle != null && attached != null) {
                this.reattachConstraint(attached, false);
            }
            if (attached != null && this.getPlatePos() != null && (plateBlock = this.level.getBlockState(this.getPlatePos())).is(SimBlocks.SWIVEL_BEARING_LINK_BLOCK)) {
                this.setTargetAngleFromCurrentOrientation(plateBlock, attached);
            }
        } else if (!shouldLock && this.isLocking()) {
            this.level.setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue((Property)BlockStateProperties.POWERED, (Comparable)Boolean.valueOf(false)));
            if (this.handle != null && attached != null) {
                this.reattachConstraint(attached, false);
            }
        }
        if (this.getSubLevelID() != null) {
            this.checkPersistence(this.getSubLevelID());
        }
        this.lastTargetAngleDegrees = this.targetAngleDegrees;
        float angularSpeed = SwivelBearingBlockEntity.convertToAngular((float)this.limitCogSpeed(this.cogwheel.getSpeed()));
        boolean shouldUpdateAngle = true;
        if (this.sequencedAngleLimit >= 0.0) {
            angularSpeed = (float)Mth.clamp((double)angularSpeed, (double)(-this.sequencedAngleLimit), (double)this.sequencedAngleLimit);
            this.sequencedAngleLimit = Math.max(0.0, this.sequencedAngleLimit - (double)Math.abs(angularSpeed));
        } else {
            SubLevelPhysicsSystem physicsSystem = SubLevelPhysicsSystem.get((Level)this.level);
            if (physicsSystem == null || physicsSystem.getPaused()) {
                shouldUpdateAngle = false;
            }
        }
        if (shouldUpdateAngle) {
            if (((Direction)this.getBlockState().getValue((Property)SwivelBearingBlock.FACING)).getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
                angularSpeed *= -1.0f;
            }
            this.targetAngleDegrees += (double)angularSpeed;
            this.targetAngleDegrees %= 360.0;
            if (attached != null && this.isAssembled() && this.handle != null) {
                SubLevel containing = this.getContainingSubLevel();
                if ((double)angularSpeed != 0.0) {
                    ServerSubLevel serverSubLevel;
                    PhysicsPipeline pipeline = ((ServerSubLevelContainer)SubLevelContainer.getContainer((Level)this.level)).physicsSystem().getPipeline();
                    if (containing instanceof ServerSubLevel) {
                        serverSubLevel = (ServerSubLevel)containing;
                        pipeline.wakeUp((PhysicsPipelineBody)serverSubLevel);
                    }
                    if (attached instanceof ServerSubLevel) {
                        serverSubLevel = (ServerSubLevel)attached;
                        pipeline.wakeUp((PhysicsPipelineBody)serverSubLevel);
                    }
                }
            }
        }
        this.assembleNextTick = false;
    }

    private void playGrindingEffect() {
        Direction facing = (Direction)this.getBlockState().getValue((Property)SwivelBearingBlock.FACING);
        RandomSource random = this.level.random;
        int stepX = facing.getStepX();
        int stepY = facing.getStepY();
        int stepZ = facing.getStepZ();
        for (int i = 0; i < 2; ++i) {
            Vec3 particlePos = this.getBlockPos().getCenter().add((double)stepX * 7.0 / 16.0, (double)stepY * 7.0 / 16.0, (double)stepZ * 7.0 / 16.0).add((double)((random.nextFloat() - 0.5f) * (float)(stepX == 0 ? 1 : 0)), (double)((random.nextFloat() - 0.5f) * (float)(stepY == 0 ? 1 : 0)), (double)((random.nextFloat() - 0.5f) * (float)(stepZ == 0 ? 1 : 0)));
            this.level.addParticle((ParticleOptions)ParticleTypes.CRIT, particlePos.x, particlePos.y, particlePos.z, 0.0, 0.0, 0.0);
        }
    }

    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (super.addToTooltip(tooltip, isPlayerSneaking)) {
            return true;
        }
        if (isPlayerSneaking) {
            return false;
        }
        if (this.cogwheel.getSpeed() == 0.0f) {
            return false;
        }
        if (this.isAssembled()) {
            if (this.isTooFast()) {
                SimLang.translate("swivel_bearing.too_fast", new Object[0]).style(ChatFormatting.GOLD).forGoggles(tooltip);
                MutableComponent component = SimLang.translate("swivel_bearing.too_fast_error", new Object[0]).component();
                List cutString = TooltipHelper.cutTextComponent((Component)component, (FontHelper.Palette)FontHelper.Palette.GRAY_AND_WHITE);
                tooltip.addAll(cutString);
                return true;
            }
            return false;
        }
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof SwivelBearingBlock)) {
            return false;
        }
        BlockState attachedState = this.level.getBlockState(this.worldPosition.relative((Direction)state.getValue((Property)BearingBlock.FACING)));
        if (attachedState.canBeReplaced()) {
            return false;
        }
        TooltipHelper.addHint(tooltip, (String)"hint.empty_bearing", (Object[])new Object[0]);
        return true;
    }

    private boolean isTooFast() {
        float maxSwivelRPM = SimConfigService.INSTANCE.server().blocks.maxSwivelBearingSpeed.getF();
        return Math.abs(this.cogwheel.getSpeed()) > maxSwivelRPM;
    }

    private float limitCogSpeed(float speed) {
        float maxSwivelRPM = SimConfigService.INSTANCE.server().blocks.maxSwivelBearingSpeed.getF();
        return Mth.clamp((float)speed, (float)(-maxSwivelRPM), (float)maxSwivelRPM);
    }

    private void setTargetAngleFromCurrentOrientation(BlockState attachedState, SubLevel attached) {
        double currentAngle;
        assert (attached != null) : "Attached sub-level is null!";
        Quaterniond orientationA = new Quaterniond();
        Quaterniond blockOrientationA = new Quaterniond((Quaternionfc)((Direction)this.getBlockState().getValue((Property)SwivelBearingPlateBlock.FACING)).getRotation());
        Quaterniond blockOrientationB = new Quaterniond((Quaternionfc)((Direction)attachedState.getValue((Property)SwivelBearingPlateBlock.FACING)).getRotation());
        Quaterniond orientationB = new Quaterniond((Quaterniondc)attached.logicalPose().orientation());
        SubLevel containing = this.getContainingSubLevel();
        if (containing != null) {
            orientationA.set((Quaterniondc)containing.logicalPose().orientation());
        }
        Quaterniond localB = new Quaterniond((Quaterniondc)orientationA).mul((Quaterniondc)blockOrientationA).conjugate().mul((Quaterniondc)new Quaterniond((Quaterniondc)orientationB).mul((Quaterniondc)blockOrientationB));
        double d = new Vec3(0.0, 1.0, 0.0).dot(new Vec3(localB.x(), localB.y(), localB.z()));
        this.targetAngleDegrees = currentAngle = -2.0 * (double)((float)Math.toDegrees(Math.atan2(-d, localB.w())));
        this.lastTargetAngleDegrees = currentAngle;
    }

    public void updateServoCoefficients() {
        ServerSubLevel serverSubLevel;
        if (!this.isAssembled() || this.handle == null) {
            return;
        }
        SimPhysics config = SimConfigService.INSTANCE.server().physics;
        if (!this.isLocking()) {
            this.handle.setMotor(RotaryConstraintHandle.DEFAULT_AXIS, 0.0, 0.0, ((Double)config.swivelBearingFriction.get()).doubleValue(), false, 0.0);
            return;
        }
        SubLevel subLevelA = this.getContainingSubLevel();
        SubLevel subLevelB = this.getAttachedSubLevel();
        Vec3i facingVec3I = ((Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING)).getNormal();
        Vector3d facingVec = new Vector3d((double)facingVec3I.getX(), (double)facingVec3I.getY(), (double)facingVec3I.getZ());
        double inertiaA = Double.MAX_VALUE;
        double inertiaB = Double.MAX_VALUE;
        Vector3d temp = new Vector3d();
        if (subLevelA instanceof ServerSubLevel) {
            serverSubLevel = (ServerSubLevel)subLevelA;
            inertiaA = serverSubLevel.getMassTracker().getInertiaTensor().transform((Vector3dc)facingVec, temp).dot((Vector3dc)facingVec);
        }
        if (subLevelB instanceof ServerSubLevel) {
            serverSubLevel = (ServerSubLevel)subLevelB;
            inertiaB = serverSubLevel.getMassTracker().getInertiaTensor().transform((Vector3dc)facingVec, temp).dot((Vector3dc)facingVec);
        }
        double totalInertia = Math.max(10.0, subLevelA != null && subLevelB != null ? Math.max(inertiaA, inertiaB) : Math.min(inertiaA, inertiaB));
        SubLevelPhysicsSystem physicsSystem = ((ServerSubLevelContainer)SubLevelContainer.getContainer((Level)this.level)).physicsSystem();
        double kP = (Double)config.swivelBearingStiffness.get() * totalInertia;
        double kD = (Double)config.swivelBearingDamping.get() * totalInertia;
        float goal = AngleHelper.rad((double)AngleHelper.angleLerp((double)physicsSystem.getPartialPhysicsTick(), (double)this.lastTargetAngleDegrees, (double)this.targetAngleDegrees));
        this.handle.setMotor(RotaryConstraintHandle.DEFAULT_AXIS, (double)goal, kP, kD, false, 0.0);
        this.handle.setContactsEnabled(false);
    }

    public void assemble() {
        BlockEntity be;
        BlockPos assembleOffset;
        ServerSubLevel assembledSubLevel;
        SimAssemblyHelper.AssemblyResult result;
        BlockPos pos = this.getBlockPos();
        BlockPos toAssemble = pos.relative((Direction)this.getBlockState().getValue((Property)SwivelBearingBlock.FACING));
        try {
            result = SimAssemblyHelper.assembleFromSingleBlock(this.level, pos, toAssemble, false, false);
            this.lastException = null;
        }
        catch (AssemblyException e) {
            this.lastException = e;
            this.sendData();
            return;
        }
        this.sendData();
        BlockState link = (BlockState)SimBlocks.SWIVEL_BEARING_LINK_BLOCK.getDefaultState().setValue((Property)SwivelBearingPlateBlock.FACING, (Comparable)((Direction)this.getBlockState().getValue((Property)SwivelBearingBlock.FACING)));
        if (result != null) {
            assembledSubLevel = (ServerSubLevel)result.subLevel();
            assembleOffset = result.offset();
        } else {
            ServerSubLevelContainer container = (ServerSubLevelContainer)SubLevelContainer.getContainer((Level)this.level);
            Pose3d pose = new Pose3d();
            pose.position().set((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
            assembledSubLevel = (ServerSubLevel)container.allocateNewSubLevel(pose);
            ServerLevelPlot plot = assembledSubLevel.getPlot();
            ChunkPos center = plot.getCenterChunk();
            plot.newEmptyChunk(center);
            plot.getEmbeddedLevelAccessor().setBlock(BlockPos.ZERO, link, 3);
            BlockPos plotAnchor = plot.getCenterBlock();
            Vector3dc centerOfMass = assembledSubLevel.getMassTracker().getCenterOfMass();
            Vector3d subLevelCenter = JOMLConversion.atLowerCornerOf((Vec3i)pos);
            if (centerOfMass != null) {
                subLevelCenter.add(centerOfMass.x() - (double)plotAnchor.getX(), centerOfMass.y() - (double)plotAnchor.getY(), centerOfMass.z() - (double)plotAnchor.getZ());
            } else {
                assembledSubLevel.logicalPose().rotationPoint().set((double)plotAnchor.getX() + 0.5, (double)plotAnchor.getY() + 0.5, (double)plotAnchor.getZ() + 0.5);
            }
            assembledSubLevel.logicalPose().position().set(subLevelCenter.x, subLevelCenter.y, subLevelCenter.z);
            assembleOffset = plotAnchor.subtract((Vec3i)pos);
            SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
            PhysicsPipeline pipeline = physicsSystem.getPipeline();
            SubLevel containingSubLevel = this.getContainingSubLevel();
            if (containingSubLevel != null) {
                SubLevelAssemblyHelper.kickFromContainingSubLevel((ServerLevel)((ServerLevel)this.level), (SubLevelPhysicsSystem)physicsSystem, (PhysicsPipeline)pipeline, (ServerSubLevel)assembledSubLevel, (SubLevel)containingSubLevel);
            }
            pipeline.teleport((PhysicsPipelineBody)assembledSubLevel, (Vector3dc)assembledSubLevel.logicalPose().position(), (Quaterniondc)assembledSubLevel.logicalPose().orientation());
            assembledSubLevel.updateLastPose();
            this.level.playSound(null, pos, SimSoundEvents.SIMULATED_CONTRAPTION_MOVES.event(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        this.getLevel().setBlockAndUpdate(pos, (BlockState)this.getBlockState().setValue((Property)SwivelBearingBlock.ASSEMBLED, (Comparable)Boolean.valueOf(true)));
        this.attachConstraints((SubLevel)assembledSubLevel, this.getConstraintPos(toAssemble, assembleOffset));
        this.setSubLevelID(assembledSubLevel.getUniqueId());
        BlockPos plotPos = pos.offset((Vec3i)assembleOffset);
        if (result != null) {
            this.getLevel().setBlockAndUpdate(plotPos, link);
        }
        if ((be = this.getLevel().getBlockEntity(plotPos)) instanceof SwivelBearingPlateBlockEntity) {
            SwivelBearingPlateBlockEntity plateBE = (SwivelBearingPlateBlockEntity)be;
            plateBE.setParent(this);
            this.setPlatePos(plotPos);
        }
        SimAdvancements.YOU_SPIN_ME_RIGHT_ROUND.awardToNearby(pos, this.getLevel());
    }

    public void disassemble() {
        BlockPos platePos;
        SubLevel subLevel;
        if (this.isRemoved()) {
            return;
        }
        this.removeHandle();
        if (this.getSubLevelID() != null && (subLevel = SubLevelContainer.getContainer((Level)this.level).getSubLevel(this.getSubLevelID())) != null && (platePos = this.getPlatePos()) != null) {
            this.destroyPlate();
            if (!subLevel.isRemoved()) {
                SimAssemblyHelper.disassembleSubLevel(this.level, subLevel, platePos, this.getBlockPos(), Rotation.NONE, true);
            } else {
                this.level.playSound(null, platePos, SimSoundEvents.SIMULATED_CONTRAPTION_STOPS.event(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
        this.getLevel().setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue((Property)SwivelBearingBlock.ASSEMBLED, (Comparable)Boolean.valueOf(false)));
        this.setSubLevelID(null);
        this.setPlatePos(null);
        this.targetAngleDegrees = 0.0;
    }

    private void checkPersistence(UUID id) {
        if (this.getPlatePos() != null && SimLevelUtil.isAreaActuallyLoaded(this.getLevel(), this.getPlatePos(), 1) && !this.getLevel().getBlockState(this.getPlatePos()).is(SimBlocks.SWIVEL_BEARING_LINK_BLOCK)) {
            return;
        }
        SubLevel subLevel = SubLevelContainer.getContainer((Level)this.getLevel()).getSubLevel(id);
        if (this.handle != null && !this.handle.isValid()) {
            this.handle = null;
        }
        if (subLevel != null && this.handle == null) {
            this.reattachConstraint(subLevel, true);
        }
    }

    public void reattachConstraint(SubLevel toAttach, boolean updatePlate) {
        BlockPos platePos = this.getPlatePos();
        if (platePos != null) {
            BlockState plateState;
            if (this.handle != null) {
                this.handle.remove();
            }
            if (updatePlate) {
                this.associatePlateWithParent();
            }
            if (!(plateState = this.level.getBlockState(platePos)).is(SimBlocks.SWIVEL_BEARING_LINK_BLOCK)) {
                return;
            }
            Direction plateFacing = (Direction)plateState.getValue((Property)SwivelBearingPlateBlock.FACING);
            this.attachConstraints(toAttach, JOMLConversion.toJOML((Position)platePos.relative(plateFacing).getCenter()));
        }
    }

    public void associatePlateWithParent() {
        if (this.getPlatePos() != null && this.getLevel().getBlockState(this.getPlatePos()).is(SimBlocks.SWIVEL_BEARING_LINK_BLOCK)) {
            SwivelBearingPlateBlockEntity plate = (SwivelBearingPlateBlockEntity)this.getLevel().getBlockEntity(this.getPlatePos());
            plate.setParent(this);
        }
    }

    private void attachConstraints(SubLevel toAttach, Vector3d attachPos) {
        BlockPos platePos = this.getPlatePos();
        if (platePos == null) {
            return;
        }
        BlockState plateState = this.level.getBlockState(platePos);
        if (!plateState.is(SimBlocks.SWIVEL_BEARING_LINK_BLOCK)) {
            return;
        }
        Vector3d anchorPos = JOMLConversion.toJOML((Position)this.getBlockPos().relative((Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING)).getCenter());
        Vec3 facingVec = Vec3.atLowerCornerOf((Vec3i)((Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING)).getNormal());
        Vec3 plateFacingVec = Vec3.atLowerCornerOf((Vec3i)((Direction)plateState.getValue((Property)DirectionalKineticBlock.FACING)).getNormal());
        RotaryConstraintConfiguration constraint = new RotaryConstraintConfiguration((Vector3dc)anchorPos, (Vector3dc)attachPos.sub((Vector3dc)JOMLConversion.toJOML((Position)plateFacingVec.scale((double)0.001f))), (Vector3dc)JOMLConversion.toJOML((Position)facingVec), (Vector3dc)JOMLConversion.toJOML((Position)plateFacingVec));
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)((ServerLevel)this.getLevel()));
        PhysicsPipeline pipeline = container.physicsSystem().getPipeline();
        this.handle = (RotaryConstraintHandle)pipeline.addConstraint((ServerSubLevel)Sable.HELPER.getContaining((BlockEntity)this), (ServerSubLevel)toAttach, (PhysicsConstraintConfiguration)constraint);
    }

    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putDouble("TargetAngle", this.targetAngleDegrees);
        BlockPos platePos = this.getPlatePos();
        UUID id = this.getSubLevelID();
        SubLevelSchematicSerializationContext schematicContext = SubLevelSchematicSerializationContext.getCurrentContext();
        if (id != null && schematicContext != null) {
            SubLevelSchematicSerializationContext.SchematicMapping mapping = schematicContext.getMapping(id);
            if (mapping != null) {
                id = mapping.newUUID();
                platePos = (BlockPos)mapping.transform().apply((Object)platePos);
            } else {
                id = null;
                platePos = null;
            }
        }
        if (id != null) {
            compound.putUUID("SubLevelID", id);
        }
        if (platePos != null) {
            compound.put("SwivelPlate", NbtUtils.writeBlockPos((BlockPos)platePos));
        }
        if (this.sequencedAngleLimit >= 0.0) {
            compound.putDouble("SequencedAngleLimit", this.sequencedAngleLimit);
        }
        AssemblyException.write((CompoundTag)compound, (HolderLookup.Provider)registries, (AssemblyException)this.lastException);
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.targetAngleDegrees = compound.getDouble("TargetAngle");
        SubLevelSchematicSerializationContext schematicContext = SubLevelSchematicSerializationContext.getCurrentContext();
        SubLevelSchematicSerializationContext.SchematicMapping mapping = null;
        if (compound.hasUUID("SubLevelID")) {
            UUID subLevelID = compound.getUUID("SubLevelID");
            if (schematicContext != null) {
                mapping = schematicContext.getMapping(subLevelID);
            }
            if (mapping != null) {
                subLevelID = mapping.newUUID();
            }
            this.setSubLevelID(subLevelID);
        }
        if (compound.contains("SwivelPlate")) {
            BlockPos blockPos = (BlockPos)NbtUtils.readBlockPos((CompoundTag)compound, (String)"SwivelPlate").orElseThrow();
            this.setPlatePos(blockPos);
        }
        this.sequencedAngleLimit = compound.contains("SequencedAngleLimit") ? compound.getDouble("SequencedAngleLimit") : -1.0;
        this.lastException = AssemblyException.read((CompoundTag)compound, (HolderLookup.Provider)registries);
    }

    public void invalidate() {
        super.invalidate();
        this.removeHandle();
    }

    public void beforeAssembly() {
        this.assembling = true;
    }

    public void remove() {
        if (!this.level.isClientSide && !this.assembling) {
            this.destroyPlate();
        }
        super.remove();
    }

    public boolean isAssembled() {
        return (Boolean)this.getBlockState().getValue((Property)SwivelBearingBlock.ASSEMBLED);
    }

    @Nullable
    private SubLevel getAttachedSubLevel() {
        SubLevelContainer container = SubLevelContainer.getContainer((Level)this.level);
        return container.getSubLevel(this.subLevelID);
    }

    @Nullable
    private SubLevel getContainingSubLevel() {
        return Sable.HELPER.getContaining((BlockEntity)this);
    }

    private boolean isLocking() {
        return (Boolean)this.getBlockState().getValue((Property)BlockStateProperties.POWERED);
    }

    @NotNull
    private Vector3d getConstraintPos(BlockPos relative, BlockPos offset) {
        return JOMLConversion.toJOML((Position)relative.offset((Vec3i)offset).getCenter());
    }

    private void destroyPlate() {
        BlockPos platePos = this.getPlatePos();
        if (platePos != null) {
            SubLevelContainer container = SubLevelContainer.getContainer((Level)this.level);
            if (container == null) {
                return;
            }
            SubLevel subLevel = container.getSubLevel(this.subLevelID);
            if (subLevel == null) {
                return;
            }
            if (this.getLevel().getBlockState(platePos).is(SimBlocks.SWIVEL_BEARING_LINK_BLOCK)) {
                ((SwivelBearingPlateBlock)((Object)SimBlocks.SWIVEL_BEARING_LINK_BLOCK.get())).withBlockEntityDo((BlockGetter)this.level, platePos, SwivelBearingPlateBlockEntity::beforeAssembly);
                this.getLevel().setBlock(platePos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    private void removeHandle() {
        if (this.handle != null) {
            this.handle.remove();
            this.handle = null;
        }
    }

    public double getTargetAngleDegrees() {
        return this.targetAngleDegrees;
    }

    @Override
    @NotNull
    public KineticBlockEntity getExtraKinetics() {
        return this.cogwheel;
    }

    @Override
    public boolean shouldConnectExtraKinetics() {
        return false;
    }

    @Override
    public String getExtraKineticsSaveName() {
        return "SwivelCog";
    }

    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        return this.getPlatePos() != null && stateTo.getBlock() instanceof SwivelBearingPlateBlock ? 1.0f : super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
    }

    public boolean isCustomConnection(KineticBlockEntity other, BlockState state, BlockState otherState) {
        return this.getPlatePos() != null && otherState.getBlock() instanceof SwivelBearingPlateBlock;
    }

    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        if (this.getPlatePos() != null) {
            neighbours.add(this.getPlatePos());
        }
        return super.addPropagationLocations(block, state, neighbours);
    }

    @Nullable
    public BlockPos getPlatePos() {
        return this.swivelPlatePos;
    }

    public void setPlatePos(@Nullable BlockPos swivelPlatePos) {
        this.swivelPlatePos = swivelPlatePos;
    }

    @Nullable
    public UUID getSubLevelID() {
        return this.subLevelID;
    }

    public void setSubLevelID(@Nullable UUID subLevelID) {
        this.subLevelID = subLevelID;
    }

    public float calculateStressApplied() {
        return 0.0f;
    }

    public AssemblyException getLastAssemblyException() {
        return this.lastException;
    }

    public @Nullable Iterable<@NotNull SubLevel> sable$getConnectionDependencies() {
        SubLevel attachedSubLevel = this.getAttachedSubLevel();
        if (attachedSubLevel == null) {
            return null;
        }
        return List.of(attachedSubLevel);
    }

    public static class SwivelBearingCogwheelBlockEntity
    extends KineticBlockEntity
    implements ExtraKinetics.ExtraKineticsBlockEntity {
        public static final ICogWheel EXTRA_COGWHEEL_CONFIG = new ICogWheel(){

            public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
                return false;
            }

            public Direction.Axis getRotationAxis(BlockState state) {
                return ((Direction)state.getValue((Property)SwivelBearingBlock.FACING)).getAxis();
            }
        };
        private final SwivelBearingBlockEntity parent;

        public SwivelBearingCogwheelBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state, SwivelBearingBlockEntity parent) {
            super(typeIn, (BlockPos)new ExtraBlockPos((Vec3i)pos), state);
            this.parent = parent;
        }

        public void onSpeedChanged(float previousSpeed) {
            super.onSpeedChanged(previousSpeed);
            if ((double)this.speed != 0.0 && !this.parent.isAssembled()) {
                this.parent.assembleNextTick = true;
            }
            this.parent.sequencedAngleLimit = -1.0;
            if (this.sequenceContext != null && this.sequenceContext.instruction() == SequencerInstructions.TURN_ANGLE) {
                this.parent.sequencedAngleLimit = this.sequenceContext.getEffectiveValue((double)this.getTheoreticalSpeed());
            }
        }

        @Override
        public KineticBlockEntity getParentBlockEntity() {
            return this.parent;
        }

        protected void addStressImpactStats(List<Component> tooltip, float stressAtBase) {
            super.addStressImpactStats(tooltip, stressAtBase);
        }

        protected boolean canPropagateDiagonally(IRotate block, BlockState state) {
            return true;
        }

        @Override
        public Component getKey() {
            return SimLang.translate("extra_kinetics.extra_cogwheel", new Object[0]).component();
        }
    }

    public static enum LockingSetting implements INamedIconOptions
    {
        LOCKED_ALWAYS(AllIcons.I_CONFIG_LOCKED, "swivel_default_always_locked"),
        LOCKED_DEFAULT(AllIcons.I_CONFIG_LOCKED, "swivel_default_locked"),
        UNLOCKED_DEFAULT(AllIcons.I_CONFIG_UNLOCKED, "swivel_default_unlocked"),
        UNLOCKED_ALWAYS(AllIcons.I_CONFIG_UNLOCKED, "swivel_default_always_unlocked");

        private final String translationKey;
        private final AllIcons icon;

        private LockingSetting(AllIcons icon, String name) {
            this.icon = icon;
            this.translationKey = "simulated.generic." + name;
        }

        public AllIcons getIcon() {
            return this.icon;
        }

        public String getTranslationKey() {
            return this.translationKey;
        }

        public boolean shouldLock(int signal) {
            if (this == UNLOCKED_ALWAYS) {
                return false;
            }
            if (this == LOCKED_ALWAYS) {
                return true;
            }
            return signal > 0 != (this == LOCKED_DEFAULT);
        }
    }

    private static class SelectionModeValueBox
    extends CenteredSideValueBoxTransform {
        public SelectionModeValueBox(BiPredicate<BlockState, Direction> allowedDirections) {
            super(allowedDirections);
        }

        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            return super.getLocalOffset(level, pos, state).subtract(Vec3.atLowerCornerOf((Vec3i)((Direction)state.getValue((Property)SwivelBearingBlock.FACING)).getNormal()).scale(0.3125));
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.75);
        }

        public float getScale() {
            return 0.35f;
        }
    }
}

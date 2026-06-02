/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.AssemblyException
 *  com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.physics.PhysicsPipeline
 *  dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintHandle
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.api.physics.mass.MassData
 *  dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  foundry.veil.api.network.VeilPacketManager
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.physics_assembler;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintHandle;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.config.server.blocks.SimAssembly;
import dev.simulated_team.simulated.content.blocks.behaviour.HoldTipBehaviour;
import dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlock;
import dev.simulated_team.simulated.content.blocks.physics_assembler.assembly_preventer.DisassemblyPrevention;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffServerHandler;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.mixin_interface.assembly_preventer.PrimaryAssemblerExtension;
import dev.simulated_team.simulated.network.packets.physics_assembler.PhysicsAssemblerFailedPacket;
import dev.simulated_team.simulated.network.packets.physics_assembler.PhysicsAssemblerFlickAndHoldLeverPacket;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimAssemblyHelper;
import dev.simulated_team.simulated.util.SimMathUtils;
import dev.simulated_team.simulated.util.assembly.SimAssemblyException;
import foundry.veil.api.network.VeilPacketManager;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PhysicsAssemblerBlockEntity
extends SmartBlockEntity
implements IDisplayAssemblyExceptions {
    private static final float FLICKED_ANGLE_DEGREES = 45.0f;
    private static final double LEVER_CHASE_SPEED = 0.75;
    private static final double LINEAR_STIFFNESS = 1000.0;
    private static final double LINEAR_DAMPING = 50.0;
    private static final double ANGULAR_STIFFNESS = 13000.0;
    private static final double ANGULAR_DAMPING = 1000.0;
    private static final MutableComponent ASSEMBLE_TIP = SimLang.translate("gui.hold_tip.hold_to_assemble", new Object[0]).component();
    private static final MutableComponent DISASSEMBLE_TIP = SimLang.translate("gui.hold_tip.hold_to_disassemble", new Object[0]).component();
    protected AssemblyException lastException;
    protected boolean primaryAssembler;
    protected LerpedFloat visualAngle = LerpedFloat.linear();
    protected boolean holdingLever = false;
    private boolean leverInitialized = false;
    private boolean disassembling = false;
    private int disassemblingTicks = 0;
    private int disassemblyReadyTicks = 0;
    private int disassemblyAngle = 0;
    private Quaterniondc disassemblyOrientation;
    private boolean controlledByPlayer = false;
    private float playerAngle = 0.0f;
    @Nullable
    private FreeConstraintHandle alignmentConstraint;
    private HoldTipBehaviour holdTipBehaviour;

    public PhysicsAssemblerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.holdTipBehaviour = new HoldTipBehaviour((SmartBlockEntity)this, ASSEMBLE_TIP);
        behaviours.add(this.holdTipBehaviour);
    }

    public void initialize() {
        super.initialize();
        if (this.primaryAssembler) {
            this.setParent(this.level);
        }
        if (!this.isVirtual()) {
            this.initializeLeverPosition();
            this.holdTipBehaviour.setHoverTip(this.getSubLevel() != null ? DISASSEMBLE_TIP : ASSEMBLE_TIP);
        }
    }

    protected void initializeLeverPosition() {
        if (!this.leverInitialized) {
            this.clientFlickLeverTo(this.getSubLevel() != null);
            this.jerkLever();
            this.leverInitialized = true;
        }
    }

    @Nullable
    private SubLevel getSubLevel() {
        return Sable.HELPER.getContaining((BlockEntity)this);
    }

    public void tick() {
        super.tick();
        if (this.disassembling) {
            this.tickDisassembling();
        }
        if (this.holdingLever) {
            this.visualAngle.setValue((double)this.visualAngle.getValue());
        } else if (this.controlledByPlayer) {
            this.visualAngle.setValue((double)this.visualAngle.getValue());
            this.visualAngle.setValueNoUpdate((double)this.playerAngle);
        } else {
            this.visualAngle.tickChaser();
        }
    }

    private void tickDisassembling() {
        ++this.disassemblingTicks;
        SimAssembly config = SimConfigService.INSTANCE.server().assembly;
        if (this.disassemblingTicks >= (Integer)config.maxDisassemblyTicks.get() * 5) {
            this.assemblyFailed(SimAssemblyException.couldNotAlign());
            this.stopDisassembling();
            return;
        }
        SubLevel subLevel = this.getSubLevel();
        if (subLevel instanceof ServerSubLevel) {
            Pose3d pose = subLevel.logicalPose();
            double angle = pose.orientation().div(this.disassemblyOrientation, new Quaterniond()).angle();
            Vector3d current = pose.transformPosition(new Vector3d((Vector3dc)pose.rotationPoint()).floor().add(0.5, 0.5, 0.5));
            Vector3d goal = current.floor(new Vector3d()).add(0.5, 0.5, 0.5);
            Vector3d localGoal = this.disassemblyOrientation.transformInverse((Vector3dc)goal, new Vector3d());
            this.alignmentConstraint.setMotor(ConstraintJointAxis.LINEAR_X, localGoal.x, 1000.0, 50.0, false, 0.0);
            this.alignmentConstraint.setMotor(ConstraintJointAxis.LINEAR_Y, localGoal.y, 1000.0, 50.0, false, 0.0);
            this.alignmentConstraint.setMotor(ConstraintJointAxis.LINEAR_Z, localGoal.z, 1000.0, 50.0, false, 0.0);
            this.disassemblyReadyTicks = Math.toDegrees(Math.abs(angle)) <= (Double)config.disassemblyDegreeTolerance.get() && current.distance((Vector3dc)goal) < 0.2 ? ++this.disassemblyReadyTicks : 0;
            if (this.disassemblyReadyTicks > 5) {
                this.placeIntoWorld();
            }
        }
    }

    private void placeIntoWorld() {
        SubLevel subLevel = this.getSubLevel();
        assert (subLevel != null);
        try {
            this.throwDisassemblyExceptions((ServerSubLevel)subLevel);
        }
        catch (AssemblyException e) {
            this.assemblyFailed(e);
            this.stopDisassembling();
            return;
        }
        BlockPos goal = BlockPos.containing((Position)subLevel.logicalPose().transformPosition(Vec3.atCenterOf((Vec3i)this.getBlockPos())));
        Rotation rotation = SimAssemblyHelper.rotationFrom90DegRots(this.disassemblyAngle);
        SimAssemblyHelper.disassembleSubLevel(this.level, subLevel, this.getBlockPos(), goal, rotation, true);
        this.stopDisassembling();
    }

    private void throwDisassemblyExceptions(ServerSubLevel subLevel) throws AssemblyException {
        SimAssembly config;
        BoundingBox3dc bounds;
        block10: {
            block9: {
                bounds = subLevel.boundingBox();
                if (bounds.maxY() > (double)this.level.getMaxBuildHeight() || bounds.minY() < (double)this.level.getMinBuildHeight()) {
                    throw SimAssemblyException.outOfWorld();
                }
                config = SimConfigService.INSTANCE.server().assembly;
                RigidBodyHandle handle = RigidBodyHandle.of((ServerSubLevel)subLevel);
                if (handle.getLinearVelocity(new Vector3d()).lengthSquared() > (double)Mth.square((float)config.disassemblyMaxVelocity.getF())) break block9;
                Vector3d vector3d = new Vector3d();
                if (!(handle.getAngularVelocity(vector3d).lengthSquared() > (double)Mth.square((float)config.disassemblyMaxAngularVelocity.getF()))) break block10;
            }
            throw SimAssemblyException.tooFast();
        }
        BoundingBox3i chunkBounds = new BoundingBox3i((Mth.floor((double)bounds.minX()) >> 4) - 1, (Mth.floor((double)bounds.minY()) >> 4) - 1, (Mth.floor((double)bounds.minZ()) >> 4) - 1, (Mth.floor((double)bounds.maxX()) >> 4) + 1, (Mth.floor((double)bounds.maxY()) >> 4) + 1, (Mth.floor((double)bounds.maxZ()) >> 4) + 1);
        if (((Boolean)config.disallowMidAirDisassembly.get()).booleanValue()) {
            boolean nearGround = false;
            block0: for (int x = chunkBounds.minX(); x <= chunkBounds.maxX(); ++x) {
                for (int z = chunkBounds.minZ(); z <= chunkBounds.maxZ(); ++z) {
                    LevelChunk chunk = this.level.getChunk(x, z);
                    for (int y = chunkBounds.minY(); y <= chunkBounds.maxY(); ++y) {
                        int index = chunk.getSectionIndexFromSectionY(y);
                        if (index < 0 || index >= chunk.getSectionsCount() || chunk.getSection(index).hasOnlyAir()) continue;
                        nearGround = true;
                        break block0;
                    }
                }
            }
            if (!nearGround) {
                throw SimAssemblyException.tooFarFromGround();
            }
        }
    }

    private void stopDisassembling() {
        if (this.alignmentConstraint != null && this.alignmentConstraint.isValid()) {
            this.alignmentConstraint.remove();
            this.alignmentConstraint = null;
        }
        this.disassemblingTicks = 0;
        this.disassembling = false;
    }

    public void setClientHoldLeverInPlace(boolean holding) {
        this.holdingLever = holding;
    }

    public void updateControlledByPlayer(float angle) {
        if (!this.controlledByPlayer) {
            this.controlledByPlayer = true;
        }
        this.playerAngle = angle;
    }

    public boolean stopControllingPlayer() {
        if (!this.controlledByPlayer) {
            return false;
        }
        this.controlledByPlayer = false;
        return true;
    }

    public void clientFlickLeverTo(boolean flicked) {
        this.visualAngle.chase(flicked ? 45.0 : 0.0, 0.75, LerpedFloat.Chaser.EXP);
    }

    public void jerkLever() {
        this.visualAngle.setValue((double)this.visualAngle.getChaseTarget());
        this.visualAngle.setValue((double)this.visualAngle.getChaseTarget());
    }

    public void assembleOrDisassemble() {
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
        Level level = this.getLevel();
        assert (level != null);
        try {
            VeilPacketManager.tracking((BlockEntity)this).sendPacket(new CustomPacketPayload[]{new PhysicsAssemblerFlickAndHoldLeverPacket(this.worldPosition, subLevel == null)});
            if (subLevel instanceof ServerSubLevel) {
                ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
                if (DisassemblyPrevention.checkSubLevelForPrimary(level, this.getBlockPos())) {
                    this.throwDisassemblyExceptions(serverSubLevel);
                    this.startDisassembling(serverSubLevel, (ServerLevel)level, subLevel);
                    this.disassembling = true;
                }
            } else {
                this.primaryAssembler = true;
                BlockPos toAssemble = this.getBlockPos().relative(PhysicsAssemblerBlock.getStickyFacing(this.getBlockState()));
                SimAssemblyHelper.assembleFromSingleBlock(level, this.getBlockPos(), toAssemble, true, true);
                this.lastException = null;
                this.sendData();
            }
        }
        catch (AssemblyException e) {
            if (!(subLevel instanceof ServerSubLevel)) {
                this.primaryAssembler = false;
            }
            this.assemblyFailed(e);
        }
    }

    private void assemblyFailed(AssemblyException exception) {
        this.lastException = exception;
        VeilPacketManager.tracking((BlockEntity)this).sendPacket(new CustomPacketPayload[]{new PhysicsAssemblerFailedPacket(this.worldPosition)});
        this.sendData();
    }

    private void startDisassembling(ServerSubLevel serverSubLevel, ServerLevel level, SubLevel subLevel) {
        int turns;
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)level);
        PhysicsPipeline pipeline = container.physicsSystem().getPipeline();
        MassData massTracker = serverSubLevel.getMassTracker();
        double closestYRotation = SimMathUtils.getClosestYaw(subLevel.logicalPose().orientation());
        double ninety = 1.5707963267948966;
        this.disassemblyAngle = turns = -Mth.floor((double)(closestYRotation / 1.5707963267948966 + 0.5));
        this.disassemblyOrientation = new Quaterniond().rotateY((double)turns * 1.5707963267948966);
        FreeConstraintConfiguration config = new FreeConstraintConfiguration((Vector3dc)new Vector3d(), (Vector3dc)new Vector3d(massTracker.getCenterOfMass()).floor().add(0.5, 0.5, 0.5), this.disassemblyOrientation);
        this.alignmentConstraint = (FreeConstraintHandle)pipeline.addConstraint(null, serverSubLevel, (PhysicsConstraintConfiguration)config);
        this.alignmentConstraint.setMotor(ConstraintJointAxis.ANGULAR_X, 0.0, 13000.0, 1000.0, false, 0.0);
        this.alignmentConstraint.setMotor(ConstraintJointAxis.ANGULAR_Z, 0.0, 13000.0, 1000.0, false, 0.0);
        this.alignmentConstraint.setMotor(ConstraintJointAxis.ANGULAR_Y, 0.0, 13000.0, 1000.0, false, 0.0);
        this.alignmentConstraint.setMotor(ConstraintJointAxis.LINEAR_X, 0.0, 1.0E-6, 50.0, false, 0.0);
        this.alignmentConstraint.setMotor(ConstraintJointAxis.LINEAR_Y, 0.0, 1.0E-6, 50.0, false, 0.0);
        this.alignmentConstraint.setMotor(ConstraintJointAxis.LINEAR_Z, 0.0, 1.0E-6, 50.0, false, 0.0);
        this.disassembling = true;
        this.disassemblingTicks = 0;
        PhysicsStaffServerHandler.get(level).removeLock((SubLevel)serverSubLevel);
    }

    public void remove() {
        SubLevel subLevel;
        if (this.primaryAssembler && !this.level.isClientSide && (subLevel = this.getSubLevel()) instanceof ServerSubLevel) {
            ServerSubLevel ssb = (ServerSubLevel)subLevel;
            ((PrimaryAssemblerExtension)ssb).simulated$setPrimaryAssembler(null);
        }
        this.stopDisassembling();
        super.remove();
    }

    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        AssemblyException.write((CompoundTag)compound, (HolderLookup.Provider)registries, (AssemblyException)this.lastException);
        compound.putBoolean("IsPrimary", this.primaryAssembler);
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.lastException = AssemblyException.read((CompoundTag)tag, (HolderLookup.Provider)registries);
        this.primaryAssembler = tag.getBoolean("IsPrimary");
    }

    public AssemblyException getLastAssemblyException() {
        return this.lastException;
    }

    public boolean isPrimaryAssembler() {
        return this.primaryAssembler;
    }

    protected void setParent(Level level) {
        this.lastException = null;
        SubLevel subLevel = Sable.HELPER.getContaining(level, (Vec3i)this.getBlockPos());
        if (!level.isClientSide && this.primaryAssembler && subLevel instanceof ServerSubLevel) {
            PrimaryAssemblerExtension duck = (PrimaryAssemblerExtension)subLevel;
            if (duck.simulated$getPrimaryAssembler() == null) {
                duck.simulated$setPrimaryAssembler(this.getBlockPos());
            }
        } else {
            this.primaryAssembler = false;
        }
    }

    public float getClientAngle(float partialTicks) {
        return this.visualAngle.getValue(partialTicks);
    }
}

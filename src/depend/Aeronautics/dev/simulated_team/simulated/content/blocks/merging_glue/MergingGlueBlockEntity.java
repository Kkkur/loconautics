/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintHandle
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.merging_glue;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintHandle;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.content.blocks.merging_glue.MergingGlueBlock;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.util.SimAssemblyHelper;
import dev.simulated_team.simulated.util.SimMathUtils;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class MergingGlueBlockEntity
extends SmartBlockEntity
implements BlockEntitySubLevelActor {
    private static final int DURATION = 10;
    private static final int WAIT_ASSEMBLE = 2;
    private final Quaterniond startPartnerOrientation = new Quaterniond();
    private final Quaterniond endPartnerOrientation = new Quaterniond();
    private final Vector3d endPartnerPosition = new Vector3d();
    private final Vector3d startPartnerPosition = new Vector3d();
    private boolean hasControllingValues = false;
    private Rotation endRotation;
    @Nullable
    private BlockPos partnerPosition;
    private boolean isController;
    private int ageTicks;
    private FixedConstraintHandle lastConstraintHandle = null;

    public MergingGlueBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public MergingGlueBlockEntity getPartnerGlue() {
        if (this.partnerPosition == null) {
            return null;
        }
        BlockEntity be = this.level.getBlockEntity(this.partnerPosition);
        if (be instanceof MergingGlueBlockEntity) {
            return (MergingGlueBlockEntity)be;
        }
        return null;
    }

    public void tick() {
        boolean serverSide;
        super.tick();
        boolean bl = serverSide = !this.level.isClientSide();
        if (serverSide && this.isController && this.ageTicks > 12) {
            MergingGlueBlockEntity partner;
            ServerSubLevel partnerServerSubLevel;
            SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
            SubLevel partnerSubLevel = Sable.HELPER.getContaining(this.level, (Vec3i)this.partnerPosition);
            if (partnerSubLevel instanceof ServerSubLevel && (partnerServerSubLevel = (ServerSubLevel)partnerSubLevel).getMassTracker().getMass() < ((ServerSubLevel)subLevel).getMassTracker().getMass() && (partner = this.getPartnerGlue()) != null) {
                this.breakGlue();
                partner.disassembleToPartner(switch (this.endRotation) {
                    default -> throw new MatchException(null, null);
                    case Rotation.NONE -> Rotation.NONE;
                    case Rotation.CLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
                    case Rotation.COUNTERCLOCKWISE_90 -> Rotation.CLOCKWISE_90;
                    case Rotation.CLOCKWISE_180 -> Rotation.CLOCKWISE_180;
                });
                return;
            }
            this.breakGlue();
            this.disassembleToPartner(this.endRotation);
            return;
        }
        if (serverSide && (this.ageTicks > 200 || this.partnerPosition == null || !this.hasControllingValues && this.isController)) {
            this.breakGlue();
        }
        ++this.ageTicks;
    }

    private void disassembleToPartner(Rotation rotation) {
        assert (this.level != null);
        assert (this.partnerPosition != null);
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
        BlockPos pos = this.getBlockPos().relative(((Direction)this.getBlockState().getValue((Property)MergingGlueBlock.FACING)).getOpposite());
        SimAssemblyHelper.disassembleSubLevel(this.level, subLevel, pos, this.partnerPosition, rotation, false);
    }

    private void breakGlue() {
        if (this.level.getBlockState(this.getBlockPos()).is(SimBlocks.MERGING_GLUE)) {
            this.level.destroyBlock(this.getBlockPos(), true);
        }
        if (this.partnerPosition != null && this.level.getBlockState(this.partnerPosition).is(SimBlocks.MERGING_GLUE)) {
            this.level.destroyBlock(this.partnerPosition, true);
        }
    }

    public Vector3d getCenter(Vector3d dest) {
        BlockState state = this.getBlockState();
        Direction facing = (Direction)state.getValue((Property)MergingGlueBlock.FACING);
        return JOMLConversion.atCenterOf((Vec3i)this.worldPosition, (Vector3d)dest).sub((double)facing.getStepX() * 0.5, (double)facing.getStepY() * 0.5, (double)facing.getStepZ() * 0.5);
    }

    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        if (!this.isController) {
            return;
        }
        if (!this.hasControllingValues) {
            return;
        }
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)subLevel.getLevel());
        SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
        double partialPhysicsTick = physicsSystem.getPartialPhysicsTick();
        double physicsTime = (double)this.ageTicks + partialPhysicsTick;
        double lerpFactor = Mth.clamp((double)Math.pow(physicsTime / 10.0, 5.0), (double)0.0, (double)1.0);
        double rotationLerpFactor = Mth.clamp((double)(lerpFactor * 2.0), (double)0.0, (double)1.0);
        this.removeConstraint();
        MergingGlueBlockEntity partner = this.getPartnerGlue();
        if (partner == null) {
            return;
        }
        SubLevel partnerSubLevel = Sable.HELPER.getContaining((BlockEntity)partner);
        if (!(partnerSubLevel instanceof ServerSubLevel)) {
            return;
        }
        ServerSubLevel partnerServerSubLevel = (ServerSubLevel)partnerSubLevel;
        RigidBodyHandle partnerHandle = RigidBodyHandle.of((ServerSubLevel)partnerServerSubLevel);
        Vector3d localPartnerCenter = partner.getCenter(new Vector3d());
        this.lastConstraintHandle = (FixedConstraintHandle)physicsSystem.getPipeline().addConstraint(subLevel, partnerServerSubLevel, (PhysicsConstraintConfiguration)new FixedConstraintConfiguration((Vector3dc)this.startPartnerPosition.lerp((Vector3dc)this.endPartnerPosition, lerpFactor, new Vector3d()), (Vector3dc)localPartnerCenter, (Quaterniondc)this.startPartnerOrientation.slerp((Quaterniondc)this.endPartnerOrientation, rotationLerpFactor, new Quaterniond())));
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putBoolean("Controller", this.isController);
        if (this.partnerPosition != null) {
            tag.putLong("PartnerPosition", this.partnerPosition.asLong());
        }
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.isController = tag.getBoolean("Controller");
        if (tag.contains("PartnerPosition")) {
            this.partnerPosition = BlockPos.of((long)tag.getLong("PartnerPosition"));
        }
    }

    public void removeConstraint() {
        if (this.lastConstraintHandle != null) {
            this.lastConstraintHandle.remove();
        }
        this.lastConstraintHandle = null;
    }

    public void remove() {
        super.remove();
        this.removeConstraint();
        this.breakGlue();
    }

    public boolean isController() {
        return this.isController;
    }

    public void setPartnerPos(BlockPos partnerPos) {
        this.partnerPosition = partnerPos;
    }

    public void startControlling(MergingGlueBlockEntity partner) {
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
        SubLevel otherSubLevel = Sable.HELPER.getContaining((BlockEntity)partner);
        if (subLevel == null || otherSubLevel == null) {
            return;
        }
        Vector3d center = this.getCenter(new Vector3d());
        Vector3d partnerCenter = partner.getCenter(new Vector3d());
        otherSubLevel.logicalPose().transformPosition(partnerCenter);
        subLevel.logicalPose().transformPositionInverse(partnerCenter);
        this.startPartnerPosition.set((Vector3dc)partnerCenter);
        this.endPartnerPosition.set((Vector3dc)center);
        Quaterniond startOrientation = otherSubLevel.logicalPose().orientation();
        subLevel.logicalPose().orientation().conjugate(new Quaterniond()).mul((Quaterniondc)startOrientation, startOrientation);
        this.startPartnerOrientation.set((Quaterniondc)startOrientation);
        this.endPartnerOrientation.set((Quaterniondc)new Quaterniond());
        Direction direction = (Direction)this.getBlockState().getValue((Property)MergingGlueBlock.FACING);
        Direction partnerDirection = (Direction)partner.getBlockState().getValue((Property)MergingGlueBlock.FACING);
        if (direction.getAxis().isVertical()) {
            double yRotation = SimMathUtils.getClosestYaw(startOrientation);
            double ninety = 1.5707963267948966;
            int turns = -Mth.floor((double)(yRotation / 1.5707963267948966 + 0.5));
            this.endPartnerOrientation.rotateY((double)turns * 1.5707963267948966);
            this.endRotation = SimAssemblyHelper.rotationFrom90DegRots(-turns);
        } else {
            Vec3i normal = direction.getNormal();
            Vec3i partnerNormal = partnerDirection.getNormal();
            double angle = Math.atan2(partnerNormal.getX(), partnerNormal.getZ()) - Math.atan2(normal.getX(), normal.getZ());
            if (direction.getAxis() == partnerDirection.getAxis()) {
                angle += Math.PI;
            }
            double ninety = 1.5707963267948966;
            int turns = -Mth.floor((double)(angle / 1.5707963267948966 + 0.5));
            this.endPartnerOrientation.rotateY(angle);
            this.endRotation = SimAssemblyHelper.rotationFrom90DegRots(turns);
        }
        this.isController = true;
        this.hasControllingValues = true;
    }
}

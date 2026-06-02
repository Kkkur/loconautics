/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions.gantry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.gantry.GantryContraption;
import com.simibubi.create.content.contraptions.gantry.GantryContraptionUpdatePacket;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlockEntity;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class GantryContraptionEntity
extends AbstractContraptionEntity {
    Direction movementAxis;
    double clientOffsetDiff;
    double axisMotion;
    public double sequencedOffsetLimit = -1.0;

    public GantryContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public static GantryContraptionEntity create(Level world, Contraption contraption, Direction movementAxis) {
        GantryContraptionEntity entity = new GantryContraptionEntity((EntityType)AllEntityTypes.GANTRY_CONTRAPTION.get(), world);
        entity.setContraption(contraption);
        entity.movementAxis = movementAxis;
        return entity;
    }

    public void limitMovement(double maxOffset) {
        this.sequencedOffsetLimit = maxOffset;
    }

    @Override
    protected void tickContraption() {
        if (!(this.contraption instanceof GantryContraption)) {
            return;
        }
        double prevAxisMotion = this.axisMotion;
        if (this.level().isClientSide) {
            this.clientOffsetDiff *= 0.75;
            this.updateClientMotion();
        }
        this.checkPinionShaft();
        this.tickActors();
        Vec3 movementVec = this.getDeltaMovement();
        if (ContraptionCollider.collideBlocks(this)) {
            if (!this.level().isClientSide) {
                this.disassemble();
            }
            return;
        }
        if (!this.isStalled() && this.tickCount > 2) {
            if (this.sequencedOffsetLimit >= 0.0) {
                movementVec = VecHelper.clampComponentWise((Vec3)movementVec, (float)((float)this.sequencedOffsetLimit));
            }
            this.move(movementVec.x, movementVec.y, movementVec.z);
            if (this.sequencedOffsetLimit > 0.0) {
                this.sequencedOffsetLimit = Math.max(0.0, this.sequencedOffsetLimit - movementVec.length());
            }
        }
        if (Math.signum(prevAxisMotion) != Math.signum(this.axisMotion) && prevAxisMotion != 0.0) {
            this.contraption.stop(this.level());
        }
        if (!(this.level().isClientSide || prevAxisMotion == this.axisMotion && this.tickCount % 3 != 0)) {
            this.sendPacket();
        }
    }

    @Override
    public void disassemble() {
        this.sequencedOffsetLimit = -1.0;
        super.disassemble();
    }

    protected void checkPinionShaft() {
        GantryShaftBlockEntity gantryShaftBlockEntity;
        BlockEntity be;
        Vec3 currentPosition;
        block11: {
            block10: {
                Direction facing = ((GantryContraption)this.contraption).getFacing();
                currentPosition = this.getAnchorVec().add(0.5, 0.5, 0.5);
                BlockPos gantryShaftPos = BlockPos.containing((Position)currentPosition).relative(facing.getOpposite());
                be = this.level().getBlockEntity(gantryShaftPos);
                if (!(be instanceof GantryShaftBlockEntity)) break block10;
                gantryShaftBlockEntity = (GantryShaftBlockEntity)be;
                if (AllBlocks.GANTRY_SHAFT.has(be.getBlockState())) break block11;
            }
            if (!this.level().isClientSide) {
                this.setContraptionMotion(Vec3.ZERO);
                this.disassemble();
            }
            return;
        }
        BlockState blockState = be.getBlockState();
        Direction direction = (Direction)blockState.getValue((Property)GantryShaftBlock.FACING);
        float pinionMovementSpeed = gantryShaftBlockEntity.getPinionMovementSpeed();
        if (((Boolean)blockState.getValue((Property)GantryShaftBlock.POWERED)).booleanValue() || pinionMovementSpeed == 0.0f) {
            this.setContraptionMotion(Vec3.ZERO);
            if (!this.level().isClientSide) {
                this.disassemble();
            }
            return;
        }
        if (this.sequencedOffsetLimit >= 0.0) {
            pinionMovementSpeed = (float)Mth.clamp((double)pinionMovementSpeed, (double)(-this.sequencedOffsetLimit), (double)this.sequencedOffsetLimit);
        }
        Vec3 movementVec = Vec3.atLowerCornerOf((Vec3i)direction.getNormal()).scale((double)pinionMovementSpeed);
        Vec3 nextPosition = currentPosition.add(movementVec);
        double currentCoord = direction.getAxis().choose(currentPosition.x, currentPosition.y, currentPosition.z);
        double nextCoord = direction.getAxis().choose(nextPosition.x, nextPosition.y, nextPosition.z);
        if ((double)Mth.floor((double)currentCoord) + 0.5 < nextCoord != pinionMovementSpeed * (float)direction.getAxisDirection().getStep() < 0.0f && !gantryShaftBlockEntity.canAssembleOn()) {
            this.setContraptionMotion(Vec3.ZERO);
            if (!this.level().isClientSide) {
                this.disassemble();
            }
            return;
        }
        if (this.level().isClientSide) {
            return;
        }
        this.axisMotion = pinionMovementSpeed;
        this.setContraptionMotion(movementVec);
    }

    @Override
    protected void writeAdditional(CompoundTag compound, HolderLookup.Provider registries, boolean spawnPacket) {
        NBTHelper.writeEnum((CompoundTag)compound, (String)"GantryAxis", (Enum)this.movementAxis);
        if (this.sequencedOffsetLimit >= 0.0) {
            compound.putDouble("SequencedOffsetLimit", this.sequencedOffsetLimit);
        }
        super.writeAdditional(compound, registries, spawnPacket);
    }

    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnData) {
        this.movementAxis = (Direction)NBTHelper.readEnum((CompoundTag)compound, (String)"GantryAxis", Direction.class);
        this.sequencedOffsetLimit = compound.contains("SequencedOffsetLimit") ? compound.getDouble("SequencedOffsetLimit") : -1.0;
        super.readAdditional(compound, spawnData);
    }

    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        return localPos;
    }

    @Override
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        return localPos;
    }

    @Override
    protected StructureTransform makeStructureTransform() {
        return new StructureTransform(BlockPos.containing((Position)this.getAnchorVec().add(0.5, 0.5, 0.5)), 0.0f, 0.0f, 0.0f);
    }

    @Override
    protected float getStalledAngle() {
        return 0.0f;
    }

    public void teleportTo(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
    }

    @OnlyIn(value=Dist.CLIENT)
    public void lerpTo(double pX, double pY, double pZ, float pYRot, float pXRot, int pSteps) {
    }

    @Override
    protected void handleStallInformation(double x, double y, double z, float angle) {
        this.setPosRaw(x, y, z);
        this.clientOffsetDiff = 0.0;
    }

    @Override
    public AbstractContraptionEntity.ContraptionRotationState getRotationState() {
        return AbstractContraptionEntity.ContraptionRotationState.NONE;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
    }

    public void updateClientMotion() {
        float modifier = this.movementAxis.getAxisDirection().getStep();
        Vec3 motion = Vec3.atLowerCornerOf((Vec3i)this.movementAxis.getNormal()).scale((this.axisMotion + this.clientOffsetDiff * (double)modifier / 2.0) * (double)ServerSpeedProvider.get());
        if (this.sequencedOffsetLimit >= 0.0) {
            motion = VecHelper.clampComponentWise((Vec3)motion, (float)((float)this.sequencedOffsetLimit));
        }
        this.setContraptionMotion(motion);
    }

    public double getAxisCoord() {
        Vec3 anchorVec = this.getAnchorVec();
        return this.movementAxis.getAxis().choose(anchorVec.x, anchorVec.y, anchorVec.z);
    }

    public void sendPacket() {
        CatnipServices.NETWORK.sendToClientsTrackingEntity((Entity)this, (CustomPacketPayload)new GantryContraptionUpdatePacket(this.getId(), this.getAxisCoord(), this.axisMotion, this.sequencedOffsetLimit));
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void handlePacket(GantryContraptionUpdatePacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.entityID());
        if (!(entity instanceof GantryContraptionEntity)) {
            return;
        }
        GantryContraptionEntity ce = (GantryContraptionEntity)entity;
        ce.axisMotion = packet.motion();
        ce.clientOffsetDiff = packet.coord() - ce.getAxisCoord();
        ce.sequencedOffsetLimit = packet.sequenceLimit();
    }
}

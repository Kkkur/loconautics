/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllContraptionTypes
 *  com.simibubi.create.api.behaviour.movement.MovementBehaviour
 *  com.simibubi.create.api.contraption.ContraptionType
 *  com.simibubi.create.content.contraptions.Contraption
 *  com.simibubi.create.content.contraptions.ControlledContraptionEntity
 *  com.simibubi.create.content.contraptions.IControlContraption
 *  com.simibubi.create.content.contraptions.StructureTransform
 *  com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement
 *  com.simibubi.create.content.contraptions.behaviour.MovementContext
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 */
package dev.ryanhcode.offroad.content.entities;

import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelActor;
import dev.ryanhcode.offroad.content.contraptions.borehead_contraption.BoreheadBearingContraption;
import dev.ryanhcode.offroad.index.OffroadContraptionTypes;
import dev.ryanhcode.offroad.index.OffroadEntityTypes;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

public class BoreheadContraptionEntity
extends ControlledContraptionEntity {
    public BoreheadContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public static BoreheadContraptionEntity create(Level world, IControlContraption controller, Contraption contraption) {
        BoreheadContraptionEntity entity = new BoreheadContraptionEntity((EntityType)OffroadEntityTypes.BOREHEAD_CONTRAPTION_ENTITY.get(), world);
        entity.setControllerPos(controller.getBlockPosition());
        entity.setContraption(contraption);
        return entity;
    }

    protected boolean shouldActorTrigger(MovementContext context, StructureTemplate.StructureBlockInfo blockInfo, MovementBehaviour actor, Vec3 actorPosition, BlockPos gridPosition) {
        if (!(actor instanceof RockCuttingWheelActor) && !(actor instanceof ContraptionControlsMovement)) {
            context.disabled = true;
            return false;
        }
        return super.shouldActorTrigger(context, blockInfo, actor, actorPosition, gridPosition);
    }

    protected boolean isActorActive(MovementContext context, MovementBehaviour actor) {
        if (!(actor instanceof RockCuttingWheelActor) && !(actor instanceof ContraptionControlsMovement)) {
            context.disabled = true;
            return false;
        }
        return super.isActorActive(context, actor);
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }

    public BlockPos getControllerPos() {
        return this.controllerPos;
    }

    public void setContraption(Contraption contraption) {
        super.setContraption(contraption);
    }

    public float getAngle(float partialTicks) {
        BoreheadBearingBlockEntity be;
        IControlContraption controller = this.getController();
        if (controller instanceof BoreheadBearingBlockEntity && (be = (BoreheadBearingBlockEntity)controller).isSlowingDown()) {
            return be.getInterpolatedAngle(partialTicks - 1.0f);
        }
        return partialTicks == 1.0f ? this.angle : AngleHelper.angleLerp((double)partialTicks, (double)this.prevAngle, (double)this.angle);
    }

    public float getAngleDelta() {
        return this.angleDelta;
    }

    protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
        CompoundTag contTag;
        if (compound.contains("Contraption") && (contTag = compound.getCompound("Contraption")).contains("Type") && contTag.getString("Type").equals(AllContraptionTypes.BEARING.key().location().toString())) {
            compound.getCompound("Contraption").putString("Type", ((ContraptionType)OffroadContraptionTypes.BOREHEAD_CONTRAPTION_TYPE.get()).holder.key().location().toString());
        }
        super.readAdditional(compound, spawnPacket);
    }

    protected StructureTransform makeStructureTransform() {
        StructureTransform transform = super.makeStructureTransform();
        transform.angle = 0;
        transform.rotation = Rotation.NONE;
        transform.mirror = Mirror.NONE;
        transform.rotationAxis = Direction.Axis.X;
        return transform;
    }

    public BoreheadBearingContraption getContraption() {
        return (BoreheadBearingContraption)this.contraption;
    }
}

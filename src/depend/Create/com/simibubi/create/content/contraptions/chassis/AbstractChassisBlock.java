/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.content.contraptions.chassis;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.chassis.ChassisBlockEntity;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

public abstract class AbstractChassisBlock
extends RotatedPillarBlock
implements IWrenchable,
IBE<ChassisBlockEntity>,
TransformableBlock {
    public AbstractChassisBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        boolean isSlimeBall = stack.is(Tags.Items.SLIMEBALLS) || AllItems.SUPER_GLUE.isIn(stack);
        BooleanProperty affectedSide = this.getGlueableSide(state, hitResult.getDirection());
        if (affectedSide == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (isSlimeBall && ((Boolean)state.getValue((Property)affectedSide)).booleanValue()) {
            for (Direction face : Iterate.directions) {
                BooleanProperty glueableSide = this.getGlueableSide(state, face);
                if (glueableSide == null || ((Boolean)state.getValue((Property)glueableSide)).booleanValue() || !this.glueAllowedOnSide((BlockGetter)level, pos, state, face)) continue;
                if (level.isClientSide) {
                    Vec3 vec = hitResult.getLocation();
                    level.addParticle((ParticleOptions)ParticleTypes.ITEM_SLIME, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
                    return ItemInteractionResult.SUCCESS;
                }
                AllSoundEvents.SLIME_ADDED.playOnServer(level, (Vec3i)pos, 0.5f, 1.0f);
                state = (BlockState)state.setValue((Property)glueableSide, (Comparable)Boolean.valueOf(true));
            }
            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos, state);
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (!(stack.isEmpty() && player.isShiftKeyDown() || isSlimeBall)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if ((Boolean)state.getValue((Property)affectedSide) == isSlimeBall) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!this.glueAllowedOnSide((BlockGetter)level, pos, state, hitResult.getDirection())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            Vec3 vec = hitResult.getLocation();
            level.addParticle((ParticleOptions)ParticleTypes.ITEM_SLIME, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
            return ItemInteractionResult.SUCCESS;
        }
        AllSoundEvents.SLIME_ADDED.playOnServer(level, (Vec3i)pos, 0.5f, 1.0f);
        level.setBlockAndUpdate(pos, (BlockState)state.setValue((Property)affectedSide, (Comparable)Boolean.valueOf(isSlimeBall)));
        return ItemInteractionResult.SUCCESS;
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        BooleanProperty glueableSide;
        if (rotation == Rotation.NONE) {
            return state;
        }
        BlockState rotated = super.rotate(state, rotation);
        for (Direction face : Iterate.directions) {
            glueableSide = this.getGlueableSide(rotated, face);
            if (glueableSide == null) continue;
            rotated = (BlockState)rotated.setValue((Property)glueableSide, (Comparable)Boolean.valueOf(false));
        }
        for (Direction face : Iterate.directions) {
            Direction rotatedFacing;
            BooleanProperty rotatedGlueableSide;
            glueableSide = this.getGlueableSide(state, face);
            if (glueableSide == null || !((Boolean)state.getValue((Property)glueableSide)).booleanValue() || (rotatedGlueableSide = this.getGlueableSide(rotated, rotatedFacing = rotation.rotate(face))) == null) continue;
            rotated = (BlockState)rotated.setValue((Property)rotatedGlueableSide, (Comparable)Boolean.valueOf(true));
        }
        return rotated;
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        BooleanProperty glueableSide;
        if (mirrorIn == Mirror.NONE) {
            return state;
        }
        BlockState mirrored = state;
        for (Direction face : Iterate.directions) {
            glueableSide = this.getGlueableSide(mirrored, face);
            if (glueableSide == null) continue;
            mirrored = (BlockState)mirrored.setValue((Property)glueableSide, (Comparable)Boolean.valueOf(false));
        }
        for (Direction face : Iterate.directions) {
            Direction mirroredFacing;
            BooleanProperty mirroredGlueableSide;
            glueableSide = this.getGlueableSide(state, face);
            if (glueableSide == null || !((Boolean)state.getValue((Property)glueableSide)).booleanValue() || (mirroredGlueableSide = this.getGlueableSide(mirrored, mirroredFacing = mirrorIn.mirror(face))) == null) continue;
            mirrored = (BlockState)mirrored.setValue((Property)mirroredGlueableSide, (Comparable)Boolean.valueOf(true));
        }
        return mirrored;
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.mirror != null) {
            state = this.mirror(state, transform.mirror);
        }
        if (transform.rotationAxis == Direction.Axis.Y) {
            return this.rotate(state, transform.rotation);
        }
        return this.transformInner(state, transform);
    }

    protected BlockState transformInner(BlockState state, StructureTransform transform) {
        BooleanProperty glueableSide;
        if (transform.rotation == Rotation.NONE) {
            return state;
        }
        BlockState rotated = (BlockState)state.setValue((Property)AXIS, (Comparable)transform.rotateAxis((Direction.Axis)state.getValue((Property)AXIS)));
        AbstractChassisBlock block = (AbstractChassisBlock)state.getBlock();
        for (Direction face : Iterate.directions) {
            glueableSide = block.getGlueableSide(rotated, face);
            if (glueableSide == null) continue;
            rotated = (BlockState)rotated.setValue((Property)glueableSide, (Comparable)Boolean.valueOf(false));
        }
        for (Direction face : Iterate.directions) {
            Direction rotatedFacing;
            BooleanProperty rotatedGlueableSide;
            glueableSide = block.getGlueableSide(state, face);
            if (glueableSide == null || !((Boolean)state.getValue((Property)glueableSide)).booleanValue() || (rotatedGlueableSide = block.getGlueableSide(rotated, rotatedFacing = transform.rotateFacing(face))) == null) continue;
            rotated = (BlockState)rotated.setValue((Property)rotatedGlueableSide, (Comparable)Boolean.valueOf(true));
        }
        return rotated;
    }

    public abstract BooleanProperty getGlueableSide(BlockState var1, Direction var2);

    protected boolean glueAllowedOnSide(BlockGetter world, BlockPos pos, BlockState state, Direction side) {
        return true;
    }

    @Override
    public Class<ChassisBlockEntity> getBlockEntityClass() {
        return ChassisBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChassisBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.CHASSIS.get();
    }
}

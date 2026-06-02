/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BasePressurePlateBlock
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.BaseTorchBlock
 *  net.minecraft.world.level.block.BedBlock
 *  net.minecraft.world.level.block.BellBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DiodeBlock
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.FlowerPotBlock
 *  net.minecraft.world.level.block.GrindstoneBlock
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.LadderBlock
 *  net.minecraft.world.level.block.RedStoneWireBlock
 *  net.minecraft.world.level.block.RedstoneWallTorchBlock
 *  net.minecraft.world.level.block.SignBlock
 *  net.minecraft.world.level.block.StandingSignBlock
 *  net.minecraft.world.level.block.WallSignBlock
 *  net.minecraft.world.level.block.WallTorchBlock
 *  net.minecraft.world.level.block.WoolCarpetBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BedPart
 *  net.minecraft.world.level.block.state.properties.BellAttachType
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.PushReaction
 *  net.neoforged.neoforge.common.Tags$Blocks
 */
package com.simibubi.create.impl.contraption;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.ContraptionMovementSetting;
import com.simibubi.create.content.contraptions.actors.AttachedActorBlock;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlock;
import com.simibubi.create.content.contraptions.bearing.ClockworkBearingBlock;
import com.simibubi.create.content.contraptions.bearing.ClockworkBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlock;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.SailBlock;
import com.simibubi.create.content.contraptions.chassis.AbstractChassisBlock;
import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlock;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleExtenderBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import com.simibubi.create.content.kinetics.fan.NozzleBlock;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlock;
import com.simibubi.create.content.logistics.vault.ItemVaultBlock;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.track.ITrackBlock;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.Tags;

public class BlockMovementChecksImpl {
    private static final List<BlockMovementChecks.MovementNecessaryCheck> MOVEMENT_NECESSARY_CHECKS = new ArrayList<BlockMovementChecks.MovementNecessaryCheck>();
    private static final List<BlockMovementChecks.MovementAllowedCheck> MOVEMENT_ALLOWED_CHECKS = new ArrayList<BlockMovementChecks.MovementAllowedCheck>();
    private static final List<BlockMovementChecks.BrittleCheck> BRITTLE_CHECKS = new ArrayList<BlockMovementChecks.BrittleCheck>();
    private static final List<BlockMovementChecks.AttachedCheck> ATTACHED_CHECKS = new ArrayList<BlockMovementChecks.AttachedCheck>();
    private static final List<BlockMovementChecks.NotSupportiveCheck> NOT_SUPPORTIVE_CHECKS = new ArrayList<BlockMovementChecks.NotSupportiveCheck>();

    public static synchronized void registerMovementNecessaryCheck(BlockMovementChecks.MovementNecessaryCheck check) {
        MOVEMENT_NECESSARY_CHECKS.add(0, check);
    }

    public static synchronized void registerMovementAllowedCheck(BlockMovementChecks.MovementAllowedCheck check) {
        MOVEMENT_ALLOWED_CHECKS.add(0, check);
    }

    public static synchronized void registerBrittleCheck(BlockMovementChecks.BrittleCheck check) {
        BRITTLE_CHECKS.add(0, check);
    }

    public static synchronized void registerAttachedCheck(BlockMovementChecks.AttachedCheck check) {
        ATTACHED_CHECKS.add(0, check);
    }

    public static synchronized void registerNotSupportiveCheck(BlockMovementChecks.NotSupportiveCheck check) {
        NOT_SUPPORTIVE_CHECKS.add(0, check);
    }

    public static boolean isMovementNecessary(BlockState state, Level world, BlockPos pos) {
        for (BlockMovementChecks.MovementNecessaryCheck check : MOVEMENT_NECESSARY_CHECKS) {
            BlockMovementChecks.CheckResult result = check.isMovementNecessary(state, world, pos);
            if (result == BlockMovementChecks.CheckResult.PASS) continue;
            return result.toBoolean();
        }
        return BlockMovementChecksImpl.isMovementNecessaryFallback(state, world, pos);
    }

    public static boolean isMovementAllowed(BlockState state, Level world, BlockPos pos) {
        for (BlockMovementChecks.MovementAllowedCheck check : MOVEMENT_ALLOWED_CHECKS) {
            BlockMovementChecks.CheckResult result = check.isMovementAllowed(state, world, pos);
            if (result == BlockMovementChecks.CheckResult.PASS) continue;
            return result.toBoolean();
        }
        return BlockMovementChecksImpl.isMovementAllowedFallback(state, world, pos);
    }

    public static boolean isBrittle(BlockState state) {
        for (BlockMovementChecks.BrittleCheck check : BRITTLE_CHECKS) {
            BlockMovementChecks.CheckResult result = check.isBrittle(state);
            if (result == BlockMovementChecks.CheckResult.PASS) continue;
            return result.toBoolean();
        }
        return BlockMovementChecksImpl.isBrittleFallback(state);
    }

    public static boolean isBlockAttachedTowards(BlockState state, Level world, BlockPos pos, Direction direction) {
        for (BlockMovementChecks.AttachedCheck check : ATTACHED_CHECKS) {
            BlockMovementChecks.CheckResult result = check.isBlockAttachedTowards(state, world, pos, direction);
            if (result == BlockMovementChecks.CheckResult.PASS) continue;
            return result.toBoolean();
        }
        return BlockMovementChecksImpl.isBlockAttachedTowardsFallback(state, world, pos, direction);
    }

    public static boolean isNotSupportive(BlockState state, Direction facing) {
        for (BlockMovementChecks.NotSupportiveCheck check : NOT_SUPPORTIVE_CHECKS) {
            BlockMovementChecks.CheckResult result = check.isNotSupportive(state, facing);
            if (result == BlockMovementChecks.CheckResult.PASS) continue;
            return result.toBoolean();
        }
        return BlockMovementChecksImpl.isNotSupportiveFallback(state, facing);
    }

    private static boolean isMovementNecessaryFallback(BlockState state, Level world, BlockPos pos) {
        if (BlockMovementChecks.isBrittle(state)) {
            return true;
        }
        if (AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.matches(state)) {
            return true;
        }
        if (state.getCollisionShape((BlockGetter)world, pos).isEmpty()) {
            return false;
        }
        return !state.canBeReplaced();
    }

    private static boolean isMovementAllowedFallback(BlockState state, Level world, BlockPos pos) {
        BlockEntity be;
        Block block = state.getBlock();
        if (block instanceof AbstractChassisBlock) {
            return true;
        }
        if (state.getDestroySpeed((BlockGetter)world, pos) == -1.0f) {
            return false;
        }
        if (state.is(Tags.Blocks.RELOCATION_NOT_SUPPORTED)) {
            return false;
        }
        if (AllTags.AllBlockTags.NON_MOVABLE.matches(state)) {
            return false;
        }
        if (ContraptionMovementSetting.get(state) == ContraptionMovementSetting.UNMOVABLE) {
            return false;
        }
        if (block instanceof MechanicalPistonBlock && state.getValue(MechanicalPistonBlock.STATE) != MechanicalPistonBlock.PistonState.MOVING) {
            return true;
        }
        if (block instanceof MechanicalBearingBlock && (be = world.getBlockEntity(pos)) instanceof MechanicalBearingBlockEntity) {
            return !((MechanicalBearingBlockEntity)be).isRunning();
        }
        if (block instanceof ClockworkBearingBlock && (be = world.getBlockEntity(pos)) instanceof ClockworkBearingBlockEntity) {
            return !((ClockworkBearingBlockEntity)be).isRunning();
        }
        if (block instanceof PulleyBlock && (be = world.getBlockEntity(pos)) instanceof PulleyBlockEntity) {
            PulleyBlockEntity pulley = (PulleyBlockEntity)be;
            return !pulley.running;
        }
        if (AllBlocks.BELT.has(state)) {
            return true;
        }
        if (state.getBlock() instanceof GrindstoneBlock) {
            return true;
        }
        if (state.getBlock() instanceof ITrackBlock) {
            return false;
        }
        if (state.getBlock() instanceof StationBlock) {
            return false;
        }
        return state.getPistonPushReaction() != PushReaction.BLOCK;
    }

    private static boolean isBrittleFallback(BlockState state) {
        Block block = state.getBlock();
        if (state.hasProperty((Property)BlockStateProperties.HANGING)) {
            return true;
        }
        if (block instanceof LadderBlock) {
            return true;
        }
        if (block instanceof BaseTorchBlock) {
            return true;
        }
        if (block instanceof SignBlock) {
            return true;
        }
        if (block instanceof BasePressurePlateBlock) {
            return true;
        }
        if (block instanceof FaceAttachedHorizontalDirectionalBlock && !(block instanceof GrindstoneBlock) && !(block instanceof PackagerLinkBlock)) {
            return true;
        }
        if (block instanceof CartAssemblerBlock) {
            return false;
        }
        if (block instanceof BaseRailBlock) {
            return true;
        }
        if (block instanceof DiodeBlock) {
            return true;
        }
        if (block instanceof RedStoneWireBlock) {
            return true;
        }
        if (block instanceof WoolCarpetBlock) {
            return true;
        }
        if (block instanceof WhistleBlock) {
            return true;
        }
        if (block instanceof WhistleExtenderBlock) {
            return true;
        }
        if (block instanceof BeltFunnelBlock) {
            return true;
        }
        return AllTags.AllBlockTags.BRITTLE.matches(state);
    }

    private static boolean isBlockAttachedTowardsFallback(BlockState state, Level world, BlockPos pos, Direction direction) {
        Block block = state.getBlock();
        if (block instanceof LadderBlock) {
            return state.getValue((Property)LadderBlock.FACING) == direction.getOpposite();
        }
        if (block instanceof WallTorchBlock) {
            return state.getValue((Property)WallTorchBlock.FACING) == direction.getOpposite();
        }
        if (block instanceof WallSignBlock) {
            return state.getValue((Property)WallSignBlock.FACING) == direction.getOpposite();
        }
        if (block instanceof StandingSignBlock) {
            return direction == Direction.DOWN;
        }
        if (block instanceof BasePressurePlateBlock) {
            return direction == Direction.DOWN;
        }
        if (block instanceof DoorBlock) {
            if (state.getValue((Property)DoorBlock.HALF) == DoubleBlockHalf.LOWER && direction == Direction.UP) {
                return true;
            }
            return direction == Direction.DOWN;
        }
        if (block instanceof BedBlock) {
            Direction facing = (Direction)state.getValue((Property)BedBlock.FACING);
            if (state.getValue((Property)BedBlock.PART) == BedPart.HEAD) {
                facing = facing.getOpposite();
            }
            return direction == facing;
        }
        if (block instanceof RedstoneLinkBlock) {
            return direction.getOpposite() == state.getValue((Property)RedstoneLinkBlock.FACING);
        }
        if (block instanceof FlowerPotBlock) {
            return direction == Direction.DOWN;
        }
        if (block instanceof DiodeBlock) {
            return direction == Direction.DOWN;
        }
        if (block instanceof RedStoneWireBlock) {
            return direction == Direction.DOWN;
        }
        if (block instanceof WoolCarpetBlock) {
            return direction == Direction.DOWN;
        }
        if (block instanceof RedstoneWallTorchBlock) {
            return state.getValue((Property)RedstoneWallTorchBlock.FACING) == direction.getOpposite();
        }
        if (block instanceof BaseTorchBlock) {
            return direction == Direction.DOWN;
        }
        if (block instanceof FaceAttachedHorizontalDirectionalBlock) {
            AttachFace attachFace = (AttachFace)state.getValue((Property)FaceAttachedHorizontalDirectionalBlock.FACE);
            if (attachFace == AttachFace.CEILING) {
                return direction == Direction.UP;
            }
            if (attachFace == AttachFace.FLOOR) {
                return direction == Direction.DOWN;
            }
            if (attachFace == AttachFace.WALL) {
                return direction.getOpposite() == state.getValue((Property)FaceAttachedHorizontalDirectionalBlock.FACING);
            }
        }
        if (state.hasProperty((Property)BlockStateProperties.HANGING)) {
            return direction == ((Boolean)state.getValue((Property)BlockStateProperties.HANGING) != false ? Direction.UP : Direction.DOWN);
        }
        if (block instanceof BaseRailBlock) {
            return direction == Direction.DOWN;
        }
        if (block instanceof AttachedActorBlock) {
            return direction == ((Direction)state.getValue((Property)HarvesterBlock.FACING)).getOpposite();
        }
        if (block instanceof HandCrankBlock) {
            return direction == ((Direction)state.getValue((Property)HandCrankBlock.FACING)).getOpposite();
        }
        if (block instanceof NozzleBlock) {
            return direction == ((Direction)state.getValue((Property)NozzleBlock.FACING)).getOpposite();
        }
        if (block instanceof BellBlock) {
            BellAttachType attachment = (BellAttachType)state.getValue((Property)BlockStateProperties.BELL_ATTACHMENT);
            if (attachment == BellAttachType.FLOOR) {
                return direction == Direction.DOWN;
            }
            if (attachment == BellAttachType.CEILING) {
                return direction == Direction.UP;
            }
            return direction == state.getValue((Property)HorizontalDirectionalBlock.FACING);
        }
        if (state.getBlock() instanceof SailBlock) {
            return direction.getAxis() != ((Direction)state.getValue((Property)SailBlock.FACING)).getAxis();
        }
        if (state.getBlock() instanceof FluidTankBlock) {
            return ConnectivityHandler.isConnected((BlockGetter)world, pos, pos.relative(direction));
        }
        if (state.getBlock() instanceof ItemVaultBlock) {
            return ConnectivityHandler.isConnected((BlockGetter)world, pos, pos.relative(direction));
        }
        if (AllBlocks.STICKER.has(state) && ((Boolean)state.getValue((Property)StickerBlock.EXTENDED)).booleanValue()) {
            return direction == state.getValue((Property)StickerBlock.FACING) && !BlockMovementChecks.isNotSupportive(world.getBlockState(pos.relative(direction)), direction.getOpposite());
        }
        if (block instanceof AbstractBogeyBlock) {
            AbstractBogeyBlock bogey = (AbstractBogeyBlock)block;
            return bogey.getStickySurfaces((BlockGetter)world, pos, state).contains(direction);
        }
        if (block instanceof WhistleBlock) {
            return direction == ((Boolean)state.getValue((Property)WhistleBlock.WALL) != false ? (Direction)state.getValue((Property)WhistleBlock.FACING) : Direction.DOWN);
        }
        if (block instanceof WhistleExtenderBlock) {
            return direction == Direction.DOWN;
        }
        return false;
    }

    private static boolean isNotSupportiveFallback(BlockState state, Direction facing) {
        if (AllBlocks.MECHANICAL_DRILL.has(state)) {
            return state.getValue((Property)BlockStateProperties.FACING) == facing;
        }
        if (AllBlocks.MECHANICAL_BEARING.has(state)) {
            return state.getValue((Property)BlockStateProperties.FACING) == facing;
        }
        if (AllBlocks.CART_ASSEMBLER.has(state)) {
            return facing == Direction.DOWN;
        }
        if (AllBlocks.MECHANICAL_SAW.has(state)) {
            return state.getValue((Property)BlockStateProperties.FACING) == facing;
        }
        if (AllBlocks.PORTABLE_STORAGE_INTERFACE.has(state)) {
            return state.getValue((Property)PortableStorageInterfaceBlock.FACING) == facing;
        }
        if (state.getBlock() instanceof AttachedActorBlock && !AllBlocks.MECHANICAL_ROLLER.has(state)) {
            return state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING) == facing;
        }
        if (AllBlocks.ROPE_PULLEY.has(state)) {
            return facing == Direction.DOWN;
        }
        if (state.getBlock() instanceof WoolCarpetBlock) {
            return facing == Direction.UP;
        }
        if (state.getBlock() instanceof SailBlock) {
            return facing.getAxis() == ((Direction)state.getValue((Property)SailBlock.FACING)).getAxis();
        }
        if (AllBlocks.PISTON_EXTENSION_POLE.has(state)) {
            return facing.getAxis() != ((Direction)state.getValue((Property)BlockStateProperties.FACING)).getAxis();
        }
        if (AllBlocks.MECHANICAL_PISTON_HEAD.has(state)) {
            return facing.getAxis() != ((Direction)state.getValue((Property)BlockStateProperties.FACING)).getAxis();
        }
        if (AllBlocks.STICKER.has(state) && !((Boolean)state.getValue((Property)StickerBlock.EXTENDED)).booleanValue()) {
            return facing == state.getValue((Property)StickerBlock.FACING);
        }
        if (state.getBlock() instanceof SlidingDoorBlock) {
            return false;
        }
        return BlockMovementChecks.isBrittle(state);
    }
}

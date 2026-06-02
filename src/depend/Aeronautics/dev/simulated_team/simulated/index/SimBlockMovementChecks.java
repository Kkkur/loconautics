/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.api.contraption.BlockMovementChecks
 *  com.simibubi.create.api.contraption.BlockMovementChecks$CheckResult
 *  com.simibubi.create.content.contraptions.bearing.BearingBlock
 *  com.simibubi.create.content.contraptions.bearing.SailBlock
 *  com.simibubi.create.content.contraptions.bearing.WindmillBearingBlock
 *  com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity
 *  com.simibubi.create.content.contraptions.chassis.StickerBlock
 *  com.simibubi.create.content.contraptions.gantry.GantryCarriageBlock
 *  com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock
 *  com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock$PistonState
 *  com.simibubi.create.content.contraptions.piston.MechanicalPistonHeadBlock
 *  com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock
 *  com.simibubi.create.content.contraptions.pulley.PulleyBlock
 *  com.simibubi.create.content.contraptions.pulley.PulleyBlock$MagnetBlock
 *  com.simibubi.create.content.contraptions.pulley.PulleyBlock$RopeBlock
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.belt.BeltBlock
 *  com.simibubi.create.content.kinetics.gantry.GantryShaftBlock
 *  com.simibubi.create.infrastructure.config.AllConfigs
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.PistonType
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.simulated_team.simulated.index;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.SailBlock;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlock;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlock;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonHeadBlock;
import com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlock;
import dev.simulated_team.simulated.content.blocks.symmetric_sail.SymmetricSailBlock;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.ApiStatus;

public class SimBlockMovementChecks {
    private static final List<BlockPos> TEMP_DEFAULT_POSITIONS = new ArrayList<BlockPos>();
    private static final ObjectList<AdditionalBlocks> ADDITIONAL_BLOCK_REGISTRATIONS = new ObjectArrayList();
    private static final ObjectList<AttachedCheck> ATTACHED_CHECKS = new ObjectArrayList();

    private static BlockMovementChecks.CheckResult registerDefaultBlockAttachedTowards(BlockState state, Level world, BlockPos pos, Direction direction) {
        BlockMovementChecks.CheckResult checkResult;
        Block block = state.getBlock();
        BlockState relativeState = world.getBlockState(pos.relative(direction));
        Block relativeBlock = relativeState.getBlock();
        Block block2 = block;
        Objects.requireNonNull(block2);
        Block block3 = block2;
        int n = 0;
        block6: while (true) {
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{SymmetricSailBlock.class, SailBlock.class, SymmetricSailBlock.class, SpringBlock.class}, (Object)block3, n)) {
                case 0: {
                    SymmetricSailBlock ignored = (SymmetricSailBlock)block3;
                    if (!(relativeBlock instanceof SailBlock)) {
                        n = 1;
                        continue block6;
                    }
                    checkResult = BlockMovementChecks.CheckResult.FAIL;
                    break block6;
                }
                case 1: {
                    SailBlock ignored = (SailBlock)block3;
                    if (!(relativeBlock instanceof SymmetricSailBlock)) {
                        n = 2;
                        continue block6;
                    }
                    checkResult = BlockMovementChecks.CheckResult.FAIL;
                    break block6;
                }
                case 2: {
                    SymmetricSailBlock ignored = (SymmetricSailBlock)block3;
                    if (direction.getAxis() == state.getValue((Property)SymmetricSailBlock.AXIS)) {
                        checkResult = BlockMovementChecks.CheckResult.FAIL;
                        break block6;
                    }
                    checkResult = BlockMovementChecks.CheckResult.SUCCESS;
                    break block6;
                }
                case 3: {
                    SpringBlock ignored = (SpringBlock)block3;
                    if (direction.getOpposite() == state.getValue((Property)SpringBlock.FACING)) {
                        checkResult = BlockMovementChecks.CheckResult.SUCCESS;
                        break block6;
                    }
                    checkResult = BlockMovementChecks.CheckResult.FAIL;
                    break block6;
                }
                default: {
                    checkResult = BlockMovementChecks.CheckResult.PASS;
                    break block6;
                }
            }
            break;
        }
        return checkResult;
    }

    private static synchronized Iterable<BlockPos> registerDefaultAdditionalBlocks(BlockState state, Level level, BlockPos pos, Set<BlockPos> visited) {
        Block block;
        TEMP_DEFAULT_POSITIONS.clear();
        Block block2 = block = state.getBlock();
        Objects.requireNonNull(block2);
        Block block3 = block2;
        int n = 0;
        block0 : switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{BeltBlock.class, PulleyBlock.class, WindmillBearingBlock.class, BearingBlock.class, MechanicalPistonBlock.class, PistonExtensionPoleBlock.class, MechanicalPistonHeadBlock.class, GantryCarriageBlock.class, GantryShaftBlock.class, StickerBlock.class}, (Object)block3, n)) {
            case 0: {
                BlockPos prevPos;
                BeltBlock ignored = (BeltBlock)block3;
                BlockPos nextPos = BeltBlock.nextSegmentPosition((BlockState)state, (BlockPos)pos, (boolean)true);
                if (nextPos != null && !visited.contains(nextPos)) {
                    TEMP_DEFAULT_POSITIONS.add(nextPos);
                }
                if ((prevPos = BeltBlock.nextSegmentPosition((BlockState)state, (BlockPos)pos, (boolean)false)) == null || visited.contains(prevPos)) break;
                TEMP_DEFAULT_POSITIONS.add(prevPos);
                break;
            }
            case 1: {
                PulleyBlock ignored = (PulleyBlock)block3;
                int limit = (Integer)AllConfigs.server().kinetics.maxRopeLength.get();
                BlockPos ropePos = pos;
                while (limit-- >= 0 && level.isLoaded(ropePos = ropePos.below())) {
                    BlockState ropeState = level.getBlockState(ropePos);
                    Block ropeBlock = ropeState.getBlock();
                    if (!(ropeBlock instanceof PulleyBlock.RopeBlock) && !(ropeBlock instanceof PulleyBlock.MagnetBlock)) {
                        if (visited.contains(ropePos)) break block0;
                        TEMP_DEFAULT_POSITIONS.add(ropePos);
                        break block0;
                    }
                    if (visited.contains(ropePos)) continue;
                    TEMP_DEFAULT_POSITIONS.add(ropePos);
                }
                break;
            }
            case 2: {
                BlockPos relative;
                WindmillBearingBlock ignored = (WindmillBearingBlock)block3;
                BlockEntity ropeState = level.getBlockEntity(pos);
                if (ropeState instanceof WindmillBearingBlockEntity) {
                    WindmillBearingBlockEntity wwbe = (WindmillBearingBlockEntity)ropeState;
                    wwbe.disassembleForMovement();
                }
                if (visited.contains(relative = pos.relative((Direction)state.getValue((Property)BearingBlock.FACING)))) break;
                TEMP_DEFAULT_POSITIONS.add(relative);
                break;
            }
            case 3: {
                BearingBlock ignored = (BearingBlock)block3;
                BlockPos relative = pos.relative((Direction)state.getValue((Property)BearingBlock.FACING));
                if (visited.contains(relative)) break;
                TEMP_DEFAULT_POSITIONS.add(relative);
                break;
            }
            case 4: {
                BlockState poleState;
                MechanicalPistonBlock ignored = (MechanicalPistonBlock)block3;
                MechanicalPistonBlock.PistonState s = (MechanicalPistonBlock.PistonState)state.getValue((Property)MechanicalPistonBlock.STATE);
                if (s == MechanicalPistonBlock.PistonState.MOVING) break;
                Direction dir = (Direction)state.getValue((Property)MechanicalPistonBlock.FACING);
                BlockPos reverseOffset = pos.relative(dir.getOpposite());
                if (!visited.contains(reverseOffset) && (poleState = level.getBlockState(reverseOffset)).getBlock() instanceof PistonExtensionPoleBlock && ((Direction)poleState.getValue((Property)PistonExtensionPoleBlock.FACING)).getAxis() == dir.getAxis()) {
                    TEMP_DEFAULT_POSITIONS.add(reverseOffset);
                }
                if (s != MechanicalPistonBlock.PistonState.EXTENDED && !MechanicalPistonBlock.isStickyPiston((BlockState)state) || visited.contains(reverseOffset = pos.relative(dir))) break;
                TEMP_DEFAULT_POSITIONS.add(reverseOffset);
                break;
            }
            case 5: {
                PistonExtensionPoleBlock ignored = (PistonExtensionPoleBlock)block3;
                for (Direction d : Iterate.directionsInAxis((Direction.Axis)((Direction)state.getValue((Property)PistonExtensionPoleBlock.FACING)).getAxis())) {
                    Direction pistonFacing;
                    BlockPos offset = pos.relative(d);
                    if (visited.contains(offset)) continue;
                    BlockState blockState = level.getBlockState(offset);
                    if (MechanicalPistonBlock.isExtensionPole((BlockState)blockState) && ((Direction)blockState.getValue((Property)PistonExtensionPoleBlock.FACING)).getAxis() == d.getAxis()) {
                        TEMP_DEFAULT_POSITIONS.add(offset);
                    }
                    if (MechanicalPistonBlock.isPistonHead((BlockState)blockState) && ((Direction)blockState.getValue((Property)MechanicalPistonHeadBlock.FACING)).getAxis() == d.getAxis()) {
                        TEMP_DEFAULT_POSITIONS.add(offset);
                    }
                    if (!(blockState.getBlock() instanceof MechanicalPistonBlock) || (pistonFacing = (Direction)blockState.getValue((Property)MechanicalPistonBlock.FACING)) != d && (pistonFacing != d.getOpposite() || blockState.getValue((Property)MechanicalPistonBlock.STATE) != MechanicalPistonBlock.PistonState.EXTENDED)) continue;
                    TEMP_DEFAULT_POSITIONS.add(offset);
                }
                break;
            }
            case 6: {
                BlockPos attached;
                MechanicalPistonHeadBlock ignore = (MechanicalPistonHeadBlock)block3;
                Direction direction = (Direction)state.getValue((Property)MechanicalPistonHeadBlock.FACING);
                BlockPos offset = pos.relative(direction.getOpposite());
                if (!visited.contains(offset)) {
                    Direction pistonFacing;
                    BlockState blockState = level.getBlockState(offset);
                    if (MechanicalPistonBlock.isExtensionPole((BlockState)blockState) && ((Direction)blockState.getValue((Property)PistonExtensionPoleBlock.FACING)).getAxis() == direction.getAxis()) {
                        TEMP_DEFAULT_POSITIONS.add(offset);
                    }
                    if (blockState.getBlock() instanceof MechanicalPistonBlock && (pistonFacing = (Direction)blockState.getValue((Property)MechanicalPistonBlock.FACING)) == direction && blockState.getValue((Property)MechanicalPistonBlock.STATE) == MechanicalPistonBlock.PistonState.EXTENDED) {
                        TEMP_DEFAULT_POSITIONS.add(offset);
                    }
                }
                if (state.getValue((Property)MechanicalPistonHeadBlock.TYPE) != PistonType.STICKY || visited.contains(attached = pos.relative(direction))) break;
                TEMP_DEFAULT_POSITIONS.add(attached);
                break;
            }
            case 7: {
                GantryCarriageBlock ignored = (GantryCarriageBlock)block3;
                BlockPos offset = pos.relative((Direction)state.getValue((Property)GantryCarriageBlock.FACING));
                if (!visited.contains(offset)) {
                    TEMP_DEFAULT_POSITIONS.add(offset);
                }
                Direction.Axis rotationAxis = ((IRotate)state.getBlock()).getRotationAxis(state);
                for (Direction d : Iterate.directionsInAxis((Direction.Axis)rotationAxis)) {
                    offset = pos.relative(d);
                    BlockState offsetState = level.getBlockState(offset);
                    if (!AllBlocks.GANTRY_SHAFT.has(offsetState) || ((Direction)offsetState.getValue((Property)GantryShaftBlock.FACING)).getAxis() != d.getAxis() || visited.contains(offset)) continue;
                    TEMP_DEFAULT_POSITIONS.add(offset);
                }
                break;
            }
            case 8: {
                GantryShaftBlock ignored = (GantryShaftBlock)block3;
                for (Direction d : Iterate.directions) {
                    BlockPos offset = pos.relative(d);
                    if (visited.contains(offset)) continue;
                    BlockState offsetState = level.getBlockState(offset);
                    Direction facing = (Direction)state.getValue((Property)GantryShaftBlock.FACING);
                    if (d.getAxis() == facing.getAxis() && AllBlocks.GANTRY_SHAFT.has(offsetState) && offsetState.getValue((Property)GantryShaftBlock.FACING) == facing) {
                        TEMP_DEFAULT_POSITIONS.add(offset);
                        continue;
                    }
                    if (!AllBlocks.GANTRY_CARRIAGE.has(offsetState) || offsetState.getValue((Property)GantryCarriageBlock.FACING) != d) continue;
                    TEMP_DEFAULT_POSITIONS.add(offset);
                }
                break;
            }
            case 9: {
                Direction offset;
                BlockPos attached;
                StickerBlock ignored = (StickerBlock)block3;
                if (!((Boolean)state.getValue((Property)StickerBlock.EXTENDED)).booleanValue() || visited.contains(attached = pos.relative(offset = (Direction)state.getValue((Property)StickerBlock.FACING))) || BlockMovementChecks.isNotSupportive((BlockState)level.getBlockState(attached), (Direction)offset.getOpposite())) break;
                TEMP_DEFAULT_POSITIONS.add(attached);
                break;
            }
        }
        return TEMP_DEFAULT_POSITIONS;
    }

    public static void addAdditionalBlocks(BlockState state, Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited) {
        for (AdditionalBlocks additional : ADDITIONAL_BLOCK_REGISTRATIONS) {
            additional.addAdditionalBlocks(state, world, pos, visited).forEach(frontier::add);
        }
    }

    public static boolean checkIsBlockAttachedTowards(BlockState state, Level world, BlockPos pos, BlockPos direction) {
        for (AttachedCheck check : ATTACHED_CHECKS) {
            BlockMovementChecks.CheckResult result = check.isBlockAttachedTowards(state, world, pos, direction);
            if (result == BlockMovementChecks.CheckResult.PASS) continue;
            return result.toBoolean();
        }
        return false;
    }

    @ApiStatus.Internal
    public static void register() {
        BlockMovementChecks.registerAttachedCheck(SimBlockMovementChecks::registerDefaultBlockAttachedTowards);
        SimBlockMovementChecks.registerAdditionalBlocks(SimBlockMovementChecks::registerDefaultAdditionalBlocks);
    }

    public static synchronized void registerAttachedCheck(AttachedCheck check) {
        ATTACHED_CHECKS.addFirst((Object)check);
    }

    public static synchronized void registerAdditionalBlocks(AdditionalBlocks additionalBlocks) {
        ADDITIONAL_BLOCK_REGISTRATIONS.addFirst((Object)additionalBlocks);
    }

    @FunctionalInterface
    public static interface AdditionalBlocks {
        public Iterable<BlockPos> addAdditionalBlocks(BlockState var1, Level var2, BlockPos var3, Set<BlockPos> var4);
    }

    @FunctionalInterface
    public static interface AttachedCheck {
        public BlockMovementChecks.CheckResult isBlockAttachedTowards(BlockState var1, Level var2, BlockPos var3, BlockPos var4);
    }
}

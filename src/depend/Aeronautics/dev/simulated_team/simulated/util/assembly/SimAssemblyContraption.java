/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.api.contraption.BlockMovementChecks
 *  com.simibubi.create.content.contraptions.AssemblyException
 *  com.simibubi.create.content.contraptions.chassis.AbstractChassisBlock
 *  com.simibubi.create.content.contraptions.chassis.ChassisBlockEntity
 *  com.simibubi.create.content.contraptions.gantry.GantryCarriageBlock
 *  com.simibubi.create.content.contraptions.glue.SuperGlueEntity
 *  com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock
 *  com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock$PistonState
 *  com.simibubi.create.content.contraptions.piston.MechanicalPistonHeadBlock
 *  com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity
 *  com.simibubi.create.content.kinetics.gantry.GantryShaftBlock
 *  com.simibubi.create.content.trains.bogey.AbstractBogeyBlock
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.UniqueLinkedList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.ChestBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.ChestType
 *  net.minecraft.world.level.block.state.properties.PistonType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.PushReaction
 */
package dev.simulated_team.simulated.util.assembly;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.chassis.AbstractChassisBlock;
import com.simibubi.create.content.contraptions.chassis.ChassisBlockEntity;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlock;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonHeadBlock;
import com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlock;
import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueEntity;
import dev.simulated_team.simulated.index.SimBlockMovementChecks;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.service.SimAssemblyService;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.assembly.SimAssemblyException;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.UniqueLinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;

public class SimAssemblyContraption {
    private static final BlockPos[] DIRECTION_OFFSETS = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 1, 0), new BlockPos(0, -1, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 1, 0), new BlockPos(-1, -1, 0), new BlockPos(1, -1, 0), new BlockPos(-1, 1, 0), new BlockPos(1, 0, 1), new BlockPos(-1, 0, -1), new BlockPos(1, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(0, 1, 1), new BlockPos(0, -1, -1), new BlockPos(0, -1, 1), new BlockPos(0, 1, -1)};
    public final BlockPos anchor;
    public final boolean ignoreEnclosingGlue;
    private final ObjectOpenHashSet<BlockPos> blocks = new ObjectOpenHashSet(4096);
    private final ObjectOpenHashSet<SuperGlueEntity> glueCache = new ObjectOpenHashSet();
    private final ObjectOpenHashSet<HoneyGlueEntity> honeyGlueCache = new ObjectOpenHashSet();

    public SimAssemblyContraption(BlockPos anchor, boolean ignoreEnclosingGlue) {
        this.anchor = anchor;
        this.ignoreEnclosingGlue = ignoreEnclosingGlue;
    }

    public boolean checkAndCacheGlue(LevelAccessor level, BlockPos blockPos, BlockPos offsetDir) {
        BlockPos targetPos = blockPos.offset((Vec3i)offsetDir);
        boolean inHoneyGlue = false;
        boolean containedByAnyHoneyGlue = false;
        for (Object honeyGlueEntity : this.honeyGlueCache) {
            boolean firstContained = ((HoneyGlueEntity)((Object)honeyGlueEntity)).contains(blockPos);
            boolean targetContained = ((HoneyGlueEntity)((Object)honeyGlueEntity)).contains(targetPos);
            containedByAnyHoneyGlue |= firstContained;
            containedByAnyHoneyGlue |= targetContained;
            if (!firstContained || !targetContained) continue;
            inHoneyGlue = true;
        }
        if (containedByAnyHoneyGlue) {
            int honeyGlueRange = (Integer)SimConfigService.INSTANCE.server().assembly.honeyGlueRange.get();
            for (HoneyGlueEntity honeyGlueEntity : level.getEntitiesOfClass(HoneyGlueEntity.class, SuperGlueEntity.span((BlockPos)blockPos, (BlockPos)targetPos).inflate((double)honeyGlueRange))) {
                if (this.anchor != null && this.ignoreEnclosingGlue && honeyGlueEntity.contains(this.anchor) || !honeyGlueEntity.contains(blockPos) || !honeyGlueEntity.contains(targetPos)) continue;
                this.honeyGlueCache.add((Object)honeyGlueEntity);
                inHoneyGlue = true;
            }
        }
        for (SuperGlueEntity glueEntity : this.glueCache) {
            if (!glueEntity.contains(blockPos) || !glueEntity.contains(targetPos)) continue;
            return true;
        }
        for (SuperGlueEntity glueEntity : level.getEntitiesOfClass(SuperGlueEntity.class, SuperGlueEntity.span((BlockPos)blockPos, (BlockPos)targetPos).inflate(16.0))) {
            if (!glueEntity.contains(blockPos) || !glueEntity.contains(targetPos)) continue;
            this.glueCache.add((Object)glueEntity);
            return true;
        }
        return inHoneyGlue;
    }

    public boolean searchMovedStructure(Level level, BlockPos pos) throws AssemblyException {
        int maxBlocksMoved;
        SimAssemblyContraption.addInitialHoneyGlue(level, this, this.anchor, pos, this.ignoreEnclosingGlue);
        UniqueLinkedList frontier = new UniqueLinkedList();
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        Set<BlockPos> immutableVisited = Collections.unmodifiableSet(visited);
        if (!BlockMovementChecks.isBrittle((BlockState)level.getBlockState(pos))) {
            frontier.add(pos);
        }
        for (int limit = maxBlocksMoved = ((Integer)SimConfigService.INSTANCE.server().assembly.maxBlocksMoved.get()).intValue(); limit > 0; --limit) {
            if (frontier.isEmpty()) {
                return true;
            }
            if (this.moveBlock(level, (Queue<BlockPos>)frontier, visited, immutableVisited)) continue;
            return false;
        }
        throw SimAssemblyException.structureTooLarge();
    }

    protected static void addInitialHoneyGlue(Level level, SimAssemblyContraption contraption, BlockPos anchor, BlockPos pos, boolean ignoreEnclosingGlue) {
        int honeyGlueRange = (Integer)SimConfigService.INSTANCE.server().assembly.honeyGlueRange.get();
        for (HoneyGlueEntity honeyGlueEntity : level.getEntitiesOfClass(HoneyGlueEntity.class, SuperGlueEntity.span((BlockPos)pos, (BlockPos)pos).inflate((double)honeyGlueRange))) {
            if (anchor != null ? ignoreEnclosingGlue && honeyGlueEntity.contains(anchor) || !honeyGlueEntity.contains(pos) && !honeyGlueEntity.contains(anchor) : !honeyGlueEntity.contains(pos)) continue;
            contraption.honeyGlueCache.add((Object)honeyGlueEntity);
        }
    }

    protected boolean moveBlock(Level world, Queue<BlockPos> frontier, Set<BlockPos> visited, Set<BlockPos> immutableVisitedView) throws AssemblyException {
        Direction offset;
        Object attached;
        BlockEntity blockEntity;
        BlockPos pos = frontier.poll();
        if (pos == null) {
            return false;
        }
        visited.add(pos);
        if (world.isOutsideBuildHeight(pos)) {
            return true;
        }
        if (!world.isLoaded(pos)) {
            throw AssemblyException.unloadedChunk((BlockPos)pos);
        }
        if (this.isAnchoringBlockAt(pos)) {
            return true;
        }
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) {
            return true;
        }
        if (!this.movementAllowed(state, world, pos)) {
            throw AssemblyException.unmovableBlock((BlockPos)pos, (BlockState)state);
        }
        if (state.getBlock() instanceof AbstractChassisBlock && !this.moveChassis(world, pos, null, frontier, visited)) {
            return false;
        }
        if (SimBlocks.SWIVEL_BEARING.has(state)) {
            this.moveSwivelBearing(world, pos, frontier, visited, state);
        }
        if ((blockEntity = world.getBlockEntity(pos)) instanceof ChainConveyorBlockEntity) {
            ChainConveyorBlockEntity ccbe = (ChainConveyorBlockEntity)blockEntity;
            ccbe.notifyConnectedToValidate();
        }
        if (state.hasProperty((Property)ChestBlock.TYPE) && state.hasProperty((Property)ChestBlock.FACING) && state.getValue((Property)ChestBlock.TYPE) != ChestType.SINGLE && !visited.contains(attached = pos.relative(offset = ChestBlock.getConnectedDirection((BlockState)state)))) {
            frontier.add((BlockPos)attached);
        }
        if ((attached = state.getBlock()) instanceof AbstractBogeyBlock) {
            AbstractBogeyBlock bogey = (AbstractBogeyBlock)attached;
            for (Direction d : bogey.getStickySurfaces((BlockGetter)world, pos, state)) {
                if (visited.contains(pos.relative(d))) continue;
                frontier.add(pos.relative(d));
            }
        }
        BlockPos posDown = pos.below();
        BlockState stateBelow = world.getBlockState(posDown);
        if (!visited.contains(posDown) && AllBlocks.CART_ASSEMBLER.has(stateBelow)) {
            frontier.add(posDown);
        }
        SimBlockMovementChecks.addAdditionalBlocks(state, world, pos, frontier, immutableVisitedView);
        for (BlockPos offsetDirection : DIRECTION_OFFSETS) {
            boolean canStick;
            int absTotal = Math.abs(offsetDirection.getX()) + Math.abs(offsetDirection.getY()) + Math.abs(offsetDirection.getZ());
            Direction offsetDirectionNullable = absTotal == 1 ? Direction.fromDelta((int)offsetDirection.getX(), (int)offsetDirection.getY(), (int)offsetDirection.getZ()) : null;
            BlockPos offsetPos = pos.offset((Vec3i)offsetDirection);
            BlockState blockState = world.getBlockState(offsetPos);
            if (this.isAnchoringBlockAt(offsetPos) || !this.movementAllowed(blockState, world, offsetPos)) continue;
            boolean wasVisited = visited.contains(offsetPos);
            boolean faceHasGlue = this.checkAndCacheGlue((LevelAccessor)world, pos, offsetDirection);
            boolean blockAttachedTowardsFace = offsetDirectionNullable != null && BlockMovementChecks.isBlockAttachedTowards((BlockState)blockState, (Level)world, (BlockPos)offsetPos, (Direction)offsetDirectionNullable.getOpposite());
            blockAttachedTowardsFace |= SimBlockMovementChecks.checkIsBlockAttachedTowards(blockState, world, offsetPos, offsetDirection.multiply(-1));
            boolean brittle = BlockMovementChecks.isBrittle((BlockState)blockState);
            boolean bl = canStick = !brittle && SimAssemblyService.INSTANCE.canStickTo(state, blockState) && SimAssemblyService.INSTANCE.canStickTo(blockState, state);
            if (canStick) {
                if (state.getPistonPushReaction() == PushReaction.PUSH_ONLY || blockState.getPistonPushReaction() == PushReaction.PUSH_ONLY) {
                    canStick = false;
                }
                if (offsetDirectionNullable != null) {
                    if (BlockMovementChecks.isNotSupportive((BlockState)state, (Direction)offsetDirectionNullable)) {
                        canStick = false;
                    }
                    if (BlockMovementChecks.isNotSupportive((BlockState)blockState, (Direction)offsetDirectionNullable.getOpposite())) {
                        canStick = false;
                    }
                }
            }
            if (wasVisited || !canStick && !blockAttachedTowardsFace && !faceHasGlue) continue;
            frontier.add(offsetPos);
        }
        this.blocks.add((Object)pos);
        if (this.blocks.size() <= (Integer)SimConfigService.INSTANCE.server().assembly.maxBlocksMoved.get()) {
            return true;
        }
        throw SimAssemblyException.structureTooLarge();
    }

    private void moveSwivelBearing(Level level, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {
        Direction facing = (Direction)state.getValue((Property)SwivelBearingBlock.FACING);
        BlockPos attachPos = pos.relative(facing);
        SimAssemblyContraption.addInitialHoneyGlue(level, this, pos, attachPos, true);
        frontier.add(attachPos);
    }

    protected void movePistonHead(Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {
        BlockPos attached;
        Direction direction = (Direction)state.getValue((Property)MechanicalPistonHeadBlock.FACING);
        BlockPos offset = pos.relative(direction.getOpposite());
        if (!visited.contains(offset)) {
            Direction pistonFacing;
            BlockState blockState = world.getBlockState(offset);
            if (MechanicalPistonBlock.isExtensionPole((BlockState)blockState) && ((Direction)blockState.getValue((Property)PistonExtensionPoleBlock.FACING)).getAxis() == direction.getAxis()) {
                frontier.add(offset);
            }
            if (blockState.getBlock() instanceof MechanicalPistonBlock && (pistonFacing = (Direction)blockState.getValue((Property)MechanicalPistonBlock.FACING)) == direction && blockState.getValue((Property)MechanicalPistonBlock.STATE) == MechanicalPistonBlock.PistonState.EXTENDED) {
                frontier.add(offset);
            }
        }
        if (state.getValue((Property)MechanicalPistonHeadBlock.TYPE) == PistonType.STICKY && !visited.contains(attached = pos.relative(direction))) {
            frontier.add(attached);
        }
    }

    protected void movePistonPole(Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {
        for (Direction d : Iterate.directionsInAxis((Direction.Axis)((Direction)state.getValue((Property)PistonExtensionPoleBlock.FACING)).getAxis())) {
            Direction pistonFacing;
            BlockPos offset = pos.relative(d);
            if (visited.contains(offset)) continue;
            BlockState blockState = world.getBlockState(offset);
            if (MechanicalPistonBlock.isExtensionPole((BlockState)blockState) && ((Direction)blockState.getValue((Property)PistonExtensionPoleBlock.FACING)).getAxis() == d.getAxis()) {
                frontier.add(offset);
            }
            if (MechanicalPistonBlock.isPistonHead((BlockState)blockState) && ((Direction)blockState.getValue((Property)MechanicalPistonHeadBlock.FACING)).getAxis() == d.getAxis()) {
                frontier.add(offset);
            }
            if (!(blockState.getBlock() instanceof MechanicalPistonBlock) || (pistonFacing = (Direction)blockState.getValue((Property)MechanicalPistonBlock.FACING)) != d && (pistonFacing != d.getOpposite() || blockState.getValue((Property)MechanicalPistonBlock.STATE) != MechanicalPistonBlock.PistonState.EXTENDED)) continue;
            frontier.add(offset);
        }
    }

    protected void moveGantryPinion(Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {
        BlockPos offset = pos.relative((Direction)state.getValue((Property)GantryCarriageBlock.FACING));
        if (!visited.contains(offset)) {
            frontier.add(offset);
        }
        Direction.Axis rotationAxis = ((IRotate)state.getBlock()).getRotationAxis(state);
        for (Direction d : Iterate.directionsInAxis((Direction.Axis)rotationAxis)) {
            offset = pos.relative(d);
            BlockState offsetState = world.getBlockState(offset);
            if (!AllBlocks.GANTRY_SHAFT.has(offsetState) || ((Direction)offsetState.getValue((Property)GantryShaftBlock.FACING)).getAxis() != d.getAxis() || visited.contains(offset)) continue;
            frontier.add(offset);
        }
    }

    protected void moveGantryShaft(Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {
        for (Direction d : Iterate.directions) {
            BlockPos offset = pos.relative(d);
            if (visited.contains(offset)) continue;
            BlockState offsetState = world.getBlockState(offset);
            Direction facing = (Direction)state.getValue((Property)GantryShaftBlock.FACING);
            if (d.getAxis() == facing.getAxis() && AllBlocks.GANTRY_SHAFT.has(offsetState) && offsetState.getValue((Property)GantryShaftBlock.FACING) == facing) {
                frontier.add(offset);
                continue;
            }
            if (!AllBlocks.GANTRY_CARRIAGE.has(offsetState) || offsetState.getValue((Property)GantryCarriageBlock.FACING) != d) continue;
            frontier.add(offset);
        }
    }

    private boolean moveMechanicalPiston(Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) throws AssemblyException {
        BlockState poleState;
        Direction direction = (Direction)state.getValue((Property)MechanicalPistonBlock.FACING);
        MechanicalPistonBlock.PistonState pistonState = (MechanicalPistonBlock.PistonState)state.getValue((Property)MechanicalPistonBlock.STATE);
        if (pistonState == MechanicalPistonBlock.PistonState.MOVING) {
            return false;
        }
        BlockPos offset = pos.relative(direction.getOpposite());
        if (!visited.contains(offset) && AllBlocks.PISTON_EXTENSION_POLE.has(poleState = world.getBlockState(offset)) && ((Direction)poleState.getValue((Property)PistonExtensionPoleBlock.FACING)).getAxis() == direction.getAxis()) {
            frontier.add(offset);
        }
        if ((pistonState == MechanicalPistonBlock.PistonState.EXTENDED || MechanicalPistonBlock.isStickyPiston((BlockState)state)) && !visited.contains(offset = pos.relative(direction))) {
            frontier.add(offset);
        }
        return true;
    }

    private boolean moveChassis(Level world, BlockPos pos, Direction movementDirection, Queue<BlockPos> frontier, Set<BlockPos> visited) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ChassisBlockEntity)) {
            return false;
        }
        ChassisBlockEntity chassis = (ChassisBlockEntity)be;
        chassis.addAttachedChasses(frontier, visited);
        List includedBlockPositions = chassis.getIncludedBlockPositions(movementDirection, false);
        if (includedBlockPositions == null) {
            return false;
        }
        for (BlockPos blockPos : includedBlockPositions) {
            if (visited.contains(blockPos)) continue;
            frontier.add(blockPos);
        }
        return true;
    }

    protected boolean movementAllowed(BlockState state, Level world, BlockPos pos) {
        return state.getDestroySpeed((BlockGetter)world, pos) != -1.0f && !state.is(SimTags.Blocks.NON_MOVABLE);
    }

    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return pos.equals((Object)this.anchor);
    }

    public Collection<SuperGlueEntity> getGlues() {
        return this.glueCache;
    }

    public Collection<HoneyGlueEntity> getHoneyGlues() {
        return this.honeyGlueCache;
    }

    public Collection<BlockPos> getBlocks() {
        return this.blocks;
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.WoolCarpetBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.PistonType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.contraptions.piston;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.Queue;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class PistonContraption
extends TranslatingContraption {
    protected int extensionLength;
    protected int initialExtensionProgress;
    protected Direction orientation;
    private AABB pistonExtensionCollisionBox;
    private boolean retract;

    @Override
    public ContraptionType getType() {
        return (ContraptionType)AllContraptionTypes.PISTON.value();
    }

    public PistonContraption() {
    }

    public PistonContraption(Direction direction, boolean retract) {
        this.orientation = direction;
        this.retract = retract;
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        if (!this.collectExtensions(world, pos, this.orientation)) {
            return false;
        }
        int count = this.blocks.size();
        if (!this.searchMovedStructure(world, this.anchor, this.retract ? this.orientation.getOpposite() : this.orientation)) {
            return false;
        }
        this.bounds = this.blocks.size() == count ? this.pistonExtensionCollisionBox : this.bounds.minmax(this.pistonExtensionCollisionBox);
        this.startMoving(world);
        return true;
    }

    private boolean collectExtensions(Level world, BlockPos pos, Direction direction) throws AssemblyException {
        ArrayList<StructureTemplate.StructureBlockInfo> poles = new ArrayList<StructureTemplate.StructureBlockInfo>();
        BlockPos actualStart = pos;
        BlockState nextBlock = world.getBlockState(actualStart.relative(direction));
        int extensionsInFront = 0;
        BlockState blockState = world.getBlockState(pos);
        boolean sticky = MechanicalPistonBlock.isStickyPiston(blockState);
        if (!MechanicalPistonBlock.isPiston(blockState)) {
            return false;
        }
        if (blockState.getValue(MechanicalPistonBlock.STATE) == MechanicalPistonBlock.PistonState.EXTENDED) {
            while (PistonExtensionPoleBlock.PlacementHelper.get().matchesAxis(nextBlock, direction.getAxis()) || MechanicalPistonBlock.isPistonHead(nextBlock) && nextBlock.getValue((Property)BlockStateProperties.FACING) == direction) {
                actualStart = actualStart.relative(direction);
                poles.add(new StructureTemplate.StructureBlockInfo(actualStart, (BlockState)nextBlock.setValue((Property)BlockStateProperties.FACING, (Comparable)direction), null));
                ++extensionsInFront;
                if (MechanicalPistonBlock.isPistonHead(nextBlock)) break;
                nextBlock = world.getBlockState(actualStart.relative(direction));
                if (extensionsInFront <= MechanicalPistonBlock.maxAllowedPistonPoles()) continue;
                throw AssemblyException.tooManyPistonPoles();
            }
        }
        if (extensionsInFront == 0) {
            poles.add(new StructureTemplate.StructureBlockInfo(pos, (BlockState)((BlockState)AllBlocks.MECHANICAL_PISTON_HEAD.getDefaultState().setValue((Property)BlockStateProperties.FACING, (Comparable)direction)).setValue((Property)BlockStateProperties.PISTON_TYPE, (Comparable)(sticky ? PistonType.STICKY : PistonType.DEFAULT)), null));
        } else {
            poles.add(new StructureTemplate.StructureBlockInfo(pos, (BlockState)AllBlocks.PISTON_EXTENSION_POLE.getDefaultState().setValue((Property)BlockStateProperties.FACING, (Comparable)direction), null));
        }
        BlockPos end = pos;
        nextBlock = world.getBlockState(end.relative(direction.getOpposite()));
        int extensionsInBack = 0;
        while (PistonExtensionPoleBlock.PlacementHelper.get().matchesAxis(nextBlock, direction.getAxis())) {
            end = end.relative(direction.getOpposite());
            poles.add(new StructureTemplate.StructureBlockInfo(end, (BlockState)nextBlock.setValue((Property)BlockStateProperties.FACING, (Comparable)direction), null));
            nextBlock = world.getBlockState(end.relative(direction.getOpposite()));
            if (extensionsInFront + ++extensionsInBack <= MechanicalPistonBlock.maxAllowedPistonPoles()) continue;
            throw AssemblyException.tooManyPistonPoles();
        }
        this.anchor = pos.relative(direction, this.initialExtensionProgress + 1);
        this.extensionLength = extensionsInBack + extensionsInFront;
        this.initialExtensionProgress = extensionsInFront;
        this.pistonExtensionCollisionBox = new AABB(Vec3.atLowerCornerOf((Vec3i)BlockPos.ZERO.relative(direction, -1)), Vec3.atLowerCornerOf((Vec3i)BlockPos.ZERO.relative(direction, -this.extensionLength - 1))).expandTowards(1.0, 1.0, 1.0);
        if (this.extensionLength == 0) {
            throw AssemblyException.noPistonPoles();
        }
        this.bounds = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        for (StructureTemplate.StructureBlockInfo pole : poles) {
            BlockPos relPos = pole.pos().relative(direction, -extensionsInFront);
            BlockPos localPos = relPos.subtract((Vec3i)this.anchor);
            this.getBlocks().put(localPos, new StructureTemplate.StructureBlockInfo(localPos, pole.state(), null));
        }
        return true;
    }

    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return this.pistonExtensionCollisionBox.contains(VecHelper.getCenterOf((Vec3i)pos.subtract((Vec3i)this.anchor)));
    }

    @Override
    protected boolean addToInitialFrontier(Level world, BlockPos pos, Direction direction, Queue<BlockPos> frontier) throws AssemblyException {
        boolean retracting;
        frontier.clear();
        boolean sticky = MechanicalPistonBlock.isStickyPiston(world.getBlockState(pos.relative(this.orientation, -1)));
        boolean bl = retracting = direction != this.orientation;
        if (retracting && !sticky) {
            return true;
        }
        for (int offset = 0; offset <= (Integer)AllConfigs.server().kinetics.maxChassisRange.get(); ++offset) {
            if (offset == 1 && retracting) {
                return true;
            }
            BlockPos currentPos = pos.relative(this.orientation, offset + this.initialExtensionProgress);
            if (retracting && world.isOutsideBuildHeight(currentPos)) {
                return true;
            }
            if (!world.isLoaded(currentPos)) {
                throw AssemblyException.unloadedChunk(currentPos);
            }
            BlockState state = world.getBlockState(currentPos);
            if (!BlockMovementChecks.isMovementNecessary(state, world, currentPos)) {
                return true;
            }
            if (BlockMovementChecks.isBrittle(state) && !(state.getBlock() instanceof WoolCarpetBlock)) {
                return true;
            }
            if (MechanicalPistonBlock.isPistonHead(state) && state.getValue((Property)BlockStateProperties.FACING) == direction.getOpposite()) {
                return true;
            }
            if (!BlockMovementChecks.isMovementAllowed(state, world, currentPos)) {
                if (retracting) {
                    return true;
                }
                throw AssemblyException.unmovableBlock(currentPos, state);
            }
            if (retracting && state.getPistonPushReaction() == PushReaction.PUSH_ONLY) {
                return true;
            }
            frontier.add(currentPos);
            if (!BlockMovementChecks.isNotSupportive(state, this.orientation)) continue;
            return true;
        }
        return true;
    }

    @Override
    public void addBlock(Level level, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture) {
        super.addBlock(level, pos.relative(this.orientation, -this.initialExtensionProgress), capture);
    }

    @Override
    public BlockPos toLocalPos(BlockPos globalPos) {
        return globalPos.subtract((Vec3i)this.anchor).relative(this.orientation, -this.initialExtensionProgress);
    }

    @Override
    protected boolean customBlockPlacement(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockPos pistonPos = this.anchor.relative(this.orientation, -1);
        BlockState pistonState = world.getBlockState(pistonPos);
        BlockEntity be = world.getBlockEntity(pistonPos);
        if (pos.equals((Object)pistonPos)) {
            if (be == null || be.isRemoved()) {
                return true;
            }
            if (!MechanicalPistonBlock.isExtensionPole(state) && MechanicalPistonBlock.isPiston(pistonState)) {
                world.setBlock(pistonPos, (BlockState)pistonState.setValue(MechanicalPistonBlock.STATE, (Comparable)((Object)MechanicalPistonBlock.PistonState.RETRACTED)), 19);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean customBlockRemoval(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockPos pistonPos = this.anchor.relative(this.orientation, -1);
        BlockState blockState = world.getBlockState(pos);
        if (pos.equals((Object)pistonPos) && MechanicalPistonBlock.isPiston(blockState)) {
            world.setBlock(pos, (BlockState)blockState.setValue(MechanicalPistonBlock.STATE, (Comparable)((Object)MechanicalPistonBlock.PistonState.MOVING)), 82);
            return true;
        }
        return false;
    }

    @Override
    public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
        super.readNBT(world, nbt, spawnData);
        this.initialExtensionProgress = nbt.getInt("InitialLength");
        this.extensionLength = nbt.getInt("ExtensionLength");
        this.orientation = Direction.from3DDataValue((int)nbt.getInt("Orientation"));
    }

    @Override
    public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) {
        CompoundTag tag = super.writeNBT(registries, spawnPacket);
        tag.putInt("InitialLength", this.initialExtensionProgress);
        tag.putInt("ExtensionLength", this.extensionLength);
        tag.putInt("Orientation", this.orientation.get3DDataValue());
        return tag;
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;

public class BearingContraption
extends Contraption {
    protected int sailBlocks;
    protected Direction facing;
    private boolean isWindmill;

    public BearingContraption() {
    }

    public BearingContraption(boolean isWindmill, Direction facing) {
        this.isWindmill = isWindmill;
        this.facing = facing;
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        BlockPos offset = pos.relative(this.facing);
        if (!this.searchMovedStructure(world, offset, null)) {
            return false;
        }
        this.startMoving(world);
        this.expandBoundsAroundAxis(this.facing.getAxis());
        if (this.isWindmill && this.sailBlocks < (Integer)AllConfigs.server().kinetics.minimumWindmillSails.get()) {
            throw AssemblyException.notEnoughSails(this.sailBlocks);
        }
        return !this.blocks.isEmpty();
    }

    @Override
    public ContraptionType getType() {
        return (ContraptionType)AllContraptionTypes.BEARING.value();
    }

    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return pos.equals((Object)this.anchor.relative(this.facing.getOpposite()));
    }

    @Override
    public void addBlock(Level level, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture) {
        BlockPos localPos = pos.subtract((Vec3i)this.anchor);
        if (!this.getBlocks().containsKey(localPos) && AllTags.AllBlockTags.WINDMILL_SAILS.matches(this.getSailBlock(capture))) {
            ++this.sailBlocks;
        }
        super.addBlock(level, pos, capture);
    }

    private BlockState getSailBlock(Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture) {
        Object object;
        BlockState state = ((StructureTemplate.StructureBlockInfo)capture.getKey()).state();
        if (AllBlocks.COPYCAT_PANEL.has(state) && (object = capture.getRight()) instanceof CopycatBlockEntity) {
            CopycatBlockEntity cbe = (CopycatBlockEntity)object;
            return cbe.getMaterial();
        }
        return state;
    }

    @Override
    public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) {
        CompoundTag tag = super.writeNBT(registries, spawnPacket);
        tag.putInt("Sails", this.sailBlocks);
        tag.putInt("Facing", this.facing.get3DDataValue());
        return tag;
    }

    @Override
    public void readNBT(Level world, CompoundTag tag, boolean spawnData) {
        this.sailBlocks = tag.getInt("Sails");
        this.facing = Direction.from3DDataValue((int)tag.getInt("Facing"));
        super.readNBT(world, tag, spawnData);
    }

    public int getSailBlocks() {
        return this.sailBlocks;
    }

    public Direction getFacing() {
        return this.facing;
    }

    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        if (facing.getOpposite() == this.facing && BlockPos.ZERO.equals((Object)localPos)) {
            return false;
        }
        return facing.getAxis() == this.facing.getAxis();
    }
}

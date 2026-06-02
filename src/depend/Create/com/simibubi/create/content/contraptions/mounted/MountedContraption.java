/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.wrapper.InvWrapper
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.contraptions.mounted;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlock;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlockEntity;
import java.util.Iterator;
import java.util.Queue;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.tuple.Pair;

public class MountedContraption
extends Contraption {
    public CartAssemblerBlockEntity.CartMovementMode rotationMode;
    public AbstractMinecart connectedCart;

    public MountedContraption() {
        this(CartAssemblerBlockEntity.CartMovementMode.ROTATE);
    }

    public MountedContraption(CartAssemblerBlockEntity.CartMovementMode mode) {
        this.rotationMode = mode;
    }

    @Override
    public ContraptionType getType() {
        return (ContraptionType)AllContraptionTypes.MOUNTED.value();
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        BlockState state = world.getBlockState(pos);
        if (!state.hasProperty(CartAssemblerBlock.RAIL_SHAPE)) {
            return false;
        }
        if (!this.searchMovedStructure(world, pos, null)) {
            return false;
        }
        Direction.Axis axis = state.getValue(CartAssemblerBlock.RAIL_SHAPE) == RailShape.EAST_WEST ? Direction.Axis.X : Direction.Axis.Z;
        this.addBlock(world, pos, (Pair<StructureTemplate.StructureBlockInfo, BlockEntity>)Pair.of((Object)new StructureTemplate.StructureBlockInfo(pos, (BlockState)AllBlocks.MINECART_ANCHOR.getDefaultState().setValue((Property)BlockStateProperties.HORIZONTAL_AXIS, (Comparable)axis), null), null));
        return this.blocks.size() != 1;
    }

    @Override
    protected boolean addToInitialFrontier(Level world, BlockPos pos, Direction direction, Queue<BlockPos> frontier) {
        frontier.clear();
        frontier.add(pos.above());
        return true;
    }

    @Override
    protected Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
        Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair = super.capture(world, pos);
        StructureTemplate.StructureBlockInfo capture = (StructureTemplate.StructureBlockInfo)pair.getKey();
        if (!AllBlocks.CART_ASSEMBLER.has(capture.state())) {
            return pair;
        }
        Pair anchorSwap = Pair.of((Object)new StructureTemplate.StructureBlockInfo(pos, CartAssemblerBlock.createAnchor(capture.state()), null), (Object)((BlockEntity)pair.getValue()));
        if (pos.equals((Object)this.anchor) || this.connectedCart != null) {
            return anchorSwap;
        }
        for (Direction.Axis axis : Iterate.axes) {
            AbstractMinecart abstractMinecartEntity;
            if (axis.isVertical() || !VecHelper.onSameAxis((BlockPos)this.anchor, (BlockPos)pos, (Direction.Axis)axis)) continue;
            Iterator iterator = world.getEntitiesOfClass(AbstractMinecart.class, new AABB(pos)).iterator();
            while (iterator.hasNext() && CartAssemblerBlock.canAssembleTo(abstractMinecartEntity = (AbstractMinecart)iterator.next())) {
                this.connectedCart = abstractMinecartEntity;
                this.connectedCart.setPos((double)pos.getX() + 0.5, (double)pos.getY(), (double)((float)pos.getZ() + 0.5f));
            }
        }
        return anchorSwap;
    }

    @Override
    protected boolean movementAllowed(BlockState state, Level world, BlockPos pos) {
        if (!pos.equals((Object)this.anchor) && AllBlocks.CART_ASSEMBLER.has(state)) {
            return this.testSecondaryCartAssembler(world, state, pos);
        }
        return super.movementAllowed(state, world, pos);
    }

    protected boolean testSecondaryCartAssembler(Level world, BlockState state, BlockPos pos) {
        for (Direction.Axis axis : Iterate.axes) {
            AbstractMinecart abstractMinecartEntity;
            Iterator iterator;
            if (axis.isVertical() || !VecHelper.onSameAxis((BlockPos)this.anchor, (BlockPos)pos, (Direction.Axis)axis) || !(iterator = world.getEntitiesOfClass(AbstractMinecart.class, new AABB(pos)).iterator()).hasNext() || !CartAssemblerBlock.canAssembleTo(abstractMinecartEntity = (AbstractMinecart)iterator.next())) continue;
            return true;
        }
        return false;
    }

    @Override
    public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) {
        CompoundTag tag = super.writeNBT(registries, spawnPacket);
        NBTHelper.writeEnum((CompoundTag)tag, (String)"RotationMode", (Enum)this.rotationMode);
        return tag;
    }

    @Override
    public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
        this.rotationMode = (CartAssemblerBlockEntity.CartMovementMode)NBTHelper.readEnum((CompoundTag)nbt, (String)"RotationMode", CartAssemblerBlockEntity.CartMovementMode.class);
        super.readNBT(world, nbt, spawnData);
    }

    @Override
    protected boolean customBlockPlacement(LevelAccessor world, BlockPos pos, BlockState state) {
        return AllBlocks.MINECART_ANCHOR.has(state);
    }

    @Override
    protected boolean customBlockRemoval(LevelAccessor world, BlockPos pos, BlockState state) {
        return AllBlocks.MINECART_ANCHOR.has(state);
    }

    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        return true;
    }

    public void addExtraInventories(Entity cart) {
        if (cart instanceof Container) {
            Container container = (Container)cart;
            this.storage.attachExternal((IItemHandlerModifiable)new InvWrapper(container));
        }
    }
}

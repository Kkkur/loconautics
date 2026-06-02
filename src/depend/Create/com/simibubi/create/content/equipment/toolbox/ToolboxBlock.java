/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.equipment.toolbox;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

public class ToolboxBlock
extends HorizontalDirectionalBlock
implements SimpleWaterloggedBlock,
IBE<ToolboxBlockEntity> {
    protected final DyeColor color;
    public static final MapCodec<ToolboxBlock> CODEC = ToolboxBlock.simpleCodec(p -> new ToolboxBlock((BlockBehaviour.Properties)p, DyeColor.WHITE));

    public ToolboxBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{BlockStateProperties.WATERLOGGED}).add(new Property[]{FACING}));
    }

    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isClientSide) {
            return;
        }
        if (stack == null) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> {
            be.readInventory((ToolboxInventory)((Object)((Object)stack.get(AllDataComponents.TOOLBOX_INVENTORY))));
            if (stack.has(AllDataComponents.TOOLBOX_UUID)) {
                be.setUniqueId((UUID)stack.get(AllDataComponents.TOOLBOX_UUID));
            }
            if (stack.has(DataComponents.CUSTOM_NAME)) {
                be.setCustomName(stack.getHoverName());
            }
        });
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moving) {
        if (!(!state.hasBlockEntity() || newState.hasBlockEntity() && newState.getBlock() instanceof ToolboxBlock)) {
            world.removeBlockEntity(pos);
        }
    }

    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        if (player instanceof FakePlayer) {
            return;
        }
        if (world.isClientSide) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)world, pos, ToolboxBlockEntity::unequipTracked);
        if (world instanceof ServerLevel) {
            ItemStack cloneItemStack = this.getCloneItemStack((LevelReader)world, pos, state);
            this.withBlockEntityDo((BlockGetter)world, pos, i -> cloneItemStack.applyComponents(i.collectComponents()));
            world.destroyBlock(pos, false);
            if (world.getBlockState(pos) != state) {
                player.getInventory().placeItemBackInInventory(cloneItemStack);
            }
        }
    }

    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack item = new ItemStack((ItemLike)this);
        Optional blockEntityOptional = this.getBlockEntityOptional((BlockGetter)level, pos);
        blockEntityOptional.map(tb -> (ToolboxInventory)((Object)((Object)item.set(AllDataComponents.TOOLBOX_INVENTORY, (Object)tb.inventory))));
        blockEntityOptional.map(ToolboxBlockEntity::getUniqueId).ifPresent(uid -> item.set(AllDataComponents.TOOLBOX_UUID, uid));
        blockEntityOptional.map(ToolboxBlockEntity::getCustomName).ifPresent(name -> item.set(DataComponents.CUSTOM_NAME, name));
        return item;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        if (((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
            world.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)world));
        }
        return state;
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return AllShapes.TOOLBOX.get((Direction)state.getValue((Property)FACING));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player == null || player.isCrouching()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        DyeColor color = DyeColor.getColor((ItemStack)stack);
        if (color != null && color != this.color) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            BlockState newState = BlockHelper.copyProperties(state, AllBlocks.TOOLBOXES.get(color).getDefaultState());
            level.setBlockAndUpdate(pos, newState);
            return ItemInteractionResult.SUCCESS;
        }
        if (player instanceof FakePlayer) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        this.withBlockEntityDo((BlockGetter)level, pos, toolbox -> player.openMenu((MenuProvider)toolbox, toolbox::sendToMenu));
        return ItemInteractionResult.SUCCESS;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return (BlockState)((BlockState)super.getStateForPlacement(context).setValue((Property)FACING, (Comparable)context.getHorizontalDirection().getOpposite())).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(ifluidstate.getType() == Fluids.WATER));
    }

    @Override
    public Class<ToolboxBlockEntity> getBlockEntityClass() {
        return ToolboxBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ToolboxBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.TOOLBOX.get();
    }

    public DyeColor getColor() {
        return this.color;
    }

    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        return ItemHelper.calcRedstoneFromBlockEntity(this, pLevel, pPos);
    }

    @NotNull
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}

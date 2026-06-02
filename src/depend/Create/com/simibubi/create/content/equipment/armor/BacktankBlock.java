/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Holder
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.level.storage.loot.LootParams$Builder
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.common.util.FakePlayer
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllEnchantments;
import com.simibubi.create.AllShapes;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayer;

public class BacktankBlock
extends HorizontalKineticBlock
implements IBE<BacktankBlockEntity>,
SimpleWaterloggedBlock,
SpecialBlockItemRequirement {
    public BacktankBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{BlockStateProperties.WATERLOGGED});
        super.createBlockStateDefinition(builder);
    }

    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return this.getBlockEntityOptional((BlockGetter)world, pos).map(BacktankBlockEntity::getComparatorOutput).orElse(0);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        if (((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
            world.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)world));
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return (BlockState)super.getStateForPlacement(context).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getType() == Fluids.WATER));
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.UP;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isClientSide) {
            return;
        }
        if (stack == null) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> {
            be.setCapacityEnchantLevel(stack.getEnchantmentLevel(worldIn.holderOrThrow(AllEnchantments.CAPACITY)));
            be.setAirLevel((Integer)stack.getOrDefault(AllDataComponents.BACKTANK_AIR, (Object)0));
            if (stack.has(DataComponents.CUSTOM_NAME)) {
                be.setCustomName(stack.getHoverName());
            }
            be.setComponentPatch(stack.getComponentsPatch());
        });
    }

    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
        List lootDrops = super.getDrops(pState, pBuilder);
        BlockEntity blockEntity = (BlockEntity)pBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(blockEntity instanceof BacktankBlockEntity)) {
            return lootDrops;
        }
        BacktankBlockEntity bbe = (BacktankBlockEntity)blockEntity;
        DataComponentPatch components = bbe.getComponentPatch().forget(c -> c.equals(AllDataComponents.BACKTANK_AIR));
        if (components.isEmpty()) {
            return lootDrops;
        }
        return lootDrops.stream().peek(stack -> {
            if (stack.getItem() instanceof BacktankItem) {
                stack.applyComponents(components);
            }
        }).toList();
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (player instanceof FakePlayer) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (player.getMainHandItem().getItem() instanceof BlockItem) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.75f, 1.0f);
            player.setItemSlot(EquipmentSlot.CHEST, this.getCloneItemStack((LevelReader)level, pos, state));
            level.destroyBlock(pos, false);
        }
        return ItemInteractionResult.SUCCESS;
    }

    public ItemStack getCloneItemStack(LevelReader pLevel, BlockPos pos, BlockState state) {
        Item item = this.asItem();
        if (item instanceof BacktankItem.BacktankBlockItem) {
            BacktankItem.BacktankBlockItem placeable = (BacktankItem.BacktankBlockItem)item;
            item = placeable.getActualItem();
        }
        Optional blockEntityOptional = this.getBlockEntityOptional((BlockGetter)pLevel, pos);
        DataComponentPatch components = blockEntityOptional.map(BacktankBlockEntity::getComponentPatch).orElse(DataComponentPatch.EMPTY);
        int air = blockEntityOptional.map(BacktankBlockEntity::getAirLevel).orElse(0);
        ItemStack stack = new ItemStack((Holder)item.builtInRegistryHolder(), 1, components);
        stack.set(AllDataComponents.BACKTANK_AIR, (Object)air);
        return stack;
    }

    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return AllShapes.BACKTANK;
    }

    @Override
    public Class<BacktankBlockEntity> getBlockEntityClass() {
        return BacktankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BacktankBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.BACKTANK.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity blockEntity) {
        Item item = this.asItem();
        if (item instanceof BacktankItem.BacktankBlockItem) {
            BacktankItem.BacktankBlockItem placeable = (BacktankItem.BacktankBlockItem)item;
            item = placeable.getActualItem();
        }
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, item);
    }
}

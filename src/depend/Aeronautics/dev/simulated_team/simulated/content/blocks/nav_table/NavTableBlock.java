/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement$ItemUseType
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement$StackRequirement
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement$StrictNbtStackRequirement
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.nav_table;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimDataComponents;
import dev.simulated_team.simulated.multiloader.CommonRedstoneBlock;
import dev.simulated_team.simulated.multiloader.inventory.ContainerSlot;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class NavTableBlock
extends DirectionalBlock
implements IBE<NavTableBlockEntity>,
IWrenchable,
CommonRedstoneBlock,
SpecialBlockItemRequirement {
    public static final MapCodec<NavTableBlock> CODEC = NavTableBlock.simpleCodec(NavTableBlock::new);

    public NavTableBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{FACING}));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        return (BlockState)super.getStateForPlacement(context).setValue((Property)FACING, (Comparable)clickedFace);
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide() && this.canSwitchStacks(itemStack, level, blockPos)) {
            return ItemInteractionResult.SUCCESS;
        }
        if (this.switchStacks(level, blockPos, player, interactionHand)) {
            return ItemInteractionResult.CONSUME;
        }
        return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    private boolean canSwitchStacks(ItemStack heldStack, Level level, BlockPos pos) {
        NavTableBlockEntity blockEntity = (NavTableBlockEntity)level.getBlockEntity(pos);
        if (blockEntity != null) {
            return heldStack.has(SimDataComponents.TARGET) || !blockEntity.getHeldItem().isEmpty() && heldStack.isEmpty();
        }
        return false;
    }

    private boolean switchStacks(Level level, BlockPos pos, Player player, InteractionHand hand) {
        boolean passed = false;
        ItemStack heldItem = player.getItemInHand(hand);
        NavigationTarget navigationTarget = NavigationTarget.ofStack(heldItem);
        if (heldItem.isEmpty() || navigationTarget != null) {
            this.withBlockEntityDo((BlockGetter)level, pos, nav -> {
                ContainerSlot slot = nav.inventory.slot;
                ItemStack save = slot.getStack().copy();
                ItemStack oldSlotItem = save.copy();
                if (navigationTarget != null) {
                    navigationTarget.onInsert(heldItem, (NavTableBlockEntity)((Object)nav), player);
                }
                slot.setStack(heldItem.copyWithCount(1));
                if (!player.hasInfiniteMaterials()) {
                    heldItem.shrink(1);
                }
                player.getInventory().placeItemBackInInventory(save);
                ItemStack newSlotItem = slot.getStack();
                nav.setChanged();
                nav.sendData();
                float pitch = 0.8f + level.random.nextFloat() * 0.4f;
                float volume = 0.75f;
                if (oldSlotItem.isEmpty() && !newSlotItem.isEmpty()) {
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 0.75f, pitch);
                } else if (!oldSlotItem.isEmpty() && newSlotItem.isEmpty()) {
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.75f, pitch);
                } else if (!oldSlotItem.isEmpty()) {
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 0.75f, pitch);
                }
            });
            passed = true;
        }
        return passed;
    }

    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        NavTableBlockEntity be = (NavTableBlockEntity)this.getBlockEntity(level, pos);
        if (be == null || direction.getAxis() == ((Direction)state.getValue((Property)FACING)).getAxis()) {
            return 0;
        }
        return be.getRedstoneStrength(direction);
    }

    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (direction.getAxis() == ((Direction)state.getValue((Property)FACING)).getAxis()) {
            return 0;
        }
        if (((Direction)state.getValue((Property)FACING)).getAxis().isHorizontal() && direction == Direction.DOWN) {
            return this.getSignal(state, level, pos, direction);
        }
        return 0;
    }

    @Override
    public boolean commonCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public boolean commonConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        if (direction == null) {
            return false;
        }
        return direction.getAxis() != ((Direction)state.getValue((Property)FACING)).getAxis();
    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        this.withBlockEntityDo((BlockGetter)level, pos, NavTableBlockEntity::dropHeldItem);
        IBE.onRemove((BlockState)state, (Level)level, (BlockPos)pos, (BlockState)newState);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimBlockShapes.NAV_TABLE.get((Direction)state.getValue((Property)FACING));
    }

    public Class<NavTableBlockEntity> getBlockEntityClass() {
        return NavTableBlockEntity.class;
    }

    public BlockEntityType<? extends NavTableBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.NAVIGATION_TABLE.get();
    }

    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity blockEntity) {
        NavTableBlockEntity ntbe;
        ItemStack heldItem;
        ItemStack tableStack = SimBlocks.NAVIGATION_TABLE.asStack();
        if (blockEntity instanceof NavTableBlockEntity && !(heldItem = (ntbe = (NavTableBlockEntity)blockEntity).getHeldItem()).isEmpty()) {
            return new ItemRequirement(List.of(new ItemRequirement.StackRequirement(tableStack, ItemRequirement.ItemUseType.CONSUME), new ItemRequirement.StrictNbtStackRequirement(heldItem, ItemRequirement.ItemUseType.CONSUME)));
        }
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, tableStack);
    }
}

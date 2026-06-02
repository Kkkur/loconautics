/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.MapCodec
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.ShulkerBoxBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.storage.loot.LootParams$Builder
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.equipment.clipboard;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardScreen;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import java.util.List;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

public class ClipboardBlock
extends FaceAttachedHorizontalDirectionalBlock
implements IBE<ClipboardBlockEntity>,
IWrenchable,
ProperWaterloggedBlock {
    public static final BooleanProperty WRITTEN = BooleanProperty.create((String)"written");
    public static final MapCodec<ClipboardBlock> CODEC = ClipboardBlock.simpleCodec(ClipboardBlock::new);

    public ClipboardBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue((Property)WRITTEN, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{WRITTEN, FACE, FACING, WATERLOGGED}));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState stateForPlacement = super.getStateForPlacement(pContext);
        if (stateForPlacement == null) {
            return null;
        }
        if (stateForPlacement.getValue((Property)FACE) != AttachFace.WALL) {
            stateForPlacement = (BlockState)stateForPlacement.setValue((Property)FACING, (Comparable)((Direction)stateForPlacement.getValue((Property)FACING)).getOpposite());
        }
        return (BlockState)this.withWater(stateForPlacement, pContext).setValue((Property)WRITTEN, (Comparable)Boolean.valueOf(!pContext.getItemInHand().isComponentsPatchEmpty()));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return (switch ((AttachFace)pState.getValue((Property)FACE)) {
            case AttachFace.FLOOR -> AllShapes.CLIPBOARD_FLOOR;
            case AttachFace.CEILING -> AllShapes.CLIPBOARD_CEILING;
            default -> AllShapes.CLIPBOARD_WALL;
        }).get((Direction)pState.getValue((Property)FACING));
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return !pLevel.getBlockState(pPos.relative(ClipboardBlock.getConnectedDirection((BlockState)pState).getOpposite())).canBeReplaced();
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            this.breakAndCollect(state, level, pos, player);
            return InteractionResult.SUCCESS;
        }
        return this.onBlockEntityUse((BlockGetter)level, pos, cbe -> {
            if (level.isClientSide()) {
                CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.openScreen(player, cbe.components(), pos));
            }
            return InteractionResult.SUCCESS;
        });
    }

    @OnlyIn(value=Dist.CLIENT)
    private void openScreen(Player player, DataComponentMap components, BlockPos pos) {
        if (Minecraft.getInstance().player == player) {
            ScreenOpener.open((Screen)new ClipboardScreen(player.getInventory().selected, components, pos));
        }
    }

    public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        this.breakAndCollect(pState, pLevel, pPos, pPlayer);
    }

    private void breakAndCollect(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        if (pPlayer instanceof FakePlayer) {
            return;
        }
        if (pLevel.isClientSide) {
            return;
        }
        ItemStack cloneItemStack = this.getCloneItemStack((LevelReader)pLevel, pPos, pState);
        pLevel.destroyBlock(pPos, false);
        if (pLevel.getBlockState(pPos) != pState) {
            Inventory inv = pPlayer.getInventory();
            ItemStack selected = inv.getSelected();
            if (selected.isEmpty()) {
                inv.setItem(inv.selected, cloneItemStack);
            } else {
                inv.placeItemBackInInventory(cloneItemStack);
            }
        }
    }

    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return this.applyComponentsToDropStack(new ItemStack((ItemLike)this), level.getBlockEntity(pos));
    }

    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ClipboardBlockEntity)) {
            return state;
        }
        ClipboardBlockEntity cbe = (ClipboardBlockEntity)blockEntity;
        if (level.isClientSide || player.isCreative()) {
            return state;
        }
        Block.popResource((Level)level, (BlockPos)pos, (ItemStack)this.applyComponentsToDropStack(new ItemStack((ItemLike)this), cbe));
        return state;
    }

    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
        Object object = pBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(object instanceof ClipboardBlockEntity)) {
            return super.getDrops(pState, pBuilder);
        }
        ClipboardBlockEntity cbe = (ClipboardBlockEntity)object;
        ItemStack drop = this.applyComponentsToDropStack(new ItemStack((ItemLike)this), cbe);
        pBuilder.withDynamicDrop(ShulkerBoxBlock.CONTENTS, c -> c.accept(drop.copy()));
        return ImmutableList.of((Object)drop);
    }

    private ItemStack applyComponentsToDropStack(ItemStack stack, BlockEntity blockEntity) {
        if (blockEntity instanceof ClipboardBlockEntity) {
            ClipboardBlockEntity cbe = (ClipboardBlockEntity)blockEntity;
            stack.applyComponents(cbe.components());
            return stack;
        }
        return stack;
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        this.updateWater(pLevel, pState, pCurrentPos);
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public Class<ClipboardBlockEntity> getBlockEntityClass() {
        return ClipboardBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ClipboardBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.CLIPBOARD.get();
    }

    @NotNull
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}

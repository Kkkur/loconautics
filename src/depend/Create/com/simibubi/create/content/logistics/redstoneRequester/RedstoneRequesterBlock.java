/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.logistics.redstoneRequester;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.redstoneRequester.AutoRequestData;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;

public class RedstoneRequesterBlock
extends Block
implements IBE<RedstoneRequesterBlockEntity>,
IWrenchable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public RedstoneRequesterBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{POWERED, AXIS}));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState stateForPlacement = super.getStateForPlacement(pContext);
        if (stateForPlacement == null) {
            return null;
        }
        return (BlockState)((BlockState)stateForPlacement.setValue(AXIS, (Comparable)pContext.getHorizontalDirection().getAxis())).setValue((Property)POWERED, (Comparable)Boolean.valueOf(pContext.getLevel().hasNeighborSignal(pContext.getClickedPos())));
    }

    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        RedstoneRequesterBlockEntity req = (RedstoneRequesterBlockEntity)this.getBlockEntity((BlockGetter)pLevel, pPos);
        return req != null && req.lastRequestSucceeded ? 15 : 0;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return this.onBlockEntityUse((BlockGetter)level, pos, be -> be.use(player));
    }

    public static void programRequester(ServerPlayer player, StockTickerBlockEntity be, PackageOrderWithCrafts order, String address) {
        ItemStack stack = player.getMainHandItem();
        boolean isRequester = AllBlocks.REDSTONE_REQUESTER.isIn(stack);
        boolean isShopCloth = AllTags.AllItemTags.TABLE_CLOTHS.matches(stack);
        if (!isRequester && !isShopCloth) {
            return;
        }
        String targetDim = player.level().dimension().location().toString();
        AutoRequestData autoRequestData = new AutoRequestData(order, address, be.getBlockPos(), targetDim, false);
        autoRequestData.writeToItem(BlockPos.ZERO, stack);
        if (isRequester) {
            CompoundTag beTag = ((CustomData)stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.EMPTY)).copyTag();
            beTag.putUUID("Freq", be.behaviour.freqId);
            BlockEntity.addEntityType((CompoundTag)beTag, (BlockEntityType)((BlockEntityType)AllBlockEntityTypes.REDSTONE_REQUESTER.get()));
            stack.set(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.of((CompoundTag)beTag));
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
    }

    public static void appendRequesterTooltip(ItemStack pStack, List<Component> pTooltip) {
        if (!pStack.has(AllDataComponents.AUTO_REQUEST_DATA)) {
            return;
        }
        AutoRequestData data = (AutoRequestData)pStack.get(AllDataComponents.AUTO_REQUEST_DATA);
        for (BigItemStack entry : data.encodedRequest().stacks()) {
            pTooltip.add((Component)entry.stack.getHoverName().copy().append(" x").append(String.valueOf(entry.count)).withStyle(ChatFormatting.GRAY));
        }
        CreateLang.translate("logistically_linked.tooltip_clear", new Object[0]).style(ChatFormatting.DARK_GRAY).addTo(pTooltip);
    }

    public void setPlacedBy(Level pLevel, BlockPos requesterPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        Player player = pPlacer instanceof Player ? (Player)pPlacer : null;
        this.withBlockEntityDo((BlockGetter)pLevel, requesterPos, rrbe -> {
            AutoRequestData data = AutoRequestData.readFromItem(pLevel, player, requesterPos, pStack);
            if (data == null) {
                return;
            }
            rrbe.encodedRequest = data.encodedRequest();
            rrbe.encodedTargetAdress = data.encodedTargetAddress();
        });
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (pLevel.isClientSide()) {
            return;
        }
        pLevel.setBlockAndUpdate(pPos, (BlockState)pState.setValue((Property)POWERED, (Comparable)Boolean.valueOf(pLevel.hasNeighborSignal(pPos))));
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, RedstoneRequesterBlockEntity::onRedstonePowerChanged);
    }

    @Override
    public Class<RedstoneRequesterBlockEntity> getBlockEntityClass() {
        return RedstoneRequesterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RedstoneRequesterBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.REDSTONE_REQUESTER.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return (BlockState)pState.setValue(AXIS, (Comparable)pRotation.rotate(Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)((Direction.Axis)pState.getValue(AXIS)))).getAxis());
    }
}

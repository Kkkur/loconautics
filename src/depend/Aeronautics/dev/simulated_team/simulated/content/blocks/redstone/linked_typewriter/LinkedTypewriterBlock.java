/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler
 *  com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler$Mode
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.ShulkerBoxBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.storage.loot.LootParams$Builder
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler;
import com.simibubi.create.foundation.block.IBE;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterInteractionHandler;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.mixin_interface.PlayerTypewriterExtension;
import dev.simulated_team.simulated.service.SimMenuService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;

public class LinkedTypewriterBlock
extends HorizontalDirectionalBlock
implements IBE<LinkedTypewriterBlockEntity>,
IWrenchable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final MapCodec<LinkedTypewriterBlock> CODEC = LinkedTypewriterBlock.simpleCodec(LinkedTypewriterBlock::new);
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    public LinkedTypewriterBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED}).add(new Property[]{FACING});
        super.createBlockStateDefinition(builder);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction dir = pContext.getHorizontalDirection().getOpposite();
        assert (pContext.getPlayer() != null);
        return (BlockState)this.defaultBlockState().setValue((Property)HORIZONTAL_FACING, (Comparable)(pContext.getPlayer().isShiftKeyDown() ? dir.getOpposite() : dir));
    }

    public Class<LinkedTypewriterBlockEntity> getBlockEntityClass() {
        return LinkedTypewriterBlockEntity.class;
    }

    public BlockEntityType<? extends LinkedTypewriterBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.LINKED_TYPEWRITER.get();
    }

    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SimBlockShapes.LINKED_TYPEWRITER.get((Direction)state.getValue((Property)HORIZONTAL_FACING));
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemStack heldItem = player.getItemInHand(interactionHand);
        Item linkedControllerItem = AllItems.LINKED_CONTROLLER.asItem();
        if (player.getMainHandItem().is(linkedControllerItem) || player.getOffhandItem().is(linkedControllerItem)) {
            if (level.isClientSide) {
                ItemStack item = player.getMainHandItem().is(linkedControllerItem) ? player.getMainHandItem() : player.getOffhandItem();
                player.displayClientMessage((Component)SimLang.translate("linked_typewriter.linked_controller_copy", new Object[0]).component(), true);
                LinkedTypewriterInteractionHandler.sendLinkedControllerData(level, blockPos, item);
            }
            LinkedControllerClientHandler.MODE = LinkedControllerClientHandler.Mode.IDLE;
            return ItemInteractionResult.sidedSuccess((boolean)level.isClientSide);
        }
        if (heldItem.isEmpty() && interactionHand == InteractionHand.MAIN_HAND) {
            MutableBoolean success = new MutableBoolean(false);
            this.withBlockEntityDo((BlockGetter)level, blockPos, be -> {
                UUID uuid = player.getUUID();
                if (player.isShiftKeyDown() && be.checkAndStartUsing(uuid)) {
                    if (!level.isClientSide) {
                        this.displayScreen((LinkedTypewriterBlockEntity)((Object)be), player);
                    } else {
                        LinkedTypewriterInteractionHandler.setMode(LinkedTypewriterInteractionHandler.Mode.SCREEN_BINDING);
                    }
                    success.setTrue();
                    return;
                }
                if (be.checkAndStartUsing(uuid)) {
                    success.setTrue();
                    return;
                }
                if (be.checkUser(uuid)) {
                    ((PlayerTypewriterExtension)player).simulated$setCurrentTypewriter(null);
                    be.disconnectUser();
                    success.setTrue();
                }
            });
            if (success.getValue().booleanValue()) {
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (world instanceof ServerLevel && player != null && player.isCreative()) {
            Block.getDrops((BlockState)state, (ServerLevel)((ServerLevel)world), (BlockPos)pos, (BlockEntity)world.getBlockEntity(pos), (Entity)player, (ItemStack)context.getItemInHand()).forEach(itemStack -> player.getInventory().placeItemBackInInventory(itemStack));
        }
        return super.onSneakWrenched(state, context);
    }

    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack itemStack = super.getCloneItemStack(level, pos, state);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            BlockItem.setBlockEntityData((ItemStack)itemStack, (BlockEntityType)blockEntity.getType(), (CompoundTag)blockEntity.saveWithoutMetadata((HolderLookup.Provider)level.registryAccess()));
        }
        return itemStack;
    }

    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        LinkedTypewriterBlockEntity linkedTypewriterBlockEntity;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null && !level.isClientSide && player.isCreative() && blockEntity instanceof LinkedTypewriterBlockEntity && !(linkedTypewriterBlockEntity = (LinkedTypewriterBlockEntity)blockEntity).getTypewriterEntries().getKeyMap().isEmpty()) {
            ItemStack itemStack = this.getCloneItemStack((LevelReader)Objects.requireNonNull(level), pos, state);
            Containers.dropItemStack((Level)level, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (ItemStack)itemStack);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @NotNull
    public List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = (BlockEntity)params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof LinkedTypewriterBlockEntity) {
            LinkedTypewriterBlockEntity typewriter = (LinkedTypewriterBlockEntity)blockEntity;
            ItemStack itemStack = new ItemStack((ItemLike)this);
            typewriter.saveToItem(itemStack, (HolderLookup.Provider)params.getLevel().registryAccess());
            params.withDynamicDrop(ShulkerBoxBlock.CONTENTS, consumer -> itemStack.copy());
            return ImmutableList.of((Object)itemStack);
        }
        return super.getDrops(state, params);
    }

    protected void displayScreen(LinkedTypewriterBlockEntity be, Player player) {
        SimMenuService.INSTANCE.openScreen((ServerPlayer)player, be, arg_0 -> ((LinkedTypewriterBlockEntity)be).sendToMenu(arg_0));
    }
}

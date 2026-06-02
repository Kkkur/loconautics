/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.foundation.block.WrenchableDirectionalBlock
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import dev.simulated_team.simulated.content.blocks.redstone.AbstractLinkedReceiverBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver.ModulatingLinkedReceiverBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver.ModulatingLinkedReceiverScreen;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.multiloader.CommonRedstoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModulatingLinkedReceiverBlock
extends WrenchableDirectionalBlock
implements IBE<ModulatingLinkedReceiverBlockEntity>,
IWrenchable,
CommonRedstoneBlock {
    public static final MapCodec<ModulatingLinkedReceiverBlock> CODEC = ModulatingLinkedReceiverBlock.simpleCodec(ModulatingLinkedReceiverBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ModulatingLinkedReceiverBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED});
        super.createBlockStateDefinition(builder);
    }

    public Class<ModulatingLinkedReceiverBlockEntity> getBlockEntityClass() {
        return ModulatingLinkedReceiverBlockEntity.class;
    }

    public BlockEntityType<? extends ModulatingLinkedReceiverBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.MODULATING_LINKED_RECEIVER.get();
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        state = (BlockState)state.setValue((Property)FACING, (Comparable)context.getClickedFace());
        return state;
    }

    public boolean isSignalSource(BlockState state) {
        return (Boolean)state.getValue((Property)POWERED);
    }

    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (side != blockState.getValue((Property)FACING)) {
            return 0;
        }
        return this.getSignal(blockState, blockAccess, pos, side);
    }

    public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return this.getBlockEntityOptional(blockAccess, pos).map(AbstractLinkedReceiverBlockEntity::getReceivedSignal).orElse(0);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SimBlockShapes.MODULATING_DIRECTIONAL_LINK.get((Direction)pState.getValue((Property)FACING));
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        Direction blockFacing = (Direction)state.getValue((Property)FACING);
        if (fromPos.equals((Object)pos.relative(blockFacing.getOpposite())) && !this.canSurvive(state, (LevelReader)level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos neighbourPos = pos.relative(((Direction)state.getValue((Property)FACING)).getOpposite());
        BlockState neighbour = level.getBlockState(neighbourPos);
        return !neighbour.canBeReplaced();
    }

    @Override
    public boolean commonConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return side != null;
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!player.getMainHandItem().is(SimBlocks.LINKED_TYPEWRITER.asItem())) {
            if (level.isClientSide()) {
                this.withBlockEntityDo((BlockGetter)level, blockPos, ModulatingLinkedReceiverScreen::open);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}

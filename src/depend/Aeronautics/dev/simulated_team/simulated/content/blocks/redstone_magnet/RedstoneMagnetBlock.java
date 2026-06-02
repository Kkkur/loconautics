/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.foundation.block.WrenchableDirectionalBlock
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.redstone_magnet;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.RedstoneMagnetBlockEntity;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RedstoneMagnetBlock
extends WrenchableDirectionalBlock
implements IBE<RedstoneMagnetBlockEntity> {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RedstoneMagnetBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED});
        super.createBlockStateDefinition(builder);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction nearestLookingDirection = context.getNearestLookingDirection();
        if (context.getPlayer().isShiftKeyDown()) {
            nearestLookingDirection = nearestLookingDirection.getOpposite();
        }
        return (BlockState)((BlockState)super.getStateForPlacement(context).setValue((Property)POWERED, (Comparable)Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())))).setValue((Property)FACING, (Comparable)nearestLookingDirection.getOpposite());
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)level, pos, RedstoneMagnetBlockEntity::updateSignal);
        boolean previouslyPowered = (Boolean)state.getValue((Property)POWERED);
        if (previouslyPowered != level.hasNeighborSignal(pos)) {
            level.setBlock(pos, (BlockState)state.cycle((Property)POWERED), 2);
        }
        if (((Boolean)state.getValue((Property)POWERED)).booleanValue()) {
            SimAdvancements.OPPOSITES_ATTRACT.awardToNearby(pos, level);
        }
    }

    public Class<RedstoneMagnetBlockEntity> getBlockEntityClass() {
        return RedstoneMagnetBlockEntity.class;
    }

    public BlockEntityType<? extends RedstoneMagnetBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.REDSTONE_MAGNET.get();
    }
}

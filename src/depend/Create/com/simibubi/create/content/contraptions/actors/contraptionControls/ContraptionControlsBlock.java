/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.contraptions.actors.contraptionControls;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ContraptionControlsBlock
extends ControlsBlock
implements IBE<ContraptionControlsBlockEntity> {
    public static final MapCodec<ContraptionControlsBlock> CODEC = ContraptionControlsBlock.simpleCodec(ContraptionControlsBlock::new);

    public ContraptionControlsBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return this.onBlockEntityUse((BlockGetter)level, pos, cte -> {
            cte.pressButton();
            if (!level.isClientSide()) {
                cte.disabled = !cte.disabled;
                cte.notifyUpdate();
                ContraptionControlsBlockEntity.sendStatus(player, cte.filtering.getFilter(), !cte.disabled);
                AllSoundEvents.CONTROLLER_CLICK.play(cte.getLevel(), null, (Vec3i)cte.getBlockPos(), 1.0f, cte.disabled ? 0.8f : 1.5f);
            }
            return InteractionResult.SUCCESS;
        });
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, ContraptionControlsBlockEntity::updatePoweredState);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.CONTRAPTION_CONTROLS.get((Direction)pState.getValue((Property)FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.CONTRAPTION_CONTROLS_COLLISION.get((Direction)pState.getValue((Property)FACING));
    }

    @Override
    public Class<ContraptionControlsBlockEntity> getBlockEntityClass() {
        return ContraptionControlsBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ContraptionControlsBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.CONTRAPTION_CONTROLS.get();
    }

    @Override
    @NotNull
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}

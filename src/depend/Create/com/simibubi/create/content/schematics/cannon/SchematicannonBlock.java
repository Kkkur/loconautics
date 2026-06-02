/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.schematics.cannon;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class SchematicannonBlock
extends Block
implements IBE<SchematicannonBlockEntity> {
    public SchematicannonBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.SCHEMATICANNON_SHAPE;
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            this.withBlockEntityDo((BlockGetter)level, pos, be -> {
                be.defaultYaw = (float)(-Mth.floor((float)((entity.getYRot() + (entity.isShiftKeyDown() ? 180.0f : 0.0f)) * 16.0f / 360.0f + 0.5f)) & 0xF) * 360.0f / 16.0f;
            });
        }
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        this.withBlockEntityDo((BlockGetter)level, pos, be -> player.openMenu((MenuProvider)be, be::sendToMenu));
        return InteractionResult.SUCCESS;
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> {
            be.neighbourCheckCooldown = 0;
        });
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.hasBlockEntity() || state.getBlock() == newState.getBlock()) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> ItemHelper.dropContents(worldIn, pos, (IItemHandler)be.inventory));
        worldIn.removeBlockEntity(pos);
    }

    @Override
    public Class<SchematicannonBlockEntity> getBlockEntityClass() {
        return SchematicannonBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SchematicannonBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.SCHEMATICANNON.get();
    }
}

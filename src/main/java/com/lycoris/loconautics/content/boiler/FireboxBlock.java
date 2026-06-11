package com.lycoris.loconautics.content.boiler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Firebox block — the fuel-consuming base of the steam boiler multiblock.
 *
 * <p>Accepts any item that a vanilla furnace accepts as fuel
 * ({@code stack.getBurnTime(RecipeType.SMELTING) > 0}).
 * Flammable — will catch fire and spread like wood-tier blocks.
 *
 * <p>Right-clicking opens the firebox inventory so players can load fuel manually.
 * Fuel can also be inserted via hopper or by an adjacent coal car connection
 * (handled in Phase 3).
 */
public class FireboxBlock extends BaseEntityBlock {

    public FireboxBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // ------------------------------------------------------------------ flammability

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    // ------------------------------------------------------------------ interaction

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FireboxBlockEntity firebox) {
            firebox.openInventory(player);
        }
        return ItemInteractionResult.CONSUME;
    }

    // ------------------------------------------------------------------ block entity

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FireboxBlockEntity(BoilerBlocks.FIREBOX_BE.get(), pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return type == BoilerBlocks.FIREBOX_BE.get()
                ? (lvl, pos, st, be) -> ((FireboxBlockEntity) be).tick()
                : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.bearing.linearbearing;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class AndesiteLampBlock
extends Block
implements IWrenchable {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final EnumProperty<LampMode> MODE = EnumProperty.create((String)"mode", LampMode.class);

    public AndesiteLampBlock(BlockBehaviour.Properties properties) {
        super(properties.lightLevel(state -> {
            int power = (Integer)state.getValue((Property)POWER);
            return state.getValue(MODE) == LampMode.INVERTED ? 15 - power : power;
        }));
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)POWER, (Comparable)Integer.valueOf(0))).setValue(MODE, (Comparable)((Object)LampMode.NORMAL)));
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            BlockPos pos = context.getClickedPos();
            LampMode newMode = state.getValue(MODE) == LampMode.NORMAL ? LampMode.INVERTED : LampMode.NORMAL;
            level.setBlock(pos, (BlockState)state.setValue(MODE, (Comparable)((Object)newMode)), 3);
        }
        return InteractionResult.SUCCESS;
    }

    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (!level.isClientSide && player != null) {
            if (!player.isCreative()) {
                ItemStack dropStack = new ItemStack((ItemLike)this.asItem());
                if (!player.getInventory().add(dropStack)) {
                    player.drop(dropStack, false);
                }
            }
            level.destroyBlock(pos, false, (Entity)player);
        }
        return InteractionResult.SUCCESS;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)this.defaultBlockState().setValue((Property)POWER, (Comparable)Integer.valueOf(context.getLevel().getBestNeighborSignal(context.getClickedPos())));
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        int neighborPower;
        int currentPower;
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide && (currentPower = ((Integer)state.getValue((Property)POWER)).intValue()) != (neighborPower = level.getBestNeighborSignal(pos))) {
            level.setBlock(pos, (BlockState)state.setValue((Property)POWER, (Comparable)Integer.valueOf(neighborPower)), 2);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWER, MODE});
    }

    public static enum LampMode implements StringRepresentable
    {
        NORMAL("normal"),
        INVERTED("inverted");

        private final String name;

        private LampMode(String name) {
            this.name = name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }
}

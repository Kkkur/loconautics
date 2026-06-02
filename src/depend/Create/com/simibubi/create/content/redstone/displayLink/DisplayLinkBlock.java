/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.redstone.displayLink;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkScreen;
import com.simibubi.create.content.redstone.displayLink.source.RedstonePowerDisplaySource;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class DisplayLinkBlock
extends WrenchableDirectionalBlock
implements IBE<DisplayLinkBlockEntity> {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final MapCodec<DisplayLinkBlock> CODEC = DisplayLinkBlock.simpleCodec(DisplayLinkBlock::new);

    public DisplayLinkBlock(BlockBehaviour.Properties p_i48415_1_) {
        super(p_i48415_1_);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState placed = super.getStateForPlacement(context);
        placed = (BlockState)placed.setValue((Property)FACING, (Comparable)context.getClickedFace());
        return (BlockState)placed.setValue((Property)POWERED, (Comparable)Boolean.valueOf(this.shouldBePowered(placed, context.getLevel(), context.getClickedPos())));
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    public static void notifyGatherers(LevelAccessor level, BlockPos pos) {
        DisplayLinkBlock.forEachAttachedGatherer(level, pos, DisplayLinkBlockEntity::tickSource);
    }

    public static <T extends DisplaySource> void sendToGatherers(LevelAccessor level, BlockPos pos, BiConsumer<DisplayLinkBlockEntity, T> callback, Class<T> type) {
        DisplayLinkBlock.forEachAttachedGatherer(level, pos, dgte -> {
            if (type.isInstance(dgte.activeSource)) {
                callback.accept((DisplayLinkBlockEntity)dgte, (Object)dgte.activeSource);
            }
        });
    }

    private static void forEachAttachedGatherer(LevelAccessor level, BlockPos pos, Consumer<DisplayLinkBlockEntity> callback) {
        for (Direction d : Iterate.directions) {
            BlockEntity blockEntity;
            BlockPos offsetPos = pos.relative(d);
            BlockState blockState = level.getBlockState(offsetPos);
            if (!AllBlocks.DISPLAY_LINK.has(blockState) || !((blockEntity = level.getBlockEntity(offsetPos)) instanceof DisplayLinkBlockEntity)) continue;
            DisplayLinkBlockEntity dlbe = (DisplayLinkBlockEntity)blockEntity;
            if (dlbe.activeSource == null || dlbe.getDirection() != d.getOpposite()) continue;
            callback.accept(dlbe);
        }
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isClientSide) {
            return;
        }
        if (fromPos.equals((Object)pos.relative(((Direction)state.getValue((Property)FACING)).getOpposite()))) {
            DisplayLinkBlock.sendToGatherers((LevelAccessor)worldIn, fromPos, (dlte, p) -> dlte.tickSource(), RedstonePowerDisplaySource.class);
        }
        boolean powered = this.shouldBePowered(state, worldIn, pos);
        boolean previouslyPowered = (Boolean)state.getValue((Property)POWERED);
        if (previouslyPowered != powered) {
            worldIn.setBlock(pos, (BlockState)state.cycle((Property)POWERED), 2);
            if (!powered) {
                this.withBlockEntityDo((BlockGetter)worldIn, pos, DisplayLinkBlockEntity::onNoLongerPowered);
            }
        }
    }

    private boolean shouldBePowered(BlockState state, Level worldIn, BlockPos pos) {
        boolean powered = false;
        for (Direction d : Iterate.directions) {
            if (d.getOpposite() == state.getValue((Property)FACING) || worldIn.getSignal(pos.relative(d), d) == 0) continue;
            powered = true;
            break;
        }
        return powered;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{POWERED}));
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.withBlockEntityDo((BlockGetter)level, pos, be -> this.displayScreen((DisplayLinkBlockEntity)be, player)));
        return InteractionResult.SUCCESS;
    }

    @OnlyIn(value=Dist.CLIENT)
    protected void displayScreen(DisplayLinkBlockEntity be, Player player) {
        if (!(player instanceof LocalPlayer)) {
            return;
        }
        if (be.targetOffset.equals((Object)BlockPos.ZERO)) {
            player.displayClientMessage((Component)CreateLang.translateDirect("display_link.invalid", new Object[0]), true);
            return;
        }
        ScreenOpener.open((Screen)new DisplayLinkScreen(be));
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.DATA_GATHERER.get((Direction)pState.getValue((Property)FACING));
    }

    @Override
    public Class<DisplayLinkBlockEntity> getBlockEntityClass() {
        return DisplayLinkBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DisplayLinkBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.DISPLAY_LINK.get();
    }

    @Override
    @NotNull
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }
}

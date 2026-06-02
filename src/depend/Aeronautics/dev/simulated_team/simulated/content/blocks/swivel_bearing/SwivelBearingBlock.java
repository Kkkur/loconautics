/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem
 *  com.simibubi.create.foundation.block.IBE
 *  dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.simulated_team.simulated.content.blocks.swivel_bearing;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.simibubi.create.foundation.block.IBE;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlockEntity;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import dev.simulated_team.simulated.util.placement_helpers.CogwheelPlacementExtension;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SwivelBearingBlock
extends DirectionalKineticBlock
implements IBE<SwivelBearingBlockEntity>,
IRotate,
ExtraKinetics.ExtraKineticsBlock,
BlockSubLevelAssemblyListener {
    public static final BooleanProperty ASSEMBLED = BooleanProperty.create((String)"assembled");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new CogwheelPlacementExtension(i -> i.getItem() instanceof CogwheelBlockItem, arg_0 -> SimBlocks.SWIVEL_BEARING.has(arg_0)));

    public SwivelBearingBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)ASSEMBLED, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{ASSEMBLED}).add(new Property[]{POWERED}));
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!player.mayBuild()) {
            return ItemInteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.FAIL;
        }
        if (player.getItemInHand(interactionHand).isEmpty()) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            this.withBlockEntityDo((BlockGetter)level, blockPos, be -> {
                be.assembleNextTick = true;
            });
            return ItemInteractionResult.SUCCESS;
        }
        ItemStack heldItem = player.getItemInHand(interactionHand);
        IPlacementHelper helper = PlacementHelpers.get((int)placementHelperId);
        if (helper.matchesItem(heldItem)) {
            return helper.getOffset(player, level, blockState, blockPos, blockHitResult).placeInWorld(level, (BlockItem)heldItem.getItem(), player, interactionHand, blockHitResult);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public Direction.Axis getRotationAxis(BlockState blockState) {
        return ((Direction)blockState.getValue((Property)FACING)).getAxis();
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        return ((Boolean)state.getValue((Property)ASSEMBLED)).booleanValue() ? face == facing.getOpposite() : face.getAxis() == facing.getAxis();
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState rotated = this.getRotatedBlockState(state, context.getClickedFace());
        if (!rotated.canSurvive((LevelReader)level, context.getClickedPos())) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            this.withBlockEntityDo((BlockGetter)level, pos, SwivelBearingBlockEntity::disassemble);
        }
        rotated = this.getRotatedBlockState(level.getBlockState(pos), context.getClickedFace());
        KineticBlockEntity.switchToBlockState((Level)level, (BlockPos)pos, (BlockState)this.updateAfterWrenched(rotated, context));
        if (level.getBlockState(pos) != state) {
            IWrenchable.playRotateSound((Level)level, (BlockPos)pos);
        }
        return InteractionResult.SUCCESS;
    }

    public Class<SwivelBearingBlockEntity> getBlockEntityClass() {
        return SwivelBearingBlockEntity.class;
    }

    public BlockEntityType<? extends SwivelBearingBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.SWIVEL_BEARING.get();
    }

    @Override
    public IRotate getExtraKineticsRotationConfiguration() {
        return SwivelBearingBlockEntity.SwivelBearingCogwheelBlockEntity.EXTRA_COGWHEEL_CONFIG;
    }

    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return (Boolean)blockState.getValue((Property)ASSEMBLED) != false ? SimBlockShapes.SWIVEL_BEARING_ASSEMBLED.get((Direction)blockState.getValue((Property)FACING)) : Shapes.block();
    }

    public void beforeMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        this.withBlockEntityDo((BlockGetter)originLevel, oldPos, SwivelBearingBlockEntity::beforeAssembly);
    }

    public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        this.withBlockEntityDo((BlockGetter)resultingLevel, newPos, SwivelBearingBlockEntity::associatePlateWithParent);
    }
}

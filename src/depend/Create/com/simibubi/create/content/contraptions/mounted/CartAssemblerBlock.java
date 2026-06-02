/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.MinecartChest
 *  net.minecraft.world.entity.vehicle.MinecartFurnace
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
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
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.level.storage.loot.LootParams$Builder
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.EntityCollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.mounted;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.contraptions.mounted.CartAssembleRailType;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlockEntity;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.redstone.rail.ControllerRailBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
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
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CartAssemblerBlock
extends BaseRailBlock
implements IBE<CartAssemblerBlockEntity>,
IWrenchable,
SpecialBlockItemRequirement {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty BACKWARDS = BooleanProperty.create((String)"backwards");
    public static final Property<RailShape> RAIL_SHAPE = EnumProperty.create((String)"shape", RailShape.class, (Enum[])new RailShape[]{RailShape.EAST_WEST, RailShape.NORTH_SOUTH});
    public static final Property<CartAssembleRailType> RAIL_TYPE = EnumProperty.create((String)"rail_type", CartAssembleRailType.class);
    public static final MapCodec<CartAssemblerBlock> CODEC = CartAssemblerBlock.simpleCodec(CartAssemblerBlock::new);

    public CartAssemblerBlock(BlockBehaviour.Properties properties) {
        super(true, properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)BACKWARDS, (Comparable)Boolean.valueOf(false))).setValue(RAIL_TYPE, (Comparable)((Object)CartAssembleRailType.POWERED_RAIL))).setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public static BlockState createAnchor(BlockState state) {
        Direction.Axis axis = state.getValue(RAIL_SHAPE) == RailShape.NORTH_SOUTH ? Direction.Axis.Z : Direction.Axis.X;
        return (BlockState)AllBlocks.MINECART_ANCHOR.getDefaultState().setValue((Property)BlockStateProperties.HORIZONTAL_AXIS, (Comparable)axis);
    }

    private static Item getRailItem(BlockState state) {
        return ((CartAssembleRailType)((Object)state.getValue(RAIL_TYPE))).getItem();
    }

    public static BlockState getRailBlock(BlockState state) {
        BaseRailBlock railBlock = (BaseRailBlock)((CartAssembleRailType)((Object)state.getValue(RAIL_TYPE))).getBlock();
        BlockState railState = (BlockState)railBlock.defaultBlockState().setValue(railBlock.getShapeProperty(), (Comparable)((RailShape)state.getValue(RAIL_SHAPE)));
        if (railState.hasProperty((Property)ControllerRailBlock.BACKWARDS)) {
            railState = (BlockState)railState.setValue((Property)ControllerRailBlock.BACKWARDS, (Comparable)((Boolean)state.getValue((Property)BACKWARDS)));
        }
        return railState;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{RAIL_SHAPE, POWERED, RAIL_TYPE, BACKWARDS, WATERLOGGED});
        super.createBlockStateDefinition(builder);
    }

    public boolean canMakeSlopes(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return false;
    }

    public void onMinecartPass(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, AbstractMinecart cart) {
        if (!CartAssemblerBlock.canAssembleTo(cart)) {
            return;
        }
        if (world.isClientSide) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)world, pos, be -> be.assembleNextTick(cart));
    }

    public static CartAssemblerAction getActionForCart(BlockState state, AbstractMinecart cart) {
        CartAssembleRailType type = (CartAssembleRailType)((Object)state.getValue(RAIL_TYPE));
        boolean powered = (Boolean)state.getValue((Property)POWERED);
        switch (type) {
            case ACTIVATOR_RAIL: {
                return powered ? CartAssemblerAction.DISASSEMBLE : CartAssemblerAction.PASS;
            }
            case CONTROLLER_RAIL: {
                return powered ? CartAssemblerAction.ASSEMBLE_ACCELERATE_DIRECTIONAL : CartAssemblerAction.DISASSEMBLE_BRAKE;
            }
            case DETECTOR_RAIL: {
                return cart.getPassengers().isEmpty() ? CartAssemblerAction.ASSEMBLE_ACCELERATE : CartAssemblerAction.DISASSEMBLE;
            }
            case POWERED_RAIL: {
                return powered ? CartAssemblerAction.ASSEMBLE_ACCELERATE : CartAssemblerAction.DISASSEMBLE_BRAKE;
            }
            case REGULAR: {
                return powered ? CartAssemblerAction.ASSEMBLE : CartAssemblerAction.DISASSEMBLE;
            }
        }
        return CartAssemblerAction.PASS;
    }

    public static boolean canAssembleTo(AbstractMinecart cart) {
        return cart.canBeRidden() || cart instanceof MinecartFurnace || cart instanceof MinecartChest;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Item previousItem = CartAssemblerBlock.getRailItem(state);
        Item heldItem = stack.getItem();
        if (heldItem != previousItem) {
            CartAssembleRailType newType = null;
            for (CartAssembleRailType type : CartAssembleRailType.values()) {
                if (heldItem != type.getItem()) continue;
                newType = type;
            }
            if (newType == null) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0f, 1.0f);
            level.setBlockAndUpdate(pos, (BlockState)state.setValue(RAIL_TYPE, (Comparable)((Object)newType)));
            if (!player.isCreative()) {
                stack.shrink(1);
                player.getInventory().placeItemBackInInventory(new ItemStack((ItemLike)previousItem));
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void neighborChanged(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        if (worldIn.isClientSide) {
            return;
        }
        boolean previouslyPowered = (Boolean)state.getValue((Property)POWERED);
        if (previouslyPowered != worldIn.hasNeighborSignal(pos)) {
            worldIn.setBlock(pos, (BlockState)state.cycle((Property)POWERED), 2);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @NotNull
    public Property<RailShape> getShapeProperty() {
        return RAIL_SHAPE;
    }

    @NotNull
    public VoxelShape getShape(BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return AllShapes.CART_ASSEMBLER.get(this.getRailAxis(state));
    }

    protected Direction.Axis getRailAxis(BlockState state) {
        return state.getValue(RAIL_SHAPE) == RailShape.NORTH_SOUTH ? Direction.Axis.Z : Direction.Axis.X;
    }

    @NotNull
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext) {
            Entity entity = ((EntityCollisionContext)context).getEntity();
            if (entity instanceof AbstractMinecart) {
                return Shapes.empty();
            }
            if (entity instanceof Player) {
                return AllShapes.CART_ASSEMBLER_PLAYER_COLLISION.get(this.getRailAxis(state));
            }
        }
        return Shapes.block();
    }

    @NotNull
    public PushReaction getPistonPushReaction(@NotNull BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public Class<CartAssemblerBlockEntity> getBlockEntityClass() {
        return CartAssemblerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CartAssemblerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.CART_ASSEMBLER.get();
    }

    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader world, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        ArrayList<ItemStack> requiredItems = new ArrayList<ItemStack>();
        requiredItems.add(new ItemStack((ItemLike)CartAssemblerBlock.getRailItem(state)));
        requiredItems.add(new ItemStack((ItemLike)this.asItem()));
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, requiredItems);
    }

    @NotNull
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List drops = super.getDrops(state, builder);
        drops.addAll(CartAssemblerBlock.getRailBlock(state).getDrops(builder));
        return drops;
    }

    public List<ItemStack> getDropsNoRail(BlockState state, ServerLevel world, BlockPos pos, @Nullable BlockEntity p_220077_3_, @Nullable Entity p_220077_4_, ItemStack p_220077_5_) {
        return super.getDrops(state, new LootParams.Builder(world).withParameter(LootContextParams.ORIGIN, (Object)Vec3.atLowerCornerOf((Vec3i)pos)).withParameter(LootContextParams.TOOL, (Object)p_220077_5_).withOptionalParameter(LootContextParams.THIS_ENTITY, (Object)p_220077_4_).withOptionalParameter(LootContextParams.BLOCK_ENTITY, (Object)p_220077_3_));
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player != null && !player.isCreative()) {
            this.getDropsNoRail(state, (ServerLevel)world, pos, world.getBlockEntity(pos), (Entity)player, context.getItemInHand()).forEach(itemStack -> player.getInventory().placeItemBackInInventory(itemStack));
        }
        if (world instanceof ServerLevel) {
            state.spawnAfterBreak((ServerLevel)world, pos, ItemStack.EMPTY, true);
        }
        world.setBlockAndUpdate(pos, CartAssemblerBlock.getRailBlock(state));
        return InteractionResult.SUCCESS;
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = context.getClickedPos();
        world.setBlock(pos, this.rotate(state, Rotation.CLOCKWISE_90), 3);
        world.updateNeighborsAt(pos.below(), (Block)this);
        return InteractionResult.SUCCESS;
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        if (rotation == Rotation.NONE) {
            return state;
        }
        BlockState base = ((BlockState)((BlockState)AllBlocks.CONTROLLER_RAIL.getDefaultState().setValue(ControllerRailBlock.SHAPE, (Comparable)((RailShape)state.getValue(RAIL_SHAPE)))).setValue((Property)ControllerRailBlock.BACKWARDS, (Comparable)((Boolean)state.getValue((Property)BACKWARDS)))).rotate(rotation);
        return (BlockState)((BlockState)state.setValue(RAIL_SHAPE, (Comparable)((RailShape)base.getValue(ControllerRailBlock.SHAPE)))).setValue((Property)BACKWARDS, (Comparable)((Boolean)base.getValue((Property)ControllerRailBlock.BACKWARDS)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }
        BlockState base = ((BlockState)((BlockState)AllBlocks.CONTROLLER_RAIL.getDefaultState().setValue(ControllerRailBlock.SHAPE, (Comparable)((RailShape)state.getValue(RAIL_SHAPE)))).setValue((Property)ControllerRailBlock.BACKWARDS, (Comparable)((Boolean)state.getValue((Property)BACKWARDS)))).mirror(mirror);
        return (BlockState)state.setValue((Property)BACKWARDS, (Comparable)((Boolean)base.getValue((Property)ControllerRailBlock.BACKWARDS)));
    }

    public static Direction getHorizontalDirection(BlockState blockState) {
        if (!(blockState.getBlock() instanceof CartAssemblerBlock)) {
            return Direction.SOUTH;
        }
        Direction pointingTo = CartAssemblerBlock.getPointingTowards(blockState);
        return (Boolean)blockState.getValue((Property)BACKWARDS) != false ? pointingTo.getOpposite() : pointingTo;
    }

    private static Direction getPointingTowards(BlockState state) {
        switch ((RailShape)state.getValue(RAIL_SHAPE)) {
            case EAST_WEST: {
                return Direction.WEST;
            }
        }
        return Direction.NORTH;
    }

    @NotNull
    protected MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }

    public static enum CartAssemblerAction {
        ASSEMBLE,
        DISASSEMBLE,
        ASSEMBLE_ACCELERATE,
        DISASSEMBLE_BRAKE,
        ASSEMBLE_ACCELERATE_DIRECTIONAL,
        PASS;


        public boolean shouldAssemble() {
            return this == ASSEMBLE || this == ASSEMBLE_ACCELERATE || this == ASSEMBLE_ACCELERATE_DIRECTIONAL;
        }

        public boolean shouldDisassemble() {
            return this == DISASSEMBLE || this == DISASSEMBLE_BRAKE;
        }
    }

    public static class MinecartAnchorBlock
    extends Block {
        public MinecartAnchorBlock(BlockBehaviour.Properties p_i48440_1_) {
            super(p_i48440_1_);
        }

        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(new Property[]{BlockStateProperties.HORIZONTAL_AXIS});
            super.createBlockStateDefinition(builder);
        }

        @NotNull
        public VoxelShape getShape(@NotNull BlockState p_220053_1_, @NotNull BlockGetter p_220053_2_, @NotNull BlockPos p_220053_3_, @NotNull CollisionContext p_220053_4_) {
            return Shapes.empty();
        }
    }
}

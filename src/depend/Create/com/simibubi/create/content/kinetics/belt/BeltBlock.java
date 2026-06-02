/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.DebugLevelSource
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.level.pathfinder.PathType
 *  net.minecraft.world.level.storage.loot.LootParams$Builder
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.EntityCollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.common.Tags$Items
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package com.simibubi.create.content.kinetics.belt;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltShapes;
import com.simibubi.create.content.kinetics.belt.BeltSlicer;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler;
import com.simibubi.create.content.kinetics.belt.transport.BeltTunnelInteractionHandler;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import com.simibubi.create.foundation.block.render.ReducedDestroyEffects;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class BeltBlock
extends HorizontalKineticBlock
implements IBE<BeltBlockEntity>,
SpecialBlockItemRequirement,
TransformableBlock,
ProperWaterloggedBlock {
    public static final Property<BeltSlope> SLOPE = EnumProperty.create((String)"slope", BeltSlope.class);
    public static final Property<BeltPart> PART = EnumProperty.create((String)"part", BeltPart.class);
    public static final BooleanProperty CASING = BooleanProperty.create((String)"casing");

    public BeltBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(SLOPE, (Comparable)((Object)BeltSlope.HORIZONTAL))).setValue(PART, (Comparable)((Object)BeltPart.START))).setValue((Property)CASING, (Comparable)Boolean.valueOf(false))).setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        return super.areStatesKineticallyEquivalent(oldState, newState) && oldState.getValue(PART) == newState.getValue(PART);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        if (face.getAxis() != this.getRotationAxis(state)) {
            return false;
        }
        return this.getBlockEntityOptional((BlockGetter)world, pos).map(BeltBlockEntity::hasPulley).orElse(false);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        if (state.getValue(SLOPE) == BeltSlope.SIDEWAYS) {
            return Direction.Axis.Y;
        }
        return ((Direction)state.getValue(HORIZONTAL_FACING)).getClockWise().getAxis();
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return AllItems.BELT_CONNECTOR.asStack();
    }

    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List drops = super.getDrops(state, builder);
        BlockEntity blockEntity = (BlockEntity)builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof BeltBlockEntity && ((BeltBlockEntity)blockEntity).hasPulley()) {
            drops.addAll(AllBlocks.SHAFT.getDefaultState().getDrops(builder));
        }
        return drops;
    }

    public void spawnAfterBreak(BlockState state, ServerLevel worldIn, BlockPos pos, ItemStack p_220062_4_, boolean b) {
        BeltBlockEntity controllerBE = BeltHelper.getControllerBE((LevelAccessor)worldIn, pos);
        if (controllerBE != null) {
            controllerBE.getInventory().ejectAll();
        }
    }

    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return false;
    }

    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        BlockPos entityPosition = entityIn.blockPosition();
        BlockPos beltPos = null;
        if (AllBlocks.BELT.has(worldIn.getBlockState(entityPosition))) {
            beltPos = entityPosition;
        } else if (AllBlocks.BELT.has(worldIn.getBlockState(entityPosition.below()))) {
            beltPos = entityPosition.below();
        }
        if (beltPos == null) {
            return;
        }
        if (!(worldIn instanceof Level)) {
            return;
        }
        this.entityInside(worldIn.getBlockState(beltPos), (Level)worldIn, beltPos, entityIn);
    }

    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (!BeltBlock.canTransportObjects(state)) {
            return;
        }
        if (entityIn instanceof Player) {
            Player player = (Player)entityIn;
            if (player.isShiftKeyDown() && !AllItems.CARDBOARD_BOOTS.isIn(player.getItemBySlot(EquipmentSlot.FEET))) {
                return;
            }
            if (player.getAbilities().flying) {
                return;
            }
        }
        if (DivingBootsItem.isWornBy(entityIn)) {
            return;
        }
        BeltBlockEntity belt = BeltHelper.getSegmentBE((LevelAccessor)worldIn, pos);
        if (belt == null) {
            return;
        }
        ItemStack asItem = ItemHelper.fromItemEntity(entityIn);
        if (!asItem.isEmpty()) {
            if (worldIn.isClientSide) {
                return;
            }
            if (entityIn.getDeltaMovement().y > 0.0) {
                return;
            }
            Vec3 targetLocation = VecHelper.getCenterOf((Vec3i)pos).add(0.0, 0.3125, 0.0);
            if (!PackageEntity.centerPackage(entityIn, targetLocation)) {
                return;
            }
            if (BeltTunnelInteractionHandler.getTunnelOnPosition(worldIn, pos) != null) {
                return;
            }
            this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> {
                IItemHandler handler = (IItemHandler)worldIn.getCapability(Capabilities.ItemHandler.BLOCK, pos, state, (BlockEntity)be, null);
                if (handler == null) {
                    return;
                }
                ItemStack remainder = handler.insertItem(0, asItem, false);
                if (remainder.isEmpty()) {
                    entityIn.discard();
                } else if (entityIn instanceof ItemEntity) {
                    ItemEntity itemEntity = (ItemEntity)entityIn;
                    if (remainder.getCount() != itemEntity.getItem().getCount()) {
                        itemEntity.setItem(remainder);
                    }
                }
            });
            return;
        }
        BeltBlockEntity controller = BeltHelper.getControllerBE((LevelAccessor)worldIn, pos);
        if (controller == null || controller.passengers == null) {
            return;
        }
        if (controller.passengers.containsKey(entityIn)) {
            BeltMovementHandler.TransportedEntityInfo info = controller.passengers.get(entityIn);
            if (info.getTicksSinceLastCollision() != 0 || pos.equals((Object)entityIn.blockPosition())) {
                info.refresh(pos, state);
            }
        } else {
            controller.passengers.put(entityIn, new BeltMovementHandler.TransportedEntityInfo(pos, state));
            entityIn.setOnGround(true);
        }
    }

    public static boolean canTransportObjects(BlockState state) {
        if (!AllBlocks.BELT.has(state)) {
            return false;
        }
        BeltSlope slope = (BeltSlope)((Object)state.getValue(SLOPE));
        return slope != BeltSlope.VERTICAL && slope != BeltSlope.SIDEWAYS;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        SoundType soundType;
        boolean isHand;
        if (player.isShiftKeyDown() || !player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        boolean isWrench = AllItems.WRENCH.isIn(stack);
        boolean isConnector = AllItems.BELT_CONNECTOR.isIn(stack);
        boolean isShaft = AllBlocks.SHAFT.isIn(stack);
        boolean isDye = stack.is(Tags.Items.DYES);
        boolean hasWater = ((FluidStack)GenericItemEmptying.emptyItem(level, stack, true).getFirst()).getFluid().isSame((Fluid)Fluids.WATER);
        boolean bl = isHand = stack.isEmpty() && hand == InteractionHand.MAIN_HAND;
        if (isDye || hasWater) {
            return this.onBlockEntityUseItemOn((BlockGetter)level, pos, be -> be.applyColor(DyeColor.getColor((ItemStack)stack)) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
        }
        if (isConnector) {
            return BeltSlicer.useConnector(state, level, pos, player, hand, hitResult, new BeltSlicer.Feedback());
        }
        if (isWrench) {
            return BeltSlicer.useWrench(state, level, pos, player, hand, hitResult, new BeltSlicer.Feedback());
        }
        BeltBlockEntity belt = BeltHelper.getSegmentBE((LevelAccessor)level, pos);
        if (belt == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (PackageItem.isPackage(stack)) {
            ItemStack toInsert = stack.copy();
            IItemHandler handler = (IItemHandler)level.getCapability(Capabilities.ItemHandler.BLOCK, belt.getBlockPos(), null);
            if (handler == null) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            ItemStack remainder = handler.insertItem(0, toInsert, false);
            if (remainder.isEmpty()) {
                stack.shrink(1);
                return ItemInteractionResult.SUCCESS;
            }
        }
        if (isHand) {
            BeltBlockEntity controllerBelt = belt.getControllerBE();
            if (controllerBelt == null) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            MutableBoolean success = new MutableBoolean(false);
            controllerBelt.getInventory().applyToEachWithin((float)belt.index + 0.5f, 0.55f, transportedItemStack -> {
                player.getInventory().placeItemBackInInventory(transportedItemStack.stack);
                success.setTrue();
                return TransportedItemStackHandlerBehaviour.TransportedResult.removeItem();
            });
            if (success.isTrue()) {
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, 1.0f + level.random.nextFloat());
            }
        }
        if (isShaft) {
            if (state.getValue(PART) != BeltPart.MIDDLE) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            KineticBlockEntity.switchToBlockState(level, pos, (BlockState)state.setValue(PART, (Comparable)((Object)BeltPart.PULLEY)));
            return ItemInteractionResult.SUCCESS;
        }
        if (AllBlocks.BRASS_CASING.isIn(stack)) {
            this.withBlockEntityDo((BlockGetter)level, pos, be -> be.setCasingType(BeltBlockEntity.CasingType.BRASS));
            this.updateCoverProperty((LevelAccessor)level, pos, level.getBlockState(pos));
            soundType = AllBlocks.BRASS_CASING.getDefaultState().getSoundType((LevelReader)level, pos, (Entity)player);
            level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
            return ItemInteractionResult.SUCCESS;
        }
        if (AllBlocks.ANDESITE_CASING.isIn(stack)) {
            this.withBlockEntityDo((BlockGetter)level, pos, be -> be.setCasingType(BeltBlockEntity.CasingType.ANDESITE));
            this.updateCoverProperty((LevelAccessor)level, pos, level.getBlockState(pos));
            soundType = AllBlocks.ANDESITE_CASING.getDefaultState().getSoundType((LevelReader)level, pos, (Entity)player);
            level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        if (((Boolean)state.getValue((Property)CASING)).booleanValue()) {
            if (world.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            this.withBlockEntityDo((BlockGetter)world, pos, be -> be.setCasingType(BeltBlockEntity.CasingType.NONE));
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(PART) == BeltPart.PULLEY) {
            if (world.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            KineticBlockEntity.switchToBlockState(world, pos, (BlockState)state.setValue(PART, (Comparable)((Object)BeltPart.MIDDLE)));
            if (player != null && !player.isCreative()) {
                player.getInventory().placeItemBackInInventory(AllBlocks.SHAFT.asStack());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{SLOPE, PART, CASING, WATERLOGGED});
        super.createBlockStateDefinition(builder);
    }

    public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
        return PathType.RAIL;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return BeltShapes.getShape(state);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getBlock() != this) {
            return Shapes.empty();
        }
        VoxelShape shape = this.getShape(state, worldIn, pos, context);
        if (!(context instanceof EntityCollisionContext)) {
            return shape;
        }
        return this.getBlockEntityOptional(worldIn, pos).map(be -> {
            Entity entity = ((EntityCollisionContext)context).getEntity();
            if (entity == null) {
                return shape;
            }
            BeltBlockEntity controller = be.getControllerBE();
            if (controller == null) {
                return shape;
            }
            if (controller.passengers == null || !controller.passengers.containsKey(entity)) {
                return BeltShapes.getCollisionShape(state);
            }
            return shape;
        }).orElse(shape);
    }

    public RenderShape getRenderShape(BlockState state) {
        return (Boolean)state.getValue((Property)CASING) != false ? RenderShape.MODEL : RenderShape.ENTITYBLOCK_ANIMATED;
    }

    /*
     * Enabled aggressive block sorting
     */
    public static void initBelt(Level world, BlockPos pos) {
        if (world.isClientSide) {
            return;
        }
        if (world instanceof ServerLevel && ((ServerLevel)world).getChunkSource().getGenerator() instanceof DebugLevelSource) {
            return;
        }
        BlockState state = world.getBlockState(pos);
        if (!AllBlocks.BELT.has(state)) {
            return;
        }
        int limit = 1000;
        BlockPos currentPos = pos;
        while (limit-- > 0) {
            BlockState currentState = world.getBlockState(currentPos);
            if (!AllBlocks.BELT.has(currentState)) {
                world.destroyBlock(pos, true);
                return;
            }
            BlockPos nextSegmentPosition = BeltBlock.nextSegmentPosition(currentState, currentPos, false);
            if (nextSegmentPosition == null) break;
            if (!world.isLoaded(nextSegmentPosition)) {
                return;
            }
            currentPos = nextSegmentPosition;
        }
        int index = 0;
        List<BlockPos> beltChain = BeltBlock.getBeltChain((LevelAccessor)world, currentPos);
        if (beltChain.size() < 2) {
            world.destroyBlock(currentPos, true);
            return;
        }
        Iterator<BlockPos> iterator = beltChain.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                return;
            }
            BlockPos beltPos = iterator.next();
            BlockEntity blockEntity = world.getBlockEntity(beltPos);
            BlockState currentState = world.getBlockState(beltPos);
            if (!(blockEntity instanceof BeltBlockEntity)) break;
            BeltBlockEntity be = (BeltBlockEntity)blockEntity;
            if (!AllBlocks.BELT.has(currentState)) break;
            be.setController(currentPos);
            be.beltLength = beltChain.size();
            be.index = index;
            be.attachKinetics();
            be.setChanged();
            be.sendData();
            if (be.isController() && !BeltBlock.canTransportObjects(currentState)) {
                be.getInventory().ejectAll();
            }
            ++index;
        }
        world.destroyBlock(currentPos, true);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, world, pos, newState, isMoving);
        if (world.isClientSide) {
            return;
        }
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        if (isMoving) {
            return;
        }
        for (boolean forward : Iterate.trueAndFalse) {
            BlockState currentState;
            BlockPos currentPos = BeltBlock.nextSegmentPosition(state, pos, forward);
            if (currentPos == null || !AllBlocks.BELT.has(currentState = world.getBlockState(currentPos))) continue;
            boolean hasPulley = false;
            BlockEntity blockEntity = world.getBlockEntity(currentPos);
            if (blockEntity instanceof BeltBlockEntity) {
                BeltBlockEntity belt = (BeltBlockEntity)blockEntity;
                if (belt.isController()) {
                    belt.getInventory().ejectAll();
                }
                hasPulley = belt.hasPulley();
            }
            world.removeBlockEntity(currentPos);
            BlockState shaftState = (BlockState)AllBlocks.SHAFT.getDefaultState().setValue((Property)BlockStateProperties.AXIS, (Comparable)this.getRotationAxis(currentState));
            world.setBlock(currentPos, ProperWaterloggedBlock.withWater((LevelAccessor)world, hasPulley ? shaftState : Blocks.AIR.defaultBlockState(), currentPos), 3);
            world.levelEvent(2001, currentPos, Block.getId((BlockState)currentState));
        }
    }

    public BlockState updateShape(BlockState state, Direction side, BlockState p_196271_3_, LevelAccessor world, BlockPos pos, BlockPos p_196271_6_) {
        this.updateWater(world, state, pos);
        if (side.getAxis().isHorizontal()) {
            this.updateTunnelConnections(world, pos.above());
        }
        if (side == Direction.UP) {
            this.updateCoverProperty(world, pos, state);
        }
        return state;
    }

    public void updateCoverProperty(LevelAccessor world, BlockPos pos, BlockState state) {
        if (world.isClientSide()) {
            return;
        }
        if (((Boolean)state.getValue((Property)CASING)).booleanValue() && state.getValue(SLOPE) == BeltSlope.HORIZONTAL) {
            this.withBlockEntityDo((BlockGetter)world, pos, bbe -> bbe.setCovered(BeltBlock.isBlockCoveringBelt(world, pos.above())));
        }
    }

    public static boolean isBlockCoveringBelt(LevelAccessor world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        VoxelShape collisionShape = blockState.getCollisionShape((BlockGetter)world, pos);
        if (collisionShape.isEmpty()) {
            return false;
        }
        AABB bounds = collisionShape.bounds();
        if (bounds.getXsize() < 0.5 || bounds.getZsize() < 0.5) {
            return false;
        }
        if (bounds.minY > 0.0) {
            return false;
        }
        if (AllBlocks.CRUSHING_WHEEL_CONTROLLER.has(blockState)) {
            return false;
        }
        if (FunnelBlock.isFunnel(blockState) && FunnelBlock.getFunnelFacing(blockState) != Direction.UP) {
            return false;
        }
        return !(blockState.getBlock() instanceof BeltTunnelBlock);
    }

    private void updateTunnelConnections(LevelAccessor world, BlockPos pos) {
        Block tunnelBlock = world.getBlockState(pos).getBlock();
        if (tunnelBlock instanceof BeltTunnelBlock) {
            ((BeltTunnelBlock)tunnelBlock).updateTunnel(world, pos);
        }
    }

    public static List<BlockPos> getBeltChain(LevelAccessor world, BlockPos controllerPos) {
        BlockState state;
        LinkedList<BlockPos> positions = new LinkedList<BlockPos>();
        BlockState blockState = world.getBlockState(controllerPos);
        if (!AllBlocks.BELT.has(blockState)) {
            return positions;
        }
        int limit = 1000;
        BlockPos current = controllerPos;
        while (limit-- > 0 && current != null && AllBlocks.BELT.has(state = world.getBlockState(current))) {
            positions.add(current);
            current = BeltBlock.nextSegmentPosition(state, current, true);
        }
        return positions;
    }

    public static BlockPos nextSegmentPosition(BlockState state, BlockPos pos, boolean forward) {
        int offset;
        Direction direction = (Direction)state.getValue(HORIZONTAL_FACING);
        BeltSlope slope = (BeltSlope)((Object)state.getValue(SLOPE));
        BeltPart part = (BeltPart)((Object)state.getValue(PART));
        int n = offset = forward ? 1 : -1;
        if (part == BeltPart.END && forward || part == BeltPart.START && !forward) {
            return null;
        }
        if (slope == BeltSlope.VERTICAL) {
            return pos.above(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? offset : -offset);
        }
        pos = pos.relative(direction, offset);
        if (slope != BeltSlope.HORIZONTAL && slope != BeltSlope.SIDEWAYS) {
            return pos.above(slope == BeltSlope.UPWARD ? offset : -offset);
        }
        return pos;
    }

    @Override
    public Class<BeltBlockEntity> getBlockEntityClass() {
        return BeltBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BeltBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.BELT.get();
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        ArrayList<ItemStack> required = new ArrayList<ItemStack>();
        if (state.getValue(PART) != BeltPart.MIDDLE) {
            required.add(AllBlocks.SHAFT.asStack());
        }
        if (state.getValue(PART) == BeltPart.START) {
            required.add(AllItems.BELT_CONNECTOR.asStack());
        }
        if (required.isEmpty()) {
            return ItemRequirement.NONE;
        }
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, required);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        BlockState rotate = super.rotate(state, rot);
        if (state.getValue(SLOPE) != BeltSlope.VERTICAL) {
            return rotate;
        }
        if (((Direction)state.getValue(HORIZONTAL_FACING)).getAxisDirection() != ((Direction)rotate.getValue(HORIZONTAL_FACING)).getAxisDirection()) {
            if (state.getValue(PART) == BeltPart.START) {
                return (BlockState)rotate.setValue(PART, (Comparable)((Object)BeltPart.END));
            }
            if (state.getValue(PART) == BeltPart.END) {
                return (BlockState)rotate.setValue(PART, (Comparable)((Object)BeltPart.START));
            }
        }
        return rotate;
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.mirror != null) {
            state = this.mirror(state, transform.mirror);
        }
        if (transform.rotationAxis == Direction.Axis.Y) {
            return this.rotate(state, transform.rotation);
        }
        return this.transformInner(state, transform);
    }

    protected BlockState transformInner(BlockState state, StructureTransform transform) {
        boolean diagonal;
        boolean halfTurn = transform.rotation == Rotation.CLOCKWISE_180;
        Direction initialDirection = (Direction)state.getValue(HORIZONTAL_FACING);
        boolean bl = diagonal = state.getValue(SLOPE) == BeltSlope.DOWNWARD || state.getValue(SLOPE) == BeltSlope.UPWARD;
        if (!diagonal) {
            for (int i = 0; i < transform.rotation.ordinal(); ++i) {
                Direction direction = (Direction)state.getValue(HORIZONTAL_FACING);
                BeltSlope slope = (BeltSlope)((Object)state.getValue(SLOPE));
                boolean vertical = slope == BeltSlope.VERTICAL;
                boolean horizontal = slope == BeltSlope.HORIZONTAL;
                boolean sideways = slope == BeltSlope.SIDEWAYS;
                Direction newDirection = direction.getOpposite();
                BeltSlope newSlope = BeltSlope.VERTICAL;
                if (vertical) {
                    if (direction.getAxis() == transform.rotationAxis) {
                        newDirection = direction.getCounterClockWise();
                        newSlope = BeltSlope.SIDEWAYS;
                    } else {
                        newSlope = BeltSlope.HORIZONTAL;
                        newDirection = direction;
                        if (direction.getAxis() == Direction.Axis.Z) {
                            newDirection = direction.getOpposite();
                        }
                    }
                }
                if (sideways) {
                    newDirection = direction;
                    if (direction.getAxis() == transform.rotationAxis) {
                        newSlope = BeltSlope.HORIZONTAL;
                    } else {
                        newDirection = direction.getCounterClockWise();
                    }
                }
                if (horizontal) {
                    newDirection = direction;
                    if (direction.getAxis() == transform.rotationAxis) {
                        newSlope = BeltSlope.SIDEWAYS;
                    } else if (direction.getAxis() != Direction.Axis.Z) {
                        newDirection = direction.getOpposite();
                    }
                }
                state = (BlockState)state.setValue(HORIZONTAL_FACING, (Comparable)newDirection);
                state = (BlockState)state.setValue(SLOPE, (Comparable)((Object)newSlope));
            }
        } else if (initialDirection.getAxis() != transform.rotationAxis) {
            for (int i = 0; i < transform.rotation.ordinal(); ++i) {
                Direction direction = (Direction)state.getValue(HORIZONTAL_FACING);
                Direction newDirection = direction.getOpposite();
                BeltSlope slope = (BeltSlope)((Object)state.getValue(SLOPE));
                boolean upward = slope == BeltSlope.UPWARD;
                boolean downward = slope == BeltSlope.DOWNWARD;
                state = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ^ downward ^ direction.getAxis() == Direction.Axis.Z ? (BlockState)state.setValue(SLOPE, (Comparable)((Object)(upward ? BeltSlope.DOWNWARD : BeltSlope.UPWARD))) : (BlockState)state.setValue(HORIZONTAL_FACING, (Comparable)newDirection);
            }
        } else if (halfTurn) {
            boolean vertical;
            Direction direction = (Direction)state.getValue(HORIZONTAL_FACING);
            Direction newDirection = direction.getOpposite();
            BeltSlope slope = (BeltSlope)((Object)state.getValue(SLOPE));
            boolean bl2 = vertical = slope == BeltSlope.VERTICAL;
            if (diagonal) {
                state = (BlockState)state.setValue(SLOPE, (Comparable)((Object)(slope == BeltSlope.UPWARD ? BeltSlope.DOWNWARD : (slope == BeltSlope.DOWNWARD ? BeltSlope.UPWARD : slope))));
            } else if (vertical) {
                state = (BlockState)state.setValue(HORIZONTAL_FACING, (Comparable)newDirection);
            }
        }
        return state;
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    public static class RenderProperties
    extends ReducedDestroyEffects
    implements MultiPosDestructionHandler {
        @Override
        public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BeltBlockEntity) {
                BeltBlockEntity belt = (BeltBlockEntity)blockEntity;
                return new HashSet<BlockPos>(BeltBlock.getBeltChain((LevelAccessor)level, belt.getController()));
            }
            return null;
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.storage.loot.LootParams$Builder
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.tableCloth;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.redstoneRequester.AutoRequestData;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.IHaveBigOutline;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TableClothBlock
extends Block
implements IHaveBigOutline,
IWrenchable,
IBE<TableClothBlockEntity> {
    public static final BooleanProperty HAS_BE = BooleanProperty.create((String)"entity");
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());
    private DyeColor colour;

    public TableClothBlock(BlockBehaviour.Properties pProperties, DyeColor colour) {
        super(pProperties);
        this.colour = colour;
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)HAS_BE, (Comparable)Boolean.valueOf(false)));
    }

    public TableClothBlock(BlockBehaviour.Properties pProperties, String type) {
        super(pProperties);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{HAS_BE}));
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (!(pPlacer instanceof Player)) {
            return;
        }
        Player player = (Player)pPlacer;
        AutoRequestData requestData = AutoRequestData.readFromItem(pLevel, player, pPos, pStack);
        if (requestData == null) {
            return;
        }
        pLevel.setBlockAndUpdate(pPos, (BlockState)pState.setValue((Property)HAS_BE, (Comparable)Boolean.valueOf(true)));
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, dcbe -> {
            dcbe.requestData = requestData;
            dcbe.owner = player.getUUID();
            dcbe.facing = player.getDirection().getOpposite();
            AllAdvancements.TABLE_CLOTH_SHOP.awardTo(player);
        });
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hitResult.getDirection() == Direction.DOWN) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        ItemStack heldItem = player.getItemInHand(hand);
        boolean shiftKeyDown = player.isShiftKeyDown();
        if (!player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        IPlacementHelper placementHelper = PlacementHelpers.get((int)placementHelperId);
        if (placementHelper.matchesItem(heldItem)) {
            if (shiftKeyDown) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            placementHelper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)heldItem.getItem(), player, hand, hitResult);
            return ItemInteractionResult.SUCCESS;
        }
        if ((shiftKeyDown || heldItem.isEmpty()) && !((Boolean)state.getValue((Property)HAS_BE)).booleanValue()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide() && !((Boolean)state.getValue((Property)HAS_BE)).booleanValue()) {
            level.setBlockAndUpdate(pos, (BlockState)state.cycle((Property)HAS_BE));
        }
        return this.onBlockEntityUseItemOn((BlockGetter)level, pos, dcbe -> dcbe.use(player, hitResult));
    }

    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        List drops = super.getDrops(pState, pParams);
        Iterator iterator = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(iterator instanceof TableClothBlockEntity)) {
            return drops;
        }
        TableClothBlockEntity dcbe = (TableClothBlockEntity)((Object)iterator);
        if (!dcbe.isShop()) {
            return drops;
        }
        for (ItemStack stack : drops) {
            if (!AllTags.AllItemTags.TABLE_CLOTHS.matches(stack)) continue;
            ItemStack drop = new ItemStack((ItemLike)this);
            dcbe.requestData.writeToItem(dcbe.getBlockPos(), drop);
            return List.of(drop);
        }
        return drops;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.TABLE_CLOTH;
    }

    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return AllShapes.TABLE_CLOTH;
    }

    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return AllShapes.TABLE_CLOTH_OCCLUSION;
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.TABLE_CLOTH_OCCLUSION;
    }

    public boolean canSurvive(BlockState p_152922_, LevelReader p_152923_, BlockPos p_152924_) {
        return true;
    }

    @Nullable
    public DyeColor getColor() {
        return this.colour;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return (Boolean)state.getValue((Property)HAS_BE) != false ? IBE.super.newBlockEntity(pos, state) : null;
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pNewState.getOptionalValue((Property)HAS_BE).orElse(false).booleanValue()) {
            pNewState = Blocks.AIR.defaultBlockState();
        }
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    @Override
    public Class<TableClothBlockEntity> getBlockEntityClass() {
        return TableClothBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TableClothBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.TABLE_CLOTH.get();
    }

    private static class PlacementHelper
    implements IPlacementHelper {
        private PlacementHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return i -> AllTags.AllItemTags.TABLE_CLOTHS.matches(i.getItem());
        }

        public Predicate<BlockState> getStatePredicate() {
            return s -> s.getBlock() instanceof TableClothBlock;
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)Direction.Axis.Y, dir -> world.getBlockState(pos.relative(dir)).canBeReplaced());
            if (directions.isEmpty()) {
                return PlacementOffset.fail();
            }
            return PlacementOffset.success((Vec3i)pos.relative((Direction)directions.get(0)), s -> s);
        }
    }
}

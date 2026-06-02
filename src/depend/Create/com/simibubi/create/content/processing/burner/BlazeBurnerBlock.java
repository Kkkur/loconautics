/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.advancements.critereon.StatePropertiesPredicate$Builder
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.FlintAndSteelItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.level.storage.loot.LootPool
 *  net.minecraft.world.level.storage.loot.LootPool$Builder
 *  net.minecraft.world.level.storage.loot.LootTable
 *  net.minecraft.world.level.storage.loot.LootTable$Builder
 *  net.minecraft.world.level.storage.loot.entries.LootItem
 *  net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer$Builder
 *  net.minecraft.world.level.storage.loot.predicates.ExplosionCondition
 *  net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition$Builder
 *  net.minecraft.world.level.storage.loot.providers.number.ConstantValue
 *  net.minecraft.world.level.storage.loot.providers.number.NumberProvider
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.processing.burner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerInteractionHandler;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import javax.annotation.ParametersAreNonnullByDefault;
import net.createmod.catnip.lang.Lang;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlazeBurnerBlock
extends HorizontalDirectionalBlock
implements IBE<BlazeBurnerBlockEntity>,
IWrenchable,
SpecialBlockItemRequirement {
    public static final EnumProperty<HeatLevel> HEAT_LEVEL = EnumProperty.create((String)"blaze", HeatLevel.class);
    public static final MapCodec<BlazeBurnerBlock> CODEC = BlazeBurnerBlock.simpleCodec(BlazeBurnerBlock::new);

    public BlazeBurnerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(HEAT_LEVEL, (Comparable)((Object)HeatLevel.NONE)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{HEAT_LEVEL, FACING});
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        if (world.isClientSide) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos.above());
        if (!(blockEntity instanceof BasinBlockEntity)) {
            return;
        }
        BasinBlockEntity basin = (BasinBlockEntity)blockEntity;
        basin.notifyChangeOfContents();
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return BlazeBurnerBlock.getLitOrUnlitStack(state);
    }

    @Override
    public Class<BlazeBurnerBlockEntity> getBlockEntityClass() {
        return BlazeBurnerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlazeBurnerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.HEATER.get();
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HEAT_LEVEL) == HeatLevel.NONE) {
            return null;
        }
        return IBE.super.newBlockEntity(pos, state);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        HeatLevel heat = (HeatLevel)((Object)state.getValue(HEAT_LEVEL));
        if (AllItems.GOGGLES.isIn(stack) && heat != HeatLevel.NONE) {
            return this.onBlockEntityUseItemOn((BlockGetter)level, pos, bbte -> {
                if (bbte.goggles) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                bbte.goggles = true;
                bbte.notifyUpdate();
                return ItemInteractionResult.SUCCESS;
            });
        }
        BlazeBurnerBlockEntity be = (BlazeBurnerBlockEntity)this.getBlockEntity((BlockGetter)level, pos);
        if (be != null && be.stockKeeper) {
            StockTickerBlockEntity stockTicker = BlazeBurnerBlockEntity.getStockTicker((LevelAccessor)level, pos);
            if (stockTicker != null) {
                StockTickerInteractionHandler.interactWithLogisticsManagerAt(player, level, stockTicker.getBlockPos());
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (stack.isEmpty() && heat != HeatLevel.NONE) {
            return this.onBlockEntityUseItemOn((BlockGetter)level, pos, bbte -> {
                if (!bbte.goggles) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                bbte.goggles = false;
                bbte.notifyUpdate();
                return ItemInteractionResult.SUCCESS;
            });
        }
        if (heat == HeatLevel.NONE) {
            if (stack.getItem() instanceof FlintAndSteelItem) {
                level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, level.random.nextFloat() * 0.4f + 0.8f);
                if (level.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }
                stack.hurtAndBreak(1, (LivingEntity)player, LivingEntity.getSlotForHand((InteractionHand)hand));
                level.setBlockAndUpdate(pos, AllBlocks.LIT_BLAZE_BURNER.getDefaultState());
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        boolean doNotConsume = player.isCreative();
        boolean forceOverflow = !(player instanceof FakePlayer);
        InteractionResultHolder<ItemStack> res = BlazeBurnerBlock.tryInsert(state, level, pos, stack, doNotConsume, forceOverflow, false);
        ItemStack leftover = (ItemStack)res.getObject();
        if (!(level.isClientSide || doNotConsume || leftover.isEmpty())) {
            if (stack.isEmpty()) {
                player.setItemInHand(hand, leftover);
            } else if (!player.getInventory().add(leftover)) {
                player.drop(leftover, false);
            }
        }
        return res.getResult() == InteractionResult.SUCCESS ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level world, BlockPos pos, ItemStack stack, boolean doNotConsume, boolean forceOverflow, boolean simulate) {
        if (!state.hasBlockEntity()) {
            return InteractionResultHolder.fail((Object)ItemStack.EMPTY);
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof BlazeBurnerBlockEntity)) {
            return InteractionResultHolder.fail((Object)ItemStack.EMPTY);
        }
        BlazeBurnerBlockEntity burnerBE = (BlazeBurnerBlockEntity)be;
        if (burnerBE.isCreativeFuel(stack)) {
            if (!simulate) {
                burnerBE.applyCreativeFuel();
            }
            return InteractionResultHolder.success((Object)ItemStack.EMPTY);
        }
        if (!burnerBE.tryUpdateFuel(stack, forceOverflow, simulate)) {
            return InteractionResultHolder.fail((Object)ItemStack.EMPTY);
        }
        if (!doNotConsume) {
            ItemStack container;
            ItemStack itemStack = container = stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
            if (!world.isClientSide) {
                stack.shrink(1);
            }
            return InteractionResultHolder.success((Object)container);
        }
        return InteractionResultHolder.success((Object)ItemStack.EMPTY);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        Item item = stack.getItem();
        BlockState defaultState = this.defaultBlockState();
        if (!(item instanceof BlazeBurnerBlockItem)) {
            return defaultState;
        }
        HeatLevel initialHeat = ((BlazeBurnerBlockItem)item).hasCapturedBlaze() ? HeatLevel.SMOULDERING : HeatLevel.NONE;
        return (BlockState)((BlockState)defaultState.setValue(HEAT_LEVEL, (Comparable)((Object)initialHeat))).setValue((Property)FACING, (Comparable)context.getHorizontalDirection().getOpposite());
    }

    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return AllShapes.HEATER_BLOCK_SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
        if (p_220071_4_ == CollisionContext.empty()) {
            return AllShapes.HEATER_BLOCK_SPECIAL_COLLISION_SHAPE;
        }
        return this.getShape(p_220071_1_, p_220071_2_, p_220071_3_, p_220071_4_);
    }

    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState state, Level p_180641_2_, BlockPos p_180641_3_) {
        return Math.max(0, ((HeatLevel)((Object)state.getValue(HEAT_LEVEL))).ordinal() - 1);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(10) != 0) {
            return;
        }
        if (!((HeatLevel)((Object)state.getValue(HEAT_LEVEL))).isAtLeast(HeatLevel.SMOULDERING)) {
            return;
        }
        world.playLocalSound((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() + 0.5f), (double)((float)pos.getZ() + 0.5f), SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5f + random.nextFloat(), random.nextFloat() * 0.7f + 0.6f, false);
    }

    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public static HeatLevel getHeatLevelOf(BlockState blockState) {
        return blockState.hasProperty(HEAT_LEVEL) ? (HeatLevel)((Object)blockState.getValue(HEAT_LEVEL)) : HeatLevel.NONE;
    }

    public static int getLight(BlockState state) {
        HeatLevel level = (HeatLevel)((Object)state.getValue(HEAT_LEVEL));
        return switch (level.ordinal()) {
            case 0 -> 0;
            case 1 -> 8;
            default -> 15;
        };
    }

    public static LootTable.Builder buildLootTable() {
        LootItemCondition.Builder survivesExplosion = ExplosionCondition.survivesExplosion();
        BlazeBurnerBlock block = (BlazeBurnerBlock)AllBlocks.BLAZE_BURNER.get();
        LootTable.Builder builder = LootTable.lootTable();
        LootPool.Builder poolBuilder = LootPool.lootPool();
        for (HeatLevel level : HeatLevel.values()) {
            ItemLike drop = level == HeatLevel.NONE ? (ItemLike)AllItems.EMPTY_BLAZE_BURNER.get() : (ItemLike)AllBlocks.BLAZE_BURNER.get();
            poolBuilder.add(((LootPoolSingletonContainer.Builder)LootItem.lootTableItem((ItemLike)drop).when(survivesExplosion)).when((LootItemCondition.Builder)LootItemBlockStatePropertyCondition.hasBlockStateProperties((Block)block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HEAT_LEVEL, (Comparable)((Object)level)))));
        }
        builder.withPool(poolBuilder.setRolls((NumberProvider)ConstantValue.exactly((float)1.0f)));
        return builder;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity blockEntity) {
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, BlazeBurnerBlock.getLitOrUnlitStack(state));
    }

    private static ItemStack getLitOrUnlitStack(BlockState state) {
        boolean isLit = state.getValue(HEAT_LEVEL) != HeatLevel.NONE;
        return (isLit ? AllBlocks.BLAZE_BURNER : AllItems.EMPTY_BLAZE_BURNER).asStack();
    }

    public static enum HeatLevel implements StringRepresentable
    {
        NONE,
        SMOULDERING,
        FADING,
        KINDLED,
        SEETHING;

        public static final Codec<HeatLevel> CODEC;

        public static HeatLevel byIndex(int index) {
            return HeatLevel.values()[index];
        }

        public HeatLevel nextActiveLevel() {
            return HeatLevel.byIndex(this.ordinal() % (HeatLevel.values().length - 1) + 1);
        }

        public boolean isAtLeast(HeatLevel heatLevel) {
            return this.ordinal() >= heatLevel.ordinal();
        }

        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }

        static {
            CODEC = StringRepresentable.fromEnum(HeatLevel::values);
        }
    }
}

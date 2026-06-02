/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTProcessors
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Registry
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.stats.Stats
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.BedBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CampfireBlock
 *  net.minecraft.world.level.block.IceBlock
 *  net.minecraft.world.level.block.SlimeBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.SlabType
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.common.SpecialPlantable
 *  net.neoforged.neoforge.event.level.BlockDropsEvent
 *  net.neoforged.neoforge.event.level.BlockEvent$BreakEvent
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.utility;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.schematic.nbt.PartialSafeNBT;
import com.simibubi.create.api.schematic.nbt.SafeNbtWriterRegistry;
import com.simibubi.create.api.schematic.state.SchematicStateFilter;
import com.simibubi.create.api.schematic.state.SchematicStateFilterRegistry;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.framedblocks.FramedBlocksInSchematics;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.IMergeableBE;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.createmod.catnip.nbt.NBTProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.SpecialPlantable;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

public class BlockHelper {
    private static final List<IntegerProperty> COUNT_STATES = List.of(BlockStateProperties.EGGS, BlockStateProperties.PICKLES, BlockStateProperties.CANDLES);
    private static final List<Block> VINELIKE_BLOCKS = List.of(Blocks.VINE, Blocks.GLOW_LICHEN);
    private static final List<BooleanProperty> VINELIKE_STATES = List.of(BlockStateProperties.UP, BlockStateProperties.NORTH, BlockStateProperties.EAST, BlockStateProperties.SOUTH, BlockStateProperties.WEST, BlockStateProperties.DOWN);

    public static BlockState setZeroAge(BlockState blockState) {
        if (blockState.hasProperty((Property)BlockStateProperties.AGE_1)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.AGE_1, (Comparable)Integer.valueOf(0));
        }
        if (blockState.hasProperty((Property)BlockStateProperties.AGE_2)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.AGE_2, (Comparable)Integer.valueOf(0));
        }
        if (blockState.hasProperty((Property)BlockStateProperties.AGE_3)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.AGE_3, (Comparable)Integer.valueOf(0));
        }
        if (blockState.hasProperty((Property)BlockStateProperties.AGE_5)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.AGE_5, (Comparable)Integer.valueOf(0));
        }
        if (blockState.hasProperty((Property)BlockStateProperties.AGE_7)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.AGE_7, (Comparable)Integer.valueOf(0));
        }
        if (blockState.hasProperty((Property)BlockStateProperties.AGE_15)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.AGE_15, (Comparable)Integer.valueOf(0));
        }
        if (blockState.hasProperty((Property)BlockStateProperties.AGE_25)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.AGE_25, (Comparable)Integer.valueOf(0));
        }
        if (blockState.hasProperty((Property)BlockStateProperties.LEVEL_HONEY)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.LEVEL_HONEY, (Comparable)Integer.valueOf(0));
        }
        if (blockState.hasProperty((Property)BlockStateProperties.HATCH)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.HATCH, (Comparable)Integer.valueOf(0));
        }
        if (blockState.hasProperty((Property)BlockStateProperties.STAGE)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.STAGE, (Comparable)Integer.valueOf(0));
        }
        if (blockState.is(BlockTags.CAULDRONS)) {
            return Blocks.CAULDRON.defaultBlockState();
        }
        if (blockState.hasProperty((Property)BlockStateProperties.LEVEL_COMPOSTER)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.LEVEL_COMPOSTER, (Comparable)Integer.valueOf(0));
        }
        if (blockState.hasProperty((Property)BlockStateProperties.EXTENDED)) {
            return (BlockState)blockState.setValue((Property)BlockStateProperties.EXTENDED, (Comparable)Boolean.valueOf(false));
        }
        return blockState;
    }

    public static int findAndRemoveInInventory(BlockState block, Player player, int amount) {
        int taken;
        boolean needsTwo;
        int amountFound = 0;
        Item required = BlockHelper.getRequiredItem(block).getItem();
        boolean bl = needsTwo = block.hasProperty((Property)BlockStateProperties.SLAB_TYPE) && block.getValue((Property)BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE;
        if (needsTwo) {
            amount *= 2;
        }
        for (IntegerProperty integerProperty : COUNT_STATES) {
            if (!block.hasProperty((Property)integerProperty)) continue;
            amount *= ((Integer)block.getValue((Property)integerProperty)).intValue();
        }
        if (VINELIKE_BLOCKS.contains(block.getBlock())) {
            int vineCount = 0;
            for (BooleanProperty vineState : VINELIKE_STATES) {
                if (!block.hasProperty((Property)vineState) || !((Boolean)block.getValue((Property)vineState)).booleanValue()) continue;
                ++vineCount;
            }
            amount += vineCount - 1;
        }
        int preferredSlot = player.getInventory().selected;
        ItemStack itemStack = player.getInventory().getItem(preferredSlot);
        int count = itemStack.getCount();
        if (itemStack.getItem() == required && count > 0) {
            taken = Math.min(count, amount - amountFound);
            player.getInventory().setItem(preferredSlot, new ItemStack((ItemLike)itemStack.getItem(), count - taken));
            amountFound += taken;
        }
        for (int i = 0; i < player.getInventory().getContainerSize() && amountFound != amount; ++i) {
            ItemStack itemStack2 = player.getInventory().getItem(i);
            count = itemStack2.getCount();
            if (itemStack2.getItem() != required || count <= 0) continue;
            taken = Math.min(count, amount - amountFound);
            player.getInventory().setItem(i, new ItemStack((ItemLike)itemStack2.getItem(), count - taken));
            amountFound += taken;
        }
        if (needsTwo) {
            if (amountFound % 2 != 0) {
                player.getInventory().add(new ItemStack((ItemLike)required));
            }
            amountFound /= 2;
        }
        return amountFound;
    }

    public static ItemStack getRequiredItem(BlockState state) {
        ItemStack itemStack = new ItemStack((ItemLike)state.getBlock());
        Item item = itemStack.getItem();
        if (item == Items.FARMLAND || item == Items.DIRT_PATH) {
            itemStack = new ItemStack((ItemLike)Items.DIRT);
        }
        return itemStack;
    }

    public static void destroyBlock(Level world, BlockPos pos, float effectChance) {
        BlockHelper.destroyBlock(world, pos, effectChance, stack -> Block.popResource((Level)world, (BlockPos)pos, (ItemStack)stack));
    }

    public static void destroyBlock(Level world, BlockPos pos, float effectChance, Consumer<ItemStack> droppedItemCallback) {
        BlockHelper.destroyBlockAs(world, pos, null, ItemStack.EMPTY, effectChance, droppedItemCallback);
    }

    public static void destroyBlockAs(Level level, BlockPos pos, @Nullable Player player, ItemStack usedTool, float effectChance, Consumer<ItemStack> droppedItemCallback) {
        BlockEntity blockEntity;
        FluidState fluidState = level.getFluidState(pos);
        BlockState state = level.getBlockState(pos);
        if (level.random.nextFloat() < effectChance) {
            level.levelEvent(2001, pos, Block.getId((BlockState)state));
        }
        BlockEntity blockEntity2 = blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        if (player != null) {
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, player);
            NeoForge.EVENT_BUS.post((Event)event);
            if (event.isCanceled()) {
                return;
            }
            usedTool.mineBlock(level, state, pos, player);
            player.awardStat(Stats.BLOCK_MINED.get((Object)state.getBlock()));
        }
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            if (!(!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) || level.restoringBlockSnapshots || player != null && player.isCreative())) {
                BlockState below;
                List drops = Block.getDrops((BlockState)state, (ServerLevel)serverLevel, (BlockPos)pos, (BlockEntity)blockEntity, (Entity)player, (ItemStack)usedTool);
                BlockDropsEvent event = new BlockDropsEvent(serverLevel, pos, state, blockEntity, new ArrayList(), (Entity)player, usedTool);
                NeoForge.EVENT_BUS.post((Event)event);
                if (!event.isCanceled() && event.getDroppedExperience() > 0) {
                    state.getBlock().popExperience(serverLevel, pos, event.getDroppedExperience());
                }
                for (ItemStack itemStack : drops) {
                    droppedItemCallback.accept(itemStack);
                }
                Registry enchantmentRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
                if (state.getBlock() instanceof IceBlock && usedTool.getEnchantmentLevel((Holder)enchantmentRegistry.getHolderOrThrow(Enchantments.SILK_TOUCH)) == 0 && !level.dimensionType().ultraWarm() && ((below = level.getBlockState(pos.below())).blocksMotion() || below.liquid())) {
                    fluidState = IceBlock.meltsInto().getFluidState();
                }
                state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, false);
            }
        }
        level.setBlockAndUpdate(pos, fluidState.createLegacyBlock());
    }

    public static boolean isSolidWall(BlockGetter reader, BlockPos fromPos, Direction toDirection) {
        return BlockHelper.hasBlockSolidSide(reader.getBlockState(fromPos.relative(toDirection)), reader, fromPos.relative(toDirection), toDirection.getOpposite());
    }

    public static boolean noCollisionInSpace(BlockGetter reader, BlockPos pos) {
        return reader.getBlockState(pos).getCollisionShape(reader, pos).isEmpty();
    }

    private static void placeRailWithoutUpdate(Level world, BlockState state, BlockPos target) {
        int idx;
        LevelChunk chunk = world.getChunkAt(target);
        LevelChunkSection chunksection = chunk.getSection(idx = chunk.getSectionIndex(target.getY()));
        if (chunksection == null) {
            chunk.getSections()[idx] = chunksection = new LevelChunkSection(world.registryAccess().registryOrThrow(Registries.BIOME));
        }
        BlockState old = chunksection.setBlockState(SectionPos.sectionRelative((int)target.getX()), SectionPos.sectionRelative((int)target.getY()), SectionPos.sectionRelative((int)target.getZ()), state);
        chunk.setUnsaved(true);
        world.markAndNotifyBlock(target, chunk, old, state, 82, 512);
        world.setBlock(target, state, 82);
        world.neighborChanged(target, world.getBlockState(target.below()).getBlock(), target.below());
    }

    public static CompoundTag prepareBlockEntityData(Level level, BlockState blockState, BlockEntity blockEntity) {
        CompoundTag data = null;
        if (blockEntity == null) {
            return null;
        }
        RegistryAccess access = level.registryAccess();
        SafeNbtWriterRegistry.SafeNbtWriter writer = SafeNbtWriterRegistry.REGISTRY.get(blockEntity.getType());
        if (AllTags.AllBlockTags.SAFE_NBT.matches(blockState)) {
            data = blockEntity.saveWithFullMetadata((HolderLookup.Provider)access);
        } else if (writer != null) {
            data = new CompoundTag();
            writer.writeSafe(blockEntity, data, (HolderLookup.Provider)access);
        } else if (blockEntity instanceof PartialSafeNBT) {
            PartialSafeNBT safeNbtBE = (PartialSafeNBT)blockEntity;
            data = new CompoundTag();
            safeNbtBE.writeSafe(data, (HolderLookup.Provider)access);
        } else if (Mods.FRAMEDBLOCKS.contains((ItemLike)blockState.getBlock())) {
            data = FramedBlocksInSchematics.prepareBlockEntityData(blockState, blockEntity);
        }
        return NBTProcessors.process((BlockState)blockState, (BlockEntity)blockEntity, (CompoundTag)data, (boolean)true);
    }

    public static void placeSchematicBlock(Level world, BlockState state, BlockPos target, ItemStack stack, @Nullable CompoundTag data) {
        Block block = state.getBlock();
        BlockEntity existingBlockEntity = world.getBlockEntity(target);
        boolean alreadyPlaced = false;
        SchematicStateFilterRegistry.StateFilter filter = SchematicStateFilterRegistry.REGISTRY.get((StateHolder<Block, ?>)state);
        if (filter != null) {
            state = filter.filterStates(existingBlockEntity, state);
        } else if (block instanceof SchematicStateFilter) {
            SchematicStateFilter schematicStateFilter = (SchematicStateFilter)block;
            state = schematicStateFilter.filterStates(existingBlockEntity, state);
        }
        if (state.hasProperty((Property)BlockStateProperties.EXTENDED)) {
            state = (BlockState)state.setValue((Property)BlockStateProperties.EXTENDED, (Comparable)Boolean.FALSE);
        }
        if (state.hasProperty((Property)BlockStateProperties.WATERLOGGED)) {
            state = (BlockState)state.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.FALSE);
        }
        if (block == Blocks.COMPOSTER) {
            state = Blocks.COMPOSTER.defaultBlockState();
        } else if (block != Blocks.SEA_PICKLE && block instanceof SpecialPlantable) {
            SpecialPlantable specialPlantable = (SpecialPlantable)block;
            alreadyPlaced = true;
            if (specialPlantable.canPlacePlantAtPosition(stack, (LevelReader)world, target, null)) {
                specialPlantable.spawnPlantAtPosition(stack, (LevelAccessor)world, target, null);
            }
        } else if (state.is(BlockTags.CAULDRONS)) {
            state = Blocks.CAULDRON.defaultBlockState();
        }
        if (world.dimensionType().ultraWarm() && state.getFluidState().is(FluidTags.WATER)) {
            int i = target.getX();
            int j = target.getY();
            int k = target.getZ();
            world.playSound(null, target, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
            for (int l = 0; l < 8; ++l) {
                world.addParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
            }
            Block.dropResources((BlockState)state, (Level)world, (BlockPos)target);
            return;
        }
        if (!alreadyPlaced) {
            if (state.getBlock() instanceof BaseRailBlock) {
                BlockHelper.placeRailWithoutUpdate(world, state, target);
            } else if (AllBlocks.BELT.has(state)) {
                world.setBlock(target, state, 2);
            } else {
                world.setBlock(target, state, 18);
            }
        }
        if (data != null) {
            BlockEntity blockEntity;
            if (existingBlockEntity instanceof IMergeableBE) {
                IMergeableBE mergeable = (IMergeableBE)existingBlockEntity;
                BlockEntity loaded = BlockEntity.loadStatic((BlockPos)target, (BlockState)state, (CompoundTag)data, (HolderLookup.Provider)world.registryAccess());
                if (loaded != null && existingBlockEntity.getType().equals(loaded.getType())) {
                    mergeable.accept(loaded);
                    return;
                }
            }
            if ((blockEntity = world.getBlockEntity(target)) != null) {
                IMultiBlockEntityContainer imbe;
                data.putInt("x", target.getX());
                data.putInt("y", target.getY());
                data.putInt("z", target.getZ());
                if (blockEntity instanceof KineticBlockEntity) {
                    KineticBlockEntity kbe = (KineticBlockEntity)blockEntity;
                    kbe.warnOfMovement();
                }
                if (blockEntity instanceof IMultiBlockEntityContainer && !(imbe = (IMultiBlockEntityContainer)blockEntity).isController()) {
                    data.put("Controller", NbtUtils.writeBlockPos((BlockPos)imbe.getController()));
                }
                blockEntity.loadWithComponents(data, (HolderLookup.Provider)world.registryAccess());
            }
        }
        try {
            state.getBlock().setPlacedBy(world, target, state, null, stack);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static double getBounceMultiplier(Block block) {
        if (block instanceof SlimeBlock) {
            return 0.8;
        }
        if (block instanceof BedBlock) {
            return 0.528;
        }
        return 0.0;
    }

    public static boolean hasBlockSolidSide(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir) {
        return !state.is(BlockTags.LEAVES) && Block.isFaceFull((VoxelShape)state.getCollisionShape(blockGetter, pos), (Direction)dir);
    }

    public static boolean extinguishFire(Level world, @Nullable Player player, BlockPos pos, Direction dir) {
        if (world.getBlockState(pos = pos.relative(dir)).getBlock() == Blocks.FIRE) {
            world.levelEvent(player, 1009, pos, 0);
            world.removeBlock(pos, false);
            return true;
        }
        return false;
    }

    public static BlockState copyProperties(BlockState fromState, BlockState toState) {
        for (Property property : fromState.getProperties()) {
            toState = BlockHelper.copyProperty(property, fromState, toState);
        }
        return toState;
    }

    public static <T extends Comparable<T>> BlockState copyProperty(Property<T> property, BlockState fromState, BlockState toState) {
        if (fromState.hasProperty(property) && toState.hasProperty(property)) {
            return (BlockState)toState.setValue(property, fromState.getValue(property));
        }
        return toState;
    }

    public static boolean isNotUnheated(BlockState state) {
        if (state.is(BlockTags.CAMPFIRES) && state.hasProperty((Property)CampfireBlock.LIT)) {
            return (Boolean)state.getValue((Property)CampfireBlock.LIT);
        }
        if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
            return state.getValue(BlazeBurnerBlock.HEAT_LEVEL) != BlazeBurnerBlock.HeatLevel.NONE;
        }
        return true;
    }

    public static InteractionResult invokeUse(BlockState state, Level level, Player player, InteractionHand hand, BlockHitResult ray) {
        InteractionResult interactionresult;
        ItemInteractionResult iteminteractionresult = state.useItemOn(player.getItemInHand(hand), level, player, hand, ray);
        if (iteminteractionresult.consumesAction()) {
            return iteminteractionresult.result();
        }
        if (iteminteractionresult == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION && hand == InteractionHand.MAIN_HAND && (interactionresult = state.useWithoutItem(level, player, ray)).consumesAction()) {
            return interactionresult;
        }
        return InteractionResult.PASS;
    }
}

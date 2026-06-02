/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement
 *  com.simibubi.create.content.decoration.encasing.CasingBlock
 *  com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement$ItemUseType
 *  com.simibubi.create.foundation.utility.BlockHelper
 *  dev.simulated_team.simulated.service.SimItemService
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.envelope;

import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.utility.BlockHelper;
import dev.eriksonn.aeronautics.content.blocks.hot_air.envelope.Envelope;
import dev.eriksonn.aeronautics.content.blocks.hot_air.envelope.EnvelopeEncasedShaftBlock;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.simulated_team.simulated.service.SimItemService;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EnvelopeBlock
extends CasingBlock
implements Envelope,
SpecialBlockItemRequirement {
    private static final BlockPos[] DIRECTION_OFFSETS = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 1, 0), new BlockPos(0, -1, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 1, 0), new BlockPos(-1, -1, 0), new BlockPos(1, -1, 0), new BlockPos(-1, 1, 0), new BlockPos(1, 0, 1), new BlockPos(-1, 0, -1), new BlockPos(1, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(0, 1, 1), new BlockPos(0, -1, -1), new BlockPos(0, -1, 1), new BlockPos(0, 1, -1)};
    protected final DyeColor color;

    public EnvelopeBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    protected static void applyDye(BlockState state, Level level, BlockPos pos, DyeColor color) {
        BlockState newEnvelopeState = BlockHelper.copyProperties((BlockState)state, (BlockState)AeroBlocks.DYED_ENVELOPE_BLOCKS.get(color).getDefaultState());
        BlockState newEncasedEnvelopeState = BlockHelper.copyProperties((BlockState)state, (BlockState)AeroBlocks.ENVELOPE_ENCASED_SHAFTS.get(color).getDefaultState());
        if (EnvelopeBlock.selfDye(level, pos, state, color)) {
            return;
        }
        boolean hasDyed = false;
        for (Direction d : Iterate.directions) {
            BlockState adjacentState;
            BlockPos offset = pos.relative(d);
            if (!EnvelopeBlock.selfDye(level, offset, adjacentState = level.getBlockState(offset), color)) continue;
            hasDyed = true;
        }
        if (hasDyed) {
            return;
        }
        ObjectArrayList frontier = new ObjectArrayList();
        frontier.add(pos);
        ObjectOpenHashSet visited = new ObjectOpenHashSet();
        float timeout = 125.0f;
        while (!frontier.isEmpty()) {
            float f = timeout;
            timeout = f - 1.0f;
            if (f < 0.0f) break;
            BlockPos currentPos = (BlockPos)frontier.removeFirst();
            visited.add(currentPos);
            for (BlockPos d : DIRECTION_OFFSETS) {
                BlockState adjacentState;
                BlockPos offsetPos = currentPos.offset((Vec3i)d);
                if (visited.contains(offsetPos) || !EnvelopeBlock.multiDye(level, offsetPos, adjacentState = level.getBlockState(offsetPos), newEnvelopeState) && !EnvelopeBlock.multiDye(level, offsetPos, adjacentState, newEncasedEnvelopeState)) continue;
                frontier.add(offsetPos);
                visited.add(offsetPos);
            }
        }
    }

    static boolean selfDye(Level level, BlockPos pos, BlockState state, DyeColor color) {
        Envelope eb;
        Block block = state.getBlock();
        if (block instanceof EnvelopeBlock && ((EnvelopeBlock)(eb = (EnvelopeBlock)block)).getColor() != color) {
            level.setBlockAndUpdate(pos, AeroBlocks.DYED_ENVELOPE_BLOCKS.get(color).getDefaultState());
            return true;
        }
        block = state.getBlock();
        if (block instanceof EnvelopeEncasedShaftBlock && ((EnvelopeEncasedShaftBlock)(eb = (EnvelopeEncasedShaftBlock)block)).getColor() != color) {
            Direction.Axis axis = eb.getRotationAxis(state);
            level.setBlockAndUpdate(pos, (BlockState)AeroBlocks.ENVELOPE_ENCASED_SHAFTS.get(color).getDefaultState().setValue((Property)RotatedPillarKineticBlock.AXIS, (Comparable)axis));
            return true;
        }
        return false;
    }

    static boolean multiDye(Level Level2, BlockPos pos, BlockState state, BlockState newState) {
        if (state.getBlock() instanceof EnvelopeBlock && newState.getBlock() instanceof EnvelopeBlock) {
            if (state != newState) {
                Level2.setBlockAndUpdate(pos, newState);
            }
            return true;
        }
        if (state.getBlock() instanceof EnvelopeEncasedShaftBlock && newState.getBlock() instanceof EnvelopeEncasedShaftBlock) {
            if (state != newState) {
                Direction.Axis axis = (Direction.Axis)state.getValue((Property)RotatedPillarKineticBlock.AXIS);
                Level2.setBlockAndUpdate(pos, (BlockState)newState.setValue((Property)RotatedPillarKineticBlock.AXIS, (Comparable)axis));
            }
            return true;
        }
        return false;
    }

    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        DyeColor color = SimItemService.getDyeColor((ItemStack)itemStack);
        if (color != null) {
            if (!level.isClientSide()) {
                level.playSound(null, blockPos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.1f - level.random.nextFloat() * 0.2f);
            }
            EnvelopeBlock.applyDye(blockState, level, blockPos, color);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return AeroBlocks.DYED_ENVELOPE_BLOCKS.get(this.color).asStack();
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

    public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
        if (pEntity.isSuppressingBounce()) {
            super.fallOn(pLevel, pState, pPos, pEntity, pFallDistance);
        } else {
            pEntity.causeFallDamage(pFallDistance, 0.0f, pLevel.damageSources().fall());
        }
    }

    public void updateEntityAfterFallOn(BlockGetter pLevel, Entity pEntity) {
        if (pEntity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(pLevel, pEntity);
        } else {
            this.bounceUp(pEntity);
        }
    }

    private void bounceUp(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < 0.0) {
            double scale = 0.65 * (entity instanceof LivingEntity ? 1.0 : 0.8);
            entity.setDeltaMovement(vec3.x, -vec3.y * scale, vec3.z);
        }
    }

    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity blockEntity) {
        ItemStack stack = AeroBlocks.WHITE_ENVELOPE_BLOCK.asStack();
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, stack);
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement$ItemUseType
 *  dev.simulated_team.simulated.service.SimItemService
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.envelope;

import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import dev.eriksonn.aeronautics.content.blocks.hot_air.envelope.Envelope;
import dev.eriksonn.aeronautics.content.blocks.hot_air.envelope.EnvelopeBlock;
import dev.eriksonn.aeronautics.index.AeroBlockEntityTypes;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.simulated_team.simulated.service.SimItemService;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EnvelopeEncasedShaftBlock
extends EncasedShaftBlock
implements Envelope,
SpecialBlockItemRequirement {
    protected final DyeColor color;

    protected EnvelopeEncasedShaftBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties, () -> (Block)AeroBlocks.ENVELOPE_ENCASED_SHAFTS.get(color).get());
        this.color = color;
    }

    public static EnvelopeEncasedShaftBlock withCanvas(BlockBehaviour.Properties properties, DyeColor color) {
        return new EnvelopeEncasedShaftBlock(properties, color);
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

    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Player player;
        super.onSneakWrenched(state, context);
        Level world = context.getLevel();
        if (world instanceof ServerLevel && (player = context.getPlayer()) != null && !player.isCreative()) {
            player.getInventory().placeItemBackInInventory(AeroBlocks.WHITE_ENVELOPE_BLOCK.asStack());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AeroBlockEntityTypes.ENVELOPE_ENCASED_SHAFT.get();
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AeroBlockEntityTypes.ENVELOPE_ENCASED_SHAFT.create(pos, state);
    }

    public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
        if (pEntity.isSuppressingBounce()) {
            super.fallOn(pLevel, pState, pPos, pEntity, pFallDistance);
        } else {
            pEntity.causeFallDamage(pFallDistance, 0.5f, pLevel.damageSources().fall());
        }
    }

    public void updateEntityAfterFallOn(BlockGetter pLevel, Entity pEntity) {
        if (pEntity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(pLevel, pEntity);
        } else {
            this.bounceUp(pEntity);
        }
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return this.getCasing().asItem().getDefaultInstance();
    }

    public void bounceUp(Entity pEntity) {
        Vec3 vec3 = pEntity.getDeltaMovement();
        if (vec3.y < 0.0) {
            double d0 = pEntity instanceof LivingEntity ? 0.5 : 0.25;
            pEntity.setDeltaMovement(vec3.x, -vec3.y * d0, vec3.z);
        }
    }

    public Block getCasing() {
        return (Block)AeroBlocks.DYED_ENVELOPE_BLOCKS.get(this.color).get();
    }

    public void handleEncasing(BlockState state, Level level, BlockPos pos, ItemStack heldItem, Player player, InteractionHand hand, BlockHitResult ray) {
        super.handleEncasing(state, level, pos, heldItem, player, hand, ray);
        if (!player.isCreative()) {
            player.getItemInHand(hand).shrink(1);
        }
    }

    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        ItemStack stack = AeroBlocks.WHITE_ENVELOPE_BLOCK.asStack();
        return super.getRequiredItems(state, be).union(new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, stack));
    }
}

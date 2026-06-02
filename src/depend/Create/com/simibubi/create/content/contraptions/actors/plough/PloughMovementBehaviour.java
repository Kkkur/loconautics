/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.FallingBlockEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.AnvilBlock
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BubbleColumnBlock
 *  net.minecraft.world.level.block.FarmBlock
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.storage.loot.LootParams$Builder
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.actors.plough;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.actors.plough.PloughBlock;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import com.simibubi.create.content.trains.track.FakeTrackBlock;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PloughMovementBehaviour
extends BlockBreakingMovementBehaviour {
    @Override
    public boolean isActive(MovementContext context) {
        return super.isActive(context) && !VecHelper.isVecPointingTowards((Vec3)context.relativeMotion, (Direction)((Direction)context.state.getValue((Property)PloughBlock.FACING)).getOpposite());
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        super.visitNewPosition(context, pos);
        Level world = context.world;
        if (world.isClientSide) {
            return;
        }
        BlockPos below = pos.below();
        if (!world.isLoaded(below)) {
            return;
        }
        Vec3 vec = VecHelper.getCenterOf((Vec3i)pos);
        PloughBlock.PloughFakePlayer player = this.getPlayer(context);
        if (player == null) {
            return;
        }
        BlockHitResult ray = world.clip(new ClipContext(vec, vec.add(0.0, -1.0, 0.0), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)player));
        if (ray.getType() != HitResult.Type.BLOCK) {
            return;
        }
        UseOnContext ctx = new UseOnContext((Player)player, InteractionHand.MAIN_HAND, ray);
        new ItemStack((ItemLike)Items.DIAMOND_HOE).useOn(ctx);
    }

    @Override
    protected void throwEntity(MovementContext context, Entity entity) {
        super.throwEntity(context, entity);
        if (!(entity instanceof FallingBlockEntity)) {
            return;
        }
        FallingBlockEntity fbe = (FallingBlockEntity)entity;
        if (!(fbe.getBlockState().getBlock() instanceof AnvilBlock)) {
            return;
        }
        if (entity.getDeltaMovement().length() < 0.25) {
            return;
        }
        entity.level().getEntitiesOfClass(Player.class, new AABB(entity.blockPosition()).inflate(32.0)).forEach(AllAdvancements.ANVIL_PLOUGH::awardTo);
    }

    @Override
    public Vec3 getActiveAreaOffset(MovementContext context) {
        return Vec3.atLowerCornerOf((Vec3i)((Direction)context.state.getValue((Property)PloughBlock.FACING)).getNormal()).scale(0.45);
    }

    @Override
    protected boolean throwsEntities(Level level) {
        return true;
    }

    @Override
    public boolean canBreak(Level world, BlockPos breakingPos, BlockState state) {
        if (state.isAir()) {
            return false;
        }
        if (world.getBlockState(breakingPos.below()).getBlock() instanceof FarmBlock) {
            return false;
        }
        if (state.getBlock() instanceof LiquidBlock) {
            return false;
        }
        if (state.getBlock() instanceof BubbleColumnBlock) {
            return false;
        }
        if (state.getBlock() instanceof ITrackBlock) {
            return true;
        }
        if (state.getBlock() instanceof FakeTrackBlock) {
            return false;
        }
        if (AllTags.AllBlockTags.PLOUGH_BLACKLIST.matches(state.getBlock())) {
            return false;
        }
        if (AllTags.AllBlockTags.PLOUGH_WHITELIST.matches(state.getBlock())) {
            return true;
        }
        return state.getCollisionShape((BlockGetter)world, breakingPos).isEmpty();
    }

    @Override
    protected void onBlockBroken(MovementContext context, BlockPos pos, BlockState brokenState) {
        Level level;
        super.onBlockBroken(context, pos, brokenState);
        if (brokenState.getBlock() == Blocks.SNOW && (level = context.world) instanceof ServerLevel) {
            ServerLevel world = (ServerLevel)level;
            brokenState.getDrops(new LootParams.Builder(world).withParameter(LootContextParams.BLOCK_STATE, (Object)brokenState).withParameter(LootContextParams.ORIGIN, (Object)Vec3.atCenterOf((Vec3i)pos)).withParameter(LootContextParams.THIS_ENTITY, (Object)this.getPlayer(context)).withParameter(LootContextParams.TOOL, (Object)new ItemStack((ItemLike)Items.IRON_SHOVEL))).forEach(s -> this.collectOrDropItem(context, (ItemStack)s));
        }
    }

    @Override
    public void stopMoving(MovementContext context) {
        super.stopMoving(context);
        if (context.temporaryData instanceof PloughBlock.PloughFakePlayer) {
            ((PloughBlock.PloughFakePlayer)((Object)context.temporaryData)).discard();
        }
    }

    private PloughBlock.PloughFakePlayer getPlayer(MovementContext context) {
        if (!(context.temporaryData instanceof PloughBlock.PloughFakePlayer) && context.world != null) {
            PloughBlock.PloughFakePlayer player = new PloughBlock.PloughFakePlayer((ServerLevel)context.world);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)Items.DIAMOND_HOE));
            context.temporaryData = player;
        }
        return (PloughBlock.PloughFakePlayer)((Object)context.temporaryData);
    }
}

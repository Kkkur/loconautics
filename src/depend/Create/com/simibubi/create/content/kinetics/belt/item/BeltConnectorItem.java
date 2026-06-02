/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.kinetics.belt.item;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.LinkedList;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public class BeltConnectorItem
extends BlockItem {
    public BeltConnectorItem(Item.Properties properties) {
        super((Block)AllBlocks.BELT.get(), properties);
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @NotNull
    public InteractionResult useOn(UseOnContext context) {
        Player playerEntity = context.getPlayer();
        ItemStack heldStack = context.getItemInHand();
        if (playerEntity != null && playerEntity.isShiftKeyDown()) {
            heldStack.remove(AllDataComponents.BELT_FIRST_SHAFT);
            return InteractionResult.SUCCESS;
        }
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean validAxis = BeltConnectorItem.validateAxis(world, pos);
        if (world.isClientSide) {
            return validAxis ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        BlockPos firstPulley = null;
        if (!(!heldStack.has(AllDataComponents.BELT_FIRST_SHAFT) || BeltConnectorItem.validateAxis(world, firstPulley = (BlockPos)heldStack.get(AllDataComponents.BELT_FIRST_SHAFT)) && firstPulley.closerThan((Vec3i)pos, (double)(BeltConnectorItem.maxLength() * 2)))) {
            heldStack.remove(AllDataComponents.BELT_FIRST_SHAFT);
        }
        if (!validAxis || playerEntity == null) {
            return InteractionResult.FAIL;
        }
        if (heldStack.has(AllDataComponents.BELT_FIRST_SHAFT)) {
            if (!BeltConnectorItem.canConnect(world, firstPulley, pos)) {
                return InteractionResult.FAIL;
            }
            if (firstPulley != null && !firstPulley.equals((Object)pos)) {
                BeltConnectorItem.createBelts(world, firstPulley, pos);
                AllAdvancements.BELT.awardTo(playerEntity);
                if (!playerEntity.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
            }
            if (!context.getItemInHand().isEmpty()) {
                heldStack.remove(AllDataComponents.BELT_FIRST_SHAFT);
                playerEntity.getCooldowns().addCooldown((Item)this, 5);
            }
            return InteractionResult.SUCCESS;
        }
        heldStack.set(AllDataComponents.BELT_FIRST_SHAFT, (Object)pos);
        playerEntity.getCooldowns().addCooldown((Item)this, 5);
        return InteractionResult.SUCCESS;
    }

    public static void createBelts(Level world, BlockPos start, BlockPos end) {
        world.playSound(null, BlockPos.containing((Position)VecHelper.getCenterOf((Vec3i)start.offset((Vec3i)end)).scale(0.5)), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);
        BeltSlope slope = BeltConnectorItem.getSlopeBetween(start, end);
        Direction facing = BeltConnectorItem.getFacingFromTo(start, end);
        BlockPos diff = end.subtract((Vec3i)start);
        if (diff.getX() == diff.getZ()) {
            facing = Direction.get((Direction.AxisDirection)facing.getAxisDirection(), (Direction.Axis)(world.getBlockState(start).getValue((Property)BlockStateProperties.AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X));
        }
        List<BlockPos> beltsToCreate = BeltConnectorItem.getBeltChainBetween(start, end, slope, facing);
        BlockState beltBlock = AllBlocks.BELT.getDefaultState();
        boolean failed = false;
        for (BlockPos pos : beltsToCreate) {
            BlockState existingBlock = world.getBlockState(pos);
            if (existingBlock.getDestroySpeed((BlockGetter)world, pos) == -1.0f) {
                failed = true;
                break;
            }
            BeltPart part = pos.equals((Object)start) ? BeltPart.START : (pos.equals((Object)end) ? BeltPart.END : BeltPart.MIDDLE);
            BlockState shaftState = world.getBlockState(pos);
            boolean pulley = ShaftBlock.isShaft(shaftState);
            if (part == BeltPart.MIDDLE && pulley) {
                part = BeltPart.PULLEY;
            }
            if (pulley && shaftState.getValue((Property)AbstractSimpleShaftBlock.AXIS) == Direction.Axis.Y) {
                slope = BeltSlope.SIDEWAYS;
            }
            if (!existingBlock.canBeReplaced()) {
                world.destroyBlock(pos, false);
            }
            KineticBlockEntity.switchToBlockState(world, pos, ProperWaterloggedBlock.withWater((LevelAccessor)world, (BlockState)((BlockState)((BlockState)beltBlock.setValue(BeltBlock.SLOPE, (Comparable)((Object)slope))).setValue(BeltBlock.PART, (Comparable)((Object)part))).setValue(BeltBlock.HORIZONTAL_FACING, (Comparable)facing), pos));
        }
        if (!failed) {
            return;
        }
        for (BlockPos pos : beltsToCreate) {
            if (!AllBlocks.BELT.has(world.getBlockState(pos))) continue;
            world.destroyBlock(pos, false);
        }
    }

    private static Direction getFacingFromTo(BlockPos start, BlockPos end) {
        Direction.Axis beltAxis = start.getX() == end.getX() ? Direction.Axis.Z : Direction.Axis.X;
        BlockPos diff = end.subtract((Vec3i)start);
        Direction.AxisDirection axisDirection = Direction.AxisDirection.POSITIVE;
        axisDirection = diff.getX() == 0 && diff.getZ() == 0 ? (diff.getY() > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE) : (beltAxis.choose(diff.getX(), 0, diff.getZ()) > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
        return Direction.get((Direction.AxisDirection)axisDirection, (Direction.Axis)beltAxis);
    }

    private static BeltSlope getSlopeBetween(BlockPos start, BlockPos end) {
        BlockPos diff = end.subtract((Vec3i)start);
        if (diff.getY() != 0) {
            if (diff.getZ() != 0 || diff.getX() != 0) {
                return diff.getY() > 0 ? BeltSlope.UPWARD : BeltSlope.DOWNWARD;
            }
            return BeltSlope.VERTICAL;
        }
        return BeltSlope.HORIZONTAL;
    }

    private static List<BlockPos> getBeltChainBetween(BlockPos start, BlockPos end, BeltSlope slope, Direction direction) {
        LinkedList<BlockPos> positions = new LinkedList<BlockPos>();
        int limit = 1000;
        BlockPos current = start;
        do {
            positions.add(current);
            if (slope == BeltSlope.VERTICAL) {
                current = current.above(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1);
                continue;
            }
            current = current.relative(direction);
            if (slope == BeltSlope.HORIZONTAL) continue;
            current = current.above(slope == BeltSlope.UPWARD ? 1 : -1);
        } while (!current.equals((Object)end) && limit-- > 0);
        positions.add(end);
        return positions;
    }

    public static boolean canConnect(Level world, BlockPos first, BlockPos second) {
        if (!world.isLoaded(first) || !world.isLoaded(second)) {
            return false;
        }
        if (!second.closerThan((Vec3i)first, (double)BeltConnectorItem.maxLength().intValue())) {
            return false;
        }
        BlockPos diff = second.subtract((Vec3i)first);
        Direction.Axis shaftAxis = (Direction.Axis)world.getBlockState(first).getValue((Property)BlockStateProperties.AXIS);
        int x = diff.getX();
        int y = diff.getY();
        int z = diff.getZ();
        int sames = (Math.abs(x) == Math.abs(y) ? 1 : 0) + (Math.abs(y) == Math.abs(z) ? 1 : 0) + (Math.abs(z) == Math.abs(x) ? 1 : 0);
        if (shaftAxis.choose(x, y, z) != 0) {
            return false;
        }
        if (sames != 1) {
            return false;
        }
        if (shaftAxis != world.getBlockState(second).getValue((Property)BlockStateProperties.AXIS)) {
            return false;
        }
        if (shaftAxis == Direction.Axis.Y && x != 0 && z != 0) {
            return false;
        }
        BlockEntity blockEntity = world.getBlockEntity(first);
        BlockEntity blockEntity2 = world.getBlockEntity(second);
        if (!(blockEntity instanceof KineticBlockEntity)) {
            return false;
        }
        if (!(blockEntity2 instanceof KineticBlockEntity)) {
            return false;
        }
        float speed1 = ((KineticBlockEntity)blockEntity).getTheoreticalSpeed();
        float speed2 = ((KineticBlockEntity)blockEntity2).getTheoreticalSpeed();
        if (Math.signum(speed1) != Math.signum(speed2) && speed1 != 0.0f && speed2 != 0.0f) {
            return false;
        }
        BlockPos step = BlockPos.containing((double)Math.signum(diff.getX()), (double)Math.signum(diff.getY()), (double)Math.signum(diff.getZ()));
        int limit = 1000;
        BlockPos currentPos = first.offset((Vec3i)step);
        while (!currentPos.equals((Object)second) && limit-- > 0) {
            BlockState blockState = world.getBlockState(currentPos);
            if (!(ShaftBlock.isShaft(blockState) && blockState.getValue((Property)AbstractSimpleShaftBlock.AXIS) == shaftAxis || blockState.canBeReplaced())) {
                return false;
            }
            currentPos = currentPos.offset((Vec3i)step);
        }
        return true;
    }

    public static Integer maxLength() {
        return (Integer)AllConfigs.server().kinetics.maxBeltLength.get();
    }

    public static boolean validateAxis(Level world, BlockPos pos) {
        if (!world.isLoaded(pos)) {
            return false;
        }
        return ShaftBlock.isShaft(world.getBlockState(pos));
    }
}

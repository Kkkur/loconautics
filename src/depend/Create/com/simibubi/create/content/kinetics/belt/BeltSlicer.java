/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.Outliner
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.kinetics.belt;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BeltSlicer {
    public static ItemInteractionResult useWrench(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, Feedback feedBack) {
        BlockPos next;
        boolean towardPositive;
        int hitSegment;
        BeltBlockEntity segmentBE;
        List<BlockPos> beltChain;
        BeltBlockEntity controllerBE;
        block32: {
            controllerBE = BeltHelper.getControllerBE((LevelAccessor)world, pos);
            if (controllerBE == null) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (((Boolean)state.getValue((Property)BeltBlock.CASING)).booleanValue() && hit.getDirection() != Direction.UP) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (state.getValue(BeltBlock.PART) == BeltPart.PULLEY && hit.getDirection().getAxis() != Direction.Axis.Y) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            int beltLength = controllerBE.beltLength;
            if (beltLength == 2) {
                return ItemInteractionResult.FAIL;
            }
            BlockPos beltVector = BlockPos.containing((Position)BeltHelper.getBeltVector(state));
            BeltPart part = (BeltPart)((Object)state.getValue(BeltBlock.PART));
            beltChain = BeltBlock.getBeltChain((LevelAccessor)world, controllerBE.getBlockPos());
            boolean creative = player.isCreative();
            if (BeltSlicer.hoveringEnd(state, hit)) {
                if (world.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }
                for (BlockPos blockPos : beltChain) {
                    BeltBlockEntity belt = BeltHelper.getSegmentBE((LevelAccessor)world, blockPos);
                    if (belt == null) continue;
                    belt.detachKinetics();
                    belt.invalidateItemHandler();
                    belt.beltLength = 0;
                }
                BeltInventory inventory = controllerBE.inventory;
                BlockPos next2 = part == BeltPart.END ? pos.subtract((Vec3i)beltVector) : pos.offset((Vec3i)beltVector);
                BlockState replacedState = world.getBlockState(next2);
                BeltBlockEntity segmentBE2 = BeltHelper.getSegmentBE((LevelAccessor)world, next2);
                KineticBlockEntity.switchToBlockState(world, next2, ProperWaterloggedBlock.withWater((LevelAccessor)world, (BlockState)state.setValue((Property)BeltBlock.CASING, (Comparable)Boolean.valueOf(segmentBE2 != null && segmentBE2.casing != BeltBlockEntity.CasingType.NONE)), next2));
                world.setBlock(pos, ProperWaterloggedBlock.withWater((LevelAccessor)world, Blocks.AIR.defaultBlockState(), pos), 67);
                world.removeBlockEntity(pos);
                world.levelEvent(2001, pos, Block.getId((BlockState)state));
                if (!creative && AllBlocks.BELT.has(replacedState) && replacedState.getValue(BeltBlock.PART) == BeltPart.PULLEY) {
                    player.getInventory().placeItemBackInInventory(AllBlocks.SHAFT.asStack());
                }
                if (part == BeltPart.END && inventory != null) {
                    ArrayList toEject = new ArrayList();
                    for (TransportedItemStack transportedItemStack : inventory.getTransportedItems()) {
                        if (!(transportedItemStack.beltPosition > (float)(beltLength - 1))) continue;
                        toEject.add(transportedItemStack);
                    }
                    toEject.forEach(inventory::eject);
                    toEject.forEach(inventory.getTransportedItems()::remove);
                }
                if (part == BeltPart.START && segmentBE2 != null && inventory != null) {
                    controllerBE.inventory = null;
                    segmentBE2.inventory = null;
                    segmentBE2.setController(next2);
                    for (TransportedItemStack transportedItemStack : inventory.getTransportedItems()) {
                        transportedItemStack.beltPosition -= 1.0f;
                        if (transportedItemStack.beltPosition <= 0.0f) {
                            ItemEntity entity = new ItemEntity(world, (double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() + 0.6875f), (double)((float)pos.getZ() + 0.5f), transportedItemStack.stack);
                            entity.setDeltaMovement(Vec3.ZERO);
                            entity.setDefaultPickUpDelay();
                            entity.hurtMarked = true;
                            world.addFreshEntity((Entity)entity);
                            continue;
                        }
                        segmentBE2.getInventory().addItem(transportedItemStack);
                    }
                }
                return ItemInteractionResult.SUCCESS;
            }
            segmentBE = BeltHelper.getSegmentBE((LevelAccessor)world, pos);
            if (segmentBE == null) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            hitSegment = segmentBE.index;
            Vec3 centerOf = VecHelper.getCenterOf((Vec3i)hit.getBlockPos());
            Vec3 subtract = hit.getLocation().subtract(centerOf);
            towardPositive = subtract.dot(Vec3.atLowerCornerOf((Vec3i)beltVector)) > 0.0;
            BlockPos blockPos = next = !towardPositive ? pos.subtract((Vec3i)beltVector) : pos.offset((Vec3i)beltVector);
            if (hitSegment == 0 || hitSegment == 1 && !towardPositive) {
                return ItemInteractionResult.FAIL;
            }
            if (hitSegment == controllerBE.beltLength - 1 || hitSegment == controllerBE.beltLength - 2 && towardPositive) {
                return ItemInteractionResult.FAIL;
            }
            if (!creative) {
                BlockState other;
                int requiredShafts = 0;
                if (!segmentBE.hasPulley()) {
                    ++requiredShafts;
                }
                if (AllBlocks.BELT.has(other = world.getBlockState(next)) && other.getValue(BeltBlock.PART) == BeltPart.MIDDLE) {
                    ++requiredShafts;
                }
                int amountRetrieved = 0;
                boolean beltFound = false;
                for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                    if (amountRetrieved != requiredShafts || !beltFound) {
                        ItemStack itemstack = player.getInventory().getItem(i);
                        if (itemstack.isEmpty()) continue;
                        int count = itemstack.getCount();
                        if (AllItems.BELT_CONNECTOR.isIn(itemstack) && !beltFound) {
                            if (!world.isClientSide) {
                                itemstack.shrink(1);
                            }
                            beltFound = true;
                            continue;
                        }
                        if (!AllBlocks.SHAFT.isIn(itemstack)) continue;
                        int taken = Math.min(count, requiredShafts - amountRetrieved);
                        if (!world.isClientSide) {
                            if (taken == count) {
                                player.getInventory().setItem(i, ItemStack.EMPTY);
                            } else {
                                itemstack.shrink(taken);
                            }
                        }
                        amountRetrieved += taken;
                        continue;
                    }
                    break block32;
                }
                if (!world.isClientSide) {
                    player.getInventory().placeItemBackInInventory(AllBlocks.SHAFT.asStack(amountRetrieved));
                    if (beltFound) {
                        player.getInventory().placeItemBackInInventory(AllItems.BELT_CONNECTOR.asStack());
                    }
                }
                return ItemInteractionResult.FAIL;
            }
        }
        if (!world.isClientSide) {
            BeltBlockEntity newController;
            for (BlockPos blockPos : beltChain) {
                BeltBlockEntity belt = BeltHelper.getSegmentBE((LevelAccessor)world, blockPos);
                if (belt == null) continue;
                belt.detachKinetics();
                belt.invalidateItemHandler();
                belt.beltLength = 0;
            }
            BeltInventory inventory = controllerBE.inventory;
            KineticBlockEntity.switchToBlockState(world, pos, (BlockState)state.setValue(BeltBlock.PART, (Comparable)((Object)(towardPositive ? BeltPart.END : BeltPart.START))));
            KineticBlockEntity.switchToBlockState(world, next, (BlockState)world.getBlockState(next).setValue(BeltBlock.PART, (Comparable)((Object)(towardPositive ? BeltPart.START : BeltPart.END))));
            world.playSound(null, pos, SoundEvents.WOOL_HIT, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 0.5f, 2.3f);
            BeltBlockEntity beltBlockEntity = newController = towardPositive ? BeltHelper.getSegmentBE((LevelAccessor)world, next) : segmentBE;
            if (newController != null && inventory != null) {
                newController.inventory = null;
                newController.setController(newController.getBlockPos());
                Iterator<TransportedItemStack> iterator = inventory.getTransportedItems().iterator();
                while (iterator.hasNext()) {
                    TransportedItemStack transportedItemStack = iterator.next();
                    float newPosition = transportedItemStack.beltPosition - (float)hitSegment - (float)(towardPositive ? 1 : 0);
                    if (newPosition <= 0.0f) continue;
                    transportedItemStack.beltPosition = newPosition;
                    iterator.remove();
                    newController.getInventory().addItem(transportedItemStack);
                }
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    /*
     * WARNING - void declaration
     */
    public static ItemInteractionResult useConnector(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, Feedback feedBack) {
        block23: {
            void var21_29;
            BlockState blockState;
            BeltInventory inventory;
            int mergedBeltLength;
            BeltBlockEntity mergedController;
            BlockPos next;
            boolean creative;
            BlockPos beltVector;
            int beltLength;
            BeltBlockEntity controllerBE;
            block24: {
                controllerBE = BeltHelper.getControllerBE((LevelAccessor)world, pos);
                if (controllerBE == null) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                beltLength = controllerBE.beltLength;
                if (beltLength == BeltConnectorItem.maxLength()) {
                    return ItemInteractionResult.FAIL;
                }
                beltVector = BlockPos.containing((Position)BeltHelper.getBeltVector(state));
                BeltPart part = (BeltPart)((Object)state.getValue(BeltBlock.PART));
                Direction facing = (Direction)state.getValue(BeltBlock.HORIZONTAL_FACING);
                List<BlockPos> beltChain = BeltBlock.getBeltChain((LevelAccessor)world, controllerBE.getBlockPos());
                creative = player.isCreative();
                if (!BeltSlicer.hoveringEnd(state, hit)) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                next = part == BeltPart.START ? pos.subtract((Vec3i)beltVector) : pos.offset((Vec3i)beltVector);
                mergedController = null;
                mergedBeltLength = 0;
                BlockState nextState = world.getBlockState(next);
                if (!nextState.canBeReplaced()) {
                    if (!AllBlocks.BELT.has(nextState)) {
                        return ItemInteractionResult.FAIL;
                    }
                    if (!BeltSlicer.beltStatesCompatible(state, nextState)) {
                        return ItemInteractionResult.FAIL;
                    }
                    mergedController = BeltHelper.getControllerBE((LevelAccessor)world, next);
                    if (mergedController == null) {
                        return ItemInteractionResult.FAIL;
                    }
                    if (mergedController.beltLength + beltLength > BeltConnectorItem.maxLength()) {
                        return ItemInteractionResult.FAIL;
                    }
                    mergedBeltLength = mergedController.beltLength;
                    if (!world.isClientSide) {
                        boolean flipBelt = facing != nextState.getValue(BeltBlock.HORIZONTAL_FACING);
                        Optional<DyeColor> color = controllerBE.color;
                        for (BlockPos blockPos : BeltBlock.getBeltChain((LevelAccessor)world, mergedController.getBlockPos())) {
                            BeltBlockEntity belt = BeltHelper.getSegmentBE((LevelAccessor)world, blockPos);
                            if (belt == null) continue;
                            belt.detachKinetics();
                            belt.invalidateItemHandler();
                            belt.beltLength = 0;
                            belt.color = color;
                            if (!flipBelt) continue;
                            world.setBlock(blockPos, BeltSlicer.flipBelt(world.getBlockState(blockPos)), 67);
                        }
                        if (flipBelt && mergedController.inventory != null) {
                            List<TransportedItemStack> transportedItems = mergedController.inventory.getTransportedItems();
                            for (TransportedItemStack transportedItemStack : transportedItems) {
                                transportedItemStack.beltPosition = (float)mergedBeltLength - transportedItemStack.beltPosition;
                                transportedItemStack.prevBeltPosition = (float)mergedBeltLength - transportedItemStack.prevBeltPosition;
                            }
                        }
                        beltChain = BeltBlock.getBeltChain((LevelAccessor)world, mergedController.getBlockPos());
                    }
                }
                if (world.isClientSide) break block23;
                for (BlockPos blockPos : beltChain) {
                    BeltBlockEntity belt = BeltHelper.getSegmentBE((LevelAccessor)world, blockPos);
                    if (belt == null) continue;
                    belt.detachKinetics();
                    belt.invalidateItemHandler();
                    belt.beltLength = 0;
                }
                inventory = controllerBE.inventory;
                KineticBlockEntity.switchToBlockState(world, pos, (BlockState)state.setValue(BeltBlock.PART, (Comparable)((Object)BeltPart.MIDDLE)));
                if (mergedController != null) break block24;
                world.setBlock(next, ProperWaterloggedBlock.withWater((LevelAccessor)world, (BlockState)state.setValue((Property)BeltBlock.CASING, (Comparable)Boolean.valueOf(false)), next), 67);
                BeltBlockEntity segmentBE = BeltHelper.getSegmentBE((LevelAccessor)world, next);
                if (segmentBE != null) {
                    segmentBE.color = controllerBE.color;
                }
                world.playSound(null, pos, SoundEvents.WOOL_PLACE, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 0.5f, 1.0f);
                if (part != BeltPart.START || segmentBE == null || inventory == null) break block23;
                segmentBE.setController(next);
                for (TransportedItemStack transportedItemStack : inventory.getTransportedItems()) {
                    transportedItemStack.beltPosition += 1.0f;
                    segmentBE.getInventory().addItem(transportedItemStack);
                }
                break block23;
            }
            BeltInventory mergedInventory = mergedController.inventory;
            world.playSound(null, pos, SoundEvents.WOOL_HIT, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 0.5f, 1.3f);
            BeltBlockEntity segmentBE = BeltHelper.getSegmentBE((LevelAccessor)world, next);
            KineticBlockEntity.switchToBlockState(world, next, (BlockState)((BlockState)state.setValue((Property)BeltBlock.CASING, (Comparable)Boolean.valueOf(segmentBE != null && segmentBE.casing != BeltBlockEntity.CasingType.NONE))).setValue(BeltBlock.PART, (Comparable)((Object)BeltPart.MIDDLE)));
            if (!creative) {
                player.getInventory().placeItemBackInInventory(AllBlocks.SHAFT.asStack(2));
                player.getInventory().placeItemBackInInventory(AllItems.BELT_CONNECTOR.asStack());
            }
            for (BlockPos blockPos : BeltBlock.getBeltChain((LevelAccessor)world, controllerBE.getBlockPos())) {
                BeltBlockEntity belt = BeltHelper.getSegmentBE((LevelAccessor)world, blockPos);
                if (belt == null) continue;
                belt.invalidateItemHandler();
            }
            BlockPos blockPos = controllerBE.getBlockPos();
            for (int i = 0; i < 10000 && AllBlocks.BELT.has(blockState = world.getBlockState((BlockPos)var21_29)); ++i) {
                if (blockState.getValue(BeltBlock.PART) != BeltPart.START) {
                    BlockPos blockPos2 = var21_29.subtract((Vec3i)beltVector);
                    continue;
                }
                BeltBlockEntity newController = BeltHelper.getSegmentBE((LevelAccessor)world, (BlockPos)var21_29);
                if (newController != controllerBE && inventory != null) {
                    newController.setController((BlockPos)var21_29);
                    controllerBE.inventory = null;
                    for (TransportedItemStack transportedItemStack : inventory.getTransportedItems()) {
                        transportedItemStack.beltPosition += (float)mergedBeltLength;
                        newController.getInventory().addItem(transportedItemStack);
                    }
                }
                if (newController == mergedController || mergedInventory == null) break;
                newController.setController((BlockPos)var21_29);
                mergedController.inventory = null;
                for (TransportedItemStack transportedItemStack : mergedInventory.getTransportedItems()) {
                    if (newController == controllerBE) {
                        transportedItemStack.beltPosition += (float)beltLength;
                    }
                    newController.getInventory().addItem(transportedItemStack);
                }
                break;
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    static boolean beltStatesCompatible(BlockState state, BlockState nextState) {
        Direction facing1 = (Direction)state.getValue(BeltBlock.HORIZONTAL_FACING);
        BeltSlope slope1 = (BeltSlope)((Object)state.getValue(BeltBlock.SLOPE));
        Direction facing2 = (Direction)nextState.getValue(BeltBlock.HORIZONTAL_FACING);
        BeltSlope slope2 = (BeltSlope)((Object)nextState.getValue(BeltBlock.SLOPE));
        switch (slope1) {
            case UPWARD: {
                if (slope2 == BeltSlope.DOWNWARD) {
                    return facing1 == facing2.getOpposite();
                }
                return slope2 == slope1 && facing1 == facing2;
            }
            case DOWNWARD: {
                if (slope2 == BeltSlope.UPWARD) {
                    return facing1 == facing2.getOpposite();
                }
                return slope2 == slope1 && facing1 == facing2;
            }
        }
        return slope2 == slope1 && facing2.getAxis() == facing1.getAxis();
    }

    static BlockState flipBelt(BlockState state) {
        Direction facing = (Direction)state.getValue(BeltBlock.HORIZONTAL_FACING);
        BeltSlope slope = (BeltSlope)((Object)state.getValue(BeltBlock.SLOPE));
        BeltPart part = (BeltPart)((Object)state.getValue(BeltBlock.PART));
        if (slope == BeltSlope.UPWARD) {
            state = (BlockState)state.setValue(BeltBlock.SLOPE, (Comparable)((Object)BeltSlope.DOWNWARD));
        } else if (slope == BeltSlope.DOWNWARD) {
            state = (BlockState)state.setValue(BeltBlock.SLOPE, (Comparable)((Object)BeltSlope.UPWARD));
        }
        if (part == BeltPart.END) {
            state = (BlockState)state.setValue(BeltBlock.PART, (Comparable)((Object)BeltPart.START));
        } else if (part == BeltPart.START) {
            state = (BlockState)state.setValue(BeltBlock.PART, (Comparable)((Object)BeltPart.END));
        }
        return (BlockState)state.setValue(BeltBlock.HORIZONTAL_FACING, (Comparable)facing.getOpposite());
    }

    static boolean hoveringEnd(BlockState state, BlockHitResult hit) {
        BeltPart part = (BeltPart)((Object)state.getValue(BeltBlock.PART));
        if (part == BeltPart.MIDDLE || part == BeltPart.PULLEY) {
            return false;
        }
        Vec3 beltVector = BeltHelper.getBeltVector(state);
        Vec3 centerOf = VecHelper.getCenterOf((Vec3i)hit.getBlockPos());
        Vec3 subtract = hit.getLocation().subtract(centerOf);
        return subtract.dot(beltVector) > 0.0 == (part == BeltPart.END);
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void tickHoveringInformation() {
        Minecraft mc = Minecraft.getInstance();
        HitResult target = mc.hitResult;
        if (target == null || !(target instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult result = (BlockHitResult)target;
        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack held = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack heldOffHand = mc.player.getItemInHand(InteractionHand.OFF_HAND);
        if (mc.player.isShiftKeyDown()) {
            return;
        }
        if (!AllBlocks.BELT.has(state)) {
            return;
        }
        Feedback feedback = new Feedback();
        if (AllItems.WRENCH.isIn(held) || AllItems.WRENCH.isIn(heldOffHand)) {
            BeltSlicer.useWrench(state, (Level)world, pos, (Player)mc.player, InteractionHand.MAIN_HAND, result, feedback);
        } else if (AllItems.BELT_CONNECTOR.isIn(held) || AllItems.BELT_CONNECTOR.isIn(heldOffHand)) {
            BeltSlicer.useConnector(state, (Level)world, pos, (Player)mc.player, InteractionHand.MAIN_HAND, result, feedback);
        } else {
            return;
        }
        if (feedback.langKey != null) {
            mc.player.displayClientMessage((Component)CreateLang.translateDirect(feedback.langKey, new Object[0]).withStyle(feedback.formatting), true);
        } else {
            mc.player.displayClientMessage(CommonComponents.EMPTY, true);
        }
        if (feedback.bb != null) {
            Outliner.getInstance().chaseAABB((Object)"BeltSlicer", feedback.bb).lineWidth(0.0625f).colored(feedback.color);
        }
    }

    public static class Feedback {
        int color = 0xFFFFFF;
        AABB bb;
        String langKey;
        ChatFormatting formatting = ChatFormatting.WHITE;
    }
}

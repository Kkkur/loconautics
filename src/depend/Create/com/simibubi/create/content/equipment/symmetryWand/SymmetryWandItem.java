/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Inventory
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
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 *  net.neoforged.neoforge.common.util.BlockSnapshot
 *  net.neoforged.neoforge.event.EventHooks
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.equipment.symmetryWand;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlock;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryEffectPacket;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItemRenderer;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandScreen;
import com.simibubi.create.content.equipment.symmetryWand.mirror.CrossPlaneMirror;
import com.simibubi.create.content.equipment.symmetryWand.mirror.EmptyMirror;
import com.simibubi.create.content.equipment.symmetryWand.mirror.PlaneMirror;
import com.simibubi.create.content.equipment.symmetryWand.mirror.SymmetryMirror;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class SymmetryWandItem
extends Item {
    public SymmetryWandItem(Item.Properties properties) {
        super(properties);
    }

    @NotNull
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        if (player == null) {
            return InteractionResult.PASS;
        }
        player.getCooldowns().addCooldown((Item)this, 5);
        ItemStack wand = player.getItemInHand(context.getHand());
        SymmetryWandItem.checkComponents(wand);
        if (player.isShiftKeyDown()) {
            if (player.level().isClientSide) {
                CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.openWandGUI(wand, context.getHand()));
                player.getCooldowns().addCooldown((Item)this, 5);
            }
            return InteractionResult.SUCCESS;
        }
        if (context.getLevel().isClientSide || context.getHand() != InteractionHand.MAIN_HAND) {
            return InteractionResult.SUCCESS;
        }
        pos = pos.relative(context.getClickedFace());
        SymmetryMirror previousElement = (SymmetryMirror)wand.get(AllDataComponents.SYMMETRY_WAND);
        wand.set(AllDataComponents.SYMMETRY_WAND_ENABLE, (Object)true);
        Vec3 pos3d = new Vec3((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        SymmetryMirror newElement = new PlaneMirror(pos3d);
        if (previousElement instanceof EmptyMirror) {
            ((SymmetryMirror)newElement).setOrientation(player.getDirection() == Direction.NORTH || player.getDirection() == Direction.SOUTH ? PlaneMirror.Align.XY.ordinal() : PlaneMirror.Align.YZ.ordinal());
            newElement.enable = true;
            wand.set(AllDataComponents.SYMMETRY_WAND_ENABLE, (Object)true);
        } else {
            previousElement.setPosition(pos3d);
            if (previousElement instanceof PlaneMirror) {
                previousElement.setOrientation(player.getDirection() == Direction.NORTH || player.getDirection() == Direction.SOUTH ? PlaneMirror.Align.XY.ordinal() : PlaneMirror.Align.YZ.ordinal());
            }
            if (previousElement instanceof CrossPlaneMirror) {
                float rotation = player.getYHeadRot();
                float abs = Math.abs(rotation % 90.0f);
                boolean diagonal = abs > 22.0f && abs < 67.0f;
                previousElement.setOrientation(diagonal ? CrossPlaneMirror.Align.D.ordinal() : CrossPlaneMirror.Align.Y.ordinal());
            }
            newElement = previousElement;
        }
        wand.set(AllDataComponents.SYMMETRY_WAND, (Object)newElement);
        player.setItemInHand(context.getHand(), wand);
        return InteractionResult.SUCCESS;
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack wand = playerIn.getItemInHand(handIn);
        SymmetryWandItem.checkComponents(wand);
        if (playerIn.isShiftKeyDown()) {
            if (worldIn.isClientSide) {
                CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.openWandGUI(playerIn.getItemInHand(handIn), handIn));
                playerIn.getCooldowns().addCooldown((Item)this, 5);
            }
            return new InteractionResultHolder(InteractionResult.SUCCESS, (Object)wand);
        }
        wand.set(AllDataComponents.SYMMETRY_WAND_ENABLE, (Object)false);
        return new InteractionResultHolder(InteractionResult.SUCCESS, (Object)wand);
    }

    @OnlyIn(value=Dist.CLIENT)
    private void openWandGUI(ItemStack wand, InteractionHand hand) {
        ScreenOpener.open((Screen)new SymmetryWandScreen(wand, hand));
    }

    private static void checkComponents(ItemStack wand) {
        if (!wand.has(AllDataComponents.SYMMETRY_WAND)) {
            wand.set(AllDataComponents.SYMMETRY_WAND, (Object)new EmptyMirror(new Vec3(0.0, 0.0, 0.0)));
            wand.set(AllDataComponents.SYMMETRY_WAND_ENABLE, (Object)false);
        }
    }

    public static boolean isEnabled(ItemStack stack) {
        SymmetryWandItem.checkComponents(stack);
        return (Boolean)stack.getOrDefault(AllDataComponents.SYMMETRY_WAND_ENABLE, (Object)false) != false && (Boolean)stack.getOrDefault(AllDataComponents.SYMMETRY_WAND_SIMULATE, (Object)false) == false;
    }

    public static SymmetryMirror getMirror(ItemStack stack) {
        SymmetryWandItem.checkComponents(stack);
        return (SymmetryMirror)stack.get(AllDataComponents.SYMMETRY_WAND);
    }

    public static void configureSettings(ItemStack stack, SymmetryMirror mirror) {
        SymmetryWandItem.checkComponents(stack);
        stack.set(AllDataComponents.SYMMETRY_WAND, (Object)mirror);
    }

    public static void apply(Level world, ItemStack wand, Player player, BlockPos pos, BlockState block) {
        SymmetryWandItem.checkComponents(wand);
        if (!SymmetryWandItem.isEnabled(wand)) {
            return;
        }
        if (!BlockItem.BY_BLOCK.containsKey(block.getBlock())) {
            return;
        }
        HashMap<BlockPos, BlockState> blockSet = new HashMap<BlockPos, BlockState>();
        blockSet.put(pos, block);
        SymmetryMirror symmetry = (SymmetryMirror)wand.get(AllDataComponents.SYMMETRY_WAND);
        Vec3 mirrorPos = symmetry.getPosition();
        if (mirrorPos.distanceTo(Vec3.atLowerCornerOf((Vec3i)pos)) > (double)((Integer)AllConfigs.server().equipment.maxSymmetryWandRange.get()).intValue()) {
            return;
        }
        if (!player.isCreative() && SymmetryWandItem.isHoldingBlock(player, block) && BlockHelper.findAndRemoveInInventory(block, player, 1) == 0) {
            return;
        }
        symmetry.process(blockSet);
        BlockPos to = BlockPos.containing((Position)mirrorPos);
        ArrayList<BlockPos> targets = new ArrayList<BlockPos>();
        targets.add(pos);
        for (BlockPos position : blockSet.keySet()) {
            if (position.equals((Object)pos) || !world.isUnobstructed(block, position, CollisionContext.of((Entity)player))) continue;
            BlockState blockState = (BlockState)blockSet.get(position);
            for (Direction face : Iterate.directions) {
                blockState = blockState.updateShape(face, world.getBlockState(position.relative(face)), (LevelAccessor)world, position, position.relative(face));
            }
            if (player.isCreative()) {
                world.setBlockAndUpdate(position, blockState);
                targets.add(position);
                continue;
            }
            BlockState toReplace = world.getBlockState(position);
            if (!toReplace.canBeReplaced() || toReplace.getDestroySpeed((BlockGetter)world, position) == -1.0f) continue;
            if (AllBlocks.CART_ASSEMBLER.has(blockState)) {
                BlockState railBlock = CartAssemblerBlock.getRailBlock(blockState);
                if (BlockHelper.findAndRemoveInInventory(railBlock, player, 1) == 0) continue;
                if (BlockHelper.findAndRemoveInInventory(blockState, player, 1) == 0) {
                    blockState = railBlock;
                }
            } else if (BlockHelper.findAndRemoveInInventory(blockState, player, 1) == 0) continue;
            BlockSnapshot blocksnapshot = BlockSnapshot.create((ResourceKey)world.dimension(), (LevelAccessor)world, (BlockPos)position);
            FluidState ifluidstate = world.getFluidState(position);
            world.setBlock(position, ifluidstate.createLegacyBlock(), 16);
            world.setBlockAndUpdate(position, blockState);
            wand.set(AllDataComponents.SYMMETRY_WAND_SIMULATE, (Object)true);
            boolean placeInterrupted = EventHooks.onBlockPlace((Entity)player, (BlockSnapshot)blocksnapshot, (Direction)Direction.UP);
            wand.set(AllDataComponents.SYMMETRY_WAND_SIMULATE, (Object)false);
            if (placeInterrupted) {
                blocksnapshot.restore(2);
                continue;
            }
            targets.add(position);
        }
        CatnipServices.NETWORK.sendToClientsTrackingAndSelf((Entity)player, (CustomPacketPayload)new SymmetryEffectPacket(to, targets));
    }

    private static boolean isHoldingBlock(Player player, BlockState block) {
        ItemStack itemBlock = BlockHelper.getRequiredItem(block);
        return player.isHolding(itemBlock.getItem());
    }

    public static void remove(Level world, ItemStack wand, Player player, BlockPos pos) {
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState ogBlock = world.getBlockState(pos);
        SymmetryWandItem.checkComponents(wand);
        if (!SymmetryWandItem.isEnabled(wand)) {
            return;
        }
        HashMap<BlockPos, BlockState> blockSet = new HashMap<BlockPos, BlockState>();
        blockSet.put(pos, air);
        SymmetryMirror symmetry = (SymmetryMirror)wand.get(AllDataComponents.SYMMETRY_WAND);
        Vec3 mirrorPos = symmetry.getPosition();
        if (mirrorPos.distanceTo(Vec3.atLowerCornerOf((Vec3i)pos)) > (double)((Integer)AllConfigs.server().equipment.maxSymmetryWandRange.get()).intValue()) {
            return;
        }
        symmetry.process(blockSet);
        BlockPos to = BlockPos.containing((Position)mirrorPos);
        ArrayList<BlockPos> targets = new ArrayList<BlockPos>();
        targets.add(pos);
        for (BlockPos position : blockSet.keySet()) {
            BlockState blockstate;
            if (!player.isCreative() && ogBlock.getBlock() != world.getBlockState(position).getBlock() || position.equals((Object)pos) || (blockstate = world.getBlockState(position)).isAir()) continue;
            targets.add(position);
            world.levelEvent(2001, position, Block.getId((BlockState)blockstate));
            world.setBlock(position, air, 3);
            if (player.isCreative()) continue;
            if (!player.getMainHandItem().isEmpty()) {
                player.getMainHandItem().mineBlock(world, blockstate, position, player);
            }
            BlockEntity blockEntity = blockstate.hasBlockEntity() ? world.getBlockEntity(position) : null;
            Block.dropResources((BlockState)blockstate, (Level)world, (BlockPos)pos, (BlockEntity)blockEntity, (Entity)player, (ItemStack)player.getMainHandItem());
        }
        CatnipServices.NETWORK.sendToClientsTrackingAndSelf((Entity)player, (CustomPacketPayload)new SymmetryEffectPacket(to, targets));
    }

    public static boolean presentInHotbar(Player player) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < Inventory.getSelectionSize(); ++i) {
            if (!AllItems.WAND_OF_SYMMETRY.isIn(inv.getItem(i))) continue;
            return true;
        }
        return false;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SymmetryWandItemRenderer()));
    }
}

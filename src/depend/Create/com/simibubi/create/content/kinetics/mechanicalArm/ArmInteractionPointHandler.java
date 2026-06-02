/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmPlacementPacket;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(value={Dist.CLIENT})
public class ArmInteractionPointHandler {
    static List<ArmInteractionPoint> currentSelection = new ArrayList<ArmInteractionPoint>();
    static ItemStack currentItem;
    static long lastBlockPos;

    @SubscribeEvent
    public static void rightClickingBlocksSelectsThem(PlayerInteractEvent.RightClickBlock event) {
        if (currentItem == null) {
            return;
        }
        BlockPos pos = event.getPos();
        Level world = event.getLevel();
        if (!world.isClientSide) {
            return;
        }
        Player player = event.getEntity();
        if (player != null && player.isSpectator()) {
            return;
        }
        ArmInteractionPoint selected = ArmInteractionPointHandler.getSelected(pos);
        BlockState state = world.getBlockState(pos);
        if (selected == null) {
            ArmInteractionPoint point = ArmInteractionPoint.create(world, pos, state);
            if (point == null) {
                return;
            }
            selected = point;
            ArmInteractionPointHandler.put(point);
        }
        selected.cycleMode();
        if (player != null) {
            ArmInteractionPoint.Mode mode = selected.getMode();
            CreateLang.builder().translate(mode.getTranslationKey(), new Object[]{CreateLang.blockName(state).style(ChatFormatting.WHITE)}).color(mode.getColor()).sendStatus(player);
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void leftClickingBlocksDeselectsThem(PlayerInteractEvent.LeftClickBlock event) {
        if (currentItem == null) {
            return;
        }
        if (!event.getLevel().isClientSide) {
            return;
        }
        BlockPos pos = event.getPos();
        if (ArmInteractionPointHandler.remove(pos) != null) {
            event.setCanceled(true);
        }
    }

    public static void flushSettings(BlockPos pos) {
        if (currentSelection == null) {
            return;
        }
        int removed = 0;
        Iterator<ArmInteractionPoint> iterator = currentSelection.iterator();
        while (iterator.hasNext()) {
            ArmInteractionPoint point = iterator.next();
            if (point.getPos().closerThan((Vec3i)pos, (double)ArmBlockEntity.getRange())) continue;
            iterator.remove();
            ++removed;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (removed > 0) {
            CreateLang.builder().translate("mechanical_arm.points_outside_range", new Object[]{removed}).style(ChatFormatting.RED).sendStatus((Player)player);
        } else {
            int inputs = 0;
            int outputs = 0;
            for (ArmInteractionPoint armInteractionPoint : currentSelection) {
                if (armInteractionPoint.getMode() == ArmInteractionPoint.Mode.DEPOSIT) {
                    ++outputs;
                    continue;
                }
                ++inputs;
            }
            if (inputs + outputs > 0) {
                CreateLang.builder().translate("mechanical_arm.summary", new Object[]{inputs, outputs}).style(ChatFormatting.WHITE).sendStatus((Player)player);
            }
        }
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ArmPlacementPacket(currentSelection, pos));
        currentSelection.clear();
        currentItem = null;
    }

    public static void tick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack heldItemMainhand = player.getMainHandItem();
        if (!AllBlocks.MECHANICAL_ARM.isIn(heldItemMainhand)) {
            currentItem = null;
        } else {
            if (heldItemMainhand != currentItem) {
                currentSelection.clear();
                currentItem = heldItemMainhand;
            }
            ArmInteractionPointHandler.drawOutlines(currentSelection);
        }
        ArmInteractionPointHandler.checkForWrench(heldItemMainhand);
    }

    private static void checkForWrench(ItemStack heldItem) {
        if (!AllItems.WRENCH.isIn(heldItem)) {
            return;
        }
        HitResult objectMouseOver = Minecraft.getInstance().hitResult;
        if (!(objectMouseOver instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult result = (BlockHitResult)objectMouseOver;
        BlockPos pos = result.getBlockPos();
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
        if (!(be instanceof ArmBlockEntity)) {
            lastBlockPos = -1L;
            currentSelection.clear();
            return;
        }
        if (lastBlockPos == -1L || lastBlockPos != pos.asLong()) {
            currentSelection.clear();
            ArmBlockEntity arm = (ArmBlockEntity)be;
            arm.inputs.forEach(ArmInteractionPointHandler::put);
            arm.outputs.forEach(ArmInteractionPointHandler::put);
            lastBlockPos = pos.asLong();
        }
        if (lastBlockPos != -1L) {
            ArmInteractionPointHandler.drawOutlines(currentSelection);
        }
    }

    private static void drawOutlines(Collection<ArmInteractionPoint> selection) {
        Iterator<ArmInteractionPoint> iterator = selection.iterator();
        while (iterator.hasNext()) {
            BlockPos pos;
            ArmInteractionPoint point = iterator.next();
            if (!point.isValid()) {
                iterator.remove();
                continue;
            }
            Level level = point.getLevel();
            BlockState state = level.getBlockState(pos = point.getPos());
            VoxelShape shape = state.getShape((BlockGetter)level, pos);
            if (shape.isEmpty()) continue;
            int color = point.getMode().getColor();
            Outliner.getInstance().showAABB((Object)point, shape.bounds().move(pos)).colored(color).lineWidth(0.0625f);
        }
    }

    private static void put(ArmInteractionPoint point) {
        currentSelection.add(point);
    }

    private static ArmInteractionPoint remove(BlockPos pos) {
        ArmInteractionPoint result = ArmInteractionPointHandler.getSelected(pos);
        if (result != null) {
            currentSelection.remove(result);
        }
        return result;
    }

    private static ArmInteractionPoint getSelected(BlockPos pos) {
        for (ArmInteractionPoint point : currentSelection) {
            if (!point.getPos().equals((Object)pos)) continue;
            return point;
        }
        return null;
    }

    static {
        lastBlockPos = -1L;
    }
}

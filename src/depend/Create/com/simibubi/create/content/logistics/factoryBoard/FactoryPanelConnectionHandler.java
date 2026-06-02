/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionPacket;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSlotPositioning;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FactoryPanelConnectionHandler {
    static FactoryPanelPosition connectingFrom;
    static AABB connectingFromBox;
    static boolean relocating;
    static FactoryPanelPosition validRelocationTarget;

    public static boolean panelClicked(LevelAccessor level, Player player, FactoryPanelBehaviour panel) {
        if (connectingFrom == null) {
            return false;
        }
        FactoryPanelBehaviour at = FactoryPanelBehaviour.at((BlockAndTintGetter)level, connectingFrom);
        if (panel.getPanelPosition().equals(connectingFrom) || at == null) {
            player.displayClientMessage((Component)Component.empty(), true);
            connectingFrom = null;
            connectingFromBox = null;
            return true;
        }
        String checkForIssues = FactoryPanelConnectionHandler.checkForIssues(at, panel);
        if (checkForIssues != null) {
            player.displayClientMessage((Component)CreateLang.translate(checkForIssues, new Object[0]).style(ChatFormatting.RED).component(), true);
            connectingFrom = null;
            connectingFromBox = null;
            AllSoundEvents.DENY.playAt(player.level(), (Vec3i)player.blockPosition(), 1.0f, 1.0f, false);
            return true;
        }
        ItemStack filterFrom = panel.getFilter();
        ItemStack filterTo = at.getFilter();
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new FactoryPanelConnectionPacket(panel.getPanelPosition(), connectingFrom, false));
        player.displayClientMessage((Component)CreateLang.translate("factory_panel.panels_connected", filterFrom.getHoverName().getString(), filterTo.getHoverName().getString()).style(ChatFormatting.GREEN).component(), true);
        connectingFrom = null;
        connectingFromBox = null;
        player.level().playLocalSound(player.blockPosition(), SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 0.5f, 0.5f, false);
        return true;
    }

    @Nullable
    private static String checkForIssues(FactoryPanelBehaviour from, FactoryPanelBehaviour to) {
        if (from == null) {
            return "factory_panel.connection_aborted";
        }
        if (from.targetedBy.containsKey(to.getPanelPosition())) {
            return "factory_panel.already_connected";
        }
        if (from.targetedBy.size() >= 9) {
            return "factory_panel.cannot_add_more_inputs";
        }
        BlockState state1 = to.blockEntity.getBlockState();
        BlockState state2 = from.blockEntity.getBlockState();
        BlockPos diff = to.getPos().subtract((Vec3i)from.getPos());
        if (((BlockState)state1.setValue((Property)FactoryPanelBlock.WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue((Property)FactoryPanelBlock.POWERED, (Comparable)Boolean.valueOf(false)) != ((BlockState)state2.setValue((Property)FactoryPanelBlock.WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue((Property)FactoryPanelBlock.POWERED, (Comparable)Boolean.valueOf(false))) {
            return "factory_panel.same_orientation";
        }
        if (FactoryPanelBlock.connectedDirection(state1).getAxis().choose(diff.getX(), diff.getY(), diff.getZ()) != 0) {
            return "factory_panel.same_surface";
        }
        if (!diff.closerThan((Vec3i)BlockPos.ZERO, 16.0)) {
            return "factory_panel.too_far_apart";
        }
        if (to.panelBE().restocker) {
            return "factory_panel.input_in_restock_mode";
        }
        if (to.getFilter().isEmpty() || from.getFilter().isEmpty()) {
            return "factory_panel.no_item";
        }
        return null;
    }

    @Nullable
    private static String checkForIssues(FactoryPanelBehaviour from, FactoryPanelSupportBehaviour to) {
        if (from == null) {
            return "factory_panel.connection_aborted";
        }
        BlockState state1 = from.blockEntity.getBlockState();
        BlockState state2 = to.blockEntity.getBlockState();
        BlockPos diff = to.getPos().subtract((Vec3i)from.getPos());
        Direction connectedDirection = FactoryPanelBlock.connectedDirection(state1);
        if (connectedDirection != state2.getOptionalValue((Property)WrenchableDirectionalBlock.FACING).orElse(connectedDirection)) {
            return "factory_panel.same_orientation";
        }
        if (connectedDirection.getAxis().choose(diff.getX(), diff.getY(), diff.getZ()) != 0) {
            return "factory_panel.same_surface";
        }
        if (!diff.closerThan((Vec3i)BlockPos.ZERO, 16.0)) {
            return "factory_panel.too_far_apart";
        }
        return null;
    }

    public static void clientTick() {
        BlockHitResult bhr;
        if (connectingFrom == null || connectingFromBox == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        FactoryPanelBehaviour at = FactoryPanelBehaviour.at((BlockAndTintGetter)mc.level, connectingFrom);
        if (!connectingFrom.pos().closerThan((Vec3i)mc.player.blockPosition(), 16.0) || at == null) {
            connectingFrom = null;
            connectingFromBox = null;
            mc.player.displayClientMessage((Component)Component.empty(), true);
            return;
        }
        Outliner.getInstance().showAABB((Object)connectingFrom, connectingFromBox).colored(AnimationTickHolder.getTicks() % 16 > 8 ? 3716964 : 11006064).lineWidth(0.0625f);
        mc.player.displayClientMessage((Component)CreateLang.translate(relocating ? "factory_panel.click_to_relocate" : "factory_panel.click_second_panel", new Object[0]).component(), true);
        if (!relocating) {
            return;
        }
        validRelocationTarget = null;
        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult) || (bhr = (BlockHitResult)hitResult).getType() == HitResult.Type.MISS) {
            return;
        }
        Vec3 offsetPos = bhr.getLocation().add(Vec3.atLowerCornerOf((Vec3i)bhr.getDirection().getNormal()).scale(0.03125));
        BlockPos pos = BlockPos.containing((Position)offsetPos);
        BlockState blockState = at.blockEntity.getBlockState();
        FactoryPanelBlock.PanelSlot slot = FactoryPanelBlock.getTargetedSlot(pos, blockState, offsetPos);
        BlockPos diff = pos.subtract((Vec3i)connectingFrom.pos());
        Direction facing = FactoryPanelBlock.connectedDirection(blockState);
        if (facing.getAxis().choose(diff.getX(), diff.getY(), diff.getZ()) != 0) {
            return;
        }
        if (!((FactoryPanelBlock)AllBlocks.FACTORY_GAUGE.get()).canSurvive(blockState, (LevelReader)mc.level, pos)) {
            return;
        }
        if (AllBlocks.PACKAGER.has(mc.level.getBlockState(pos.relative(facing.getOpposite())))) {
            return;
        }
        validRelocationTarget = new FactoryPanelPosition(pos, slot);
        Outliner.getInstance().showAABB((Object)"target", FactoryPanelConnectionHandler.getBB(blockState, validRelocationTarget)).colored(0xEEEEEE).disableLineNormals().lineWidth(0.0625f);
    }

    public static boolean onRightClick() {
        BlockHitResult bhr;
        if (connectingFrom == null || connectingFromBox == null) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        boolean missed = false;
        if (relocating) {
            if (mc.player.isShiftKeyDown()) {
                validRelocationTarget = null;
            }
            if (validRelocationTarget != null) {
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new FactoryPanelConnectionPacket(validRelocationTarget, connectingFrom, true));
            }
            connectingFrom = null;
            connectingFromBox = null;
            if (validRelocationTarget == null) {
                mc.player.displayClientMessage((Component)CreateLang.translate("factory_panel.relocation_aborted", new Object[0]).component(), true);
            }
            relocating = false;
            validRelocationTarget = null;
            return true;
        }
        HitResult hitResult = mc.hitResult;
        if (hitResult instanceof BlockHitResult && (bhr = (BlockHitResult)hitResult).getType() != HitResult.Type.MISS) {
            BlockEntity blockEntity = mc.level.getBlockEntity(bhr.getBlockPos());
            FactoryPanelSupportBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)mc.level, bhr.getBlockPos(), FactoryPanelSupportBehaviour.TYPE);
            if (behaviour != null) {
                FactoryPanelBehaviour at = FactoryPanelBehaviour.at((BlockAndTintGetter)mc.level, connectingFrom);
                String checkForIssues = FactoryPanelConnectionHandler.checkForIssues(at, behaviour);
                if (checkForIssues != null) {
                    mc.player.displayClientMessage((Component)CreateLang.translate(checkForIssues, new Object[0]).style(ChatFormatting.RED).component(), true);
                    connectingFrom = null;
                    connectingFromBox = null;
                    AllSoundEvents.DENY.playAt((Level)mc.level, (Vec3i)mc.player.blockPosition(), 1.0f, 1.0f, false);
                    return true;
                }
                FactoryPanelPosition bestPosition = null;
                double bestDistance = Double.POSITIVE_INFINITY;
                for (FactoryPanelBlock.PanelSlot slot : FactoryPanelBlock.PanelSlot.values()) {
                    FactoryPanelPosition panelPosition = new FactoryPanelPosition(blockEntity.getBlockPos(), slot);
                    FactoryPanelConnection connection = new FactoryPanelConnection(panelPosition, 1);
                    Vec3 diff = connection.calculatePathDiff(mc.level.getBlockState(connectingFrom.pos()), connectingFrom);
                    if (bestDistance < diff.lengthSqr()) continue;
                    bestDistance = diff.lengthSqr();
                    bestPosition = panelPosition;
                }
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new FactoryPanelConnectionPacket(bestPosition, connectingFrom, false));
                mc.player.displayClientMessage((Component)CreateLang.translate("factory_panel.link_connected", blockEntity.getBlockState().getBlock().getName()).style(ChatFormatting.GREEN).component(), true);
                connectingFrom = null;
                connectingFromBox = null;
                mc.player.level().playLocalSound(mc.player.blockPosition(), SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 0.5f, 0.5f, false);
                return true;
            }
            if (!(blockEntity instanceof FactoryPanelBlockEntity)) {
                missed = true;
            }
        }
        if (!mc.player.isShiftKeyDown() && !missed) {
            return false;
        }
        connectingFrom = null;
        connectingFromBox = null;
        mc.player.displayClientMessage((Component)CreateLang.translate("factory_panel.connection_aborted", new Object[0]).component(), true);
        return true;
    }

    public static void startRelocating(FactoryPanelBehaviour behaviour) {
        FactoryPanelConnectionHandler.startConnection(behaviour);
        relocating = true;
    }

    public static void startConnection(FactoryPanelBehaviour behaviour) {
        relocating = false;
        connectingFrom = behaviour.getPanelPosition();
        connectingFromBox = FactoryPanelConnectionHandler.getBB(behaviour.blockEntity.getBlockState(), connectingFrom);
    }

    public static AABB getBB(BlockState blockState, FactoryPanelPosition factoryPanelPosition) {
        Vec3 location = FactoryPanelSlotPositioning.getCenterOfSlot(blockState, factoryPanelPosition.slot()).add(Vec3.atLowerCornerOf((Vec3i)factoryPanelPosition.pos()));
        Vec3 plane = VecHelper.axisAlingedPlaneOf((Direction)FactoryPanelBlock.connectedDirection(blockState));
        return new AABB(location, location).inflate(plane.x * 3.0 / 16.0, plane.y * 3.0 / 16.0, plane.z * 3.0 / 16.0);
    }
}

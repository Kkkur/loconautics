/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.blueprint.BlueprintOverlayRenderer;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlock;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionPacket;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(value={Dist.CLIENT})
public class ChainConveyorConnectionHandler {
    private static BlockPos firstPos;
    private static ResourceKey<Level> firstDim;

    public static boolean onRightClick() {
        BlockHitResult bhr;
        Minecraft mc = Minecraft.getInstance();
        if (!ChainConveyorConnectionHandler.isChain(mc.player.getMainHandItem())) {
            return false;
        }
        if (firstPos == null) {
            return false;
        }
        boolean missed = false;
        HitResult hitResult = mc.hitResult;
        if (hitResult instanceof BlockHitResult && (bhr = (BlockHitResult)hitResult).getType() != HitResult.Type.MISS && !(mc.level.getBlockEntity(bhr.getBlockPos()) instanceof ChainConveyorBlockEntity)) {
            missed = true;
        }
        if (!mc.player.isShiftKeyDown() && !missed) {
            return false;
        }
        firstPos = null;
        CreateLang.translate("chain_conveyor.selection_cleared", new Object[0]).sendStatus((Player)mc.player);
        return true;
    }

    @SubscribeEvent
    public static void onItemUsedOnBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        Player player = event.getEntity();
        BlockState blockState = level.getBlockState(pos);
        if (!AllBlocks.CHAIN_CONVEYOR.has(blockState)) {
            return;
        }
        if (!ChainConveyorConnectionHandler.isChain(itemStack)) {
            return;
        }
        if (!player.mayBuild() || player instanceof FakePlayer) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.CONSUME);
        if (!level.isClientSide()) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChainConveyorBlockEntity) {
            ChainConveyorBlockEntity ccbe = (ChainConveyorBlockEntity)blockEntity;
            if (ccbe.connections.size() >= (Integer)AllConfigs.server().kinetics.maxChainConveyorConnections.get()) {
                CreateLang.translate("chain_conveyor.cannot_add_more_connections", new Object[0]).style(ChatFormatting.RED).sendStatus(player);
                return;
            }
        }
        if (firstPos == null || firstDim != level.dimension()) {
            firstPos = pos;
            firstDim = level.dimension();
            player.swing(event.getHand());
            return;
        }
        boolean success = ChainConveyorConnectionHandler.validateAndConnect((LevelAccessor)level, pos, player, itemStack, false);
        firstPos = null;
        if (!success) {
            AllSoundEvents.DENY.play(level, player, (Vec3i)pos);
            return;
        }
        SoundType soundtype = Blocks.CHAIN.defaultBlockState().getSoundType();
        if (soundtype != null) {
            level.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
        }
    }

    private static boolean isChain(ItemStack itemStack) {
        return itemStack.is(Items.CHAIN);
    }

    public static void clientTick() {
        if (firstPos == null) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        BlockEntity sourceLift = player.level().getBlockEntity(firstPos);
        if (firstDim != player.level().dimension() || !(sourceLift instanceof ChainConveyorBlockEntity)) {
            firstPos = null;
            CreateLang.translate("chain_conveyor.selection_cleared", new Object[0]).sendStatus((Player)player);
            return;
        }
        ItemStack stack = player.getMainHandItem();
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (!ChainConveyorConnectionHandler.isChain(stack) && !ChainConveyorConnectionHandler.isChain(stack = player.getOffhandItem())) {
            return;
        }
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            ChainConveyorConnectionHandler.highlightConveyor(firstPos, 0xFFFFFF, "chain_connect");
            return;
        }
        Level level = player.level();
        BlockHitResult bhr = (BlockHitResult)hitResult;
        BlockPos pos = bhr.getBlockPos();
        BlockState hitState = level.getBlockState(pos);
        if (pos.equals((Object)firstPos)) {
            ChainConveyorConnectionHandler.highlightConveyor(firstPos, 0xFFFFFF, "chain_connect");
            CreateLang.translate("chain_conveyor.select_second", new Object[0]).sendStatus((Player)player);
            return;
        }
        if (!(hitState.getBlock() instanceof ChainConveyorBlock)) {
            ChainConveyorConnectionHandler.highlightConveyor(firstPos, 0xFFFFFF, "chain_connect");
            return;
        }
        boolean success = ChainConveyorConnectionHandler.validateAndConnect((LevelAccessor)level, pos, (Player)player, stack, true);
        if (success) {
            CreateLang.translate("chain_conveyor.valid_connection", new Object[0]).style(ChatFormatting.GREEN).sendStatus((Player)player);
        }
        int color = success ? 9817409 : 15359019;
        ChainConveyorConnectionHandler.highlightConveyor(firstPos, color, "chain_connect");
        ChainConveyorConnectionHandler.highlightConveyor(pos, color, "chain_connect_to");
        Vec3 from = Vec3.atCenterOf((Vec3i)pos);
        Vec3 to = Vec3.atCenterOf((Vec3i)firstPos);
        Vec3 diff = from.subtract(to);
        if (diff.length() < 1.0) {
            return;
        }
        from = from.subtract(diff.normalize().scale(0.5));
        to = to.add(diff.normalize().scale(0.5));
        Vec3 normal = diff.cross(new Vec3(0.0, 1.0, 0.0)).normalize().scale(0.875);
        Outliner.getInstance().showLine((Object)"chain_connect_line", from.add(normal), to.add(normal)).lineWidth(0.0625f).colored(color);
        Outliner.getInstance().showLine((Object)"chain_connect_line_1", from.subtract(normal), to.subtract(normal)).lineWidth(0.0625f).colored(color);
    }

    private static void highlightConveyor(BlockPos pos, int color, String key) {
        for (int y : Iterate.zeroAndOne) {
            Vec3 prevV = VecHelper.rotate((Vec3)new Vec3(0.0, 0.125 + (double)y * 0.75, 1.25), (double)-22.5, (Direction.Axis)Direction.Axis.Y).add(Vec3.atBottomCenterOf((Vec3i)pos));
            for (int i = 0; i < 8; ++i) {
                Vec3 v = VecHelper.rotate((Vec3)new Vec3(0.0, 0.125 + (double)y * 0.75, 1.25), (double)(22.5 + (double)(i * 45)), (Direction.Axis)Direction.Axis.Y).add(Vec3.atBottomCenterOf((Vec3i)pos));
                Outliner.getInstance().showLine((Object)(key + y + i), prevV, v).lineWidth(0.0625f).colored(color);
                prevV = v;
            }
        }
    }

    public static boolean validateAndConnect(LevelAccessor level, BlockPos pos, Player player, ItemStack chain, boolean simulate) {
        if (!simulate && player.isShiftKeyDown()) {
            CreateLang.translate("chain_conveyor.selection_cleared", new Object[0]).sendStatus(player);
            return false;
        }
        if (pos.equals((Object)firstPos)) {
            return false;
        }
        if (!pos.closerThan((Vec3i)firstPos, (double)((Integer)AllConfigs.server().kinetics.maxChainConveyorLength.get()).intValue())) {
            return ChainConveyorConnectionHandler.fail("chain_conveyor.too_far");
        }
        if (pos.closerThan((Vec3i)firstPos, 2.5)) {
            return ChainConveyorConnectionHandler.fail("chain_conveyor.too_close");
        }
        Vec3 diff = Vec3.atLowerCornerOf((Vec3i)pos.subtract((Vec3i)firstPos));
        double horizontalDistance = diff.multiply(1.0, 0.0, 1.0).length() - 1.5;
        if (horizontalDistance <= 0.0) {
            return ChainConveyorConnectionHandler.fail("chain_conveyor.cannot_connect_vertically");
        }
        if (Math.abs(diff.y) / horizontalDistance > 1.0) {
            return ChainConveyorConnectionHandler.fail("chain_conveyor.too_steep");
        }
        ChainConveyorBlock chainConveyorBlock = (ChainConveyorBlock)AllBlocks.CHAIN_CONVEYOR.get();
        ChainConveyorBlockEntity sourceLift = (ChainConveyorBlockEntity)chainConveyorBlock.getBlockEntity((BlockGetter)level, firstPos);
        ChainConveyorBlockEntity targetLift = (ChainConveyorBlockEntity)chainConveyorBlock.getBlockEntity((BlockGetter)level, pos);
        if (targetLift.connections.size() >= (Integer)AllConfigs.server().kinetics.maxChainConveyorConnections.get()) {
            return ChainConveyorConnectionHandler.fail("chain_conveyor.cannot_add_more_connections");
        }
        if (targetLift.connections.contains(firstPos.subtract((Vec3i)pos))) {
            return ChainConveyorConnectionHandler.fail("chain_conveyor.already_connected");
        }
        if (sourceLift == null || targetLift == null) {
            return ChainConveyorConnectionHandler.fail("chain_conveyor.blocks_invalid");
        }
        if (!player.isCreative()) {
            int chainCost = ChainConveyorBlockEntity.getChainCost(pos.subtract((Vec3i)firstPos));
            boolean hasEnough = ChainConveyorBlockEntity.getChainsFromInventory(player, chain, chainCost, true);
            if (simulate) {
                BlueprintOverlayRenderer.displayChainRequirements(chain.getItem(), chainCost, hasEnough);
            }
            if (!hasEnough) {
                return ChainConveyorConnectionHandler.fail("chain_conveyor.not_enough_chains");
            }
        }
        if (simulate) {
            return true;
        }
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ChainConveyorConnectionPacket(firstPos, pos, chain, true));
        CreateLang.text("").sendStatus(player);
        firstPos = null;
        firstDim = null;
        return true;
    }

    private static boolean fail(String message) {
        CreateLang.translate(message, new Object[0]).style(ChatFormatting.RED).sendStatus((Player)Minecraft.getInstance().player);
        return false;
    }
}

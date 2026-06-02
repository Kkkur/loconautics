/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.logistics.depot;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;
import com.simibubi.create.content.logistics.depot.EjectorPlacementPacket;
import com.simibubi.create.content.logistics.depot.EntityLauncher;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.joml.Vector3f;

@EventBusSubscriber(value={Dist.CLIENT})
public class EjectorTargetHandler {
    static BlockPos currentSelection;
    static ItemStack currentItem;
    static long lastHoveredBlockPos;
    static EntityLauncher launcher;

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
        if (player == null || player.isSpectator() || !player.isShiftKeyDown()) {
            return;
        }
        String key = "weighted_ejector.target_set";
        ChatFormatting colour = ChatFormatting.GOLD;
        player.displayClientMessage((Component)CreateLang.translateDirect(key, new Object[0]).withStyle(colour), true);
        currentSelection = pos;
        launcher = null;
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
        if (!event.getEntity().isShiftKeyDown()) {
            return;
        }
        BlockPos pos = event.getPos();
        if (pos.equals((Object)currentSelection)) {
            currentSelection = null;
            launcher = null;
            event.setCanceled(true);
        }
    }

    public static void flushSettings(BlockPos pos) {
        Direction validTargetDirection;
        int h = 0;
        int v = 0;
        LocalPlayer player = Minecraft.getInstance().player;
        String key = "weighted_ejector.target_not_valid";
        ChatFormatting colour = ChatFormatting.WHITE;
        if (currentSelection == null) {
            key = "weighted_ejector.no_target";
        }
        if ((validTargetDirection = EjectorTargetHandler.getValidTargetDirection(pos)) == null) {
            player.displayClientMessage((Component)CreateLang.translateDirect(key, new Object[0]).withStyle(colour), true);
            currentItem = null;
            currentSelection = null;
            return;
        }
        key = "weighted_ejector.targeting";
        colour = ChatFormatting.GREEN;
        player.displayClientMessage((Component)CreateLang.translateDirect(key, currentSelection.getX(), currentSelection.getY(), currentSelection.getZ()).withStyle(colour), true);
        BlockPos diff = pos.subtract((Vec3i)currentSelection);
        h = Math.abs(diff.getX() + diff.getZ());
        v = -diff.getY();
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new EjectorPlacementPacket(h, v, pos, validTargetDirection));
        currentSelection = null;
        currentItem = null;
    }

    public static Direction getValidTargetDirection(BlockPos pos) {
        if (currentSelection == null) {
            return null;
        }
        if (VecHelper.onSameAxis((BlockPos)pos, (BlockPos)currentSelection, (Direction.Axis)Direction.Axis.Y)) {
            return null;
        }
        int xDiff = currentSelection.getX() - pos.getX();
        int zDiff = currentSelection.getZ() - pos.getZ();
        int max = (Integer)AllConfigs.server().kinetics.maxEjectorDistance.get();
        if (Math.abs(xDiff) > max || Math.abs(zDiff) > max) {
            return null;
        }
        if (xDiff == 0) {
            return Direction.get((Direction.AxisDirection)(zDiff < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE), (Direction.Axis)Direction.Axis.Z);
        }
        if (zDiff == 0) {
            return Direction.get((Direction.AxisDirection)(xDiff < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE), (Direction.Axis)Direction.Axis.X);
        }
        return null;
    }

    public static void tick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack heldItemMainhand = player.getMainHandItem();
        if (!AllBlocks.WEIGHTED_EJECTOR.isIn(heldItemMainhand)) {
            currentItem = null;
        } else {
            if (heldItemMainhand != currentItem) {
                currentSelection = null;
                currentItem = heldItemMainhand;
            }
            EjectorTargetHandler.drawOutline(currentSelection);
        }
        EjectorTargetHandler.checkForWrench(heldItemMainhand);
        EjectorTargetHandler.drawArc();
    }

    protected static void drawArc() {
        int validZ;
        Minecraft mc = Minecraft.getInstance();
        boolean wrench = AllItems.WRENCH.isIn(mc.player.getMainHandItem());
        if (currentSelection == null) {
            return;
        }
        if (currentItem == null && !wrench) {
            return;
        }
        HitResult objectMouseOver = mc.hitResult;
        if (!(objectMouseOver instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult blockRayTraceResult = (BlockHitResult)objectMouseOver;
        if (blockRayTraceResult.getType() == HitResult.Type.MISS) {
            return;
        }
        BlockPos pos = blockRayTraceResult.getBlockPos();
        if (!wrench) {
            pos = pos.relative(blockRayTraceResult.getDirection());
        }
        int xDiff = currentSelection.getX() - pos.getX();
        int yDiff = currentSelection.getY() - pos.getY();
        int zDiff = currentSelection.getZ() - pos.getZ();
        int validX = Math.abs(zDiff) > Math.abs(xDiff) ? 0 : xDiff;
        BlockPos validPos = currentSelection.offset(validX, yDiff, validZ = Math.abs(zDiff) < Math.abs(xDiff) ? 0 : zDiff);
        Direction d = EjectorTargetHandler.getValidTargetDirection(validPos);
        if (d == null) {
            return;
        }
        if (launcher == null || lastHoveredBlockPos != pos.asLong()) {
            lastHoveredBlockPos = pos.asLong();
            launcher = new EntityLauncher(Math.abs(validX + validZ), yDiff);
        }
        double totalFlyingTicks = launcher.getTotalFlyingTicks() + 3.0;
        int segments = (int)totalFlyingTicks / 3 + 1;
        double tickOffset = totalFlyingTicks / (double)segments;
        boolean valid = xDiff == validX && zDiff == validZ;
        int intColor = valid ? 10411635 : 0xFF7171;
        Vector3f color = new Color(intColor).asVectorF();
        DustParticleOptions data = new DustParticleOptions(color, 1.0f);
        ClientLevel world = mc.level;
        AABB bb = new AABB(0.0, 0.0, 0.0, 1.0, 0.0, 1.0).move(currentSelection.offset(-validX, -yDiff, -validZ));
        Outliner.getInstance().chaseAABB((Object)"valid", bb).colored(intColor).lineWidth(0.0625f);
        for (int i = 0; i < segments; ++i) {
            double ticks = (double)(AnimationTickHolder.getRenderTime() / 3.0f) % tickOffset + (double)i * tickOffset;
            Vec3 vec = launcher.getGlobalPos(ticks, d, pos).add((double)(xDiff - validX), 0.0, (double)(zDiff - validZ));
            world.addParticle((ParticleOptions)data, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
        }
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
        if (!(be instanceof EjectorBlockEntity)) {
            lastHoveredBlockPos = -1L;
            currentSelection = null;
            return;
        }
        if (lastHoveredBlockPos == -1L || lastHoveredBlockPos != pos.asLong()) {
            EjectorBlockEntity ejector = (EjectorBlockEntity)be;
            if (!ejector.getTargetPosition().equals((Object)ejector.getBlockPos())) {
                currentSelection = ejector.getTargetPosition();
            }
            lastHoveredBlockPos = pos.asLong();
            launcher = null;
        }
        if (lastHoveredBlockPos != -1L) {
            EjectorTargetHandler.drawOutline(currentSelection);
        }
    }

    public static void drawOutline(BlockPos selection) {
        ClientLevel world = Minecraft.getInstance().level;
        if (selection == null) {
            return;
        }
        BlockPos pos = selection;
        BlockState state = world.getBlockState(pos);
        VoxelShape shape = state.getShape((BlockGetter)world, pos);
        AABB boundingBox = shape.isEmpty() ? new AABB(BlockPos.ZERO) : shape.bounds();
        Outliner.getInstance().showAABB((Object)"target", boundingBox.move(pos)).colored(16763764).lineWidth(0.0625f);
    }

    static {
        lastHoveredBlockPos = -1L;
    }
}

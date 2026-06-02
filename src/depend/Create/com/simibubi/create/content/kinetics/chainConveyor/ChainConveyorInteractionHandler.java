/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.createmod.catnip.data.WorldAttached
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.RenderHighlightEvent$Block
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.google.common.cache.Cache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionPacket;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorRidingHandler;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorShape;
import com.simibubi.create.content.kinetics.chainConveyor.ChainPackageInteractionPacket;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packagePort.PackagePortTarget;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetSelectionHandler;
import com.simibubi.create.foundation.utility.RaycastHelper;
import com.simibubi.create.foundation.utility.TickBasedCache;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.data.WorldAttached;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.Tags;

@EventBusSubscriber(value={Dist.CLIENT})
public class ChainConveyorInteractionHandler {
    public static WorldAttached<Cache<BlockPos, List<ChainConveyorShape>>> loadedChains = new WorldAttached($ -> new TickBasedCache(60, true));
    public static BlockPos selectedLift;
    public static float selectedChainPosition;
    public static BlockPos selectedConnection;
    public static Vec3 selectedBakedPosition;
    public static ChainConveyorShape selectedShape;

    public static void clientTick() {
        if (!ChainConveyorInteractionHandler.isActive()) {
            selectedLift = null;
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        boolean isWrench = mc.player.isHolding(i -> i.is(Tags.Items.TOOLS_WRENCH));
        boolean dismantling = isWrench && mc.player.isShiftKeyDown();
        double range = mc.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1.0;
        Vec3 from = mc.player.getEyePosition();
        Vec3 to = RaycastHelper.getTraceTarget((Player)mc.player, range, from);
        HitResult hitResult = mc.hitResult;
        double bestDiff = 3.4028234663852886E38;
        if (hitResult != null) {
            bestDiff = hitResult.getLocation().distanceToSqr(from);
        }
        BlockPos bestLift = null;
        ChainConveyorShape bestShape = null;
        selectedConnection = null;
        for (Map.Entry entry : ((Cache)loadedChains.get((LevelAccessor)Minecraft.getInstance().level)).asMap().entrySet()) {
            BlockPos liftPos = (BlockPos)entry.getKey();
            for (ChainConveyorShape chainConveyorShape : (List)entry.getValue()) {
                double distanceToSqr;
                Vec3 liftVec;
                Vec3 intersect;
                if (chainConveyorShape instanceof ChainConveyorShape.ChainConveyorBB && dismantling || (intersect = chainConveyorShape.intersect(from.subtract(liftVec = Vec3.atLowerCornerOf((Vec3i)liftPos)), to.subtract(liftVec))) == null || (distanceToSqr = intersect.add(liftVec).distanceToSqr(from)) > bestDiff) continue;
                bestDiff = distanceToSqr;
                bestLift = liftPos;
                bestShape = chainConveyorShape;
                selectedChainPosition = chainConveyorShape.getChainPosition(intersect);
                if (!(chainConveyorShape instanceof ChainConveyorShape.ChainConveyorOBB)) continue;
                ChainConveyorShape.ChainConveyorOBB obb = (ChainConveyorShape.ChainConveyorOBB)chainConveyorShape;
                selectedConnection = obb.connection;
            }
        }
        selectedLift = bestLift;
        if (bestLift == null) {
            return;
        }
        selectedShape = bestShape;
        selectedBakedPosition = bestShape.getVec(bestLift, selectedChainPosition);
        if (!isWrench) {
            Outliner.getInstance().chaseAABB((Object)"ChainPointSelection", new AABB(selectedBakedPosition, selectedBakedPosition)).colored(Color.WHITE).lineWidth(0.16666667f).disableLineNormals();
        }
    }

    private static boolean isActive() {
        Minecraft mc = Minecraft.getInstance();
        ItemStack mainHandItem = mc.player.getMainHandItem();
        return mc.player.isHolding(AllTags.AllItemTags.CHAIN_RIDEABLE::matches) || AllBlocks.PACKAGE_FROGPORT.isIn(mainHandItem) || PackageItem.isPackage(mainHandItem);
    }

    public static boolean onUse() {
        if (selectedLift == null) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        ItemStack mainHandItem = mc.player.getMainHandItem();
        if (mc.player.isHolding(AllTags.AllItemTags.CHAIN_RIDEABLE::matches)) {
            ItemStack usedItem;
            ItemStack offHandItem = mc.player.getOffhandItem();
            ItemStack itemStack = usedItem = AllTags.AllItemTags.CHAIN_RIDEABLE.matches(mainHandItem) ? mainHandItem : offHandItem;
            if (!mc.player.isShiftKeyDown()) {
                ChainConveyorRidingHandler.embark(selectedLift, selectedChainPosition, selectedConnection);
                return true;
            }
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ChainConveyorConnectionPacket(selectedLift, selectedLift.offset((Vec3i)selectedConnection), usedItem, false));
            return true;
        }
        if (AllBlocks.PACKAGE_FROGPORT.isIn(mainHandItem)) {
            PackagePortTargetSelectionHandler.exactPositionOfTarget = selectedBakedPosition;
            PackagePortTargetSelectionHandler.activePackageTarget = new PackagePortTarget.ChainConveyorFrogportTarget(selectedLift, selectedChainPosition, selectedConnection, false);
            return true;
        }
        if (PackageItem.isPackage(mainHandItem)) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ChainPackageInteractionPacket(selectedLift, selectedConnection, selectedChainPosition, false));
            return true;
        }
        return true;
    }

    public static void drawCustomBlockSelection(PoseStack ms, MultiBufferSource buffer, Vec3 camera) {
        if (selectedLift == null || selectedShape == null) {
            return;
        }
        VertexConsumer vb = buffer.getBuffer(RenderType.lines());
        ms.pushPose();
        ms.translate((double)selectedLift.getX() - camera.x, (double)selectedLift.getY() - camera.y, (double)selectedLift.getZ() - camera.z);
        selectedShape.drawOutline(selectedLift, ms, vb);
        ms.popPose();
    }

    @SubscribeEvent
    public static void hideVanillaBlockSelection(RenderHighlightEvent.Block event) {
        if (selectedLift == null || selectedShape == null) {
            return;
        }
        event.setCanceled(true);
    }
}

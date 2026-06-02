/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.google.common.cache.Cache;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.kinetics.chainConveyor.ChainPackageInteractionPacket;
import com.simibubi.create.foundation.utility.RaycastHelper;
import java.util.List;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ChainPackageInteractionHandler {
    public static boolean onUse() {
        Minecraft mc = Minecraft.getInstance();
        MutableBoolean success = new MutableBoolean(false);
        ((Cache)ChainConveyorPackage.physicsDataCache.get((LevelAccessor)mc.level)).asMap().forEach((i, data) -> {
            Vec3 to;
            if (success.booleanValue()) {
                return;
            }
            if (data == null || data.targetPos == null || data.beReference == null) {
                return;
            }
            AABB bounds = new AABB(data.targetPos, data.targetPos).move(0.0, -0.25, 0.0).expandTowards(0.0, 0.5, 0.0).inflate(0.45);
            double range = mc.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1.0;
            Vec3 from = mc.player.getEyePosition();
            if (bounds.clip(from, to = RaycastHelper.getTraceTarget((Player)mc.player, range, from)).isEmpty()) {
                return;
            }
            ChainConveyorBlockEntity ccbe = (ChainConveyorBlockEntity)data.beReference.get();
            if (ccbe == null || ccbe.isRemoved()) {
                return;
            }
            for (ChainConveyorPackage pckg : ccbe.getLoopingPackages()) {
                if (pckg.netId != i) continue;
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ChainPackageInteractionPacket(ccbe.getBlockPos(), null, pckg.chainPosition, true));
                success.setTrue();
                return;
            }
            for (BlockPos connection : ccbe.connections) {
                List<ChainConveyorPackage> list = ccbe.travellingPackages.get(connection);
                if (list == null) continue;
                for (ChainConveyorPackage pckg : list) {
                    if (pckg.netId != i) continue;
                    CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ChainPackageInteractionPacket(ccbe.getBlockPos(), connection, pckg.chainPosition, true));
                    success.setTrue();
                    return;
                }
            }
        });
        return success.booleanValue();
    }
}

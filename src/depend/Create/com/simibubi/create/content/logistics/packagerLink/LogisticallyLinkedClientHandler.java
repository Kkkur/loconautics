/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.outliner.Outliner
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.logistics.packagerLink;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionHandler;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import java.util.UUID;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

public class LogisticallyLinkedClientHandler {
    private static UUID previouslyHeldFrequency;

    public static void tick() {
        UUID uuid;
        previouslyHeldFrequency = null;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof LogisticallyLinkedBlockItem) || !LogisticallyLinkedBlockItem.isTuned(mainHandItem)) {
            return;
        }
        CompoundTag tag = ((CustomData)mainHandItem.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.EMPTY)).copyTag();
        if (!tag.hasUUID("Freq")) {
            return;
        }
        previouslyHeldFrequency = uuid = tag.getUUID("Freq");
        for (LogisticallyLinkedBehaviour behaviour : LogisticallyLinkedBehaviour.getAllPresent(uuid, false, true)) {
            SmartBlockEntity be = behaviour.blockEntity;
            VoxelShape shape = be.getBlockState().getShape((BlockGetter)player.level(), be.getBlockPos());
            if (shape.isEmpty() || !player.canInteractWithBlock(be.getBlockPos(), 64.0)) continue;
            for (int i = 0; i < shape.toAabbs().size(); ++i) {
                AABB aabb = (AABB)shape.toAabbs().get(i);
                Outliner.getInstance().showAABB((Object)Pair.of((Object)behaviour, (Object)i), aabb.inflate(-0.0078125).move(be.getBlockPos()), 2).lineWidth(0.03125f).disableLineNormals().colored(AnimationTickHolder.getTicks() % 16 < 8 ? 7376301 : 9481677);
            }
        }
    }

    public static void tickPanel(FactoryPanelBehaviour fpb) {
        if (previouslyHeldFrequency == null) {
            return;
        }
        if (!previouslyHeldFrequency.equals(fpb.network)) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (!player.blockPosition().closerThan((Vec3i)fpb.getPos(), 64.0)) {
            return;
        }
        Outliner.getInstance().showAABB((Object)fpb, FactoryPanelConnectionHandler.getBB(fpb.blockEntity.getBlockState(), fpb.getPanelPosition()).inflate(-0.01171875)).lineWidth(0.03125f).disableLineNormals().colored(AnimationTickHolder.getTicks() % 16 < 8 ? 7376301 : 9481677);
    }
}

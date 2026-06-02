/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.render.BindableTexture
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 */
package com.simibubi.create.content.contraptions.chassis;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.contraptions.chassis.ChassisBlockEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.BindableTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class ChassisRangeDisplay {
    private static final int DISPLAY_TIME = 200;
    private static GroupEntry lastHoveredGroup = null;
    static Map<BlockPos, Entry> entries = new HashMap<BlockPos, Entry>();
    static List<GroupEntry> groupEntries = new ArrayList<GroupEntry>();

    public static void tick() {
        GroupEntry existingGroupForPos;
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel world = Minecraft.getInstance().level;
        boolean hasWrench = AllItems.WRENCH.isIn(player.getMainHandItem());
        Iterator<Object> iterator = entries.keySet().iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            Entry entry = entries.get(pos);
            if (ChassisRangeDisplay.tickEntry(entry, hasWrench)) {
                iterator.remove();
            }
            Outliner.getInstance().keep(entry.getOutlineKey());
        }
        iterator = groupEntries.iterator();
        while (iterator.hasNext()) {
            GroupEntry group = (GroupEntry)iterator.next();
            if (ChassisRangeDisplay.tickEntry(group, hasWrench)) {
                iterator.remove();
                if (group == lastHoveredGroup) {
                    lastHoveredGroup = null;
                }
            }
            Outliner.getInstance().keep(group.getOutlineKey());
        }
        if (!hasWrench) {
            return;
        }
        HitResult over = Minecraft.getInstance().hitResult;
        if (!(over instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult ray = (BlockHitResult)over;
        BlockPos pos = ray.getBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.isRemoved()) {
            return;
        }
        if (!(blockEntity instanceof ChassisBlockEntity)) {
            return;
        }
        ChassisBlockEntity chassisBlockEntity = (ChassisBlockEntity)blockEntity;
        boolean ctrl = AllKeys.ctrlDown();
        if (ctrl && (existingGroupForPos = ChassisRangeDisplay.getExistingGroupForPos(pos)) != null) {
            for (ChassisBlockEntity included : existingGroupForPos.includedBEs) {
                entries.remove(included.getBlockPos());
            }
            existingGroupForPos.timer = 200;
            return;
        }
        if (!entries.containsKey(pos) || ctrl) {
            ChassisRangeDisplay.display(chassisBlockEntity);
        } else if (!ctrl) {
            ChassisRangeDisplay.entries.get((Object)pos).timer = 200;
        }
    }

    private static boolean tickEntry(Entry entry, boolean hasWrench) {
        ChassisBlockEntity chassisBlockEntity = entry.be;
        Level beWorld = chassisBlockEntity.getLevel();
        ClientLevel world = Minecraft.getInstance().level;
        if (chassisBlockEntity.isRemoved() || beWorld == null || beWorld != world || !world.isLoaded(chassisBlockEntity.getBlockPos())) {
            return true;
        }
        if (!hasWrench && entry.timer > 20) {
            entry.timer = 20;
            return false;
        }
        --entry.timer;
        return entry.timer == 0;
    }

    public static void display(ChassisBlockEntity chassis) {
        if (AllKeys.ctrlDown()) {
            GroupEntry hoveredGroup = new GroupEntry(chassis);
            for (ChassisBlockEntity included : hoveredGroup.includedBEs) {
                Outliner.getInstance().remove((Object)Pair.of((Object)included.getBlockPos(), (Object)1));
            }
            groupEntries.forEach(entry -> Outliner.getInstance().remove(entry.getOutlineKey()));
            groupEntries.clear();
            entries.clear();
            groupEntries.add(hoveredGroup);
            return;
        }
        BlockPos pos = chassis.getBlockPos();
        GroupEntry entry2 = ChassisRangeDisplay.getExistingGroupForPos(pos);
        if (entry2 != null) {
            Outliner.getInstance().remove(entry2.getOutlineKey());
        }
        groupEntries.clear();
        entries.clear();
        entries.put(pos, new Entry(chassis));
    }

    private static GroupEntry getExistingGroupForPos(BlockPos pos) {
        for (GroupEntry groupEntry : groupEntries) {
            for (ChassisBlockEntity chassis : groupEntry.includedBEs) {
                if (!pos.equals((Object)chassis.getBlockPos())) continue;
                return groupEntry;
            }
        }
        return null;
    }

    private static class Entry {
        ChassisBlockEntity be;
        int timer;

        public Entry(ChassisBlockEntity be) {
            this.be = be;
            this.timer = 200;
            Outliner.getInstance().showCluster(this.getOutlineKey(), this.createSelection(be)).colored(0xFFFFFF).disableLineNormals().lineWidth(0.0625f).withFaceTexture((BindableTexture)AllSpecialTextures.HIGHLIGHT_CHECKERED);
        }

        protected Object getOutlineKey() {
            return Pair.of((Object)this.be.getBlockPos(), (Object)1);
        }

        protected Set<BlockPos> createSelection(ChassisBlockEntity chassis) {
            HashSet<BlockPos> positions = new HashSet<BlockPos>();
            List<BlockPos> includedBlockPositions = chassis.getIncludedBlockPositions(null, true);
            if (includedBlockPositions == null) {
                return Collections.emptySet();
            }
            positions.addAll(includedBlockPositions);
            return positions;
        }
    }

    private static class GroupEntry
    extends Entry {
        List<ChassisBlockEntity> includedBEs;

        public GroupEntry(ChassisBlockEntity be) {
            super(be);
        }

        @Override
        protected Object getOutlineKey() {
            return this;
        }

        @Override
        protected Set<BlockPos> createSelection(ChassisBlockEntity chassis) {
            HashSet<BlockPos> list = new HashSet<BlockPos>();
            this.includedBEs = this.be.collectChassisGroup();
            if (this.includedBEs == null) {
                return list;
            }
            for (ChassisBlockEntity chassisBlockEntity : this.includedBEs) {
                list.addAll(super.createSelection(chassisBlockEntity));
            }
            return list;
        }
    }
}

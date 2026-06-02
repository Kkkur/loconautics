/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.render.BindableTexture
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.content.contraptions.chassis;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.contraptions.chassis.ChassisBlockEntity;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.BindableTexture;
import net.minecraft.core.BlockPos;

private static class ChassisRangeDisplay.Entry {
    ChassisBlockEntity be;
    int timer;

    public ChassisRangeDisplay.Entry(ChassisBlockEntity be) {
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

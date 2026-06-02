/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.symmetryWand.mirror;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.equipment.symmetryWand.mirror.CrossPlaneMirror;
import com.simibubi.create.content.equipment.symmetryWand.mirror.SymmetryMirror;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TriplePlaneMirror
extends SymmetryMirror {
    public TriplePlaneMirror(Vec3 pos) {
        super(pos);
        this.orientationIndex = 0;
    }

    @Override
    public Map<BlockPos, BlockState> process(BlockPos position, BlockState block) {
        HashMap<BlockPos, BlockState> result = new HashMap<BlockPos, BlockState>();
        result.put(this.flipX(position), this.flipX(block));
        result.put(this.flipZ(position), this.flipZ(block));
        result.put(this.flipX(this.flipZ(position)), this.flipX(this.flipZ(block)));
        result.put(this.flipD1(position), this.flipD1(block));
        result.put(this.flipD1(this.flipX(position)), this.flipD1(this.flipX(block)));
        result.put(this.flipD1(this.flipZ(position)), this.flipD1(this.flipZ(block)));
        result.put(this.flipD1(this.flipX(this.flipZ(position))), this.flipD1(this.flipX(this.flipZ(block))));
        return result;
    }

    @Override
    public String typeName() {
        return "triple_plane";
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public PartialModel getModel() {
        return AllPartialModels.SYMMETRY_TRIPLEPLANE;
    }

    @Override
    protected void setOrientation() {
    }

    @Override
    public void setOrientation(int index) {
    }

    @Override
    public StringRepresentable getOrientation() {
        return CrossPlaneMirror.Align.Y;
    }

    @Override
    public List<Component> getAlignToolTips() {
        return ImmutableList.of((Object)CreateLang.translateDirect("orientation.horizontal", new Object[0]));
    }
}

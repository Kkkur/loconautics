/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
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
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.equipment.symmetryWand.mirror.SymmetryMirror;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
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

public class PlaneMirror
extends SymmetryMirror {
    public PlaneMirror(Vec3 pos) {
        super(pos);
        this.orientation = Align.XY;
    }

    @Override
    protected void setOrientation() {
        if (this.orientationIndex < 0) {
            this.orientationIndex += Align.values().length;
        }
        if (this.orientationIndex >= Align.values().length) {
            this.orientationIndex -= Align.values().length;
        }
        this.orientation = Align.values()[this.orientationIndex];
    }

    @Override
    public void setOrientation(int index) {
        this.orientation = Align.values()[index];
        this.orientationIndex = index;
    }

    @Override
    public Map<BlockPos, BlockState> process(BlockPos position, BlockState block) {
        HashMap<BlockPos, BlockState> result = new HashMap<BlockPos, BlockState>();
        switch (((Align)this.orientation).ordinal()) {
            case 0: {
                result.put(this.flipZ(position), this.flipZ(block));
                break;
            }
            case 1: {
                result.put(this.flipX(position), this.flipX(block));
                break;
            }
        }
        return result;
    }

    @Override
    public String typeName() {
        return "plane";
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public PartialModel getModel() {
        return AllPartialModels.SYMMETRY_PLANE;
    }

    @Override
    public void applyModelTransform(PoseStack ms) {
        super.applyModelTransform(ms);
        ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).center()).rotateYDegrees((Align)this.orientation == Align.XY ? 0.0f : 90.0f)).uncenter();
    }

    @Override
    public List<Component> getAlignToolTips() {
        return ImmutableList.of((Object)CreateLang.translateDirect("orientation.alongZ", new Object[0]), (Object)CreateLang.translateDirect("orientation.alongX", new Object[0]));
    }

    public static enum Align implements StringRepresentable
    {
        XY("xy"),
        YZ("yz");

        private final String name;

        private Align(String name) {
            this.name = name;
        }

        public String getSerializedName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }
}

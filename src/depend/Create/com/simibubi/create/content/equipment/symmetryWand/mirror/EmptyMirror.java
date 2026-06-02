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
import com.simibubi.create.content.equipment.symmetryWand.mirror.SymmetryMirror;
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

public class EmptyMirror
extends SymmetryMirror {
    public EmptyMirror(Vec3 pos) {
        super(pos);
        this.orientation = Align.None;
    }

    @Override
    protected void setOrientation() {
    }

    @Override
    public void setOrientation(int index) {
        this.orientation = Align.values()[index];
        this.orientationIndex = index;
    }

    @Override
    public Map<BlockPos, BlockState> process(BlockPos position, BlockState block) {
        return new HashMap<BlockPos, BlockState>();
    }

    @Override
    public String typeName() {
        return "empty";
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public PartialModel getModel() {
        return null;
    }

    @Override
    public List<Component> getAlignToolTips() {
        return ImmutableList.of();
    }

    public static enum Align implements StringRepresentable
    {
        None("none");

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

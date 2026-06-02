/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.displayLink.target;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DisplayBoardTarget
extends DisplayTarget {
    @Override
    public void acceptText(int line, List<MutableComponent> text, DisplayLinkContext context) {
    }

    public void acceptFlapText(int line, List<List<MutableComponent>> text, DisplayLinkContext context) {
        FlapDisplayBlockEntity controller = this.getController(context);
        if (controller == null) {
            return;
        }
        if (!controller.isSpeedRequirementFulfilled()) {
            return;
        }
        DisplaySource source = context.blockEntity().activeSource;
        List<FlapDisplayLayout> lines = controller.getLines();
        int i = 0;
        while (i + line < lines.size()) {
            if (i == 0) {
                DisplayBoardTarget.reserve(i + line, controller, context);
            }
            if (i > 0 && this.isReserved(i + line, controller, context)) break;
            FlapDisplayLayout layout = lines.get(i + line);
            if (i >= text.size()) {
                if (source instanceof SingleLineDisplaySource) break;
                controller.applyTextManually(i + line, null);
            } else {
                List<MutableComponent> textLine;
                source.loadFlapDisplayLayout(context, controller, layout, i);
                for (int sectionIndex = 0; sectionIndex < layout.getSections().size() && (textLine = text.get(i)).size() > sectionIndex; ++sectionIndex) {
                    layout.getSections().get(sectionIndex).setText((Component)textLine.get(sectionIndex));
                }
            }
            ++i;
        }
        controller.sendData();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean isReserved(int line, BlockEntity target, DisplayLinkContext context) {
        if (super.isReserved(line, target, context)) return true;
        if (!(target instanceof FlapDisplayBlockEntity)) return false;
        FlapDisplayBlockEntity fdte = (FlapDisplayBlockEntity)target;
        if (fdte.manualLines.length <= line) return false;
        if (!fdte.manualLines[line]) return false;
        return true;
    }

    @Override
    public DisplayTargetStats provideStats(DisplayLinkContext context) {
        FlapDisplayBlockEntity controller = this.getController(context);
        if (controller == null) {
            return new DisplayTargetStats(1, 1, this);
        }
        return new DisplayTargetStats(controller.ySize * 2, controller.getMaxCharCount(), this);
    }

    private FlapDisplayBlockEntity getController(DisplayLinkContext context) {
        BlockEntity teIn = context.getTargetBlockEntity();
        if (!(teIn instanceof FlapDisplayBlockEntity)) {
            return null;
        }
        FlapDisplayBlockEntity be = (FlapDisplayBlockEntity)teIn;
        return be.getController();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public AABB getMultiblockBounds(LevelAccessor level, BlockPos pos) {
        AABB baseShape = super.getMultiblockBounds(level, pos);
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof FlapDisplayBlockEntity)) {
            return baseShape;
        }
        FlapDisplayBlockEntity fdbe = (FlapDisplayBlockEntity)be;
        FlapDisplayBlockEntity controller = fdbe.getController();
        if (controller == null) {
            return baseShape;
        }
        Vec3i normal = controller.getDirection().getClockWise().getNormal();
        return baseShape.move(controller.getBlockPos().subtract((Vec3i)pos)).expandTowards((double)(normal.getX() * (controller.xSize - 1)), (double)(1 - controller.ySize), (double)(normal.getZ() * (controller.xSize - 1)));
    }
}

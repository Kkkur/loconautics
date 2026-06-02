/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.bogey;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class BogeyBlockEntityVisual
extends AbstractBlockEntityVisual<AbstractBogeyBlockEntity>
implements SimpleDynamicVisual {
    private final PoseStack poseStack = new PoseStack();
    @Nullable
    private final BogeySizes.BogeySize bogeySize;
    private BogeyStyle lastStyle;
    @Nullable
    private BogeyVisual bogey;

    public BogeyBlockEntityVisual(VisualizationContext ctx, AbstractBogeyBlockEntity blockEntity, float partialTick) {
        super(ctx, (BlockEntity)blockEntity, partialTick);
        this.lastStyle = blockEntity.getStyle();
        Block block = this.blockState.getBlock();
        if (!(block instanceof AbstractBogeyBlock)) {
            this.bogeySize = null;
            return;
        }
        AbstractBogeyBlock block2 = (AbstractBogeyBlock)block;
        this.bogeySize = block2.getSize();
        BlockPos visualPos = this.getVisualPosition();
        this.poseStack.translate((float)visualPos.getX(), (float)visualPos.getY(), (float)visualPos.getZ());
        this.poseStack.translate(0.5f, 0.5f, 0.5f);
        if (this.blockState.getValue(AbstractBogeyBlock.AXIS) == Direction.Axis.X) {
            this.poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
        }
        this.poseStack.translate(0.0, -1.5078125, 0.0);
        this.bogey = this.lastStyle.createVisual(this.bogeySize, this.visualizationContext, partialTick, false);
        this.updateBogey(partialTick);
    }

    public void beginFrame(DynamicVisual.Context context) {
        if (this.bogeySize == null) {
            return;
        }
        BogeyStyle style = ((AbstractBogeyBlockEntity)this.blockEntity).getStyle();
        if (style != this.lastStyle) {
            if (this.bogey != null) {
                this.bogey.delete();
                this.bogey = null;
            }
            this.lastStyle = style;
            this.bogey = this.lastStyle.createVisual(this.bogeySize, this.visualizationContext, context.partialTick(), false);
            this.updateLight(context.partialTick());
        }
        this.updateBogey(context.partialTick());
    }

    private void updateBogey(float partialTick) {
        if (this.bogey == null) {
            return;
        }
        CompoundTag bogeyData = ((AbstractBogeyBlockEntity)this.blockEntity).getBogeyData();
        float angle = ((AbstractBogeyBlockEntity)this.blockEntity).getVirtualAngle(partialTick);
        this.bogey.update(bogeyData, angle, this.poseStack);
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        if (this.bogey != null) {
            this.bogey.collectCrumblingInstances(consumer);
        }
    }

    public void updateLight(float partialTick) {
        if (this.bogey != null) {
            this.bogey.updateLight(this.computePackedLight());
        }
    }

    protected void _delete() {
        if (this.bogey != null) {
            this.bogey.delete();
        }
    }
}

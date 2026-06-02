/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.chassis;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.chassis.StickerBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.Property;

public class StickerVisual
extends AbstractBlockEntityVisual<StickerBlockEntity>
implements SimpleDynamicVisual {
    float lastOffset = Float.NaN;
    final Direction facing;
    final boolean fakeWorld;
    final int offset;
    private final TransformedInstance head = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.STICKER_HEAD)).createInstance();

    public StickerVisual(VisualizationContext context, StickerBlockEntity blockEntity, float partialTick) {
        super(context, (BlockEntity)blockEntity, partialTick);
        this.fakeWorld = blockEntity.getLevel() != Minecraft.getInstance().level;
        this.facing = (Direction)this.blockState.getValue((Property)StickerBlock.FACING);
        this.offset = (Boolean)this.blockState.getValue((Property)StickerBlock.EXTENDED) != false ? 1 : 0;
        this.animateHead(this.offset);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        float offset = ((StickerBlockEntity)this.blockEntity).piston.getValue(ctx.partialTick());
        if (this.fakeWorld) {
            offset = this.offset;
        }
        if (Mth.equal((float)offset, (float)this.lastOffset)) {
            return;
        }
        this.animateHead(offset);
        this.lastOffset = offset;
    }

    private void animateHead(float offset) {
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.head.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).nudge(((StickerBlockEntity)this.blockEntity).hashCode())).center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)this.facing))).rotateXDegrees(AngleHelper.verticalAngle((Direction)this.facing) + 90.0f)).uncenter()).translate(0.0f, offset * offset * 4.0f / 16.0f, 0.0f).setChanged();
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.head});
    }

    protected void _delete() {
        this.head.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.head);
    }
}

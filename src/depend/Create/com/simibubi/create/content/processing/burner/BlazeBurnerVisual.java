/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visual.TickableVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleTickableVisual
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.processing.burner;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerRenderer;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import java.util.function.Consumer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class BlazeBurnerVisual
extends AbstractBlockEntityVisual<BlazeBurnerBlockEntity>
implements SimpleDynamicVisual,
SimpleTickableVisual {
    private BlazeBurnerBlock.HeatLevel heatLevel = BlazeBurnerBlock.HeatLevel.SMOULDERING;
    private final TransformedInstance head;
    private final boolean isInert;
    @Nullable
    private TransformedInstance smallRods;
    @Nullable
    private TransformedInstance largeRods;
    @Nullable
    private ScrollInstance flame;
    @Nullable
    private TransformedInstance goggles;
    @Nullable
    private TransformedInstance hat;
    private boolean validBlockAbove;

    public BlazeBurnerVisual(VisualizationContext ctx, BlazeBurnerBlockEntity blockEntity, float partialTick) {
        super(ctx, (BlockEntity)blockEntity, partialTick);
        this.validBlockAbove = blockEntity.isValidBlockAbove();
        PartialModel blazeModel = BlazeBurnerRenderer.getBlazeModel(this.heatLevel, this.validBlockAbove);
        this.isInert = blazeModel == AllPartialModels.BLAZE_INERT;
        this.head = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)blazeModel)).createInstance();
        this.head.light(0xF000F0);
        this.animate(partialTick);
    }

    public void tick(TickableVisual.Context context) {
        ((BlazeBurnerBlockEntity)this.blockEntity).tickAnimation();
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        if (!this.isVisible(ctx.frustum()) || this.doDistanceLimitThisFrame(ctx)) {
            return;
        }
        this.animate(ctx.partialTick());
    }

    private void animate(float partialTicks) {
        boolean hatPresent;
        float animation = ((BlazeBurnerBlockEntity)this.blockEntity).headAnimation.getValue(partialTicks) * 0.175f;
        boolean validBlockAbove = animation > 0.125f;
        BlazeBurnerBlock.HeatLevel heatLevel = ((BlazeBurnerBlockEntity)this.blockEntity).getHeatLevelForRender();
        if (validBlockAbove != this.validBlockAbove || heatLevel != this.heatLevel) {
            this.validBlockAbove = validBlockAbove;
            PartialModel blazeModel = BlazeBurnerRenderer.getBlazeModel(heatLevel, validBlockAbove);
            this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)blazeModel)).stealInstance((Instance)this.head);
            boolean needsRods = heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING);
            boolean hasRods = this.heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING);
            if (needsRods && !hasRods) {
                PartialModel rodsModel = heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllPartialModels.BLAZE_BURNER_SUPER_RODS : AllPartialModels.BLAZE_BURNER_RODS;
                PartialModel rodsModel2 = heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllPartialModels.BLAZE_BURNER_SUPER_RODS_2 : AllPartialModels.BLAZE_BURNER_RODS_2;
                this.smallRods = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)rodsModel)).createInstance();
                this.largeRods = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)rodsModel2)).createInstance();
                this.smallRods.light(0xF000F0);
                this.largeRods.light(0xF000F0);
            } else if (!needsRods && hasRods) {
                if (this.smallRods != null) {
                    this.smallRods.delete();
                }
                if (this.largeRods != null) {
                    this.largeRods.delete();
                }
                this.smallRods = null;
                this.largeRods = null;
            }
            this.heatLevel = heatLevel;
        }
        if (validBlockAbove && this.flame == null) {
            this.setupFlameInstance();
        } else if (!validBlockAbove && this.flame != null) {
            this.flame.delete();
            this.flame = null;
        }
        if (((BlazeBurnerBlockEntity)this.blockEntity).goggles && this.goggles == null) {
            this.goggles = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)(this.isInert ? AllPartialModels.BLAZE_GOGGLES_SMALL : AllPartialModels.BLAZE_GOGGLES))).createInstance();
            this.goggles.light(0xF000F0);
        } else if (!((BlazeBurnerBlockEntity)this.blockEntity).goggles && this.goggles != null) {
            this.goggles.delete();
            this.goggles = null;
        }
        boolean bl = hatPresent = ((BlazeBurnerBlockEntity)this.blockEntity).hat || ((BlazeBurnerBlockEntity)this.blockEntity).stockKeeper;
        if (hatPresent && this.hat == null) {
            this.hat = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)(((BlazeBurnerBlockEntity)this.blockEntity).stockKeeper ? AllPartialModels.LOGISTICS_HAT : AllPartialModels.TRAIN_HAT))).createInstance();
            this.hat.light(0xF000F0);
        } else if (!hatPresent && this.hat != null) {
            this.hat.delete();
            this.hat = null;
        }
        int hashCode = ((BlazeBurnerBlockEntity)this.blockEntity).hashCode();
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)this.level);
        float renderTick = time + (float)(hashCode % 13) * 16.0f;
        float offsetMult = heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING) ? 64.0f : 16.0f;
        float offset = Mth.sin((float)((float)((double)(renderTick / 16.0f) % (Math.PI * 2)))) / offsetMult;
        float headY = offset - animation * 0.75f;
        float horizontalAngle = AngleHelper.rad((double)((BlazeBurnerBlockEntity)this.blockEntity).headAngle.getValue(partialTicks));
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.head.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translateY(headY)).translate(0.5f)).rotateY(horizontalAngle).translateBack(0.5f)).setChanged();
        if (this.goggles != null) {
            ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.goggles.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translateY(headY + 0.5f)).translate(0.5f)).rotateY(horizontalAngle).translateBack(0.5f)).setChanged();
        }
        if (this.hat != null) {
            ((TransformedInstance)((TransformedInstance)this.hat.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translateY(headY)).translateY(0.75f);
            ((TransformedInstance)this.hat.rotateCentered(horizontalAngle + (float)Math.PI, Direction.UP)).translate(0.5f, 0.0f, 0.5f).light(0xF000F0);
            this.hat.setChanged();
        }
        if (this.smallRods != null) {
            float offset1 = Mth.sin((float)((float)(((double)(renderTick / 16.0f) + Math.PI) % (Math.PI * 2)))) / offsetMult;
            ((TransformedInstance)((TransformedInstance)this.smallRods.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translateY(offset1 + animation + 0.125f)).setChanged();
        }
        if (this.largeRods != null) {
            float offset2 = Mth.sin((float)((float)(((double)(renderTick / 16.0f) + 1.5707963267948966) % (Math.PI * 2)))) / offsetMult;
            ((TransformedInstance)((TransformedInstance)this.largeRods.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translateY(offset2 + animation - 0.1875f)).setChanged();
        }
    }

    private void setupFlameInstance() {
        this.flame = (ScrollInstance)this.instancerProvider().instancer(AllInstanceTypes.SCROLLING, Models.partial((PartialModel)AllPartialModels.BLAZE_BURNER_FLAME)).createInstance();
        this.flame.position((Vec3i)this.getVisualPosition()).light(0xF000F0);
        SpriteShiftEntry spriteShift = this.heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllSpriteShifts.SUPER_BURNER_FLAME : AllSpriteShifts.BURNER_FLAME;
        float spriteWidth = spriteShift.getTarget().getU1() - spriteShift.getTarget().getU0();
        float spriteHeight = spriteShift.getTarget().getV1() - spriteShift.getTarget().getV0();
        float speed = 0.03125f + 0.015625f * (float)this.heatLevel.ordinal();
        this.flame.speedU = speed / 2.0f;
        this.flame.speedV = speed;
        this.flame.scaleU = spriteWidth / 2.0f;
        this.flame.scaleV = spriteHeight / 2.0f;
        this.flame.diffU = spriteShift.getTarget().getU0() - spriteShift.getOriginal().getU0();
        this.flame.diffV = spriteShift.getTarget().getV0() - spriteShift.getOriginal().getV0();
    }

    public void updateLight(float partialTick) {
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
    }

    protected void _delete() {
        this.head.delete();
        if (this.smallRods != null) {
            this.smallRods.delete();
        }
        if (this.largeRods != null) {
            this.largeRods.delete();
        }
        if (this.flame != null) {
            this.flame.delete();
        }
        if (this.goggles != null) {
            this.goggles.delete();
        }
        if (this.hat != null) {
            this.hat.delete();
        }
    }
}

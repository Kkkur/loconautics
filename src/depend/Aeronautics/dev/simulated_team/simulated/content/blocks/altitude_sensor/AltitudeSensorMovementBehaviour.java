/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.behaviour.movement.MovementBehaviour
 *  com.simibubi.create.content.contraptions.behaviour.MovementContext
 *  com.simibubi.create.content.contraptions.render.ContraptionMatrices
 *  com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.util.Tuple
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3d
 */
package dev.simulated_team.simulated.content.blocks.altitude_sensor;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

public class AltitudeSensorMovementBehaviour
implements MovementBehaviour {
    public boolean disableBlockEntityRendering() {
        return true;
    }

    public void tick(MovementContext context) {
        super.tick(context);
        float yPos = (float)Sable.HELPER.projectOutOfSubLevel((Level)context.world, (Vector3d)JOMLConversion.toJOML((Position)context.position)).y;
        Object object = context.temporaryData;
        if (object instanceof Tuple) {
            Tuple heights = (Tuple)object;
            context.temporaryData = new Tuple(heights.getB(), (Object)Float.valueOf(yPos));
        } else {
            context.temporaryData = new Tuple((Object)Float.valueOf(yPos), (Object)Float.valueOf(yPos));
        }
    }

    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        float visualHeight;
        float lowSignal = context.blockEntityData.getFloat("low_signal");
        float highSignal = context.blockEntityData.getFloat("high_signal");
        Object object = context.temporaryData;
        if (object instanceof Tuple) {
            Tuple heights = (Tuple)object;
            visualHeight = ((Float)heights.getA()).floatValue() * (1.0f - AnimationTickHolder.getPartialTicks()) + ((Float)heights.getB()).floatValue() * AnimationTickHolder.getPartialTicks();
        } else {
            Vector3d pos = context.position != null ? JOMLConversion.toJOML((Position)context.position) : new Vector3d();
            visualHeight = (float)Sable.HELPER.projectOutOfSubLevel((Level)context.world, (Vector3d)pos).y;
        }
        Level level = context.contraption.entity.level();
        float y = (float)Mth.map((double)context.position.y, (double)level.getMinBuildHeight(), (double)level.getMaxBuildHeight(), (double)0.0, (double)1.0);
        float value = Mth.clampedMap((float)y, (float)0.0f, (float)1.0f, (float)lowSignal, (float)highSignal);
        AltitudeSensorRenderer.render(context.state, 1000, value, visualHeight, matrices.getViewProjection(), matrices.getModel(), matrices.getWorld(), buffer, LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos));
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.client.model.BakedModelWrapper
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntityTilt;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrackModel
extends BakedModelWrapper<BakedModel> {
    public TrackModel(BakedModel originalModel) {
        super(originalModel);
    }

    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        List templateQuads = super.getQuads(state, side, rand, extraData, renderType);
        if (templateQuads.isEmpty()) {
            return templateQuads;
        }
        if (!extraData.has(TrackBlockEntityTilt.ASCENDING_PROPERTY)) {
            return templateQuads;
        }
        double angleIn = (Double)extraData.get(TrackBlockEntityTilt.ASCENDING_PROPERTY);
        double angle = Math.abs(angleIn);
        boolean flip = angleIn < 0.0;
        TrackShape trackShape = (TrackShape)((Object)state.getValue(TrackBlock.SHAPE));
        double hAngle = switch (trackShape) {
            case TrackShape.XO -> 0.0;
            case TrackShape.PD -> 45.0;
            case TrackShape.ZO -> 90.0;
            case TrackShape.ND -> 135.0;
            default -> 0.0;
        };
        Vec3 verticalOffset = new Vec3(0.0, -0.25, 0.0);
        Vec3 diagonalRotationPoint = trackShape == TrackShape.ND || trackShape == TrackShape.PD ? new Vec3((double)((Mth.SQRT_OF_TWO - 1.0f) / 2.0f), 0.0, 0.0) : Vec3.ZERO;
        UnaryOperator transform = v -> {
            v = v.add(verticalOffset);
            v = VecHelper.rotateCentered((Vec3)v, (double)hAngle, (Direction.Axis)Direction.Axis.Y);
            v = v.add(diagonalRotationPoint);
            v = VecHelper.rotate((Vec3)v, (double)angle, (Direction.Axis)Direction.Axis.Z);
            v = v.subtract(diagonalRotationPoint);
            v = VecHelper.rotateCentered((Vec3)v, (double)(-hAngle + (double)(flip ? 180 : 0)), (Direction.Axis)Direction.Axis.Y);
            v = v.subtract(verticalOffset);
            return v;
        };
        int size = templateQuads.size();
        ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>();
        for (BakedQuad templateQuad : templateQuads) {
            BakedQuad quad = BakedQuadHelper.clone(templateQuad);
            int[] vertexData = quad.getVertices();
            for (int j = 0; j < 4; ++j) {
                BakedQuadHelper.setXYZ(vertexData, j, (Vec3)transform.apply(BakedQuadHelper.getXYZ(vertexData, j)));
            }
            quads.add(quad);
        }
        return quads;
    }
}

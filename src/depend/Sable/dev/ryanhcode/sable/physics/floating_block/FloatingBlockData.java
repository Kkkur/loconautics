/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3d
 *  org.joml.Matrix3dc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.physics.floating_block;

import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SableMathUtils;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class FloatingBlockData {
    private static final Matrix3d tempMassMatrix = new Matrix3d();
    private static final Vector3d tempPosOffset = new Vector3d();
    protected Matrix3d outerProduct = new Matrix3d().scale(0.0);
    protected Vector3d weightedPosition = new Vector3d();
    protected double totalScale;
    protected int blockCount = 0;
    protected double latestPressureScale = 1.0;
    static final Vector3d positionTemp = new Vector3d();

    public void addFloatingBlock(Vector3dc pos, double scale) {
        this.addData(pos, scale);
        ++this.blockCount;
    }

    public void removeFloatingBlock(Vector3dc pos, double scale) {
        --this.blockCount;
        this.addData(pos, -scale);
    }

    private void addData(Vector3dc pos, double scale) {
        this.weightedPosition.fma(scale, pos);
        this.totalScale += scale;
        pos.fma(-1.0 / this.totalScale, (Vector3dc)this.weightedPosition, tempPosOffset);
        if (this.blockCount > 0) {
            SableMathUtils.fmaOuterProduct((Vector3dc)tempPosOffset, (Vector3dc)tempPosOffset, scale * this.totalScale / (this.totalScale - scale), this.outerProduct);
        }
        this.outerProduct.add((Matrix3dc)tempMassMatrix.identity().scale(scale / 6.0));
    }

    public void translateOrigin(Vector3dc nudge) {
        this.weightedPosition.fma(this.totalScale, nudge);
    }

    public double getPressureScale() {
        return this.latestPressureScale;
    }

    public void computePressureScale(SubLevel subLevel) {
        subLevel.logicalPose().orientation().transform((Vector3dc)this.weightedPosition, positionTemp);
        subLevel.logicalPose().position().fma(1.0 / this.totalScale, (Vector3dc)positionTemp, positionTemp);
        this.latestPressureScale = DimensionPhysicsData.getAirPressure(subLevel.getLevel(), (Vector3dc)positionTemp);
    }
}

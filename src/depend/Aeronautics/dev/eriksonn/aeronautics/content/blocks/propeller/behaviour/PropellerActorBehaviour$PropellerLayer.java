/*
 * Decompiled with CFR 0.152.
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.behaviour;

public record PropellerActorBehaviour.PropellerLayer(double offset, double innerRadius, double outerRadius) {
    public double innerRadiusSquared() {
        return this.innerRadius * this.innerRadius;
    }

    public double outerRadiusSquared() {
        return this.outerRadius * this.outerRadius;
    }
}

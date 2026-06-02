/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;

public static class ConnectedTextureBehaviour.ContextRequirement.Builder {
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private boolean topLeft;
    private boolean topRight;
    private boolean bottomLeft;
    private boolean bottomRight;

    public ConnectedTextureBehaviour.ContextRequirement.Builder up() {
        this.up = true;
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder down() {
        this.down = true;
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder left() {
        this.left = true;
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder right() {
        this.right = true;
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder topLeft() {
        this.topLeft = true;
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder topRight() {
        this.topRight = true;
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder bottomLeft() {
        this.bottomLeft = true;
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder bottomRight() {
        this.bottomRight = true;
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder horizontal() {
        this.left();
        this.right();
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder vertical() {
        this.up();
        this.down();
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder axisAligned() {
        this.horizontal();
        this.vertical();
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder corners() {
        this.topLeft();
        this.topRight();
        this.bottomLeft();
        this.bottomRight();
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement.Builder all() {
        this.axisAligned();
        this.corners();
        return this;
    }

    public ConnectedTextureBehaviour.ContextRequirement build() {
        return new ConnectedTextureBehaviour.ContextRequirement(this.up, this.down, this.left, this.right, this.topLeft, this.topRight, this.bottomLeft, this.bottomRight);
    }
}

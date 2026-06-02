/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.block.connected;

public static class ConnectedTextureBehaviour.ContextRequirement {
    public final boolean up;
    public final boolean down;
    public final boolean left;
    public final boolean right;
    public final boolean topLeft;
    public final boolean topRight;
    public final boolean bottomLeft;
    public final boolean bottomRight;

    public ConnectedTextureBehaviour.ContextRequirement(boolean up, boolean down, boolean left, boolean right, boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean up;
        private boolean down;
        private boolean left;
        private boolean right;
        private boolean topLeft;
        private boolean topRight;
        private boolean bottomLeft;
        private boolean bottomRight;

        public Builder up() {
            this.up = true;
            return this;
        }

        public Builder down() {
            this.down = true;
            return this;
        }

        public Builder left() {
            this.left = true;
            return this;
        }

        public Builder right() {
            this.right = true;
            return this;
        }

        public Builder topLeft() {
            this.topLeft = true;
            return this;
        }

        public Builder topRight() {
            this.topRight = true;
            return this;
        }

        public Builder bottomLeft() {
            this.bottomLeft = true;
            return this;
        }

        public Builder bottomRight() {
            this.bottomRight = true;
            return this;
        }

        public Builder horizontal() {
            this.left();
            this.right();
            return this;
        }

        public Builder vertical() {
            this.up();
            this.down();
            return this;
        }

        public Builder axisAligned() {
            this.horizontal();
            this.vertical();
            return this;
        }

        public Builder corners() {
            this.topLeft();
            this.topRight();
            this.bottomLeft();
            this.bottomRight();
            return this;
        }

        public Builder all() {
            this.axisAligned();
            this.corners();
            return this;
        }

        public ConnectedTextureBehaviour.ContextRequirement build() {
            return new ConnectedTextureBehaviour.ContextRequirement(this.up, this.down, this.left, this.right, this.topLeft, this.topRight, this.bottomLeft, this.bottomRight);
        }
    }
}

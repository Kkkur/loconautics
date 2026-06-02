/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.redstone.nixieTube;

import org.jetbrains.annotations.NotNull;

public static final class NixieTubeBlockEntity.ComputerSignal {
    @NotNull
    public TubeDisplay first = new TubeDisplay();
    @NotNull
    public TubeDisplay second = new TubeDisplay();

    public void decode(byte[] encoded) {
        this.first.decode(encoded, 0);
        this.second.decode(encoded, 7);
    }

    public byte[] encode() {
        byte[] encoded = new byte[14];
        this.first.encode(encoded, 0);
        this.second.encode(encoded, 7);
        return encoded;
    }

    public static final class TubeDisplay {
        public static final int ENCODED_SIZE = 7;
        public byte r = (byte)63;
        public byte g = (byte)63;
        public byte b = (byte)63;
        public byte blinkPeriod = 0;
        public byte blinkOffTime = 0;
        public byte glowWidth = 1;
        public byte glowHeight = 1;

        public void decode(byte[] data, int offset) {
            this.r = data[offset];
            this.g = data[offset + 1];
            this.b = data[offset + 2];
            this.blinkPeriod = data[offset + 3];
            this.blinkOffTime = data[offset + 4];
            this.glowWidth = data[offset + 5];
            this.glowHeight = data[offset + 6];
        }

        public void encode(byte[] data, int offset) {
            data[offset] = this.r;
            data[offset + 1] = this.g;
            data[offset + 2] = this.b;
            data[offset + 3] = this.blinkPeriod;
            data[offset + 4] = this.blinkOffTime;
            data[offset + 5] = this.glowWidth;
            data[offset + 6] = this.glowHeight;
        }
    }
}

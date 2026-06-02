/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.IntAttached
 */
package com.simibubi.create.content.contraptions.actors.trainControls;

import net.createmod.catnip.data.IntAttached;

static class ControlsServerHandler.ManuallyPressedKey
extends IntAttached<Integer> {
    public ControlsServerHandler.ManuallyPressedKey(Integer second) {
        super(Integer.valueOf(30), (Object)second);
    }

    public void keepAlive() {
        this.setFirst(30);
    }

    public boolean isAlive() {
        return (Integer)this.getFirst() > 0;
    }
}

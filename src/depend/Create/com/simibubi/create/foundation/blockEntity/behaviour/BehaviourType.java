/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

public class BehaviourType<T extends BlockEntityBehaviour> {
    private String name;

    public BehaviourType(String name) {
        this.name = name;
    }

    public BehaviourType() {
        this("");
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        return super.hashCode() * 31 * 493286711;
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

private static class BracketedKineticBlockModel.BracketedModelData {
    private BakedModel bracket;

    private BracketedKineticBlockModel.BracketedModelData() {
    }

    public void putBracket(BlockState state) {
        if (state != null) {
            this.bracket = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        }
    }

    public BakedModel getBracket() {
        return this.bracket;
    }
}

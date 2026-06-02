/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

private static class PipeAttachmentModel.PipeModelData {
    private FluidTransportBehaviour.AttachmentTypes[] attachments = new FluidTransportBehaviour.AttachmentTypes[6];
    private boolean encased;
    private BakedModel bracket;

    public PipeAttachmentModel.PipeModelData() {
        Arrays.fill((Object[])this.attachments, (Object)FluidTransportBehaviour.AttachmentTypes.NONE);
    }

    public void putBracket(BlockState state) {
        if (state != null) {
            this.bracket = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        }
    }

    public BakedModel getBracket() {
        return this.bracket;
    }

    public void putAttachment(Direction face, FluidTransportBehaviour.AttachmentTypes rim) {
        this.attachments[face.get3DDataValue()] = rim;
    }

    public FluidTransportBehaviour.AttachmentTypes getAttachment(Direction face) {
        return this.attachments[face.get3DDataValue()];
    }

    public void setEncased(boolean encased) {
        this.encased = encased;
    }

    public boolean isEncased() {
        return this.encased;
    }
}

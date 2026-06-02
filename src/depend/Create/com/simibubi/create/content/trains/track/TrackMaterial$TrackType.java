/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;

public static class TrackMaterial.TrackType {
    public static final TrackMaterial.TrackType STANDARD = new TrackMaterial.TrackType(Create.asResource("standard"), TrackBlock::new);
    public final ResourceLocation id;
    protected final TrackBlockFactory factory;

    public TrackMaterial.TrackType(ResourceLocation id, TrackBlockFactory factory) {
        this.id = id;
        this.factory = factory;
    }

    @FunctionalInterface
    public static interface TrackBlockFactory {
        public TrackBlock create(BlockBehaviour.Properties var1, TrackMaterial var2);
    }
}

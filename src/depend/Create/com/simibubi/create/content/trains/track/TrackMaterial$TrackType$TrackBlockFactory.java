/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.world.level.block.state.BlockBehaviour;

@FunctionalInterface
public static interface TrackMaterial.TrackType.TrackBlockFactory {
    public TrackBlock create(BlockBehaviour.Properties var1, TrackMaterial var2);
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.AgeableListModel
 *  net.minecraft.client.model.geom.ModelPart
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={AgeableListModel.class})
public interface AgeableListModelAccessor {
    @Invoker(value="headParts")
    public Iterable<ModelPart> create$callHeadParts();

    @Invoker(value="bodyParts")
    public Iterable<ModelPart> create$callBodyParts();
}

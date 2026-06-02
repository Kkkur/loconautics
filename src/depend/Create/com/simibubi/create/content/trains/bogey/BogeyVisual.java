/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.instance.Instance
 *  net.minecraft.nbt.CompoundTag
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.bogey;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.api.instance.Instance;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface BogeyVisual {
    public void update(CompoundTag var1, float var2, PoseStack var3);

    public void hide();

    public void updateLight(int var1);

    public void collectCrumblingInstances(Consumer<@Nullable Instance> var1);

    public void delete();
}

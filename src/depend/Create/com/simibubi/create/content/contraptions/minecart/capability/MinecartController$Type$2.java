/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.neoforged.neoforge.attachment.IAttachmentHolder
 *  net.neoforged.neoforge.attachment.IAttachmentSerializer
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.minecart.capability;

import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MinecartController.Type.2
implements IAttachmentSerializer<CompoundTag, MinecartController> {
    MinecartController.Type.2() {
    }

    @NotNull
    public MinecartController read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        MinecartController controller = new MinecartController(null);
        controller.deserializeNBT(provider, tag);
        return controller;
    }

    @Nullable
    public CompoundTag write(@NotNull MinecartController attachment, @NotNull HolderLookup.Provider provider) {
        return attachment.serializeNBT(provider);
    }
}

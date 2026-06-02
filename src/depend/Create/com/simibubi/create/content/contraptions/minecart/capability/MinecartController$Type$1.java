/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.neoforged.neoforge.attachment.IAttachmentHolder
 *  net.neoforged.neoforge.attachment.IAttachmentSerializer
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.contraptions.minecart.capability;

import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

class MinecartController.Type.1
implements IAttachmentSerializer<CompoundTag, MinecartController> {
    MinecartController.Type.1() {
    }

    @NotNull
    public MinecartController read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        return MinecartController.EMPTY;
    }

    public CompoundTag write(@NotNull MinecartController attachment, @NotNull HolderLookup.Provider provider) {
        return attachment.serializeNBT(provider);
    }
}

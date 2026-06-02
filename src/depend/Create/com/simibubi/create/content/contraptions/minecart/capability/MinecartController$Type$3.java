/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.neoforged.neoforge.attachment.IAttachmentHolder
 *  net.neoforged.neoforge.attachment.IAttachmentSerializer
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.minecart.capability;

import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MinecartController.Type.3
implements IAttachmentSerializer<CompoundTag, MinecartController> {
    MinecartController.Type.3() {
    }

    @NotNull
    public MinecartController read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        return (MinecartController)MinecartController.Type.valueOf(tag.getString("Type")).getSerializer().read(holder, (Tag)tag, provider);
    }

    @Nullable
    public CompoundTag write(MinecartController attachment, @NotNull HolderLookup.Provider provider) {
        CompoundTag tag = attachment.serializeNBT(provider);
        if (tag != null) {
            tag.putString("Type", attachment.getType().name());
        }
        return tag;
    }
}

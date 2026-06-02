/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.StringRepresentable
 *  net.neoforged.neoforge.attachment.IAttachmentHolder
 *  net.neoforged.neoforge.attachment.IAttachmentSerializer
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.minecart.capability;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

protected static enum MinecartController.Type implements StringRepresentable
{
    EMPTY(new IAttachmentSerializer<CompoundTag, MinecartController>(){

        @NotNull
        public MinecartController read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
            return MinecartController.EMPTY;
        }

        public CompoundTag write(@NotNull MinecartController attachment, @NotNull HolderLookup.Provider provider) {
            return attachment.serializeNBT(provider);
        }
    }),
    NORMAL(new IAttachmentSerializer<CompoundTag, MinecartController>(){

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
    });

    public static final Codec<MinecartController.Type> CODEC;
    private final IAttachmentSerializer<CompoundTag, MinecartController> serializer;
    private static final IAttachmentSerializer<CompoundTag, MinecartController> SERIALIZER;

    private MinecartController.Type(IAttachmentSerializer<CompoundTag, MinecartController> serializer) {
        this.serializer = serializer;
    }

    public IAttachmentSerializer<CompoundTag, MinecartController> getSerializer() {
        return this.serializer;
    }

    @NotNull
    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    static {
        CODEC = StringRepresentable.fromValues(MinecartController.Type::values);
        SERIALIZER = new IAttachmentSerializer<CompoundTag, MinecartController>(){

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
        };
    }
}

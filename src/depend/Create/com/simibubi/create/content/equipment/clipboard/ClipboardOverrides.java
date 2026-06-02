/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateItemModelProvider
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.client.renderer.item.ItemProperties
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.Item
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.model.generators.ItemModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 *  net.neoforged.neoforge.client.model.generators.ModelFile$UncheckedModelFile
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.equipment.clipboard;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockItem;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.NotNull;

public class ClipboardOverrides {
    @OnlyIn(value=Dist.CLIENT)
    public static void registerModelOverridesClient(ClipboardBlockItem item) {
        ItemProperties.register((Item)item, (ResourceLocation)ClipboardType.ID, (pStack, pLevel, pEntity, pSeed) -> ((ClipboardContent)pStack.getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, (Object)ClipboardContent.EMPTY)).type().ordinal());
    }

    public static ItemModelBuilder addOverrideModels(DataGenContext<Item, ClipboardBlockItem> c, RegistrateItemModelProvider p) {
        ItemModelBuilder builder = p.generated(() -> c.get());
        for (ClipboardType type : ClipboardType.values()) {
            int i = type.ordinal();
            builder.override().predicate(ClipboardType.ID, (float)i).model((ModelFile)((ItemModelBuilder)((ItemModelBuilder)p.getBuilder(c.getName() + "_" + i)).parent((ModelFile)new ModelFile.UncheckedModelFile("item/generated"))).texture("layer0", Create.asResource("item/" + type.file))).end();
        }
        return builder;
    }

    public static enum ClipboardType implements StringRepresentable
    {
        EMPTY("empty_clipboard"),
        WRITTEN("clipboard"),
        EDITING("clipboard_and_quill");

        public static final Codec<ClipboardType> CODEC;
        public static final StreamCodec<ByteBuf, ClipboardType> STREAM_CODEC;
        public final String file;
        public static ResourceLocation ID;

        private ClipboardType(String file) {
            this.file = file;
        }

        @NotNull
        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }

        static {
            CODEC = StringRepresentable.fromValues(ClipboardType::values);
            STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(ClipboardType.class);
            ID = Create.asResource("clipboard_type");
        }
    }
}

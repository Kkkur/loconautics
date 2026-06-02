/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  net.minecraft.SharedConstants
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackLocationInfo
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.PackResources$ResourceOutput
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.metadata.MetadataSectionSerializer
 *  net.minecraft.server.packs.metadata.pack.PackMetadataSection
 *  net.minecraft.server.packs.repository.PackSource
 *  net.minecraft.server.packs.resources.IoSupplier
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.pack;

import com.google.gson.JsonElement;
import com.simibubi.create.Create;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicPack
implements PackResources {
    private final Map<String, IoSupplier<InputStream>> files = new HashMap<String, IoSupplier<InputStream>>();
    private final String packId;
    private final PackType packType;
    private final PackMetadataSection metadata;
    private final PackLocationInfo packLocationInfo;

    public DynamicPack(String packId, PackType packType) {
        this.packId = packId;
        this.packType = packType;
        this.metadata = new PackMetadataSection((Component)Component.empty(), SharedConstants.getCurrentVersion().getPackVersion(packType));
        this.packLocationInfo = new PackLocationInfo(packId, (Component)Component.literal((String)packId), PackSource.BUILT_IN, Optional.empty());
    }

    private static String getPath(PackType packType, ResourceLocation resourceLocation) {
        return packType.getDirectory() + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath();
    }

    public DynamicPack put(ResourceLocation location, IoSupplier<InputStream> stream) {
        this.files.put(DynamicPack.getPath(this.packType, location), stream);
        return this;
    }

    public DynamicPack put(ResourceLocation location, byte[] bytes) {
        return this.put(location, (IoSupplier<InputStream>)((IoSupplier)() -> new ByteArrayInputStream(bytes)));
    }

    public DynamicPack put(ResourceLocation location, String string) {
        return this.put(location, string.getBytes(StandardCharsets.UTF_8));
    }

    public DynamicPack put(ResourceLocation location, JsonElement json) {
        return this.put(location.withSuffix(".json"), Create.GSON.toJson(json));
    }

    @Nullable
    public IoSupplier<InputStream> getRootResource(String ... elements) {
        return this.files.getOrDefault(String.join((CharSequence)"/", elements), null);
    }

    @Nullable
    public IoSupplier<InputStream> getResource(@NotNull PackType packType, @NotNull ResourceLocation resourceLocation) {
        return this.files.getOrDefault(DynamicPack.getPath(packType, resourceLocation), null);
    }

    public void listResources(@NotNull PackType packType, @NotNull String namespace, @NotNull String path, @NotNull PackResources.ResourceOutput resourceOutput) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath((String)namespace, (String)path);
        String directoryAndNamespace = packType.getDirectory() + "/" + namespace + "/";
        String prefix = directoryAndNamespace + path + "/";
        this.files.forEach((filePath, streamSupplier) -> {
            if (filePath.startsWith(prefix)) {
                resourceOutput.accept((Object)resourceLocation.withPath(filePath.substring(directoryAndNamespace.length())), streamSupplier);
            }
        });
    }

    @NotNull
    public Set<String> getNamespaces(PackType packType) {
        HashSet<String> namespaces = new HashSet<String>();
        String dir = packType.getDirectory() + "/";
        for (String path : this.files.keySet()) {
            String relative;
            if (!path.startsWith(dir) || !(relative = path.substring(dir.length())).contains("/")) continue;
            namespaces.add(relative.substring(0, relative.indexOf("/")));
        }
        return namespaces;
    }

    @Nullable
    public <T> T getMetadataSection(@NotNull MetadataSectionSerializer<T> deserializer) throws IOException {
        return (T)(deserializer == PackMetadataSection.TYPE ? this.metadata : null);
    }

    @NotNull
    public PackLocationInfo location() {
        return this.packLocationInfo;
    }

    @NotNull
    public String packId() {
        return this.packId;
    }

    public void close() {
    }
}

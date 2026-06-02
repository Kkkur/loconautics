/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.packs.PackLocationInfo
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.PackSelectionConfig
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.repository.Pack
 *  net.minecraft.server.packs.repository.Pack$Metadata
 *  net.minecraft.server.packs.repository.Pack$Position
 *  net.minecraft.server.packs.repository.Pack$ResourcesSupplier
 *  net.minecraft.server.packs.repository.PackSource
 *  net.minecraft.server.packs.repository.RepositorySource
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.pack;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;

public record DynamicPackSource(String packId, PackType packType, Pack.Position packPosition, PackResources packResources) implements RepositorySource
{
    public void loadPacks(@NotNull Consumer<Pack> onLoad) {
        PackLocationInfo locationInfo = new PackLocationInfo(this.packId, (Component)Component.literal((String)this.packId), PackSource.BUILT_IN, Optional.empty());
        PackSelectionConfig selectionConfig = new PackSelectionConfig(true, this.packPosition, true);
        Pack.ResourcesSupplier resourcesSupplier = new Pack.ResourcesSupplier(){

            @NotNull
            public PackResources openPrimary(@NotNull PackLocationInfo packLocationInfo) {
                return DynamicPackSource.this.packResources;
            }

            @NotNull
            public PackResources openFull(@NotNull PackLocationInfo packLocationInfo, @NotNull Pack.Metadata metadata) {
                return DynamicPackSource.this.packResources;
            }
        };
        onLoad.accept(Pack.readMetaAndCreate((PackLocationInfo)locationInfo, (Pack.ResourcesSupplier)resourcesSupplier, (PackType)this.packType, (PackSelectionConfig)selectionConfig));
    }
}

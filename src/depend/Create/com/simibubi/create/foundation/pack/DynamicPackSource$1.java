/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.PackLocationInfo
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.repository.Pack$Metadata
 *  net.minecraft.server.packs.repository.Pack$ResourcesSupplier
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.pack;

import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import org.jetbrains.annotations.NotNull;

class DynamicPackSource.1
implements Pack.ResourcesSupplier {
    DynamicPackSource.1() {
    }

    @NotNull
    public PackResources openPrimary(@NotNull PackLocationInfo packLocationInfo) {
        return DynamicPackSource.this.packResources;
    }

    @NotNull
    public PackResources openFull(@NotNull PackLocationInfo packLocationInfo, @NotNull Pack.Metadata metadata) {
        return DynamicPackSource.this.packResources;
    }
}

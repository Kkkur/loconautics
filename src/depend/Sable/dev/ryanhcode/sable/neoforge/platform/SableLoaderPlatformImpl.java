/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.fml.loading.LoadingModList
 */
package dev.ryanhcode.sable.neoforge.platform;

import dev.ryanhcode.sable.platform.SableLoaderPlatform;
import net.neoforged.fml.loading.LoadingModList;

public class SableLoaderPlatformImpl
implements SableLoaderPlatform {
    @Override
    public String getModVersion(String modId) {
        return LoadingModList.get().getModFileById(modId).versionString();
    }
}

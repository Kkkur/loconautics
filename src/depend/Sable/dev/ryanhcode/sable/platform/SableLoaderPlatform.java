/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.platform;

import dev.ryanhcode.sable.platform.SablePlatformUtil;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SableLoaderPlatform {
    public static final SableLoaderPlatform INSTANCE = SablePlatformUtil.load(SableLoaderPlatform.class);

    public String getModVersion(String var1);
}

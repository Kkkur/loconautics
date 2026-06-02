/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.platform;

import dev.ryanhcode.sable.platform.SablePlatformUtil;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SableAssemblyPlatform {
    public static final SableAssemblyPlatform INSTANCE = SablePlatformUtil.load(SableAssemblyPlatform.class);

    public void setIgnoreOnPlace(Level var1, boolean var2);
}

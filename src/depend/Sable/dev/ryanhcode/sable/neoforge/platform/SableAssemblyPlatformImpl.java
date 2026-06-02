/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.neoforge.platform;

import dev.ryanhcode.sable.platform.SableAssemblyPlatform;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SableAssemblyPlatformImpl
implements SableAssemblyPlatform {
    @Override
    public void setIgnoreOnPlace(Level level, boolean ignore) {
        level.captureBlockSnapshots = ignore;
    }
}

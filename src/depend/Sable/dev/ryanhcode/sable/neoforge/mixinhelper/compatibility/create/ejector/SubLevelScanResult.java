/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.BlockHitResult
 */
package dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.ejector;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.world.phys.BlockHitResult;

public record SubLevelScanResult(BlockHitResult result, ServerSubLevel serverSubLevel) {
}

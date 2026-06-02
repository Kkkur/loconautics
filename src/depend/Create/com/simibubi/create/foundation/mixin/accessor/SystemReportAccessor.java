/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.SystemReport
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.simibubi.create.foundation.mixin.accessor;

import java.util.Map;
import net.minecraft.SystemReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SystemReport.class})
public interface SystemReportAccessor {
    @Accessor
    public static String getOPERATING_SYSTEM() {
        throw new AssertionError();
    }

    @Accessor
    public static String getJAVA_VERSION() {
        throw new AssertionError();
    }

    @Accessor
    public Map<String, String> getEntries();
}

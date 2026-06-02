/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ShaderInstance
 */
package dev.ryanhcode.sable.compatibility;

import dev.ryanhcode.sable.mixinterface.compatibility.iris.ExtendedShaderExtension;
import net.minecraft.client.renderer.ShaderInstance;

public class SableIrisCompat {
    public static void refreshModelMatrices(ShaderInstance shader) {
        if (shader instanceof ExtendedShaderExtension) {
            ExtendedShaderExtension ext = (ExtendedShaderExtension)shader;
            ext.sable$refreshModelMatrices();
        }
    }
}

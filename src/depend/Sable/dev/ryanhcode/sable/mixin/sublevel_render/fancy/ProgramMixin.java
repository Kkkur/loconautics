/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.Program
 *  org.lwjgl.opengl.GL20C
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.sublevel_render.fancy;

import com.mojang.blaze3d.shaders.Program;
import dev.ryanhcode.sable.mixinterface.sublevel_render.fancy.ProgramExtension;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={Program.class})
public class ProgramMixin
implements ProgramExtension {
    @Shadow
    private int id;

    @Override
    public String sable$getSource() {
        return GL20C.glGetShaderSource((int)this.id);
    }
}

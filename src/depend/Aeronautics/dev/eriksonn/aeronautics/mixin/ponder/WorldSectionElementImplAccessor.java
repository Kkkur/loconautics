/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.foundation.element.WorldSectionElementImpl
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.eriksonn.aeronautics.mixin.ponder;

import net.createmod.ponder.foundation.element.WorldSectionElementImpl;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={WorldSectionElementImpl.class})
public interface WorldSectionElementImplAccessor {
    @Accessor
    public Vec3 getCenterOfRotation();
}

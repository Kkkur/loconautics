/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.foundation.element.ParrotElementImpl
 *  net.minecraft.world.entity.animal.Parrot
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.simulated_team.simulated.mixin.accessor;

import net.createmod.ponder.foundation.element.ParrotElementImpl;
import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ParrotElementImpl.class})
public interface ParrotElementAccessor {
    @Accessor
    public Parrot getEntity();
}

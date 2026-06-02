/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.fan.NozzleBlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.nozzle;

import com.simibubi.create.content.kinetics.fan.NozzleBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={NozzleBlockEntity.class})
public interface NozzleBlockEntityAccessor {
    @Accessor
    public float getRange();
}

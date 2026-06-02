/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.dimension.end.EndDragonFight$Data
 */
package dev.simulated_team.simulated.mixin_interface;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.end.EndDragonFight;

public interface PrimaryLevelDataExtension {
    public ResourceLocation getPreset();

    public void setPreset(ResourceLocation var1);

    public void setEndDragonFight(EndDragonFight.Data var1);
}

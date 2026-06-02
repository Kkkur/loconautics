/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.TooltipFlag$Default
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.simulated_team.simulated.mixin.tooltip_flag;

import dev.simulated_team.simulated.mixin_interface.tooltip_flag.TooltipFlagExtension;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={TooltipFlag.Default.class})
public class TooltipFlagDefaultMixin
implements TooltipFlagExtension {
    @Unique
    private boolean sable$creativeSearch;

    @Override
    public boolean simulated$getCreativeSearch() {
        return this.sable$creativeSearch;
    }

    @Override
    public void simulated$setCreativeSearch(boolean value) {
        this.sable$creativeSearch = value;
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.SessionSearchTrees
 *  net.minecraft.world.item.TooltipFlag
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package dev.simulated_team.simulated.mixin.tooltip_flag;

import dev.simulated_team.simulated.mixin_interface.tooltip_flag.TooltipFlagExtension;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={SessionSearchTrees.class})
public class SessionSearchTreesMixin {
    @ModifyVariable(method={"lambda$updateCreativeTooltips$15"}, at=@At(value="STORE"))
    private static TooltipFlag markAsCreativeSearch(TooltipFlag value) {
        ((TooltipFlagExtension)value).simulated$setCreativeSearch(true);
        return value;
    }
}

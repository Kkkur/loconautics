/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.DebugScreenOverlay
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package dev.ryanhcode.sable.mixin.debug_render;

import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={DebugScreenOverlay.class})
public abstract class DebugScreenOverlayMixin {
    @Shadow
    protected abstract Level getLevel();

    @ModifyVariable(method={"getSystemInformation"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;showOnlyReducedInfo()Z", shift=At.Shift.BEFORE), ordinal=0)
    public List<String> sable$addDebugInfo(List<String> value) {
        ClientSubLevelContainer container = SubLevelContainer.getContainer(Minecraft.getInstance().level);
        value.add("");
        value.add(String.valueOf(ChatFormatting.UNDERLINE) + "Sable");
        if (container instanceof ClientSubLevelContainer) {
            ClientSubLevelContainer clientContainer = container;
            clientContainer.addDebugInfo(value::add);
        }
        SubLevelRenderDispatcher.get().addDebugInfo(value::add);
        return value;
    }
}

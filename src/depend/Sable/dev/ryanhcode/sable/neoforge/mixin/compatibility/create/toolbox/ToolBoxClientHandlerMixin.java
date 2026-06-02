/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.toolbox.ToolboxHandlerClient
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.toolbox;

import com.simibubi.create.content.equipment.toolbox.ToolboxHandlerClient;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ToolboxHandlerClient.class})
public class ToolBoxClientHandlerMixin {
    @Redirect(method={"*"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/equipment/toolbox/ToolboxHandler;distance(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;)D"))
    private static double sable$sublevelDistance(Vec3 location, BlockPos p) {
        return Sable.HELPER.distanceSquaredWithSubLevels((Level)Minecraft.getInstance().level, (Position)location, (double)p.getX() + 0.5, (double)p.getY(), (double)p.getZ() + 0.5);
    }
}

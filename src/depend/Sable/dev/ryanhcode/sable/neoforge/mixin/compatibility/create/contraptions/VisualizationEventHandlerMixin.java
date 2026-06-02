/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.impl.visualization.VisualizationEventHandler
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.contraptions;

import dev.engine_room.flywheel.impl.visualization.VisualizationEventHandler;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.neoforge.compatibility.flywheel.FlywheelCompatNeoForge;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={VisualizationEventHandler.class})
public class VisualizationEventHandlerMixin {
    @Inject(method={"onEntityJoinLevel"}, at={@At(value="TAIL")})
    private static void sable$onEntityJoinLevel(Level level, Entity entity, CallbackInfo ci) {
        SubLevel subLevel = Sable.HELPER.getContaining(entity);
        if (subLevel != null) {
            FlywheelCompatNeoForge.createRenderInfo(level, subLevel);
        }
    }
}

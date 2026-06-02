/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.impl.visualization.VisualizationManagerImpl
 *  net.minecraft.world.level.LevelAccessor
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.simulated_team.simulated.neoforge.mixin.diagram;

import dev.engine_room.flywheel.impl.visualization.VisualizationManagerImpl;
import dev.simulated_team.simulated.mixin_interface.diagram.VisualizationManagerExtension;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={VisualizationManagerImpl.class})
public class VisualizationManagerImplMixin
implements VisualizationManagerExtension {
    @Unique
    private static boolean sable$drawingDiagram = false;

    @Inject(method={"supportsVisualization"}, at={@At(value="HEAD")}, cancellable=true)
    private static void simulated$supportsVisualization(LevelAccessor level, CallbackInfoReturnable<Boolean> cir) {
        if (sable$drawingDiagram) {
            cir.setReturnValue((Object)false);
        }
    }

    @Override
    public void sable$setDrawingDiagram(boolean drawing) {
        sable$drawingDiagram = drawing;
    }
}

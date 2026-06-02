/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.client.render.shader.processor.ShaderPreProcessor
 *  foundry.veil.api.event.VeilRenderLevelStageEvent$Stage
 *  foundry.veil.platform.VeilEventPlatform
 *  net.createmod.catnip.render.SuperByteBufferCache
 *  net.createmod.ponder.api.registration.PonderPlugin
 *  net.createmod.ponder.foundation.PonderIndex
 */
package dev.simulated_team.simulated;

import dev.simulated_team.simulated.client.BlockPropertiesTooltip;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelRenderer;
import dev.simulated_team.simulated.content.end_sea.EndSeaFadeTransformer;
import dev.simulated_team.simulated.content.end_sea.EndSeaShadowRenderer;
import dev.simulated_team.simulated.content.items.merging_glue.MergingGlueItemHandler;
import dev.simulated_team.simulated.content.items.plunger_launcher.PlungerLauncherItemRenderer;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffClientHandler;
import dev.simulated_team.simulated.events.SimulatedCommonClientEvents;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.index.SimRenderTypes;
import dev.simulated_team.simulated.index.SimResourceManagers;
import dev.simulated_team.simulated.index.ponder.SimPonderPlugin;
import foundry.veil.api.client.render.shader.processor.ShaderPreProcessor;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;

public class SimulatedClient {
    public static final PhysicsStaffClientHandler PHYSICS_STAFF_CLIENT_HANDLER = new PhysicsStaffClientHandler();
    public static PlungerLauncherItemRenderer.RenderHandler PLUNGER_LAUNCHER_RENDER_HANDLER = new PlungerLauncherItemRenderer.RenderHandler();
    public static final MergingGlueItemHandler MERGING_GLUE_ITEM_HANDLER = new MergingGlueItemHandler();

    public static void init() {
        SimPartialModels.init();
        BlockPropertiesTooltip.init();
        SimResourceManagers.init();
        PonderIndex.addPlugin((PonderPlugin)new SimPonderPlugin());
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(EndSeaShadowRenderer::renderShadowMap);
        VeilEventPlatform.INSTANCE.onVeilAddShaderProcessors((provider, registry) -> registry.addPreprocessor((ShaderPreProcessor)new EndSeaFadeTransformer(), false));
        VeilEventPlatform.INSTANCE.onVeilRegisterFixedBuffers(registry -> {
            registry.registerFixedBuffer(VeilRenderLevelStageEvent.Stage.AFTER_PARTICLES, SimRenderTypes.laser());
            registry.registerFixedBuffer(VeilRenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS, SimRenderTypes.lens());
            registry.registerFixedBuffer(VeilRenderLevelStageEvent.Stage.AFTER_LEVEL, SimRenderTypes.staffOverlay());
        });
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(SimulatedCommonClientEvents::onRenderLevelStage);
        SuperByteBufferCache.getInstance().registerCompartment(SteeringWheelRenderer.STEERING_WHEEL);
    }
}

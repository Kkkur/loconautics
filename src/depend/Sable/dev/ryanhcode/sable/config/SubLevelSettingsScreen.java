/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.OptionInstance
 *  net.minecraft.client.OptionInstance$IntRange
 *  net.minecraft.client.OptionInstance$ValueSet
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.options.OptionsSubScreen
 *  net.minecraft.client.server.IntegratedServer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 */
package dev.ryanhcode.sable.config;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.physics.config.PhysicsConfigData;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class SubLevelSettingsScreen
extends OptionsSubScreen {
    public static final Component TITLE = Component.translatable((String)"options.sable_menu");

    public SubLevelSettingsScreen(Screen optionsScreen, Options options, Component component) {
        super(optionsScreen, options, component);
    }

    protected void addOptions() {
        IntegratedServer singleplayerServer = this.minecraft.getSingleplayerServer();
        this.list.addBig(new OptionInstance("options.physics_steps", OptionInstance.cachedConstantTooltip((Component)Component.translatable((String)"options.physics_steps.tooltip")), (component, substeps) -> Options.genericValueLabel((Component)component, (Component)Component.translatable((String)"options.physics_steps_template", (Object[])new Object[]{substeps * 20})), (OptionInstance.ValueSet)new OptionInstance.IntRange(1, 10, false), (Object)SubLevelContainer.getContainer((ServerLevel)singleplayerServer.overworld()).physicsSystem().getConfig().substepsPerTick, steps -> {
            for (ServerLevel level : singleplayerServer.getAllLevels()) {
                SubLevelPhysicsSystem physicsSystem = SubLevelContainer.getContainer(level).physicsSystem();
                PhysicsConfigData config = physicsSystem.getConfig();
                config.substepsPerTick = steps;
                physicsSystem.getPipeline().updateConfigFrom(config);
            }
        }));
    }
}

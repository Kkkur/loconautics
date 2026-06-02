/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.common.NeoForge
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.neoforge.platform;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.neoforge.event.ForgeSablePostPhysicsTickEvent;
import dev.ryanhcode.sable.neoforge.event.ForgeSablePrePhysicsTickEvent;
import dev.ryanhcode.sable.neoforge.event.ForgeSableSubLevelContainerReadyEvent;
import dev.ryanhcode.sable.platform.SableEventPublishPlatform;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SableEventPublishPlatformImpl
implements SableEventPublishPlatform {
    @Override
    public void onSubLevelContainerReady(Level level, SubLevelContainer container) {
        NeoForge.EVENT_BUS.post((Event)new ForgeSableSubLevelContainerReadyEvent(level, container));
    }

    @Override
    public void prePhysicsTick(SubLevelPhysicsSystem physicsSystem, double timeStep) {
        NeoForge.EVENT_BUS.post((Event)new ForgeSablePrePhysicsTickEvent(physicsSystem, timeStep));
    }

    @Override
    public void postPhysicsTick(SubLevelPhysicsSystem physicsSystem, double timeStep) {
        NeoForge.EVENT_BUS.post((Event)new ForgeSablePostPhysicsTickEvent(physicsSystem, timeStep));
    }
}

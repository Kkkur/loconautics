/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.platform;

import dev.ryanhcode.sable.api.event.SablePostPhysicsTickEvent;
import dev.ryanhcode.sable.api.event.SablePrePhysicsTickEvent;
import dev.ryanhcode.sable.api.event.SableSubLevelContainerReadyEvent;
import dev.ryanhcode.sable.platform.SablePlatformUtil;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SableEventPublishPlatform
extends SableSubLevelContainerReadyEvent,
SablePrePhysicsTickEvent,
SablePostPhysicsTickEvent {
    public static final SableEventPublishPlatform INSTANCE = SablePlatformUtil.load(SableEventPublishPlatform.class);
}

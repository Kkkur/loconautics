/*
 * Decompiled with CFR 0.152.
 */
package dev.simulated_team.simulated.service;

import dev.simulated_team.simulated.service.ServiceUtil;

public interface SimPlatformService {
    public static final SimPlatformService INSTANCE = ServiceUtil.load(SimPlatformService.class);

    public boolean isLoaded(String var1);
}

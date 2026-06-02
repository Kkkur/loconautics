/*
 * Decompiled with CFR 0.152.
 */
package dev.simulated_team.simulated.service;

import dev.simulated_team.simulated.config.client.SimClient;
import dev.simulated_team.simulated.config.server.SimServer;
import dev.simulated_team.simulated.service.ServiceUtil;

public interface SimConfigService {
    public static final SimConfigService INSTANCE = ServiceUtil.load(SimConfigService.class);

    public SimServer server();

    public boolean serverLoaded();

    public SimClient client();

    public boolean clientLoaded();
}

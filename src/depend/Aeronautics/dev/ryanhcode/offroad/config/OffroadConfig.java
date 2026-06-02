/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.simulated_team.simulated.service.ServiceUtil
 */
package dev.ryanhcode.offroad.config;

import dev.ryanhcode.offroad.config.client.OffroadClientConfig;
import dev.ryanhcode.offroad.config.server.OffroadServer;
import dev.simulated_team.simulated.service.ServiceUtil;

public interface OffroadConfig {
    public static final OffroadConfig INSTANCE = (OffroadConfig)ServiceUtil.load(OffroadConfig.class);

    public static OffroadServer server() {
        return INSTANCE.getServerConfig();
    }

    public static OffroadClientConfig client() {
        return INSTANCE.getClientConfig();
    }

    public OffroadServer getServerConfig();

    public OffroadClientConfig getClientConfig();
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.simulated_team.simulated.service.ServiceUtil
 */
package dev.eriksonn.aeronautics.config;

import dev.eriksonn.aeronautics.config.client.AeroClient;
import dev.eriksonn.aeronautics.config.server.AeroServer;
import dev.simulated_team.simulated.service.ServiceUtil;

public interface AeroConfig {
    public static final AeroConfig INSTANCE = (AeroConfig)ServiceUtil.load(AeroConfig.class);

    public static AeroServer server() {
        return INSTANCE.getServerConfig();
    }

    public static AeroClient client() {
        return INSTANCE.getClientConfig();
    }

    public AeroServer getServerConfig();

    public AeroClient getClientConfig();
}

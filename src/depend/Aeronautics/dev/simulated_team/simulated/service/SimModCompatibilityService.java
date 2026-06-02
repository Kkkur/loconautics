/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.simulated_team.simulated.service;

import dev.simulated_team.simulated.service.SimPlatformService;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import org.jetbrains.annotations.ApiStatus;

public interface SimModCompatibilityService {
    public void init();

    public String getModId();

    @ApiStatus.Internal
    public static void initLoaded() {
        ServiceLoader<SimModCompatibilityService> loader = ServiceLoader.load(SimModCompatibilityService.class);
        Iterator<SimModCompatibilityService> iterator = loader.iterator();
        while (iterator.hasNext()) {
            try {
                SimModCompatibilityService service = iterator.next();
                if (!SimPlatformService.INSTANCE.isLoaded(service.getModId())) continue;
                service.init();
            }
            catch (NoClassDefFoundError | ServiceConfigurationError error) {}
        }
    }
}

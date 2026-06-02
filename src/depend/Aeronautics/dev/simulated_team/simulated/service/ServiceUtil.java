/*
 * Decompiled with CFR 0.152.
 */
package dev.simulated_team.simulated.service;

import java.util.ServiceLoader;

public class ServiceUtil {
    public static <T> T load(Class<T> tClass) {
        return ServiceLoader.load(tClass).findFirst().orElseThrow(() -> new RuntimeException("Unable to find %s implementation".formatted(tClass.getName())));
    }
}

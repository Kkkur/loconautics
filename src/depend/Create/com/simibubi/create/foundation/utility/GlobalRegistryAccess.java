/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.server.MinecraftServer
 *  net.neoforged.neoforge.server.ServerLifecycleHooks
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.utility;

import java.util.function.Supplier;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public final class GlobalRegistryAccess {
    private static Supplier<@Nullable RegistryAccess> supplier;

    @Nullable
    public static RegistryAccess get() {
        return supplier.get();
    }

    public static RegistryAccess getOrThrow() {
        RegistryAccess registryAccess = GlobalRegistryAccess.get();
        if (registryAccess == null) {
            throw new IllegalStateException("Could not get RegistryAccess");
        }
        return registryAccess;
    }

    static {
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> {
            supplier = () -> {
                ClientPacketListener packetListener = Minecraft.getInstance().getConnection();
                if (packetListener == null) {
                    return null;
                }
                return packetListener.registryAccess();
            };
        });
        if (supplier == null) {
            supplier = () -> {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server == null) {
                    return null;
                }
                return server.registryAccess();
            };
        }
    }
}

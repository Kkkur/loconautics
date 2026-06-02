/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.attachment.AttachmentType
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.NeoForgeRegistries
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.simibubi.create;

import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

public class AllAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create((Registry)NeoForgeRegistries.ATTACHMENT_TYPES, (String)"create");
    public static final Supplier<AttachmentType<MinecartController>> MINECART_CONTROLLER = ATTACHMENT_TYPES.register("minecart_controller", () -> AttachmentType.builder(() -> MinecartController.EMPTY).serialize(MinecartController.SERIALIZER).build());

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.createmod.ponder.api.registration.PonderTagRegistrationHelper
 *  net.minecraft.resources.ResourceLocation
 */
package dev.ryanhcode.offroad.content.ponder;

import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class OffroadPonderTags {
    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper itemHelper = helper.withKeyFunction(RegisteredObjectsHelper::getKeyOrThrow);
    }
}

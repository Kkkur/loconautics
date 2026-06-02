/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.ponder.CreatePonderPlugin
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.createmod.ponder.api.registration.IndexExclusionHelper
 *  net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
 *  net.createmod.ponder.api.registration.PonderTagRegistrationHelper
 *  net.createmod.ponder.api.registration.SharedTextRegistrationHelper
 *  net.minecraft.resources.ResourceLocation
 */
package dev.ryanhcode.offroad.content.ponder;

import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import dev.ryanhcode.offroad.content.ponder.OffroadPonderTags;
import dev.ryanhcode.offroad.index.OffroadPonderScenes;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.IndexExclusionHelper;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.registration.SharedTextRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class OffroadPonderPlugin
extends CreatePonderPlugin {
    public String getModId() {
        return "offroad";
    }

    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        OffroadPonderScenes.register(helper);
    }

    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        OffroadPonderTags.register(helper);
    }

    public void registerSharedText(SharedTextRegistrationHelper helper) {
    }

    public void onPonderLevelRestore(PonderLevel ponderLevel) {
    }

    public void indexExclusions(IndexExclusionHelper helper) {
    }
}

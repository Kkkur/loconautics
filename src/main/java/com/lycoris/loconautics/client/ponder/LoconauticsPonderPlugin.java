package com.lycoris.loconautics.client.ponder;

import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import dev.simulated_team.simulated.ponder.scenes.RopeScenes;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

/**
 * Registers Loconautics items into the Ponder index.
 *
 * getModId() returns "simulated" so that addStoryBoard("rope", ...) resolves to
 * simulated:ponder/rope.nbt — the existing scene files from Simulated — instead of
 * looking for loconautics:ponder/rope.nbt which doesn't exist.
 */
public class LoconauticsPonderPlugin extends CreatePonderPlugin {

    @Override
    public String getModId() {
        // Scene paths ("rope") resolve against this namespace, so we use "simulated"
        // to reuse its existing NBT scene files without duplicating them.
        return "simulated";
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        // forComponents(ResourceLocation...) — pass the item's registry location directly
        helper.forComponents(LoconauticsRegistries.STEEL_CABLE.getId())
                .addStoryBoard("rope", RopeScenes::ropeIntro)
                .addStoryBoard("rope", RopeScenes::ropeConnections);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
    }
}
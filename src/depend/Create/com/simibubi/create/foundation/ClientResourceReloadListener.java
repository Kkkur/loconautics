/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 */
package com.simibubi.create.foundation;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.logistics.tableCloth.TableClothModel;
import com.simibubi.create.foundation.sound.SoundScapes;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ClientResourceReloadListener
implements ResourceManagerReloadListener {
    public void onResourceManagerReload(ResourceManager resourceManager) {
        CreateClient.invalidateRenderers();
        SoundScapes.invalidateAll();
        BeltHelper.uprightCache.clear();
        TableClothModel.reload();
    }
}

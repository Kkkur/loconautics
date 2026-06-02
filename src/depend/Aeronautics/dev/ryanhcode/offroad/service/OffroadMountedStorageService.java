/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.MountedStorageManager
 *  dev.simulated_team.simulated.service.ServiceUtil
 */
package dev.ryanhcode.offroad.service;

import com.simibubi.create.content.contraptions.MountedStorageManager;
import dev.simulated_team.simulated.service.ServiceUtil;

public interface OffroadMountedStorageService {
    public static final OffroadMountedStorageService INSTANCE = (OffroadMountedStorageService)ServiceUtil.load(OffroadMountedStorageService.class);

    public <T extends MountedStorageManager> T getSidedBoreheadContraptionMountedStorage();
}

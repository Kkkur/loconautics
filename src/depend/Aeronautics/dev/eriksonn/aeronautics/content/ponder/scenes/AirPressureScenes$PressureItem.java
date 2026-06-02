/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.ponder.scenes;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public static interface AirPressureScenes.PressureItem {
    default public int getSceneOffset() {
        return 0;
    }

    public String getSceneId();

    public String getSceneTitle();

    public void setup(CreateSceneBuilder var1, SceneBuildingUtil var2);

    public String getItemName();

    public String getForceName();

    public Vec3 getPointingPos();

    public BlockPos getSensorPos();

    public void decreasePower(CreateSceneBuilder var1, SceneBuildingUtil var2);
}

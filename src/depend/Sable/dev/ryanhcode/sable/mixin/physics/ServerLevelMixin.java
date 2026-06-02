/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.ryanhcode.sable.mixin.physics;

import dev.ryanhcode.sable.mixinterface.physics.ServerLevelSceneExtension;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={ServerLevel.class})
public class ServerLevelMixin
implements ServerLevelSceneExtension {
    @Unique
    private int sable$sceneID = -1;

    @Override
    public int sable$getSceneID() {
        return this.sable$sceneID;
    }

    @Override
    public void sable$setSceneID(int sable$sceneID) {
        this.sable$sceneID = sable$sceneID;
    }
}

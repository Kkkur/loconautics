/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  net.minecraft.gametest.framework.GameTestInfo
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.game_test;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GameTestInfo.class})
public abstract class GameTestInfoMixin {
    @Shadow
    public abstract ServerLevel getLevel();

    @Inject(method={"succeed"}, at={@At(value="INVOKE", target="Lnet/minecraft/server/level/ServerLevel;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;")})
    public void removeSublevels(CallbackInfo ci, @Local AABB aabb) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.getLevel());
        if (container != null) {
            for (SubLevel subLevel : container.queryIntersecting((BoundingBox3dc)new BoundingBox3d(aabb))) {
                container.removeSubLevel(subLevel, SubLevelRemovalReason.REMOVED);
            }
        }
    }
}

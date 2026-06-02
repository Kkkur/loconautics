/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  net.minecraft.gametest.framework.GameTestInfo
 *  net.minecraft.gametest.framework.TestCommand
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.game_test;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={TestCommand.class})
public class TestCommandMixin {
    @Inject(method={"resetGameTestInfo"}, at={@At(value="HEAD")})
    private static void resetGameTestInfo(GameTestInfo gameTestInfo, CallbackInfoReturnable<Integer> cir) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(gameTestInfo.getLevel());
        if (container != null) {
            for (SubLevel subLevel : container.queryIntersecting((BoundingBox3dc)new BoundingBox3d(gameTestInfo.getStructureBounds()))) {
                container.removeSubLevel(subLevel, SubLevelRemovalReason.REMOVED);
            }
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  net.minecraft.gametest.framework.StructureUtils
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.game_test;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={StructureUtils.class})
public class StructureUtilsMixin {
    @Inject(method={"clearSpaceForStructure"}, at={@At(value="TAIL")})
    private static void clearSpaceForStructure(BoundingBox box, ServerLevel level, CallbackInfo ci) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container != null) {
            for (SubLevel subLevel : container.queryIntersecting((BoundingBox3dc)new BoundingBox3d(box))) {
                container.removeSubLevel(subLevel, SubLevelRemovalReason.REMOVED);
            }
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.entity.EntityAccess
 *  net.minecraft.world.level.entity.EntitySection
 *  net.minecraft.world.level.entity.EntitySectionStorage
 *  net.minecraft.world.level.entity.PersistentEntitySectionManager
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.entity.entity_unloading;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PersistentEntitySectionManager.class})
public class PersistentEntitySectionManagerMixin {
    @Shadow
    @Final
    public EntitySectionStorage<EntityAccess> sectionStorage;

    @Inject(method={"processChunkUnload"}, at={@At(value="HEAD")})
    private void processChunkUnload(long l, CallbackInfoReturnable<Boolean> cir) {
        List sections = this.sectionStorage.getExistingSectionsInChunk(l).toList();
        for (EntitySection section : sections) {
            List entities = section.getEntities().toList();
            for (EntityAccess entityAccess : entities) {
                Entity entity = (Entity)entityAccess;
                boolean inPlot = SubLevelContainer.getContainer(entity.level()).inBounds(entity.chunkPosition());
                if (!inPlot || entity.getRemovalReason() != null && !entity.getRemovalReason().shouldSave() || !entity.isVehicle() || !entity.hasExactlyOnePlayerPassenger()) continue;
                ((Entity)entity.getPassengers().getFirst()).removeVehicle();
            }
        }
    }
}

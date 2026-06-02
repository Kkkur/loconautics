/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.simibubi.create.content.contraptions.glue.SuperGlueEntity
 *  net.minecraft.util.AbortableIterationConsumer$Continuation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.entity.EntityTypeTest
 *  net.minecraft.world.level.entity.LevelEntityGetter
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.super_glue;

import com.google.common.collect.Lists;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import dev.ryanhcode.sable.neoforge.mixin.compatibility.create.super_glue.LevelAccessor;
import dev.ryanhcode.sable.util.SubLevelInclusiveLevelEntityGetter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={SuperGlueEntity.class}, remap=false)
public class SuperGlueEntityMixin {
    @Redirect(method={"collectCropped"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private static List sable$collectGlueEntities(Level instance, Class aClass, AABB aabb) {
        LevelEntityGetter<Entity> levelEntityGetter = ((LevelAccessor)instance).invokeGetEntities();
        if (levelEntityGetter instanceof SubLevelInclusiveLevelEntityGetter) {
            SubLevelInclusiveLevelEntityGetter getter = (SubLevelInclusiveLevelEntityGetter)levelEntityGetter;
            ArrayList list = Lists.newArrayList();
            getter.getIgnoringSubLevels(EntityTypeTest.forClass((Class)aClass), aabb, entity -> {
                list.add(entity);
                return AbortableIterationConsumer.Continuation.CONTINUE;
            });
            return list;
        }
        return instance.getEntitiesOfClass(aClass, aabb);
    }
}

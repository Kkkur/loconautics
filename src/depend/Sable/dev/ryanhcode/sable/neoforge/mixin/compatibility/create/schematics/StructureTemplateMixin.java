/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.schematics;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.schematics.StructureTemplateExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SableNBTUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={StructureTemplate.class})
public abstract class StructureTemplateMixin
implements StructureTemplateExtension {
    @Unique
    private final List<StructureTemplateExtension.SubLevelTemplate> sable$subLevelTemplates = new ObjectArrayList();

    @Inject(method={"load"}, at={@At(value="TAIL")})
    private void sable$load(HolderGetter<Block> holderGetter, CompoundTag tag, CallbackInfo ci) {
        ListTag subLevelTags = tag.getList("sub_levels", 10);
        for (Tag subLevelTag : subLevelTags) {
            CompoundTag subLevelCompound = (CompoundTag)subLevelTag;
            StructureTemplate t = new StructureTemplate();
            t.load(holderGetter, subLevelCompound);
            UUID uuid = subLevelCompound.getUUID("uuid");
            Vector3d position = SableNBTUtils.readVector3d(subLevelCompound.getCompound("position"));
            Quaterniond orientation = SableNBTUtils.readQuaternion(subLevelCompound.getCompound("orientation"));
            this.sable$subLevelTemplates.add(new StructureTemplateExtension.SubLevelTemplate(uuid, position, orientation, t));
        }
    }

    @Inject(method={"fillEntityList"}, at={@At(value="INVOKE", target="Ljava/util/List;clear()V")})
    private void fillEntityList(Level level, BlockPos minPos, BlockPos maxPos, CallbackInfo ci, @Local List<Entity> entities) {
        ActiveSableCompanion helper = Sable.HELPER;
        SubLevel schematicSubLevel = helper.getContaining(level, (Vec3i)minPos);
        entities.removeIf(entity -> {
            SubLevel entitySubLevel = helper.getContaining((Entity)entity);
            return entitySubLevel != schematicSubLevel && Sable.HELPER.getTrackingSubLevel((Entity)entity) != schematicSubLevel;
        });
    }

    @Override
    public List<StructureTemplateExtension.SubLevelTemplate> sable$getSubLevels() {
        return this.sable$subLevelTemplates;
    }
}

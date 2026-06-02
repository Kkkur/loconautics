/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.schematics;

import java.util.UUID;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public record StructureTemplateExtension.SubLevelTemplate(UUID uuid, Vector3d position, Quaterniond orientation, StructureTemplate template) {
}

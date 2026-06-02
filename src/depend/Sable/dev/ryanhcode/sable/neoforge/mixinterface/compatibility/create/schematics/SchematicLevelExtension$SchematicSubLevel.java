/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.levelWrappers.SchematicLevel
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.schematics;

import java.util.UUID;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public record SchematicLevelExtension.SchematicSubLevel(UUID uuid, Vector3d position, Quaterniond orientation, SchematicLevel level) {
}

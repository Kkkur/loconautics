/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Function
 *  net.minecraft.core.BlockPos
 *  org.joml.Quaterniondc
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.schematic;

import it.unimi.dsi.fastutil.Function;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;

public record SubLevelSchematicSerializationContext.SchematicMapping(Vector3dc newCorner, Quaterniondc newOrientation, UUID newUUID, Function<BlockPos, BlockPos> transform) {
}

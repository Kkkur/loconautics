/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.resources.ResourceLocation
 */
package dev.ryanhcode.offroad.index;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.ryanhcode.offroad.Offroad;
import net.minecraft.resources.ResourceLocation;

public class OffroadPartialModels {
    public static final PartialModel DIODE_LEFT = OffroadPartialModels.block("wheel_mount/diode_left");
    public static final PartialModel DIODE_RIGHT = OffroadPartialModels.block("wheel_mount/diode_right");
    public static final PartialModel TELE_OUTER = OffroadPartialModels.block("wheel_mount/tele_outer");
    public static final PartialModel TELE_INNER = OffroadPartialModels.block("wheel_mount/tele_inner");
    public static final PartialModel TELE_MOUNT = OffroadPartialModels.block("wheel_mount/mount");
    public static final PartialModel SPRING_UPPER = OffroadPartialModels.block("wheel_mount/spring_upper");
    public static final PartialModel SPRING_MIDDLE = OffroadPartialModels.block("wheel_mount/spring_middle");
    public static final PartialModel SPRING_LOWER = OffroadPartialModels.block("wheel_mount/spring_lower");
    public static final PartialModel ROCK_CUTTING_WHEEL_WHEEL = OffroadPartialModels.block("rockcutting_wheel/wheel");

    private static PartialModel block(String path) {
        return PartialModel.of((ResourceLocation)Offroad.path("block/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of((ResourceLocation)Offroad.path("entity/" + path));
    }

    private static PartialModel item(String path) {
        return PartialModel.of((ResourceLocation)Offroad.path("item/" + path));
    }

    public static void init() {
    }
}

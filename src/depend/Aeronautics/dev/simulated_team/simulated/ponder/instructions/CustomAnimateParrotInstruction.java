/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.ParrotElement
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.ponder.instructions;

import dev.simulated_team.simulated.ponder.instructions.CustomAnimateElementInstruction;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.ParrotElement;
import net.minecraft.world.phys.Vec3;

public class CustomAnimateParrotInstruction
extends CustomAnimateElementInstruction<ParrotElement> {
    protected CustomAnimateParrotInstruction(ElementLink<ParrotElement> link, Vec3 totalDelta, int ticks, BiConsumer<ParrotElement, Vec3> setter, Function<ParrotElement, Vec3> getter, FloatUnaryOperator positionFunc) {
        super(link, totalDelta, ticks, setter, getter, (UnaryOperator<Float>)positionFunc);
    }

    public static CustomAnimateParrotInstruction move(ElementLink<ParrotElement> link, Vec3 offset, int ticks, FloatUnaryOperator positionFunc) {
        return new CustomAnimateParrotInstruction(link, offset, ticks, (wse, v) -> wse.setPositionOffset(v, ticks == 0), ParrotElement::getPositionOffset, positionFunc);
    }
}

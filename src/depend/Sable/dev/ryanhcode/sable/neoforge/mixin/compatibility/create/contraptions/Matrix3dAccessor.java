/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.collision.Matrix3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.contraptions;

import com.simibubi.create.foundation.collision.Matrix3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Matrix3d.class})
public interface Matrix3dAccessor {
    @Accessor(value="m00")
    public double getM00();

    @Accessor(value="m00")
    public void setM00(double var1);

    @Accessor(value="m01")
    public double getM01();

    @Accessor(value="m01")
    public void setM01(double var1);

    @Accessor(value="m02")
    public double getM02();

    @Accessor(value="m02")
    public void setM02(double var1);

    @Accessor(value="m10")
    public double getM10();

    @Accessor(value="m10")
    public void setM10(double var1);

    @Accessor(value="m11")
    public double getM11();

    @Accessor(value="m11")
    public void setM11(double var1);

    @Accessor(value="m12")
    public double getM12();

    @Accessor(value="m12")
    public void setM12(double var1);

    @Accessor(value="m20")
    public double getM20();

    @Accessor(value="m20")
    public void setM20(double var1);

    @Accessor(value="m21")
    public double getM21();

    @Accessor(value="m21")
    public void setM21(double var1);

    @Accessor(value="m22")
    public double getM22();

    @Accessor(value="m22")
    public void setM22(double var1);
}

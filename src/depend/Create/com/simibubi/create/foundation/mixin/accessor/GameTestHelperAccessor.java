/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.gametest.framework.GameTestHelper
 *  net.minecraft.gametest.framework.GameTestInfo
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={GameTestHelper.class})
public interface GameTestHelperAccessor {
    @Accessor
    public GameTestInfo getTestInfo();

    @Accessor
    public boolean getFinalCheckAdded();

    @Accessor
    public void setFinalCheckAdded(boolean var1);
}

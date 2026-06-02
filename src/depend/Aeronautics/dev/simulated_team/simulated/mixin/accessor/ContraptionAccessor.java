/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.Contraption
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.simulated_team.simulated.mixin.accessor;

import com.simibubi.create.content.contraptions.Contraption;
import java.util.List;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Contraption.class})
public interface ContraptionAccessor {
    @Accessor(value="superglue")
    public List<AABB> getSuperGlue();
}

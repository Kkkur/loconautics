/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.ui.PonderUI
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Constant
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyConstant
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.ponder;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.simulated_team.simulated.mixin_interface.ponder.PonderSceneExtension;
import java.util.List;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PonderUI.class})
public class PonderUIMixin {
    @Shadow
    private List<PonderScene> scenes;

    @ModifyConstant(method={"renderScene"}, constant={@Constant(intValue=0x66000000, ordinal=0)})
    private int customShadowFade(int constant, GuiGraphics graphics, int mouseX, int mouseY, int i, float partialTicks) {
        int alpha = (int)((float)(constant >> 24) * ((PonderSceneExtension)this.scenes.get(i)).simulated$getBasePlateAnimationTimer(partialTicks));
        return alpha << 24 | constant & 0xFFFFFF;
    }

    @Inject(method={"renderScene"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal=1, shift=At.Shift.AFTER)})
    private void shadowTranslate(GuiGraphics graphics, int mouseX, int mouseY, int i, float partialTicks, CallbackInfo ci, @Local PoseStack ms) {
        Vec3 offset = ((PonderSceneExtension)this.scenes.get(i)).simulated$getShadowOffset(partialTicks);
        ms.translate(offset.x, offset.y, offset.z);
    }
}

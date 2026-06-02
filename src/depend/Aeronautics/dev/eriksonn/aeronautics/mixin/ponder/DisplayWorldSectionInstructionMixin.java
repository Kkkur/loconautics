/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.createmod.ponder.api.element.AnimatedSceneElement
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.element.WorldSectionElementImpl
 *  net.createmod.ponder.foundation.instruction.DisplayWorldSectionInstruction
 *  net.createmod.ponder.foundation.instruction.FadeIntoSceneInstruction
 *  net.minecraft.core.Direction
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.eriksonn.aeronautics.mixin.ponder;

import dev.eriksonn.aeronautics.mixin.ponder.WorldSectionElementImplAccessor;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.createmod.ponder.api.element.AnimatedSceneElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.WorldSectionElementImpl;
import net.createmod.ponder.foundation.instruction.DisplayWorldSectionInstruction;
import net.createmod.ponder.foundation.instruction.FadeIntoSceneInstruction;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={DisplayWorldSectionInstruction.class})
public abstract class DisplayWorldSectionInstructionMixin
extends FadeIntoSceneInstruction<WorldSectionElement> {
    @Shadow
    @Final
    @Nullable
    private Supplier<WorldSectionElement> mergeOnto;

    public DisplayWorldSectionInstructionMixin(int fadeInTicks, Direction fadeInFrom, WorldSectionElement element) {
        super(fadeInTicks, fadeInFrom, (AnimatedSceneElement)element);
    }

    @Inject(method={"firstTick"}, at={@At(value="INVOKE", target="Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;")})
    public void firstTick(PonderScene scene, CallbackInfo ci) {
        Optional.ofNullable(this.mergeOnto).ifPresent(wse -> {
            WorldSectionElement e = (WorldSectionElement)wse.get();
            ((WorldSectionElement)this.element).setAnimatedRotation(e.getAnimatedRotation(), true);
            if (e instanceof WorldSectionElementImpl) {
                WorldSectionElementImpl impl = (WorldSectionElementImpl)e;
                ((WorldSectionElement)this.element).setCenterOfRotation(((WorldSectionElementImplAccessor)impl).getCenterOfRotation());
            }
        });
    }
}

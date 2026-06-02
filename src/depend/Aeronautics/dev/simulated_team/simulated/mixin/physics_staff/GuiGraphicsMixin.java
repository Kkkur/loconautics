/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.simulated_team.simulated.mixin.physics_staff;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.simulated_team.simulated.index.SimItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={GuiGraphics.class})
public abstract class GuiGraphicsMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private PoseStack pose;

    @Shadow
    public abstract int guiWidth();

    @Shadow
    public abstract void fill(int var1, int var2, int var3, int var4, int var5);

    @WrapMethod(method={"renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V"})
    private void simulated$renderPhysicsStaff(LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, int guiOffset, Operation<Void> original) {
        boolean isStaff = stack.is(SimItems.PHYSICS_STAFF);
        if (isStaff) {
            Window window = Minecraft.getInstance().getWindow();
            float scale = (float)window.getGuiScale();
            Matrix4f pose = this.pose.last().pose();
            Vector3f position = pose.transformPosition(new Vector3f((float)x, (float)y, 0.0f));
            Vector3f corner = pose.transformPosition(new Vector3f((float)(x + 16), (float)(y + 16), 0.0f));
            position.mul(scale);
            corner.mul(scale);
            int slotHeight = (int)(corner.y - position.y);
            RenderSystem.enableScissor((int)((int)position.x), (int)(window.getHeight() - (int)position.y - slotHeight), (int)((int)(corner.x - position.x)), (int)slotHeight);
        }
        original.call(new Object[]{entity, level, stack, x, y, seed, guiOffset});
        if (isStaff) {
            RenderSystem.disableScissor();
        }
    }
}

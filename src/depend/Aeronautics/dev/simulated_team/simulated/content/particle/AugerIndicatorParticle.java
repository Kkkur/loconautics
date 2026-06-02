/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.equipment.goggles.GogglesItem
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.SimpleAnimatedParticle
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import dev.simulated_team.simulated.content.particle.AugerIndicatorParticleData;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class AugerIndicatorParticle
extends SimpleAnimatedParticle {
    protected float radius;
    protected float radius1;
    protected float radius2;
    protected float angleOffset;
    protected float speed;
    protected Direction direction;
    protected Vec3 origin;
    protected Vec3 offset;
    protected boolean isVisible;

    protected AugerIndicatorParticle(ClientLevel level, double x, double y, double z, int color, float radius1, float radius2, float angle, float speed, Direction direction, int lifeSpan, boolean isVisible, SpriteSet sprite) {
        super(level, x, y, z, sprite, 0.0f);
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        Vec3i normal = direction.getNormal();
        this.origin = new Vec3(x - (double)normal.getX() * 0.5, y - (double)normal.getY() * 0.5, z - (double)normal.getZ() * 0.5);
        this.quadSize *= 0.75f;
        this.lifetime = lifeSpan + this.random.nextInt(32);
        this.setFadeColor(color);
        this.setColor(Color.mixColors((int)color, (int)0xFFFFFF, (float)0.5f));
        this.setSpriteFromAge(sprite);
        this.radius1 = radius1;
        this.radius = radius1;
        this.radius2 = radius2;
        this.angleOffset = angle;
        this.speed = speed;
        this.direction = direction;
        this.isVisible = isVisible;
        this.offset = direction.getAxis().isHorizontal() ? new Vec3(0.0, 1.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
        this.move(0.0, 0.0, 0.0);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
    }

    public void tick() {
        super.tick();
        this.radius += (this.radius2 - this.radius) * 0.1f;
    }

    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        if (!this.isVisible) {
            return;
        }
        super.render(buffer, renderInfo, partialTicks);
    }

    public void move(double x, double y, double z) {
        float time = AnimationTickHolder.getTicks((LevelAccessor)this.level);
        float angle = time * this.speed % 360.0f - this.speed / 2.0f * (float)this.age * ((float)this.age / (float)this.lifetime);
        if (this.speed < 0.0f && this.direction.getAxis().isVertical()) {
            angle += 180.0f;
        }
        Vec3 position = VecHelper.rotate((Vec3)this.offset.scale((double)this.radius), (double)(angle += this.angleOffset * 360.0f), (Direction.Axis)this.direction.getAxis()).add(this.origin).add(Vec3.atLowerCornerOf((Vec3i)this.direction.getNormal()).scale((double)this.age / (double)this.lifetime));
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
    }

    public static class Factory
    implements ParticleProvider<AugerIndicatorParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        public Particle createParticle(AugerIndicatorParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            boolean visible = worldIn != mc.level || player != null && GogglesItem.isWearingGoggles((Player)player);
            return new AugerIndicatorParticle(worldIn, x, y, z, data.color, data.radius1, data.radius2, data.angleOffset, data.speed, data.direction, data.lifeSpan, visible, this.spriteSet);
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper
 *  dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData
 *  dev.ryanhcode.sable.physics.floating_block.FloatingBlockMaterial
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  dev.ryanhcode.sable.util.SableMathUtils
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Matrix3f
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package dev.eriksonn.aeronautics.content.blocks.levitite;

import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockMaterial;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.util.SableMathUtils;
import foundry.veil.api.client.render.VeilRenderSystem;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class LevititeShaderManager {
    private static final double SMOOTHING_SPEED = 0.5;
    private static final Vector3d linearVelocity = new Vector3d();
    private static final Vector3d angularVelocity = new Vector3d();
    private static final Vector3d temp = new Vector3d();
    private static final Vector3d currentPos = new Vector3d();
    private static final Quaterniond currentOrientation = new Quaterniond();
    private static final Vector3d offset = new Vector3d();
    private static final Matrix3f matrix = new Matrix3f();
    private static final Vector3d gravityVector1 = new Vector3d();
    private static final Vector3f gravityVector2 = new Vector3f();
    private static boolean enabled = false;
    public static HashMap<ClientSubLevel, LevititeShaderManager> managers = new HashMap();
    private final Vector3d smoothedLinearVelocity = new Vector3d();
    private final Vector3d lastSmoothedLinearVelocity = new Vector3d();
    private final Vector3d smoothedAngularVelocity = new Vector3d();
    private final Vector3d lastSmoothedAngularVelocity = new Vector3d();
    private final Vector3d accumulatedPosition = new Vector3d();

    public static void tick() {
        if (managers.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<ClientSubLevel, LevititeShaderManager>> iterator = managers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ClientSubLevel, LevititeShaderManager> entry = iterator.next();
            ClientSubLevel subLevel = entry.getKey();
            if (subLevel.isRemoved()) {
                iterator.remove();
                continue;
            }
            entry.getValue().internalTick(subLevel);
        }
    }

    public static LevititeShaderManager getInstance(ClientSubLevel subLevel) {
        managers.putIfAbsent(subLevel, new LevititeShaderManager());
        return managers.get(subLevel);
    }

    public static void disableShader() {
        enabled = false;
    }

    public static void prepareShaderForWorld(ShaderInstance shader, double camX, double camY, double camZ) {
        LevititeShaderManager.setMaterialProperties(shader);
        shader.safeGetUniform("offset").set(-((float)(camX %= 10000.0)), -((float)(camY %= 10000.0)), -((float)(camZ %= 10000.0)));
        shader.safeGetUniform("currentOrientation").set(matrix.identity());
        shader.safeGetUniform("sublevelPosition").set(0.0f, 0.0f, 0.0f);
        shader.safeGetUniform("linearVelocity").set(0.0f, 0.0f, 0.0f);
        shader.safeGetUniform("angularVelocity").set(0.0f, 0.0f, 0.0f);
        shader.safeGetUniform("onSublevel").set(0);
        enabled = true;
    }

    public static void setMaterialProperties(ShaderInstance shader) {
        FloatingBlockMaterial material = PhysicsBlockPropertyHelper.getFloatingMaterial((BlockState)AeroBlocks.LEVITITE.getDefaultState());
        if (material == null) {
            return;
        }
        shader.safeGetUniform("materialTransitionSpeed").set((float)material.transitionSpeed());
        shader.safeGetUniform("materialMatrixSlow").set(LevititeShaderManager.getGravityMatrix(gravityVector2, (float)material.slowVerticalFriction(), (float)material.slowHorizontalFriction(), matrix));
        shader.safeGetUniform("materialMatrixFast").set(LevititeShaderManager.getGravityMatrix(gravityVector2, (float)material.fastVerticalFriction(), (float)material.fastHorizontalFriction(), matrix));
    }

    private static Matrix3f getGravityMatrix(Vector3f g, float verticalDrag, float horizontalDrag, Matrix3f target) {
        if ((double)g.lengthSquared() > 1.0E-5) {
            float scale = (horizontalDrag - verticalDrag) / g.dot((Vector3fc)g);
            target.m00 = g.x() * g.x() * scale;
            target.m01 = g.y() * g.x() * scale;
            target.m02 = g.z() * g.x() * scale;
            target.m10 = g.x() * g.y() * scale;
            target.m11 = g.y() * g.y() * scale;
            target.m12 = g.z() * g.y() * scale;
            target.m20 = g.x() * g.z() * scale;
            target.m21 = g.y() * g.z() * scale;
            target.m22 = g.z() * g.z() * scale;
        } else {
            target.identity();
        }
        target.m00 -= horizontalDrag;
        target.m11 -= horizontalDrag;
        target.m22 -= horizontalDrag;
        return target;
    }

    void internalTick(ClientSubLevel subLevel) {
        this.lastSmoothedLinearVelocity.set((Vector3dc)this.smoothedLinearVelocity);
        this.lastSmoothedAngularVelocity.set((Vector3dc)this.smoothedAngularVelocity);
        subLevel.logicalPose().position().sub(subLevel.lastPose().position(), linearVelocity);
        subLevel.logicalPose().rotationPoint().sub(subLevel.lastPose().rotationPoint(), temp);
        subLevel.logicalPose().orientation().transform(temp);
        linearVelocity.sub((Vector3dc)temp);
        SableMathUtils.getAngularVelocity((Quaterniondc)subLevel.lastPose().orientation(), (Quaterniondc)subLevel.logicalPose().orientation(), (Vector3d)angularVelocity);
        this.smoothedLinearVelocity.lerp((Vector3dc)linearVelocity, 0.5);
        this.smoothedAngularVelocity.lerp((Vector3dc)angularVelocity, 0.5);
        this.accumulatedPosition.add((Vector3dc)this.smoothedLinearVelocity);
        this.accumulatedPosition.set(this.accumulatedPosition.x % 10000.0, this.accumulatedPosition.y % 10000.0, this.accumulatedPosition.z % 10000.0);
    }

    public boolean needsLayers() {
        return this.smoothedAngularVelocity.lengthSquared() > 1.0E-6 || this.smoothedLinearVelocity.lengthSquared() > 1.0E-6;
    }

    public void prepareShaderForSublevel(ClientSubLevel subLevel, ShaderInstance shader, double camX, double camY, double camZ) {
        float pt = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        Pose3dc currentPose = subLevel.renderPose(pt);
        currentPos.set(currentPose.position());
        currentOrientation.set(currentPose.orientation());
        currentPos.sub(camX, camY, camZ, offset);
        this.lastSmoothedLinearVelocity.lerp((Vector3dc)this.smoothedLinearVelocity, (double)pt, linearVelocity);
        this.lastSmoothedAngularVelocity.lerp((Vector3dc)this.smoothedAngularVelocity, (double)pt, angularVelocity);
        DimensionPhysicsData.getGravity((Level)subLevel.getLevel(), (Vector3dc)subLevel.logicalPose().position(), (Vector3d)gravityVector1);
        currentOrientation.transformInverse(offset);
        currentOrientation.transformInverse(linearVelocity);
        currentOrientation.transformInverse(angularVelocity);
        currentOrientation.transformInverse(gravityVector1);
        gravityVector2.set((Vector3dc)gravityVector1);
        shader.safeGetUniform("offset").set((float)LevititeShaderManager.offset.x, (float)LevititeShaderManager.offset.y, (float)LevititeShaderManager.offset.z);
        shader.safeGetUniform("linearVelocity").set((float)LevititeShaderManager.linearVelocity.x * 20.0f, (float)LevititeShaderManager.linearVelocity.y * 20.0f, (float)LevititeShaderManager.linearVelocity.z * 20.0f);
        shader.safeGetUniform("angularVelocity").set((float)LevititeShaderManager.angularVelocity.x * 20.0f, (float)LevititeShaderManager.angularVelocity.y * 20.0f, (float)LevititeShaderManager.angularVelocity.z * 20.0f);
        shader.safeGetUniform("sublevelPosition").set((float)LevititeShaderManager.currentPos.x % 10000.0f, (float)LevititeShaderManager.currentPos.y % 10000.0f, (float)LevititeShaderManager.currentPos.z % 10000.0f);
        shader.safeGetUniform("currentOrientation").set(matrix.set((Quaterniondc)currentOrientation));
        shader.safeGetUniform("onSublevel").set(1);
    }

    public static boolean isEnabled() {
        return VeilRenderSystem.tessellationSupported() && enabled;
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.InstancerProvider
 *  dev.engine_room.flywheel.api.model.Model
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.simibubi.create.content.logistics;

import com.simibubi.create.content.logistics.FlapStuffs;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import java.util.function.Consumer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public static class FlapStuffs.Visual {
    private final TransformedInstance[] flaps;
    private final Matrix4f commonTransform = new Matrix4f();
    private final Vec3 pivot;

    public FlapStuffs.Visual(InstancerProvider instancerProvider, Matrix4fc commonTransform, Vec3 pivot, Model flapModel) {
        this.pivot = pivot;
        this.commonTransform.set(commonTransform).translate((float)pivot.x, (float)pivot.y, (float)pivot.z);
        this.flaps = new TransformedInstance[4];
        instancerProvider.instancer(InstanceTypes.TRANSFORMED, flapModel).createInstances((Instance[])this.flaps);
    }

    public void update(float f) {
        for (int segment = 0; segment < 4; ++segment) {
            TransformedInstance flap = this.flaps[segment];
            ((TransformedInstance)((TransformedInstance)flap.setTransform((Matrix4fc)this.commonTransform).rotateXDegrees(FlapStuffs.flapAngle(f, segment))).translateBack(this.pivot)).translate((float)segment * -0.190625f, 0.0f, 0.0f).setChanged();
        }
    }

    public void delete() {
        for (TransformedInstance flap : this.flaps) {
            flap.delete();
        }
    }

    public void updateLight(int light) {
        for (TransformedInstance flap : this.flaps) {
            flap.light(light).setChanged();
        }
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        for (TransformedInstance flap : this.flaps) {
            consumer.accept((Instance)flap);
        }
    }
}

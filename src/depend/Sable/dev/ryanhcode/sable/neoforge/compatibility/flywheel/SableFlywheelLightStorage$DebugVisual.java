/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visual.EffectVisual
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.backend.engine.LightStorage
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  dev.engine_room.flywheel.lib.visual.component.HitboxComponent
 *  dev.engine_room.flywheel.lib.visual.util.InstanceRecycler
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2IntMap
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 */
package dev.ryanhcode.sable.neoforge.compatibility.flywheel;

import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.EffectVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.backend.engine.LightStorage;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.component.HitboxComponent;
import dev.engine_room.flywheel.lib.visual.util.InstanceRecycler;
import dev.ryanhcode.sable.neoforge.compatibility.flywheel.SableLightLut;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;

public class SableFlywheelLightStorage.DebugVisual
implements EffectVisual<LightStorage>,
SimpleDynamicVisual {
    private final InstanceRecycler<TransformedInstance> boxes;
    private final Vec3i renderOrigin;

    public SableFlywheelLightStorage.DebugVisual(VisualizationContext ctx, float partialTick) {
        this.renderOrigin = ctx.renderOrigin();
        this.boxes = new InstanceRecycler(() -> (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, HitboxComponent.BOX_MODEL).createInstance());
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.boxes.resetCount();
        this.setupSectionBoxes();
        this.setupLutRangeBoxes();
        this.boxes.discardExtra();
    }

    private void setupSectionBoxes() {
        for (Int2ObjectMap.Entry entry : SableFlywheelLightStorage.this.scene2SectionArenaIndexMap.int2ObjectEntrySet()) {
            int sceneId = entry.getIntKey();
            Long2IntMap section2ArenaIndex = (Long2IntMap)entry.getValue();
            section2ArenaIndex.keySet().forEach(l -> {
                int x = SectionPos.x((long)l) * 16 - this.renderOrigin.getX();
                int y = SectionPos.y((long)l) * 16 - this.renderOrigin.getY();
                int z = SectionPos.z((long)l) * 16 - this.renderOrigin.getZ();
                TransformedInstance instance = (TransformedInstance)this.boxes.get();
                ((TransformedInstance)instance.setIdentityTransform().translate((float)(x + 1), (float)(y + 1), (float)(z + 1)).scale(14.0f)).color(255, 255, sceneId * 64).light(0xF000F0).setChanged();
            });
        }
    }

    private void setupLutRangeBoxes() {
        SableLightLut.Layer<SableLightLut.Layer<SableLightLut.Layer<SableLightLut.IntLayer>>> first = SableFlywheelLightStorage.this.sableLut.indices;
        int base1 = first.base();
        int size1 = first.size();
        float debug1 = base1 * 16 - this.renderOrigin.getY();
        float min2 = Float.POSITIVE_INFINITY;
        float max2 = Float.NEGATIVE_INFINITY;
        float min3 = Float.POSITIVE_INFINITY;
        float max3 = Float.NEGATIVE_INFINITY;
        for (int y = 0; y < size1; ++y) {
            SableLightLut.Layer<SableLightLut.Layer<SableLightLut.IntLayer>> second = first.getRaw(y);
            if (second == null) continue;
            int base2 = second.base();
            int size2 = second.size();
            float y2 = (float)((base1 + y) * 16 - this.renderOrigin.getY()) + 7.5f;
            min2 = Math.min(min2, (float)base2);
            max2 = Math.max(max2, (float)(base2 + size2));
            float minLocal3 = Float.POSITIVE_INFINITY;
            float maxLocal3 = Float.NEGATIVE_INFINITY;
            float debug2 = base2 * 16 - this.renderOrigin.getX();
            for (int x = 0; x < size2; ++x) {
                SableLightLut.Layer<SableLightLut.IntLayer> third = second.getRaw(x);
                if (third == null) continue;
                int base3 = third.base();
                int size3 = third.size();
                float x2 = (float)((base2 + x) * 16 - this.renderOrigin.getX()) + 7.5f;
                min3 = Math.min(min3, (float)base3);
                max3 = Math.max(max3, (float)(base3 + size3));
                minLocal3 = Math.min(minLocal3, (float)base3);
                maxLocal3 = Math.max(maxLocal3, (float)(base3 + size3));
                float debug3 = base3 * 16 - this.renderOrigin.getZ();
                for (int z = 0; z < size3; ++z) {
                    ((TransformedInstance)this.boxes.get()).setIdentityTransform().translate(x2, y2, debug3).scale(1.0f, 1.0f, (float)(size3 * 16)).color(0, 0, 255).light(0xF000F0).setChanged();
                }
            }
            ((TransformedInstance)this.boxes.get()).setIdentityTransform().translate(debug2, y2, minLocal3 * 16.0f - (float)this.renderOrigin.getZ()).scale((float)(size2 * 16), 1.0f, (maxLocal3 - minLocal3) * 16.0f).color(255, 0, 0).light(0xF000F0).setChanged();
        }
        ((TransformedInstance)this.boxes.get()).setIdentityTransform().translate(min2 * 16.0f - (float)this.renderOrigin.getX(), debug1, min3 * 16.0f - (float)this.renderOrigin.getZ()).scale((max2 - min2) * 16.0f, (float)(size1 * 16), (max3 - min3) * 16.0f).color(0, 255, 0).light(0xF000F0).setChanged();
    }

    public void update(float partialTick) {
    }

    public void delete() {
        this.boxes.delete();
    }
}

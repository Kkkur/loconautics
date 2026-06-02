/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visual.SectionTrackedVisual$SectionCollector
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.math.MoreMath
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  dev.engine_room.flywheel.lib.visual.util.SmartRecycler
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 *  it.unimi.dsi.fastutil.bytes.ByteList
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.LightLayer
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.contraptions.pulley;

import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.SectionTrackedVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.math.MoreMath;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.util.SmartRecycler;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public abstract class AbstractPulleyVisual<T extends KineticBlockEntity>
extends ShaftVisual<T>
implements SimpleDynamicVisual {
    private final ScrollInstance coil;
    private final TransformedInstance magnet;
    private final SmartRecycler<Boolean, TransformedInstance> rope;
    protected final Direction rotatingAbout;
    protected final Axis rotationAxis;
    private final LightCache lightCache = new LightCache();
    private float offset;

    public AbstractPulleyVisual(VisualizationContext dispatcher, T blockEntity, float partialTick) {
        super(dispatcher, blockEntity, partialTick);
        this.rotatingAbout = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)this.rotationAxis());
        this.rotationAxis = Axis.of((Vector3f)this.rotatingAbout.step());
        float blockStateAngle = AngleHelper.horizontalAngle((Direction)this.rotatingAbout);
        Quaternionf rotation = new Quaternionf().rotationY((float)Math.PI / 180 * blockStateAngle);
        this.coil = ((ScrollInstance)this.getCoilModel().createInstance()).rotation((Quaternionfc)rotation).position((Vec3i)this.getVisualPosition()).setSpriteShift(this.getCoilAnimation());
        this.coil.setChanged();
        this.magnet = (TransformedInstance)this.magnetInstancer().createInstance();
        this.rope = new SmartRecycler(b -> b != false ? (TransformedInstance)this.getHalfRopeModel().createInstance() : (TransformedInstance)this.getRopeModel().createInstance());
        this.updateOffset(partialTick);
        this.updateLight(partialTick);
        this.animate();
    }

    public void setSectionCollector(SectionTrackedVisual.SectionCollector sectionCollector) {
        super.setSectionCollector(sectionCollector);
        this.lightCache.updateSections();
    }

    protected abstract Instancer<TransformedInstance> getRopeModel();

    protected abstract Instancer<TransformedInstance> getMagnetModel();

    protected abstract Instancer<TransformedInstance> getHalfMagnetModel();

    protected abstract Instancer<ScrollInstance> getCoilModel();

    protected abstract Instancer<TransformedInstance> getHalfRopeModel();

    protected abstract float getOffset(float var1);

    protected abstract boolean isRunning();

    protected abstract SpriteShiftEntry getCoilAnimation();

    private Instancer<TransformedInstance> magnetInstancer() {
        return this.offset > 0.25f ? this.getMagnetModel() : this.getHalfMagnetModel();
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.updateOffset(ctx.partialTick());
        this.animate();
    }

    private void animate() {
        this.coil.offsetV = -this.offset;
        this.coil.setChanged();
        this.magnet.setVisible(this.isRunning() || this.offset == 0.0f);
        this.magnetInstancer().stealInstance((Instance)this.magnet);
        ((TransformedInstance)this.magnet.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translate(0.0f, -this.offset, 0.0f).light(this.lightCache.getPackedLight(Math.max(0, Mth.floor((float)this.offset)))).setChanged();
        this.rope.resetCount();
        if (this.shouldRenderHalfRope()) {
            float f = this.offset % 1.0f;
            float halfRopeNudge = f > 0.75f ? f - 1.0f : f;
            ((TransformedInstance)((TransformedInstance)this.rope.get((Object)true)).setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translate(0.0f, -halfRopeNudge, 0.0f).light(this.lightCache.getPackedLight(0)).setChanged();
        }
        if (this.isRunning()) {
            int neededRopeCount = this.getNeededRopeCount();
            for (int i = 0; i < neededRopeCount; ++i) {
                ((TransformedInstance)((TransformedInstance)this.rope.get((Object)false)).setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translate(0.0f, -this.offset + (float)i + 1.0f, 0.0f).light(this.lightCache.getPackedLight(neededRopeCount - 1 - i)).setChanged();
            }
        }
        this.rope.discardExtra();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(new FlatLit[]{this.coil});
        this.lightCache.update();
    }

    private void updateOffset(float pt) {
        this.offset = this.getOffset(pt);
        this.lightCache.setSize(Mth.ceil((float)this.offset) + 2);
    }

    private int getNeededRopeCount() {
        return Math.max(0, Mth.ceil((float)(this.offset - 1.25f)));
    }

    private boolean shouldRenderHalfRope() {
        float f = this.offset % 1.0f;
        return this.offset > 0.75f && (f < 0.25f || f > 0.75f);
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.coil);
        consumer.accept((Instance)this.magnet);
    }

    @Override
    protected void _delete() {
        super._delete();
        this.coil.delete();
        this.magnet.delete();
        this.rope.delete();
    }

    private class LightCache {
        private final ByteList data = new ByteArrayList();
        private final LongSet sections = new LongOpenHashSet();
        private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        private int sectionCount;

        private LightCache() {
        }

        public void setSize(int size) {
            if (size != this.data.size()) {
                this.data.size(size);
                this.update();
                int sectionCount = MoreMath.ceilingDiv((int)(size + 15 - AbstractPulleyVisual.this.pos.getY() + AbstractPulleyVisual.this.pos.getY() / 4 * 4), (int)16);
                if (sectionCount != this.sectionCount) {
                    this.sectionCount = sectionCount;
                    this.sections.clear();
                    int sectionX = SectionPos.blockToSectionCoord((int)AbstractPulleyVisual.this.pos.getX());
                    int sectionY = SectionPos.blockToSectionCoord((int)AbstractPulleyVisual.this.pos.getY());
                    int sectionZ = SectionPos.blockToSectionCoord((int)AbstractPulleyVisual.this.pos.getZ());
                    for (int i = 0; i < sectionCount; ++i) {
                        this.sections.add(SectionPos.asLong((int)sectionX, (int)(sectionY - i), (int)sectionZ));
                    }
                    if (AbstractPulleyVisual.this.lightSections != null) {
                        this.updateSections();
                    }
                }
            }
        }

        public void updateSections() {
            AbstractPulleyVisual.this.lightSections.sections(this.sections);
        }

        public void update() {
            this.mutablePos.set((Vec3i)AbstractPulleyVisual.this.pos);
            for (int i = 0; i < this.data.size(); ++i) {
                int blockLight = AbstractPulleyVisual.this.level.getBrightness(LightLayer.BLOCK, (BlockPos)this.mutablePos);
                int skyLight = AbstractPulleyVisual.this.level.getBrightness(LightLayer.SKY, (BlockPos)this.mutablePos);
                int light = (skyLight & 0xF) << 4 | blockLight & 0xF;
                this.data.set(i, (byte)light);
                this.mutablePos.move(Direction.DOWN);
            }
        }

        public int getPackedLight(int offset) {
            if (offset < 0 || offset >= this.data.size()) {
                return 0;
            }
            int light = Byte.toUnsignedInt(this.data.getByte(offset));
            int blockLight = light & 0xF;
            int skyLight = light >>> 4 & 0xF;
            return LightTexture.pack((int)blockLight, (int)skyLight);
        }
    }
}

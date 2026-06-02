/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.track.TrackBlockEntityTilt;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BezierConnection
implements Iterable<Segment> {
    public final Couple<BlockPos> bePositions;
    public final Couple<Vec3> starts;
    public final Couple<Vec3> axes;
    public final Couple<Vec3> normals;
    @Nullable
    public Couple<Integer> smoothing;
    public final boolean primary;
    public final boolean hasGirder;
    protected TrackMaterial trackMaterial;
    private final AtomicReference<@Nullable Runtime> lazyRuntime = new AtomicReference<Object>(null);
    private final AtomicReference<@Nullable SegmentAngles> bakedSegments = new AtomicReference<Object>(null);
    private final AtomicReference<@Nullable GirderAngles> bakedGirders = new AtomicReference<Object>(null);

    public BezierConnection(Couple<BlockPos> positions, Couple<Vec3> starts, Couple<Vec3> axes, Couple<Vec3> normals, boolean primary, boolean girder, TrackMaterial material) {
        this.bePositions = positions;
        this.starts = starts;
        this.axes = axes;
        this.normals = normals;
        this.primary = primary;
        this.hasGirder = girder;
        this.trackMaterial = material;
    }

    public BezierConnection secondary() {
        BezierConnection bezierConnection = new BezierConnection((Couple<BlockPos>)this.bePositions.swap(), (Couple<Vec3>)this.starts.swap(), (Couple<Vec3>)this.axes.swap(), (Couple<Vec3>)this.normals.swap(), !this.primary, this.hasGirder, this.trackMaterial);
        if (this.smoothing != null) {
            bezierConnection.smoothing = this.smoothing.swap();
        }
        return bezierConnection;
    }

    public BezierConnection clone() {
        BezierConnection out = new BezierConnection((Couple<BlockPos>)this.bePositions.copy(), (Couple<Vec3>)this.starts.copy(), (Couple<Vec3>)this.axes.copy(), (Couple<Vec3>)this.normals.copy(), this.primary, this.hasGirder, this.trackMaterial);
        if (this.smoothing != null) {
            out.smoothing = this.smoothing.copy();
        }
        return out;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static boolean coupleEquals(Couple<?> a, Couple<?> b) {
        Object object;
        if (a.getFirst().equals(b.getFirst())) {
            if (a.getSecond().equals(b.getSecond())) return true;
        }
        if (!((object = a.getFirst()) instanceof Vec3)) return false;
        Vec3 aFirst = (Vec3)object;
        object = a.getSecond();
        if (!(object instanceof Vec3)) return false;
        Vec3 aSecond = (Vec3)object;
        object = b.getFirst();
        if (!(object instanceof Vec3)) return false;
        Vec3 bFirst = (Vec3)object;
        object = b.getSecond();
        if (!(object instanceof Vec3)) return false;
        Vec3 bSecond = (Vec3)object;
        if (!aFirst.closerThan((Position)bFirst, 1.0E-6)) return false;
        if (!aSecond.closerThan((Position)bSecond, 1.0E-6)) return false;
        return true;
    }

    public boolean equalsSansMaterial(BezierConnection other) {
        return this.equalsSansMaterialInner(other) || this.equalsSansMaterialInner(other.secondary());
    }

    private boolean equalsSansMaterialInner(BezierConnection other) {
        return this == other || other != null && BezierConnection.coupleEquals(this.bePositions, other.bePositions) && BezierConnection.coupleEquals(this.starts, other.starts) && BezierConnection.coupleEquals(this.axes, other.axes) && BezierConnection.coupleEquals(this.normals, other.normals) && this.hasGirder == other.hasGirder;
    }

    public BezierConnection(CompoundTag compound, BlockPos localTo) {
        this((Couple<BlockPos>)Couple.deserializeEach((ListTag)compound.getList("Positions", 10), t -> NBTHelper.readBlockPos((CompoundTag)t, (String)"Pos")).map(b -> b.offset((Vec3i)localTo)), (Couple<Vec3>)Couple.deserializeEach((ListTag)compound.getList("Starts", 10), VecHelper::readNBTCompound).map(v -> v.add(Vec3.atLowerCornerOf((Vec3i)localTo))), (Couple<Vec3>)Couple.deserializeEach((ListTag)compound.getList("Axes", 10), VecHelper::readNBTCompound), (Couple<Vec3>)Couple.deserializeEach((ListTag)compound.getList("Normals", 10), VecHelper::readNBTCompound), compound.getBoolean("Primary"), compound.getBoolean("Girder"), TrackMaterial.deserialize(compound.getString("Material")));
        if (compound.contains("Smoothing")) {
            this.smoothing = Couple.deserializeEach((ListTag)compound.getList("Smoothing", 10), NBTHelper::intFromCompound);
        }
    }

    public CompoundTag write(BlockPos localTo) {
        Couple tePositions = this.bePositions.map(b -> b.subtract((Vec3i)localTo));
        Couple starts = this.starts.map(v -> v.subtract(Vec3.atLowerCornerOf((Vec3i)localTo)));
        CompoundTag compound = new CompoundTag();
        compound.putBoolean("Girder", this.hasGirder);
        compound.putBoolean("Primary", this.primary);
        compound.put("Positions", (Tag)tePositions.serializeEach(t -> {
            CompoundTag tag = new CompoundTag();
            tag.put("Pos", NbtUtils.writeBlockPos((BlockPos)t));
            return tag;
        }));
        compound.put("Starts", (Tag)starts.serializeEach(VecHelper::writeNBTCompound));
        compound.put("Axes", (Tag)this.axes.serializeEach(VecHelper::writeNBTCompound));
        compound.put("Normals", (Tag)this.normals.serializeEach(VecHelper::writeNBTCompound));
        compound.putString("Material", this.getMaterial().id.toString());
        if (this.smoothing != null) {
            compound.put("Smoothing", (Tag)this.smoothing.serializeEach(NBTHelper::intToCompound));
        }
        return compound;
    }

    public BezierConnection(FriendlyByteBuf buffer) {
        this((Couple<BlockPos>)Couple.create(() -> ((FriendlyByteBuf)buffer).readBlockPos()), (Couple<Vec3>)Couple.create(() -> VecHelper.read((FriendlyByteBuf)buffer)), (Couple<Vec3>)Couple.create(() -> VecHelper.read((FriendlyByteBuf)buffer)), (Couple<Vec3>)Couple.create(() -> VecHelper.read((FriendlyByteBuf)buffer)), buffer.readBoolean(), buffer.readBoolean(), TrackMaterial.deserialize(buffer.readUtf()));
        if (buffer.readBoolean()) {
            this.smoothing = Couple.create(() -> ((FriendlyByteBuf)buffer).readVarInt());
        }
    }

    public void write(FriendlyByteBuf buffer) {
        this.bePositions.forEach(arg_0 -> ((FriendlyByteBuf)buffer).writeBlockPos(arg_0));
        this.starts.forEach(v -> VecHelper.write((Vec3)v, (FriendlyByteBuf)buffer));
        this.axes.forEach(v -> VecHelper.write((Vec3)v, (FriendlyByteBuf)buffer));
        this.normals.forEach(v -> VecHelper.write((Vec3)v, (FriendlyByteBuf)buffer));
        buffer.writeBoolean(this.primary);
        buffer.writeBoolean(this.hasGirder);
        buffer.writeUtf(this.getMaterial().id.toString());
        buffer.writeBoolean(this.smoothing != null);
        if (this.smoothing != null) {
            this.smoothing.forEach(arg_0 -> ((FriendlyByteBuf)buffer).writeVarInt(arg_0));
        }
    }

    public BlockPos getKey() {
        return (BlockPos)this.bePositions.getSecond();
    }

    public boolean isPrimary() {
        return this.primary;
    }

    public int yOffsetAt(Vec3 end) {
        if (this.smoothing == null) {
            return 0;
        }
        if (TrackBlockEntityTilt.compareHandles((Vec3)this.starts.getFirst(), end)) {
            return (Integer)this.smoothing.getFirst();
        }
        if (TrackBlockEntityTilt.compareHandles((Vec3)this.starts.getSecond(), end)) {
            return (Integer)this.smoothing.getSecond();
        }
        return 0;
    }

    public double getLength() {
        return this.resolve().length;
    }

    public float[] getStepLUT() {
        return this.resolve().stepLUT;
    }

    public int getSegmentCount() {
        return this.resolve().segments;
    }

    public Vec3 getPosition(double t) {
        Runtime runtime = this.resolve();
        return VecHelper.bezier((Vec3)((Vec3)this.starts.getFirst()), (Vec3)((Vec3)this.starts.getSecond()), (Vec3)runtime.finish1, (Vec3)runtime.finish2, (float)((float)t));
    }

    public double getRadius() {
        return this.resolve().radius;
    }

    public double getHandleLength() {
        return this.resolve().handleLength;
    }

    public float getSegmentT(int index) {
        return this.resolve().getSegmentT(index);
    }

    public double incrementT(double currentT, double distance) {
        Runtime runtime = this.resolve();
        double dx = VecHelper.bezierDerivative((Vec3)((Vec3)this.starts.getFirst()), (Vec3)((Vec3)this.starts.getSecond()), (Vec3)runtime.finish1, (Vec3)runtime.finish2, (float)((float)currentT)).length() / this.getLength();
        return currentT + distance / dx;
    }

    public AABB getBounds() {
        return this.resolve().bounds;
    }

    public Vec3 getNormal(double t) {
        Runtime runtime = this.resolve();
        Vec3 end1 = (Vec3)this.starts.getFirst();
        Vec3 end2 = (Vec3)this.starts.getSecond();
        Vec3 fn1 = (Vec3)this.normals.getFirst();
        Vec3 fn2 = (Vec3)this.normals.getSecond();
        Vec3 derivative = VecHelper.bezierDerivative((Vec3)end1, (Vec3)end2, (Vec3)runtime.finish1, (Vec3)runtime.finish2, (float)((float)t)).normalize();
        Vec3 faceNormal = fn1.equals((Object)fn2) ? fn1 : VecHelper.slerp((float)((float)t), (Vec3)fn1, (Vec3)fn2);
        Vec3 normal = faceNormal.cross(derivative).normalize();
        return derivative.cross(normal);
    }

    @NotNull
    private Runtime resolve() {
        Runtime out = this.lazyRuntime.get();
        if (out == null) {
            out = new Runtime(this.starts, this.axes);
            this.lazyRuntime.set(out);
        }
        return out;
    }

    @Override
    public Iterator<Segment> iterator() {
        Vec3 offset = Vec3.atLowerCornerOf((Vec3i)((Vec3i)this.bePositions.getFirst())).scale(-1.0).add(0.0, 0.1875, 0.0);
        return new Bezierator(this, offset);
    }

    public void addItemsToPlayer(Player player) {
        Inventory inv = player.getInventory();
        for (int tracks = this.getTrackItemCost(); tracks > 0; tracks -= 64) {
            inv.placeItemBackInInventory(new ItemStack((ItemLike)this.getMaterial().getBlock(), Math.min(64, tracks)));
        }
        for (int girders = this.getGirderItemCost(); girders > 0; girders -= 64) {
            inv.placeItemBackInInventory(AllBlocks.METAL_GIRDER.asStack(Math.min(64, girders)));
        }
    }

    public int getGirderItemCost() {
        return this.hasGirder ? this.getTrackItemCost() * 2 : 0;
    }

    public int getTrackItemCost() {
        return (this.getSegmentCount() + 1) / 2;
    }

    public void spawnItems(Level level) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            return;
        }
        Vec3 origin = Vec3.atLowerCornerOf((Vec3i)((Vec3i)this.bePositions.getFirst()));
        for (Segment segment : this) {
            if (segment.index % 2 != 0 || segment.index == this.getSegmentCount()) continue;
            Vec3 v = VecHelper.offsetRandomly((Vec3)segment.position, (RandomSource)level.random, (float)0.125f).add(origin);
            ItemEntity entity = new ItemEntity(level, v.x, v.y, v.z, this.getMaterial().asStack());
            entity.setDefaultPickUpDelay();
            level.addFreshEntity((Entity)entity);
            if (!this.hasGirder) continue;
            for (int i = 0; i < 2; ++i) {
                entity = new ItemEntity(level, v.x, v.y, v.z, AllBlocks.METAL_GIRDER.asStack());
                entity.setDefaultPickUpDelay();
                level.addFreshEntity((Entity)entity);
            }
        }
    }

    public void spawnDestroyParticles(Level level) {
        BlockParticleOption data = new BlockParticleOption(ParticleTypes.BLOCK, this.getMaterial().getBlock().defaultBlockState());
        BlockParticleOption girderData = new BlockParticleOption(ParticleTypes.BLOCK, AllBlocks.METAL_GIRDER.getDefaultState());
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel slevel = (ServerLevel)level;
        Vec3 origin = Vec3.atLowerCornerOf((Vec3i)((Vec3i)this.bePositions.getFirst()));
        for (Segment segment : this) {
            for (int offset : Iterate.positiveAndNegative) {
                Vec3 v = segment.position.add(segment.normal.scale((double)(0.875f * (float)offset))).add(origin);
                slevel.sendParticles((ParticleOptions)data, v.x, v.y, v.z, 1, 0.0, 0.0, 0.0, 0.0);
                if (!this.hasGirder) continue;
                slevel.sendParticles((ParticleOptions)girderData, v.x, v.y - 0.5, v.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    public TrackMaterial getMaterial() {
        return this.trackMaterial;
    }

    public void setMaterial(TrackMaterial material) {
        this.trackMaterial = material;
    }

    public SegmentAngles getBakedSegments() {
        SegmentAngles out = this.bakedSegments.get();
        if (out == null) {
            out = new SegmentAngles(this);
            this.bakedSegments.set(out);
        }
        return out;
    }

    public GirderAngles getBakedGirders() {
        GirderAngles out = this.bakedGirders.get();
        if (out == null) {
            out = new GirderAngles(this);
            this.bakedGirders.set(out);
        }
        return out;
    }

    public Map<Pair<Integer, Integer>, Double> rasterise() {
        HashMap<Pair<Integer, Integer>, Double> yLevels = new HashMap<Pair<Integer, Integer>, Double>();
        BlockPos tePosition = (BlockPos)this.bePositions.getFirst();
        Vec3 end1 = ((Vec3)this.starts.getFirst()).subtract(Vec3.atLowerCornerOf((Vec3i)tePosition)).add(0.0, 0.1875, 0.0);
        Vec3 end2 = ((Vec3)this.starts.getSecond()).subtract(Vec3.atLowerCornerOf((Vec3i)tePosition)).add(0.0, 0.1875, 0.0);
        Vec3 axis1 = (Vec3)this.axes.getFirst();
        Vec3 axis2 = (Vec3)this.axes.getSecond();
        double handleLength = this.getHandleLength();
        Vec3 finish1 = axis1.scale(handleLength).add(end1);
        Vec3 finish2 = axis2.scale(handleLength).add(end2);
        Vec3 faceNormal1 = (Vec3)this.normals.getFirst();
        Vec3 faceNormal2 = (Vec3)this.normals.getSecond();
        int segCount = this.getSegmentCount();
        float[] lut = this.getStepLUT();
        Vec3[] samples = new Vec3[segCount];
        for (int i = 0; i < segCount; ++i) {
            Vec3 railMiddle;
            float t = Mth.clamp((float)(((float)i + 0.5f) * lut[i] / (float)segCount), (float)0.0f, (float)1.0f);
            Vec3 result = VecHelper.bezier((Vec3)end1, (Vec3)end2, (Vec3)finish1, (Vec3)finish2, (float)t);
            Vec3 derivative = VecHelper.bezierDerivative((Vec3)end1, (Vec3)end2, (Vec3)finish1, (Vec3)finish2, (float)t).normalize();
            Vec3 faceNormal = faceNormal1.equals((Object)faceNormal2) ? faceNormal1 : VecHelper.slerp((float)t, (Vec3)faceNormal1, (Vec3)faceNormal2);
            Vec3 normal = faceNormal.cross(derivative).normalize();
            Vec3 below = result.add(faceNormal.scale(-0.25));
            Vec3 rail1 = below.add(normal.scale((double)0.05f));
            Vec3 rail2 = below.subtract(normal.scale((double)0.05f));
            samples[i] = railMiddle = rail1.add(rail2).scale(0.5);
        }
        Vec3 center = end1.add(end2).scale(0.5);
        Pair prev = null;
        Pair prev2 = null;
        Pair prev3 = null;
        for (int i = 0; i < segCount; ++i) {
            Vec3 railMiddle = samples[i];
            BlockPos pos = BlockPos.containing((Position)railMiddle);
            Pair key = Pair.of((Object)pos.getX(), (Object)pos.getZ());
            boolean alreadyPresent = yLevels.containsKey(key);
            if (alreadyPresent && (Double)yLevels.get(key) <= railMiddle.y) continue;
            yLevels.put((Pair<Integer, Integer>)key, railMiddle.y);
            if (alreadyPresent) continue;
            if (prev3 != null) {
                boolean prevCloser;
                boolean doubledViaPrev = this.isLineDoubled(prev2, prev, (Pair<Integer, Integer>)key);
                boolean doubledViaPrev2 = this.isLineDoubled((Pair<Integer, Integer>)prev3, (Pair<Integer, Integer>)prev2, (Pair<Integer, Integer>)prev);
                boolean bl = prevCloser = this.diff((Pair<Integer, Integer>)prev, center) > this.diff((Pair<Integer, Integer>)prev2, center);
                if (!(!doubledViaPrev2 || doubledViaPrev && prevCloser)) {
                    yLevels.remove(prev2);
                    prev2 = prev;
                    prev = key;
                    continue;
                }
                if (doubledViaPrev && doubledViaPrev2 && prevCloser) {
                    yLevels.remove(prev);
                    prev = key;
                    continue;
                }
            }
            prev3 = prev2;
            prev2 = prev;
            prev = key;
        }
        return yLevels;
    }

    private double diff(Pair<Integer, Integer> pFrom, Vec3 to) {
        return to.distanceToSqr((double)((Integer)pFrom.getFirst()).intValue() + 0.5, to.y, (double)((Integer)pFrom.getSecond()).intValue() + 0.5);
    }

    private boolean isLineDoubled(Pair<Integer, Integer> pFrom, Pair<Integer, Integer> pVia, Pair<Integer, Integer> pTo) {
        int diff1x = (Integer)pVia.getFirst() - (Integer)pFrom.getFirst();
        int diff1z = (Integer)pVia.getSecond() - (Integer)pFrom.getSecond();
        int diff2x = (Integer)pTo.getFirst() - (Integer)pVia.getFirst();
        int diff2z = (Integer)pTo.getSecond() - (Integer)pVia.getSecond();
        return Math.abs(diff1x) + Math.abs(diff1z) == 1 && Math.abs(diff2x) + Math.abs(diff2z) == 1 && diff1x != diff2x && diff1z != diff2z;
    }

    private static class Runtime {
        private final Vec3 finish1;
        private final Vec3 finish2;
        private final double length;
        private final float[] stepLUT;
        private final int segments;
        private double radius;
        private double handleLength;
        private final AABB bounds;

        private Runtime(Couple<Vec3> starts, Couple<Vec3> axes) {
            Vec3 end1 = (Vec3)starts.getFirst();
            Vec3 end2 = (Vec3)starts.getSecond();
            Vec3 axis1 = ((Vec3)axes.getFirst()).normalize();
            Vec3 axis2 = ((Vec3)axes.getSecond()).normalize();
            this.determineHandles(end1, end2, axis1, axis2);
            this.finish1 = axis1.scale(this.handleLength).add(end1);
            this.finish2 = axis2.scale(this.handleLength).add(end2);
            int scanCount = 16;
            this.length = Runtime.computeLength(this.finish1, this.finish2, end1, end2, scanCount);
            this.segments = (int)(this.length * 2.0);
            this.stepLUT = new float[this.segments + 1];
            this.stepLUT[0] = 1.0f;
            float combinedDistance = 0.0f;
            AABB bounds = new AABB(end1, end2);
            Vec3 previous = end1;
            for (int i = 0; i <= this.segments; ++i) {
                float t = (float)i / (float)this.segments;
                Vec3 result = VecHelper.bezier((Vec3)end1, (Vec3)end2, (Vec3)this.finish1, (Vec3)this.finish2, (float)t);
                bounds = bounds.minmax(new AABB(result, result));
                if (i > 0) {
                    combinedDistance = (float)((double)combinedDistance + result.distanceTo(previous) / this.length);
                    this.stepLUT[i] = t / combinedDistance;
                }
                previous = result;
            }
            this.bounds = bounds.inflate(1.375);
        }

        private static double computeLength(Vec3 finish1, Vec3 finish2, Vec3 end1, Vec3 end2, int scanCount) {
            double length = 0.0;
            Vec3 previous = end1;
            for (int i = 0; i <= scanCount; ++i) {
                float t = (float)i / (float)scanCount;
                Vec3 result = VecHelper.bezier((Vec3)end1, (Vec3)end2, (Vec3)finish1, (Vec3)finish2, (float)t);
                if (previous != null) {
                    length += result.distanceTo(previous);
                }
                previous = result;
            }
            return length;
        }

        public float getSegmentT(int index) {
            return index == this.segments ? 1.0f : (float)index * this.stepLUT[index] / (float)this.segments;
        }

        private void determineHandles(Vec3 end1, Vec3 end2, Vec3 axis1, Vec3 axis2) {
            Vec3 cross1 = axis1.cross(new Vec3(0.0, 1.0, 0.0));
            Vec3 cross2 = axis2.cross(new Vec3(0.0, 1.0, 0.0));
            this.radius = 0.0;
            double a1 = Mth.atan2((double)(-axis2.z), (double)(-axis2.x));
            double a2 = Mth.atan2((double)axis1.z, (double)axis1.x);
            double angle = a1 - a2;
            float circle = (float)Math.PI * 2;
            if (Math.abs((double)circle - (angle = (angle + (double)circle) % (double)circle)) < Math.abs(angle)) {
                angle = (double)circle - angle;
            }
            if (Mth.equal((double)angle, (double)0.0)) {
                double[] intersect = VecHelper.intersect((Vec3)end1, (Vec3)end2, (Vec3)axis1, (Vec3)cross2, (Direction.Axis)Direction.Axis.Y);
                if (intersect != null) {
                    double t = Math.abs(intersect[0]);
                    double u = Math.abs(intersect[1]);
                    double min = Math.min(t, u);
                    double max = Math.max(t, u);
                    if (min > 1.2 && max / min > 1.0 && max / min < 3.0) {
                        this.handleLength = max - min;
                        return;
                    }
                }
                this.handleLength = end2.distanceTo(end1) / 3.0;
                return;
            }
            double n = (double)circle / angle;
            double factor = 1.3333333333333333 * Math.tan(Math.PI / (2.0 * n));
            double[] intersect = VecHelper.intersect((Vec3)end1, (Vec3)end2, (Vec3)cross1, (Vec3)cross2, (Direction.Axis)Direction.Axis.Y);
            if (intersect == null) {
                this.handleLength = end2.distanceTo(end1) / 3.0;
                return;
            }
            this.radius = Math.abs(intersect[1]);
            this.handleLength = this.radius * factor;
            if (Mth.equal((double)this.handleLength, (double)0.0)) {
                this.handleLength = 1.0;
            }
        }
    }

    private static class Bezierator
    implements Iterator<Segment> {
        private final Segment segment;
        private final Vec3 end1;
        private final Vec3 end2;
        private final Vec3 finish1;
        private final Vec3 finish2;
        private final Vec3 faceNormal1;
        private final Vec3 faceNormal2;
        private final Runtime runtime;

        private Bezierator(BezierConnection bc, Vec3 offset) {
            this.runtime = bc.resolve();
            this.end1 = ((Vec3)bc.starts.getFirst()).add(offset);
            this.end2 = ((Vec3)bc.starts.getSecond()).add(offset);
            this.finish1 = ((Vec3)bc.axes.getFirst()).scale(this.runtime.handleLength).add(this.end1);
            this.finish2 = ((Vec3)bc.axes.getSecond()).scale(this.runtime.handleLength).add(this.end2);
            this.faceNormal1 = (Vec3)bc.normals.getFirst();
            this.faceNormal2 = (Vec3)bc.normals.getSecond();
            this.segment = new Segment();
            this.segment.index = -1;
        }

        @Override
        public boolean hasNext() {
            return this.segment.index + 1 <= this.runtime.segments;
        }

        @Override
        public Segment next() {
            ++this.segment.index;
            float t = this.runtime.getSegmentT(this.segment.index);
            this.segment.position = VecHelper.bezier((Vec3)this.end1, (Vec3)this.end2, (Vec3)this.finish1, (Vec3)this.finish2, (float)t);
            this.segment.derivative = VecHelper.bezierDerivative((Vec3)this.end1, (Vec3)this.end2, (Vec3)this.finish1, (Vec3)this.finish2, (float)t).normalize();
            this.segment.faceNormal = this.faceNormal1.equals((Object)this.faceNormal2) ? this.faceNormal1 : VecHelper.slerp((float)t, (Vec3)this.faceNormal1, (Vec3)this.faceNormal2);
            this.segment.normal = this.segment.faceNormal.cross(this.segment.derivative).normalize();
            return this.segment;
        }
    }

    public static class Segment {
        public int index;
        public Vec3 position;
        public Vec3 derivative;
        public Vec3 faceNormal;
        public Vec3 normal;
    }

    public static class SegmentAngles {
        public final int length;
        @NotNull
        public final PoseStack.Pose[] tieTransform;
        @NotNull
        public final Couple<PoseStack.Pose>[] railTransforms;
        @NotNull
        public final BlockPos[] lightPosition;

        private SegmentAngles(BezierConnection bc) {
            int segmentCount = bc.getSegmentCount();
            this.length = segmentCount + 1;
            this.tieTransform = new PoseStack.Pose[segmentCount + 1];
            this.railTransforms = new Couple[segmentCount + 1];
            this.lightPosition = new BlockPos[segmentCount + 1];
            Couple previousOffsets = null;
            for (Segment segment : bc) {
                int i = segment.index;
                boolean end = i == 0 || i == segmentCount;
                Couple railOffsets = Couple.create((Object)segment.position.add(segment.normal.scale((double)0.965f)), (Object)segment.position.subtract(segment.normal.scale((double)0.965f)));
                Vec3 railMiddle = ((Vec3)railOffsets.getFirst()).add((Vec3)railOffsets.getSecond()).scale(0.5);
                if (previousOffsets == null) {
                    previousOffsets = railOffsets;
                    continue;
                }
                Vec3 prevMiddle = ((Vec3)previousOffsets.getFirst()).add((Vec3)previousOffsets.getSecond()).scale(0.5);
                Vec3 tieAngles = TrackRenderer.getModelAngles(segment.normal, railMiddle.subtract(prevMiddle));
                this.lightPosition[i] = BlockPos.containing((Position)railMiddle);
                this.railTransforms[i] = Couple.create(null, null);
                PoseStack poseStack = new PoseStack();
                ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).translate(prevMiddle)).rotateY((float)tieAngles.y)).rotateX((float)tieAngles.x)).rotateZ((float)tieAngles.z)).translate(-0.5f, -0.12890625f, 0.0f);
                this.tieTransform[i] = poseStack.last();
                float scale = end ? 2.2f : 2.1f;
                for (boolean first : Iterate.trueAndFalse) {
                    Vec3 railI = (Vec3)railOffsets.get(first);
                    Vec3 prevI = (Vec3)previousOffsets.get(first);
                    Vec3 diff = railI.subtract(prevI);
                    Vec3 anglesI = TrackRenderer.getModelAngles(segment.normal, diff);
                    poseStack = new PoseStack();
                    ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).translate(prevI)).rotateY((float)anglesI.y)).rotateX((float)anglesI.x)).rotateZ((float)anglesI.z)).translate(0.0f, -0.12890625f, -0.03125f).scale(1.0f, 1.0f, (float)diff.length() * scale);
                    this.railTransforms[i].set(first, (Object)poseStack.last());
                }
                previousOffsets = railOffsets;
            }
        }
    }

    public static class GirderAngles {
        public final int length;
        public final Couple<PoseStack.Pose>[] beams;
        public final Couple<Couple<PoseStack.Pose>>[] beamCaps;
        public final BlockPos[] lightPosition;

        private GirderAngles(BezierConnection bc) {
            int segmentCount = bc.getSegmentCount();
            this.length = segmentCount + 1;
            this.beams = new Couple[this.length];
            this.beamCaps = new Couple[this.length];
            this.lightPosition = new BlockPos[this.length];
            Couple previousOffsets = null;
            for (Segment segment : bc) {
                int i = segment.index;
                boolean end = i == 0 || i == segmentCount;
                Vec3 leftGirder = segment.position.add(segment.normal.scale((double)0.965f));
                Vec3 rightGirder = segment.position.subtract(segment.normal.scale((double)0.965f));
                Vec3 upNormal = segment.derivative.normalize().cross(segment.normal);
                Vec3 firstGirderOffset = upNormal.scale(-0.5);
                Vec3 secondGirderOffset = upNormal.scale(-0.625);
                Vec3 leftTop = segment.position.add(segment.normal.scale(1.0)).add(firstGirderOffset);
                Vec3 rightTop = segment.position.subtract(segment.normal.scale(1.0)).add(firstGirderOffset);
                Vec3 leftBottom = leftTop.add(secondGirderOffset);
                Vec3 rightBottom = rightTop.add(secondGirderOffset);
                this.lightPosition[i] = BlockPos.containing((Position)leftGirder.add(rightGirder).scale(0.5));
                Couple offsets = Couple.create((Object)Couple.create((Object)leftTop, (Object)rightTop), (Object)Couple.create((Object)leftBottom, (Object)rightBottom));
                if (previousOffsets == null) {
                    previousOffsets = offsets;
                    continue;
                }
                this.beams[i] = Couple.create(null, null);
                this.beamCaps[i] = Couple.create((Object)Couple.create(null, null), (Object)Couple.create(null, null));
                float scale = end ? 2.3f : 2.2f;
                for (boolean first : Iterate.trueAndFalse) {
                    Vec3 currentBeam = ((Vec3)((Couple)offsets.getFirst()).get(first)).add((Vec3)((Couple)offsets.getSecond()).get(first)).scale(0.5);
                    Vec3 previousBeam = ((Vec3)((Couple)previousOffsets.getFirst()).get(first)).add((Vec3)((Couple)previousOffsets.getSecond()).get(first)).scale(0.5);
                    Vec3 beamDiff = currentBeam.subtract(previousBeam);
                    Vec3 beamAngles = TrackRenderer.getModelAngles(segment.normal, beamDiff);
                    PoseStack poseStack = new PoseStack();
                    ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).translate(previousBeam)).rotateY((float)beamAngles.y)).rotateX((float)beamAngles.x)).rotateZ((float)beamAngles.z)).translate(0.0f, 0.125f + (float)(segment.index % 2 == 0 ? 1 : -1) / 2048.0f - 9.765625E-4f, -0.03125f).scale(1.0f, 1.0f, (float)beamDiff.length() * scale);
                    this.beams[i].set(first, (Object)poseStack.last());
                    for (boolean top : Iterate.trueAndFalse) {
                        Vec3 current = (Vec3)((Couple)offsets.get(top)).get(first);
                        Vec3 previous = (Vec3)((Couple)previousOffsets.get(top)).get(first);
                        Vec3 diff = current.subtract(previous);
                        Vec3 capAngles = TrackRenderer.getModelAngles(segment.normal, diff);
                        poseStack = new PoseStack();
                        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).translate(previous)).rotateY((float)capAngles.y)).rotateX((float)capAngles.x)).rotateZ((float)capAngles.z)).translate(0.0f, 0.125f + (float)(segment.index % 2 == 0 ? 1 : -1) / 2048.0f - 9.765625E-4f, -0.03125f).rotateZ(top ? 0.0f : 0.0f)).scale(1.0f, 1.0f, (float)diff.length() * scale);
                        ((Couple)this.beamCaps[i].get(top)).set(first, (Object)poseStack.last());
                    }
                }
                previousOffsets = offsets;
            }
        }
    }
}

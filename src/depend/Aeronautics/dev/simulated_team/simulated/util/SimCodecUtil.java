/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  dev.ryanhcode.sable.api.physics.force.ForceGroup
 *  dev.ryanhcode.sable.api.physics.force.ForceGroups
 *  dev.ryanhcode.sable.api.physics.force.QueuedForceGroup$PointForce
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.util.SableBufferUtils
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.Registry
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.util.SableBufferUtils;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SimCodecUtil {
    public static final StreamCodec<ByteBuf, Vector3d> STREAM_VECTOR3D = StreamCodec.of(SableBufferUtils::write, x -> SableBufferUtils.read((ByteBuf)x, (Vector3d)new Vector3d()));
    public static final StreamCodec<ByteBuf, Vector3dc> STREAM_VECTOR3DC = ByteBufCodecs.DOUBLE.apply(ByteBufCodecs.list((int)3)).map(l -> new Vector3d(((Double)l.getFirst()).doubleValue(), ((Double)l.get(1)).doubleValue(), ((Double)l.get(2)).doubleValue()), v -> List.of(Double.valueOf(v.x()), Double.valueOf(v.y()), Double.valueOf(v.z())));
    public static final StreamCodec<ByteBuf, BoundingBox3d> BOUNDING_BOX_3D_STREAM_CODEC = ByteBufCodecs.DOUBLE.apply(ByteBufCodecs.list((int)6)).map(l -> new BoundingBox3d(((Double)l.getFirst()).doubleValue(), ((Double)l.get(1)).doubleValue(), ((Double)l.get(2)).doubleValue(), ((Double)l.get(3)).doubleValue(), ((Double)l.get(4)).doubleValue(), ((Double)l.get(5)).doubleValue()), bb -> List.of(Double.valueOf(bb.minX), Double.valueOf(bb.minY), Double.valueOf(bb.minZ), Double.valueOf(bb.maxX), Double.valueOf(bb.maxY), Double.valueOf(bb.maxZ)));
    public static final StreamCodec<ByteBuf, ForceGroup> STREAM_FORCE_GROUP = ResourceLocation.STREAM_CODEC.map(arg_0 -> ((Registry)ForceGroups.REGISTRY).get(arg_0), arg_0 -> ((Registry)ForceGroups.REGISTRY).getKey(arg_0));
    public static final StreamCodec<ByteBuf, QueuedForceGroup.PointForce> STREAM_POINT_FORCE = STREAM_VECTOR3DC.apply(ByteBufCodecs.list((int)2)).map(l -> new QueuedForceGroup.PointForce((Vector3dc)l.getFirst(), (Vector3dc)l.get(1)), p -> List.of(p.point(), p.force()));

    public static <T> Codec<T> withAlternative(Codec<T> first, Codec<T> second) {
        return new WithAlternativeButGood<T>(first, second);
    }

    private record WithAlternativeButGood<T>(Codec<T> first, Codec<T> second) implements Codec<T>
    {
        public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
            DataResult result = this.first.decode(ops, input);
            if (result.isSuccess()) {
                return result;
            }
            return this.second.decode(ops, input);
        }

        public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
            DataResult result = this.first.encode(input, ops, prefix);
            if (result.isSuccess()) {
                return result;
            }
            return this.second.encode(input, ops, prefix);
        }
    }
}

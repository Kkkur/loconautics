package com.lycoris.loconautics.allsable;

import java.util.Map;
import java.util.WeakHashMap;

import org.joml.Vector3d;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Spins the bogey wheels (and everything the bogey style animates with them — side rods, shafts) while a
 * Sable train moves, by feeding the distance travelled into Create's own
 * {@link AbstractBogeyBlockEntity#animate} each frame. {@code animate} drives the BE's virtual wheel angle
 * (degrees from distance over wheel radius), which {@code BogeyBlockEntityRenderer} already passes into the
 * bogey style's render — so any Create-compatible bogey animates exactly as it does on a Create train.
 *
 * <p>The travelled distance is measured client-side from the sub-level's interpolated render pose, projected
 * on the bogey's axis so reversing spins the wheels backwards. Render-thread only.
 */
@OnlyIn(Dist.CLIENT)
public final class BogeyWheelAnimator {

    private static final Map<AbstractBogeyBlockEntity, Vector3d> LAST_POS = new WeakHashMap<>();
    /** Client-side index of the train bogey BEs currently being rendered, keyed to their sub-level id —
     *  lets the particle emitter find each train's bogeys without scanning sub-level blocks. Written from
     *  the render thread, read from the client tick, hence the synchronized wrapper. */
    private static final Map<AbstractBogeyBlockEntity, java.util.UUID> RENDERED =
            java.util.Collections.synchronizedMap(new WeakHashMap<>());

    private BogeyWheelAnimator() {
    }

    /** Snapshot of the rendered bogey BEs belonging to the given train sub-level. */
    public static java.util.List<AbstractBogeyBlockEntity> bogeysOf(java.util.UUID subLevelId) {
        java.util.List<AbstractBogeyBlockEntity> out = new java.util.ArrayList<>(2);
        synchronized (RENDERED) {
            for (Map.Entry<AbstractBogeyBlockEntity, java.util.UUID> e : RENDERED.entrySet()) {
                if (subLevelId.equals(e.getValue()) && !e.getKey().isRemoved()) {
                    out.add(e.getKey());
                }
            }
        }
        return out;
    }

    /** Called once per rendered frame per bogey; advances the wheel animation by the distance moved. */
    public static void frame(AbstractBogeyBlockEntity be) {
        ClientSubLevel sub = Sable.HELPER.getContainingClient(be);
        if (sub == null || !SableTrainClientRegistry.isTrain(sub.getUniqueId())) {
            return;
        }
        RENDERED.put(be, sub.getUniqueId());
        Vec3 w = sub.renderPose().transformPosition(Vec3.atCenterOf(be.getBlockPos()));
        Vector3d pos = new Vector3d(w.x, w.y, w.z);
        Vector3d last = LAST_POS.put(be, pos);
        if (last == null) {
            return;
        }
        double dx = pos.x - last.x;
        double dz = pos.z - last.z;
        if (dx * dx + dz * dz < 1.0e-10) {
            return;
        }
        // Signed travel along the bogey's axis (world space), so driving in reverse spins the wheels back.
        BlockState state = be.getBlockState();
        Direction.Axis axis = state.hasProperty(AbstractBogeyBlock.AXIS)
                ? state.getValue(AbstractBogeyBlock.AXIS) : Direction.Axis.X;
        Vector3d a = axis == Direction.Axis.X ? new Vector3d(1, 0, 0) : new Vector3d(0, 0, 1);
        sub.renderPose().orientation().transform(a);
        double dist = dx * a.x + dz * a.z;
        be.animate((float) dist);
    }
}

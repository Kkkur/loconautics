package com.lycoris.loconautics.allsable;

import java.util.Map;
import java.util.WeakHashMap;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;

import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Per-frame smoothing of the server-synced bogey visual pose ({@link BogeyYawVisual}), playing the role
 * of Create's {@code LerpedFloat} on {@code CarriageBogey.yaw/pitch}: the authoritative rail values
 * arrive at tick rate (20&nbsp;Hz), and the renderer eases the displayed yaw/offset toward them each
 * frame so the wheels turn fluidly instead of stepping.
 *
 * <p>Render-thread only.
 */
@OnlyIn(Dist.CLIENT)
public final class BogeyVisualSmoother {

    /** Approach rate (1/s): higher = snappier, lower = smoother. ~14 ≈ settled in under 1/4 second. */
    private static final double RATE = 14.0;

    private static final class State {
        float yaw;
        float ox;
        float oy;
        float oz;
        long nanos;
        boolean initialized;
    }

    private static final Map<AbstractBogeyBlockEntity, State> STATES = new WeakHashMap<>();

    private BogeyVisualSmoother() {
    }

    /**
     * Eases this bogey's displayed pose toward the given target values and returns the smoothed
     * {@code [yaw, offX, offY, offZ]} for this frame.
     */
    public static float[] smooth(AbstractBogeyBlockEntity be, float yaw, float ox, float oy, float oz) {
        State s = STATES.computeIfAbsent(be, k -> new State());
        long now = System.nanoTime();
        if (!s.initialized) {
            s.yaw = yaw;
            s.ox = ox;
            s.oy = oy;
            s.oz = oz;
            s.initialized = true;
        } else {
            double dt = Math.min(0.25, Math.max(0.0, (now - s.nanos) / 1.0e9));
            float a = (float) (1.0 - Math.exp(-RATE * dt));
            s.yaw += Mth.wrapDegrees(yaw - s.yaw) * a;
            s.ox += (ox - s.ox) * a;
            s.oy += (oy - s.oy) * a;
            s.oz += (oz - s.oz) * a;
        }
        s.nanos = now;
        return new float[] {s.yaw, s.ox, s.oy, s.oz};
    }
}

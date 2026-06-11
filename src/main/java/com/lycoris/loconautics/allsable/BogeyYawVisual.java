package com.lycoris.loconautics.allsable;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3f;

/**
 * Reads/writes the extra visual pose of a bogey — a "local yaw" float plus a "local offset" vector —
 * on a {@link AbstractBogeyBlockEntity}'s existing {@code bogeyData} NBT compound.
 *
 * <p>This is how Loconautics makes a bogey block <b>visually</b> follow the track like Create's train
 * bogeys do, without detaching it from the cart's body sub-level (unlike Create's contraption-based
 * trains, our bogey blocks stay part of the body and never move on their own — see
 * {@link SableTrainDriver}):
 * <ul>
 *   <li><b>yaw</b> — the angle between the body's forward and the rail tangent under the bogey, so the
 *       wheels pivot into curves;</li>
 *   <li><b>offset</b> — the displacement (in the body sub-level's local axes) from the bogey block's
 *       rigid position to its actual point ON the rail. The body sits on the chord between its bogeys,
 *       so on curves the rigid position drifts off the rail — Create renders each bogey at its own rail
 *       point, and this offset reproduces that.</li>
 * </ul>
 *
 * <p>The values are stored in the bogey's own {@code bogeyData} compound, which
 * {@link AbstractBogeyBlockEntity#setBogeyData} already keeps networked to the client via the normal
 * block-entity update path, so no extra packet plumbing is needed.
 *
 * <p>The client-side mixin ({@code BogeyYawRendererMixin}) reads these values each frame and applies
 * them to the bogey's render pose stack (translate to the rail point, then rotate about the block's
 * vertical centerline).
 */
public final class BogeyYawVisual {

    private static final String LOCAL_YAW_KEY = "LoconauticsLocalYaw";
    private static final String LOCAL_OFFSET_X_KEY = "LoconauticsLocalOffsetX";
    private static final String LOCAL_OFFSET_Y_KEY = "LoconauticsLocalOffsetY";
    private static final String LOCAL_OFFSET_Z_KEY = "LoconauticsLocalOffsetZ";

    private BogeyYawVisual() {
    }

    /** Reads the bogey's current extra yaw (degrees), or {@code 0} if never set. */
    public static float getLocalYaw(AbstractBogeyBlockEntity be) {
        CompoundTag data = be.getBogeyData();
        return data != null && data.contains(LOCAL_YAW_KEY) ? data.getFloat(LOCAL_YAW_KEY) : 0.0f;
    }

    /** Reads the bogey's visual offset (sub-level local axes) into {@code dest}; zero if never set. */
    public static Vector3f getLocalOffset(AbstractBogeyBlockEntity be, Vector3f dest) {
        CompoundTag data = be.getBogeyData();
        if (data == null || !data.contains(LOCAL_OFFSET_X_KEY)) {
            return dest.zero();
        }
        return dest.set(data.getFloat(LOCAL_OFFSET_X_KEY),
                data.getFloat(LOCAL_OFFSET_Y_KEY),
                data.getFloat(LOCAL_OFFSET_Z_KEY));
    }

    /**
     * Sets the bogey's visual pose (yaw in degrees + offset in sub-level local axes) and marks it
     * changed so the new values are sent to clients. Skips the update entirely if nothing has
     * meaningfully changed, to avoid spamming block updates every tick.
     */
    public static void setLocalVisual(AbstractBogeyBlockEntity be, float yawDegrees,
                                      float offX, float offY, float offZ) {
        CompoundTag data = be.getBogeyData();
        float curYaw = 0.0f, curX = 0.0f, curY = 0.0f, curZ = 0.0f;
        if (data != null) {
            curYaw = data.getFloat(LOCAL_YAW_KEY);
            curX = data.getFloat(LOCAL_OFFSET_X_KEY);
            curY = data.getFloat(LOCAL_OFFSET_Y_KEY);
            curZ = data.getFloat(LOCAL_OFFSET_Z_KEY);
        }
        if (Math.abs(curYaw - yawDegrees) < 0.05f
                && Math.abs(curX - offX) < 0.005f
                && Math.abs(curY - offY) < 0.005f
                && Math.abs(curZ - offZ) < 0.005f) {
            return;
        }
        if (data == null) {
            data = new CompoundTag();
        }
        data.putFloat(LOCAL_YAW_KEY, yawDegrees);
        data.putFloat(LOCAL_OFFSET_X_KEY, offX);
        data.putFloat(LOCAL_OFFSET_Y_KEY, offY);
        data.putFloat(LOCAL_OFFSET_Z_KEY, offZ);
        be.setBogeyData(data);
        // setBogeyData() does not itself mark/sync (unlike setBogeyStyle) — do it ourselves so the
        // new values reach the client.
        be.setChanged();
        var level = be.getLevel();
        if (level != null) {
            var pos = be.getBlockPos();
            var state = level.getBlockState(pos);
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }
}

package com.lycoris.loconautics.allsable;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import net.minecraft.nbt.CompoundTag;

/**
 * Reads/writes an extra "local yaw" float on a {@link AbstractBogeyBlockEntity}'s existing
 * {@code bogeyData} NBT compound.
 *
 * <p>This is how Loconautics makes a bogey block <b>visually</b> pivot on curves without detaching it
 * from the cart's body sub-level (unlike Create's contraption-based trains, our bogey blocks stay part
 * of the body and never move on their own — see {@link SableTrainDriver}).
 *
 * <p>The value is stored in the bogey's own {@code bogeyData} compound, which
 * {@link AbstractBogeyBlockEntity#setBogeyData} already keeps networked to the client via the normal
 * block-entity update path, so no extra packet plumbing is needed.
 *
 * <p>The client-side mixin ({@code BogeyYawVisualMixin}) reads this value when building the bogey's
 * base render pose stack and applies it as an extra rotation around the block's vertical (Y) axis,
 * mirroring how Create's {@code StandardBogeyVisual} rotates wheel/shaft partial models independently
 * of the static block — except here the WHOLE bogey assembly (frame + wheels + shafts) turns together.
 */
public final class BogeyYawVisual {

    private static final String LOCAL_YAW_KEY = "LoconauticsLocalYaw";

    private BogeyYawVisual() {
    }

    /** Reads the bogey's current extra yaw (degrees), or {@code 0} if never set. */
    public static float getLocalYaw(AbstractBogeyBlockEntity be) {
        CompoundTag data = be.getBogeyData();
        return data != null && data.contains(LOCAL_YAW_KEY) ? data.getFloat(LOCAL_YAW_KEY) : 0.0f;
    }

    /**
     * Sets the bogey's extra yaw (degrees) and marks it changed so the new value is sent to clients.
     * Skips the update entirely if the value hasn't meaningfully changed, to avoid spamming block
     * updates every tick.
     */
    public static void setLocalYaw(AbstractBogeyBlockEntity be, float yawDegrees) {
        CompoundTag data = be.getBogeyData();
        float current = data != null && data.contains(LOCAL_YAW_KEY) ? data.getFloat(LOCAL_YAW_KEY) : 0.0f;
        if (Math.abs(current - yawDegrees) < 0.05f) {
            return;
        }
        if (data == null) {
            data = new CompoundTag();
        }
        data.putFloat(LOCAL_YAW_KEY, yawDegrees);
        be.setBogeyData(data);
        // setBogeyData() does not itself mark/sync (unlike setBogeyStyle) — do it ourselves so the
        // new yaw reaches the client.
        be.setChanged();
        var level = be.getLevel();
        if (level != null) {
            var pos = be.getBlockPos();
            var state = level.getBlockState(pos);
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }
}
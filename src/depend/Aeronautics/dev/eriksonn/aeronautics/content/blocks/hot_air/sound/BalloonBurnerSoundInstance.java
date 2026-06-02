/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.sound;

import dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner.HotAirBurnerBlockEntity;
import dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent.SteamVentBlockEntity;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class BalloonBurnerSoundInstance
extends AbstractTickableSoundInstance {
    public static final BalloonBurnerSoundInstance GLOBAL_HOT_AIR_BURNER_SOUND = new BalloonBurnerSoundInstance(AeroSoundEvents.HOT_AIR_BURNER_HEAT.event());
    public static final BalloonBurnerSoundInstance GLOBAL_STEAM_VENT_AIR_BURNER_SOUND = new BalloonBurnerSoundInstance(AeroSoundEvents.STEAM_VENT_HEAT.event());
    private static final int MAX_DISTANCE = 10;
    private static final float VOLUME_SCALE = 0.325f;
    private final Set<BlockPos> NEARBY_BLOCKS = new HashSet<BlockPos>();
    private final Vector3d meanPos = new Vector3d();
    private float meanPitch = 0.0f;
    private float meanVolume = 0.0f;

    public BalloonBurnerSoundInstance(SoundEvent sound) {
        super(sound, SoundSource.AMBIENT, RandomSource.create());
        this.looping = true;
        this.delay = 0;
        this.volume = 0.001f;
        this.pitch = 0.001f;
    }

    public void addPos(BlockPos pos) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (BalloonBurnerSoundInstance.distSquared(camera, pos) < 100.0 && this.NEARBY_BLOCKS.add(pos)) {
            this.updateMeanPos();
        }
    }

    public void removePos(BlockPos pos) {
        this.NEARBY_BLOCKS.remove(pos);
        this.updateMeanPos();
    }

    private void updateMeanPos() {
        this.meanPos.zero();
        Vector3d v = new Vector3d();
        if (!this.NEARBY_BLOCKS.isEmpty()) {
            for (BlockPos nearby : this.NEARBY_BLOCKS) {
                v.set((double)nearby.getX() + 0.5, (double)nearby.getY() + 0.5, (double)nearby.getZ() + 0.5);
                ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Vector3dc)v);
                if (subLevel != null) {
                    subLevel.logicalPose().transformPosition(v);
                }
                this.meanPos.add((Vector3dc)v);
            }
            this.meanPos.div((double)this.NEARBY_BLOCKS.size());
        }
    }

    private void updateInformation() {
        ClientLevel level = Minecraft.getInstance().level;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        this.meanVolume = 0.001f;
        int volumeChangers = 0;
        Iterator<BlockPos> iter = this.NEARBY_BLOCKS.iterator();
        while (iter.hasNext()) {
            BlockPos next = iter.next();
            if (next == null) continue;
            if (BalloonBurnerSoundInstance.distSquared(camera, next) > 100.0) {
                iter.remove();
                this.updateMeanPos();
                continue;
            }
            BlockEntity be = level.getBlockEntity(next);
            float intensityScaling = 0.0f;
            if (be instanceof HotAirBurnerBlockEntity) {
                HotAirBurnerBlockEntity hbe = (HotAirBurnerBlockEntity)be;
                intensityScaling = Mth.clamp((float)hbe.getClientIntensity().getValue(), (float)0.0f, (float)1.0f);
            } else if (be instanceof SteamVentBlockEntity) {
                SteamVentBlockEntity sbe = (SteamVentBlockEntity)be;
                intensityScaling = Mth.clamp((float)sbe.getClientIntensity().getValue(), (float)0.0f, (float)1.0f);
            } else {
                iter.remove();
                this.updateMeanPos();
                continue;
            }
            this.meanVolume += Math.max(Math.min(2.0f, intensityScaling * 4.0f), 0.0f);
            ++volumeChangers;
        }
        if (!this.NEARBY_BLOCKS.isEmpty()) {
            this.meanPitch = 1.0f;
            this.meanVolume /= (float)volumeChangers;
            this.meanVolume *= (float)(1.0 - Math.sqrt(BalloonBurnerSoundInstance.distSquared(camera, JOMLConversion.toMojang((Vector3dc)this.meanPos))) / 10.0);
        }
    }

    private static double distSquared(Camera camera, Vec3 pos) {
        ClientLevel level = Minecraft.getInstance().level;
        return Sable.HELPER.distanceSquaredWithSubLevels((Level)level, (Position)camera.getPosition(), (Position)pos);
    }

    private static double distSquared(Camera camera, BlockPos pos) {
        return BalloonBurnerSoundInstance.distSquared(camera, pos.getCenter());
    }

    public void tick() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        this.updateInformation();
        if (this.NEARBY_BLOCKS.isEmpty()) {
            this.volume = 0.001f;
            this.pitch = 0.001f;
            this.meanPos.zero();
            return;
        }
        this.x = this.meanPos.x;
        this.y = this.meanPos.y;
        this.z = this.meanPos.z;
        this.volume = this.meanVolume * 0.325f;
        this.pitch = this.meanPitch;
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean canPlaySound() {
        ClientLevel level = Minecraft.getInstance().level;
        return level != null;
    }

    public boolean isStopped() {
        ClientLevel level = Minecraft.getInstance().level;
        return level == null;
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.TickableSoundInstance
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.core.BlockPos
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.util;

import dev.eriksonn.aeronautics.content.blocks.hot_air.sound.BalloonBurnerSoundInstance;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlockEntity;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.sound.PropellerBearingSoundHolder;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.sound.PropellerBearingSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AeroSoundDistUtil {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static Object tickPropellerSounds(PropellerBearingBlockEntity be, @Nullable Object soundInstance) {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        boolean needsNewSounds = false;
        if (soundInstance instanceof PropellerBearingSoundHolder) {
            PropellerBearingSoundInstance large;
            PropellerBearingSoundInstance small;
            PropellerBearingSoundHolder propellerBearingSoundHolder = (PropellerBearingSoundHolder)soundInstance;
            try {
                PropellerBearingSoundInstance propellerBearingSoundInstance;
                small = propellerBearingSoundInstance = propellerBearingSoundHolder.small();
                large = propellerBearingSoundInstance = propellerBearingSoundHolder.large();
            }
            catch (Throwable throwable) {
                throw new MatchException(throwable.toString(), throwable);
            }
            if (small.isStopped() || large.isStopped()) {
                soundManager.stop((SoundInstance)small);
                soundManager.stop((SoundInstance)large);
                needsNewSounds = true;
            }
        }
        if (soundInstance == null) {
            needsNewSounds = true;
        }
        if (!needsNewSounds) return soundInstance;
        PropellerBearingSoundInstance smallSound = new PropellerBearingSoundInstance(be, false);
        PropellerBearingSoundInstance largeSound = new PropellerBearingSoundInstance(be, true);
        soundManager.queueTickingSound((TickableSoundInstance)smallSound);
        soundManager.queueTickingSound((TickableSoundInstance)largeSound);
        return new PropellerBearingSoundHolder(smallSound, largeSound);
    }

    public static void tickGlobalBurnerSound() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        SoundManager soundManager = minecraft.getSoundManager();
        if (level != null && !minecraft.isPaused()) {
            if (!soundManager.isActive((SoundInstance)BalloonBurnerSoundInstance.GLOBAL_HOT_AIR_BURNER_SOUND)) {
                soundManager.queueTickingSound((TickableSoundInstance)BalloonBurnerSoundInstance.GLOBAL_HOT_AIR_BURNER_SOUND);
            }
            if (!soundManager.isActive((SoundInstance)BalloonBurnerSoundInstance.GLOBAL_STEAM_VENT_AIR_BURNER_SOUND)) {
                soundManager.queueTickingSound((TickableSoundInstance)BalloonBurnerSoundInstance.GLOBAL_STEAM_VENT_AIR_BURNER_SOUND);
            }
        }
    }

    public static void addPosHotAirBurnerSound(BlockPos pos) {
        BalloonBurnerSoundInstance.GLOBAL_HOT_AIR_BURNER_SOUND.addPos(pos);
    }

    public static void removePosHotAirBurnerSound(BlockPos pos) {
        BalloonBurnerSoundInstance.GLOBAL_HOT_AIR_BURNER_SOUND.removePos(pos);
    }

    public static void addPosSteamVentSound(BlockPos pos) {
        BalloonBurnerSoundInstance.GLOBAL_STEAM_VENT_AIR_BURNER_SOUND.addPos(pos);
    }

    public static void removePosSteamVentSound(BlockPos pos) {
        BalloonBurnerSoundInstance.GLOBAL_STEAM_VENT_AIR_BURNER_SOUND.removePos(pos);
    }
}

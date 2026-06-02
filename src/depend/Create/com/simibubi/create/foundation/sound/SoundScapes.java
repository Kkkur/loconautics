/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.entity.Entity
 */
package com.simibubi.create.foundation.sound;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.sound.SoundScape;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;

public class SoundScapes {
    static final int MAX_AMBIENT_SOURCE_DISTANCE = 16;
    static final int UPDATE_INTERVAL = 5;
    static final int SOUND_VOLUME_ARG_MAX = 15;
    private static Map<AmbienceGroup, Map<PitchGroup, Set<BlockPos>>> counter = new IdentityHashMap<AmbienceGroup, Map<PitchGroup, Set<BlockPos>>>();
    private static Map<Pair<AmbienceGroup, PitchGroup>, SoundScape> activeSounds = new HashMap<Pair<AmbienceGroup, PitchGroup>, SoundScape>();

    private static SoundScape kinetic(float pitch, AmbienceGroup group) {
        return new SoundScape(pitch, group).continuous(SoundEvents.MINECART_INSIDE, 0.25f, 1.0f);
    }

    private static SoundScape cogwheel(float pitch, AmbienceGroup group) {
        return new SoundScape(pitch, group).continuous(AllSoundEvents.COGS.getMainEvent(), 1.5f, 1.0f);
    }

    private static SoundScape crushing(float pitch, AmbienceGroup group) {
        return new SoundScape(pitch, group).repeating(AllSoundEvents.CRUSHING_1.getMainEvent(), 1.545f, 0.75f, 1).repeating(AllSoundEvents.CRUSHING_2.getMainEvent(), 0.425f, 0.75f, 2).repeating(AllSoundEvents.CRUSHING_3.getMainEvent(), 2.0f, 1.75f, 2);
    }

    private static SoundScape milling(float pitch, AmbienceGroup group) {
        return new SoundScape(pitch, group).repeating(AllSoundEvents.CRUSHING_1.getMainEvent(), 1.545f, 0.75f, 1).repeating(AllSoundEvents.CRUSHING_2.getMainEvent(), 0.425f, 0.75f, 2);
    }

    public static void play(AmbienceGroup group, BlockPos pos, float pitch) {
        if (!((Boolean)AllConfigs.client().enableAmbientSounds.get()).booleanValue()) {
            return;
        }
        if (!SoundScapes.outOfRange(pos)) {
            SoundScapes.addSound(group, pos, pitch);
        }
    }

    public static void tick() {
        activeSounds.values().forEach(SoundScape::tick);
        if (AnimationTickHolder.getTicks() % 5 != 0) {
            return;
        }
        boolean disable = (Boolean)AllConfigs.client().enableAmbientSounds.get() == false;
        Iterator<Map.Entry<Pair<AmbienceGroup, PitchGroup>, SoundScape>> iterator = activeSounds.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Pair<AmbienceGroup, PitchGroup>, SoundScape> entry = iterator.next();
            Pair<AmbienceGroup, PitchGroup> key = entry.getKey();
            SoundScape value = entry.getValue();
            if (!disable && SoundScapes.getSoundCount((AmbienceGroup)((Object)key.getFirst()), (PitchGroup)((Object)key.getSecond())) != 0) continue;
            value.remove();
            iterator.remove();
        }
        counter.values().forEach(m -> m.values().forEach(Set::clear));
    }

    private static void addSound(AmbienceGroup group, BlockPos pos, float pitch) {
        PitchGroup groupFromPitch = SoundScapes.getGroupFromPitch(pitch);
        Set set = counter.computeIfAbsent(group, ag -> new IdentityHashMap()).computeIfAbsent(groupFromPitch, pg -> new HashSet());
        set.add(pos);
        Pair pair = Pair.of((Object)((Object)group), (Object)((Object)groupFromPitch));
        activeSounds.computeIfAbsent((Pair<AmbienceGroup, PitchGroup>)pair, $ -> {
            SoundScape soundScape = group.instantiate(pitch);
            soundScape.play();
            return soundScape;
        });
    }

    public static void invalidateAll() {
        counter.clear();
        activeSounds.forEach(($, sound) -> sound.remove());
        activeSounds.clear();
    }

    protected static boolean outOfRange(BlockPos pos) {
        return !SoundScapes.getCameraPos().closerThan((Vec3i)pos, 16.0);
    }

    protected static BlockPos getCameraPos() {
        Entity renderViewEntity = Minecraft.getInstance().cameraEntity;
        if (renderViewEntity == null) {
            return BlockPos.ZERO;
        }
        return renderViewEntity.blockPosition();
    }

    public static int getSoundCount(AmbienceGroup group, PitchGroup pitchGroup) {
        return SoundScapes.getAllLocations(group, pitchGroup).size();
    }

    public static Set<BlockPos> getAllLocations(AmbienceGroup group, PitchGroup pitchGroup) {
        return counter.getOrDefault((Object)group, Collections.emptyMap()).getOrDefault((Object)pitchGroup, Collections.emptySet());
    }

    public static PitchGroup getGroupFromPitch(float pitch) {
        if ((double)pitch < 0.7) {
            return PitchGroup.VERY_LOW;
        }
        if ((double)pitch < 0.9) {
            return PitchGroup.LOW;
        }
        if ((double)pitch < 1.1) {
            return PitchGroup.NORMAL;
        }
        if ((double)pitch < 1.3) {
            return PitchGroup.HIGH;
        }
        return PitchGroup.VERY_HIGH;
    }

    public static enum AmbienceGroup {
        KINETIC(SoundScapes::kinetic),
        COG(SoundScapes::cogwheel),
        CRUSHING(SoundScapes::crushing),
        MILLING(SoundScapes::milling);

        private BiFunction<Float, AmbienceGroup, SoundScape> factory;

        private AmbienceGroup(BiFunction<Float, AmbienceGroup, SoundScape> factory) {
            this.factory = factory;
        }

        public SoundScape instantiate(float pitch) {
            return this.factory.apply(Float.valueOf(pitch), this);
        }
    }

    static enum PitchGroup {
        VERY_LOW,
        LOW,
        NORMAL,
        HIGH,
        VERY_HIGH;

    }
}

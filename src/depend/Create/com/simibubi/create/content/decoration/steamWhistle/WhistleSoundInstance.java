/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.decoration.steamWhistle;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class WhistleSoundInstance
extends AbstractTickableSoundInstance {
    private boolean active;
    private int keepAlive;
    private WhistleBlock.WhistleSize size;

    public WhistleSoundInstance(WhistleBlock.WhistleSize size, BlockPos worldPosition) {
        super((size == WhistleBlock.WhistleSize.SMALL ? AllSoundEvents.WHISTLE_HIGH : (size == WhistleBlock.WhistleSize.MEDIUM ? AllSoundEvents.WHISTLE_MEDIUM : AllSoundEvents.WHISTLE_LOW)).getMainEvent(), SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.size = size;
        this.looping = true;
        this.active = true;
        this.volume = 0.05f;
        this.delay = 0;
        this.keepAlive();
        Vec3 v = Vec3.atCenterOf((Vec3i)worldPosition);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public WhistleBlock.WhistleSize getOctave() {
        return this.size;
    }

    public void fadeOut() {
        this.active = false;
    }

    public void keepAlive() {
        this.keepAlive = 2;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void tick() {
        if (this.active) {
            this.volume = Math.min(1.0f, this.volume + 0.25f);
            --this.keepAlive;
            if (this.keepAlive == 0) {
                this.fadeOut();
            }
            return;
        }
        this.volume = Math.max(0.0f, this.volume - 0.25f);
        if (this.volume == 0.0f) {
            this.stop();
        }
    }
}

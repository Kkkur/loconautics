/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.server.level.ServerLevel
 *  net.neoforged.neoforge.common.util.FakePlayer
 */
package com.simibubi.create.content.contraptions.actors.plough;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.FakePlayer;

static class PloughBlock.PloughFakePlayer
extends FakePlayer {
    public static final GameProfile PLOUGH_PROFILE = new GameProfile(UUID.fromString("9e2faded-eeee-4ec2-c314-dad129ae971d"), "Plough");

    public PloughBlock.PloughFakePlayer(ServerLevel world) {
        super(world, PLOUGH_PROFILE);
    }
}

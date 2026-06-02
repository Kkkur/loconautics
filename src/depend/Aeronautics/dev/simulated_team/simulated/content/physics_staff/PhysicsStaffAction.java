/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.network.codec.StreamCodec
 */
package dev.simulated_team.simulated.content.physics_staff;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.codec.StreamCodec;

public enum PhysicsStaffAction {
    STOP_DRAG,
    LOCK,
    START_DRAG;

    public static final StreamCodec<ByteBuf, PhysicsStaffAction> STREAM_CODEC;

    static {
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(PhysicsStaffAction.class);
    }
}

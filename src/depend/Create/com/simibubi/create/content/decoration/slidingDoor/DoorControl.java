/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.entity.Entity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.decoration.slidingDoor;

import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.function.Consumer;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum DoorControl {
    ALL,
    NORTH,
    EAST,
    SOUTH,
    WEST,
    NONE;

    public static final StreamCodec<ByteBuf, DoorControl> STREAM_CODEC;

    private static String[] valuesAsString() {
        DoorControl[] values = DoorControl.values();
        return Arrays.stream(values).map(dc -> Lang.asId((String)dc.name())).toList().toArray(new String[values.length]);
    }

    public boolean matches(Direction doorDirection) {
        return switch (this.ordinal()) {
            case 0 -> true;
            case 1 -> {
                if (doorDirection == Direction.NORTH) {
                    yield true;
                }
                yield false;
            }
            case 2 -> {
                if (doorDirection == Direction.EAST) {
                    yield true;
                }
                yield false;
            }
            case 3 -> {
                if (doorDirection == Direction.SOUTH) {
                    yield true;
                }
                yield false;
            }
            case 4 -> {
                if (doorDirection == Direction.WEST) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    @OnlyIn(value=Dist.CLIENT)
    public static Pair<ScrollInput, Label> createWidget(int x, int y, Consumer<DoorControl> callback, DoorControl initial) {
        DoorControl playerFacing = NONE;
        Entity cameraEntity = Minecraft.getInstance().cameraEntity;
        if (cameraEntity != null) {
            Direction direction = cameraEntity.getDirection();
            if (direction == Direction.EAST) {
                playerFacing = EAST;
            }
            if (direction == Direction.WEST) {
                playerFacing = WEST;
            }
            if (direction == Direction.NORTH) {
                playerFacing = NORTH;
            }
            if (direction == Direction.SOUTH) {
                playerFacing = SOUTH;
            }
        }
        Label label = new Label(x + 4, y + 6, (Component)Component.empty()).withShadow();
        ScrollInput input = new SelectionScrollInput(x, y, 53, 16).forOptions(CreateLang.translatedOptions("contraption.door_control", DoorControl.valuesAsString())).titled(CreateLang.translateDirect("contraption.door_control", new Object[0])).calling(s -> {
            DoorControl mode = DoorControl.values()[s];
            label.text = CreateLang.translateDirect("contraption.door_control." + Lang.asId((String)mode.name()) + ".short", new Object[0]);
            callback.accept(mode);
        }).addHint(CreateLang.translateDirect("contraption.door_control.player_facing", CreateLang.translateDirect("contraption.door_control." + Lang.asId((String)playerFacing.name()) + ".short", new Object[0]))).setState(initial.ordinal());
        input.onChanged();
        return Pair.of((Object)((Object)input), (Object)((Object)label));
    }

    static {
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(DoorControl.class);
    }
}

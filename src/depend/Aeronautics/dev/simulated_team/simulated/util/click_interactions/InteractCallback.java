/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.util.click_interactions;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface InteractCallback {
    @NotNull
    public static Result filterInteract(InteractCallback clickInteraction, Input input, int modifiers, int action, KeyMappings associatedMappings) {
        if (input.matches(associatedMappings.attack)) {
            return clickInteraction.onAttack(modifiers, action, associatedMappings.attack);
        }
        if (input.matches(associatedMappings.middle)) {
            return clickInteraction.onPick(modifiers, action, associatedMappings.middle);
        }
        if (input.matches(associatedMappings.use)) {
            return clickInteraction.onUse(modifiers, action, associatedMappings.use);
        }
        return Result.empty();
    }

    default public Result onPick(int modifiers, int action, KeyMapping middleKey) {
        return Result.empty();
    }

    default public Result onAttack(int modifiers, int action, KeyMapping leftKey) {
        return Result.empty();
    }

    default public Result onUse(int modifiers, int action, KeyMapping rightKey) {
        return Result.empty();
    }

    default public Result onScroll(double deltaX, double deltaY) {
        return Result.empty();
    }

    default public Result onMouseMove(double yaw, double pitch) {
        return Result.empty();
    }

    default public void clientTick(Level level, LocalPlayer player) {
    }

    public record KeyMappings(KeyMapping use, KeyMapping attack, KeyMapping middle) {
        private static final KeyMappings MAPPINGS = KeyMappings.populateMappings();

        public static KeyMappings getMappings() {
            return MAPPINGS;
        }

        private static KeyMappings populateMappings() {
            Options options = Minecraft.getInstance().options;
            return new KeyMappings(options.keyUse, options.keyAttack, options.keyPickItem);
        }
    }

    public record Input(boolean mouse, int key, int scanCode) {
        public static Input mouse(int key) {
            return new Input(true, key, -1);
        }

        public static Input key(int key, int scanCode) {
            return new Input(false, key, scanCode);
        }

        public boolean matches(KeyMapping mapping) {
            if (this.mouse) {
                return mapping.matchesMouse(this.key);
            }
            return mapping.matches(this.key, this.scanCode);
        }
    }

    public record Result(boolean cancelled) {
        private static final Result EMPTY = new Result(false);

        public static Result empty() {
            return EMPTY;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() != this.getClass()) {
                return false;
            }
            Result otherEvent = (Result)obj;
            return otherEvent.cancelled == this.cancelled;
        }
    }
}

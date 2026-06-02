/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.simibubi.create.AllKeys
 *  net.createmod.catnip.client.ConflictSafeKeyMapping
 *  net.minecraft.client.KeyMapping
 */
package dev.simulated_team.simulated.index;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.AllKeys;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.createmod.catnip.client.ConflictSafeKeyMapping;
import net.minecraft.client.KeyMapping;

public enum SimKeys {
    ROTATE_MODE("rotate_mode", 258, "Physics Staff Rotate Mode"),
    SCROLL_UP("scroll_up", InputConstants.UNKNOWN.getValue(), "Scroll Up"),
    SCROLL_DOWN("scroll_down", InputConstants.UNKNOWN.getValue(), "Scroll Down");

    private KeyMapping keybind;
    private final String description;
    private final String translation;
    private final int key;
    private final boolean modifiable;
    private final boolean conflictSafe;

    private SimKeys(int defaultKey) {
        this("", defaultKey, "");
    }

    private SimKeys(String description, int defaultKey, String translation) {
        this(description, defaultKey, translation, false);
    }

    private SimKeys(String description, int defaultKey, String translation, boolean conflictSafe) {
        this.description = "simulated.keyinfo." + description;
        this.key = defaultKey;
        this.modifiable = !description.isEmpty();
        this.translation = translation;
        this.conflictSafe = conflictSafe;
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (SimKeys key : SimKeys.values()) {
            if (!key.modifiable) continue;
            consumer.accept(key.description, key.translation);
        }
    }

    public static void registerTo(Consumer<KeyMapping> consumer) {
        for (SimKeys key : SimKeys.values()) {
            key.keybind = key.conflictSafe ? new ConflictSafeKeyMapping(key.description, key.key, "Create Simulated") : new KeyMapping(key.description, key.key, "Create Simulated");
            if (!key.modifiable) continue;
            consumer.accept(key.keybind);
        }
    }

    public KeyMapping getKeybind() {
        return this.keybind;
    }

    public boolean isPressed() {
        if (!this.modifiable) {
            return AllKeys.isKeyDown((int)this.key);
        }
        return this.keybind != null && this.keybind.isDown();
    }

    public String getBoundKey() {
        return this.keybind.getTranslatedKeyMessage().getString().toUpperCase();
    }
}

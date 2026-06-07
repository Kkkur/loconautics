package com.lycoris.loconautics.foundation;

import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;

public class LoconauticsLang {

    public static LangBuilder builder() {
        return Lang.builder("loconautics");
    }

    public static LangBuilder translate(String key, Object... args) {
        return builder().translate(key, args);
    }

    public static LangBuilder number(double number) {
        return builder().text(String.format("%.1f", number));
    }
}
/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.theme.Color
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.util;

import java.awt.Color;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SimColors {
    public static int SUCCESS_LIME = new Color(158, 222, 115).getRGB();
    public static int NUH_UH_RED = new Color(255, 113, 113).getRGB();
    public static int REDSTONE_OFF = new Color(86, 1, 1).getRGB();
    public static int REDSTONE_ON = new Color(205, 0, 0).getRGB();
    public static int ADVANCABLE_GOLD = new Color(219, 162, 19).getRGB();
    public static int EPIC_OURPLE = new Color(165, 0, 170).getRGB();
    public static int STRESSED_RED = new Color(235, 50, 48).getRGB();
    public static int THROTTLE_VALUE_BROWN = new Color(68, 32, 0).getRGB();
    public static int ACTIVE_YELLOW = new Color(255, 235, 133).getRGB();
    public static int PERCHANCE_ORANGE = new Color(255, 201, 102).getRGB();
    public static int DISCARDABLE_ORANGE = new Color(255, 161, 102).getRGB();
    public static int TITLE_DARK_RED = new Color(89, 36, 36).getRGB();
    public static int GROSS_BINDING_BROWN = new Color(183, 60, 45).getRGB();
    public static int WOODEN_BROWN = new Color(142, 111, 73).getRGB();
    public static int OFF_WHITE = new Color(221, 221, 221).getRGB();
    public static int MEDIA_OURPLE = new Color(188, 118, 255).getRGB();

    public static int redstone(float frac) {
        return net.createmod.catnip.theme.Color.mixColors((int)REDSTONE_OFF, (int)REDSTONE_ON, (float)frac);
    }

    public static Color LChOklab(float lightness, float chroma, float hue) {
        double a = (double)chroma * Math.cos(hue);
        double b = (double)chroma * Math.sin(hue);
        return SimColors.fromOklab(lightness, (float)a, (float)b);
    }

    public static Color fromOklab(float lightness, float a, float b) {
        float l_ = lightness + 0.39633778f * a + 0.21580376f * b;
        float m_ = lightness - 0.105561346f * a - 0.06385417f * b;
        float s_ = lightness - 0.08948418f * a - 1.2914855f * b;
        float l = l_ * l_ * l_;
        float m = m_ * m_ * m_;
        float s = s_ * s_ * s_;
        return new Color(Math.clamp(4.0767417f * l - 3.3077116f * m + 0.23096994f * s, 0.0f, 1.0f), Math.clamp(-1.268438f * l + 2.6097574f * m - 0.34131938f * s, 0.0f, 1.0f), Math.clamp(-0.0041960864f * l - 0.7034186f * m + 1.7076147f * s, 0.0f, 1.0f));
    }

    public static Vector3d toOklab(Color c) {
        float l = 0.41222146f * (float)c.getRed() + 0.53633255f * (float)c.getGreen() + 0.051445995f * (float)c.getBlue();
        float m = 0.2119035f * (float)c.getRed() + 0.6806995f * (float)c.getGreen() + 0.10739696f * (float)c.getBlue();
        float s = 0.08830246f * (float)c.getRed() + 0.28171885f * (float)c.getGreen() + 0.6299787f * (float)c.getBlue();
        double l_ = Math.cbrt(l / 255.0f);
        double m_ = Math.cbrt(m / 255.0f);
        double s_ = Math.cbrt(s / 255.0f);
        return new Vector3d(0.21045425534248352 * l_ + 0.7936177849769592 * m_ - 0.004072046838700771 * s_, 1.9779984951019287 * l_ - 2.4285922050476074 * m_ + 0.4505937099456787 * s_, 0.025904037058353424 * l_ + 0.7827717661857605 * m_ - 0.8086757659912109 * s_);
    }

    public static Vector3d LabToLCh(Vector3dc Lab) {
        return new Vector3d(Lab.x(), Math.sqrt(Lab.y() * Lab.y() + Lab.z() * Lab.z()), Math.atan2(Lab.y(), Lab.z()));
    }
}

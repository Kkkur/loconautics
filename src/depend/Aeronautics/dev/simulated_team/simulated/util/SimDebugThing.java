/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  org.joml.Vector3d
 */
package dev.simulated_team.simulated.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3d;

public class SimDebugThing {
    static int steps = 0;
    static int limit = 0;
    static boolean active = false;
    static HashMap<String, List<String>> things = new HashMap();
    static NumberFormat nf = NumberFormat.getInstance();
    static ServerLevel level;

    public static void start(int limit, ServerLevel level) {
        SimDebugThing.limit = limit;
        SimDebugThing.level = level;
        SimDebugThing.sendMessage("DebugThing started with " + limit + " steps");
        active = true;
        steps = 0;
        nf.setMaximumFractionDigits(7);
        NumberFormat numberFormat = nf;
        if (numberFormat instanceof DecimalFormat) {
            DecimalFormat decimalFormat = (DecimalFormat)numberFormat;
            decimalFormat.setNegativePrefix("-");
        }
    }

    public static void step() {
        if (active) {
            if (steps >= limit) {
                SimDebugThing.stop();
                return;
            }
            ++steps;
        }
    }

    public static void stop() {
        active = false;
        SimDebugThing.output();
        things.clear();
    }

    public static void abort() {
        SimDebugThing.sendMessage("DebugThing aborted");
        things.clear();
        active = false;
    }

    public static void push(String label, Vector3d v) {
        SimDebugThing.push(label, "(" + SimDebugThing.formatNumber(v.x) + "," + SimDebugThing.formatNumber(v.y) + "," + SimDebugThing.formatNumber(v.z) + ")");
    }

    public static void push(String label, double d) {
        SimDebugThing.push(label, SimDebugThing.formatNumber(d));
    }

    public static void push(String label, int i) {
        SimDebugThing.push(label, Integer.toString(i));
    }

    static void push(String label, String s) {
        if (active) {
            if (!things.containsKey(label)) {
                things.put(label, new ArrayList());
            } else {
                things.get(label).add(s);
            }
        }
    }

    static String formatNumber(double d) {
        return nf.format(d).replace(',', '.').replace('?', '-');
    }

    static void output() {
        if (things.isEmpty()) {
            SimDebugThing.sendMessage("DebugThing finished after " + steps + " steps. No result available");
        } else {
            System.out.println("DebugThing output:");
            for (Map.Entry<String, List<String>> entry : things.entrySet()) {
                List<String> thing = entry.getValue();
                StringBuilder s = new StringBuilder(entry.getKey() + "=[");
                for (int j = 0; j < thing.size(); ++j) {
                    s.append(thing.get(j));
                    if (j >= thing.size() - 1) continue;
                    s.append(",");
                }
                s.append("]");
                System.out.println(s);
            }
            SimDebugThing.sendMessage("DebugThing finished after " + steps + " steps. Results dumped into system log");
        }
    }

    static void sendMessage(String s) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            player.sendSystemMessage((Component)Component.literal((String)s));
        }
    }
}

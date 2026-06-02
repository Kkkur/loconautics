/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.sublevel.system;

import dev.ryanhcode.sable.sublevel.SubLevel;

private record SubLevelTrackingSystem.SubLevelUpdateTicket(SubLevel subLevels, UpdateTicketType type) {

    private static enum UpdateTicketType {
        STOP,
        MOVE;

    }
}

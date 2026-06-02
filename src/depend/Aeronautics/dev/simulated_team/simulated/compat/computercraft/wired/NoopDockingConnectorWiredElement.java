/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.simulated_team.simulated.compat.computercraft.wired;

import dev.simulated_team.simulated.compat.computercraft.wired.DockingConnectorWiredElement;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public enum NoopDockingConnectorWiredElement implements DockingConnectorWiredElement
{
    INSTANCE;


    @Override
    public void connect(DockingConnectorWiredElement other) {
    }

    @Override
    public void disconnect(DockingConnectorWiredElement other) {
    }

    @Override
    public void remove() {
    }
}

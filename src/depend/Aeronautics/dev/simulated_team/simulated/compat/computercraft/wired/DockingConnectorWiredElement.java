/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.simulated_team.simulated.compat.computercraft.wired;

import dev.simulated_team.simulated.compat.computercraft.wired.DockingConnectorWiredElementImpl;
import dev.simulated_team.simulated.compat.computercraft.wired.NoopDockingConnectorWiredElement;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import dev.simulated_team.simulated.service.SimPlatformService;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface DockingConnectorWiredElement {
    public static final boolean CC_LOADED = SimPlatformService.INSTANCE.isLoaded("computercraft");

    public void connect(DockingConnectorWiredElement var1);

    public void disconnect(DockingConnectorWiredElement var1);

    public void remove();

    public static DockingConnectorWiredElement create(DockingConnectorBlockEntity blockEntity) {
        return CC_LOADED ? new DockingConnectorWiredElementImpl(blockEntity) : NoopDockingConnectorWiredElement.INSTANCE;
    }
}

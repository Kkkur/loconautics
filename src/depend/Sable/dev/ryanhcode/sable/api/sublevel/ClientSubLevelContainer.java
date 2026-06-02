/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.api.sublevel;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.network.client.ClientSableInterpolationState;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

public class ClientSubLevelContainer
extends SubLevelContainer {
    private final ClientSableInterpolationState interpolation = new ClientSableInterpolationState();
    private final BitSet lightingSceneIds = new BitSet(this.subLevels.length);

    public ClientSubLevelContainer(Level level, int logSideLength, int logPlotSize, int originX, int originZ) {
        super(level, logSideLength, logPlotSize, originX, originZ);
    }

    @Override
    protected SubLevel createSubLevel(int globalPlotX, int globalPlotZ, Pose3d pose, UUID uuid) {
        ClientSubLevel subLevel = new ClientSubLevel((Level)this.getLevel(), globalPlotX, globalPlotZ, pose);
        subLevel.setUniqueId(uuid);
        return subLevel;
    }

    @Override
    public void tick() {
        this.interpolation.tick();
        super.tick();
    }

    @ApiStatus.Internal
    public void addDebugInfo(Consumer<String> consumer) {
        consumer.accept("Sub-Levels: " + this.getAllSubLevels().size());
        this.interpolation.addDebugInfo(consumer);
    }

    public List<ClientSubLevel> getAllSubLevels() {
        return super.getAllSubLevels();
    }

    public ClientLevel getLevel() {
        return (ClientLevel)super.getLevel();
    }

    public ClientSableInterpolationState getInterpolation() {
        return this.interpolation;
    }

    public int getLightingSceneId(ClientSubLevel subLevel) {
        BitSet bitSet = this.lightingSceneIds;
        synchronized (bitSet) {
            if (subLevel.getLightingSceneId() >= 0) {
                return subLevel.getLightingSceneId();
            }
            for (int i = 0; i < this.lightingSceneIds.size(); ++i) {
                if (this.lightingSceneIds.get(i)) continue;
                this.lightingSceneIds.set(i);
                subLevel.setLightingSceneId(i + 1);
                return subLevel.getLightingSceneId();
            }
            throw new IllegalStateException("Out of lighting scene ids, uh oh!");
        }
    }

    @ApiStatus.Internal
    public void freeLightingScene(int lightingSceneId) {
        this.lightingSceneIds.clear(lightingSceneId - 1);
    }
}

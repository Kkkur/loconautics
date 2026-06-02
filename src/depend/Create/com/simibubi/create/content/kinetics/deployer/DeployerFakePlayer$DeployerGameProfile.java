/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.neoforged.neoforge.common.UsernameCache
 */
package com.simibubi.create.content.kinetics.deployer;

import com.mojang.authlib.GameProfile;
import java.util.Objects;
import java.util.UUID;
import net.neoforged.neoforge.common.UsernameCache;

private static class DeployerFakePlayer.DeployerGameProfile
extends GameProfile {
    private UUID owner;

    public DeployerFakePlayer.DeployerGameProfile(UUID id, String name, UUID owner) {
        super(id, name);
        this.owner = owner;
    }

    public UUID getId() {
        return this.owner == null ? super.getId() : this.owner;
    }

    public String getName() {
        if (this.owner == null) {
            return super.getName();
        }
        String lastKnownUsername = UsernameCache.getLastKnownUsername((UUID)this.owner);
        return lastKnownUsername == null ? super.getName() : lastKnownUsername;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameProfile)) {
            return false;
        }
        GameProfile otherProfile = (GameProfile)o;
        return Objects.equals(this.getId(), otherProfile.getId()) && Objects.equals(this.getName(), otherProfile.getName());
    }

    public int hashCode() {
        UUID id = this.getId();
        String name = this.getName();
        int result = id == null ? 0 : id.hashCode();
        result = 31 * result + (name == null ? 0 : name.hashCode());
        return result;
    }
}

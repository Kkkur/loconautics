/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.link.LinkBehaviour
 *  com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.simulated_team.simulated.mixin.accessor;

import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={RedstoneLinkBlockEntity.class})
public interface RedstoneLinkBlockEntityAccessor {
    @Accessor(value="link")
    public LinkBehaviour getLink();
}

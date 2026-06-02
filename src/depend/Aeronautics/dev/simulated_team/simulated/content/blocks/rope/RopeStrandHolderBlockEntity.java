/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.rope;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopeStrand;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachment;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerRopeStrand;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RopeStrandHolderBlockEntity
extends BlockEntitySubLevelActor {
    public RopeStrandHolderBehavior getBehavior();

    public Vec3 getAttachmentPoint(BlockPos var1, BlockState var2);

    default public Vec3 getVisualAttachmentPoint(BlockPos pos, BlockState state) {
        return this.getAttachmentPoint(pos, state);
    }

    default public @Nullable Iterable<@NotNull SubLevel> sable$getConnectionDependencies() {
        RopeStrandHolderBehavior behavior = this.getBehavior();
        SmartBlockEntity be = behavior.blockEntity;
        Level level = be.getLevel();
        SubLevelContainer container = SubLevelContainer.getContainer((Level)level);
        if (!1.$assertionsDisabled && container == null) {
            throw new AssertionError();
        }
        ServerRopeStrand serverStrand = behavior.getAttachedStrand();
        if (serverStrand != null) {
            ObjectArrayList dependencies = new ObjectArrayList();
            Iterable<RopeAttachment> attachments = serverStrand.getAttachments();
            for (RopeAttachment attachment : attachments) {
                SubLevel subLevel;
                UUID id = attachment.subLevelID();
                if (id == null || (subLevel = container.getSubLevel(id)) == null) continue;
                dependencies.add((Object)subLevel);
            }
            return dependencies;
        }
        ClientRopeStrand clientStrand = behavior.getClientStrand();
        if (clientStrand != null) {
            ObjectArrayList dependencies = new ObjectArrayList();
            SubLevel subLevelA = Sable.HELPER.getContaining(level, (Position)clientStrand.startAttachment);
            SubLevel subLevelB = Sable.HELPER.getContaining(level, (Position)clientStrand.endAttachment);
            if (subLevelA != null) {
                dependencies.add((Object)subLevelA);
            }
            if (subLevelB != null) {
                dependencies.add((Object)subLevelB);
            }
            return dependencies;
        }
        return null;
    }

    static {
        if (1.$assertionsDisabled) {
            // empty if block
        }
    }
}

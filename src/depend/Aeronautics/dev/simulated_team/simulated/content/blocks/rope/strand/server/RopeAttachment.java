/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.BlockPos
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.rope.strand.server;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachmentPoint;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public record RopeAttachment(RopeAttachmentPoint point, @Nullable UUID subLevelID, BlockPos blockAttachment) {
    private static final Codec<RopeAttachmentPoint> ATTACHMENT_POINT_CODEC = Codec.STRING.xmap(RopeAttachmentPoint::valueOf, Enum::name);
    public static final Codec<RopeAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ATTACHMENT_POINT_CODEC.fieldOf("point").forGetter(RopeAttachment::point), (App)Codec.STRING.optionalFieldOf("subLevelID").xmap(opt -> opt.map(UUID::fromString), uuid -> uuid.map(UUID::toString)).forGetter(x -> Optional.ofNullable(x.subLevelID())), (App)BlockPos.CODEC.fieldOf("blockAttachment").forGetter(RopeAttachment::blockAttachment)).apply((Applicative)instance, RopeAttachment::new));

    private RopeAttachment(RopeAttachmentPoint point, Optional<UUID> subLevelID, BlockPos blockAttachment) {
        this(point, (UUID)subLevelID.orElse(null), blockAttachment);
    }
}

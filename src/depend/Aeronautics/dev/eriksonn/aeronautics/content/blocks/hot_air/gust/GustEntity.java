/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.util.SableBufferUtils
 *  dev.ryanhcode.sable.util.SableNBTUtils
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.entity.IEntityWithComplexSpawn
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.gust;

import dev.eriksonn.aeronautics.content.particle.AirPoofParticleData;
import dev.eriksonn.aeronautics.content.particle.GustParticleData;
import dev.eriksonn.aeronautics.index.AeroEntityTypes;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SableBufferUtils;
import dev.ryanhcode.sable.util.SableNBTUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class GustEntity
extends Entity
implements IEntityWithComplexSpawn {
    private final Quaterniond orientation = new Quaterniond();
    private boolean spawnedInitialBurst = false;

    public static void addGust(Level level, BlockPos pos, Direction direction) {
        Quaterniond orientation = new Quaterniond((Quaternionfc)direction.getRotation());
        GustEntity gust = new GustEntity((EntityType)AeroEntityTypes.GUST.get(), level, (Quaterniondc)orientation);
        gust.setPos(pos.getCenter());
        level.addFreshEntity((Entity)gust);
    }

    public GustEntity(EntityType<?> entityType, Level level) {
        this(entityType, level, JOMLConversion.QUAT_IDENTITY);
    }

    public GustEntity(EntityType<?> entityType, Level level, Quaterniondc orientation) {
        super(entityType, level);
        this.orientation.set(orientation);
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.spawnClientEffects();
        } else {
            SubLevel subLevel = Sable.HELPER.getContaining((Entity)this);
            if (subLevel instanceof ServerSubLevel) {
                ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
                Vector3d forceDir = this.orientation.transform(new Vector3d(0.0, 1.0, 0.0)).mul(-3.0);
                RigidBodyHandle.of((ServerSubLevel)serverSubLevel).applyImpulseAtPoint(this.position(), JOMLConversion.toMojang((Vector3dc)forceDir));
            }
            if (this.tickCount > 5) {
                this.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    private void spawnClientEffects() {
        Level level = this.level();
        if (!this.spawnedInitialBurst) {
            int i;
            Vec3 soundPos = this.position();
            level.playLocalSound(soundPos.x, soundPos.y, soundPos.z, AeroSoundEvents.GUST.event(), SoundSource.BLOCKS, 0.65f, 0.35f, false);
            int poofParticleCount = 30;
            for (i = 0; i < 3; ++i) {
                if ((double)level.random.nextFloat() < 0.1) continue;
                Quaternionf particleOrientation = new Quaternionf((Quaterniondc)this.orientation);
                particleOrientation.rotateY((float)(2.0943951023931953 * (double)i));
                particleOrientation.rotateZ((float)Math.toRadians(-10.0));
                float randomRot = (float)Math.toRadians(12.0);
                particleOrientation.rotateX(this.random.nextFloat() * randomRot - randomRot / 2.0f);
                particleOrientation.rotateZ(this.random.nextFloat() * randomRot - randomRot / 2.0f);
                particleOrientation.rotateY(this.random.nextFloat() * randomRot - randomRot / 2.0f);
                Vector3d particlePos = JOMLConversion.toJOML((Position)this.position());
                particlePos.add((Vector3dc)particleOrientation.transform(new Vector3d(0.5, 0.5, 0.0)));
                level.addParticle((ParticleOptions)new GustParticleData(particleOrientation), particlePos.x, particlePos.y, particlePos.z, 0.0, 0.0, 0.0);
            }
            for (i = 0; i < 30; ++i) {
                Vector3d outDir = this.orientation.transform(new Vector3d(0.0, 1.0, 0.0));
                float velocity = 0.07f + this.random.nextFloat() * 0.1f;
                double vx = outDir.x * (double)velocity + this.random.nextGaussian() * 0.01;
                double vy = outDir.y * (double)velocity + this.random.nextGaussian() * 0.01;
                double vz = outDir.z * (double)velocity + this.random.nextGaussian() * 0.01;
                float positionalRandomness = 0.7f;
                Vec3 particlePos = this.position().subtract(outDir.x, outDir.y, outDir.z).add((double)0.7f * ((double)this.random.nextFloat() - 0.5), (double)0.7f * ((double)this.random.nextFloat() - 0.5), (double)0.7f * ((double)this.random.nextFloat() - 0.5));
                level.addParticle((ParticleOptions)new AirPoofParticleData(), particlePos.x, particlePos.y, particlePos.z, vx, vy, vz);
            }
            this.spawnedInitialBurst = true;
        }
    }

    @NotNull
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @NotNull
    protected AABB makeBoundingBox() {
        AABB boundingBox = this.getDimensions(this.getPose()).makeBoundingBox(this.position());
        return boundingBox.move(0.0, -boundingBox.getYsize() / 2.0, 0.0);
    }

    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
    }

    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.put("GustOrientation", (Tag)SableNBTUtils.writeQuaternion((Quaterniondc)this.orientation));
    }

    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        this.orientation.set((Quaterniondc)SableNBTUtils.readQuaternion((CompoundTag)compoundTag));
    }

    public void writeSpawnData(@NotNull RegistryFriendlyByteBuf buffer) {
        SableBufferUtils.write((ByteBuf)buffer, (Quaterniondc)this.orientation);
    }

    public void readSpawnData(@NotNull RegistryFriendlyByteBuf buffer) {
        SableBufferUtils.read((ByteBuf)buffer, (Quaterniond)this.orientation);
    }
}

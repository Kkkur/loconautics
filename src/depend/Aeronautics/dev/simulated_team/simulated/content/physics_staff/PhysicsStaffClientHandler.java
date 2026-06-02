/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.CreateClient
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  foundry.veil.api.network.VeilPacketManager
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.outliner.LineOutline
 *  net.createmod.catnip.render.DefaultSuperRenderTypeBuffer
 *  net.createmod.catnip.render.SuperRenderTypeBuffer
 *  net.minecraft.client.Camera
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.AxisAngle4d
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.physics_staff;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.CreateClient;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.SimulatedClient;
import dev.simulated_team.simulated.config.client.items.SimItemConfigs;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffAction;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItem;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItemRenderer;
import dev.simulated_team.simulated.index.SimKeys;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.network.packets.physics_staff.PhysicsStaffActionPacket;
import dev.simulated_team.simulated.network.packets.physics_staff.PhysicsStaffDragPacket;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimDistUtil;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import foundry.veil.api.network.VeilPacketManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.outliner.LineOutline;
import net.createmod.catnip.render.DefaultSuperRenderTypeBuffer;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4d;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PhysicsStaffClientHandler {
    protected final Object2ObjectMap<UUID, PhysicsBeam> beams = new Object2ObjectOpenHashMap();
    private final Map<ResourceKey<Level>, List<UUID>> locks = new Object2ObjectOpenHashMap();
    private final Map<ResourceKey<Level>, Object2ObjectOpenHashMap<UUID, Vector3dc>> serverDragSessions = new Object2ObjectOpenHashMap();
    public float tilt = 0.0f;
    public float previousTilt = 0.0f;
    public float extension = 0.0f;
    public float previousExtension = 0.0f;
    public float targetExtension = 0.0f;
    public float cubeScale = 0.0f;
    public float previousCubeScale = 0.0f;
    public Quaternionf lastCubeOrientation = new Quaternionf();
    private State state = State.PASSIVE;
    private boolean holdingStaff;
    @Nullable
    private ClientDragSession dragSession;
    @Nullable
    private LoopingSoundInstance sound;

    public static Vec3 getStaffFocusPos(Player player, boolean mainHand, float pt) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();
        if (player.isLocalPlayer() && !camera.isDetached()) {
            Vec3 savedPos = PhysicsStaffItemRenderer.getFirstPersonFocusPos(pt).add(player.getPosition(pt)).add(0.0, (double)Mth.lerp((float)pt, (float)camera.eyeHeightOld, (float)camera.eyeHeight), 0.0);
            return savedPos;
        }
        Vec3 viewDirection = player.calculateViewVector(0.0f, player.getPreciseBodyRotation(pt));
        Vec3 handDirection = player.calculateViewVector(0.0f, player.getPreciseBodyRotation(pt) + 90.0f);
        return player.getPosition(pt).add(0.0, 1.28, 0.0).add(viewDirection.scale(1.275)).add(handDirection.scale(0.325 * (double)(mainHand ? 1 : -1)));
    }

    private static void spawnParticles(InteractionHand hand, SubLevel subLevel, Vec3 hitLocation, Level level) {
        CreateClient.ZAPPER_RENDER_HANDLER.shoot(hand, subLevel.logicalPose().transformPosition(hitLocation));
        RandomSource random = level.getRandom();
        Supplier<Double> randomSpeed = () -> (random.nextDouble() - 0.5) * (double)0.2f;
        for (int i = 0; i < 10; ++i) {
            level.addParticle((ParticleOptions)ParticleTypes.END_ROD, hitLocation.x, hitLocation.y, hitLocation.z, randomSpeed.get().doubleValue(), randomSpeed.get().doubleValue(), randomSpeed.get().doubleValue());
        }
    }

    public void onItemPunched() {
        this.onItemUsed(PhysicsStaffAction.LOCK);
    }

    public void onItemUsed(PhysicsStaffAction action) {
        BlockHitResult blockHitResult;
        if (!this.holdingStaff) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (action == PhysicsStaffAction.START_DRAG && this.state == State.DRAGGING) {
            if (player != null) {
                player.playSound(SimSoundEvents.STAFF_EXTINGUISH.event());
            }
            this.stopDragging();
            return;
        }
        Level level = player.level();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (this.dragSession != null && action == PhysicsStaffAction.LOCK) {
            Vec3 hitLocation = JOMLConversion.toMojang((Vector3dc)this.dragSession.dragLocalAnchor);
            this.lockSubLevel(this.dragSession.dragSubLevel, hitLocation, player, hand);
            PhysicsStaffClientHandler.spawnParticles(hand, this.dragSession.dragSubLevel, hitLocation, level);
            if (this.state == State.DRAGGING) {
                this.stopDragging();
            }
            this.state = State.LOCKING;
            return;
        }
        HitResult hit = player.pick((double)PhysicsStaffItem.RANGE, 1.0f, false);
        if (!(hit instanceof BlockHitResult) || (blockHitResult = (BlockHitResult)hit).getType() == HitResult.Type.MISS) {
            this.state = State.OPENING;
            return;
        }
        Vec3 hitLocation = hit.getLocation();
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Position)hitLocation);
        if (subLevel == null) {
            this.state = State.OPENING;
            return;
        }
        if (action == PhysicsStaffAction.START_DRAG) {
            if (this.state == State.DRAGGING) {
                this.stopDragging();
            }
            this.startDraggingSubLevel((SubLevel)subLevel, blockHitResult.getBlockPos(), player, hand);
            this.state = State.DRAGGING;
        }
        if (action == PhysicsStaffAction.LOCK) {
            this.lockSubLevel((SubLevel)subLevel, hitLocation, player, hand);
            if (this.state == State.DRAGGING) {
                this.stopDragging();
            }
            Vec3 focusPos = PhysicsStaffClientHandler.getStaffFocusPos((Player)player, hand == InteractionHand.MAIN_HAND, 1.0f);
            this.updateBeam(level, player.getUUID(), focusPos, hitLocation);
            this.state = State.LOCKING;
        }
        PhysicsStaffClientHandler.spawnParticles(hand, (SubLevel)subLevel, hitLocation, level);
    }

    private void startDraggingSubLevel(SubLevel subLevel, BlockPos blockPos, LocalPlayer player, InteractionHand hand) {
        Vector3d localAnchor = JOMLConversion.atCenterOf((Vec3i)blockPos);
        this.dragSession = new ClientDragSession(subLevel, (Vector3dc)localAnchor, new Quaterniond((Quaterniondc)subLevel.logicalPose().orientation()), this.clampDistance(player.getEyePosition().distanceTo(subLevel.logicalPose().transformPosition(JOMLConversion.toMojang((Vector3dc)localAnchor)))));
        SoundEvent soundEvent = this.isLocked(subLevel) ? SimSoundEvents.STAFF_UNLOCK.event() : SimSoundEvents.STAFF_IGNITE.event();
        player.playSound(soundEvent);
    }

    private void lockSubLevel(SubLevel subLevel, Vec3 hitLocation, LocalPlayer player, InteractionHand hand) {
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new PhysicsStaffActionPacket(PhysicsStaffAction.LOCK, subLevel.getUniqueId(), (Vector3dc)JOMLConversion.toJOML((Position)hitLocation))});
        SoundEvent soundEvent = this.isLocked(subLevel) ? SimSoundEvents.STAFF_UNLOCK.event() : SimSoundEvents.STAFF_LOCK.event();
        player.playSound(soundEvent);
    }

    private boolean isLocked(SubLevel subLevel) {
        List<UUID> locks = this.locks.get(subLevel.getLevel().dimension());
        return locks.contains(subLevel.getUniqueId());
    }

    public void tick() {
        Player player = SimDistUtil.getClientPlayer();
        if (player == null) {
            this.reset();
            return;
        }
        this.holdingStaff = PhysicsStaffItem.isHolding(player);
        if (!this.holdingStaff) {
            this.state = State.PASSIVE;
            this.dragSession = null;
        }
        switch (this.state.ordinal()) {
            case 1: 
            case 3: {
                if (!((double)this.extension > 0.97)) break;
                this.state = State.PASSIVE;
                break;
            }
            case 2: {
                assert (this.dragSession != null);
                SubLevel draggingSubLevel = this.dragSession.dragSubLevel;
                if (draggingSubLevel != null && draggingSubLevel.isRemoved()) {
                    this.stopDragging();
                    break;
                }
                ObjectIterator focusPos = PhysicsStaffClientHandler.getStaffFocusPos(player, player.getMainHandItem().getItem() instanceof PhysicsStaffItem, 1.0f);
                this.updateBeam(player.level(), player.getUUID(), (Vec3)focusPos, JOMLConversion.toMojang((Vector3dc)this.dragSession.dragLocalAnchor));
                this.sendDraggingData(player);
                if (this.sound == null) {
                    this.sound = new LoopingSoundInstance((LocalPlayer)player, SimSoundEvents.STAFF_IDLE.event(), player.level().getRandom());
                }
                if (!Minecraft.getInstance().getSoundManager().isActive((SoundInstance)this.sound)) {
                    Minecraft.getInstance().getSoundManager().play((SoundInstance)this.sound);
                }
                this.sound.setVolume(1.0f);
                break;
            }
            case 0: {
                if (this.sound == null) break;
                this.sound.setVolume(0.0f);
            }
        }
        Object2ObjectOpenHashMap<UUID, Vector3dc> sessions = this.serverDragSessions.get(player.level().dimension());
        if (sessions != null) {
            for (Map.Entry draggingEntry : sessions.entrySet()) {
                Player otherPlayer;
                if (((UUID)draggingEntry.getKey()).equals(player.getUUID())) continue;
                UUID playerId = (UUID)draggingEntry.getKey();
                Vector3dc localAnchor = (Vector3dc)draggingEntry.getValue();
                SubLevel draggingSubLevel = Sable.HELPER.getContaining(player.level(), localAnchor);
                if (draggingSubLevel == null || (otherPlayer = player.level().getPlayerByUUID(playerId)) == null) continue;
                Vec3 focusPos = PhysicsStaffClientHandler.getStaffFocusPos(otherPlayer, otherPlayer.getMainHandItem().getItem() instanceof PhysicsStaffItem, 1.0f);
                this.updateBeam(player.level(), playerId, focusPos, JOMLConversion.toMojang((Vector3dc)localAnchor));
            }
        }
        boolean isUsing = this.state != State.PASSIVE;
        this.targetExtension = isUsing ? 1.0f : 0.0f;
        float targetCubeScale = isUsing && this.state != State.OPENING ? 1.0f : 0.0f;
        this.previousExtension = this.extension;
        this.extension = Mth.lerp((float)0.65f, (float)this.extension, (float)this.targetExtension);
        this.previousCubeScale = this.cubeScale;
        this.cubeScale = Mth.lerp((float)0.65f, (float)this.cubeScale, (float)targetCubeScale);
        this.previousTilt = this.tilt;
        this.tilt = Mth.lerp((float)0.65f, (float)this.tilt, (float)(this.state == State.DRAGGING ? 1.0f : 0.0f));
        this.beams.values().removeIf(beam -> beam.intensity < 0.4f);
        if (this.beams.isEmpty()) {
            return;
        }
        this.beams.forEach((uuid, beam) -> {
            beam.previousStart = beam.start;
            beam.previousEnd = beam.end;
            beam.start = beam.serverStart;
            beam.end = beam.serverEnd;
            beam.intensity *= 0.6f;
            beam.update();
        });
    }

    private void reset() {
        this.tilt = 0.0f;
        this.previousTilt = 0.0f;
        this.extension = 0.0f;
        this.previousExtension = 0.0f;
        this.targetExtension = 0.0f;
        this.lastCubeOrientation.identity();
        this.state = State.PASSIVE;
        this.holdingStaff = false;
        this.dragSession = null;
        this.sound = null;
    }

    private void sendDraggingData(Player player) {
        ClientDragSession session = this.dragSession;
        assert (session != null);
        Vec3 goalPosition = player.getLookAngle().scale(session.distance);
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new PhysicsStaffDragPacket(session.dragSubLevel.getUniqueId(), (Vector3dc)JOMLConversion.toJOML((Position)goalPosition), session.dragLocalAnchor, (Quaterniondc)session.dragOrientation)});
    }

    private void stopDragging() {
        ClientDragSession session = this.dragSession;
        assert (session != null);
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new PhysicsStaffActionPacket(PhysicsStaffAction.STOP_DRAG, session.dragSubLevel.getUniqueId(), session.dragLocalAnchor)});
        this.dragSession = null;
        this.state = State.PASSIVE;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            this.beams.remove((Object)player.getUUID());
        }
    }

    public void onRender(PoseStack ms) {
        DefaultSuperRenderTypeBuffer buffer = DefaultSuperRenderTypeBuffer.getInstance();
        float pt = AnimationTickHolder.getPartialTicks();
        Minecraft client = Minecraft.getInstance();
        Vec3 camera = client.gameRenderer.getMainCamera().getPosition();
        this.beams.forEach((arg_0, arg_1) -> PhysicsStaffClientHandler.lambda$onRender$3(client, pt, ms, (SuperRenderTypeBuffer)buffer, camera, arg_0, arg_1));
        buffer.draw();
        RenderSystem.enableCull();
    }

    public void updateBeam(Level level, UUID uuid, Vec3 start, Vec3 end) {
        PhysicsBeam beam = (PhysicsBeam)this.beams.get((Object)uuid);
        if (beam == null) {
            this.beams.put((Object)uuid, (Object)new PhysicsBeam(start, end, Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)start, (Position)end))));
        } else {
            beam.serverStart = start;
            beam.serverEnd = end;
            beam.intensity = 1.0f;
        }
    }

    public void setLocks(ResourceKey<Level> dimension, List<UUID> locks) {
        this.locks.put(dimension, locks);
    }

    protected List<UUID> getLocks(Level level) {
        return this.locks.get(level.dimension());
    }

    public ClientDragSession getDragSession() {
        return this.dragSession;
    }

    public void setServerDragSessions(ResourceKey<Level> dimension, List<Pair<UUID, Vector3d>> newSessions) {
        Object2ObjectOpenHashMap dragSessions = this.serverDragSessions.computeIfAbsent(dimension, x -> new Object2ObjectOpenHashMap());
        dragSessions.clear();
        for (Pair<UUID, Vector3d> pair : newSessions) {
            dragSessions.put((Object)((UUID)pair.getFirst()), (Object)((Vector3dc)pair.getSecond()));
        }
    }

    private double clampDistance(double distance) {
        return Math.clamp(distance, 2.0, (double)PhysicsStaffItem.RANGE);
    }

    private boolean isRotating() {
        return this.holdingStaff && this.dragSession != null && SimKeys.ROTATE_MODE.isPressed();
    }

    private static /* synthetic */ void lambda$onRender$3(Minecraft client, float pt, PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, UUID uuid, PhysicsBeam beam) {
        Player player = client.level.getPlayerByUUID(uuid);
        if (player != null) {
            boolean mainHand = true;
            if (!(player.getMainHandItem().getItem() instanceof PhysicsStaffItem) && player.getOffhandItem().getItem() instanceof PhysicsStaffItem) {
                mainHand = false;
            }
            Vec3 focusPos = PhysicsStaffClientHandler.getStaffFocusPos(player, mainHand, pt);
            Vec3 interpolatedBeamEnd = beam.previousEnd.lerp(beam.end, (double)pt);
            ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Position)interpolatedBeamEnd);
            if (subLevel == null) {
                return;
            }
            interpolatedBeamEnd = subLevel.renderPose(pt).transformPosition(interpolatedBeamEnd);
            beam.render(focusPos, interpolatedBeamEnd, ms, buffer, camera, pt);
        }
    }

    private static enum State {
        PASSIVE,
        LOCKING,
        DRAGGING,
        OPENING;

    }

    public static final class ClientDragSession {
        private final SubLevel dragSubLevel;
        private final Vector3dc dragLocalAnchor;
        private final Quaterniond dragOrientation;
        private double distance;

        public ClientDragSession(SubLevel dragSubLevel, Vector3dc dragLocalAnchor, Quaterniond dragOrientation, double distance) {
            this.dragSubLevel = dragSubLevel;
            this.dragLocalAnchor = dragLocalAnchor;
            this.dragOrientation = dragOrientation;
            this.distance = distance;
        }

        public SubLevel dragSubLevel() {
            return this.dragSubLevel;
        }

        public Vector3dc dragLocalAnchor() {
            return this.dragLocalAnchor;
        }

        public Quaterniond dragOrientation() {
            return this.dragOrientation;
        }

        public double distance() {
            return this.distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public String toString() {
            return "ClientDragSession[dragSubLevel=" + String.valueOf(this.dragSubLevel) + ", dragLocalAnchor=" + String.valueOf(this.dragLocalAnchor) + ", dragOrientation=" + String.valueOf(this.dragOrientation) + ", distance=" + this.distance + "]";
        }
    }

    public static class LoopingSoundInstance
    extends AbstractTickableSoundInstance {
        private final LocalPlayer player;

        protected LoopingSoundInstance(LocalPlayer player, SoundEvent event, RandomSource random) {
            super(event, SoundSource.PLAYERS, random);
            this.player = player;
        }

        public void setVolume(float volume) {
            this.volume = volume;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public double getX() {
            return this.player.position().x();
        }

        public double getY() {
            return this.player.position().y();
        }

        public double getZ() {
            return this.player.position().z();
        }

        public void tick() {
        }
    }

    public static class PhysicsBeam {
        private static final float TARGET_SPACING = 1.5f;
        private static final int MIN_POINTS = 8;
        private final LineOutline line;
        private final double targetNodeRadius = 0.2;
        private final List<BeamNode> nodes = new ObjectArrayList();
        protected float extension;
        protected float previousExtension;
        protected float cubeScale;
        protected float previousCubeScale;
        private float intensity;
        private Vec3 start;
        private Vec3 end;
        private Vec3 previousStart;
        private Vec3 previousEnd;
        private Vec3 serverStart;
        private Vec3 serverEnd;
        private double length;
        private double currentNodeRadius = 0.0;

        public PhysicsBeam(Vec3 start, Vec3 end, double length) {
            this.start = start;
            this.previousStart = start;
            this.serverStart = start;
            this.end = end;
            this.previousEnd = end;
            this.serverEnd = end;
            this.intensity = 1.0f;
            this.line = new LineOutline();
            this.line.getParams().colored(0xFFFFFF).disableLineNormals().lineWidth(0.0375f);
            this.length = length;
            this.extension = 0.0f;
            this.update();
        }

        private void update() {
            double scaledLength = this.length / 1.5;
            double targetCount = 64.0 / (scaledLength + 8.0) + scaledLength;
            if (targetCount > 4096.0) {
                return;
            }
            this.currentNodeRadius = this.targetNodeRadius * Math.sqrt(scaledLength / targetCount);
            while ((double)this.nodes.size() < targetCount - 0.7) {
                this.nodes.add(new BeamNode());
            }
            while ((double)this.nodes.size() > targetCount + 0.7) {
                this.nodes.remove(0);
            }
            for (int i = 1; i < this.nodes.size() - 1; ++i) {
                this.nodes.get(i).update();
            }
            this.previousExtension = this.extension;
            this.previousCubeScale = this.cubeScale;
            this.extension = (double)this.intensity < 0.4 ? Mth.lerp((float)0.5f, (float)this.extension, (float)0.0f) : Mth.lerp((float)0.5f, (float)this.extension, (float)1.0f);
            this.cubeScale = this.extension;
        }

        private void render(Vec3 start, Vec3 end, PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, float pt) {
            Vec3 relative = end.subtract(start);
            this.length = relative.length();
            Vec3 lastPos = start;
            for (int i = 1; i < this.nodes.size(); ++i) {
                Vec3 offset = this.nodes.get((int)i).previousPosition.lerp(this.nodes.get((int)i).position, (double)pt);
                Vec3 currentPos = start.add(relative.scale((double)((float)i / (float)this.nodes.size())).add(offset.scale(this.currentNodeRadius)));
                this.line.set(lastPos, currentPos).render(ms, buffer, camera, pt);
                lastPos = currentPos;
            }
        }

        private static class BeamNode {
            Vec3 position = new Vec3(0.0, 0.0, 0.0);
            Vec3 previousPosition = new Vec3(0.0, 0.0, 0.0);

            private BeamNode() {
            }

            void update() {
                RandomSource random = Minecraft.getInstance().level.random;
                this.previousPosition = this.position;
                this.position = this.position.offsetRandom(random, 3.0f).scale(0.5);
            }
        }
    }

    public static class PhysicsStaffMouseHandler
    implements InteractCallback {
        @Override
        public InteractCallback.Result onAttack(int modifiers, int action, KeyMapping leftKey) {
            if (SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.holdingStaff && action == 1) {
                SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.onItemPunched();
                return new InteractCallback.Result(true);
            }
            return InteractCallback.super.onAttack(modifiers, action, leftKey);
        }

        @Override
        public InteractCallback.Result onUse(int modifiers, int action, KeyMapping rightKey) {
            if (SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.holdingStaff && action == 1) {
                SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.onItemUsed(PhysicsStaffAction.START_DRAG);
                return new InteractCallback.Result(true);
            }
            return InteractCallback.super.onUse(modifiers, action, rightKey);
        }

        @Override
        public InteractCallback.Result onMouseMove(double yaw, double pitch) {
            Minecraft mc = Minecraft.getInstance();
            PhysicsStaffClientHandler handler = SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER;
            if (handler.isRotating()) {
                assert (handler.dragSession != null);
                assert (mc.player != null);
                Vec3 axis = mc.player.calculateViewVector(0.0f, mc.player.getYRot() - 90.0f);
                Quaterniond orientation = handler.dragSession.dragOrientation();
                SimItemConfigs config = SimConfigService.INSTANCE.client().itemConfig;
                double rotationSensitivity = (Double)config.physicsStaffRotateSensitivity.get();
                double yawChange = Math.toRadians(yaw) * rotationSensitivity;
                orientation.rotateLocalY(yawChange);
                orientation.premul((Quaterniondc)new Quaterniond(new AxisAngle4d(Math.toRadians(-pitch) * rotationSensitivity, axis.x, axis.y, axis.z)));
                return new InteractCallback.Result(true);
            }
            return InteractCallback.super.onMouseMove(yaw, pitch);
        }

        @Override
        public InteractCallback.Result onScroll(double deltaX, double deltaY) {
            PhysicsStaffClientHandler handler = SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER;
            ClientDragSession dragSession = handler.dragSession;
            SimItemConfigs config = SimConfigService.INSTANCE.client().itemConfig;
            double scrollSensitivity = (Double)config.physicsStaffScrollSensitivity.get();
            if (handler.holdingStaff && dragSession != null) {
                double currentDistance = dragSession.distance;
                boolean sprint = Minecraft.getInstance().options.keySprint.isDown();
                double sensMultiplier = Mth.clamp((double)Math.pow(currentDistance / 10.0, 0.5), (double)1.0, (double)5.0) * (double)(sprint ? 4 : 1);
                dragSession.setDistance(handler.clampDistance(currentDistance + deltaY * scrollSensitivity * sensMultiplier));
                return new InteractCallback.Result(true);
            }
            return InteractCallback.super.onScroll(deltaX, deltaY);
        }
    }
}

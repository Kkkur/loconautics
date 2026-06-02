/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jpountz.lz4.LZ4FrameInputStream
 *  net.minecraft.Util
 *  net.minecraft.Util$OS
 *  net.minecraft.server.level.ServerLevel
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.joml.Matrix3dc
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.physics.impl.rapier;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.PhysicsPipelineBody;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.mixinterface.physics.ServerLevelSceneExtension;
import dev.ryanhcode.sable.physics.impl.rapier.collider.RapierVoxelColliderData;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.jpountz.lz4.LZ4FrameInputStream;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3dc;
import org.joml.Vector3dc;

@ApiStatus.Internal
public class Rapier3D {
    private static final String NATIVE_DIR = ".sable/natives";
    private static final String LIB_NAME = "sable_rapier";
    public static boolean ENABLED = false;
    private static int countingSceneID = 0;
    private static int countingObjectID = 0;

    private static String getNativeName() {
        String arch = System.getProperty("os.arch").equals("arm") || System.getProperty("os.arch").startsWith("aarch64") ? "aarch64" : "x86_64";
        Util.OS os = Util.getPlatform();
        if (os == Util.OS.WINDOWS) {
            return "sable_rapier_" + arch + "_windows.dll";
        }
        if (os == Util.OS.OSX) {
            return "sable_rapier_" + arch + "_macos.dylib";
        }
        if (os != Util.OS.LINUX) {
            Sable.LOGGER.error("Unknown platform '{}' detected, sable will attempt to use linux natives, this may or may not work.", (Object)System.getProperty("os.name"));
        }
        return "sable_rapier_" + arch + "_linux.so";
    }

    private static void loadLibrary() {
        String nativeName = Rapier3D.getNativeName();
        try (InputStream is = Rapier3D.class.getResourceAsStream("/natives/sable_rapier/sable_rapier_binaries.zip.l4z");){
            if (is == null) {
                throw new FileNotFoundException("sable_rapier_binaries.zip.l4z");
            }
            Path dir = Paths.get(NATIVE_DIR, new String[0]);
            if (!Files.exists(dir, new LinkOption[0])) {
                Files.createDirectories(dir, new FileAttribute[0]);
            }
            try (LZ4FrameInputStream is2 = new LZ4FrameInputStream(is);
                 ZipInputStream ti = new ZipInputStream((InputStream)is2);){
                ZipEntry entry;
                while ((entry = ti.getNextEntry()) != null) {
                    if (!entry.getName().equals(nativeName)) continue;
                    Path tempFile = dir.resolve(nativeName);
                    Files.copy(ti, tempFile, StandardCopyOption.REPLACE_EXISTING);
                    System.load(tempFile.toAbsolutePath().toString());
                    ENABLED = true;
                    return;
                }
                throw new FileNotFoundException(nativeName);
            }
        }
        catch (Throwable t) {
            ENABLED = false;
            Sable.LOGGER.error("Sable has failed to load the natives needed for its Rapier pipeline. Native library name {}. Please report with system details and logs to {}", new Object[]{nativeName, "https://github.com/ryanhcode/sable/issues", t});
            return;
        }
    }

    @ApiStatus.Internal
    public static int getID(PhysicsPipelineBody body) {
        return body.getRuntimeId();
    }

    @ApiStatus.Internal
    public static synchronized int nextBodyID() {
        return countingObjectID++;
    }

    @ApiStatus.Internal
    public static synchronized int getID(ServerLevel level) {
        if (!(level instanceof ServerLevelSceneExtension)) {
            throw new IllegalArgumentException("ServerLevel must implement ServerLevelSceneExtension to be used with Rapier");
        }
        ServerLevelSceneExtension extension = (ServerLevelSceneExtension)level;
        if (extension.sable$getSceneID() == -1) {
            extension.sable$setSceneID(countingSceneID++);
            Sable.LOGGER.info("Assigned physics scene ID {} to {}", (Object)extension.sable$getSceneID(), (Object)level.dimension().location());
        }
        return extension.sable$getSceneID();
    }

    @ApiStatus.Internal
    public static native void initialize(int var0, double var1, double var3, double var5, double var7);

    @ApiStatus.Internal
    public static native void tick(int var0, double var1);

    @ApiStatus.Internal
    public static native void step(int var0, double var1);

    @ApiStatus.Internal
    public static native void createSubLevel(int var0, int var1, double[] var2);

    @ApiStatus.Internal
    public static native void removeSubLevel(int var0, int var1);

    @ApiStatus.Internal
    public static native void createBox(int var0, int var1, double var2, double var4, double var6, double var8, double[] var10);

    @ApiStatus.Internal
    public static native void removeBox(int var0, int var1);

    @ApiStatus.Internal
    public static native void getPose(int var0, int var1, double[] var2);

    @ApiStatus.Internal
    public static native void setCenterOfMass(int var0, int var1, double var2, double var4, double var6);

    @ApiStatus.Internal
    public static native void setLocalBounds(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7);

    @ApiStatus.Internal
    public static native void addChunk(int var0, int var1, int var2, int var3, int[] var4, boolean var5, int var6);

    @ApiStatus.Internal
    public static native void removeChunk(int var0, int var1, int var2, int var3, boolean var4);

    @ApiStatus.Internal
    public static native void changeBlock(int var0, int var1, int var2, int var3, int var4);

    @ApiStatus.Internal
    protected static native int newVoxelCollider(double var0, double var2, double var4, boolean var6, BlockSubLevelCollisionCallback var7);

    @ApiStatus.Internal
    public static native void addVoxelColliderBox(int var0, double[] var1);

    @ApiStatus.Internal
    public static native void clearVoxelColliderBoxes(int var0);

    @ApiStatus.Internal
    protected static native void setMassProperties(int var0, int var1, double var2, double[] var4, double[] var5);

    @ApiStatus.Internal
    public static RapierVoxelColliderData createVoxelColliderEntry(double frictionMultiplier, double volume, double restitution, boolean isFluid, BlockSubLevelCollisionCallback contactEvents) {
        return new RapierVoxelColliderData(Rapier3D.newVoxelCollider(frictionMultiplier, volume, restitution, isFluid, contactEvents));
    }

    @ApiStatus.Internal
    public static native void teleportObject(int var0, int var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14);

    @ApiStatus.Internal
    public static native void wakeUpObject(int var0, int var1);

    @ApiStatus.Internal
    public static native long addRotaryConstraint(int var0, int var1, int var2, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, double var21, double var23, double var25);

    @ApiStatus.Internal
    public static native long addFixedConstraint(int var0, int var1, int var2, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, double var21);

    @ApiStatus.Internal
    public static native long addFreeConstraint(int var0, int var1, int var2, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, double var21);

    @ApiStatus.Internal
    public static native long addGenericConstraint(int var0, int var1, int var2, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, double var21, double var23, double var25, double var27, double var29, int var31);

    @ApiStatus.Internal
    public static native void setConstraintFrame(int var0, long var1, int var3, double var4, double var6, double var8, double var10, double var12, double var14, double var16);

    @ApiStatus.Internal
    public static native void setConstraintContactsEnabled(int var0, long var1, boolean var3);

    @ApiStatus.Internal
    public static native void getConstraintImpulses(int var0, long var1, double[] var3);

    @ApiStatus.Internal
    public static native boolean isConstraintValid(int var0, long var1);

    @ApiStatus.Internal
    public static native void removeConstraint(int var0, long var1);

    @ApiStatus.Internal
    public static native void setConstraintMotor(int var0, long var1, int var3, double var4, double var6, double var8, boolean var10, double var11);

    @ApiStatus.Internal
    public static native void addLinearAngularVelocities(int var0, int var1, double var2, double var4, double var6, double var8, double var10, double var12, boolean var14);

    @ApiStatus.Internal
    public static native double[] clearCollisions(int var0);

    @ApiStatus.Internal
    public static native void applyForce(int var0, int var1, double var2, double var4, double var6, double var8, double var10, double var12, boolean var14);

    @ApiStatus.Internal
    public static native void applyForceAndTorque(int var0, int var1, double var2, double var4, double var6, double var8, double var10, double var12, boolean var14);

    @ApiStatus.Internal
    public static native void getLinearVelocity(int var0, int var1, double[] var2);

    @ApiStatus.Internal
    public static native void getAngularVelocity(int var0, int var1, double[] var2);

    @ApiStatus.Internal
    public static native void createKinematicContraption(int var0, int var1, int var2, double[] var3);

    @ApiStatus.Internal
    public static native void removeKinematicContraption(int var0, int var1);

    @ApiStatus.Internal
    public static native void setKinematicContraptionTransform(int var0, int var1, double[] var2, double[] var3, double[] var4);

    @ApiStatus.Internal
    public static native void addKinematicContraptionChunkSection(int var0, int var1, int var2, int var3, int var4, int[] var5);

    @ApiStatus.Internal
    public static native long createRope(int var0, double var1, double var3, double[] var5, int var6);

    @ApiStatus.Internal
    public static native long removeRope(int var0, long var1);

    @ApiStatus.Internal
    public static native void setRopeAttachment(int var0, long var1, int var3, double var4, double var6, double var8, boolean var10);

    @ApiStatus.Internal
    public static native void addRopePointAtStart(int var0, long var1, double var3, double var5, double var7);

    @ApiStatus.Internal
    public static native void removeRopePointAtStart(int var0, long var1);

    @ApiStatus.Internal
    public static native void wakeUpRope(int var0, long var1);

    @ApiStatus.Internal
    public static native void setRopeFirstSegmentLength(int var0, long var1, double var3);

    @ApiStatus.Internal
    public static native double[] queryRope(int var0, long var1);

    @ApiStatus.Internal
    public static native void configFrequencyAndDamping(double var0, double var2);

    @ApiStatus.Internal
    public static native void configSolverIterations(int var0, int var1, int var2);

    @ApiStatus.Internal
    public static native void configMinIslandSize(int var0);

    @ApiStatus.Internal
    public static native void dispose();

    @ApiStatus.Internal
    public static void setMassPropertiesFrom(int dimensionID, int id, MassData massTracker) {
        Matrix3dc inertiaTensor = massTracker.getInertiaTensor();
        Vector3dc centerOfMass = massTracker.getCenterOfMass();
        double mass = massTracker.getMass();
        double[] centerOfMassArray = new double[]{centerOfMass.x(), centerOfMass.y(), centerOfMass.z()};
        double[] inertiaTensorArray = new double[]{inertiaTensor.m00(), inertiaTensor.m01(), inertiaTensor.m02(), inertiaTensor.m10(), inertiaTensor.m11(), inertiaTensor.m12(), inertiaTensor.m20(), inertiaTensor.m21(), inertiaTensor.m22()};
        Rapier3D.setMassProperties(dimensionID, id, mass, centerOfMassArray, inertiaTensorArray);
    }

    static {
        Rapier3D.loadLibrary();
    }
}

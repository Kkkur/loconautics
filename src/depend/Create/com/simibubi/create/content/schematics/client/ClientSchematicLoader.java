/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.schematics.client;

import com.simibubi.create.Create;
import com.simibubi.create.content.schematics.packet.SchematicUploadPacket;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.CreatePaths;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public class ClientSchematicLoader {
    public static final int PACKET_DELAY = 10;
    private final List<Component> availableSchematics = new ArrayList<Component>();
    private final Map<String, InputStream> activeUploads = new HashMap<String, InputStream>();
    private int packetCycle;

    public ClientSchematicLoader() {
        this.refresh();
    }

    public void tick() {
        if (this.activeUploads.isEmpty()) {
            return;
        }
        if (this.packetCycle-- > 0) {
            return;
        }
        this.packetCycle = 10;
        for (String schematic : new HashSet<String>(this.activeUploads.keySet())) {
            this.continueUpload(schematic);
        }
    }

    public void startNewUpload(String schematic) {
        Path path = CreatePaths.SCHEMATICS_DIR.resolve(schematic);
        if (!Files.exists(path, new LinkOption[0])) {
            Create.LOGGER.error("Missing Schematic file: {}", (Object)path);
            return;
        }
        try {
            long size = Files.size(path);
            if (!ClientSchematicLoader.validateSizeLimitation(size)) {
                return;
            }
            if (!ClientSchematicLoader.isGZIPEncoded(path.toFile())) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    player.displayClientMessage((Component)CreateLang.translateDirect("schematics.wrongFormat", new Object[0]), false);
                }
                return;
            }
            InputStream in = Files.newInputStream(path, StandardOpenOption.READ);
            this.activeUploads.put(schematic, in);
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)SchematicUploadPacket.begin(schematic, size));
        }
        catch (IOException e) {
            Create.LOGGER.error("Encountered an error while starting schematic upload", (Throwable)e);
        }
    }

    public static boolean validateSizeLimitation(long size) {
        if (Minecraft.getInstance().hasSingleplayerServer()) {
            return true;
        }
        long maxSize = ((Integer)AllConfigs.server().schematics.maxTotalSchematicSize.get()).intValue();
        if (size > maxSize * 1000L) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.displayClientMessage((Component)CreateLang.translateDirect("schematics.uploadTooLarge", new Object[0]).append(" (" + size / 1000L + " KB)."), false);
                player.displayClientMessage((Component)CreateLang.translateDirect("schematics.maxAllowedSize", new Object[0]).append(" " + maxSize + " KB"), false);
            }
            return false;
        }
        return true;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean isGZIPEncoded(File file) {
        try (FileInputStream fis = new FileInputStream(file);){
            byte[] bytes = new byte[2];
            if (fis.read(bytes) != 2) {
                boolean bl = false;
                return bl;
            }
            int byte1 = bytes[0] & 0xFF;
            int byte2 = bytes[1] & 0xFF;
            boolean bl = byte1 == 31 && byte2 == 139;
            return bl;
        }
        catch (IOException exception) {
            return false;
        }
    }

    private void continueUpload(String schematic) {
        if (this.activeUploads.containsKey(schematic)) {
            int maxPacketSize = (Integer)AllConfigs.server().schematics.maxSchematicPacketSize.get();
            byte[] data = new byte[maxPacketSize];
            try {
                int status = this.activeUploads.get(schematic).read(data);
                if (status != -1) {
                    if (status < maxPacketSize) {
                        data = Arrays.copyOf(data, status);
                    }
                    if (Minecraft.getInstance().level != null) {
                        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)SchematicUploadPacket.write(schematic, data));
                    } else {
                        this.activeUploads.remove(schematic);
                        return;
                    }
                }
                if (status < maxPacketSize) {
                    this.finishUpload(schematic);
                }
            }
            catch (IOException e) {
                Create.LOGGER.error("Encountered a error while uploading schematic", (Throwable)e);
            }
        }
    }

    private void finishUpload(String schematic) {
        if (this.activeUploads.containsKey(schematic)) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)SchematicUploadPacket.finish(schematic));
            this.activeUploads.remove(schematic);
        }
    }

    public void refresh() {
        FilesHelper.createFolderIfMissing(CreatePaths.SCHEMATICS_DIR);
        this.availableSchematics.clear();
        try (Stream<Path> paths2 = Files.list(CreatePaths.SCHEMATICS_DIR);){
            paths2.filter(f -> !Files.isDirectory(f, new LinkOption[0]) && f.getFileName().toString().endsWith(".nbt")).forEach(path -> {
                if (Files.isDirectory(path, new LinkOption[0])) {
                    return;
                }
                this.availableSchematics.add((Component)Component.literal((String)path.getFileName().toString()));
            });
        }
        catch (NoSuchFileException paths2) {
        }
        catch (IOException e) {
            Create.LOGGER.error("Failed to refresh schematics", (Throwable)e);
        }
        this.availableSchematics.sort((aT, bT) -> {
            String a = aT.getString();
            String b = bT.getString();
            if (a.endsWith(".nbt")) {
                a = a.substring(0, a.length() - 4);
            }
            if (b.endsWith(".nbt")) {
                b = b.substring(0, b.length() - 4);
            }
            int aLength = a.length();
            int bLength = b.length();
            int minSize = Math.min(aLength, bLength);
            boolean asNumeric = false;
            int lastNumericCompare = 0;
            for (int i = 0; i < minSize; ++i) {
                boolean bNumber;
                char aChar = a.charAt(i);
                char bChar = b.charAt(i);
                boolean aNumber = aChar >= '0' && aChar <= '9';
                boolean bl = bNumber = bChar >= '0' && bChar <= '9';
                if (asNumeric) {
                    if (aNumber && bNumber) {
                        if (lastNumericCompare != 0) continue;
                        lastNumericCompare = aChar - bChar;
                        continue;
                    }
                    if (aNumber) {
                        return 1;
                    }
                    if (bNumber) {
                        return -1;
                    }
                    if (lastNumericCompare == 0) {
                        if (aChar != bChar) {
                            return aChar - bChar;
                        }
                        asNumeric = false;
                        continue;
                    }
                    return lastNumericCompare;
                }
                if (aNumber && bNumber) {
                    asNumeric = true;
                    if (lastNumericCompare != 0) continue;
                    lastNumericCompare = aChar - bChar;
                    continue;
                }
                if (aChar == bChar) continue;
                return aChar - bChar;
            }
            if (asNumeric) {
                if (aLength > bLength && a.charAt(bLength) >= '0' && a.charAt(bLength) <= '9') {
                    return 1;
                }
                if (bLength > aLength && b.charAt(aLength) >= '0' && b.charAt(aLength) <= '9') {
                    return -1;
                }
                if (lastNumericCompare == 0) {
                    return aLength - bLength;
                }
                return lastNumericCompare;
            }
            return aLength - bLength;
        });
    }

    public List<Component> getAvailableSchematics() {
        return this.availableSchematics;
    }
}

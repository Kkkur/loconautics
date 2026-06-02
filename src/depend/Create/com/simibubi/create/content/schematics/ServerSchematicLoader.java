/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.schematics;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.schematics.SchematicExport;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.CreatePaths;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CSchematics;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ServerSchematicLoader {
    private final Map<String, SchematicUploadEntry> activeUploads;
    private final ObjectArrayList<String> deadEntries = ObjectArrayList.of();

    public ServerSchematicLoader() {
        this.activeUploads = new HashMap<String, SchematicUploadEntry>();
    }

    public void tick() {
        int timeout = (Integer)this.getConfig().schematicIdleTimeout.get();
        for (String upload : this.activeUploads.keySet()) {
            SchematicUploadEntry entry = this.activeUploads.get(upload);
            if (entry.idleTime++ <= timeout) continue;
            Create.LOGGER.warn("Schematic Upload timed out: " + upload);
            this.deadEntries.add((Object)upload);
        }
        for (String toRemove : this.deadEntries) {
            this.cancelUpload(toRemove);
        }
        this.deadEntries.clear();
    }

    public void shutdown() {
        new HashSet<String>(this.activeUploads.keySet()).forEach(this::cancelUpload);
    }

    public void handleNewUpload(ServerPlayer player, String schematic, long size, BlockPos pos) {
        String playerName = player.getGameProfile().getName();
        Path baseDir = CreatePaths.UPLOADED_SCHEMATICS_DIR;
        Path playerPath = baseDir.resolve(playerName).normalize();
        Path uploadPath = playerPath.resolve(schematic).normalize();
        String playerSchematicId = playerName + "/" + schematic;
        if (!playerPath.startsWith(baseDir) || !uploadPath.startsWith(playerPath)) {
            Create.LOGGER.warn("Attempted Schematic Upload with path traversal: {}", (Object)playerSchematicId);
            return;
        }
        FilesHelper.createFolderIfMissing(playerPath);
        if (!schematic.endsWith(".nbt")) {
            Create.LOGGER.warn("Attempted Schematic Upload with non-supported Format: {}", (Object)playerSchematicId);
            return;
        }
        if (!this.validateSchematicSizeOnServer(player, size)) {
            return;
        }
        if (this.activeUploads.containsKey(playerSchematicId)) {
            return;
        }
        try {
            long count;
            SchematicTableBlockEntity table = this.getTable(player.getCommandSenderWorld(), pos);
            if (table == null) {
                return;
            }
            Files.deleteIfExists(uploadPath);
            try (Stream<Path> list = Files.list(playerPath);){
                count = list.count();
            }
            if (count >= (long)((Integer)this.getConfig().maxSchematics.get()).intValue()) {
                Stream<Path> list2 = Files.list(playerPath);
                Optional<Path> lastFilePath = list2.filter(f -> !Files.isDirectory(f, new LinkOption[0])).min(Comparator.comparingLong(f -> f.toFile().lastModified()));
                list2.close();
                if (lastFilePath.isPresent()) {
                    Files.deleteIfExists(lastFilePath.get());
                }
            }
            OutputStream writer = Files.newOutputStream(uploadPath, new OpenOption[0]);
            this.activeUploads.put(playerSchematicId, new SchematicUploadEntry(writer, size, player.level(), pos));
            table.startUpload(schematic);
        }
        catch (IOException e) {
            Create.LOGGER.error("Exception Thrown when starting Upload: {}", (Object)playerSchematicId, (Object)e);
        }
    }

    protected boolean validateSchematicSizeOnServer(ServerPlayer player, long size) {
        long maxFileSize = ((Integer)this.getConfig().maxTotalSchematicSize.get()).intValue();
        if (size > maxFileSize * 1000L) {
            player.sendSystemMessage((Component)CreateLang.translateDirect("schematics.uploadTooLarge", new Object[0]).append((Component)Component.literal((String)(" (" + size / 1000L + " KB)."))));
            player.sendSystemMessage((Component)CreateLang.translateDirect("schematics.maxAllowedSize", new Object[0]).append((Component)Component.literal((String)(" " + maxFileSize + " KB"))));
            return false;
        }
        return true;
    }

    public CSchematics getConfig() {
        return AllConfigs.server().schematics;
    }

    public void handleWriteRequest(ServerPlayer player, String schematic, byte[] data) {
        String playerSchematicId = player.getGameProfile().getName() + "/" + schematic;
        if (this.activeUploads.containsKey(playerSchematicId)) {
            SchematicUploadEntry entry = this.activeUploads.get(playerSchematicId);
            entry.bytesUploaded += (long)data.length;
            if (data.length > (Integer)this.getConfig().maxSchematicPacketSize.get()) {
                Create.LOGGER.warn("Oversized Upload Packet received: {}", (Object)playerSchematicId);
                this.cancelUpload(playerSchematicId);
                return;
            }
            if (entry.bytesUploaded > entry.totalBytes) {
                Create.LOGGER.warn("Received more data than Expected: {}", (Object)playerSchematicId);
                this.cancelUpload(playerSchematicId);
                return;
            }
            try {
                entry.stream.write(data);
                entry.idleTime = 0;
                SchematicTableBlockEntity table = this.getTable(entry.world, entry.tablePos);
                if (table == null) {
                    return;
                }
                table.uploadingProgress = (float)((double)entry.bytesUploaded / (double)entry.totalBytes);
                table.sendUpdate = true;
            }
            catch (IOException e) {
                Create.LOGGER.error("Exception Thrown when uploading Schematic: {}", (Object)playerSchematicId, (Object)e);
                this.cancelUpload(playerSchematicId);
            }
        }
    }

    protected void cancelUpload(String playerSchematicId) {
        if (!this.activeUploads.containsKey(playerSchematicId)) {
            return;
        }
        SchematicUploadEntry entry = this.activeUploads.remove(playerSchematicId);
        try {
            entry.stream.close();
            Files.deleteIfExists(CreatePaths.UPLOADED_SCHEMATICS_DIR.resolve(playerSchematicId));
            Create.LOGGER.warn("Cancelled Schematic Upload: {}", (Object)playerSchematicId);
        }
        catch (IOException e) {
            Create.LOGGER.error("Exception Thrown when cancelling Upload: {}", (Object)playerSchematicId, (Object)e);
        }
        BlockPos pos = entry.tablePos;
        if (pos == null) {
            return;
        }
        SchematicTableBlockEntity table = this.getTable(entry.world, pos);
        if (table != null) {
            table.finishUpload();
        }
    }

    public SchematicTableBlockEntity getTable(Level world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof SchematicTableBlockEntity)) {
            return null;
        }
        SchematicTableBlockEntity table = (SchematicTableBlockEntity)be;
        return table;
    }

    public void handleFinishedUpload(ServerPlayer player, String schematic) {
        String playerSchematicId = player.getGameProfile().getName() + "/" + schematic;
        if (this.activeUploads.containsKey(playerSchematicId)) {
            try {
                this.activeUploads.get((Object)playerSchematicId).stream.close();
                SchematicUploadEntry removed = this.activeUploads.remove(playerSchematicId);
                Level world = removed.world;
                BlockPos pos = removed.tablePos;
                Create.LOGGER.info("New Schematic Uploaded: " + playerSchematicId);
                if (pos == null) {
                    return;
                }
                BlockState blockState = world.getBlockState(pos);
                if (AllBlocks.SCHEMATIC_TABLE.get() != blockState.getBlock()) {
                    return;
                }
                SchematicTableBlockEntity table = this.getTable(world, pos);
                if (table == null) {
                    return;
                }
                table.finishUpload();
                table.inventory.setStackInSlot(1, SchematicItem.create(world, schematic, player.getGameProfile().getName()));
            }
            catch (IOException e) {
                Create.LOGGER.error("Exception Thrown when finishing Upload: {}", (Object)playerSchematicId, (Object)e);
            }
        }
    }

    public void handleInstantSchematic(ServerPlayer player, String schematic, Level world, BlockPos pos, BlockPos bounds) {
        String playerName = player.getGameProfile().getName();
        Path baseDir = CreatePaths.UPLOADED_SCHEMATICS_DIR;
        Path playerPath = baseDir.resolve(playerName).normalize();
        Path uploadPath = playerPath.resolve(schematic).normalize();
        String playerSchematicId = playerName + "/" + schematic;
        if (!playerPath.startsWith(baseDir) || !uploadPath.startsWith(playerPath)) {
            Create.LOGGER.warn("Attempted Schematic Upload with path traversal: {}", (Object)playerSchematicId);
            return;
        }
        FilesHelper.createFolderIfMissing(playerPath);
        if (!schematic.endsWith(".nbt")) {
            Create.LOGGER.warn("Attempted Schematic Upload with non-supported Format: {}", (Object)playerSchematicId);
            return;
        }
        if (!AllItems.SCHEMATIC_AND_QUILL.isIn(player.getMainHandItem())) {
            return;
        }
        if (!this.tryDeleteOldestSchematic(playerPath)) {
            return;
        }
        SchematicExport.SchematicExportResult result = SchematicExport.saveSchematic(playerPath, schematic, true, world, pos, pos.offset((Vec3i)bounds).offset(-1, -1, -1));
        if (result != null) {
            player.setItemInHand(InteractionHand.MAIN_HAND, SchematicItem.create(world, schematic, playerName));
        } else {
            CreateLang.translate("schematicAndQuill.instant_failed", new Object[0]).style(ChatFormatting.RED).sendStatus((Player)player);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean tryDeleteOldestSchematic(Path dir) {
        try (Stream<Path> stream = Files.list(dir);){
            List<Path> files = stream.toList();
            if (files.size() < (Integer)this.getConfig().maxSchematics.get()) {
                boolean bl2 = true;
                return bl2;
            }
            Optional<Path> oldest = files.stream().min(Comparator.comparingLong(this::getLastModifiedTime));
            Files.delete(oldest.orElseThrow());
            boolean bl = true;
            return bl;
        }
        catch (IOException | IllegalStateException e) {
            Create.LOGGER.error("Error deleting oldest schematic", (Throwable)e);
            return false;
        }
    }

    private long getLastModifiedTime(Path file) {
        try {
            return Files.getLastModifiedTime(file, new LinkOption[0]).toMillis();
        }
        catch (IOException e) {
            Create.LOGGER.error("Error getting modification time of file {}", (Object)file.getFileName(), (Object)e);
            throw new IllegalStateException(e);
        }
    }

    public static class SchematicUploadEntry {
        public Level world;
        public BlockPos tablePos;
        public OutputStream stream;
        public long bytesUploaded;
        public long totalBytes;
        public int idleTime;

        public SchematicUploadEntry(OutputStream stream, long totalBytes, Level world, BlockPos tablePos) {
            this.stream = stream;
            this.totalBytes = totalBytes;
            this.tablePos = tablePos;
            this.world = world;
            this.bytesUploaded = 0L;
            this.idleTime = 0;
        }
    }
}

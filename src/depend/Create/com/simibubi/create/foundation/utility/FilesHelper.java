/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.internal.Streams
 *  com.google.gson.stream.JsonReader
 *  net.createmod.catnip.lang.Lang
 */
package com.simibubi.create.foundation.utility;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.simibubi.create.Create;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.createmod.catnip.lang.Lang;

public class FilesHelper {
    public static void createFolderIfMissing(Path path) {
        try {
            Files.createDirectories(path, new FileAttribute[0]);
        }
        catch (IOException e) {
            Path parentPath = path.getParent() == null ? path : path.getParent();
            Create.LOGGER.warn("Could not create Folder: {}", (Object)parentPath);
        }
    }

    public static String findFirstValidFilename(String name, Path folderPath, String extension) {
        String filename;
        Path filepath;
        int index = 0;
        do {
            filename = FilesHelper.slug(name) + (String)(index == 0 ? "" : "_" + index) + "." + extension;
            ++index;
        } while (Files.exists(filepath = folderPath.resolve(filename), new LinkOption[0]));
        return filename;
    }

    public static String slug(String name) {
        return Lang.asId((String)name).replaceAll("\\W+", "_");
    }

    private static JsonElement loadJson(InputStream inputStream) {
        try {
            JsonReader reader = new JsonReader((Reader)new BufferedReader(new InputStreamReader(inputStream)));
            reader.setLenient(true);
            JsonElement element = Streams.parse((JsonReader)reader);
            reader.close();
            inputStream.close();
            return element;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonElement loadJsonResource(String filepath) {
        return FilesHelper.loadJson(ClassLoader.getSystemResourceAsStream(filepath));
    }
}

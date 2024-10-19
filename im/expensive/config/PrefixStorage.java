/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.config;

import im.expensive.command.api.PrefixImpl;
import im.expensive.utils.client.IMinecraft;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

public final class PrefixStorage
implements IMinecraft {
    private static final File file = new File(PrefixStorage.mc.gameDir, File.separator + "saves" + File.separator + "files" + File.separator + "other" + File.separator + "prefix.cfg");
    private static PrefixImpl prefixImpl = new PrefixImpl();
    public static String prefix = "";

    public static void load() {
        if (file.exists()) {
            prefixImpl.set(Files.readString((Path)file.toPath()));
        } else {
            file.createNewFile();
            Files.write(file.toPath(), Collections.singleton("."), StandardOpenOption.WRITE);
            prefix = ".";
        }
    }

    public static void updatePrefix(String newPrefix) {
        Files.write(file.toPath(), Collections.singleton(newPrefix), StandardOpenOption.WRITE);
    }

    private PrefixStorage() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}


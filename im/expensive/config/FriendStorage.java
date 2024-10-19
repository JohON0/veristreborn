/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.config;

import im.expensive.utils.SoundUtil;
import im.expensive.utils.client.IMinecraft;
import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashSet;
import java.util.Set;

public final class FriendStorage
implements IMinecraft {
    private static int color = new Color(128, 255, 128).getRGB();
    private static final Set<String> friends = new HashSet<String>();
    private static final File file = new File(FriendStorage.mc.gameDir, File.separator + "saves" + File.separator + "files" + File.separator + "other" + File.separator + "friends.cfg");

    public static void load() {
        if (file.exists()) {
            friends.addAll(Files.readAllLines(file.toPath()));
        } else {
            file.createNewFile();
        }
    }

    public static void add(String name) {
        friends.add(name);
        SoundUtil.playSound("friendadd.wav");
        FriendStorage.save();
    }

    public static void remove(String name) {
        friends.remove(name);
        SoundUtil.playSound("friendremove.wav");
        FriendStorage.save();
    }

    public static void clear() {
        friends.clear();
        SoundUtil.playSound("friendremove.wav");
        FriendStorage.save();
    }

    public static boolean isFriend(String name) {
        return friends.contains(name);
    }

    private static void save() {
        Files.write(file.toPath(), friends, new OpenOption[0]);
    }

    private FriendStorage() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int getColor() {
        return color;
    }

    public static Set<String> getFriends() {
        return friends;
    }
}


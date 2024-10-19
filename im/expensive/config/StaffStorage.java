/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.config;

import im.expensive.utils.SoundUtil;
import im.expensive.utils.client.IMinecraft;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashSet;
import java.util.Set;

public final class StaffStorage
implements IMinecraft {
    private static final Set<String> staffs = new HashSet<String>();
    private static final File file = new File(StaffStorage.mc.gameDir + File.separator + "saves" + File.separator + "files" + File.separator + "other" + File.separator + "staffs.cfg");

    public static void load() {
        if (file.exists()) {
            staffs.addAll(Files.readAllLines(file.toPath()));
        } else {
            file.createNewFile();
        }
    }

    public static void add(String name) {
        staffs.add(name);
        SoundUtil.playSound("friendadd.wav");
        StaffStorage.save();
    }

    public static void remove(String name) {
        staffs.remove(name);
        SoundUtil.playSound("friendremove.wav");
        StaffStorage.save();
    }

    public static void clear() {
        staffs.clear();
        SoundUtil.playSound("friendremove.wav");
        StaffStorage.save();
    }

    public static boolean isStaff(String name) {
        return staffs.contains(name);
    }

    private static void save() {
        Files.write(file.toPath(), staffs, new OpenOption[0]);
    }

    private StaffStorage() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Set<String> getStaffs() {
        return staffs;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.drag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import im.expensive.utils.drag.Dragging;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import net.minecraft.client.Minecraft;

public class DragManager {
    public static LinkedHashMap<String, Dragging> draggables = new LinkedHashMap();
    private static final File DRAG_DATA = new File(Minecraft.getInstance().gameDir, "\\saves\\files\\other\\drags.cfg");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    public static void save() {
        if (!DRAG_DATA.exists()) {
            DRAG_DATA.getParentFile().mkdirs();
        }
        try {
            Files.writeString((Path)DRAG_DATA.toPath(), (CharSequence)GSON.toJson(draggables.values()), (OpenOption[])new OpenOption[0]);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void load() {
        Dragging[] draggings;
        if (!DRAG_DATA.exists()) {
            DRAG_DATA.getParentFile().mkdirs();
            return;
        }
        try {
            draggings = GSON.fromJson(Files.readString((Path)DRAG_DATA.toPath()), Dragging[].class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        for (Dragging dragging : draggings) {
            if (dragging == null) {
                return;
            }
            Dragging currentDrag = draggables.get(dragging.getName());
            if (currentDrag == null) continue;
            currentDrag.setX(dragging.getX());
            currentDrag.setY(dragging.getY());
            draggables.put(dragging.getName(), currentDrag);
        }
    }

    public static void resetAllPositions() {
        for (Dragging dragging : draggables.values()) {
            dragging.resetPosition();
        }
        DragManager.save();
    }
}


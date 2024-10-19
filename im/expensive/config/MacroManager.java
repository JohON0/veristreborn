/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.config;

import im.expensive.utils.client.IMinecraft;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MacroManager
implements IMinecraft {
    public List<Macro> macroList = new ArrayList<Macro>();
    public File macroFile;

    public MacroManager() {
        this.macroFile = new File(MacroManager.mc.gameDir, "\\saves\\files\\other\\macro.cfg");
    }

    public void init() throws IOException {
        if (!this.macroFile.exists()) {
            this.macroFile.createNewFile();
        } else {
            this.readFile();
        }
    }

    public boolean isEmpty() {
        return this.macroList.isEmpty();
    }

    public void addMacro(String name, String message, int key) {
        this.macroList.add(new Macro(name, message, key));
        this.writeFile();
    }

    public boolean hasMacro(String macroName) {
        for (Macro macro : this.macroList) {
            if (!macro.getName().equalsIgnoreCase(macroName)) continue;
            return true;
        }
        return false;
    }

    public void deleteMacro(String name) {
        if (this.macroList.stream().anyMatch(macro -> macro.getName().equals(name))) {
            this.macroList.removeIf(macro -> macro.getName().equalsIgnoreCase(name));
            this.writeFile();
        }
    }

    public void clearList() {
        if (!this.macroList.isEmpty()) {
            this.macroList.clear();
        }
        this.writeFile();
    }

    public void onKeyPressed(int key) {
        if (MacroManager.mc.player == null) {
            return;
        }
        this.macroList.stream().filter(macro -> macro.getKey() == key).findFirst().ifPresent(macro -> {
            try {
                MacroManager.mc.player.sendChatMessage(macro.getMessage());
            } catch (Exception e) {
                this.print("\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043e\u0442\u043f\u0440\u0430\u0432\u043a\u0438 \u043a\u043e\u043c\u0430\u043d\u0434\u044b " + e);
            }
        });
    }

    public void writeFile() {
        StringBuilder builder = new StringBuilder();
        this.macroList.forEach(macro -> builder.append(macro.getName()).append(":").append(macro.getMessage()).append(":").append(String.valueOf(macro.getKey()).toUpperCase()).append("\n"));
        Files.write(this.macroFile.toPath(), builder.toString().getBytes(), new OpenOption[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void readFile() {
        FileInputStream fileInputStream = new FileInputStream(this.macroFile.getAbsolutePath());
        BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(fileInputStream)));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String curLine = line.trim();
                String name = curLine.split(":")[0];
                String command = curLine.split(":")[1];
                String key = curLine.split(":")[2];
                this.macroList.add(new Macro(name, command, Integer.parseInt(key)));
            }
        } finally {
            if (Collections.singletonList(reader).get(0) != null) {
                reader.close();
            }
        }
    }

    public static final class Macro {
        private final String name;
        private final String message;
        private final int key;

        public Macro(String name, String message, int key) {
            this.name = name;
            this.message = message;
            this.key = key;
        }

        public String getName() {
            return this.name;
        }

        public String getMessage() {
            return this.message;
        }

        public int getKey() {
            return this.key;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Macro)) {
                return false;
            }
            Macro other = (Macro)o;
            if (this.getKey() != other.getKey()) {
                return false;
            }
            String this$name = this.getName();
            String other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
                return false;
            }
            String this$message = this.getMessage();
            String other$message = other.getMessage();
            return !(this$message == null ? other$message != null : !this$message.equals(other$message));
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getKey();
            String $name = this.getName();
            result = result * 59 + ($name == null ? 43 : $name.hashCode());
            String $message = this.getMessage();
            result = result * 59 + ($message == null ? 43 : $message.hashCode());
            return result;
        }

        public String toString() {
            return "MacroManager.Macro(name=" + this.getName() + ", message=" + this.getMessage() + ", key=" + this.getKey() + ")";
        }
    }
}


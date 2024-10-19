/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import im.expensive.Expensive;
import im.expensive.ui.mainmenu.Alt;
import im.expensive.utils.client.IMinecraft;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import net.minecraft.util.Session;

public class AltConfig
implements IMinecraft {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File file = new File(AltConfig.mc.gameDir, "\\saves\\files\\other\\alts.cfg");

    public void init() throws Exception {
        if (!file.exists()) {
            file.createNewFile();
        } else {
            this.readAlts();
        }
    }

    public static void updateFile() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("last", AltConfig.mc.session.getUsername());
        JsonArray altsArray = new JsonArray();
        for (Alt alt : Expensive.getInstance().getAltScreen().alts) {
            altsArray.add(alt.name);
        }
        jsonObject.add("alts", altsArray);
        try (PrintWriter printWriter = new PrintWriter(file);){
            printWriter.println(gson.toJson(jsonObject));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readAlts() throws FileNotFoundException {
        JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(file)));
        if (jsonElement.isJsonNull()) {
            return;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("last")) {
            AltConfig.mc.session = new Session(jsonObject.get("last").getAsString(), UUID.randomUUID().toString(), "", "mojang");
        }
        if (jsonObject.has("alts")) {
            for (JsonElement element : jsonObject.get("alts").getAsJsonArray()) {
                String name = element.getAsString();
                Expensive.getInstance().getAltScreen().alts.add(new Alt(name));
            }
        }
    }
}


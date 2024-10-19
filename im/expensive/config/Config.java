/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import im.expensive.Expensive;
import im.expensive.modules.settings.Setting;
import im.expensive.modules.settings.impl.BindSetting;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ColorSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.modules.settings.impl.StringSetting;
import im.expensive.utils.client.IMinecraft;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

public class Config
implements IMinecraft {
    private final File file;
    private final String name;

    public Config(String name) {
        this.name = name;
        this.file = new File(new File(Minecraft.getInstance().gameDir, "\\saves\\files\\configs"), name + ".cfg");
    }

    public void loadConfig(JsonObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        if (jsonObject.has("modules")) {
            this.loadFunctionSettings(jsonObject.getAsJsonObject("modules"));
        }
    }

    private void loadFunctionSettings(JsonObject functionsObject) {
        Expensive.getInstance().getModuleManager().getModules().forEach(f -> {
            JsonObject moduleObject = functionsObject.getAsJsonObject(f.getName().toLowerCase());
            if (moduleObject == null) {
                return;
            }
            f.setState(false, true);
            this.loadSettingFromJson(moduleObject, "bind", value -> f.setBind(value.getAsInt()));
            this.loadSettingFromJson(moduleObject, "state", value -> f.setState(value.getAsBoolean(), true));
            f.getSettings().forEach(setting -> this.loadIndividualSetting(moduleObject, (Setting<?>)setting));
        });
    }

    private void loadIndividualSetting(JsonObject moduleObject, Setting<?> setting) {
        JsonElement settingElement = moduleObject.get(setting.getName());
        if (settingElement == null || settingElement.isJsonNull()) {
            return;
        }
        if (setting instanceof SliderSetting) {
            ((SliderSetting)setting).set(Float.valueOf(settingElement.getAsFloat()));
        }
        if (setting instanceof BooleanSetting) {
            ((BooleanSetting)setting).set(settingElement.getAsBoolean());
        }
        if (setting instanceof ColorSetting) {
            ((ColorSetting)setting).set(settingElement.getAsInt());
        }
        if (setting instanceof ModeSetting) {
            ((ModeSetting)setting).set(settingElement.getAsString());
        }
        if (setting instanceof BindSetting) {
            ((BindSetting)setting).set(settingElement.getAsInt());
        }
        if (setting instanceof StringSetting) {
            ((StringSetting)setting).set(settingElement.getAsString());
        }
        if (setting instanceof ModeListSetting) {
            this.loadModeListSetting((ModeListSetting)setting, moduleObject);
        }
    }

    private void loadModeListSetting(ModeListSetting setting, JsonObject moduleObject) {
//        JsonObject elements = moduleObject.getAsJsonObject(setting.getName());
//        ((List)setting.get()).forEach(option -> {
//            JsonElement optionElement = elements.get(option.getName());
//            if (optionElement != null && !optionElement.isJsonNull()) {
//                option.getClass(optionElement.getAsBoolean());
//            }
//        });
    }

    private void loadSettingFromJson(JsonObject jsonObject, String key, Consumer<JsonElement> consumer) {
        JsonElement element = jsonObject.get(key);
        if (element != null && !element.isJsonNull()) {
            consumer.accept(element);
        }
    }

    public JsonElement saveConfig() {
        JsonObject functionsObject = new JsonObject();
        this.saveFunctionSettings(functionsObject);
        JsonObject newObject = new JsonObject();
        newObject.add("modules", functionsObject);
        return newObject;
    }

    private void saveFunctionSettings(JsonObject functionsObject) {
        Expensive.getInstance().getModuleManager().getModules().forEach(module -> {
            JsonObject moduleObject = new JsonObject();
            moduleObject.addProperty("bind", module.getBind());
            moduleObject.addProperty("state", module.isState());
            module.getSettings().forEach(setting -> this.saveIndividualSetting(moduleObject, (Setting<?>)setting));
            functionsObject.add(module.getName().toLowerCase(), moduleObject);
        });
    }

    private void saveIndividualSetting(JsonObject moduleObject, Setting<?> setting) {
        if (setting instanceof BooleanSetting) {
            moduleObject.addProperty(setting.getName(), (Boolean)((BooleanSetting)setting).get());
        }
        if (setting instanceof SliderSetting) {
            moduleObject.addProperty(setting.getName(), (Number)((SliderSetting)setting).get());
        }
        if (setting instanceof ModeSetting) {
            moduleObject.addProperty(setting.getName(), (String)((ModeSetting)setting).get());
        }
        if (setting instanceof ColorSetting) {
            moduleObject.addProperty(setting.getName(), (Number)((ColorSetting)setting).get());
        }
        if (setting instanceof BindSetting) {
            moduleObject.addProperty(setting.getName(), (Number)((BindSetting)setting).get());
        }
        if (setting instanceof StringSetting) {
            moduleObject.addProperty(setting.getName(), (String)((StringSetting)setting).get());
        }
        if (setting instanceof ModeListSetting) {
            this.saveModeListSetting(moduleObject, (ModeListSetting)setting);
        }
    }

    private void saveModeListSetting(JsonObject moduleObject, ModeListSetting setting) {
        JsonObject elements = new JsonObject();
//        ((List)setting.get()).forEach(option -> elements.addProperty(option.getName(), (Boolean)option.get()));
        moduleObject.add(setting.getName(), elements);
    }

    public File getFile() {
        return this.file;
    }

    public String getName() {
        return this.name;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.feature;

import im.expensive.Expensive;
import im.expensive.command.api.CommandException;
import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.CommandWithAdvice;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.Parameters;
import im.expensive.command.interfaces.Prefix;
import im.expensive.modules.api.Module;
import im.expensive.ui.notify.impl.WarningNotify;
import im.expensive.utils.client.KeyStorage;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;

public class BindCommand
implements Command,
CommandWithAdvice {
    private final Prefix prefix;
    private final Logger logger;

    @Override
    public void execute(Parameters parameters) {
        String commandType;
        switch (commandType = parameters.asString(0).orElse("")) {
            case "add": {
                this.addBindToFunction(parameters, this.logger);
                break;
            }
            case "remove": {
                this.removeBindFromFunction(parameters, this.logger);
                break;
            }
            case "clear": {
                this.clearAllBindings(this.logger);
                break;
            }
            case "list": {
                this.listBoundKeys(this.logger);
                break;
            }
            default: {
                throw new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0442\u0438\u043f \u043a\u043e\u043c\u0430\u043d\u0434\u044b:" + TextFormatting.GRAY + " add, remove, clear, list");
            }
        }
    }

    @Override
    public String name() {
        return "bind";
    }

    @Override
    public String description() {
        return "\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0437\u0430\u0431\u0438\u043d\u0434\u0438\u0442\u044c \u0444\u0443\u043d\u043a\u0446\u0438\u044e \u043d\u0430 \u043e\u043f\u0440\u0435\u0434\u0435\u043b\u0435\u043d\u043d\u0443\u044e \u043a\u043b\u0430\u0432\u0438\u0448\u0443";
    }

    @Override
    public List<String> adviceMessage() {
        Expensive.getInstance().getNotifyManager().add(0, new WarningNotify("\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b!", 1000L));
        String commandPrefix = this.prefix.get();
        return List.of((commandPrefix + "bind add <function> <key> - \u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043d\u043e\u0432\u044b\u0439 \u0431\u0438\u043d\u0434"), (commandPrefix + "bind remove <function> <key> - \u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0431\u0438\u043d\u0434"), (commandPrefix + "bind list - \u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0431\u0438\u043d\u0434\u043e\u0432"), (commandPrefix + "bind clear - \u041e\u0447\u0438\u0441\u0442\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0431\u0438\u043d\u0434\u043e\u0432"), ("\u041f\u0440\u0438\u043c\u0435\u0440: " + TextFormatting.RED + commandPrefix + "bind add HitAura R"));
    }

    private void addBindToFunction(Parameters parameters, Logger logger) {
        String functionName = parameters.asString(1).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0444\u0443\u043d\u043a\u0446\u0438\u0438!"));
        String keyName = parameters.asString(2).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043a\u043d\u043e\u043f\u043a\u0443!"));
        Module module = null;
        for (Module func : Expensive.getInstance().getModuleManager().getModules()) {
            if (!func.getName().toLowerCase(Locale.ROOT).equals(functionName.toLowerCase(Locale.ROOT))) continue;
            module = func;
            break;
        }
        Integer key = KeyStorage.getKey(keyName.toUpperCase());
        if (module == null) {
            logger.log(TextFormatting.RED + "\u0424\u0443\u043d\u043a\u0446\u0438\u044f " + functionName + " \u043d\u0435 \u0431\u044b\u043b\u0430 \u043d\u0430\u0439\u0434\u0435\u043d\u0430");
            return;
        }
        if (key == null) {
            logger.log(TextFormatting.RED + "\u041a\u043b\u0430\u0432\u0438\u0448\u0430 " + keyName + " \u043d\u0435 \u0431\u044b\u043b\u0430 \u043d\u0430\u0439\u0434\u0435\u043d\u0430");
            return;
        }
        module.setBind(key);
        logger.log(TextFormatting.GREEN + "\u0411\u0438\u043d\u0434 " + TextFormatting.RED + keyName.toUpperCase() + TextFormatting.GREEN + " \u0431\u044b\u043b \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d \u0434\u043b\u044f \u0444\u0443\u043d\u043a\u0446\u0438\u0438 " + TextFormatting.RED + functionName);
    }

    private void removeBindFromFunction(Parameters parameters, Logger logger) {
        String functionName = parameters.asString(1).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0444\u0443\u043d\u043a\u0446\u0438\u0438!"));
        String keyName = parameters.asString(2).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043a\u043d\u043e\u043f\u043a\u0443!"));
        Expensive.getInstance().getModuleManager().getModules().stream().filter(f -> f.getName().equalsIgnoreCase(functionName)).forEach(f -> {
            f.setBind(0);
            logger.log(TextFormatting.GREEN + "\u041a\u043b\u0430\u0432\u0438\u0448\u0430 " + TextFormatting.RED + keyName.toUpperCase() + TextFormatting.GREEN + " \u0431\u044b\u043b\u0430 \u043e\u0442\u0432\u044f\u0437\u0430\u043d\u0430 \u043e\u0442 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 " + TextFormatting.RED + f.getName());
        });
    }

    private void clearAllBindings(Logger logger) {
        Expensive.getInstance().getModuleManager().getModules().forEach(function -> function.setBind(0));
        logger.log(TextFormatting.GREEN + "\u0412\u0441\u0435 \u043a\u043b\u0430\u0432\u0438\u0448\u0438 \u0431\u044b\u043b\u0438 \u043e\u0442\u0432\u044f\u0437\u0430\u043d\u044b \u043e\u0442 \u043c\u043e\u0434\u0443\u043b\u0435\u0439");
    }

    private void listBoundKeys(Logger logger) {
        logger.log(TextFormatting.GRAY + "\u0421\u043f\u0438\u0441\u043e\u043a \u0432\u0441\u0435\u0445 \u043c\u043e\u0434\u0443\u043b\u0435\u0439 \u0441 \u043f\u0440\u0438\u0432\u044f\u0437\u0430\u043d\u043d\u044b\u043c\u0438 \u043a\u043b\u0430\u0432\u0438\u0448\u0430\u043c\u0438:");
        Expensive.getInstance().getModuleManager().getModules().stream().filter(f -> f.getBind() != 0).map(f -> {
            String keyName = GLFW.glfwGetKeyName(f.getBind(), -1);
            keyName = keyName != null ? keyName : "";
            return String.format("%s [%s%s%s]", new Object[]{f.getName(), TextFormatting.GRAY, keyName, TextFormatting.WHITE});
        }).forEach(logger::log);
    }

    public BindCommand(Prefix prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }
}


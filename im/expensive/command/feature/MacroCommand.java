/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.feature;

import im.expensive.Expensive;
import im.expensive.command.api.CommandException;
import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.CommandWithAdvice;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.MultiNamedCommand;
import im.expensive.command.interfaces.Parameters;
import im.expensive.command.interfaces.Prefix;
import im.expensive.config.MacroManager;
import im.expensive.ui.notify.impl.WarningNotify;
import im.expensive.utils.client.KeyStorage;
import java.util.List;
import net.minecraft.util.text.TextFormatting;

public class MacroCommand
implements Command,
MultiNamedCommand,
CommandWithAdvice {
    private final MacroManager macroManager;
    private final Prefix prefix;
    private final Logger logger;

    @Override
    public void execute(Parameters parameters) {
        String commandType;
        switch (commandType = (String)parameters.asString(0).orElseThrow()) {
            case "add": {
                this.addMacro(parameters);
                break;
            }
            case "remove": {
                this.removeMacro(parameters);
                break;
            }
            case "clear": {
                this.clearMacros();
                break;
            }
            case "list": {
                this.printMacrosList();
                break;
            }
            default: {
                throw new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0442\u0438\u043f \u043a\u043e\u043c\u0430\u043d\u0434\u044b:" + TextFormatting.GRAY + " add, remove, clear, list");
            }
        }
    }

    @Override
    public String name() {
        return "macro";
    }

    @Override
    public String description() {
        return "\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0443\u043f\u0440\u0430\u0432\u043b\u044f\u0442\u044c \u043c\u0430\u043a\u0440\u043e\u0441\u0430\u043c\u0438";
    }

    @Override
    public List<String> adviceMessage() {
        Expensive.getInstance().getNotifyManager().add(0, new WarningNotify("\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b!", 1000L));
        String commandPrefix = this.prefix.get();
        return List.of((Object)(commandPrefix + "macro add <name> <key> <message> - \u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043d\u043e\u0432\u044b\u0439 \u043c\u0430\u043a\u0440\u043e\u0441"), (Object)(commandPrefix + "macro remove <name> - \u0423\u0434\u0430\u043b\u0438\u0442\u044c \u043c\u0430\u043a\u0440\u043e\u0441"), (Object)(commandPrefix + "macro list - \u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u043c\u0430\u043a\u0440\u043e\u0441\u043e\u0432"), (Object)(commandPrefix + "macro clear - \u041e\u0447\u0438\u0441\u0442\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u043c\u0430\u043a\u0440\u043e\u0441\u043e\u0432"), (Object)("\u041f\u0440\u0438\u043c\u0435\u0440: " + TextFormatting.RED + commandPrefix + "macro add home H /home home"));
    }

    @Override
    public List<String> aliases() {
        return List.of((Object)"macros");
    }

    private void addMacro(Parameters parameters) {
        String macroName = parameters.asString(1).orElseThrow(() -> new CommandException(TextFormatting.GRAY + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043c\u0430\u043a\u0440\u043e\u0441\u0430."));
        String macroKey = parameters.asString(2).orElseThrow(() -> new CommandException(TextFormatting.GRAY + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043a\u043d\u043e\u043f\u043a\u0443 \u043f\u0440\u0438 \u043d\u0430\u0436\u0430\u0442\u0438\u0438 \u043a\u043e\u0442\u043e\u0440\u043e\u0439 \u0441\u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u043c\u0430\u043a\u0440\u043e\u0441."));
        String macroMessage = parameters.collectMessage(3);
        if (macroMessage.isEmpty()) {
            throw new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435, \u043a\u043e\u0442\u043e\u0440\u043e\u0435 \u0431\u0443\u0434\u0435\u0442 \u043f\u0438\u0441\u0430\u0442\u044c \u043c\u0430\u043a\u0440\u043e\u0441.");
        }
        Integer key = KeyStorage.getKey(macroKey.toUpperCase());
        if (key == null) {
            this.logger.log("\u041a\u043b\u0430\u0432\u0438\u0448\u0430 " + macroKey + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430!");
            return;
        }
        this.checkMacroExist(macroName);
        this.macroManager.addMacro(macroName, macroMessage, key);
        this.logger.log(TextFormatting.GREEN + "\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d \u043c\u0430\u043a\u0440\u043e\u0441 \u0441 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435\u043c " + TextFormatting.RED + macroName + TextFormatting.GREEN + " \u0441 \u043a\u043d\u043e\u043f\u043a\u043e\u0439 " + TextFormatting.RED + macroKey + TextFormatting.GREEN + " \u0441 \u043a\u043e\u043c\u0430\u043d\u0434\u043e\u0439 " + TextFormatting.RED + macroMessage);
    }

    private void removeMacro(Parameters parameters) {
        String macroName = parameters.asString(1).orElseThrow(() -> new CommandException(TextFormatting.GRAY + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043c\u0430\u043a\u0440\u043e\u0441\u0430."));
        Expensive.getInstance().getMacroManager().deleteMacro(macroName);
        this.logger.log(TextFormatting.GREEN + "\u041c\u0430\u043a\u0440\u043e\u0441 " + TextFormatting.RED + macroName + TextFormatting.GREEN + " \u0431\u044b\u043b \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0443\u0434\u0430\u043b\u0435\u043d!");
    }

    private void clearMacros() {
        Expensive.getInstance().getMacroManager().clearList();
        this.logger.log(TextFormatting.GREEN + "\u0412\u0441\u0435 \u043c\u0430\u043a\u0440\u043e\u0441\u044b \u0431\u044b\u043b\u0438 \u0443\u0434\u0430\u043b\u0435\u043d\u044b.");
    }

    private void printMacrosList() {
        if (Expensive.getInstance().getMacroManager().isEmpty()) {
            this.logger.log(TextFormatting.RED + "\u0421\u043f\u0438\u0441\u043e\u043a \u043f\u0443\u0441\u0442\u043e\u0439");
            return;
        }
        Expensive.getInstance().getMacroManager().macroList.forEach(macro -> this.logger.log(TextFormatting.WHITE + "\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435: " + TextFormatting.GRAY + macro.getName() + TextFormatting.WHITE + ", \u041a\u043e\u043c\u0430\u043d\u0434\u0430: " + TextFormatting.GRAY + macro.getMessage() + TextFormatting.WHITE + ", \u041a\u043d\u043e\u043f\u043a\u0430: " + TextFormatting.GRAY + macro.getKey()));
    }

    private void checkMacroExist(String macroName) {
        if (this.macroManager.hasMacro(macroName)) {
            throw new CommandException(TextFormatting.RED + "\u041c\u0430\u043a\u0440\u043e\u0441 \u0441 \u0442\u0430\u043a\u0438\u043c \u0438\u043c\u0435\u043d\u0435\u043c \u0443\u0436\u0435 \u0435\u0441\u0442\u044c \u0432 \u0441\u043f\u0438\u0441\u043a\u0435!");
        }
    }

    public MacroCommand(MacroManager macroManager, Prefix prefix, Logger logger) {
        this.macroManager = macroManager;
        this.prefix = prefix;
        this.logger = logger;
    }
}


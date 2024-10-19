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
import im.expensive.config.AltConfig;
import im.expensive.ui.notify.impl.WarningNotify;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.text.TextFormatting;

public class LoginCommand
implements Command,
CommandWithAdvice,
MultiNamedCommand {
    private final Prefix prefix;
    private final Logger logger;
    private final Minecraft mc;

    @Override
    public void execute(Parameters parameters) {
        String random = Expensive.getInstance().randomNickname();
        String nameArgument = parameters.asString(0).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u041d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e \u0443\u043a\u0430\u0437\u0430\u0442\u044c \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435."));
        if (nameArgument.equals("random") || nameArgument.equals("r")) {
            nameArgument = random;
        }
        this.mc.session = new Session(nameArgument, "", "", "mojang");
        AltConfig.updateFile();
        this.logger.log(TextFormatting.GREEN + "\u041d\u0438\u043a \u0438\u0437\u043c\u0435\u043d\u0451\u043d \u043d\u0430 - " + TextFormatting.GRAY + "[" + TextFormatting.WHITE + nameArgument + TextFormatting.GRAY + "]" + TextFormatting.RED + " (\u0422\u0440\u0435\u0431\u0443\u0435\u0442\u0441\u044f \u043f\u0435\u0440\u0435\u0437\u0430\u0445\u043e\u0434)");
    }

    @Override
    public String name() {
        return "login";
    }

    @Override
    public String description() {
        return "\u041c\u0435\u043d\u044f\u0435\u0442 \u043d\u0438\u043a\u043d\u0435\u0439\u043c.";
    }

    @Override
    public List<String> adviceMessage() {
        Expensive.getInstance().getNotifyManager().add(0, new WarningNotify("\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b!", 1000L));
        String commandPrefix = this.prefix.get();
        return List.of((Object)(TextFormatting.GRAY + commandPrefix + "login <nickname> - \u041c\u0435\u043d\u044f\u0435\u0442 \u043d\u0438\u043a\u043d\u0435\u0439\u043c"), (Object)("\u041f\u0440\u0438\u043c\u0435\u0440: " + TextFormatting.RED + commandPrefix + "login VeristUser1337"));
    }

    @Override
    public List<String> aliases() {
        return List.of((Object)"l");
    }

    public LoginCommand(Prefix prefix, Logger logger, Minecraft mc) {
        this.prefix = prefix;
        this.logger = logger;
        this.mc = mc;
    }
}


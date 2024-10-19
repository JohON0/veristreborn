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
import im.expensive.ui.notify.impl.WarningNotify;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.math.NumberUtils;

public class HClipCommand
implements Command,
CommandWithAdvice,
MultiNamedCommand {
    private final Prefix prefix;
    private final Logger logger;
    private final Minecraft mc;

    @Override
    public void execute(Parameters parameters) {
        int i;
        String direction = parameters.asString(0).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u041d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e \u0443\u043a\u0430\u0437\u0430\u0442\u044c \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435."));
        if (!NumberUtils.isNumber(direction)) {
            this.logger.log(TextFormatting.RED + "\u041f\u043e\u0436\u0430\u043b\u0443\u0439\u0441\u0442\u0430, \u0432\u0432\u0435\u0434\u0438\u0442\u0435 \u0447\u0438\u0441\u043b\u043e \u0434\u043b\u044f \u044d\u0442\u043e\u0439 \u043a\u043e\u043c\u0430\u043d\u0434\u044b.");
            return;
        }
        double blocks = Double.parseDouble(direction);
        Vector3d lookVector = Minecraft.getInstance().player.getLook(1.0f).mul(blocks, 0.0, blocks);
        double newX = this.mc.player.getPosX() + lookVector.getX();
        double newZ = this.mc.player.getPosZ() + lookVector.getZ();
        for (i = 0; i < 5; ++i) {
            this.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(newX, this.mc.player.getPosY(), newZ, false));
        }
        this.mc.player.setPositionAndUpdate(newX, this.mc.player.getPosY(), newZ);
        for (i = 0; i < 5; ++i) {
            this.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(newX, this.mc.player.getPosY(), newZ, false));
        }
        String blockUnit = blocks > 1.0 ? "\u0431\u043b\u043e\u043a\u0430" : "\u0431\u043b\u043e\u043a";
        String message = String.format("\u0412\u044b \u0431\u044b\u043b\u0438 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043d\u0430 %.1f %s \u043f\u043e \u0433\u043e\u0440\u0438\u0437\u043e\u043d\u0442\u0430\u043b\u0438", blocks, blockUnit);
        this.logger.log(TextFormatting.GRAY + message);
    }

    @Override
    public String name() {
        return "hclip";
    }

    @Override
    public String description() {
        return "\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u0443\u0435\u0442 \u0432\u043f\u0435\u0440\u0451\u0434/\u043d\u0430\u0437\u0430\u0434 \u043f\u043e \u0433\u043e\u0440\u0438\u0437\u043e\u043d\u0442\u0430\u043b\u0438";
    }

    @Override
    public List<String> adviceMessage() {
        Expensive.getInstance().getNotifyManager().add(0, new WarningNotify("\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b!", 1000L));
        String commandPrefix = this.prefix.get();
        return List.of((Object)(TextFormatting.GRAY + commandPrefix + "hclip <distance> - \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043d\u0430 \u0443\u043a\u0430\u0437\u0430\u043d\u043d\u043e\u0435 \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435"), (Object)("\u041f\u0440\u0438\u043c\u0435\u0440: " + TextFormatting.RED + commandPrefix + "hclip 1"));
    }

    @Override
    public List<String> aliases() {
        return List.of((Object)"hc");
    }

    public HClipCommand(Prefix prefix, Logger logger, Minecraft mc) {
        this.prefix = prefix;
        this.logger = logger;
        this.mc = mc;
    }
}


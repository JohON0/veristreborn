/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.feature;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.command.api.CommandException;
import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.CommandWithAdvice;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.MultiNamedCommand;
import im.expensive.command.interfaces.Parameters;
import im.expensive.command.interfaces.Prefix;
import im.expensive.events.EventDisplay;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.ModuleManager;
import im.expensive.modules.impl.misc.SelfDestruct;
import im.expensive.ui.notify.impl.WarningNotify;
import im.expensive.utils.animations.Animation;
import im.expensive.utils.animations.impl.DecelerateAnimation;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.projections.ProjectionUtil;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.text.font.ClientFonts;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.TextFormatting;

public class WayCommand
implements Command,
CommandWithAdvice,
MultiNamedCommand,
IMinecraft {
    private final Prefix prefix;
    private final Logger logger;
    private final Animation alpha = new DecelerateAnimation(255, 255.0);
    private final Map<String, Vector3i> waysMap = new LinkedHashMap<String, Vector3i>();
    private Vector3i vec3i;
    private Vector3d vec3d;
    private Vector2f vec2f;
    private int distance;

    public WayCommand(Prefix prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
        Expensive.getInstance().getEventBus().register(this);
    }

    @Override
    public void execute(Parameters parameters) {
        String commandType;
        switch (commandType = parameters.asString(0).orElse("")) {
            case "add": {
                this.addGPS(parameters);
                break;
            }
            case "remove": {
                this.removeGPS(parameters);
                break;
            }
            case "clear": {
                this.waysMap.clear();
                this.logger.log("\u0412\u0441\u0435 \u043f\u0443\u0442\u0438 \u0431\u044b\u043b\u0438 \u0443\u0434\u0430\u043b\u0435\u043d\u044b!");
                break;
            }
            case "list": {
                this.logger.log("\u0421\u043f\u0438\u0441\u043e\u043a \u043f\u0443\u0442\u0435\u0439:");
                for (String s : this.waysMap.keySet()) {
                    this.logger.log("- " + s + " " + this.waysMap.get(s));
                }
                break;
            }
            default: {
                throw new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0442\u0438\u043f \u043a\u043e\u043c\u0430\u043d\u0434\u044b:" + TextFormatting.GRAY + " add, remove, clear");
            }
        }
    }

    private void addGPS(Parameters param) {
        String name = param.asString(1).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0438\u043c\u044f \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b!"));
        int x = param.asInt(2).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043f\u0435\u0440\u0432\u0443\u044e \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0443!"));
        int y = param.asInt(3).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0432\u0442\u043e\u0440\u0443\u044e \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0443!"));
        int z = param.asInt(4).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0442\u0440\u0435\u0442\u044c\u044e \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0443!"));
        Vector3i vec = new Vector3i(x, y, z);
        this.waysMap.put(name, vec);
        this.logger.log("\u041f\u0443\u0442\u044c " + name + " \u0431\u044b\u043b \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d!");
    }

    private void removeGPS(Parameters param) {
        String name = param.asString(1).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0438\u043c\u044f \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b!"));
        this.waysMap.remove(name);
        this.logger.log("\u041f\u0443\u0442\u044c " + name + " \u0431\u044b\u043b \u0443\u0434\u0430\u043b\u0451\u043d!");
    }

    @Override
    public String name() {
        return "way";
    }

    @Override
    public String description() {
        return "\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0440\u0430\u0431\u043e\u0442\u0430\u0442\u044c \u0441 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0430\u043c\u0438 \u043f\u0443\u0442\u0435\u0439";
    }

    @Override
    public List<String> adviceMessage() {
        Expensive.getInstance().getNotifyManager().add(0, new WarningNotify("\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b!", 1000L));
        String commandPrefix = this.prefix.get();
        return List.of((Object)(commandPrefix + "way add <\u0438\u043c\u044f, x, y, z> - \u041f\u0440\u043e\u043b\u043e\u0436\u0438\u0442\u044c \u043f\u0443\u0442\u044c \u043a WayPoint'\u0443"), (Object)(commandPrefix + "way remove <\u0438\u043c\u044f> - \u0423\u0434\u0430\u043b\u0438\u0442\u044c WayPoint"), (Object)(commandPrefix + "way list - \u0421\u043f\u0438\u0441\u043e\u043a WayPoint'\u043e\u0432"), (Object)(commandPrefix + "way clear - \u041e\u0447\u0438\u0441\u0442\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a WayPoint'\u043e\u0432"), (Object)("\u041f\u0440\u0438\u043c\u0435\u0440: " + TextFormatting.RED + commandPrefix + "way add \u0410\u0438\u0440\u0414\u0440\u043e\u043f 1000 100 1000"));
    }

    @Subscribe
    public void onUdate(EventUpdate e) {
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {
        ModuleManager moduleManager = Expensive.getInstance().getModuleManager();
        SelfDestruct selfDestruct = moduleManager.getSelfDestruct();
        if (SelfDestruct.unhooked) {
            return;
        }
        if (this.waysMap.isEmpty()) {
            return;
        }
        for (String name : this.waysMap.keySet()) {
            this.vec3i = this.waysMap.get(name);
            this.vec3d = new Vector3d((double)this.vec3i.getX() + 0.5, (double)this.vec3i.getY() + 0.5, (double)this.vec3i.getZ() + 0.5);
            this.vec2f = ProjectionUtil.project(this.vec3d.x, this.vec3d.y, this.vec3d.z);
            this.distance = (int)Minecraft.getInstance().player.getPositionVec().distanceTo(this.vec3d);
            String text = name + " (" + this.distance + "M)";
            float textWith = Fonts.montserrat.getWidth(text, 8.0f);
            float fontHeight = Fonts.montserrat.getHeight(8.0f);
            float posX = this.vec2f.x - textWith / 2.0f;
            float posY = this.vec2f.y - fontHeight / 2.0f;
            float padding = 2.0f;
            ClientFonts.icon[30].drawString(e.getMatrixStack(), "A", (double)(posX + textWith / 2.0f - ClientFonts.icon[30].getWidth("A") / 2.0f), (double)(posY - 16.0f), ColorUtils.reAlphaInt(-1, 255));
            Fonts.montserrat.drawText(e.getMatrixStack(), text, posX, posY, ColorUtils.setAlpha(-1, 255), 8.0f);
        }
    }

    @Override
    public List<String> aliases() {
        return List.of((Object)"w");
    }
}


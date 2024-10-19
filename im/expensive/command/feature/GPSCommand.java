/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.feature;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.Expensive;
import im.expensive.command.api.CommandException;
import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.CommandWithAdvice;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.Parameters;
import im.expensive.command.interfaces.Prefix;
import im.expensive.events.EventDisplay;
import im.expensive.modules.api.ModuleManager;
import im.expensive.modules.impl.misc.SelfDestruct;
import im.expensive.modules.impl.render.Crosshair;
import im.expensive.utils.animations.Animation;
import im.expensive.utils.animations.impl.DecelerateAnimation;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;

public class GPSCommand
implements Command,
CommandWithAdvice,
IMinecraft {
    private final Prefix prefix;
    private final Logger logger;
    private Vector2f cordsMap = new Vector2f(0.0f, 0.0f);
    private final Animation alpha = new DecelerateAnimation(255, 255.0);

    public GPSCommand(Prefix prefix, Logger logger) {
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
            case "off": {
                this.removeGPS();
                break;
            }
            default: {
                throw new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0442\u0438\u043f \u043a\u043e\u043c\u0430\u043d\u0434\u044b:" + TextFormatting.GRAY + " add, off");
            }
        }
    }

    private void addGPS(Parameters param) {
        int x = param.asInt(1).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043f\u0435\u0440\u0432\u0443\u044e \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0443!"));
        int z = param.asInt(2).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0432\u0442\u043e\u0440\u0443\u044e \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0443!"));
        if (x == 0 && z == 0) {
            this.logger.log("\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u0431\u044b\u0442\u044c \u0431\u043e\u043b\u044c\u0448\u0435 \u043d\u0443\u043b\u044f.");
            return;
        }
        this.cordsMap = new Vector2f(x, z);
    }

    private void removeGPS() {
        this.cordsMap = new Vector2f(0.0f, 0.0f);
    }

    @Override
    public String name() {
        return "gps";
    }

    @Override
    public String description() {
        return "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0441\u0442\u0440\u0435\u043b\u043e\u0447\u043a\u0443 \u043a\u043e\u0442\u043e\u0440\u0430\u044f \u0432\u0435\u0434\u0451\u0442 \u043a \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0430\u043c";
    }

    @Override
    public List<String> adviceMessage() {
        String commandPrefix = this.prefix.get();
        return List.of((Object)(commandPrefix + "gps add <x, z> - \u041f\u0440\u043e\u043b\u043e\u0436\u0438\u0442\u044c \u043f\u0443\u0442\u044c"), (Object)(commandPrefix + "gps off - \u0423\u0434\u0430\u043b\u0438\u0442\u044c GPS"), (Object)("\u041f\u0440\u0438\u043c\u0435\u0440: " + TextFormatting.RED + commandPrefix + "gps add 500 152"));
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {
        ModuleManager moduleManager = Expensive.getInstance().getModuleManager();
        SelfDestruct selfDestruct = moduleManager.getSelfDestruct();
        if (SelfDestruct.unhooked || this.cordsMap.x == 0.0f && this.cordsMap.y == 0.0f) {
            return;
        }
        Vector3d vec3d = new Vector3d((double)this.cordsMap.x + 0.5, 100.5, (double)this.cordsMap.y + 0.5);
        int dst = (int)Math.sqrt(Math.pow(vec3d.x - GPSCommand.mc.player.getPosX(), 2.0) + Math.pow(vec3d.z - GPSCommand.mc.player.getPosZ(), 2.0));
        String text = dst + "M";
        Vector3d localVec = vec3d.subtract(GPSCommand.mc.getRenderManager().info.getProjectedView());
        double x = localVec.getX();
        double z = localVec.getZ();
        double cos = MathHelper.cos((float)((double)GPSCommand.mc.getRenderManager().info.getYaw() * (Math.PI / 180)));
        double sin = MathHelper.sin((float)((double)GPSCommand.mc.getRenderManager().info.getYaw() * (Math.PI / 180)));
        double rotY = -(z * cos - x * sin);
        double rotX = -(x * cos + z * sin);
        float angle = (float)(Math.atan2(rotY, rotX) * 180.0 / Math.PI);
        Crosshair crosshair = Expensive.getInstance().getModuleManager().getCrosshair();
        double x2 = 10.0f * MathHelper.cos((float)Math.toRadians(angle)) + (float)mc.getMainWindow().getScaledWidth() / 2.0f;
        double y2 = 10.0f * MathHelper.sin((float)Math.toRadians(angle)) + (float)mc.getMainWindow().getScaledHeight() / 3.0f;
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.translated(x2, y2, 0.0);
        ClientFonts.msRegular[15].drawCenteredString(e.getMatrixStack(), text, 0.0, -20.0, ColorUtils.setAlpha(-1, 255));
        GlStateManager.rotatef(angle, 0.0f, 0.0f, 1.0f);
        DisplayUtils.drawImage(new ResourceLocation("eva/images/triangle.png"), -4.0f, -8.0f, 16.0f, 16.0f, ColorUtils.setAlpha(-1, 255));
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }
}


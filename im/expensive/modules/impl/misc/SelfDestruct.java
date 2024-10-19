/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.misc;

import im.expensive.Expensive;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.math.StopWatch;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.RandomStringUtils;

@ModuleRegister(name="SelfDestruct", category=Category.Misc)
public class SelfDestruct
extends Module {
    public static boolean unhooked = false;
    public String secret = RandomStringUtils.randomAlphabetic(2);
    public StopWatch stopWatch = new StopWatch();
    public List<Module> saved = new ArrayList<Module>();

    @Override
    public void onEnable() {
        super.onEnable();
        this.process();
        this.print("\u0427\u0442\u043e \u0431\u044b \u0432\u0435\u0440\u043d\u0443\u0442\u044c \u0447\u0438\u0442 \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 \u0432 \u0447\u0430\u0442 " + TextFormatting.RED + this.secret);
        this.print("\u0412\u0441\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u0443\u0434\u0430\u043b\u044f\u0442\u0441\u044f \u0447\u0435\u0440\u0435\u0437 10 \u0441\u0435\u043a\u0443\u043d\u0434");
        this.stopWatch.reset();
        new Thread(() -> {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            SelfDestruct.mc.ingameGUI.getChatGUI().clearChatMessages(false);
            this.toggle();
        }).start();
        unhooked = true;
    }

    public void process() {
        for (Module module : Expensive.getInstance().getModuleManager().getModules()) {
            if (module == this || !module.isState()) continue;
            this.saved.add(module);
            module.setState(false, false);
        }
        File folder = new File(Minecraft.getInstance().gameDir, "\\saves\\files");
        this.hiddenFolder(folder, true);
    }

    public void hook() {
        for (Module module : this.saved) {
            if (module == this || module.isState()) continue;
            module.setState(true, false);
        }
        File folder = new File(Minecraft.getInstance().gameDir, "\\saves\\files");
        this.hiddenFolder(folder, false);
        unhooked = false;
    }

    private void hiddenFolder(File folder, boolean hide) {
        if (folder.exists()) {
            try {
                Path folderPathObj = folder.toPath();
                DosFileAttributeView attributes = Files.getFileAttributeView(folderPathObj, DosFileAttributeView.class, new LinkOption[0]);
                attributes.setHidden(true);
            } catch (IOException e) {
                System.out.println("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443: " + e.getMessage());
            }
        }
    }
}


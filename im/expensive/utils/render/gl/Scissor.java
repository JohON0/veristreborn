/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.render.gl;

import com.google.common.collect.Lists;
import java.awt.Rectangle;
import java.util.List;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class Scissor {
    private static State state = new State();
    private static final List<State> stateStack = Lists.newArrayList();

    public static void push() {
        stateStack.add(state.clone());
        GL11.glPushAttrib(524288);
    }

    public static void pop() {
        state = stateStack.remove(stateStack.size() - 1);
        GL11.glPopAttrib();
    }

    public static void unset() {
        GL11.glDisable(3089);
        Scissor.state.enabled = false;
    }

    public static void setFromComponentCoordinates(int x, int y, int width, int height) {
        MainWindow res = Minecraft.getInstance().getMainWindow();
        int scaleFactor = 2;
        int screenX = x * scaleFactor;
        int screenY = y * scaleFactor;
        int screenWidth = width * scaleFactor;
        int screenHeight = height * scaleFactor;
        screenY = Minecraft.getInstance().getMainWindow().getHeight() - screenY - screenHeight;
        Scissor.set(screenX, screenY, screenWidth, screenHeight);
    }

    public static void setFromComponentCoordinates(double x, double y, double width, double height) {
        MainWindow res = Minecraft.getInstance().getMainWindow();
        int scaleFactor = 2;
        int screenX = (int)(x * (double)scaleFactor);
        int screenY = (int)(y * (double)scaleFactor);
        int screenWidth = (int)(width * (double)scaleFactor);
        int screenHeight = (int)(height * (double)scaleFactor);
        screenY = Minecraft.getInstance().getMainWindow().getHeight() - screenY - screenHeight;
        Scissor.set(screenX, screenY, screenWidth, screenHeight);
    }

    public static void scissor(MainWindow window, double x, double y, double width, double height) {
        if (x + width == x || y + height == y || x < 0.0 || y + height < 0.0) {
            return;
        }
        double scaleFactor = window.getScaleFactor();
        GL11.glScissor((int)Math.round(x * scaleFactor), (int)Math.round(((double)window.getScaledHeight() - (y + height)) * scaleFactor), (int)Math.round(width * scaleFactor), (int)Math.round(height * scaleFactor));
    }

    public static void setFromComponentCoordinates(double x, double y, double width, double height, float scale) {
        MainWindow res = Minecraft.getInstance().getMainWindow();
        float animationValue = scale;
        float halfAnimationValueRest = (1.0f - animationValue) / 2.0f;
        double testX = x + width * (double)halfAnimationValueRest;
        double testY = y + height * (double)halfAnimationValueRest;
        double testW = width * (double)animationValue;
        double testH = height * (double)animationValue;
        testX = testX * (double)animationValue + ((double)Minecraft.getInstance().getMainWindow().getScaledWidth() - testW) * (double)halfAnimationValueRest;
        float scaleFactor = 2.0f;
        int screenX = (int)(testX * (double)scaleFactor);
        int screenY = (int)(testY * (double)scaleFactor);
        int screenWidth = (int)(testW * (double)scaleFactor);
        int screenHeight = (int)(testH * (double)scaleFactor);
        screenY = Minecraft.getInstance().getMainWindow().getHeight() - screenY - screenHeight;
        Scissor.set(screenX, screenY, screenWidth, screenHeight);
    }

    public static void set(int x, int y, int width, int height) {
        Rectangle screen = new Rectangle(0, 0, Minecraft.getInstance().getMainWindow().getWidth(), Minecraft.getInstance().getMainWindow().getHeight());
        Rectangle current = Scissor.state.enabled ? new Rectangle(Scissor.state.x, Scissor.state.y, Scissor.state.width, Scissor.state.height) : screen;
        Rectangle target = new Rectangle(x + Scissor.state.transX, y + Scissor.state.transY, width, height);
        Rectangle result = current.intersection(target);
        result = result.intersection(screen);
        if (result.width < 0) {
            result.width = 0;
        }
        if (result.height < 0) {
            result.height = 0;
        }
        Scissor.state.enabled = true;
        Scissor.state.x = result.x;
        Scissor.state.y = result.y;
        Scissor.state.width = result.width;
        Scissor.state.height = result.height;
        GL11.glEnable(3089);
        GL11.glScissor(result.x, result.y, result.width, result.height);
    }

    public static void translate(int x, int y) {
        Scissor.state.transX = x;
        Scissor.state.transY = y;
    }

    public static void translateFromComponentCoordinates(int x, int y) {
        MainWindow res = Minecraft.getInstance().getMainWindow();
        int totalHeight = res.getScaledHeight();
        int scaleFactor = (int)res.getGuiScaleFactor();
        int screenX = x * scaleFactor;
        int screenY = y * scaleFactor;
        screenY = totalHeight * scaleFactor - screenY;
        Scissor.translate(screenX, screenY);
    }

    private static class State
    implements Cloneable {
        public boolean enabled;
        public int transX;
        public int transY;
        public int x;
        public int y;
        public int width;
        public int height;

        private State() {
        }

        public State clone() {
            try {
                return (State)super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError((Object)e);
            }
        }
    }
}


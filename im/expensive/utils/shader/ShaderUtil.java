/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.shader;

import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.shader.IShader;
import im.expensive.utils.shader.Shaders;
import im.expensive.utils.shader.exception.UndefinedShader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import net.minecraft.client.MainWindow;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderUtil
implements IMinecraft {
    private final int programID = ARBShaderObjects.glCreateProgramObjectARB();
    public static ShaderUtil textShader = new ShaderUtil("textShader");
    public static ShaderUtil rounded = new ShaderUtil("rounded");
    public static ShaderUtil roundedout = new ShaderUtil("roundedout");
    public static ShaderUtil smooth = new ShaderUtil("smooth");
    public static ShaderUtil white = new ShaderUtil("white");
    public static ShaderUtil alpha = new ShaderUtil("alpha");
    public static ShaderUtil kawaseUp = new ShaderUtil("kawaseUp");
    public static ShaderUtil kawaseDown = new ShaderUtil("kawaseDown");
    public static ShaderUtil outline = new ShaderUtil("outline");
    public static ShaderUtil contrast = new ShaderUtil("contrast");
    public static ShaderUtil mask = new ShaderUtil("mask");
    public static ShaderUtil MainMenuShader = new ShaderUtil("MainMenuShader");
    public static ShaderUtil gradient = new ShaderUtil("gradient");

    public ShaderUtil(String fragmentShaderLoc) {
        try {
            int fragmentShaderID = switch (fragmentShaderLoc) {
                case "textShader" -> this.createShader(Shaders.getInstance().getFont(), 35632);
                case "smooth" -> this.createShader(Shaders.getInstance().getSmooth(), 35632);
                case "white" -> this.createShader(Shaders.getInstance().getWhite(), 35632);
                case "rounded" -> this.createShader(Shaders.getInstance().getRounded(), 35632);
                case "roundedout" -> this.createShader(Shaders.getInstance().getRoundedout(), 35632);
                case "bloom" -> this.createShader(Shaders.getInstance().getGaussianbloom(), 35632);
                case "kawaseUp" -> this.createShader(Shaders.getInstance().getKawaseUp(), 35632);
                case "kawaseDown" -> this.createShader(Shaders.getInstance().getKawaseDown(), 35632);
                case "alpha" -> this.createShader(Shaders.getInstance().getAlpha(), 35632);
                case "outline" -> this.createShader(Shaders.getInstance().getOutline(), 35632);
                case "contrast" -> this.createShader(Shaders.getInstance().getContrast(), 35632);
                case "mask" -> this.createShader(Shaders.getInstance().getMask(), 35632);
                case "MainMenuShader" -> this.createShader(Shaders.getInstance().getMainMenuShader(), 35632);
                case "gradient" -> this.createShader(Shaders.getInstance().getGradient(), 35632);
                default -> throw new UndefinedShader(fragmentShaderLoc);
            };
            ARBShaderObjects.glAttachObjectARB(this.programID, fragmentShaderID);
            ARBShaderObjects.glAttachObjectARB(this.programID, this.createShader(Shaders.getInstance().getVertex(), 35633));
            ARBShaderObjects.glLinkProgramARB(this.programID);
        } catch (UndefinedShader exception) {
            exception.fillInStackTrace();
            System.out.println("\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u0437\u0430\u0433\u0440\u0443\u0437\u043a\u0435: " + fragmentShaderLoc);
        }
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return ShaderUtil.createFrameBuffer(framebuffer, false);
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.getMainWindow().getWidth() || framebuffer.framebufferHeight != mc.getMainWindow().getHeight();
    }

    public int getUniform(String name) {
        return ARBShaderObjects.glGetUniformLocationARB(this.programID, name);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (ShaderUtil.needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), depth, false);
        }
        return framebuffer;
    }

    public void drawQuads(float x, float y, float width, float height) {
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();
    }

    public static void drawQuads() {
        MainWindow sr = mc.getMainWindow();
        float width = sr.getScaledWidth();
        float height = sr.getScaledHeight();
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(0.0f, height);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(width, height);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f(width, 0.0f);
        GL11.glEnd();
    }

    public Framebuffer setupBuffer(Framebuffer frameBuffer) {
        if (frameBuffer.framebufferWidth != mc.getMainWindow().getWidth() || frameBuffer.framebufferHeight != mc.getMainWindow().getHeight()) {
            frameBuffer.resize(Math.max(1, mc.getMainWindow().getWidth()), Math.max(1, mc.getMainWindow().getHeight()), false);
        } else {
            frameBuffer.framebufferClear(false);
        }
        frameBuffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
        return frameBuffer;
    }

    public void attach() {
        ARBShaderObjects.glUseProgramObjectARB(this.programID);
    }

    public void detach() {
        GL20.glUseProgram(0);
    }

    public void setUniform(String name, float ... args2) {
        int loc = ARBShaderObjects.glGetUniformLocationARB(this.programID, name);
        switch (args2.length) {
            case 1: {
                ARBShaderObjects.glUniform1fARB(loc, args2[0]);
                break;
            }
            case 2: {
                ARBShaderObjects.glUniform2fARB(loc, args2[0], args2[1]);
                break;
            }
            case 3: {
                ARBShaderObjects.glUniform3fARB(loc, args2[0], args2[1], args2[2]);
                break;
            }
            case 4: {
                ARBShaderObjects.glUniform4fARB(loc, args2[0], args2[1], args2[2], args2[3]);
                break;
            }
            default: {
                throw new IllegalArgumentException("\u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u043e\u0435 \u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432 \u0434\u043b\u044f uniform '" + name + "'");
            }
        }
    }

    public void setUniform(String name, int ... args2) {
        int loc = ARBShaderObjects.glGetUniformLocationARB(this.programID, name);
        switch (args2.length) {
            case 1: {
                ARBShaderObjects.glUniform1iARB(loc, args2[0]);
                break;
            }
            case 2: {
                ARBShaderObjects.glUniform2iARB(loc, args2[0], args2[1]);
                break;
            }
            case 3: {
                ARBShaderObjects.glUniform3iARB(loc, args2[0], args2[1], args2[2]);
                break;
            }
            case 4: {
                ARBShaderObjects.glUniform4iARB(loc, args2[0], args2[1], args2[2], args2[3]);
                break;
            }
            default: {
                throw new IllegalArgumentException("\u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u043e\u0435 \u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432 \u0434\u043b\u044f uniform '" + name + "'");
            }
        }
    }

    public void setUniformf(String var1, float ... var2) {
        int var3 = ARBShaderObjects.glGetUniformLocationARB(this.programID, var1);
        switch (var2.length) {
            case 1: {
                ARBShaderObjects.glUniform1fARB(var3, var2[0]);
                break;
            }
            case 2: {
                ARBShaderObjects.glUniform2fARB(var3, var2[0], var2[1]);
                break;
            }
            case 3: {
                ARBShaderObjects.glUniform3fARB(var3, var2[0], var2[1], var2[2]);
                break;
            }
            case 4: {
                ARBShaderObjects.glUniform4fARB(var3, var2[0], var2[1], var2[2], var2[3]);
            }
        }
    }

    public void setUniformf1(String name, float ... args2) {
        int loc = GL20.glGetUniformLocation(this.programID, name);
        switch (args2.length) {
            case 1: {
                GL20.glUniform1f(loc, args2[0]);
                break;
            }
            case 2: {
                GL20.glUniform2f(loc, args2[0], args2[1]);
                break;
            }
            case 3: {
                GL20.glUniform3f(loc, args2[0], args2[1], args2[2]);
                break;
            }
            case 4: {
                GL20.glUniform4f(loc, args2[0], args2[1], args2[2], args2[3]);
            }
        }
    }

    public void setUniformi(String name, int ... args2) {
        int loc = GL20.glGetUniformLocation(this.programID, name);
        switch (args2.length) {
            case 1: {
                GL20.glUniform1i(loc, args2[0]);
                break;
            }
            case 2: {
                GL20.glUniform2i(loc, args2[0], args2[1]);
                break;
            }
            case 3: {
                GL20.glUniform3i(loc, args2[0], args2[1], args2[2]);
                break;
            }
            case 4: {
                GL20.glUniform4i(loc, args2[0], args2[1], args2[2], args2[3]);
            }
        }
    }

    public void setUniformf(String var1, double ... var2) {
        int var3 = ARBShaderObjects.glGetUniformLocationARB(this.programID, var1);
        switch (var2.length) {
            case 1: {
                ARBShaderObjects.glUniform1fARB(var3, (float)var2[0]);
                break;
            }
            case 2: {
                ARBShaderObjects.glUniform2fARB(var3, (float)var2[0], (float)var2[1]);
                break;
            }
            case 3: {
                ARBShaderObjects.glUniform3fARB(var3, (float)var2[0], (float)var2[1], (float)var2[2]);
                break;
            }
            case 4: {
                ARBShaderObjects.glUniform4fARB(var3, (float)var2[0], (float)var2[1], (float)var2[2], (float)var2[3]);
            }
        }
    }

    private int createShader(IShader glsl, int shaderType) {
        int shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
        ARBShaderObjects.glShaderSourceARB(shader, (CharSequence)this.readInputStream(new ByteArrayInputStream(glsl.glsl().getBytes())));
        ARBShaderObjects.glCompileShaderARB(shader);
        if (GL20.glGetShaderi(shader, 35713) == 0) {
            System.out.println(GL20.glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }
        return shader;
    }

    public String readInputStream(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream)).lines().map(line -> line + "\n").collect(Collectors.joining());
    }
}


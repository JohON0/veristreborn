/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.render;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.IRenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.shader.ShaderUtil;
import java.nio.FloatBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

public class GaussianBloom
implements IMinecraft {
    public static GaussianBloom INGAME = new GaussianBloom();
    public static GaussianBloom GUI = new GaussianBloom();
    private final ShaderUtil bloom = new ShaderUtil("bloom");
    private final ConcurrentLinkedQueue<IRenderCall> renderQueue = Queues.newConcurrentLinkedQueue();
    private final Framebuffer inFrameBuffer = new Framebuffer(1, 1, true, false);
    private final Framebuffer outFrameBuffer = new Framebuffer(1, 1, true, false);

    public void registerRenderCall(IRenderCall rc) {
        this.renderQueue.add(rc);
    }

    public void draw(int radius, float exp, boolean fill, float direction) {
        if (this.renderQueue.isEmpty()) {
            return;
        }
        this.setupBuffer(this.inFrameBuffer);
        this.setupBuffer(this.outFrameBuffer);
        this.inFrameBuffer.bindFramebuffer(true);
        while (!this.renderQueue.isEmpty()) {
            this.renderQueue.poll().execute();
        }
        this.inFrameBuffer.unbindFramebuffer();
        this.outFrameBuffer.bindFramebuffer(true);
        this.bloom.attach();
        this.bloom.setUniformf("radius", new float[]{radius});
        this.bloom.setUniformf("exposure", exp);
        this.bloom.setUniform("textureIn", 0);
        this.bloom.setUniform("textureToCheck", 20);
        this.bloom.setUniform("avoidTexture", fill ? 1 : 0);
        FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(128);
        for (int i = 0; i <= radius; ++i) {
            weightBuffer.put(this.calculateGaussianValue(i, radius / 2));
        }
        weightBuffer.rewind();
        RenderSystem.glUniform1(this.bloom.getUniform("weights"), weightBuffer);
        this.bloom.setUniformf("texelSize", 1.0f / (float)Minecraft.getInstance().getMainWindow().getWidth(), 1.0f / (float)Minecraft.getInstance().getMainWindow().getHeight());
        this.bloom.setUniformf("direction", direction, 0.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(1, 770);
        GL30.glAlphaFunc(516, 1.0E-4f);
        this.inFrameBuffer.bindFramebufferTexture();
        ShaderUtil.drawQuads();
        mc.getFramebuffer().bindFramebuffer(false);
        GlStateManager.blendFunc(770, 771);
        this.bloom.setUniformf("direction", 0.0f, direction);
        this.outFrameBuffer.bindFramebufferTexture();
        GL30.glActiveTexture(34004);
        this.inFrameBuffer.bindFramebufferTexture();
        GL30.glActiveTexture(33984);
        ShaderUtil.drawQuads();
        this.bloom.detach();
        this.outFrameBuffer.unbindFramebuffer();
        GlStateManager.bindTexture(0);
        GlStateManager.disableBlend();
        mc.getFramebuffer().bindFramebuffer(false);
    }

    private Framebuffer setupBuffer(Framebuffer frameBuffer) {
        if (frameBuffer.framebufferWidth != mc.getMainWindow().getWidth() || frameBuffer.framebufferHeight != mc.getMainWindow().getHeight()) {
            frameBuffer.resize(Math.max(1, mc.getMainWindow().getWidth()), Math.max(1, mc.getMainWindow().getHeight()), false);
        } else {
            frameBuffer.framebufferClear(false);
        }
        frameBuffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
        return frameBuffer;
    }

    private float calculateGaussianValue(float x, float sigma) {
        double PI2 = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI2 * (double)(sigma * sigma));
        return (float)(output * Math.exp((double)(-(x * x)) / (2.0 * (double)(sigma * sigma))));
    }
}


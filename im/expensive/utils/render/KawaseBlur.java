/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.render;

import im.expensive.utils.render.CustomFramebuffer;
import im.expensive.utils.render.gl.Stencil;
import im.expensive.utils.shader.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

public class KawaseBlur {
    public static KawaseBlur blur = new KawaseBlur();
    public final CustomFramebuffer BLURRED;
    public final CustomFramebuffer ADDITIONAL;
    CustomFramebuffer blurTarget = new CustomFramebuffer(false).setLinear();

    public KawaseBlur() {
        this.BLURRED = new CustomFramebuffer(false).setLinear();
        this.ADDITIONAL = new CustomFramebuffer(false).setLinear();
    }

    public void render(Runnable run) {
        Stencil.initStencilToWrite();
        run.run();
        Stencil.readStencilBuffer(1);
        this.BLURRED.draw();
        Stencil.uninitStencilBuffer();
    }

    public void updateBlur(float offset, int steps) {
        int step;
        int i;
        Minecraft mc = Minecraft.getInstance();
        Framebuffer mcFramebuffer = mc.getFramebuffer();
        this.ADDITIONAL.setup();
        mcFramebuffer.bindFramebufferTexture();
        ShaderUtil.kawaseDown.attach();
        ShaderUtil.kawaseDown.setUniform("offset", offset);
        ShaderUtil.kawaseDown.setUniformf("resolution", 1.0f / (float)mc.getMainWindow().getWidth(), 1.0f / (float)mc.getMainWindow().getHeight());
        CustomFramebuffer.drawTexture();
        CustomFramebuffer[] buffers = new CustomFramebuffer[]{this.ADDITIONAL, this.BLURRED};
        for (i = 1; i < steps; ++i) {
            step = i % 2;
            buffers[step].setup();
            buffers[(step + 1) % 2].draw();
        }
        ShaderUtil.kawaseUp.attach();
        ShaderUtil.kawaseUp.setUniform("offset", offset);
        ShaderUtil.kawaseUp.setUniformf("resolution", 1.0f / (float)mc.getMainWindow().getWidth(), 1.0f / (float)mc.getMainWindow().getHeight());
        for (i = 0; i < steps; ++i) {
            step = i % 2;
            buffers[(step + 1) % 2].setup();
            buffers[step].draw();
        }
        ShaderUtil.kawaseUp.detach();
        mcFramebuffer.bindFramebuffer(false);
    }
}


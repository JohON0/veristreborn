/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.text.font;

import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.image.BufferedImage;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

public final class TextureHelper {
    public static int loadTexture(BufferedImage image) {
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length * 4);
        try {
            for (int pixel : pixels) {
                buffer.put((byte)(pixel >> 16 & 0xFF));
                buffer.put((byte)(pixel >> 8 & 0xFF));
                buffer.put((byte)(pixel & 0xFF));
                buffer.put((byte)(pixel >> 24 & 0xFF));
            }
            buffer.flip();
        } catch (BufferOverflowException | ReadOnlyBufferException ex) {
            return -1;
        }
        int textureID = GlStateManager.genTexture();
        GlStateManager.bindTexture(textureID);
        GlStateManager.texParameter(3553, 10241, 9729);
        GlStateManager.texParameter(3553, 10240, 9729);
        GL30.glTexImage2D(3553, 0, 32856, image.getWidth(), image.getHeight(), 0, 6408, 5121, buffer);
        GlStateManager.bindTexture(0);
        return textureID;
    }
}


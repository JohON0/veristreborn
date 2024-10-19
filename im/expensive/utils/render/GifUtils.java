/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.render;

public class GifUtils {
    public int getFrame(int totalFrames, int frameDelay, boolean countFromZero) {
        long currentTime = System.currentTimeMillis();
        int i = (int)(currentTime / (long)frameDelay % (long)totalFrames) + (countFromZero ? 0 : 1);
        return i;
    }
}


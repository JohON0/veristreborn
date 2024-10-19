/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.notify;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.utils.animations.easing.CompactAnimation;
import im.expensive.utils.animations.easing.Easing;
import im.expensive.utils.client.IMinecraft;

public abstract class Notify
implements IMinecraft {
    public final String content;
    public final long init = System.currentTimeMillis();
    public final long delay;
    public final CompactAnimation alphaAnimation = new CompactAnimation(Easing.EASE_OUT_QUAD, 250L);
    public final CompactAnimation animationY = new CompactAnimation(Easing.EASE_OUT_EXPO, 250L);
    public final CompactAnimation animationX = new CompactAnimation(Easing.EASE_OUT_EXPO, 250L);
    public final CompactAnimation chatOffset = new CompactAnimation(Easing.EASE_OUT_QUAD, 50L);
    public static boolean end;
    public float margin = 4.0f;

    public abstract void render(MatrixStack var1, int var2);

    public abstract boolean hasExpired();

    public String getContent() {
        return this.content;
    }

    public long getInit() {
        return this.init;
    }

    public long getDelay() {
        return this.delay;
    }

    public CompactAnimation getAlphaAnimation() {
        return this.alphaAnimation;
    }

    public CompactAnimation getAnimationY() {
        return this.animationY;
    }

    public CompactAnimation getAnimationX() {
        return this.animationX;
    }

    public CompactAnimation getChatOffset() {
        return this.chatOffset;
    }

    public float getMargin() {
        return this.margin;
    }

    public Notify(String content, long delay) {
        this.content = content;
        this.delay = delay;
    }
}


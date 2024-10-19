/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.oldclickgui.impl;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IBuilder {
    default public void render(MatrixStack stack, float mouseX, float mouseY) {
    }

    default public void mouseClick(float mouseX, float mouseY, int mouse) {
    }

    default public void charTyped(char codePoint, int modifiers) {
    }

    default public void mouseRelease(float mouseX, float mouseY, int mouse) {
    }

    default public void keyPressed(int key, int scanCode, int modifiers) {
    }
}


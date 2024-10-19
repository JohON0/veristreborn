/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.text.font.common;

public enum Lang {
    ENG(new int[]{31, 127, 0, 0}),
    ENG_RU(new int[]{31, 127, 1024, 1106});

    private int[] charCodes;

    private Lang(int[] charCodes) {
        this.charCodes = charCodes;
    }

    public int[] getCharCodes() {
        return this.charCodes;
    }
}


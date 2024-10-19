/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.text;

import java.util.List;

public class BetterText {
    private List<String> texts;
    public final StringBuilder output = new StringBuilder();
    private int delay;
    private int textIndex = 0;
    private int charIndex = 0;
    private boolean forward = true;
    private long lastUpdateTime = System.currentTimeMillis();

    public BetterText(List<String> texts, int delay) {
        this.texts = texts;
        this.delay = delay;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastUpdateTime >= 100L) {
            this.lastUpdateTime = currentTime;
            if (this.forward) {
                if (this.charIndex < this.texts.get(this.textIndex).length()) {
                    this.output.append(this.texts.get(this.textIndex).charAt(this.charIndex));
                    ++this.charIndex;
                } else {
                    this.forward = false;
                    this.lastUpdateTime = currentTime + (long)this.delay;
                }
            } else if (this.charIndex > 0) {
                this.output.deleteCharAt(this.charIndex - 1);
                --this.charIndex;
            } else {
                this.forward = true;
                this.textIndex = (this.textIndex + 1) % this.texts.size();
            }
        }
    }

    public static String replaceSymbols(String string) {
        return string.replaceAll("\u26a1", "").replaceAll("\u1d00", "a").replaceAll("\u0299", "b").replaceAll("\u1d04", "c").replaceAll("\u1d05", "d").replaceAll("\u1d07", "e").replaceAll("\u0493", "f").replaceAll("\u0262", "g").replaceAll("\u029c", "h").replaceAll("\u026a", "i").replaceAll("\u1d0a", "j").replaceAll("\u1d0b", "k").replaceAll("\u029f", "l").replaceAll("\u1d0d", "m").replaceAll("\u0274", "n").replaceAll("\u1d0f", "o").replaceAll("\u1d18", "p").replaceAll("\u01eb", "q").replaceAll("\u0280", "r").replaceAll("s", "s").replaceAll("\u1d1b", "t").replaceAll("\u1d1c", "u").replaceAll("\u1d20", "v").replaceAll("\u1d21", "w").replaceAll("x", "x").replaceAll("\u028f", "y").replaceAll("\u1d22", "z");
    }

    public List<String> getTexts() {
        return this.texts;
    }

    public StringBuilder getOutput() {
        return this.output;
    }

    public int getDelay() {
        return this.delay;
    }

    public int getTextIndex() {
        return this.textIndex;
    }

    public int getCharIndex() {
        return this.charIndex;
    }

    public boolean isForward() {
        return this.forward;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setTextIndex(int textIndex) {
        this.textIndex = textIndex;
    }

    public void setCharIndex(int charIndex) {
        this.charIndex = charIndex;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BetterText)) {
            return false;
        }
        BetterText other = (BetterText)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getDelay() != other.getDelay()) {
            return false;
        }
        if (this.getTextIndex() != other.getTextIndex()) {
            return false;
        }
        if (this.getCharIndex() != other.getCharIndex()) {
            return false;
        }
        if (this.isForward() != other.isForward()) {
            return false;
        }
        if (this.getLastUpdateTime() != other.getLastUpdateTime()) {
            return false;
        }
        List<String> this$texts = this.getTexts();
        List<String> other$texts = other.getTexts();
        if (this$texts == null ? other$texts != null : !((Object)this$texts).equals(other$texts)) {
            return false;
        }
        StringBuilder this$output = this.getOutput();
        StringBuilder other$output = other.getOutput();
        return !(this$output == null ? other$output != null : !this$output.equals(other$output));
    }

    protected boolean canEqual(Object other) {
        return other instanceof BetterText;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getDelay();
        result = result * 59 + this.getTextIndex();
        result = result * 59 + this.getCharIndex();
        result = result * 59 + (this.isForward() ? 79 : 97);
        long $lastUpdateTime = this.getLastUpdateTime();
        result = result * 59 + (int)($lastUpdateTime >>> 32 ^ $lastUpdateTime);
        List<String> $texts = this.getTexts();
        result = result * 59 + ($texts == null ? 43 : ((Object)$texts).hashCode());
        StringBuilder $output = this.getOutput();
        result = result * 59 + ($output == null ? 43 : $output.hashCode());
        return result;
    }

    public String toString() {
        return "BetterText(texts=" + this.getTexts() + ", output=" + this.getOutput() + ", delay=" + this.getDelay() + ", textIndex=" + this.getTextIndex() + ", charIndex=" + this.getCharIndex() + ", forward=" + this.isForward() + ", lastUpdateTime=" + this.getLastUpdateTime() + ")";
    }
}


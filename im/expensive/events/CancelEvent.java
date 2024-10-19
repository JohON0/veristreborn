/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

public class CancelEvent {
    private boolean isCancel;

    public void cancel() {
        this.isCancel = true;
    }

    public void open() {
        this.isCancel = false;
    }

    public boolean isCancel() {
        return this.isCancel;
    }
}


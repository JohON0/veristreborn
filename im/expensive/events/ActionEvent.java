/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

public class ActionEvent {
    private boolean sprintState;

    public ActionEvent(boolean sprintState) {
        this.sprintState = sprintState;
    }

    public boolean isSprintState() {
        return this.sprintState;
    }

    public void setSprintState(boolean sprintState) {
        this.sprintState = sprintState;
    }
}


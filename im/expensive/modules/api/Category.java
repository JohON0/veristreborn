/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.api;

public enum Category {
    Combat("Combat", "E"),
    Movement("Movement", "D"),
    Render("Render", "F"),
    Misc("Misc", "C");

    private final String name;
    private final String icon;

    public String getName() {
        return this.name;
    }

    public String getIcon() {
        return this.icon;
    }

    private Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.shader.exception;

public class UndefinedShader
extends Throwable {
    private final String shader;

    @Override
    public String getMessage() {
        return this.shader;
    }

    public UndefinedShader(String shader) {
        this.shader = shader;
    }
}


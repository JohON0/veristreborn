/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.shader;

public interface IShader {
    public String glsl();

    default public String getName() {
        return "SHADERNONAME";
    }
}


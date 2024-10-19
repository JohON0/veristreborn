/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.shader.shaders;

import im.expensive.utils.shader.IShader;

public class MainMenu2Glsl
implements IShader {
    @Override
    public String glsl() {
        return "#extension GL_OES_standard_derivatives : enable\n\n precision mediump float;\n uniform float time;\n uniform vec2 mouse;\n uniform float width;\n uniform float height;\n uniform float mousex;\n uniform float mousey;\n\n void main(void) {\n     vec2 m = vec2(mousex * 2.0 - 1.0, mousey * 2.0 - 1.0);\n\n     vec2 p = (gl_FragCoord.xy * 2.0 - vec2(width, height)) / min(width, height);\n\n     float lambda = time * 2.5;\n\n     float t = 0.02 / abs(tan(lambda) - length(p));\n     float t2 = atan(p.y, p.x) + time;\n\n     vec2 something = vec2(1.0, (sin(time) + 1.0) * 0.5);\n\n     float dotProduct = dot(vec2(t), something) / length(p);\n\n     gl_FragColor = vec4(vec3(dotProduct), 1.0);\n }\n";
    }
}


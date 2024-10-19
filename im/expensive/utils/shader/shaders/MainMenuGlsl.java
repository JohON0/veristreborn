/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.shader.shaders;

import im.expensive.utils.shader.IShader;

public class MainMenuGlsl
implements IShader {
    @Override
    public String glsl() {
        return " #ifdef GL_ES\n    precision mediump float;\n    #endif\n\n    #extension GL_OES_standard_derivatives : enable\n\n    uniform float width;  // \u0428\u0438\u0440\u0438\u043d\u0430\n    uniform float height; // \u0412\u044b\u0441\u043e\u0442\u0430\n    uniform float time;\n\n    /*\n    * @author Yanchenko.class (kinda)\n    */\n    mat2 m(float a) {\n        float c = cos(a), s = sin(a);\n        return mat2(c, -s, s, c);\n    }\n\n    float map(vec3 p) {\n        p.xz *= m(time * 0.4); \n        p.xy *= m(time * 0.1);\n        vec3 q = p * 2.0 + time;\n        return length(p + vec3(sin(time * 0.7))) * log(length(p) + 1.0) + sin(q.x + sin(q.z + sin(q.y))) * 0.5 - 1.0;\n    }\n\n    void main() {\n        vec2 a = gl_FragCoord.xy / height - vec2(0.9, 0.5); // \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0435\u043c height \u0432\u043c\u0435\u0441\u0442\u043e height\n        vec3 cl = vec3(0.0);\n        float d = 2.5;\n\n        for (int i = 0; i <= 5; i++) {\n            vec3 p = vec3(0, 0, 4.0) + normalize(vec3(a, -1.0)) * d;\n            float rz = map(p);\n            float f = clamp((rz - map(p + 0.1)) * 0.5, -0.1, 1.0);\n            vec3 l = vec3(0.1, 0.3, 0.4) + vec3(5.0, 2.5, 3.0) * f;\n            cl = cl * l + smoothstep(2.5, 0.0, rz) * 0.6 * l;\n            d += min(rz, 1.0);\n        }\n\n        gl_FragColor = vec4(cl, 1.0);\n    }\n";
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.shader.shaders;

import im.expensive.utils.shader.IShader;

public class RoundedOutGlsl
implements IShader {
    @Override
    public String glsl() {
        return "#version 120\n// \u043e\u0431\u044a\u044f\u0432\u043b\u0435\u043d\u0438\u0435 \u043f\u0435\u0440\u0435\u043c\u0435\u043d\u043d\u044b\u0445\nuniform vec2 size; // \u0440\u0430\u0437\u043c\u0435\u0440 \u043f\u0440\u044f\u043c\u043e\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a\u0430\nuniform vec4 round; // \u043a\u043e\u044d\u0444\u0444\u0438\u0446\u0438\u0435\u043d\u0442\u044b \u0441\u043a\u0440\u0443\u0433\u043b\u0435\u043d\u0438\u044f \u0443\u0433\u043b\u043e\u0432\nuniform vec2 smoothness; // \u043f\u043b\u0430\u0432\u043d\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0435\u0445\u043e\u0434\u0430 \u043e\u0442 \u0446\u0432\u0435\u0442\u0430 \u043a \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u0438\nuniform float value; // \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435, \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0435\u043c\u043e\u0435 \u0434\u043b\u044f \u0440\u0430\u0441\u0447\u0435\u0442\u0430 \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u044f \u0434\u043e \u0433\u0440\u0430\u043d\u0438\u0446\u044b\nuniform vec4 color; // \u0446\u0432\u0435\u0442 \u043f\u0440\u044f\u043c\u043e\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a\u0430\nuniform vec4 outlineColor; // \u0446\u0432\u0435\u0442 \u043e\u0431\u0432\u043e\u0434\u043a\u0438\nuniform vec4 outlineColor1; // \u0446\u0432\u0435\u0442 \u043e\u0431\u0432\u043e\u0434\u043a\u0438\nuniform vec4 outlineColor2; // \u0446\u0432\u0435\u0442 \u043e\u0431\u0432\u043e\u0434\u043a\u0438\nuniform vec4 outlineColor3; // \u0446\u0432\u0435\u0442 \u043e\u0431\u0432\u043e\u0434\u043a\u0438\nuniform float outline; // \u0446\u0432\u0435\u0442 \u043e\u0431\u0432\u043e\u0434\u043a\u0438\n#define NOISE .5/255.0\n// \u0444\u0443\u043d\u043a\u0446\u0438\u044f \u0434\u043b\u044f \u0440\u0430\u0441\u0447\u0435\u0442\u0430 \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u044f \u0434\u043e \u0433\u0440\u0430\u043d\u0438\u0446\u044b\nfloat test(vec2 vec_1, vec2 vec_2, vec4 vec_4) {\n    vec_4.xy = (vec_1.x > 0.0) ? vec_4.xy : vec_4.zw;\n    vec_4.x = (vec_1.y > 0.0) ? vec_4.x : vec_4.y;\n    vec2 coords = abs(vec_1) - vec_2 + vec_4.x;\n    return min(max(coords.x, coords.y), 0.0) + length(max(coords, vec2(0.0f))) - vec_4.x;\n}\n\nvec4 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4) {\n    vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);\n    //Dithering the color\n    // from https://shader-tutorial.dev/advanced/color-banding-dithering/\n    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));\n    return color;\n}\n\nvoid main() {\n    vec2 st = gl_TexCoord[0].st * size; // \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b \u0442\u0435\u043a\u0443\u0449\u0435\u0433\u043e \u043f\u0438\u043a\u0441\u0435\u043b\u044f\n    vec2 halfSize = 0.5 * size; // \u043f\u043e\u043b\u043e\u0432\u0438\u043d\u0430 \u0440\u0430\u0437\u043c\u0435\u0440\u0430 \u043f\u0440\u044f\u043c\u043e\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a\u0430\n    float sa = 1.0 - smoothstep(smoothness.x, smoothness.y, test(halfSize - st, halfSize - value, round));\n    float outline = 1.0 - smoothstep(smoothness.x, smoothness.y, test(halfSize - st, halfSize - value - outline, round));\n    float outlin1 = 1.0 - smoothstep(smoothness.x, smoothness.y, test(halfSize - st, halfSize - value - 1, round));\n\n    // \u0440\u0430\u0441\u0441\u0447\u0438\u0442\u044b\u0432\u0430\u0435\u043c \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0432 \u0437\u0430\u0432\u0438\u0441\u0438\u043c\u043e\u0441\u0442\u0438 \u043e\u0442 \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u044f \u0434\u043e \u0433\u0440\u0430\u043d\u0438\u0446\u044b\n    vec4 finalColor = mix(vec4(color.rgb, 0.0), vec4(color.rgb, color.a), sa); // \u0443\u0441\u0442\u0430\u043d\u0430\u0432\u043b\u0438\u0432\u0430\u0435\u043c \u0446\u0432\u0435\u0442 \u043f\u0440\u044f\u043c\u043e\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a\u0430 \u0441 \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c\u044e sa\n\n    // \u0435\u0441\u043b\u0438 sa \u0438 outline \u0440\u0430\u0432\u043d\u044b, \u0442\u043e \u044d\u0442\u043e \u043e\u0431\u0432\u043e\u0434\u043a\u0430\n    if (sa > outline) {\n        vec4 color = createGradient(gl_TexCoord[0].st, outlineColor, outlineColor1,outlineColor2,outlineColor3);\n        finalColor = vec4(color.r,color.g,color.b,outlin1); // \u043f\u0440\u0438\u0441\u0432\u0430\u0438\u0432\u0430\u0435\u043c \u043e\u0431\u0432\u043e\u0434\u043a\u0435 \u0435\u0435 \u0446\u0432\u0435\u0442\n    }\n\n    gl_FragColor = finalColor;\n}\n";
    }
}


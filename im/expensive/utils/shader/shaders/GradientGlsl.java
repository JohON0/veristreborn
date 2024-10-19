/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.shader.shaders;

import im.expensive.utils.shader.IShader;

public class GradientGlsl
implements IShader {
    @Override
    public String glsl() {
        return "#version 120\n\nuniform vec2 location, rectSize;\nuniform sampler2D tex;\nuniform vec4 color1, color2, color3, color4;\n\n#define NOISE .5/255.0\n\nvec3 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){\n    vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);\n    //Dithering the color from https://shader-tutorial.dev/advanced/color-banding-dithering/\n    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898,78.233))) * 43758.5453));\n    return color;\n}\nvoid main() {\n    vec2 coords = (gl_FragCoord.xy - location) / rectSize;\n    float texColorAlpha = texture2D(tex, gl_TexCoord[0].st).a;\n    gl_FragColor = vec4(createGradient(coords, color1, color2, color3, color4).rgb, texColorAlpha);\n}\n";
    }
}


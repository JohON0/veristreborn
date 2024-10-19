/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.shader;

import im.expensive.utils.shader.IShader;
import im.expensive.utils.shader.shaders.AlphaGlsl;
import im.expensive.utils.shader.shaders.ContrastGlsl;
import im.expensive.utils.shader.shaders.FontGlsl;
import im.expensive.utils.shader.shaders.GaussianBloomGlsl;
import im.expensive.utils.shader.shaders.GradientGlsl;
import im.expensive.utils.shader.shaders.KawaseDownGlsl;
import im.expensive.utils.shader.shaders.KawaseUpGlsl;
import im.expensive.utils.shader.shaders.MainMenu2Glsl;
import im.expensive.utils.shader.shaders.MainMenuGlsl;
import im.expensive.utils.shader.shaders.MaskGlsl;
import im.expensive.utils.shader.shaders.OutlineGlsl;
import im.expensive.utils.shader.shaders.RoundedGlsl;
import im.expensive.utils.shader.shaders.RoundedOutGlsl;
import im.expensive.utils.shader.shaders.SmoothGlsl;
import im.expensive.utils.shader.shaders.VertexGlsl;
import im.expensive.utils.shader.shaders.WhiteGlsl;

public class Shaders {
    private static Shaders Instance = new Shaders();
    private IShader font = new FontGlsl();
    private IShader vertex = new VertexGlsl();
    private IShader rounded = new RoundedGlsl();
    private IShader roundedout = new RoundedOutGlsl();
    private IShader smooth = new SmoothGlsl();
    private IShader white = new WhiteGlsl();
    private IShader alpha = new AlphaGlsl();
    private IShader gaussianbloom = new GaussianBloomGlsl();
    private IShader kawaseUp = new KawaseUpGlsl();
    private IShader kawaseDown = new KawaseDownGlsl();
    private IShader outline = new OutlineGlsl();
    private IShader contrast = new ContrastGlsl();
    private IShader mask = new MaskGlsl();
    private IShader MainMenuShader = new MainMenuGlsl();
    private IShader MainMenu2Shader = new MainMenu2Glsl();
    private IShader gradient = new GradientGlsl();

    public static Shaders getInstance() {
        return Instance;
    }

    public IShader getFont() {
        return this.font;
    }

    public IShader getVertex() {
        return this.vertex;
    }

    public IShader getRounded() {
        return this.rounded;
    }

    public IShader getRoundedout() {
        return this.roundedout;
    }

    public IShader getSmooth() {
        return this.smooth;
    }

    public IShader getWhite() {
        return this.white;
    }

    public IShader getAlpha() {
        return this.alpha;
    }

    public IShader getGaussianbloom() {
        return this.gaussianbloom;
    }

    public IShader getKawaseUp() {
        return this.kawaseUp;
    }

    public IShader getKawaseDown() {
        return this.kawaseDown;
    }

    public IShader getOutline() {
        return this.outline;
    }

    public IShader getContrast() {
        return this.contrast;
    }

    public IShader getMask() {
        return this.mask;
    }

    public IShader getMainMenuShader() {
        return this.MainMenuShader;
    }

    public IShader getMainMenu2Shader() {
        return this.MainMenu2Shader;
    }

    public IShader getGradient() {
        return this.gradient;
    }
}


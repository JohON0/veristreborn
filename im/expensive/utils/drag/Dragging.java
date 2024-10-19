/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.drag;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import im.expensive.modules.api.Module;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.client.Vec2i;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.rect.DisplayUtils;
import net.minecraft.client.MainWindow;

public class Dragging {
    @Expose
    @SerializedName(value="x")
    private float xPos;
    @Expose
    @SerializedName(value="y")
    private float yPos;
    public float initialXVal;
    public float initialYVal;
    private float startX;
    private float startY;
    private boolean dragging;
    private float closestVerticalLine = 0.0f;
    private float closestHorizontalLine = 0.0f;
    private static final float grid = 2.0f;
    private static final float snap_thr = 10.0f;
    private float width;
    private float height;
    boolean showVerticalLine;
    boolean showHorizontalLine;
    @Expose
    @SerializedName(value="name")
    private final String name;
    private final Module module;
    private float lineAlpha = 0.0f;
    private long lastUpdateTime;
    private int fontSize = 22;

    public Dragging(Module module, String name, float initialXVal, float initialYVal) {
        this.module = module;
        this.name = name;
        this.xPos = initialXVal;
        this.yPos = initialYVal;
        this.initialXVal = initialXVal;
        this.initialYVal = initialYVal;
    }

    public Module getModule() {
        return this.module;
    }

    public String getName() {
        return this.name;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getX() {
        return this.xPos;
    }

    public void setX(float x) {
        this.xPos = x;
    }

    public float getY() {
        return this.yPos;
    }

    public void setY(float y) {
        this.yPos = y;
    }

    public final void onDraw(int mouseX, int mouseY, MainWindow res) {
        Vec2i fixed = ClientUtil.getMouse(mouseX, mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        if (this.dragging) {
            float radius = 5.0f;
            this.xPos = (float)mouseX - this.startX;
            this.yPos = (float)mouseY - this.startY;
            this.xPos = this.snap(this.xPos, 2.0f, 10.0f);
            this.yPos = this.snap(this.yPos, 2.0f, 10.0f);
            if (this.xPos + this.width > (float)res.scaledWidth()) {
                this.xPos = (float)res.scaledWidth() - this.width;
            }
            if (this.yPos + this.height > (float)res.scaledHeight()) {
                this.yPos = (float)res.scaledHeight() - this.height;
            }
            if (this.xPos < 0.0f) {
                this.xPos = 0.0f;
            }
            if (this.yPos < 0.0f) {
                this.yPos = 0.0f;
            }
            float alpha = this.lineAlpha * 1.0f;
            int color = (int)(alpha * 255.0f) << 24 | 0xFFFFFF;
            if (this.xPos + this.width / 2.0f >= (float)(res.getScaledWidth() / 2) - radius && this.xPos + this.width / 2.0f <= (float)(res.getScaledWidth() / 2) + radius && (float)mouseX >= this.xPos) {
                this.xPos = (float)(res.getScaledWidth() / 2) - this.width / 2.0f;
                DisplayUtils.drawRoundedRect((float)(res.getScaledWidth() / 2), 0.0f, 1.0f, (float)res.getScaledHeight(), 0.0f, color);
                this.showHorizontalLine = false;
                this.showVerticalLine = false;
            } else {
                this.checkClosestGridLines();
            }
            this.updateLineAlpha(true);
        } else {
            this.updateLineAlpha(false);
        }
        this.drawGridLines(res);
    }

    private void drawGridLines(MainWindow res) {
        float alpha = this.lineAlpha * 1.0f;
        int color = (int)(alpha * 255.0f) << 24 | 0xFFFFFF;
        if (this.showVerticalLine) {
            DisplayUtils.drawRoundedRect(this.closestVerticalLine, 0.0f, 1.0f, (float)res.scaledHeight(), 0.0f, color);
        }
        if (this.showHorizontalLine) {
            DisplayUtils.drawRoundedRect(0.0f, this.closestHorizontalLine, (float)res.scaledWidth(), 1.0f, 0.0f, color);
        }
    }

    private void updateLineAlpha(boolean increasing) {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (float)(currentTime - this.lastUpdateTime) / 1000.0f;
        this.lastUpdateTime = currentTime;
        if (increasing) {
            this.lineAlpha += deltaTime * 4.0f;
            if (this.lineAlpha > 1.0f) {
                this.lineAlpha = 1.0f;
            }
        } else {
            this.lineAlpha -= deltaTime * 4.0f;
            if (this.lineAlpha < 0.0f) {
                this.lineAlpha = 0.0f;
            }
        }
    }

    private void checkClosestGridLines() {
        this.closestVerticalLine = (float)Math.round(this.xPos / 2.0f) * 2.0f;
        this.closestHorizontalLine = (float)Math.round(this.yPos / 2.0f) * 2.0f;
        this.showVerticalLine = Math.abs(this.xPos - this.closestVerticalLine) < 10.0f;
        this.showHorizontalLine = Math.abs(this.yPos - this.closestHorizontalLine) < 10.0f;
    }

    private float snap(float pos, float gridSpacing, float snapThreshold) {
        float gridPos = (float)Math.round(pos / gridSpacing) * gridSpacing;
        if (Math.abs(pos - gridPos) < snapThreshold) {
            return gridPos;
        }
        return pos;
    }

    public final boolean onClick(double mouseX, double mouseY, int button) {
        Vec2i fixed = ClientUtil.getMouse((int)mouseX, (int)mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        if (button == 0 && MathUtil.isHovered((float)mouseX, (float)mouseY, this.xPos, this.yPos, this.width, this.height)) {
            this.dragging = true;
            this.startX = (int)(mouseX - (double)this.xPos);
            this.startY = (int)(mouseY - (double)this.yPos);
            this.lastUpdateTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public final void onRelease(int button) {
        if (button == 0) {
            this.dragging = false;
        }
    }

    public void resetPosition() {
        this.xPos = this.initialXVal;
        this.yPos = this.initialYVal;
    }
}


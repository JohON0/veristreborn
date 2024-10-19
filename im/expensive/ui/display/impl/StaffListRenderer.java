/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.config.StaffStorage;
import im.expensive.events.EventDisplay;
import im.expensive.events.EventUpdate;
import im.expensive.ui.display.ElementRenderer;
import im.expensive.ui.display.ElementUpdater;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.animations.easing.CompactAnimation;
import im.expensive.utils.animations.easing.Easing;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.drag.Dragging;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.gl.Scissor;
import im.expensive.utils.render.rect.DisplayUtils;
import im.expensive.utils.text.font.ClientFonts;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;

public class StaffListRenderer
implements ElementRenderer,
ElementUpdater {
    private final Dragging dragging;
    private final CompactAnimation widthAnimation = new CompactAnimation(Easing.EASE_OUT_QUART, 100L);
    private final CompactAnimation heightAnimation = new CompactAnimation(Easing.EASE_OUT_QUART, 100L);
    private final List<Staff> staffPlayers = new ArrayList<Staff>();
    private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");
    private final Pattern prefixMatches = Pattern.compile(".*(mod|der|adm|help|wne|\u0445\u0435\u043b\u043f|\u0430\u0434\u043c|\u043f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430|\u043a\u0443\u0440\u0430|own|taf|curat|dev|supp|yt|\u0441\u043e\u0442\u0440\u0443\u0434).*");
    private float width;
    private float height;

    @Override
    public void update(EventUpdate e) {
        this.staffPlayers.clear();
        for (ScorePlayerTeam team : StaffListRenderer.mc.world.getScoreboard().getTeams().stream().sorted(Comparator.comparing(Team::getName)).toList()) {
            Staff staff;
            String name = team.getMembershipCollection().toString().replaceAll("[\\[\\]]", "");
            boolean vanish = true;
            for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                if (!info.getGameProfile().getName().equals(name)) continue;
                vanish = false;
            }
            if (!this.namePattern.matcher(name).matches() || name.equals(StaffListRenderer.mc.player.getName().getString())) continue;
            if (!vanish && (this.prefixMatches.matcher(team.getPrefix().getString().toLowerCase(Locale.ROOT)).matches() || StaffStorage.isStaff(name))) {
                staff = new Staff(team.getPrefix(), name, false, Status.NONE);
                this.staffPlayers.add(staff);
            }
            if (!vanish || team.getPrefix().getString().isEmpty()) continue;
            staff = new Staff(team.getPrefix(), name, true, Status.VANISHED);
            this.staffPlayers.add(staff);
        }
    }

    @Override
    public void render(EventDisplay eventDisplay) {
        float posX = this.dragging.getX();
        float posY = this.dragging.getY();
        float padding = 5.0f;
        float fontSize = 6.5f;
        MatrixStack ms = eventDisplay.getMatrixStack();
        String name = "Online staff";
        float iconSize = 10.0f;
        DisplayUtils.drawStyledRect(posX, posY, this.width, this.height);
        int textColor = Theme.textColor;
        Fonts.montserrat.drawText(ms, name, posX + iconSize + 8.0f, posY + padding + 0.5f, textColor, fontSize, 0.07f);
        ClientFonts.icons_nur[20].drawString(ms, "E", (double)(posX + 5.0f), (double)(posY + 6.0f), textColor);
        DisplayUtils.drawRectHorizontalW(posX, posY + 17.0f, this.width, 2.5, ColorUtils.rgba(0, 0, 0, 0), ColorUtils.rgba(0, 0, 0, 63));
        posY += fontSize + padding + 2.0f;
        float maxWidth = Fonts.sfMedium.getWidth(name, fontSize) + padding * 2.0f;
        float localHeight = fontSize + padding * 2.0f;
        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY += 5.0f, this.width, this.height);
        for (Staff f : this.staffPlayers) {
            ITextComponent prefix = f.getPrefix();
            float prefixWidth = Fonts.montserrat.getWidth(prefix, fontSize);
            String staff = (prefix.getString().isEmpty() ? "" : " ") + f.getName();
            float nameWidth = Fonts.montserrat.getWidth(staff, fontSize);
            float localWidth = prefixWidth + nameWidth + 3.0f + padding * 3.0f;
            Fonts.montserrat.drawText(ms, prefix, posX + padding - 0.5f, posY + 2.0f, fontSize, 255);
            Fonts.montserrat.drawText(ms, staff, posX + padding + prefixWidth - 0.5f, posY + 2.0f, textColor, fontSize, 0.05f);
            DisplayUtils.drawCircle(posX + this.width - padding - 1.0f, posY + padding + 0.5f, 4.0f, f.getStatus().color);
            if (localWidth > maxWidth) {
                maxWidth = localWidth;
            }
            posY += fontSize + padding - 2.0f;
            localHeight += fontSize + padding - 2.0f;
        }
        Scissor.unset();
        Scissor.pop();
        this.widthAnimation.run(Math.max(maxWidth, 70.0f));
        this.width = (float)this.widthAnimation.getValue();
        this.heightAnimation.run(localHeight + 5.5f);
        this.height = (float)this.heightAnimation.getValue();
        this.dragging.setWidth(this.width);
        this.dragging.setHeight(this.height);
    }

    public StaffListRenderer(Dragging dragging) {
        this.dragging = dragging;
    }

    public static class Staff {
        ITextComponent prefix;
        String name;
        boolean isSpec;
        Status status;

        public void updateStatus() {
            for (NetworkPlayerInfo info : IMinecraft.mc.getConnection().getPlayerInfoMap()) {
                if (!info.getGameProfile().getName().equals(this.name)) continue;
                if (info.getGameType() == GameType.SPECTATOR) {
                    return;
                }
                this.status = Status.NONE;
                return;
            }
            this.status = Status.VANISHED;
        }

        public Staff(ITextComponent prefix, String name, boolean isSpec, Status status) {
            this.prefix = prefix;
            this.name = name;
            this.isSpec = isSpec;
            this.status = status;
        }

        public ITextComponent getPrefix() {
            return this.prefix;
        }

        public String getName() {
            return this.name;
        }

        public boolean isSpec() {
            return this.isSpec;
        }

        public Status getStatus() {
            return this.status;
        }

        public void setPrefix(ITextComponent prefix) {
            this.prefix = prefix;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSpec(boolean isSpec) {
            this.isSpec = isSpec;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Staff)) {
                return false;
            }
            Staff other = (Staff)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.isSpec() != other.isSpec()) {
                return false;
            }
            ITextComponent this$prefix = this.getPrefix();
            ITextComponent other$prefix = other.getPrefix();
            if (this$prefix == null ? other$prefix != null : !this$prefix.equals(other$prefix)) {
                return false;
            }
            String this$name = this.getName();
            String other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
                return false;
            }
            Status this$status = this.getStatus();
            Status other$status = other.getStatus();
            return !(this$status == null ? other$status != null : !((Object)((Object)this$status)).equals((Object)other$status));
        }

        protected boolean canEqual(Object other) {
            return other instanceof Staff;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + (this.isSpec() ? 79 : 97);
            ITextComponent $prefix = this.getPrefix();
            result = result * 59 + ($prefix == null ? 43 : $prefix.hashCode());
            String $name = this.getName();
            result = result * 59 + ($name == null ? 43 : $name.hashCode());
            Status $status = this.getStatus();
            result = result * 59 + ($status == null ? 43 : ((Object)((Object)$status)).hashCode());
            return result;
        }

        public String toString() {
            return "StaffListRenderer.Staff(prefix=" + this.getPrefix() + ", name=" + this.getName() + ", isSpec=" + this.isSpec() + ", status=" + this.getStatus() + ")";
        }
    }

    public static enum Status {
        NONE(ColorUtils.rgb(111, 254, 68)),
        VANISHED(ColorUtils.rgb(254, 68, 68)),
        NEAR(ColorUtils.rgb(244, 221, 59));

        public final int color;

        private Status(int color) {
            this.color = color;
        }
    }
}


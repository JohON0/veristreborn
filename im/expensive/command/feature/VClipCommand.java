/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.feature;

import im.expensive.Expensive;
import im.expensive.command.api.CommandException;
import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.CommandWithAdvice;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.MultiNamedCommand;
import im.expensive.command.interfaces.Parameters;
import im.expensive.command.interfaces.Prefix;
import im.expensive.ui.notify.impl.WarningNotify;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.math.NumberUtils;

public class VClipCommand
implements Command,
CommandWithAdvice,
MultiNamedCommand {
    private final Prefix prefix;
    private final Logger logger;
    private final Minecraft mc;

    @Override
    public void execute(Parameters parameters) {
        String direction;
        BlockPos playerPos = this.mc.player.getPosition();
        float yOffset = switch (direction = parameters.asString(0).orElseThrow(() -> new CommandException(TextFormatting.RED + "\u041d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e \u0443\u043a\u0430\u0437\u0430\u0442\u044c \u043d\u0430\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0438\u043b\u0438 \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435."))) {
            case "up" -> this.findOffset(playerPos, true);
            case "down" -> this.findOffset(playerPos, false);
            default -> this.parseOffset(direction);
        };
        if (yOffset != 0.0f) {
            boolean hasElytra;
            int elytraSlot = this.getElytraSlot();
            boolean bl = hasElytra = elytraSlot != -1 && elytraSlot != -2;
            if (hasElytra) {
                this.switchElytra(elytraSlot, true);
            }
            this.teleport(yOffset, hasElytra);
            if (hasElytra) {
                this.switchElytra(elytraSlot, false);
            }
        } else {
            this.logger.log(TextFormatting.RED + "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0432\u044b\u043f\u043e\u043b\u043d\u0438\u0442\u044c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e.");
        }
    }

    @Override
    public String name() {
        return "vclip";
    }

    @Override
    public String description() {
        return "\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u0443\u0435\u0442 \u0432\u0432\u0435\u0440\u0445/\u0432\u043d\u0438\u0437 \u043f\u043e \u0432\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u0438";
    }

    @Override
    public List<String> adviceMessage() {
        Expensive.getInstance().getNotifyManager().add(0, new WarningNotify("\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b!", 1000L));
        String commandPrefix = this.prefix.get();
        return List.of((Object)(commandPrefix + "vclip up - \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u0432\u0432\u0435\u0440\u0445 \u0434\u043e \u0431\u043b\u0438\u0436\u0430\u0439\u0448\u0435\u0433\u043e \u0441\u0432\u043e\u0431\u043e\u0434\u043d\u043e\u0433\u043e \u043f\u0440\u043e\u0441\u0442\u0440\u0430\u043d\u0441\u0442\u0432\u0430"), (Object)(commandPrefix + "vclip down - \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u0432\u043d\u0438\u0437 \u0434\u043e \u0431\u043b\u0438\u0436\u0430\u0439\u0448\u0435\u0433\u043e \u0441\u0432\u043e\u0431\u043e\u0434\u043d\u043e\u0433\u043e \u043f\u0440\u043e\u0441\u0442\u0440\u0430\u043d\u0441\u0442\u0432\u0430"), (Object)(commandPrefix + "vclip <distance> - \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043d\u0430 \u0443\u043a\u0430\u0437\u0430\u043d\u043d\u043e\u0435 \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435"), (Object)("\u041f\u0440\u0438\u043c\u0435\u0440: " + TextFormatting.RED + commandPrefix + "vclip 10"));
    }

    private float findOffset(BlockPos playerPos, boolean toUp) {
        int startY = toUp ? 3 : -1;
        int endY = toUp ? 255 : -255;
        int step = toUp ? 1 : -1;
        for (int i = startY; i != endY; i += step) {
            BlockPos targetPos = playerPos.add(0, i, 0);
            if (this.mc.world.getBlockState(targetPos) == Blocks.AIR.getDefaultState()) {
                return i + (toUp ? 1 : -1);
            }
            if (this.mc.world.getBlockState(targetPos) != Blocks.BEDROCK.getDefaultState() || toUp) continue;
            this.logger.log(TextFormatting.RED + "\u0422\u0443\u0442 \u043d\u0435\u043b\u044c\u0437\u044f \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u043f\u043e\u0434 \u0437\u0435\u043c\u043b\u044e.");
            return 0.0f;
        }
        return 0.0f;
    }

    private void teleport(float yOffset, boolean elytra) {
        if (elytra) {
            for (int i = 0; i < 2; ++i) {
                this.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(this.mc.player.getPosX(), this.mc.player.getPosY(), this.mc.player.getPosZ(), false));
            }
            this.mc.player.connection.sendPacket(new CEntityActionPacket(this.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            this.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(this.mc.player.getPosX(), this.mc.player.getPosY() + (double)yOffset, this.mc.player.getPosZ(), false));
            this.mc.player.connection.sendPacket(new CEntityActionPacket(this.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            this.mc.player.setPosition(this.mc.player.getPosX(), this.mc.player.getPosY() + (double)yOffset, this.mc.player.getPosZ());
            String blockUnit = yOffset > 1.0f ? "\u0431\u043b\u043e\u043a\u0430" : "\u0431\u043b\u043e\u043a";
            this.logger.log(TextFormatting.GRAY + "\u041f\u043e\u043f\u044b\u0442\u043a\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u0441 \u044d\u043b\u0438\u0442\u0440\u043e\u0439...");
            this.logger.log(String.format("\u0412\u044b \u0431\u044b\u043b\u0438 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043d\u0430 %.1f %s \u043f\u043e \u0432\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u0438", Float.valueOf(yOffset), blockUnit));
            return;
        }
        int packetsCount = this.calculatePacketsCount(yOffset);
        for (int i = 0; i < packetsCount; ++i) {
            this.mc.player.connection.sendPacket(new CPlayerPacket(this.mc.player.isOnGround()));
        }
        this.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(this.mc.player.getPosX(), this.mc.player.getPosY() + (double)yOffset, this.mc.player.getPosZ(), false));
        this.mc.player.setPosition(this.mc.player.getPosX(), this.mc.player.getPosY() + (double)yOffset, this.mc.player.getPosZ());
        String blockUnit = yOffset > 1.0f ? "\u0431\u043b\u043e\u043a\u0430" : "\u0431\u043b\u043e\u043a";
        this.logger.log(String.format("\u0412\u044b \u0431\u044b\u043b\u0438 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043d\u0430 %.1f %s \u043f\u043e \u0432\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u0438", Float.valueOf(yOffset), blockUnit));
    }

    private float parseOffset(String distance) {
        if (NumberUtils.isNumber(distance)) {
            return Float.parseFloat(distance);
        }
        this.logger.log(TextFormatting.RED + distance + TextFormatting.GRAY + " \u043d\u0435 \u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f \u0447\u0438\u0441\u043b\u043e\u043c!");
        return 0.0f;
    }

    private int calculatePacketsCount(float yOffset) {
        return Math.max((int)(yOffset / 1000.0f), 3);
    }

    private void switchElytra(int elytraSlot, boolean equip) {
        int chestplateSlot = 6;
        if (equip) {
            this.mc.playerController.windowClick(0, elytraSlot, 0, ClickType.PICKUP, this.mc.player);
            this.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, this.mc.player);
        } else {
            this.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, this.mc.player);
            this.mc.playerController.windowClick(0, elytraSlot, 0, ClickType.PICKUP, this.mc.player);
        }
    }

    private int getElytraSlot() {
        for (ItemStack stack : this.mc.player.getArmorInventoryList()) {
            if (stack.getItem() != Items.ELYTRA) continue;
            return -2;
        }
        int slot = -1;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = this.mc.player.inventory.getStackInSlot(i);
            if (s.getItem() != Items.ELYTRA) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    @Override
    public List<String> aliases() {
        return List.of((Object)"vc");
    }

    public VClipCommand(Prefix prefix, Logger logger, Minecraft mc) {
        this.prefix = prefix;
        this.logger = logger;
        this.mc = mc;
    }
}


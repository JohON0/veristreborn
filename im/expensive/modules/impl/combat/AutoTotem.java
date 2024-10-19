/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.player.InventoryUtil;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.AirItem;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.potion.Effects;

@ModuleRegister(name="AutoTotem", category=Category.Combat)
public class AutoTotem
extends Module {
    private final SliderSetting health = new SliderSetting("\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435", 3.5f, 1.0f, 20.0f, 0.1f);
    private final BooleanSetting swapBack = new BooleanSetting("\u0412\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442", true);
    private final BooleanSetting noBallSwitch = new BooleanSetting("\u041d\u0435 \u0431\u0440\u0430\u0442\u044c \u0435\u0441\u043b\u0438 \u0448\u0430\u0440", false);
    private final ModeListSetting options = new ModeListSetting("\u0423\u0447\u0438\u0442\u044b\u0432\u0430\u0442\u044c", new BooleanSetting("\u0417\u043e\u043b\u043e\u0442\u044b\u0435 \u0441\u0435\u0440\u0434\u0446\u0430", true), new BooleanSetting("\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b", true), new BooleanSetting("\u041f\u0430\u0434\u0435\u043d\u0438\u0435", true), new BooleanSetting("\u042d\u043b\u0438\u0442\u0440\u0443", true));
    private final SliderSetting healthElytra = new SliderSetting("\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u043d\u0430 \u044d\u043b\u0438\u0442\u0440\u0435", 6.0f, 1.0f, 20.0f, 0.5f).setVisible(() -> (Boolean)this.options.getValueByName("\u042d\u043b\u0438\u0442\u0440\u0443").get());
    int oldItem = -1;

    public AutoTotem() {
        this.addSettings(this.health, this.healthElytra, this.swapBack, this.noBallSwitch, this.options);
    }

    @Subscribe
    private void handleEventUpdate(EventUpdate event) {
        boolean totemInHand;
        int slot = InventoryUtil.getItemSlot(Items.TOTEM_OF_UNDYING);
        boolean handNotNull = !(AutoTotem.mc.player.getHeldItemOffhand().getItem() instanceof AirItem);
        boolean bl = totemInHand = AutoTotem.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING || AutoTotem.mc.player.getHeldItemMainhand().getItem() == Items.TOTEM_OF_UNDYING;
        if (this.canSwap()) {
            if (slot >= 0 && !totemInHand) {
                AutoTotem.mc.playerController.windowClick(0, slot, 40, ClickType.SWAP, AutoTotem.mc.player);
                AutoTotem.mc.playerController.windowClick(0, slot, 40, ClickType.SWAP, AutoTotem.mc.player);
                AutoTotem.mc.playerController.windowClick(0, slot, 40, ClickType.SWAP, AutoTotem.mc.player);
                if (handNotNull && this.oldItem == -1) {
                    this.oldItem = slot;
                }
            }
        } else if (this.oldItem != -1 && ((Boolean)this.swapBack.get()).booleanValue()) {
            AutoTotem.mc.playerController.windowClickFixed(0, this.oldItem, 40, ClickType.SWAP, AutoTotem.mc.player, 10);
            AutoTotem.mc.playerController.windowClickFixed(0, this.oldItem, 40, ClickType.SWAP, AutoTotem.mc.player, 20);
            AutoTotem.mc.playerController.windowClickFixed(0, this.oldItem, 40, ClickType.SWAP, AutoTotem.mc.player, 30);
            AutoTotem.mc.playerController.windowClickFixed(0, this.oldItem, 40, ClickType.SWAP, AutoTotem.mc.player, 40);
            AutoTotem.mc.playerController.windowClickFixed(0, this.oldItem, 40, ClickType.SWAP, AutoTotem.mc.player, 50);
            this.oldItem = -1;
        }
    }

    private boolean canSwap() {
        float absorption;
        float f = absorption = (Boolean)this.options.getValueByName("\u0417\u043e\u043b\u043e\u0442\u044b\u0435 \u0441\u0435\u0440\u0434\u0446\u0430").get() != false && AutoTotem.mc.player.isPotionActive(Effects.ABSORPTION) ? AutoTotem.mc.player.getAbsorptionAmount() : 0.0f;
        if (AutoTotem.mc.player.getHealth() + absorption <= ((Float)this.health.get()).floatValue()) {
            return true;
        }
        if (!this.isBall() && this.checkCrystal()) {
            return true;
        }
        return this.checkElytra() || this.checkFall();
    }

    private boolean checkElytra() {
        if (!((Boolean)this.options.getValueByName("\u042d\u043b\u0438\u0442\u0440\u0443").get()).booleanValue()) {
            return false;
        }
        return AutoTotem.mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA && AutoTotem.mc.player.getHealth() <= ((Float)this.healthElytra.get()).floatValue();
    }

    private boolean checkFall() {
        if (!((Boolean)this.options.getValueByName("\u041f\u0430\u0434\u0435\u043d\u0438\u0435").get()).booleanValue()) {
            return false;
        }
        return AutoTotem.mc.player.fallDistance > 10.0f;
    }

    private boolean isBall() {
        if (((Boolean)this.options.getValueByName("\u041f\u0430\u0434\u0435\u043d\u0438\u0435").get()).booleanValue() && AutoTotem.mc.player.fallDistance > 5.0f) {
            return false;
        }
        return (Boolean)this.noBallSwitch.get() != false && AutoTotem.mc.player.getHeldItemOffhand().getItem() instanceof SkullItem;
    }

    private boolean checkCrystal() {
        Entity entity;
        if (!((Boolean)this.options.getValueByName("\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b").get()).booleanValue()) {
            return false;
        }
        Iterator<Entity> iterator2 = AutoTotem.mc.world.getAllEntities().iterator();
        do {
            if (!iterator2.hasNext()) {
                return false;
            }
            entity = iterator2.next();
            if (!(entity instanceof EnderCrystalEntity) || !(AutoTotem.mc.player.getDistance(entity) <= 6.0f)) continue;
            return true;
        } while (!(entity instanceof TNTEntity) && !(entity instanceof TNTMinecartEntity) || !(AutoTotem.mc.player.getDistance(entity) <= 6.0f));
        return true;
    }

    private void reset() {
        this.oldItem = -1;
    }

    @Override
    public void onEnable() {
        this.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.reset();
        super.onDisable();
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventUpdate;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.SliderSetting;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

@ModuleRegister(name="Nuker", category=Category.Movement)
public class Nuker
extends Module {
    private final SliderSetting rangenuking = new SliderSetting("\u0420\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435", 2.0f, 1.0f, 5.0f, 0.1f);
    private final SliderSetting intervalnuking = new SliderSetting("\u0418\u043d\u0442\u0435\u0440\u0432\u0430\u043b", 700.0f, 50.0f, 1400.0f, 50.0f);
    private long lastBlockBreakTime = 0L;

    public Nuker() {
        this.addSettings(this.rangenuking, this.intervalnuking);
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        int range = ((Float)this.rangenuking.get()).intValue();
        long blockBreakInterval = Math.round(((Float)this.intervalnuking.get()).floatValue());
        Vector3d playerPos = Nuker.mc.player.getPositionVec();
        if (System.currentTimeMillis() - this.lastBlockBreakTime >= blockBreakInterval) {
            Nuker.mc.player.swing(Hand.MAIN_HAND, false);
            for (int y = 4; y >= 0; --y) {
                for (int x = range; x >= -range; --x) {
                    for (int z = range; z >= -range; --z) {
                        BlockPos start = Nuker.mc.player.getPosition();
                        BlockPos pos = start.add(x, y, z);
                        Nuker.mc.playerController.onPlayerDamageBlock(pos, Direction.UP);
                    }
                }
            }
            this.lastBlockBreakTime = System.currentTimeMillis();
        }
    }
}


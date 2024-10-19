/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import im.expensive.events.EventDisplay;
import im.expensive.events.EventMotion;
import im.expensive.events.EventPacket;
import im.expensive.events.EventUpdate;
import im.expensive.events.WorldEvent;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleRegister;
import im.expensive.modules.settings.impl.BooleanSetting;
import im.expensive.modules.settings.impl.ModeListSetting;
import im.expensive.modules.settings.impl.ModeSetting;
import im.expensive.modules.settings.impl.SliderSetting;
import im.expensive.utils.math.StopWatch;
import im.expensive.utils.render.color.ColorUtils;
import im.expensive.utils.render.font.Fonts;
import im.expensive.utils.render.rect.DisplayUtils;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.optifine.render.RenderUtils;

@ModuleRegister(name="OreFinder", category=Category.Render)
public class OreFinder
extends Module {
    CopyOnWriteArrayList<BlockPos> waiting = new CopyOnWriteArrayList();
    public ModeSetting mode = new ModeSetting("\u0422\u0438\u043f", "Bypass", "Default", "Bypass");
    public SliderSetting up = new SliderSetting("\u0412\u0432\u0435\u0440\u0445", 5.0f, 0.0f, 30.0f, 1.0f).setVisible(() -> this.mode.is("Bypass"));
    public SliderSetting down = new SliderSetting("\u0412\u043d\u0438\u0437", 5.0f, 0.0f, 30.0f, 1.0f).setVisible(() -> this.mode.is("Bypass"));
    public SliderSetting radius = new SliderSetting("\u0420\u0430\u0434\u0438\u0443\u0441", 20.0f, 0.0f, 30.0f, 1.0f).setVisible(() -> this.mode.is("Bypass"));
    public SliderSetting delay = new SliderSetting("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430", 13.0f, 0.0f, 40.0f, 1.0f).setVisible(() -> this.mode.is("Bypass"));
    public SliderSetting skip = new SliderSetting("\u041f\u0440\u043e\u043f\u0443\u0441\u043a", 3.0f, 1.0f, 5.0f, 1.0f).setVisible(() -> this.mode.is("Bypass"));
    public ModeListSetting ores = new ModeListSetting("\u0418\u0441\u043a\u0430\u0442\u044c", new BooleanSetting("\u0423\u0433\u043e\u043b\u044c", false), new BooleanSetting("\u0416\u0435\u043b\u0435\u0437\u043e", false), new BooleanSetting("\u0420\u0435\u0434\u0441\u0442\u043e\u0443\u043d", false), new BooleanSetting("\u0417\u043e\u043b\u043e\u0442\u043e", false), new BooleanSetting("\u042d\u043c\u0435\u0440\u0430\u043b\u044c\u0434\u044b", false), new BooleanSetting("\u0410\u043b\u043c\u0430\u0437\u044b", false), new BooleanSetting("\u041d\u0435\u0437\u0435\u0440\u0438\u0442", false), new BooleanSetting("\u041b\u0430\u0437\u0443\u0440\u0438\u0442", false));
    CopyOnWriteArrayList<BlockPos> clicked = new CopyOnWriteArrayList();
    StopWatch stopWatch = new StopWatch();
    BlockPos clicking;
    Thread thread;

    public OreFinder() {
        this.addSettings(this.mode, this.radius, this.up, this.down, this.delay, this.skip, this.ores);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread.stop();
        }
        this.clicking = null;
        this.clicked.clear();
        this.waiting.clear();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.mode.is("Bypass")) {
            this.waiting = OreFinder.removeEveryOther(this.getBlocks(), ((Float)this.skip.get()).intValue());
            this.thread = new Thread(() -> {
                if (OreFinder.mc.player != null) {
                    for (BlockPos click : this.waiting) {
                        OreFinder.mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, click, Direction.UP));
                        this.clicked.add(click);
                        this.clicking = click;
                    }
                }
            });
            this.thread.start();
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (this.mode.is("Default") && this.stopWatch.isReached(2000L)) {
            this.waiting = OreFinder.removeEveryOther(this.getBlocks(), 1);
            this.thread = new Thread(() -> {
                if (OreFinder.mc.player != null) {
                    for (BlockPos click : this.waiting) {
                        this.clicked.add(click);
                        this.clicking = click;
                    }
                }
            });
            this.thread.start();
            this.stopWatch.reset();
        }
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if (this.thread != null && this.thread.isAlive() && e.isSend() && this.mode.is("Bypass")) {
            if (e.getPacket() instanceof CPlayerPacket) {
                e.cancel();
            }
            if (e.getPacket() instanceof CAnimateHandPacket) {
                e.cancel();
            }
            if (e.getPacket() instanceof CPlayerTryUseItemPacket) {
                e.cancel();
            }
            if (e.getPacket() instanceof CHeldItemChangePacket) {
                e.cancel();
            }
        }
    }

    @Subscribe
    public void onMotion(EventMotion e) {
        if (this.thread != null && this.thread.isAlive()) {
            e.cancel();
        }
    }

    @Subscribe
    public void onDisplay(EventDisplay e) {
        if (this.mode.is("Bypass")) {
            float width = 100.0f;
            float heigth = 15.0f;
            DisplayUtils.drawRoundedRect((float)mc.getMainWindow().getScaledWidth() / 2.0f - width / 2.0f, 10.0f, width, heigth, 4.0f, ColorUtils.rgba(10, 10, 10, 128));
            float x = (float)mc.getMainWindow().getScaledWidth() / 2.0f - width / 2.0f;
            Fonts.montserrat.drawText(e.getMatrixStack(), this.clicked.size() + "/" + this.waiting.size(), x + 5.0f, 15.0f, -1, 8.0f);
            long millis = (long)this.waiting.size() * (long)((Float)this.delay.get()).intValue() - (long)(this.clicked.size() * ((Float)this.delay.get()).intValue());
            String time = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            Fonts.montserrat.drawText(e.getMatrixStack(), time, x + width - Fonts.montserrat.getWidth(time, 8.0f) - 5.0f, 14.0f, -1, 8.0f);
        }
    }

    @Subscribe
    public void onWorld(WorldEvent e) {
        if (this.clicked != null && this.clicking != null) {
            RenderUtils.drawBlockBox(this.clicking, ColorUtils.rgba(83, 252, 154, 255));
            for (BlockPos click : this.clicked) {
                int color = this.isValid(click);
                if (color == -1) continue;
                RenderUtils.drawBlockBox(click, color);
            }
        }
    }

    private static <T> CopyOnWriteArrayList<T> removeEveryOther(CopyOnWriteArrayList<T> inList, int offset) {
        if (offset == 1) {
            return inList;
        }
        CopyOnWriteArrayList<Object> outList = new CopyOnWriteArrayList<Object>();
        Object[] ts = inList.toArray();
        for (int i = 0; i < ts.length; ++i) {
            if (i % offset != 0) continue;
            outList.add(ts[i]);
        }
        return (CopyOnWriteArrayList<T>) outList;
    }

    CopyOnWriteArrayList<BlockPos> getBlocks() {
        CopyOnWriteArrayList<BlockPos> blocks = new CopyOnWriteArrayList<BlockPos>();
        BlockPos start = OreFinder.mc.player.getPosition();
        int dis = this.mode.is("Bypass") ? ((Float)this.radius.get()).intValue() : 25;
        int up = this.mode.is("Bypass") ? ((Float)this.up.get()).intValue() : 25;
        int down = this.mode.is("Bypass") ? ((Float)this.down.get()).intValue() : 25;
        for (int y = up; y >= -down; --y) {
            for (int x = dis; x >= -dis; --x) {
                for (int z = dis; z >= -dis; --z) {
                    BlockPos pos = start.add(x, y, z);
                    if (pos.getY() <= 0) continue;
                    Block block = OreFinder.mc.world.getBlockState(pos).getBlock();
                    if (this.mode.is("Bypass") && block instanceof AirBlock || this.mode.is("Default") && (block != Blocks.COAL_ORE && ((Boolean)this.ores.getValueByName("\u0423\u0433\u043e\u043b\u044c").get()).booleanValue() || block != Blocks.DIAMOND_ORE && ((Boolean)this.ores.getValueByName("\u0410\u043b\u043c\u0430\u0437\u044b").get()).booleanValue() || block != Blocks.EMERALD_ORE && ((Boolean)this.ores.getValueByName("\u042d\u043c\u0435\u0440\u0430\u043b\u044c\u0434\u044b").get()).booleanValue() || block == Blocks.GOLD_ORE && ((Boolean)this.ores.getValueByName("\u0417\u043e\u043b\u043e\u0442\u043e").get()).booleanValue() || block == Blocks.REDSTONE_ORE && ((Boolean)this.ores.getValueByName("\u0420\u0435\u0434\u0441\u0442\u043e\u0443\u043d").get()).booleanValue() || block == Blocks.ANCIENT_DEBRIS && ((Boolean)this.ores.getValueByName("\u041d\u0435\u0437\u0435\u0440\u0438\u0442").get()).booleanValue() || block == Blocks.IRON_ORE && ((Boolean)this.ores.getValueByName("\u0416\u0435\u043b\u0435\u0437\u043e").get()).booleanValue())) continue;
                    blocks.add(pos);
                }
            }
        }
        return blocks;
    }

    private int isValid(BlockPos pos) {
        BlockState state = OreFinder.mc.world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof OreBlock) {
            OreBlock ore = (OreBlock)block;
            if (ore == Blocks.COAL_ORE && ((Boolean)this.ores.get(0).get()).booleanValue()) {
                return ColorUtils.rgba(12, 12, 12, 255);
            }
            if (ore == Blocks.IRON_ORE && ((Boolean)this.ores.get(1).get()).booleanValue()) {
                return ColorUtils.rgba(122, 122, 122, 255);
            }
            if (ore == Blocks.REDSTONE_ORE && ((Boolean)this.ores.get(2).get()).booleanValue()) {
                return ColorUtils.rgba(255, 82, 82, 255);
            }
            if (ore == Blocks.GOLD_ORE && ((Boolean)this.ores.get(3).get()).booleanValue()) {
                return ColorUtils.rgba(247, 255, 102, 255);
            }
            if (ore == Blocks.EMERALD_ORE && ((Boolean)this.ores.get(4).get()).booleanValue()) {
                return ColorUtils.rgba(116, 252, 101, 255);
            }
            if (ore == Blocks.DIAMOND_ORE && ((Boolean)this.ores.get(5).get()).booleanValue()) {
                return ColorUtils.rgba(77, 219, 255, 255);
            }
            if (ore == Blocks.ANCIENT_DEBRIS && ((Boolean)this.ores.get(6).get()).booleanValue()) {
                return ColorUtils.rgba(105, 60, 12, 255);
            }
            if (ore == Blocks.LAPIS_ORE && ((Boolean)this.ores.get(7).get()).booleanValue()) {
                return ColorUtils.rgba(41, 41, 255, 255);
            }
        }
        return -1;
    }
}


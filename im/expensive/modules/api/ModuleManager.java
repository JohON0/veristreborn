/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.api;

import com.google.common.eventbus.Subscribe;
import im.expensive.Expensive;
import im.expensive.events.EventKey;
import im.expensive.modules.api.Category;
import im.expensive.modules.api.Module;
import im.expensive.modules.impl.combat.AntiBot;
import im.expensive.modules.impl.combat.AutoExplosion;
import im.expensive.modules.impl.combat.AutoGapple;
import im.expensive.modules.impl.combat.AutoSwap;
import im.expensive.modules.impl.combat.AutoTotem;
import im.expensive.modules.impl.combat.Backtrack;
import im.expensive.modules.impl.combat.Criticals;
import im.expensive.modules.impl.combat.CrystalAura;
import im.expensive.modules.impl.combat.EntityBox;
import im.expensive.modules.impl.combat.HitAura;
import im.expensive.modules.impl.combat.ItemCooldown;
import im.expensive.modules.impl.combat.NoEntityTrace;
import im.expensive.modules.impl.combat.NoFriendHurt;
import im.expensive.modules.impl.combat.PotionThrower;
import im.expensive.modules.impl.combat.SexAura;
import im.expensive.modules.impl.combat.SuperBow;
import im.expensive.modules.impl.combat.TPInfluence;
import im.expensive.modules.impl.combat.TriggerBot;
import im.expensive.modules.impl.combat.Velocity;
import im.expensive.modules.impl.misc.AntiAFK;
import im.expensive.modules.impl.misc.AntiPush;
import im.expensive.modules.impl.misc.AutoActions;
import im.expensive.modules.impl.misc.AutoBuy;
import im.expensive.modules.impl.misc.AutoDuel;
import im.expensive.modules.impl.misc.AutoFix;
import im.expensive.modules.impl.misc.AutoLeave;
import im.expensive.modules.impl.misc.AutoSort;
import im.expensive.modules.impl.misc.BetterMinecraft;
import im.expensive.modules.impl.misc.Blink;
import im.expensive.modules.impl.misc.ChatHelper;
import im.expensive.modules.impl.misc.ChestStealer;
import im.expensive.modules.impl.misc.ClickFriend;
import im.expensive.modules.impl.misc.ClickPearl;
import im.expensive.modules.impl.misc.ClientTune;
import im.expensive.modules.impl.misc.Disabler;
import im.expensive.modules.impl.misc.ElytraHelper;
import im.expensive.modules.impl.misc.FTHelper;
import im.expensive.modules.impl.misc.FakePlayer;
import im.expensive.modules.impl.misc.InventoryPlus;
import im.expensive.modules.impl.misc.ItemSwapFix;
import im.expensive.modules.impl.misc.NameProtect;
import im.expensive.modules.impl.misc.NoInteract;
import im.expensive.modules.impl.misc.NoServerDesync;
import im.expensive.modules.impl.misc.PlayerHelper;
import im.expensive.modules.impl.misc.RWHelper;
import im.expensive.modules.impl.misc.RWJoiner;
import im.expensive.modules.impl.misc.SelfDestruct;
import im.expensive.modules.impl.misc.SlowPackets;
import im.expensive.modules.impl.movement.AirJump;
import im.expensive.modules.impl.movement.AutoSprint;
import im.expensive.modules.impl.movement.ElytraBooster;
import im.expensive.modules.impl.movement.ElytraBounce;
import im.expensive.modules.impl.movement.ElytraFly;
import im.expensive.modules.impl.movement.ElytraSpeed;
import im.expensive.modules.impl.movement.Fly;
import im.expensive.modules.impl.movement.FreeCam;
import im.expensive.modules.impl.movement.Jesus;
import im.expensive.modules.impl.movement.LongJump;
import im.expensive.modules.impl.movement.MoveHelper;
import im.expensive.modules.impl.movement.NoClip;
import im.expensive.modules.impl.movement.NoFall;
import im.expensive.modules.impl.movement.Nuker;
import im.expensive.modules.impl.movement.Parkour;
import im.expensive.modules.impl.movement.Phase;
import im.expensive.modules.impl.movement.SafeWalk;
import im.expensive.modules.impl.movement.Scaffold;
import im.expensive.modules.impl.movement.Speed;
import im.expensive.modules.impl.movement.Spider;
import im.expensive.modules.impl.movement.Strafe;
import im.expensive.modules.impl.movement.TargetStrafe;
import im.expensive.modules.impl.movement.TestFeature;
import im.expensive.modules.impl.movement.Timer;
import im.expensive.modules.impl.movement.WaterSpeed;
import im.expensive.modules.impl.render.Arrows;
import im.expensive.modules.impl.render.ChinaHat;
import im.expensive.modules.impl.render.ClickGui;
import im.expensive.modules.impl.render.Crosshair;
import im.expensive.modules.impl.render.DeathEffect;
import im.expensive.modules.impl.render.ESP;
import im.expensive.modules.impl.render.FreeLook;
import im.expensive.modules.impl.render.FullBright;
import im.expensive.modules.impl.render.HUD;
import im.expensive.modules.impl.render.ItemPhysic;
import im.expensive.modules.impl.render.JumpCircle;
import im.expensive.modules.impl.render.NoRender;
import im.expensive.modules.impl.render.OreFinder;
import im.expensive.modules.impl.render.Particles;
import im.expensive.modules.impl.render.Predictions;
import im.expensive.modules.impl.render.SeeInvisibles;
import im.expensive.modules.impl.render.Snow;
import im.expensive.modules.impl.render.StorageESP;
import im.expensive.modules.impl.render.TNTTimer;
import im.expensive.modules.impl.render.Tracers;
import im.expensive.modules.impl.render.Trails;
import im.expensive.modules.impl.render.ViewModel;
import im.expensive.modules.impl.render.WorldTweaks;
import im.expensive.utils.render.font.Font;
import im.expensive.utils.text.font.ClientFonts;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ModuleManager {
    private final List<Module> modules = new CopyOnWriteArrayList<Module>();
    private PlayerHelper playerHelper;
    private NoServerDesync noServerDesync;
    private ViewModel viewModel;
    private HUD hud;
    private AutoGapple autoGapple;
    private AutoSprint autoSprint;
    private Velocity velocity;
    private NoRender noRender;
    private Timer timer;
    private InventoryPlus inventoryPlus;
    private ElytraHelper elytrahelper;
    private Phase phase;
    private AutoBuy autoBuy;
    private PotionThrower autopotion;
    private TriggerBot triggerbot;
    private ClickFriend clickfriend;
    private ESP esp;
    private FTHelper FTHelper;
    private ItemCooldown itemCooldown;
    private ClickPearl clickPearl;
    private AutoSwap autoSwap;
    private EntityBox entityBox;
    private AntiPush antiPush;
    private FreeCam freeCam;
    private ChestStealer chestStealer;
    private AutoLeave autoLeave;
    private Fly fly;
    private TargetStrafe targetStrafe;
    private ClientTune clientTune;
    private AutoTotem autoTotem;
    private AutoExplosion autoExplosion;
    private HitAura hitAura;
    private AntiBot antiBot;
    private Trails trails;
    private Crosshair crosshair;
    private DeathEffect deathEffect;
    private Strafe strafe;
    private ElytraFly elytraFly;
    private ChinaHat chinaHat;
    private Snow snow;
    private Particles particles;
//    private TargetESP targetESP;
    private JumpCircle jumpCircle;
    private ItemPhysic itemPhysic;
    private Predictions predictions;
    private NoEntityTrace noEntityTrace;
    private NoClip noClip;
    private StorageESP storageESP;
    private Spider spider;
    private NameProtect nameProtect;
    private NoInteract noInteract;
    private Tracers tracers;
    private SelfDestruct selfDestruct;
    private AntiAFK antiAFK;
    private BetterMinecraft betterMinecraft;
    private Backtrack backtrack;
    private SeeInvisibles seeInvisibles;
    private Jesus jesus;
    private Speed speed;
    private AirJump airJump;
    private Disabler disabler;
    private WaterSpeed waterSpeed;
    private NoFriendHurt noFriendHurt;
    private SuperBow superBow;
    private SafeWalk safeWalk;
    private ElytraSpeed elytraSpeed;
    private TPInfluence TPInfluence;
    private AutoFix autoFix;
    private ClickGui clickGui;
    private TNTTimer tntTimer;
    private Blink blink;
    private SlowPackets slowPackets;
    private Scaffold scaffold;
    private WorldTweaks worldTweaks;
    private Arrows arrows;
    private RWJoiner rwJoiner;
    private ChatHelper chatHelper;
    private CrystalAura crystalAura;
    private FakePlayer fakePlayer;
    private ElytraBounce elytraBounce;
    private Criticals criticals;
    private AutoActions autoActions;
    private NoFall noFall;
    private FullBright fullBright;
    private TestFeature testFeature;
    private AutoDuel autoDuel;
    private SexAura sexAura;
    private AutoSort autoSort;
    private MoveHelper moveHelper;
    private ElytraBooster elytraBooster;

    public void init() {
        Module[] moduleArray = new Module[96];
        this.moveHelper = new MoveHelper();
        moduleArray[0] = this.moveHelper;
        this.elytraBooster = new ElytraBooster();
        moduleArray[1] = this.elytraBooster;
        this.fullBright = new FullBright();
        moduleArray[2] = this.fullBright;
        this.autoDuel = new AutoDuel();
        moduleArray[3] = this.autoDuel;
        this.sexAura = new SexAura();
        moduleArray[4] = this.sexAura;
        this.autoSort = new AutoSort();
        moduleArray[5] = this.autoSort;
        this.crystalAura = new CrystalAura();
        moduleArray[6] = this.crystalAura;
        this.fakePlayer = new FakePlayer();
        moduleArray[7] = this.fakePlayer;
        this.elytraBounce = new ElytraBounce();
        moduleArray[8] = this.elytraBounce;
        this.criticals = new Criticals();
        moduleArray[9] = this.criticals;
        this.autoActions = new AutoActions();
        moduleArray[10] = this.autoActions;
        this.noFall = new NoFall();
        moduleArray[11] = this.noFall;
        this.rwJoiner = new RWJoiner();
        moduleArray[12] = this.rwJoiner;
        this.hud = new HUD();
        moduleArray[13] = this.hud;
        this.chatHelper = new ChatHelper();
        moduleArray[14] = this.chatHelper;
        this.arrows = new Arrows();
        moduleArray[15] = this.arrows;
        this.autoBuy = new AutoBuy();
        moduleArray[16] = this.autoBuy;
        this.noServerDesync = new NoServerDesync();
        moduleArray[17] = this.noServerDesync;
        this.playerHelper = new PlayerHelper();
        moduleArray[18] = this.playerHelper;
        this.worldTweaks = new WorldTweaks();
        moduleArray[19] = this.worldTweaks;
        this.scaffold = new Scaffold();
        moduleArray[20] = this.scaffold;
        this.slowPackets = new SlowPackets();
        moduleArray[21] = this.slowPackets;
        this.blink = new Blink();
        moduleArray[22] = this.blink;
        this.tntTimer = new TNTTimer();
        moduleArray[23] = this.tntTimer;
        this.deathEffect = new DeathEffect();
        moduleArray[24] = this.deathEffect;
        this.clickGui = new ClickGui();
        moduleArray[25] = this.clickGui;
        this.autoFix = new AutoFix();
        moduleArray[26] = this.autoFix;
        this.TPInfluence = new TPInfluence();
        moduleArray[27] = this.TPInfluence;
        this.elytraSpeed = new ElytraSpeed();
        moduleArray[28] = this.elytraSpeed;
        this.safeWalk = new SafeWalk();
        moduleArray[29] = this.safeWalk;
        this.superBow = new SuperBow();
        moduleArray[30] = this.superBow;
        this.noFriendHurt = new NoFriendHurt();
        moduleArray[31] = this.noFriendHurt;
        this.waterSpeed = new WaterSpeed();
        moduleArray[32] = this.waterSpeed;
        this.disabler = new Disabler();
        moduleArray[33] = this.disabler;
        this.jesus = new Jesus();
        moduleArray[34] = this.jesus;
        this.airJump = new AirJump();
        moduleArray[35] = this.airJump;
        this.speed = new Speed();
        moduleArray[36] = this.speed;
        this.autoGapple = new AutoGapple();
        moduleArray[37] = this.autoGapple;
        this.autoSprint = new AutoSprint();
        moduleArray[38] = this.autoSprint;
        this.velocity = new Velocity();
        moduleArray[39] = this.velocity;
        this.noRender = new NoRender();
        moduleArray[40] = this.noRender;
        this.inventoryPlus = new InventoryPlus();
        moduleArray[41] = this.inventoryPlus;
        this.seeInvisibles = new SeeInvisibles();
        moduleArray[42] = this.seeInvisibles;
        this.elytrahelper = new ElytraHelper();
        moduleArray[43] = this.elytrahelper;
        this.phase = new Phase();
        moduleArray[44] = this.phase;
        this.autopotion = new PotionThrower();
        moduleArray[45] = this.autopotion;
        this.noClip = new NoClip();
        moduleArray[46] = this.noClip;
        this.triggerbot = new TriggerBot();
        moduleArray[47] = this.triggerbot;
        this.clickfriend = new ClickFriend();
        moduleArray[48] = this.clickfriend;
        this.esp = new ESP();
        moduleArray[49] = this.esp;
        this.FTHelper = new FTHelper();
        moduleArray[50] = this.FTHelper;
        this.entityBox = new EntityBox();
        moduleArray[51] = this.entityBox;
        this.antiPush = new AntiPush();
        moduleArray[52] = this.antiPush;
        this.freeCam = new FreeCam();
        moduleArray[53] = this.freeCam;
        this.chestStealer = new ChestStealer();
        moduleArray[54] = this.chestStealer;
        this.autoLeave = new AutoLeave();
        moduleArray[55] = this.autoLeave;
        this.fly = new Fly();
        moduleArray[56] = this.fly;
        this.clientTune = new ClientTune();
        moduleArray[57] = this.clientTune;
        this.autoExplosion = new AutoExplosion();
        moduleArray[58] = this.autoExplosion;
        this.antiBot = new AntiBot();
        moduleArray[59] = this.antiBot;
        this.trails = new Trails();
        moduleArray[60] = this.trails;
        this.crosshair = new Crosshair();
        moduleArray[61] = this.crosshair;
        this.autoTotem = new AutoTotem();
        moduleArray[62] = this.autoTotem;
        this.itemCooldown = new ItemCooldown();
        moduleArray[63] = this.itemCooldown;
        this.hitAura = new HitAura(this.autopotion);
        moduleArray[64] = this.hitAura;
        this.clickPearl = new ClickPearl(this.itemCooldown);
        moduleArray[65] = this.clickPearl;
        this.autoSwap = new AutoSwap(this.autoTotem);
        moduleArray[66] = this.autoSwap;
        this.targetStrafe = new TargetStrafe(this.hitAura);
        moduleArray[67] = this.targetStrafe;
        this.strafe = new Strafe(this.targetStrafe, this.hitAura);
        moduleArray[68] = this.strafe;
        this.viewModel = new ViewModel(this.hitAura);
        moduleArray[69] = this.viewModel;
//        this.targetESP = new TargetESP(this.hitAura);
//        moduleArray[70] = this.targetESP;
        this.elytraFly = new ElytraFly();
        moduleArray[71] = this.elytraFly;
        this.chinaHat = new ChinaHat(this.chinaHat);
        moduleArray[72] = this.chinaHat;
        this.snow = new Snow();
        moduleArray[73] = this.snow;
        this.particles = new Particles();
        moduleArray[74] = this.particles;
        this.jumpCircle = new JumpCircle();
        moduleArray[75] = this.jumpCircle;
        this.itemPhysic = new ItemPhysic();
        moduleArray[76] = this.itemPhysic;
        this.predictions = new Predictions();
        moduleArray[77] = this.predictions;
        this.noEntityTrace = new NoEntityTrace();
        moduleArray[78] = this.noEntityTrace;
        this.storageESP = new StorageESP();
        moduleArray[79] = this.storageESP;
        this.spider = new Spider();
        moduleArray[80] = this.spider;
        this.timer = new Timer();
        moduleArray[81] = this.timer;
        this.nameProtect = new NameProtect();
        moduleArray[82] = this.nameProtect;
        this.noInteract = new NoInteract();
        moduleArray[83] = this.noInteract;
        this.tracers = new Tracers();
        moduleArray[84] = this.tracers;
        this.selfDestruct = new SelfDestruct();
        moduleArray[85] = this.selfDestruct;
        this.antiAFK = new AntiAFK();
        moduleArray[86] = this.antiAFK;
        this.betterMinecraft = new BetterMinecraft();
        moduleArray[87] = this.betterMinecraft;
        moduleArray[88] = new Nuker();
        moduleArray[89] = new ItemSwapFix();
        this.backtrack = new Backtrack();
        moduleArray[90] = this.backtrack;
        moduleArray[91] = new LongJump();
        moduleArray[92] = new OreFinder();
        moduleArray[93] = new FreeLook();
        moduleArray[94] = new Parkour();
        moduleArray[95] = new RWHelper();
        this.registerAll(moduleArray);
        this.sortModulesByWidth();
        Expensive.getInstance().getEventBus().register(this);
    }

    private void registerAll(Module ... modules) {
        this.modules.addAll(List.of(modules));
    }

    private void sortModulesByWidth() {
        try {
            this.modules.sort(Comparator.comparingDouble(module -> ClientFonts.msSemiBold[17].getWidth(module.getClass().getName())).reversed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Module> getSorted(Font font, float size) {
        return this.modules.stream().sorted((f1, f2) -> Float.compare(font.getWidth(f2.getName(), size), font.getWidth(f1.getName(), size))).toList();
    }

    public List<Module> get(Category category) {
        return this.modules.stream().filter(module -> module.getCategory() == category).collect(Collectors.toList());
    }

    public int countEnabledModules() {
        int enabledModules = 0;
        for (Module module : this.modules) {
            if (!module.isState()) continue;
            ++enabledModules;
        }
        return enabledModules;
    }

    @Subscribe
    private void onKey(EventKey e) {
        if (SelfDestruct.unhooked) {
            return;
        }
        for (Module Module2 : this.modules) {
            if (Module2.getBind() != e.getKey()) continue;
            Module2.toggle();
        }
    }

    public List<Module> getModules() {
        return this.modules;
    }

    public PlayerHelper getPlayerHelper() {
        return this.playerHelper;
    }

    public NoServerDesync getNoServerDesync() {
        return this.noServerDesync;
    }

    public ViewModel getViewModel() {
        return this.viewModel;
    }

    public HUD getHud() {
        return this.hud;
    }

    public AutoGapple getAutoGapple() {
        return this.autoGapple;
    }

    public AutoSprint getAutoSprint() {
        return this.autoSprint;
    }

    public Velocity getVelocity() {
        return this.velocity;
    }

    public NoRender getNoRender() {
        return this.noRender;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public InventoryPlus getInventoryPlus() {
        return this.inventoryPlus;
    }

    public ElytraHelper getElytrahelper() {
        return this.elytrahelper;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public AutoBuy getAutoBuy() {
        return this.autoBuy;
    }

    public PotionThrower getAutopotion() {
        return this.autopotion;
    }

    public TriggerBot getTriggerbot() {
        return this.triggerbot;
    }

    public ClickFriend getClickfriend() {
        return this.clickfriend;
    }

    public ESP getEsp() {
        return this.esp;
    }

    public FTHelper getFTHelper() {
        return this.FTHelper;
    }

    public ItemCooldown getItemCooldown() {
        return this.itemCooldown;
    }

    public ClickPearl getClickPearl() {
        return this.clickPearl;
    }

    public AutoSwap getAutoSwap() {
        return this.autoSwap;
    }

    public EntityBox getEntityBox() {
        return this.entityBox;
    }

    public AntiPush getAntiPush() {
        return this.antiPush;
    }

    public FreeCam getFreeCam() {
        return this.freeCam;
    }

    public ChestStealer getChestStealer() {
        return this.chestStealer;
    }

    public AutoLeave getAutoLeave() {
        return this.autoLeave;
    }

    public Fly getFly() {
        return this.fly;
    }

    public TargetStrafe getTargetStrafe() {
        return this.targetStrafe;
    }

    public ClientTune getClientTune() {
        return this.clientTune;
    }

    public AutoTotem getAutoTotem() {
        return this.autoTotem;
    }

    public AutoExplosion getAutoExplosion() {
        return this.autoExplosion;
    }

    public HitAura getHitAura() {
        return this.hitAura;
    }

    public AntiBot getAntiBot() {
        return this.antiBot;
    }

    public Trails getTrails() {
        return this.trails;
    }

    public Crosshair getCrosshair() {
        return this.crosshair;
    }

    public DeathEffect getDeathEffect() {
        return this.deathEffect;
    }

    public Strafe getStrafe() {
        return this.strafe;
    }

    public ElytraFly getElytraFly() {
        return this.elytraFly;
    }

    public ChinaHat getChinaHat() {
        return this.chinaHat;
    }

    public Snow getSnow() {
        return this.snow;
    }

    public Particles getParticles() {
        return this.particles;
    }

    public JumpCircle getJumpCircle() {
        return this.jumpCircle;
    }

    public ItemPhysic getItemPhysic() {
        return this.itemPhysic;
    }

    public Predictions getPredictions() {
        return this.predictions;
    }

    public NoEntityTrace getNoEntityTrace() {
        return this.noEntityTrace;
    }

    public NoClip getNoClip() {
        return this.noClip;
    }

    public StorageESP getStorageESP() {
        return this.storageESP;
    }

    public Spider getSpider() {
        return this.spider;
    }

    public NameProtect getNameProtect() {
        return this.nameProtect;
    }

    public NoInteract getNoInteract() {
        return this.noInteract;
    }

    public Tracers getTracers() {
        return this.tracers;
    }

    public SelfDestruct getSelfDestruct() {
        return this.selfDestruct;
    }

    public AntiAFK getAntiAFK() {
        return this.antiAFK;
    }

    public BetterMinecraft getBetterMinecraft() {
        return this.betterMinecraft;
    }

    public Backtrack getBacktrack() {
        return this.backtrack;
    }

    public SeeInvisibles getSeeInvisibles() {
        return this.seeInvisibles;
    }

    public Jesus getJesus() {
        return this.jesus;
    }

    public Speed getSpeed() {
        return this.speed;
    }

    public AirJump getAirJump() {
        return this.airJump;
    }

    public Disabler getDisabler() {
        return this.disabler;
    }

    public WaterSpeed getWaterSpeed() {
        return this.waterSpeed;
    }

    public NoFriendHurt getNoFriendHurt() {
        return this.noFriendHurt;
    }

    public SuperBow getSuperBow() {
        return this.superBow;
    }

    public SafeWalk getSafeWalk() {
        return this.safeWalk;
    }

    public ElytraSpeed getElytraSpeed() {
        return this.elytraSpeed;
    }

    public TPInfluence getTPInfluence() {
        return this.TPInfluence;
    }

    public AutoFix getAutoFix() {
        return this.autoFix;
    }

    public ClickGui getClickGui() {
        return this.clickGui;
    }

    public TNTTimer getTntTimer() {
        return this.tntTimer;
    }

    public Blink getBlink() {
        return this.blink;
    }

    public SlowPackets getSlowPackets() {
        return this.slowPackets;
    }

    public Scaffold getScaffold() {
        return this.scaffold;
    }

    public WorldTweaks getWorldTweaks() {
        return this.worldTweaks;
    }

    public Arrows getArrows() {
        return this.arrows;
    }

    public RWJoiner getRwJoiner() {
        return this.rwJoiner;
    }

    public ChatHelper getChatHelper() {
        return this.chatHelper;
    }

    public CrystalAura getCrystalAura() {
        return this.crystalAura;
    }

    public FakePlayer getFakePlayer() {
        return this.fakePlayer;
    }

    public ElytraBounce getElytraBounce() {
        return this.elytraBounce;
    }

    public Criticals getCriticals() {
        return this.criticals;
    }

    public AutoActions getAutoActions() {
        return this.autoActions;
    }

    public NoFall getNoFall() {
        return this.noFall;
    }

    public FullBright getFullBright() {
        return this.fullBright;
    }

    public TestFeature getTestFeature() {
        return this.testFeature;
    }

    public AutoDuel getAutoDuel() {
        return this.autoDuel;
    }

    public SexAura getSexAura() {
        return this.sexAura;
    }

    public AutoSort getAutoSort() {
        return this.autoSort;
    }

    public MoveHelper getMoveHelper() {
        return this.moveHelper;
    }

    public ElytraBooster getElytraBooster() {
        return this.elytraBooster;
    }
}


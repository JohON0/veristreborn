/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive;

import com.google.common.eventbus.EventBus;
import im.expensive.command.api.AdviceCommandFactoryImpl;
import im.expensive.command.api.ConsoleLogger;
import im.expensive.command.api.MinecraftLogger;
import im.expensive.command.api.MultiLogger;
import im.expensive.command.api.ParametersFactoryImpl;
import im.expensive.command.api.PrefixImpl;
import im.expensive.command.api.StandaloneCommandDispatcher;
import im.expensive.command.feature.BindCommand;
import im.expensive.command.feature.ConfigCommand;
import im.expensive.command.feature.FriendCommand;
import im.expensive.command.feature.GPSCommand;
import im.expensive.command.feature.HClipCommand;
import im.expensive.command.feature.ListCommand;
import im.expensive.command.feature.LoginCommand;
import im.expensive.command.feature.MacroCommand;
import im.expensive.command.feature.MemoryCommand;
import im.expensive.command.feature.ParseCommand;
import im.expensive.command.feature.RCTCommand;
import im.expensive.command.feature.ReportCommand;
import im.expensive.command.feature.StaffCommand;
import im.expensive.command.feature.VClipCommand;
import im.expensive.command.feature.WayCommand;
import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.CommandDispatcher;
import im.expensive.config.AltConfig;
import im.expensive.config.ConfigStorage;
import im.expensive.config.FriendStorage;
import im.expensive.config.MacroManager;
import im.expensive.config.StaffStorage;
import im.expensive.events.EventKey;
import im.expensive.modules.api.Module;
import im.expensive.modules.api.ModuleManager;
import im.expensive.modules.impl.misc.SelfDestruct;
import im.expensive.modules.impl.render.ClickGui;
import im.expensive.ui.autobuy.AutoBuyConfig;
import im.expensive.ui.autobuy.AutoBuyHandler;
import im.expensive.ui.autobuy.api.factory.ItemFactoryImpl;
import im.expensive.ui.autobuy.api.logic.ActivationLogic;
import im.expensive.ui.autobuy.api.model.IItem;
import im.expensive.ui.autobuy.api.model.ItemStorage;
import im.expensive.ui.autobuy.api.render.Window;
import im.expensive.ui.clickgui.DropDown;
import im.expensive.ui.mainmenu.AltScreen;
import im.expensive.ui.notify.NotifyManager;
import im.expensive.ui.oldclickgui.ClickGuiScreen;
import im.expensive.ui.themes.Theme;
import im.expensive.utils.TPSCalc;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.client.ServerTPS;
import im.expensive.utils.drag.DragManager;
import im.expensive.utils.drag.Dragging;
import im.expensive.utils.rotation.FreeLookHandler;
import im.expensive.utils.rotation.RotationHandler;
import im.expensive.utils.text.font.ClientFonts;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import via.ViaMCP;

public class Expensive {
    public static UserData userData;
    public boolean playerOnServer = false;
    public static final String name = "Verist Client [Public Leak]";
    public static final String version = "1.7";
    public static final String build;
    private static Expensive instance;
    private ModuleManager moduleManager;
    private ConfigStorage configStorage;
    private CommandDispatcher commandDispatcher;
    private ServerTPS serverTPS;
    private MacroManager macroManager;
    private final EventBus eventBus = new EventBus();
    private final File clientDir;
    private final File filesDir;
    private AltConfig altConfig;
    private ClickGuiScreen clickGuiScreen;
    private DropDown dropDown;
    private Window autoBuyUI;
    private NotifyManager notifyManager;
    private AutoBuyConfig autoBuyConfig;
    private AutoBuyHandler autoBuyHandler;
    private ViaMCP viaMCP;
    private TPSCalc tpsCalc;
    private ActivationLogic activationLogic;
    private ItemStorage itemStorage;
    private FreeLookHandler freeLookHandler;
    private RotationHandler rotationHandler;
    private AltScreen altScreen;
    private Theme theme;
    private final EventKey eventKey;

    public Expensive() {
        this.clientDir = new File(Minecraft.getInstance().gameDir + "\\saves\\files");
        this.filesDir = new File(Minecraft.getInstance().gameDir + "\\saves\\files\\other");
        this.autoBuyConfig = new AutoBuyConfig();
        this.eventKey = new EventKey(-1);
        instance = this;
        if (!this.clientDir.exists()) {
            this.clientDir.mkdirs();
        }
        if (!this.filesDir.exists()) {
            this.filesDir.mkdirs();
        }
        this.clientLoad();
        FriendStorage.load();
        StaffStorage.load();
        ClientUtil.startRPC();
    }

    public Dragging createDrag(Module module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }

    private void clientLoad() {
        ClientFonts.init();
        this.viaMCP = new ViaMCP();
        this.serverTPS = new ServerTPS();
        this.moduleManager = new ModuleManager();
        this.macroManager = new MacroManager();
        this.configStorage = new ConfigStorage();
        this.moduleManager.init();
        this.notifyManager = new NotifyManager();
        this.notifyManager.init();
        this.initCommands();
        this.altScreen = new AltScreen();
        this.altConfig = new AltConfig();
        this.tpsCalc = new TPSCalc();
        this.theme = new Theme();
        userData = new UserData("Verist", 1);
        try {
            this.autoBuyConfig.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.altConfig.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.configStorage.init();
        } catch (IOException e) {
            System.out.println("\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043f\u043e\u0434\u0433\u0440\u0443\u0437\u043a\u0435 \u043a\u043e\u043d\u0444\u0438\u0433\u0430.");
        }
        try {
            this.macroManager.init();
        } catch (IOException e) {
            System.out.println("\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043f\u043e\u0434\u0433\u0440\u0443\u0437\u043a\u0435 \u043a\u043e\u043d\u0444\u0438\u0433\u0430 \u043c\u0430\u043a\u0440\u043e\u0441\u043e\u0432.");
        }
        DragManager.load();
        this.initAutoBuy();
        this.clickGuiScreen = new ClickGuiScreen(new StringTextComponent(""));
        this.dropDown = new DropDown(new StringTextComponent(""));
        this.autoBuyUI = new Window(new StringTextComponent(""), this.itemStorage);
        this.autoBuyHandler = new AutoBuyHandler();
        this.autoBuyConfig = new AutoBuyConfig();
        this.freeLookHandler = new FreeLookHandler();
        this.rotationHandler = new RotationHandler();
        this.eventBus.register(this);
    }

    public String randomNickname() {
        String[] names = new String[]{"Verist", "Femboy", "RussianPirat", "Ladoga", "ny7oBKa", "IIIuPuHKa", "DataBase", "KoTuK", "nayk", "nykaH", "nykaJLKa", "IIIa7oMeP", "Ohtani", "Tango", "HardStyle", "GoToSleep", "Movietone", "7aIIIuK", "TpyCuKu", "TheKnight", "OnlySprint", "Press_W", "HowToWASD", "BloodFlow", "CutVeins", "Im_A_Cyber", "NextTime", "Killer", "Murauder", "AntiPetuh", "CMeTaHKa", "Enigma", "Doctor", "TheGhost", "GhostRunner", "Banana", "Ba3eJLuH", "MaCTyp6eK", "BaHTy3", "AliExpress", "Agressor", "Spasm", "SHAMAN", "optimist", "", "Banker", "JahMachine", "Cu7aPa", "nuBo", "CuM6uoT", "Venom", "Superman", "Supreme", "CeKcU_6ou", "SuperSpeed", "KnuckKnuck", "6o7aTbIPb", "SouthPark", "Simpson", "IIIaJLaIII", "3_Penetrate", "EmptySoul", "Firefly", "PlusTopka", "TryMe", "YouAreWeak", "MegaSexual", "Pikachu", "Pupsik", "Legenda", "SCP", "MyNumber", "YourToy", "SexShop", "Slayer", "Murderer", "CallMe", "PvpTeacher", "CrazyEva", "4ynuK", "6aToH", "LongPenis", "Caxap", "Infernal", "Rerilo", "Remula", "Rarlin", "Devo4ka", "SexySister", "NakedBody", "PlusZ4", "ThiefInLaw", "StrongTea", "BlackTea", "SmallAss", "SmallBoobs", "CoffeeDEV", "FireRider", "MilkyWay", "PeacefulBoy", "Lambada", "MagicSpeed", "ThrowMom", "StopPlay", "KillMother", "XDeadForGay", "ALTf4", "HowAreYou", "GoSex", "Falas", "Sediment", "OpenDoor", "ShitInTrap", "SuckItUp", "NeuroNET", "BunnyHop", "BmxSport", "GiveCoord", "eHoTuK", "KucKa", "3auKa", "4aIIIa", "HykaHax", "Sweet", "MoHuTop", "Me7aMa4o", "Miner", "BonAqua", "COK", "BANK", "Lucky", "SPECTATE", "7OBHO", "MyXA", "Owner", "5opka", "JUK", "FaceBreak", "SnapBody", "Psycho", "EasyWin", "SoHard", "Panties", "SoloGame", "Robot", "Surgeon", "_IMBA", "ShakeMe", "EnterMe", "GoAway", "TRUE", "while", "Pinky", "Pickup", "Stack", "GL11", "GLSL", "Garbage", "NoBanMe", "WiFi", "Tally", "Dream", "Mommy", "6aTya", "Pivovar", "Alkash", "Gangsta", "Counter", "Clitor", "HentaUser", "BrowseHent", "LoadChunk", "Panical", "Kakashka", "MinusVse", "Pavlik", "RusskiPirat", "GoodTrip", "6A6KA", "3000IQ", "0IQ", "REMILO", "YOUR_BOSS", "CPacketGay", "4Bytes", "SinCos", "Yogurt", "SexInTrash", "TrashMyHome", "PenisUser", "VaginaLine", "VagLine", "Virginia", "NoReportMe", "Bluetouth", "PivoBak", "6AKJLAXA", "Opostrof", "Harming", "Cauldron", "Dripka", "Wurdulak", "Presedent", "Opstol", "Oposum", "Babayka", "O4KAPUK", "Dunozavr", "Cocacola", "Fantazy", "70JLA9I", "PedalKTLeave", "TolstoLobik", "nePDyH", "HABO3HUK", "KOT", "CKOT", "BISHOP", "4ukeH", "nanaxa", "Berkyt", "Matreshka", "HACBAU", "XAPEK", "Mopedik", "CKELET2013", "GodDrakona", "CoLHbiIIIKo", "HA77ETC", "PoM6uK", "PomaH", "6oM6UJLa", "MOH7OJl", "OutOfMemory", "PopkaDurak", "4nokVPupok", "Pinality", "Shaverma", "MOJLUCb", "MOJLuTBA", "CTEHA", "CKAJLA", "JohnWeak", "Plomba", "neKaPb", "Disconnect", "Oriflame", "Mojang", "TPPPPP", "EvilBoy", "DavaiEbaca", "TuMeuT", "Tapan", "600K7Puzo", "Poctelecom", "Interzet", "C_6oDUHA", "6yHTaPb", "Milka", "KOLBASA", "OhNo", "YesTea", "Mistik", "KuHDep", "Smippy", "Diamond", "KedpOBuK", "Lolikill", "CrazyGirl", "Kronos", "Kruasan", "MrProper", "HellCat", "Nameless", "Viper", "GamerYT", "slimer", "MrSlender", "Gromila", "BoomBoom", "Doshik", "BananaKenT", "NeonPlay", "Progibator", "Rubak", "MrMurzik", "Kenda", "DrShine", "cnacu6o", "Eclips", "ShadowFuse", "MrRem", "Bacardi", "UwU_FUF", "Exont", "Persik", "Mambl", "Rossamaha", "DrKraken", "MeWormy", "WaterBomb", "YourStarix", "nakeTuk", "Massik", "MineFOX", "BitCoin", "Avocado", "Chappi", "ECEQO", "Fondy", "StableFace", "JeBobby", "KrytoyKaka", "MagHyCEp", "I7evice", "LeSoulles", "EmptySoul", "KOMnOT", "MrPlay", "NGROK2009", "NoProblem", "MrPatric", "OkugAmas", "YaBuilder", "A7EHT007", "PussyGirl", "Triavan", "TyCoBKa", "UnsafeINV", "", "yKcyc_UFO", "Wendy", "Bendy", "XAOC", "ST6yP7", "XYNECI", "HENTAI", "YoutDaddy", "YouGurT", "EnumFacing", "HitVec3d", "JavaGypy", "VIWEBY", "ZamyPlay", "SUSUKI", "KPAX_TRAX", "Emiron", "UzeXilo", "Rembal", "Gejmer", "EvoNeon", "MrFazen", "ESHKERE", "FARADENZA", "EarWarm", "CMA3KA", "NaVi4oK", "A4_OMG", "YCYSAPO", "Booster", "BroDaga", "CastlePlay29", "DYWAHY", "Emirhan", "BezPontov", "Xilece", "Gigabait", "Griefer", "Goliaf", "Fallaut", "HERODOT", "KingKong", "NADOBNO", "ODIZIT", "Klawdy", "NCRurtler", "Fixik", "FINISHIST", "KPACOTA", "GlintEff", "Flexer", "NeverMore", "BludShaper", "PoSaN4Ik", "Goblin", "Aligator", "Zmeyka", "FieFoxe", "Homilion", "Locator", "kranpus", "HOLSON", "CocyD_ADA", "Anarant", "O6pUKoc", "MissTitan", "JellyKOT", "JellyCat", "LolGgYT", "MapTiNi", "GazVax", "Foxx4ever", "NaGiBaToP", "whiteKing", "KitKat", "VkEdAx", "Pro100Hy6", "Contex", "Durex", "Mr_Golem", "Moonlight", "CoolBoy", "6oTaH", "CaHa6uC", "MuJLaIIIKa", "AvtoBus", "ABOBA", "KanaTuK", "TpanoFob", "CAPSLOCK", "Sonic", "SONIK", "COHUK", "Tailss", "TAILSS", "TauJLC", "Ehidna", "exudHa", "Naklz", "HaKJL3", "coHuk", "parebuh", "nape6yx", "TEPOPNCT", "TPEHEP", "6OKCEP", "KARATE_TYAN", "Astolfo", "Itsuki", "Yotsuba", "Succub", "CyKKy6", "MuJLaIIIKa", "Chappie", "LeraPala4", "MegaSonic", "ME7A_COHUK", "SonicEzh", "IIaPe6yx", "Flamingo", "Pavlin", "VenusShop", "PinkRabbit", "EpicSonic", "EpicTailss", "Genius", "Valkiria"};
        String[] titles = new String[]{"DADA", "YA", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "SUS", "SSS", "TAM", "TyT", "TaM", "Ok", "Pon", "LoL", "CHO", "4oo", "MaM", "Top", "PvP", "PVH", "DIK", "KAK", "SUN", "SIN", "COS", "FIT", "FAT", "HA", "AHH", "OHH", "UwU", "DA", "NaN", "RAP", "WoW", "SHO", "KA4", "Ka4", "AgA", "Fov", "LoVe", "TAN", "Mia", "Alt", "4el", "bot", "GlO", "Sir", "IO", "EX", "Mac", "Win", "Lin", "AC", "Bro", "6po", "6PO", "BRO", "mXn", "XiL", "TGN", "24", "228", "1337", "1488", "007", "001", "999", "333", "666", "111", "FBI", "FBR", "FuN", "FUN", "UFO", "OLD", "Old", "New", "OFF", "ON", "YES", "LIS", "NEO", "BAN", "OwO", "0_o", "0_0", "o_0", "IQ", "99K", "AK47", "SOS", "S0S", "SoS", "z0z", "zOz", "Zzz", "zzz", "ZZZ", "6y", "BU", "RAK", "PAK", "Pak", "MeM", "MoM", "M0M", "KAK", "TAK", "n0H", "BOSS", "RU", "ENG", "BAF", "BAD", "ZED", "oy", "Oy", "0y", "Big", "Air", "Dirt", "Dog", "CaT", "CAT", "KOT", "EYE", "CAN", "ToY", "ONE", "OIL", "HOT", "HoT", "VPN", "BnH", "Ty3", "GUN", "HZ", "XZ", "XYZ", "HZ", "XyZ", "HIS", "HER", "DOC", "COM", "DIS", "TOP", "1ST", "1st", "LORD", "DED", "ded", "HAK", "FUF", "IQQ", "KBH", "KVN", "HuH", "WWW", "RUN", "RuN", "run", "PRO", "100", "300", "3OO", "RAM", "DIR", "Yaw", "YAW", "TIP", "Tun", "Ton", "Tom", "Your", "AM", "FM", "YT", "yt", "Yt", "yT", "RUS", "KON", "FAK", "FUL", "RIL", "pul", "RW", "MST", "MEN", "MAN", "NO0", "SEX", "H2O", "H20", "LyT", "3000", "01", "KEK", "PUK", "nuk", "nyk", "nyK", "191", "192", "32O", "5OO", "320", "500", "777", "720", "480", "48O", "HUK", "BUS", "LUN", "LyH", "Fuu", "LaN", "LAN", "DIC", "HAA", "NON", "FAP", "4AK", "4on", "4EK", "4eK", "NVM", "BOG", "RIP", "SON", "XXL", "XXX", "GIT", "GAD", "8GB", "5G", "4G", "3G", "2G", "TX", "GTX", "RTX", "HOP", "TIR", "ufo", "MIR", "MAG", "ALI", "BOB", "GRO", "GOT", "ME", "SO", "Ay4", "MSK", "MCK", "RAY", "Verist", "EvA", "DEL", "ADD", "UP", "VK", "LOV", "AND", "AVG", "EGO", "YTY", "YoY", "I_I", "G_G", "D_D", "V_V", "F", "FF", "FFF", "LCM", "PCM", "CPS", "FPS", "GO", "G0", "70", "7UP", "JAZ", "GAZ", "7A3", "UFA", "HIT", "DAY", "DaY", "S00", "SCP", "FUK", "SIL", "COK", "SOK", "WAT", "WHO", "PUP", "PuP", "Py", "CPy", "SRU", "OII", "IO", "IS", "THE", "SHE", "nuc", "KXN", "VAL", "MIS", "HXI", "HI", "ByE", "WEB", "TNT", "BEE", "4CB", "III", "IVI", "POP", "C4", "BRUH", "Myp", "MyP", "NET", "CAR", "PET", "POV", "POG", "OKK", "ESP", "GOP", "G0P", "7on", "E6y", "BIT", "PIX", "AYE", "Aye", "PVP", "GAS", "REK", "rek", "PEK", "n0H", "RGB"};
        String name = names[(int)(((float)names.length - 1.0f) * (float)Math.random() * (((float)names.length - 1.0f) / (float)names.length))];
        String title = titles[(int)(((float)titles.length - 1.0f) * (float)Math.random() * (((float)titles.length - 1.0f) / (float)titles.length))];
        int size = (name + "_").length();
        return name + "_" + (16 - size == 0 ? "" : title);
    }

    public void onKeyPressed(int key) {
        this.moduleManager.getSelfDestruct();
        if (SelfDestruct.unhooked) {
            return;
        }
        this.eventKey.setKey(key);
        this.eventBus.post(this.eventKey);
        this.macroManager.onKeyPressed(key);
        if (key == (Integer)ClickGui.bind.get()) {
            Minecraft.getInstance().displayGuiScreen(this.dropDown);
        }
        if (key == (Integer)this.getModuleManager().getAutoBuy().setting.get()) {
            Minecraft.getInstance().displayGuiScreen(this.autoBuyUI);
        }
    }

    private void initAutoBuy() {
        ItemFactoryImpl itemFactory = new ItemFactoryImpl();
        CopyOnWriteArrayList<IItem> items = new CopyOnWriteArrayList<IItem>();
        this.itemStorage = new ItemStorage(items, itemFactory);
        this.activationLogic = new ActivationLogic(this.itemStorage, this.eventBus);
    }

    private void initCommands() {
        Minecraft mc = Minecraft.getInstance();
        MultiLogger logger = new MultiLogger(List.of());
        ArrayList<Command> commands = new ArrayList<Command>();
        PrefixImpl prefix = new PrefixImpl();
        commands.add(new ListCommand(commands, logger));
        commands.add(new FriendCommand(prefix, logger, mc));
        commands.add(new BindCommand(prefix, logger));
        commands.add(new GPSCommand(prefix, logger));
        commands.add(new WayCommand(prefix, logger));
        commands.add(new ConfigCommand(this.configStorage, prefix, logger));
        commands.add(new MacroCommand(this.macroManager, prefix, logger));
        commands.add(new VClipCommand(prefix, logger, mc));
        commands.add(new HClipCommand(prefix, logger, mc));
        commands.add(new StaffCommand(prefix, logger));
        commands.add(new MemoryCommand(logger));
        commands.add(new RCTCommand(logger, mc));
        commands.add(new ParseCommand(prefix, logger));
        commands.add(new LoginCommand(prefix, logger, mc));
        commands.add(new ReportCommand(logger));
        AdviceCommandFactoryImpl adviceCommandFactory = new AdviceCommandFactoryImpl(logger);
        ParametersFactoryImpl parametersFactory = new ParametersFactoryImpl();
        this.commandDispatcher = new StandaloneCommandDispatcher(commands, adviceCommandFactory, prefix, parametersFactory, logger);
    }

    public boolean isPlayerOnServer() {
        return this.playerOnServer;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public ConfigStorage getConfigStorage() {
        return this.configStorage;
    }

    public CommandDispatcher getCommandDispatcher() {
        return this.commandDispatcher;
    }

    public ServerTPS getServerTPS() {
        return this.serverTPS;
    }

    public MacroManager getMacroManager() {
        return this.macroManager;
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }

    public File getClientDir() {
        return this.clientDir;
    }

    public File getFilesDir() {
        return this.filesDir;
    }

    public AltConfig getAltConfig() {
        return this.altConfig;
    }

    public ClickGuiScreen getClickGuiScreen() {
        return this.clickGuiScreen;
    }

    public DropDown getDropDown() {
        return this.dropDown;
    }

    public Window getAutoBuyUI() {
        return this.autoBuyUI;
    }

    public NotifyManager getNotifyManager() {
        return this.notifyManager;
    }

    public AutoBuyConfig getAutoBuyConfig() {
        return this.autoBuyConfig;
    }

    public AutoBuyHandler getAutoBuyHandler() {
        return this.autoBuyHandler;
    }

    public ViaMCP getViaMCP() {
        return this.viaMCP;
    }

    public TPSCalc getTpsCalc() {
        return this.tpsCalc;
    }

    public ActivationLogic getActivationLogic() {
        return this.activationLogic;
    }

    public ItemStorage getItemStorage() {
        return this.itemStorage;
    }

    public FreeLookHandler getFreeLookHandler() {
        return this.freeLookHandler;
    }

    public RotationHandler getRotationHandler() {
        return this.rotationHandler;
    }

    public AltScreen getAltScreen() {
        return this.altScreen;
    }

    public Theme getTheme() {
        return this.theme;
    }

    public EventKey getEventKey() {
        return this.eventKey;
    }

    public static Expensive getInstance() {
        return instance;
    }

    static {
        build = "0" + version.replace(".", "") + "R";
    }

    public static class UserData {
        final String user;
        final int uid;

        public String getUser() {
            return this.user;
        }

        public int getUid() {
            return this.uid;
        }

        public UserData(String user, int uid) {
            this.user = user;
            this.uid = uid;
        }
    }
}


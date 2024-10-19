/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.text.font;

import im.expensive.utils.text.font.common.Lang;
import im.expensive.utils.text.font.styled.StyledFont;

public class ClientFonts {
    public static final String FONT_DIR = "/assets/minecraft/eva/fonts/normal/";
    public static volatile StyledFont[] msBold = new StyledFont[50];
    public static volatile StyledFont[] msMedium = new StyledFont[50];
    public static volatile StyledFont[] msLight = new StyledFont[50];
    public static volatile StyledFont[] msRegular = new StyledFont[50];
    public static volatile StyledFont[] msSemiBold = new StyledFont[50];
    public static volatile StyledFont[] roadRage = new StyledFont[50];
    public static volatile StyledFont[] small_pixel = new StyledFont[50];
    public static volatile StyledFont[] tech = new StyledFont[50];
    public static volatile StyledFont[] icon = new StyledFont[50];
    public static volatile StyledFont[] icons_wex = new StyledFont[50];
    public static volatile StyledFont[] icons_nur = new StyledFont[50];
    public static volatile StyledFont[] comfortaa = new StyledFont[50];
    public static volatile StyledFont[] interBold = new StyledFont[80];
    public static volatile StyledFont[] interMedium = new StyledFont[80];
    public static volatile StyledFont[] interRegular = new StyledFont[80];
    public static volatile StyledFont[] interSemiBold = new StyledFont[80];

    public static void init() {
        int i;
        for (i = 8; i < 50; ++i) {
            ClientFonts.msBold[i] = new StyledFont("Montserrat-Bold.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 50; ++i) {
            ClientFonts.msLight[i] = new StyledFont("Montserrat-Light.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 50; ++i) {
            ClientFonts.msMedium[i] = new StyledFont("Montserrat-Medium.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 50; ++i) {
            ClientFonts.msRegular[i] = new StyledFont("Montserrat-Regular.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 50; ++i) {
            ClientFonts.msSemiBold[i] = new StyledFont("Montserrat-SemiBold.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 50; ++i) {
            ClientFonts.roadRage[i] = new StyledFont("roadrage.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 50; ++i) {
            ClientFonts.small_pixel[i] = new StyledFont("small_pixel.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 50; ++i) {
            ClientFonts.tech[i] = new StyledFont("tech.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 50; ++i) {
            ClientFonts.icon[i] = new StyledFont("icon.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 50; ++i) {
            ClientFonts.icons_wex[i] = new StyledFont("iconswex.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 50; ++i) {
            ClientFonts.icons_nur[i] = new StyledFont("iconsnur.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 50; ++i) {
            ClientFonts.comfortaa[i] = new StyledFont("comfortaa-regular.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 80; ++i) {
            ClientFonts.interRegular[i] = new StyledFont("inter_regular.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 80; ++i) {
            ClientFonts.interMedium[i] = new StyledFont("inter_medium.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 80; ++i) {
            ClientFonts.interSemiBold[i] = new StyledFont("inter_semibold.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (i = 8; i < 80; ++i) {
            ClientFonts.interBold[i] = new StyledFont("inter_bold.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
    }
}


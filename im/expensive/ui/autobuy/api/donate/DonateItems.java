/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.ui.autobuy.api.donate;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class DonateItems {
    public static ArrayList<ItemStack> donitem = new ArrayList();

    public static void add() {
        donitem.add(DonateItems.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRiNWNlMGQ0NGMzZTgxMzhkYzJlN2U1MmMyODk3YmI4NzhlMWRiYzIyMGQ3MDY4OWM3YjZiMThkMzE3NWUwZiJ9fX0=", "\u0421\u0444\u0435\u0440\u0430 \u041c\u0430\u0433\u043c\u044b"));
        donitem.add(DonateItems.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIwMWFlMWE4YTA0ZGY1MjY1NmY1ZTQ4MTNlMWZiY2Y5Nzg3N2RiYmZiYzQyNjhkMDQzMTZkNmY5Zjc1MyJ9fX0=", "\u0421\u0444\u0435\u0440\u0430 \u0422\u0435\u0443\u0440\u0433\u0435\u044f"));
        donitem.add(DonateItems.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RmZDViZjFmZjA1NDMxNDdjOWQ2NGU2ODc2MWRiNmU0YjcxMzJhYzY1OGYwYjhmNzk4MzFmYWQ5YzI4OWVjYSJ9fX0=", "\u0421\u0444\u0435\u0440\u0430 \u041f\u0430\u043d\u0430\u043a\u0435\u044f"));
        donitem.add(DonateItems.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTY2MzZiYTY5ODhjZTliNDBkZGM3NDlhMDljZTBmYjkzOWFmNTI2MDA1OTk1YzE4ZDMyM2FjOTY2MjVmMGQ2ZCJ9fX0=", "\u0421\u0444\u0435\u0440\u0430 \u0424\u0438\u043b\u043e\u043d\u0430"));
        donitem.add(DonateItems.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNmOWVlZGEzYmEyM2ZlMTQyM2M0MDM2ZTdkZDBhNzQ0NjFkZmY5NmJhZGM1YjJmMmI5ZmFhN2NjMTZmMzgyZiJ9fX0=", "\u0421\u0444\u0435\u0440\u0430 \u0410\u0444\u0438\u043d\u0430"));
        donitem.add(DonateItems.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY3OTliZmFhM2EyYzYzYWQ4NWRkMzc4ZTY2ZDU3ZDlhOTdhM2Y4NmQwZDlmNjgzYzQ5ODYzMmY0ZjVjIn19fQ=", "\u0421\u0444\u0435\u0440\u0430 \u0421\u043e\u0440\u0430\u043d\u0430"));
        donitem.add(DonateItems.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgyMjAyODJmMmVlNTk5NTExYjRmYzc0NjExMWM5NzM2ZDdiNDkxZThiY2ZiNjQ4YThhMTU2MjkyODFlZTUifX19=", "\u0421\u0444\u0435\u0440\u0430 \u042d\u043f\u0438\u043e\u043d\u0430"));
        donitem.add(DonateItems.add("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFkYzRhMDI0NzE4ZDQwMWVlYWU5ZTk1YjNjOTI3NjdmOTE2ZjMyM2M5ZTgzNjQ5YWQxNWM5MjY1ZWU1MDkyZiJ9fX0=", "\u0421\u0444\u0435\u0440\u0430 \u0418\u0430\u0441\u043e"));
        donitem.add(DonateItems.add("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQxNDQ5MDk3YjRiNzlhOWY2Y2FmNjM0NDQxOGYyMDM0ZGU0YmI5NzFmZWI3YThlNGFhY2JmYjkwNWFjZGNlZiJ9fX0=", "\u0421\u0444\u0435\u0440\u0430 \u0410\u0431\u0430\u043d\u0442\u044b"));
        donitem.add(DonateItems.add("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNkMTQ1NjFiYmQwNjNmNzA0MjRhOGFmY2MzN2JmZTljNzQ1NjJlYTM2ZjdiZmEzZjIzMjA2ODMwYzY0ZmFmMSJ9fX0=", "\u0421\u0444\u0435\u0440\u0430 \u0421\u043a\u0438\u0444\u0430"));
        ItemStack talfugu = new ItemStack(Items.TOTEM_OF_UNDYING);
        talfugu.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d \u0424\u0443\u0433\u0443"));
        ItemStack talegida = new ItemStack(Items.TOTEM_OF_UNDYING);
        talegida.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d \u042d\u0433\u0438\u0434\u0430"));
        ItemStack talkraita = new ItemStack(Items.TOTEM_OF_UNDYING);
        talkraita.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d \u041a\u0440\u0430\u0439\u0442\u0430"));
        ItemStack talmedic = new ItemStack(Items.TOTEM_OF_UNDYING);
        talmedic.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d \u041b\u0435\u043a\u0430\u0440\u044f"));
        ItemStack talmanesa = new ItemStack(Items.TOTEM_OF_UNDYING);
        talmanesa.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d \u041c\u0430\u043d\u0435\u0441\u0430"));
        ItemStack talkobra = new ItemStack(Items.TOTEM_OF_UNDYING);
        talkobra.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d \u041a\u043e\u0431\u0440\u044b"));
        ItemStack taldionisa = new ItemStack(Items.TOTEM_OF_UNDYING);
        taldionisa.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d \u0414\u0438\u043e\u043d\u0438\u0441\u0430"));
        ItemStack talgefesta = new ItemStack(Items.TOTEM_OF_UNDYING);
        talgefesta.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d \u0413\u0435\u0444\u0435\u0441\u0442\u0430"));
        ItemStack talhauberka = new ItemStack(Items.TOTEM_OF_UNDYING);
        talhauberka.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d \u0425\u0430\u0443\u0431\u0435\u0440\u043a\u0430"));
        ItemStack talkrush = new ItemStack(Items.TOTEM_OF_UNDYING);
        talkrush.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d \u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044f"));
        ItemStack desorientationItem = new ItemStack(Items.ENDER_EYE);
        desorientationItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f"));
        ItemStack crusherSwordItem = new ItemStack(Items.NETHERITE_SWORD);
        crusherSwordItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041c\u0435\u0447 \u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044f"));
        ItemStack katanaItem = new ItemStack(Items.NETHERITE_SWORD);
        katanaItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041a\u0430\u0442\u0430\u043d\u0430"));
        ItemStack satansSwordItem = new ItemStack(Items.NETHERITE_SWORD);
        satansSwordItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041c\u0435\u0447 \u0421\u0430\u0442\u0430\u043d\u044b"));
        ItemStack plastItem = new ItemStack(Items.DRIED_KELP);
        plastItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041f\u043b\u0430\u0441\u0442"));
        ItemStack obviousDustItem = new ItemStack(Items.SUGAR);
        obviousDustItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c"));
        ItemStack tridentCrusherItem = new ItemStack(Items.TRIDENT);
        tridentCrusherItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0440\u0435\u0437\u0443\u0431\u0435\u0446 \u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044f"));
        ItemStack crusherBowItem = new ItemStack(Items.BOW);
        crusherBowItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041b\u0443\u043a \u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044f"));
        ItemStack satansBowItem = new ItemStack(Items.BOW);
        satansBowItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041b\u0443\u043a \u0421\u0430\u0442\u0430\u043d\u044b"));
        ItemStack phantomBowItem = new ItemStack(Items.BOW);
        phantomBowItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041b\u0443\u043a \u0424\u0430\u043d\u0442\u043e\u043c\u0430"));
        ItemStack crossbowCrusherItem = new ItemStack(Items.CROSSBOW);
        crossbowCrusherItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0410\u0440\u0431\u0430\u043b\u0435\u0442 \u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044f"));
        ItemStack trapItem = new ItemStack(Items.NETHERITE_SCRAP);
        trapItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u0440\u0430\u043f\u043a\u0430"));
        ItemStack newYearPlastItem = new ItemStack(Items.DRIED_KELP);
        newYearPlastItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041d\u043e\u0432\u043e\u0433\u043e\u0434\u043d\u0438\u0439 \u041f\u043b\u0430\u0441\u0442"));
        ItemStack freezingSnowballItem = new ItemStack(Items.SNOWBALL);
        freezingSnowballItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0421\u043d\u0435\u0436\u043e\u043a \u0437\u0430\u043c\u043e\u0440\u043e\u0437\u043a\u0430"));
        ItemStack newYearTrapItem = new ItemStack(Items.NETHERITE_SCRAP);
        newYearTrapItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041d\u043e\u0432\u043e\u0433\u043e\u0434\u043d\u044f\u044f \u0422\u0440\u0430\u043f\u043a\u0430"));
        ItemStack satansHelmetItem = new ItemStack(Items.NETHERITE_HELMET);
        satansHelmetItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0428\u043b\u0435\u043c \u0421\u0430\u0442\u0430\u043d\u044b"));
        ItemStack crusherHelmetItem = new ItemStack(Items.NETHERITE_HELMET);
        crusherHelmetItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0428\u043b\u0435\u043c \u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044f"));
        ItemStack satansChestplateItem = new ItemStack(Items.NETHERITE_CHESTPLATE);
        satansChestplateItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a \u0421\u0430\u0442\u0430\u043d\u044b"));
        ItemStack crusherChestplateItem = new ItemStack(Items.NETHERITE_CHESTPLATE);
        crusherChestplateItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a \u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044f"));
        ItemStack satansLeggingsItem = new ItemStack(Items.NETHERITE_LEGGINGS);
        satansLeggingsItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041f\u043e\u043d\u043e\u0436\u0438 \u0421\u0430\u0442\u0430\u043d\u044b"));
        ItemStack crusherLeggingsItem = new ItemStack(Items.NETHERITE_LEGGINGS);
        crusherLeggingsItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041f\u043e\u043d\u043e\u0436\u0438 \u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044f"));
        ItemStack satansBootsItem = new ItemStack(Items.NETHERITE_BOOTS);
        satansBootsItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0411\u043e\u0442\u0438\u043d\u043a\u0438 \u0421\u0430\u0442\u0430\u043d\u044b"));
        ItemStack crusherBootsItem = new ItemStack(Items.NETHERITE_BOOTS);
        crusherBootsItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0411\u043e\u0442\u0438\u043d\u043a\u0438 \u041a\u0440\u0443\u0448\u0438\u0442\u0435\u043b\u044f"));
        ItemStack devilArrowItem = new ItemStack(Items.ARROW);
        devilArrowItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0414\u044c\u044f\u0432\u043e\u043b\u044c\u0441\u043a\u0430\u044f \u0441\u0442\u0440\u0435\u043b\u0430"));
        ItemStack sharpArrowItem = new ItemStack(Items.ARROW);
        sharpArrowItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0422\u043e\u0447\u0435\u043d\u0430\u044f \u0441\u0442\u0440\u0435\u043b\u0430"));
        ItemStack paranoiaArrowItem = new ItemStack(Items.ARROW);
        paranoiaArrowItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0421\u0442\u0440\u0435\u043b\u0430 \u043f\u0430\u0440\u0430\u043d\u043e\u0439\u0438"));
        ItemStack jesterArrowItem = new ItemStack(Items.ARROW);
        jesterArrowItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0421\u0442\u0440\u0435\u043b\u0430 \u0414\u0436\u0430\u0441\u0442\u0435\u0440\u0430"));
        ItemStack icyArrowItem = new ItemStack(Items.ARROW);
        icyArrowItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041b\u0435\u0434\u044f\u043d\u0430\u044f \u0441\u0442\u0440\u0435\u043b\u0430"));
        ItemStack poisonousArrowItem = new ItemStack(Items.ARROW);
        poisonousArrowItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u042f\u0434\u043e\u0432\u0438\u0442\u0430\u044f \u0441\u0442\u0440\u0435\u043b\u0430"));
        ItemStack cursedArrowItem = new ItemStack(Items.ARROW);
        cursedArrowItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041f\u0440\u043e\u043a\u043b\u044f\u0442\u0430\u044f \u0441\u0442\u0440\u0435\u043b\u0430"));
        ItemStack potionOfStrengthItem = new ItemStack(Items.POTION);
        potionOfStrengthItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u0441\u0438\u043b\u044b"));
        ItemStack potionOfInvisibilityItem = new ItemStack(Items.POTION);
        potionOfInvisibilityItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u043d\u0435\u0432\u0438\u0434\u0438\u043c\u043e\u0441\u0442\u0438"));
        ItemStack potionOfSwiftnessItem = new ItemStack(Items.POTION);
        potionOfSwiftnessItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u0438"));
        ItemStack potionOfLeapingItem = new ItemStack(Items.POTION);
        potionOfLeapingItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u043f\u0440\u044b\u0433\u0443\u0447\u0435\u0441\u0442\u0438"));
        ItemStack potionOfRegenerationItem = new ItemStack(Items.POTION);
        potionOfRegenerationItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u0440\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438"));
        ItemStack nightVisionPotionItem = new ItemStack(Items.POTION);
        nightVisionPotionItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u043d\u043e\u0447\u043d\u043e\u0433\u043e \u0437\u0440\u0435\u043d\u0438\u044f"));
        ItemStack fireResistancePotionItem = new ItemStack(Items.POTION);
        fireResistancePotionItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u043e\u0433\u043d\u0435\u0441\u0442\u043e\u0439\u043a\u043e\u0441\u0442\u0438"));
        ItemStack waterBreathingPotionItem = new ItemStack(Items.POTION);
        waterBreathingPotionItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u0432\u043e\u0434\u043d\u043e\u0433\u043e \u0434\u044b\u0445\u0430\u043d\u0438\u044f"));
        ItemStack flashPotionItem = new ItemStack(Items.SPLASH_POTION);
        flashPotionItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u041c\u043e\u0447\u0430 \u0444\u043b\u0435\u0448\u0430"));
        ItemStack medicPotionItem = new ItemStack(Items.SPLASH_POTION);
        medicPotionItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u041c\u0435\u0434\u0438\u043a\u0430"));
        ItemStack agentPotionItem = new ItemStack(Items.SPLASH_POTION);
        agentPotionItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u0410\u0433\u0435\u043d\u0442\u0430"));
        ItemStack winnerPotionItem = new ItemStack(Items.SPLASH_POTION);
        winnerPotionItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044f"));
        ItemStack killerPotionItem = new ItemStack(Items.SPLASH_POTION);
        killerPotionItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u041a\u0438\u043b\u043b\u0435\u0440\u0430"));
        ItemStack burpPotionItem = new ItemStack(Items.SPLASH_POTION);
        burpPotionItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0417\u0435\u043b\u044c\u0435 \u041e\u0442\u0440\u044b\u0436\u043a\u0438"));
        ItemStack sulfuricAcidItem = new ItemStack(Items.SPLASH_POTION);
        sulfuricAcidItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0421\u0435\u0440\u043d\u0430\u044f \u043a\u0438\u0441\u043b\u043e\u0442\u0430"));
        ItemStack flashItem = new ItemStack(Items.SPLASH_POTION);
        flashItem.setDisplayName(ITextComponent.getTextComponentOrEmpty("\u0412\u0441\u043f\u044b\u0448\u043a\u0430"));
        donitem.addAll(List.of((Object[])new ItemStack[]{desorientationItem, crusherSwordItem, katanaItem, satansSwordItem, plastItem, obviousDustItem, tridentCrusherItem, crusherBowItem, satansBowItem, phantomBowItem, crossbowCrusherItem, trapItem, newYearPlastItem, freezingSnowballItem, newYearTrapItem, satansHelmetItem, crusherHelmetItem, satansChestplateItem, crusherChestplateItem, satansLeggingsItem, crusherLeggingsItem, satansBootsItem, crusherBootsItem, devilArrowItem, sharpArrowItem, paranoiaArrowItem, jesterArrowItem, icyArrowItem, poisonousArrowItem, cursedArrowItem, potionOfStrengthItem, potionOfInvisibilityItem, potionOfSwiftnessItem, potionOfLeapingItem, potionOfRegenerationItem, nightVisionPotionItem, fireResistancePotionItem, waterBreathingPotionItem, flashPotionItem, medicPotionItem, agentPotionItem, winnerPotionItem, killerPotionItem, burpPotionItem, sulfuricAcidItem, flashItem, taldionisa, talegida, talfugu, talgefesta, talhauberka, talkobra, talkraita, talkrush, talmanesa, talmedic}));
        HashMap<Enchantment, Integer> fake = new HashMap<Enchantment, Integer>();
        fake.put(Enchantments.UNBREAKING, 0);
        for (ItemStack s : donitem) {
            EnchantmentHelper.setEnchantments(fake, s);
        }
    }

    public static ItemStack add(String texture, String name) {
        try {
            ItemStack magma = new ItemStack(Items.PLAYER_HEAD);
            magma.setTag(JsonToNBT.getTagFromJson(String.format("{SkullOwner:{Id:[I;-1949909288,1299464445,-1707774066,-249984712],Properties:{textures:[{Value:\"%s\"}]},Name:\"%s\"}}", texture, name)));
            magma.setDisplayName(new StringTextComponent(name));
            return magma;
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}


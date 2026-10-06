package net.dusty_dusty.bnb_core.datagen;

import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.init.BNBCoreAttributes;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.function.Supplier;

public class BNBLangProvider extends LanguageProvider {
    public BNBLangProvider(PackOutput output) {
        super(output, BnbCore.MODID,"en_us");
    }

    @Override
    protected void addTranslations() {
        addAttribute(BNBCoreAttributes.UNDEAD_DAMAGE, "Undead Damage");
        addAttribute(BNBCoreAttributes.UNDEAD_ARMOR, "Undead Armor");
        addAttribute(BNBCoreAttributes.UNDEAD_ARMOR_TOUGHNESS, "Undead Armor Toughness");
        add("config.jade.plugin_bnb_core.temp_data", "Crop Temperature Data");
    }

    public void addAttribute(Supplier<? extends Attribute> key, String name) {
        add(key.get().getDescriptionId(), name);
    }
}

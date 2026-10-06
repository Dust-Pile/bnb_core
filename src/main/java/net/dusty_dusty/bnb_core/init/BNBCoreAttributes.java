package net.dusty_dusty.bnb_core.init;

import net.dusty_dusty.bnb_core.BnbCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BNBCoreAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, BnbCore.MODID);

    public static final RegistryObject<Attribute> UNDEAD_DAMAGE = ATTRIBUTES.register("undead_damage",() ->
            new RangedAttribute("attribute.bnbcore.undead_damage", 0, 0, 2048));

    public static final RegistryObject<Attribute> UNDEAD_ARMOR = ATTRIBUTES.register("undead_armor",() ->
            new RangedAttribute("attribute.bnbcore.undead_armor", 0, 0, 2048));

    public static final RegistryObject<Attribute> UNDEAD_ARMOR_TOUGHNESS = ATTRIBUTES.register("undead_armor_toughness",() ->
            new RangedAttribute("attribute.bnbcore.undead_armor_toughness", 0, 0, 2048));

    public static final UUID[] UUIDS = new UUID[]{
            hash(BnbCore.id("attribute_modifier_helmet")),
                    hash(BnbCore.id("attribute_modifier_chestplate")),
    hash(BnbCore.id("attribute_modifier_leggings")),
    hash(BnbCore.id("attribute_modifier_boots")),
    hash(BnbCore.id("attribute_modifier_mainhand"))};

    public static final AttributeModifier MAINHAND = new AttributeModifier(UUIDS[4].toString(),2, AttributeModifier.Operation.ADDITION);

    public static final AttributeModifier HELMET_1 = new AttributeModifier(UUIDS[0].toString(),1, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier HELMET_3 = new AttributeModifier(UUIDS[0].toString(),3, AttributeModifier.Operation.ADDITION);

    public static final AttributeModifier CHESTPLATE_2 = new AttributeModifier(UUIDS[1].toString(),2, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier CHESTPLATE_3 = new AttributeModifier(UUIDS[1].toString(),3, AttributeModifier.Operation.ADDITION);

    public static final AttributeModifier LEGGINGS_1 = new AttributeModifier(UUIDS[2].toString(),1, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier LEGGINGS_3 = new AttributeModifier(UUIDS[2].toString(),3, AttributeModifier.Operation.ADDITION);

    public static final AttributeModifier BOOTS_1 = new AttributeModifier(UUIDS[3].toString(),1, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier BOOTS_3 = new AttributeModifier(UUIDS[3].toString(),3, AttributeModifier.Operation.ADDITION);

    public static final AttributeModifier[] ARMOR_MODIFIERS = new AttributeModifier[]{BOOTS_1,LEGGINGS_1,CHESTPLATE_2,HELMET_1};

    public static final AttributeModifier[] ARMOR_TOUGHNESS_MODIFIERS = new AttributeModifier[]{BOOTS_3,LEGGINGS_3,CHESTPLATE_3,HELMET_3};

    public static void init(IEventBus modBus) {
        ATTRIBUTES.register(modBus);
    }

    static UUID hash(ResourceLocation resourceLocation) {
        return UUID.nameUUIDFromBytes(resourceLocation.toString().getBytes(StandardCharsets.UTF_8));
    }
}

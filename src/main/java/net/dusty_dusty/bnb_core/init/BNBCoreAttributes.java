package net.dusty_dusty.bnb_core.init;

import net.dusty_dusty.bnb_core.BnbCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
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

    public static final UUID uuid = hash(BnbCore.id("attribute_modifier"));
    public static void init(IEventBus modBus) {
        ATTRIBUTES.register(modBus);
    }

    static UUID hash(ResourceLocation resourceLocation) {
        return UUID.nameUUIDFromBytes(resourceLocation.toString().getBytes(StandardCharsets.UTF_8));
    }
}

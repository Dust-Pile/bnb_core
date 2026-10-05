package net.dusty_dusty.bnb_core.init;

import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.HungerResistanceEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BNBCorePotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, BnbCore.MODID);

    public static final RegistryObject<Potion> HUNGER_RESISTANCE = POTIONS.register("hunger_resistance",() ->
            new Potion(new MobEffectInstance(BNBCoreEffects.HUNGER_RESISTANCE.get(),54000)));

    public static final RegistryObject<Potion> LONG_HUNGER_RESISTANCE = POTIONS.register("long_hunger_resistance",() ->
            new Potion(new MobEffectInstance(BNBCoreEffects.HUNGER_RESISTANCE.get(),108000)));

    public static final RegistryObject<Potion> STRONG_HUNGER_RESISTANCE = POTIONS.register("strong_hunger_resistance",() ->
            new Potion(new MobEffectInstance(BNBCoreEffects.HUNGER_RESISTANCE.get(),54000,1)));

    public static void init(IEventBus modBus) {
        POTIONS.register(modBus);
    }
}

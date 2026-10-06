package net.dusty_dusty.bnb_core.init;

import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.HungerResistanceEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BNBCoreEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BnbCore.MODID);

    public static final RegistryObject<MobEffect> HUNGER_RESISTANCE = EFFECTS.register("undead_damage",() ->
            new HungerResistanceEffect(MobEffectCategory.BENEFICIAL,0xaaaaff));

    public static void init(IEventBus modBus) {
        EFFECTS.register(modBus);
    }
}

package net.dusty_dusty.bnb_core.mixins;

import net.dusty_dusty.bnb_core.init.BNBCoreAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    @Nullable
    public abstract AttributeInstance getAttribute(Attribute pAttribute);

    @Shadow
    public abstract double getAttributeValue(Attribute pAttribute);

    @Unique
    private static final ThreadLocal<DamageSource> sourceThread = new ThreadLocal<>();
    @Inject(method = "getDamageAfterArmorAbsorb",at = @At("HEAD"))
    private void captureSource(DamageSource pDamageSource, float pDamageAmount, CallbackInfoReturnable<Float> cir) {
        sourceThread.set(pDamageSource);
    }

    //damage armor toughness
    @ModifyArg(method = "getDamageAfterArmorAbsorb",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterAbsorb(FFF)F"),index = 1)
    private float undeadResistance(float originalArmor) {
        DamageSource source = sourceThread.get();
        if (source.getEntity() instanceof LivingEntity livingEntity && livingEntity.getMobType() == MobType.UNDEAD) {
            double undeadArmor = getAttributeValue(BNBCoreAttributes.UNDEAD_ARMOR.get());
            originalArmor+=undeadArmor;
        }
        return originalArmor;
    }

    @ModifyArg(method = "getDamageAfterArmorAbsorb",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterAbsorb(FFF)F"),index = 2)
    private float undeadResistance1(float originalArmor) {
        DamageSource source = sourceThread.get();
        if (source.getEntity() instanceof LivingEntity livingEntity && livingEntity.getMobType() == MobType.UNDEAD) {
            double undeadArmor = getAttributeValue(BNBCoreAttributes.UNDEAD_ARMOR_TOUGHNESS.get());
            originalArmor+=undeadArmor;
        }
        return originalArmor;
    }

    @Inject(method = "createLivingAttributes",at = @At("RETURN"))
    private static void appendAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder value = cir.getReturnValue();
        value.add(BNBCoreAttributes.UNDEAD_DAMAGE.get(),0)
                .add(BNBCoreAttributes.UNDEAD_ARMOR.get(),0)
                .add(BNBCoreAttributes.UNDEAD_ARMOR_TOUGHNESS.get(),0);
    }
}

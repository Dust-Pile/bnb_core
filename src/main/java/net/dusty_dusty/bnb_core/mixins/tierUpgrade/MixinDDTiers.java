package net.dusty_dusty.bnb_core.mixins.tierUpgrade;

import com.kyanite.deeperdarker.util.DDTiers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("EqualsBetweenInconvertibleTypes")
@Mixin( DDTiers.class )
public class MixinDDTiers {

    @Final
    @Shadow( remap = false )
    private int durability;
    @Final
    @Shadow( remap = false )
    private float speed;
    @Final
    @Shadow( remap = false )
    private float damage;

    /**
     * @author Ada Aster
     * @reason Simple Change
     */
    @Overwrite
    public int getUses() {
        if ( this.equals( DDTiers.WARDEN ) ) {
            return this.durability + 500;
        }
        return this.durability;
    }

    /**
     * @author Ada Aster
     * @reason Simple Change
     */
    @Overwrite
    public float getSpeed() {
        if ( this.equals( DDTiers.WARDEN ) ) {
            return this.speed + 1;
        }
        return this.speed;
    }

    /**
     * @author Ada Aster
     * @reason Simple Change
     */
    @Overwrite
    public float getAttackDamageBonus() {
        if ( this.equals( DDTiers.WARDEN ) ) {
            return this.damage + 2F;
        }
        return this.damage;
    }
}

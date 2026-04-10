package net.dusty_dusty.bnb_core.mixins.tierUpgrade;

import com.github.smallinger.copperagebackport.item.tools.CopperTier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("EqualsBetweenInconvertibleTypes")
@Mixin( CopperTier.class )
public class MixinCopperTier {

    @Final
    @Shadow( remap = false )
    private int uses;
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
    @Overwrite( remap = false )
    public int m_6609_() {
        if ( this.equals( CopperTier.INSTANCE ) ) {
            return this.uses + 10;
        }
        return this.uses;
    }

    /**
     * @author Ada Aster
     * @reason Simple Change
     */
    @Overwrite( remap = false )
    public float m_6624_() {
        if ( this.equals( CopperTier.INSTANCE ) ) {
            return this.speed + 1;
        }
        return this.speed;
    }

    /**
     * @author Ada Aster
     * @reason Simple Change
     */
    @Overwrite( remap = false )
    public float m_6631_() {
        if ( this.equals( CopperTier.INSTANCE ) ) {
            return this.damage + 1F;
        }
        return this.damage;
    }
}

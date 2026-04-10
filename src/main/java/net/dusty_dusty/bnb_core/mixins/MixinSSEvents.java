package net.dusty_dusty.bnb_core.mixins;

import net.czachor0.simplesilver.event.ModEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.*;

import java.util.*;
import java.util.function.Supplier;

@Mixin( ModEvents.class )
public class MixinSSEvents {

    @Final
    @Shadow( remap = false )
    private static Supplier<Set<ItemLike>> SILVER_TOOLS;

    @Unique
    private static final Set<ItemLike> bnbCore$MORE_SILVER_TOOLS = new HashSet<>();
    @Unique
    private static final ArrayList<String> bnbCore$TOOLS = new ArrayList<>( Arrays.asList(
            "knife", "cleaver", "pickaxe", "pickadze", "grubhoe", "dagger", "katana", "rapier", "spear", "glaive", "hammer" ) );

    /**
     * @author Ada Aster
     * @reason Simple Change
     */
    @Overwrite( remap = false )
    private static Set<ItemLike> silverTools() {
        return bnbCore$moreSilverTools();
    }

    @Unique
    private static Set<ItemLike> bnbCore$moreSilverTools() {
        if ( bnbCore$MORE_SILVER_TOOLS.isEmpty() ) {
            for ( String tool : bnbCore$TOOLS ) {
                ResourceLocation toolPath = ResourceLocation.fromNamespaceAndPath( "cm_extended", "silver_" + tool );
                if ( ForgeRegistries.ITEMS.containsKey( toolPath ) ) {
                    bnbCore$MORE_SILVER_TOOLS.add( ForgeRegistries.ITEMS.getValue( toolPath ) );
                }
            }
            bnbCore$MORE_SILVER_TOOLS.addAll( SILVER_TOOLS.get() );
        }

        return bnbCore$MORE_SILVER_TOOLS;
    }

}

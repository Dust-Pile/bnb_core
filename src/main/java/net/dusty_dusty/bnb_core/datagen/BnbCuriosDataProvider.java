package net.dusty_dusty.bnb_core.datagen;

import net.dusty_dusty.bnb_core.BnbCore;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import top.theillusivec4.curios.api.CuriosDataProvider;

public class BnbCuriosDataProvider extends CuriosDataProvider {

    public BnbCuriosDataProvider(GatherDataEvent event) {
        super(BnbCore.MODID, event.getGenerator().getPackOutput(), event.getExistingFileHelper(), event.getLookupProvider());
    }

    @Override
    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        createSlot("pouch");
        this.createEntities("entity_slots")
                .addPlayer()
                .addSlots("pouch");

    }
}

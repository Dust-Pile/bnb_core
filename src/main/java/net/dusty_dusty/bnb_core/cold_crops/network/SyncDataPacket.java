package net.dusty_dusty.bnb_core.cold_crops.network;

import net.dusty_dusty.bnb_core.cold_crops.data.CropData;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class SyncDataPacket {
    public HashMap<String , CropData> crop_map;
    public HashMap<String, String> seeds_list; // Seed resloc string, block/crop resloc string

    //TODO Not a TODO but a reminder, This packet can become too big

    public SyncDataPacket(HashMap<String , CropData> crop_map, HashMap<String, String> seeds_list) {
        this.crop_map = crop_map;
        this.seeds_list = seeds_list;
    }

    public SyncDataPacket(FriendlyByteBuf buf) {
        this.crop_map = (HashMap<String, CropData>) buf.readMap(FriendlyByteBuf::readUtf, buffer -> CropData.fromNBT(buffer.readNbt()));
        this.seeds_list = (HashMap<String, String>) buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeMap(crop_map, FriendlyByteBuf::writeUtf, ((friendlyByteBuf, data) -> friendlyByteBuf.writeNbt(data.serializeNBT())));
        buf.writeMap(seeds_list, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public static boolean handle(SyncDataPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        CropsNSeedsData.setCropsMap( message.crop_map );
        CropsNSeedsData.setSeedsList( message.seeds_list );
        return true;
    }
}

package net.dusty_dusty.bnb_core.cold_crops.network;

import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.client.BnbCoreClient;
import net.dusty_dusty.bnb_core.cold_crops.data.CropData;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @param seeds_list Seed resloc string, block/crop resloc string
 */
public record SyncDataPacket(Map<ResourceLocation, CropData> crop_map,
                             Map<ResourceLocation, ResourceLocation> seeds_list) {
    //TODO Not a TODO but a reminder, This packet can become too big

    public SyncDataPacket(FriendlyByteBuf buf) {
        this(buf.readMap(FriendlyByteBuf::readResourceLocation, buffer -> CropData.fromNBT(buffer.readNbt()))
                , buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readResourceLocation));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeMap(crop_map, FriendlyByteBuf::writeResourceLocation, (friendlyByteBuf, data) ->
                friendlyByteBuf.writeNbt(data.serializeNBT()));
        buf.writeMap(seeds_list, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeResourceLocation);
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public static boolean handle(SyncDataPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        BnbCoreClient.setCropsMap(message.crop_map);
        BnbCoreClient.setSeedsList(message.seeds_list);
        return true;
    }
}

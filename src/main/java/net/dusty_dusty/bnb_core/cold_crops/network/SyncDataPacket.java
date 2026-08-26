package net.dusty_dusty.bnb_core.cold_crops.network;

import io.netty.buffer.Unpooled;
import net.dusty_dusty.bnb_core.client.BnbCoreClient;
import net.dusty_dusty.bnb_core.cold_crops.data.CropData;
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

    public int getNbtSize() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        encode(buffer);
        buffer.release();
        return buffer.writerIndex();
    }

    public SyncDataPacket(FriendlyByteBuf buf) {
        this(buf.readMap(FriendlyByteBuf::readResourceLocation, CropData::fromPacket)
                , buf.readMap(buf1 -> buf1.readResourceLocation(), FriendlyByteBuf::readResourceLocation));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeMap(crop_map, FriendlyByteBuf::writeResourceLocation, (friendlyByteBuf, data) ->
                data.toPacket(friendlyByteBuf));
        buf.writeMap(seeds_list, (buf1,b) -> buf1.writeResourceLocation(b), FriendlyByteBuf::writeResourceLocation);
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public static boolean handle(SyncDataPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        BnbCoreClient.setCropsMap(message.crop_map);
        BnbCoreClient.setSeedsList(message.seeds_list);
        System.out.println("Packet size: "+ message.getNbtSize());
        return true;
    }
}

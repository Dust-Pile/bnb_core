package net.dusty_dusty.bnb_core.cold_crops.network;

import io.netty.buffer.Unpooled;
import net.dusty_dusty.bnb_core.BnbCore;
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
public record SyncDataPacket(Map<ResourceLocation, CropData> crop_map) {
    //TODO Not a TODO but a reminder, This packet can become too big

    public int getNbtSize() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        encode(buffer);
        buffer.release();
        return buffer.writerIndex();
    }

    public SyncDataPacket(FriendlyByteBuf buf) {
        this(buf.readMap(FriendlyByteBuf::readResourceLocation, CropData::fromPacket));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeMap(crop_map, FriendlyByteBuf::writeResourceLocation, (friendlyByteBuf, data) ->
                data.toPacket(friendlyByteBuf));
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public static boolean handle(SyncDataPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        BnbCoreClient.setMaps(message.crop_map);
        if (BnbCore.DEBUG) {
            System.out.println("Packet size: " + message.getNbtSize());
        }
        return true;
    }
}

package net.dusty_dusty.bnb_core.cold_crops.network;

import io.netty.buffer.Unpooled;
import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.client.BnbCoreClient;
import net.dusty_dusty.bnb_core.cold_crops.data.CropData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @param seeds_list Seed resloc string, block/crop resloc string
 */
public record SyncDataPacket(Map<ResourceLocation, CropData> crop_map,
                             Map<Item, ResourceLocation> seeds_list) {
    //TODO Not a TODO but a reminder, This packet can become too big

    public int getNbtSize() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        encode(buffer);
        buffer.release();
        return buffer.writerIndex();
    }

    public SyncDataPacket(FriendlyByteBuf buf) {
        this(buf.readMap(FriendlyByteBuf::readResourceLocation, CropData::fromPacket)
                , buf.readMap(buf1 -> buf1.readById(BuiltInRegistries.ITEM), FriendlyByteBuf::readResourceLocation));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeMap(crop_map, FriendlyByteBuf::writeResourceLocation, (friendlyByteBuf, data) ->
                data.toPacket(friendlyByteBuf));
        buf.writeMap(seeds_list, (buf1,b) -> buf1.writeId(BuiltInRegistries.ITEM,b), FriendlyByteBuf::writeResourceLocation);
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public static boolean handle(SyncDataPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        BnbCoreClient.setCropsMap(message.crop_map);
        BnbCoreClient.setSeedsList(message.seeds_list);
        if (BnbCore.DEBUG) {
            System.out.println("Packet size: " + message.getNbtSize());
        }
        return true;
    }
}

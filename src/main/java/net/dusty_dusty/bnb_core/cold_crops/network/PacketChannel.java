package net.dusty_dusty.bnb_core.cold_crops.network;

import org.apache.commons.lang3.tuple.Pair;
import net.dusty_dusty.bnb_core.BnbCore;
import net.dusty_dusty.bnb_core.cold_crops.data.CropsNSeedsData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;

public class PacketChannel {
    private static SimpleChannel INSTANCE;
    private static final String CHANNEL_ID = BnbCore.MODID + ":packetchannel";

    private static int packetID = 0;
    private static int id() {
        return packetID++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named( ResourceLocation.fromNamespaceAndPath(BnbCore.MODID, "packetchannel") )
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(SyncDataPacket.class, id(), NetworkDirection.LOGIN_TO_CLIENT)
                .encoder(SyncDataPacket::encode)
                .decoder(SyncDataPacket::new)
                .consumerMainThread(SyncDataPacket::handle)
                .buildLoginPacketList( bool -> {
                    List<Pair<String, SyncDataPacket>> list = new ArrayList<>();
                    list.add( Pair.of( CHANNEL_ID,
                            new SyncDataPacket(BnbCore.getCropsAndSeedsDataUnsafe().CROPS_MAP) ));
                    return list;
                })
                .noResponse()
                .add();

        net.messageBuilder(RequestBlockTempData.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(RequestBlockTempData::encode)
                .decoder(RequestBlockTempData::new)
                .consumerMainThread(RequestBlockTempData::handle)
                .add();

        net.messageBuilder(RecordBlockTempData.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RecordBlockTempData::encode)
                .decoder(RecordBlockTempData::new)
                .consumerMainThread(RecordBlockTempData::handle)
                .add();
    }


    public static <MSG> void sendToAllClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}

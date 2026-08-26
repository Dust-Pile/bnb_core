package net.dusty_dusty.bnb_core.cold_crops.network;

import com.momosoftworks.coldsweat.util.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RequestBlockTempData(BlockPos cropPos) {

    public RequestBlockTempData(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.cropPos);
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    static public boolean handle(RequestBlockTempData message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context netCtx = contextSupplier.get();
        if (netCtx.getSender() == null) {
            return false;
        }

        double temperature = WorldHelper.getTemperatureAt(netCtx.getSender().level(), message.cropPos);
        PacketChannel.sendToClient(new RecordBlockTempData(message.cropPos, temperature), netCtx.getSender());

        return true;
    }
}


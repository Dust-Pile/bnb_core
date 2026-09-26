package net.dusty_dusty.bnb_core.cold_crops.network;

import com.momosoftworks.coldsweat.api.util.Temperature;
import com.momosoftworks.coldsweat.config.spec.ClientSettingsConfig;
import net.dusty_dusty.bnb_core.cold_crops.compat.jade.CropComponentProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RecordBlockTempData(BlockPos cropPos,double temperature){

    public RecordBlockTempData(FriendlyByteBuf buf) {
        this(buf.readBlockPos(),buf.readDouble());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.cropPos);
        buf.writeDouble(this.temperature);
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public static boolean handle(RecordBlockTempData message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context netCtx = contextSupplier.get();

        CropComponentProvider.curTemperature = (int) Math.round(Temperature.convert( message.temperature, Temperature.Units.MC, ClientSettingsConfig.USE_CELSIUS.get() ? Temperature.Units.C : Temperature.Units.F, true));
        CropComponentProvider.curBlockPos = message.cropPos;

        return true;
    }
}
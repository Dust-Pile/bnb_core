package net.dusty_dusty.bnb_core.cold_crops.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.momosoftworks.coldsweat.api.util.Temperature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class CropData {
    private Temperature.Units type; //C or F
    private final Optional<Block> transformCold;
    private final Optional<Block> transformHot;//The Block to transform to when frozen
    private final Item seedItem;
    private final Optional<Short> minTemp; //below this temp the plant freezes
    private final Optional<Short> maxTemp; //above this temp the plant dies

    public CropData(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();
        if (jsonObject.has("type")) {
            String val = jsonObject.get("type").getAsString().toUpperCase();
            try {
                this.type = Temperature.Units.valueOf(val);
            } catch (Exception e) {
                this.type = Temperature.Units.C;
            }
        } else {
            this.type = Temperature.Units.C;
        }

        this.maxTemp = Optional.ofNullable(jsonObject.has("max") ? jsonObject.get("max").getAsShort() : null);
        this.minTemp = Optional.ofNullable(jsonObject.has("min") ? jsonObject.get("min").getAsShort() : null);

        String supposedTransform = jsonObject.has("transforms_hot") ? jsonObject.get("transforms_hot").getAsString() : null;
        if (supposedTransform != null) {
            ResourceLocation location = ResourceLocation.parse(supposedTransform);
            //we do this to ensure we get air if the given block is invalid
            this.transformHot = Optional.ofNullable(ForgeRegistries.BLOCKS.getValue(location));
        } else {
            this.transformHot = Optional.empty();
        }

        //We can reuse it tbh
        supposedTransform = jsonObject.has("transforms_cold") ? jsonObject.get("transforms_cold").getAsString() : null;
        if (supposedTransform != null) {
            ResourceLocation location = ResourceLocation.parse(supposedTransform);
            //we do this to ensure we get air if the given block is invalid
            this.transformCold = Optional.ofNullable(ForgeRegistries.BLOCKS.getValue(location));
        } else {
            this.transformCold = Optional.empty();
        }

        String supposedSeed = jsonObject.has("seed") ? jsonObject.get("seed").getAsString() : null;
        if (supposedSeed != null) {
            ResourceLocation location = ResourceLocation.parse(supposedSeed);
            //we do this to ensure we don't get air
            Item targetLoc = ForgeRegistries.ITEMS.getValue(location);
            if (targetLoc != Items.AIR) {
                this.seedItem = targetLoc;
            } else {
                this.seedItem = null;
            }
        } else {
            this.seedItem = null;
        }
    }

    public CropData(Temperature.Units units, Optional<Block> transformCold, Optional<Block> transformHot,
                    Item seedItem, Optional<Short> i1, Optional<Short> i) {
        this.type = units;
        this.transformCold = transformCold;
        this.transformHot = transformHot;
        this.seedItem = seedItem;
        this.minTemp = i1;
        this.maxTemp = i;
    }

    public double getGrowOdds(double num, Temperature.Units unit) {
        double convertedTemp = Temperature.convert(num, unit, this.type, true);
        int halfTemp = (this.maxTemp.get() - this.minTemp.get()) / 2;
        double offset = 1.25;

        // Parabolic function such that 0 is returned at max and min temp, and offset is the value of the maxima.
        return offset - (Math.pow(convertedTemp - this.minTemp.get() - halfTemp, 2) / ((1 / offset) * Math.pow(halfTemp, 2)));
    }

    public boolean isWarmer(double num, Temperature.Units unit) {
        return this.maxTemp.isPresent() && this.maxTemp.get() < Temperature.convert(num, unit, this.type, true);
    }

    public boolean isColder(double num, Temperature.Units unit) {
        return this.minTemp.isPresent() && this.minTemp.get() > Temperature.convert(num, unit, this.type, true);
    }

    public void onCold(double num, Temperature.Units unit, Consumer<Block> consumer) {
        if (!(isColder(num, unit))) return;
        if (transformCold.isEmpty()) return;
        consumer.accept(transformCold.get());
    }

    public void onHot(double num, Temperature.Units unit, Consumer<Block> consumer) {
        if (!(isWarmer(num, unit))) return;
        if (transformHot.isEmpty()) return;
        consumer.accept(transformHot.get());
    }

    @SuppressWarnings("SimplifyOptionalCallChains") //PLEASE ONLY USE IN RENDERING
    public @Nullable Integer getMaxTemp() {
        return maxTemp.map(temp -> (int) temp).orElse(null);
    }

    @SuppressWarnings("SimplifyOptionalCallChains") //PLEASE ONLY USE IN RENDERING
    public @Nullable Integer getMinTemp() {
        return minTemp.map(temp -> (int) temp).orElse(null);
    }

    public Temperature.Units getType() {
        return type;
    }

    public @Nullable Item getSeedItem() {
        return seedItem;
    }

    // TODO: Test experimental! Is this different than writeShort?
    public void toPacket(FriendlyByteBuf packet) {
        packet.writeEnum(type);
        packet.writeOptional(transformCold, (FriendlyByteBuf buf, Block block) -> buf.writeId(BuiltInRegistries.BLOCK,block));
        packet.writeOptional(transformHot, (FriendlyByteBuf buf, Block block) -> buf.writeId(BuiltInRegistries.BLOCK,block));
        packet.writeId(BuiltInRegistries.ITEM,this.seedItem);

        Optional<Integer> compactedTemps = Optional.of(((int) this.minTemp.get() << 16)
                | (int) this.maxTemp.get());
        packet.writeOptional(compactedTemps, FriendlyByteBuf::writeInt);
//        packet.writeOptional(this.minTemp, FriendlyByteBuf::writeShort);
//        packet.writeOptional(this.maxTemp, FriendlyByteBuf::writeShort);
    }

    // TODO: Test experimental! Is this different than writeShort?
    public static CropData fromPacket(FriendlyByteBuf buf) {
        Temperature.Units units = buf.readEnum(Temperature.Units.class);
        Optional<Block> transformCold = buf.readOptional(buf1 -> buf1.readById(BuiltInRegistries.BLOCK));
        Optional<Block> transformHot = buf.readOptional(buf1 -> buf1.readById(BuiltInRegistries.BLOCK));
        Item seedItem = buf.readById(BuiltInRegistries.ITEM);

        Optional<Integer> compactedTemps = buf.readOptional(FriendlyByteBuf::readInt);
        Optional<Short> cold = compactedTemps.map(val -> (short) (val >> 16));
        Optional<Short> hot = compactedTemps.map(val ->
                (short) ((val & 0x0000FFFF) * ((val & 0x00008000) > 0 ? -1 : 1))
        );

//        Optional<Short> cold = buf.readOptional(FriendlyByteBuf::readShort);
//        Optional<Short> hot = buf.readOptional(FriendlyByteBuf::readShort);

        return new CropData(units,transformCold,transformHot, seedItem, cold, hot);
    }
}

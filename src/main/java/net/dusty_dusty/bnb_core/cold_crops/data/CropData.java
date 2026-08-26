package net.dusty_dusty.bnb_core.cold_crops.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.momosoftworks.coldsweat.api.util.Temperature;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class CropData implements INBTSerializable<CompoundTag> {
    private Temperature.Units type; //C or F
    private Optional<ResourceLocation> transformCold;
    private Optional<ResourceLocation> transformHot;//The Block to transform to when frozen
    private ResourceLocation seedItem;
    private Optional<Integer> minTemp; //below this temp the plant freezes
    private Optional<Integer> maxTemp; //above this temp the plant dies

    public CropData(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();
        this.maxTemp = Optional.ofNullable(jsonObject.has("max") ? jsonObject.get("max").getAsInt() : null);
        this.minTemp = Optional.ofNullable(jsonObject.has("min") ? jsonObject.get("min").getAsInt() : null);

        //Defaults to C because F makes no sense as a European :)

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

        String supposedTransform = jsonObject.has("transforms_hot") ? jsonObject.get("transforms_hot").getAsString() : null;
        if (supposedTransform != null) {
            ResourceLocation location = ResourceLocation.parse(supposedTransform);
            //we do this to ensure we get air if the given block is invalid
            this.transformHot = Optional.ofNullable(ForgeRegistries.BLOCKS.getKey(ForgeRegistries.BLOCKS.getValue(location)));
        } else {
            this.transformHot = Optional.empty();
        }

        //We can reuse it tbh
        supposedTransform = jsonObject.has("transforms_cold") ? jsonObject.get("transforms_cold").getAsString() : null;
        if (supposedTransform != null) {
            ResourceLocation location = ResourceLocation.parse(supposedTransform);
            //we do this to ensure we get air if the given block is invalid
            this.transformCold = Optional.ofNullable(ForgeRegistries.BLOCKS.getKey(ForgeRegistries.BLOCKS.getValue(location)));
        } else {
            this.transformCold = Optional.empty();
        }

        String supposedSeed = jsonObject.has("seed") ? jsonObject.get("seed").getAsString() : null;
        if (supposedSeed != null) {
            ResourceLocation location = ResourceLocation.parse(supposedSeed);
            //we do this to ensure we don't get air
            ResourceLocation targetLoc = ForgeRegistries.ITEMS.getKey(ForgeRegistries.ITEMS.getValue(location));
            //noinspection DataFlowIssue
            if (!targetLoc.getPath().equals("air")) {
                this.seedItem = targetLoc;
            } else {
                this.seedItem = null;
            }
        } else {
            this.seedItem = null;
        }
    }

    public CropData(Temperature.Units units, Optional<ResourceLocation> transformCold, Optional<ResourceLocation> transformHot,
                    ResourceLocation seedItem, Optional<Integer> i1, Optional<Integer> i) {
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

    public void onCold(double num, Temperature.Units unit, Consumer<ResourceLocation> consumer) {
        if (!(isColder(num, unit))) return;
        if (transformCold.isEmpty()) return;
        consumer.accept(transformCold.get());
    }

    public void onHot(double num, Temperature.Units unit, Consumer<ResourceLocation> consumer) {
        if (!(isWarmer(num, unit))) return;
        if (transformHot.isEmpty()) return;
        consumer.accept(transformHot.get());
    }

    //PLEASE ONLY USE IN RENDERING
    public @Nullable Integer getMaxTemp() {
        return maxTemp.orElse(null);
    }

    //PLEASE ONLY USE IN RENDERING
    public @Nullable Integer getMinTemp() {
        return minTemp.orElse(null);
    }

    public Temperature.Units getType() {
        return type;
    }

    public @Nullable ResourceLocation getSeedItem() {
        return seedItem;
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("type", this.type.toString());
        if (this.transformCold.isPresent()) nbt.putString("transforms_cold", this.transformCold.toString());
        if (this.transformHot.isPresent()) nbt.putString("transforms_hot", this.transformHot.toString());
        if (this.seedItem != null) nbt.putString("seed", this.seedItem.toString());
        this.minTemp.ifPresent(integer -> nbt.putInt("min", integer));
        this.maxTemp.ifPresent(integer -> nbt.putInt("max", integer));

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.type = Temperature.Units.valueOf(nbt.getString("type").toUpperCase());

        if (nbt.contains("transforms_cold"))
            this.transformCold = Optional.of(ResourceLocation.parse(nbt.getString("transforms_cold")));
        if (nbt.contains("transforms_hot"))
            this.transformHot = Optional.of(ResourceLocation.parse(nbt.getString("transforms_hot")));
        if (nbt.contains("seed")) this.seedItem = ResourceLocation.parse(nbt.getString("seed"));
        if (nbt.contains("min")) this.minTemp = Optional.of(nbt.getInt("min"));
        if (nbt.contains("max")) this.maxTemp = Optional.of(nbt.getInt("max"));
    }


    public void toPacket(FriendlyByteBuf packet) {
        packet.writeEnum(type);
        packet.writeOptional(transformCold, FriendlyByteBuf::writeResourceLocation);
        packet.writeOptional(transformHot, FriendlyByteBuf::writeResourceLocation);
        packet.writeResourceLocation(this.seedItem);
        packet.writeOptional(this.minTemp, FriendlyByteBuf::writeInt);
        packet.writeOptional(this.maxTemp, FriendlyByteBuf::writeInt);
    }

    public static CropData fromPacket(FriendlyByteBuf buf) {
        Temperature.Units units = buf.readEnum(Temperature.Units.class);
        Optional<ResourceLocation> transformCold = buf.readOptional(FriendlyByteBuf::readResourceLocation);
        Optional<ResourceLocation> transformHot = buf.readOptional(FriendlyByteBuf::readResourceLocation);
        ResourceLocation seedItem = buf.readResourceLocation();
        Optional<Integer> cold = buf.readOptional(FriendlyByteBuf::readInt);
        Optional<Integer> hot = buf.readOptional(FriendlyByteBuf::readInt);

        return new CropData(units,transformCold,transformHot,
                seedItem,cold,hot);
    }
}

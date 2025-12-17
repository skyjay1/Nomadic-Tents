package nomadictents.registries;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import nomadictents.NomadicTents;

import java.util.function.Supplier;

public class NTDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, NomadicTents.MOD_ID);

    public static final Supplier<DataComponentType<Integer>> TENT_ID = DATA_COMPONENTS.register("tent_id", () ->
            DataComponentType.<Integer>builder().persistent(com.mojang.serialization.Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<Integer>> TENT_LAYERS = DATA_COMPONENTS.register("tent_layers", () ->
            DataComponentType.<Integer>builder().persistent(com.mojang.serialization.Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<DyeColor>> TENT_COLOR = DATA_COMPONENTS.register("tent_color", () ->
            DataComponentType.<DyeColor>builder().persistent(DyeColor.CODEC).networkSynchronized(ByteBufCodecs.VAR_INT.map(DyeColor::byId, DyeColor::getId)).build());

    public static final Supplier<DataComponentType<BlockPos>> DOOR_POS = DATA_COMPONENTS.register("door_pos", () ->
            DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build());

    public static final Supplier<DataComponentType<Direction>> DOOR_DIRECTION = DATA_COMPONENTS.register("door_direction", () ->
            DataComponentType.<Direction>builder().persistent(Direction.CODEC).networkSynchronized(Direction.STREAM_CODEC).build());

    public static void init(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}

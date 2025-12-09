package nomadictents.registries;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import nomadictents.dimension.EmptyChunkGenerator;
import nomadictents.structure.LocStructureProcessor;
import nomadictents.structure.ShamiyanaStructureProcessor;
import nomadictents.structure.TepeeStructureProcessor;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;

public final class NTStructureProcessorRegistry {

    public static void init(IEventBus bus) {
        RegUtils.STRUCTURE_PROCESSOR_TYPES.register(bus);
        RegUtils.CHUNK_GENERATORS.register(bus);
    }

    public static final Supplier<StructureProcessorType<TepeeStructureProcessor>> TEPEE_PROCESSOR = RegUtils.STRUCTURE_PROCESSOR_TYPES.register("tepee_processor", () -> () -> TepeeStructureProcessor.MAP_CODEC);
    public static final Supplier<StructureProcessorType<ShamiyanaStructureProcessor>> SHAMIYANA_PROCESSOR = RegUtils.STRUCTURE_PROCESSOR_TYPES.register("shamiyana_processor", () -> () -> ShamiyanaStructureProcessor.CODEC);
    public static final Supplier<StructureProcessorType<LocStructureProcessor>> LOC_PROCESSOR = RegUtils.STRUCTURE_PROCESSOR_TYPES.register("loc_processor", () -> () -> LocStructureProcessor.CODEC);

    public static final Supplier<MapCodec<? extends ChunkGenerator>> EMPTY_CHUNK_GENERATOR = RegUtils.CHUNK_GENERATORS.register("empty", () -> EmptyChunkGenerator.CODEC);
}

package nomadictents.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import nomadictents.NomadicTents;
import nomadictents.dimension.EmptyChunkGenerator;
import nomadictents.structure.LocStructureProcessor;
import nomadictents.structure.ShamiyanaStructureProcessor;
import nomadictents.structure.TepeeStructureProcessor;

public final class NTStructureProcessorRegistry {

    public static StructureProcessorType<TepeeStructureProcessor> TEPEE_PROCESSOR;
    public static StructureProcessorType<ShamiyanaStructureProcessor> SHAMIYANA_PROCESSOR;
    public static StructureProcessorType<LocStructureProcessor> LOC_PROCESSOR;

    public static void setupProcessors(FMLCommonSetupEvent event) {
        // register tepee processor
        TEPEE_PROCESSOR = StructureProcessorType.register(NomadicTents.MOD_ID + ":tepee_processor", TepeeStructureProcessor.CODEC);
        // register shamiyana processor
        SHAMIYANA_PROCESSOR = StructureProcessorType.register(NomadicTents.MOD_ID + ":shamiyana_processor", ShamiyanaStructureProcessor.CODEC);
        // register loc processor
        LOC_PROCESSOR = StructureProcessorType.register(NomadicTents.MOD_ID + ":loc_processor", LocStructureProcessor.CODEC);
        // register chunk generator
        event.enqueueWork(() -> {
            Registry.register(BuiltInRegistries.CHUNK_GENERATOR, new ResourceLocation(NomadicTents.MOD_ID, "empty"), EmptyChunkGenerator.CODEC);
        });
    }
}

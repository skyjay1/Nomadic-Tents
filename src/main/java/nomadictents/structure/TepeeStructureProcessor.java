package nomadictents.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import nomadictents.NTRegistry;

import javax.annotation.Nullable;

public class TepeeStructureProcessor extends StructureProcessor {

    public static final Codec<TepeeStructureProcessor> CODEC = Codec.unit(TepeeStructureProcessor::new);

    public static final TepeeStructureProcessor PROCESSOR = new TepeeStructureProcessor();

    @Nullable
    @Override
    public Template.BlockInfo process(IWorldReader level, BlockPos rawPos, BlockPos pos, Template.BlockInfo rawBlockInfo, Template.BlockInfo blockInfo, PlacementSettings placementSettings, @Nullable Template template) {
        return super.process(level, rawPos, pos, rawBlockInfo, blockInfo, placementSettings, template);
    }

    @Override
    protected IStructureProcessorType<?> getType() {
        return NTRegistry.ProcessorReg.TEPEE_PROCESSOR;
    }
}

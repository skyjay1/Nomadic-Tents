package nomadictents.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import nomadictents.registries.NTStructureProcessorRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class LocStructureProcessor extends StructureProcessor {

    public static final MapCodec<LocStructureProcessor> CODEC
            = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RuleTest.CODEC.fieldOf("predicate").forGetter(LocStructureProcessor::getLocPredicate)
    ).apply(instance, LocStructureProcessor::new));

    public static final LocStructureProcessor REPLACE_AIR = new LocStructureProcessor(new BlockMatchTest(Blocks.AIR));

    private final RuleTest locPredicate;

    public LocStructureProcessor(RuleTest locPredicate) {
        this.locPredicate = locPredicate;
    }

    public RuleTest getLocPredicate() {
        return locPredicate;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader level, @NotNull BlockPos rawPos,
                                                        @NotNull BlockPos pos,
                                                        @NotNull StructureTemplate.StructureBlockInfo rawBlockInfo,
                                                        StructureTemplate.StructureBlockInfo blockInfo,
                                                        StructurePlaceSettings placementSettings,
                                                        @Nullable StructureTemplate template) {
        RandomSource random = placementSettings.getRandom(blockInfo.pos());
        BlockState blockState = level.getBlockState(blockInfo.pos());
        // only process the block if the existing block at this location passes the rule test
        if (locPredicate.test(blockState, random)) {
            return blockInfo;
        }
        return null;
    }

    @NotNull
    @Override
    protected StructureProcessorType<?> getType() {
        return NTStructureProcessorRegistry.LOC_PROCESSOR.get();
    }
}

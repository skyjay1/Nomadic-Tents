package nomadictents.structure;

import com.mojang.serialization.Codec;
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
import nomadictents.NTStructureProcessorsRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class LocStructureProcessor extends StructureProcessor {

    public static final Codec<LocStructureProcessor> CODEC = RuleTest.CODEC.xmap(LocStructureProcessor::new, LocStructureProcessor::getLocPredicate).stable();

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
    public StructureTemplate.StructureBlockInfo process(LevelReader level, @NotNull BlockPos rawPos, @NotNull BlockPos pos, StructureTemplate.@NotNull StructureBlockInfo rawBlockInfo, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings placementSettings, @Nullable StructureTemplate template) {
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
        return NTStructureProcessorsRegistry.LOC_PROCESSOR;
    }
}

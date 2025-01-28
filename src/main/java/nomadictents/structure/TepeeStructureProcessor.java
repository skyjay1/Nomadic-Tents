package nomadictents.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import nomadictents.NTConfig;
import nomadictents.NTStructureProcessorsRegistry;
import nomadictents.block.TepeeBlock;
import nomadictents.registries.NTBlockRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TepeeStructureProcessor extends StructureProcessor {

    public static final Codec<TepeeStructureProcessor> CODEC = Codec.unit(TepeeStructureProcessor::new);
    public static final TepeeStructureProcessor TEPEE_PROCESSOR = new TepeeStructureProcessor();

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(
            @NotNull LevelReader level, @NotNull BlockPos rawPos, @NotNull BlockPos pos,
            @NotNull StructureTemplate.StructureBlockInfo rawBlockInfo,
            StructureTemplate.StructureBlockInfo blockInfo,
            @NotNull StructurePlaceSettings placementSettings, @Nullable StructureTemplate template) {
        // process blank tepee wall
        BlockPos p = blockInfo.pos();
        if (blockInfo.state().getBlock() == NTBlockRegistry.BLANK_TEPEE_WALL.get()) {
            RandomSource rand = placementSettings.getRandom(null);
            // random pattern using block position as seed
            if (p.getY() % 2 == 0) {
                int randSeed = p.getY() + rand.hashCode();
                return new StructureTemplate.StructureBlockInfo(p, TepeeBlock.getRandomPattern(RandomSource.create(randSeed)), null);
            }
            // random design using existing seeded random
            if (rand.nextInt(100) < NTConfig.CONFIG.TEPEE_DECORATED_CHANCE.get()) {
                return new StructureTemplate.StructureBlockInfo(p, TepeeBlock.getRandomSymbol(rand), null);
            }
        }
        return blockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return NTStructureProcessorsRegistry.TEPEE_PROCESSOR;
    }
}

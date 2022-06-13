package nomadictents.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import nomadictents.NTRegistry;
import nomadictents.NomadicTents;
import nomadictents.block.TepeeBlock;

import javax.annotation.Nullable;
import java.util.Random;

public class TepeeStructureProcessor extends StructureProcessor {

    public static final Codec<TepeeStructureProcessor> CODEC = Codec.unit(TepeeStructureProcessor::new);

    public static final TepeeStructureProcessor TEPEE_PROCESSOR = new TepeeStructureProcessor();

    @Nullable
    @Override
    public Template.BlockInfo process(IWorldReader level, BlockPos rawPos, BlockPos pos, Template.BlockInfo rawBlockInfo, Template.BlockInfo blockInfo, PlacementSettings placementSettings, @Nullable Template template) {
        // process blank tepee wall
        BlockPos p = blockInfo.pos;
        if (blockInfo.state.getBlock() == NTRegistry.BlockReg.BLANK_TEPEE_WALL) {
            Random rand = placementSettings.getRandom(null);
            // random pattern using block position as seed
            if (p.getY() % 2 == 0) {
                int randSeed = p.getY() + rand.hashCode();
                return new Template.BlockInfo(p, TepeeBlock.getRandomPattern(new Random(randSeed)), null);
            }
            // random design using existing seeded random
            if (rand.nextInt(100) < NomadicTents.CONFIG.TEPEE_DECORATED_CHANCE.get()) {
                return new Template.BlockInfo(p, TepeeBlock.getRandomSymbol(rand), null);
            }
        }
        return blockInfo;
    }

    @Override
    protected IStructureProcessorType<?> getType() {
        return NTRegistry.ProcessorReg.TEPEE_PROCESSOR;
    }
}

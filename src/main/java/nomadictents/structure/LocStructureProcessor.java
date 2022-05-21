package nomadictents.structure;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.*;
import nomadictents.NTRegistry;
import nomadictents.block.ShamiyanaWallBlock;

import javax.annotation.Nullable;
import java.util.Random;

public class LocStructureProcessor extends StructureProcessor {

    public static final Codec<LocStructureProcessor> CODEC = RuleTest.CODEC.xmap(LocStructureProcessor::new, LocStructureProcessor::getLocPredicate).stable();

    public static final LocStructureProcessor REPLACE_AIR = new LocStructureProcessor(new BlockMatchRuleTest(Blocks.AIR));

    private final RuleTest locPredicate;

    public LocStructureProcessor(RuleTest locPredicate) {
        this.locPredicate = locPredicate;
    }

    public RuleTest getLocPredicate() {
        return locPredicate;
    }

    @Nullable
    @Override
    public Template.BlockInfo process(IWorldReader level, BlockPos rawPos, BlockPos pos, Template.BlockInfo rawBlockInfo, Template.BlockInfo blockInfo, PlacementSettings placementSettings, @Nullable Template template) {
        Random random = placementSettings.getRandom(blockInfo.pos);
        BlockState blockState = level.getBlockState(blockInfo.pos);
        // only process the block if the existing block at this location passes the rule test
        if(locPredicate.test(blockState, random)) {
            return blockInfo;
        }
        return null;
    }

    @Override
    protected IStructureProcessorType<?> getType() {
        return NTRegistry.ProcessorReg.LOC_PROCESSOR;
    }
}

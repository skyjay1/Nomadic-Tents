package nomadictents.structure;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import nomadictents.NTRegistry;
import nomadictents.block.ShamiyanaWallBlock;

import javax.annotation.Nullable;

public class ShamiyanaStructureProcessor extends StructureProcessor {

    public static final Codec<DyeColor> COLOR_CODEC = Codec.STRING.xmap(
            name -> DyeColor.byName(name, DyeColor.WHITE),
            DyeColor::getSerializedName).stable();

    public static final Codec<ShamiyanaStructureProcessor> CODEC = COLOR_CODEC.xmap(ShamiyanaStructureProcessor::new, ShamiyanaStructureProcessor::getColor).stable();

    private final DyeColor color;

    public ShamiyanaStructureProcessor(DyeColor color) {
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader level, BlockPos rawPos, BlockPos pos, StructureTemplate.StructureBlockInfo rawBlockInfo, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings placementSettings, @Nullable StructureTemplate template) {
        // process blank tepee wall
        BlockPos p = blockInfo.pos;
        if (blockInfo.state.getBlock() == NTRegistry.BlockReg.WHITE_SHAMIYANA_WALL) {
            boolean pattern = /*p.getY() % 3 == 1 || */blockInfo.state.getValue(ShamiyanaWallBlock.PATTERN);
            BlockState state = TentPlacer.SHAMIYANA_WALLS.get(this.color).get().setValue(ShamiyanaWallBlock.PATTERN, pattern);
            return new StructureTemplate.StructureBlockInfo(p, state, null);
        }
        return blockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return NTRegistry.ProcessorReg.TEPEE_PROCESSOR;
    }
}

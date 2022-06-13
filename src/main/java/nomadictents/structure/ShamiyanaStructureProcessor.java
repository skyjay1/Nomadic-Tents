package nomadictents.structure;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
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
    public Template.BlockInfo process(IWorldReader level, BlockPos rawPos, BlockPos pos, Template.BlockInfo rawBlockInfo, Template.BlockInfo blockInfo, PlacementSettings placementSettings, @Nullable Template template) {
        // process blank shamiyana wall
        BlockPos p = blockInfo.pos;
        if (blockInfo.state.getBlock() == NTRegistry.BlockReg.WHITE_SHAMIYANA_WALL) {
            boolean pattern = /*p.getY() % 3 == 1 || */ blockInfo.state.getValue(ShamiyanaWallBlock.PATTERN);
            BlockState state = TentPlacer.SHAMIYANA_WALLS.get(this.color).get().setValue(ShamiyanaWallBlock.PATTERN, pattern);
            return new Template.BlockInfo(p, state, null);
        }
        return blockInfo;
    }

    @Override
    protected IStructureProcessorType<?> getType() {
        return NTRegistry.ProcessorReg.TEPEE_PROCESSOR;
    }
}

package nomadictents.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import nomadictents.NTStructureProcessorsRegistry;
import nomadictents.block.ShamiyanaWallBlock;
import nomadictents.registries.NTBlockRegistry;
import org.jetbrains.annotations.NotNull;

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
    public StructureTemplate.StructureBlockInfo process(@NotNull LevelReader level, @NotNull BlockPos rawPos, @NotNull BlockPos pos, @NotNull StructureTemplate.StructureBlockInfo rawBlockInfo, StructureTemplate.StructureBlockInfo blockInfo, @NotNull StructurePlaceSettings placementSettings, @Nullable StructureTemplate template) {
        // process blank tepee wall
        BlockPos p = blockInfo.pos();
        if (blockInfo.state().getBlock() == NTBlockRegistry.WHITE_SHAMIYANA_WALL.get()) {
            boolean pattern = /*p.getY() % 3 == 1 || */ blockInfo.state().getValue(ShamiyanaWallBlock.PATTERN);
            BlockState state = TentPlacer.SHAMIYANA_WALLS.get(this.color).get().defaultBlockState().setValue(ShamiyanaWallBlock.PATTERN, pattern);
            return new StructureTemplate.StructureBlockInfo(p, state, null);
        }
        return blockInfo;
    }

    @NotNull
    @Override
    protected StructureProcessorType<?> getType() {
        return NTStructureProcessorsRegistry.TEPEE_PROCESSOR;
    }
}

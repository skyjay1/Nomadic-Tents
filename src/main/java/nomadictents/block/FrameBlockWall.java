package nomadictents.block;


import net.minecraft.block.BlockState;
import net.minecraft.util.IStringSerializable;
import nomadictents.NTRegistry;

import java.util.function.Supplier;

public class FrameBlockWall extends FrameBlock {

    private final FrameBlockWall.Type type;

    public FrameBlockWall(FrameBlockWall.Type type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public static enum Type implements IStringSerializable {
        TEPEE_WALL("tepee_wall", () -> NTRegistry.BlockReg.BLANK_TEPEE_WALL.defaultBlockState()),
        YURT_WALL("yurt_wall", () -> NTRegistry.BlockReg.YURT_WALL.defaultBlockState()),
        YURT_ROOF("yurt_roof", () -> NTRegistry.BlockReg.YURT_ROOF.defaultBlockState());

        private final String name;
        private final Supplier<BlockState> block;

        Type(final String name, Supplier<BlockState> block) {
            this.name = name;
            this.block = block;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public BlockState getBlock() {
            return block.get();
        }
    }
}

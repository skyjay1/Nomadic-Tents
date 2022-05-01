package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;

public class TentBlock extends Block {

    public TentBlock(Properties properties) {
        super(properties.noDrops().strength(-1, 3600000.0F));
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}

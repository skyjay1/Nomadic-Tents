package nomadictents.registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import nomadictents.structure.TentPlacer;
import nomadictents.tileentity.TentDoorBlockEntity;
import nomadictents.util.TentType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class NTBlockEntityRegistry {

    public static void init(IEventBus bus) {
        RegUtils.BLOCK_ENTITIES.register(bus);
    }

    // Block entities
    public static final Supplier<BlockEntityType<TentDoorBlockEntity>> TENT_BLOCK_ENTITY
            = RegUtils.BLOCK_ENTITIES.register("tent_door", () -> {
        // create a set of blocks that can use the tile entity
        Set<Block> doorBlocks = new HashSet<>();
        for (Map<TentType, Supplier<Block>> doorMap : TentPlacer.DOORS.values()) {
            for (Supplier<Block> supplier : doorMap.values()) {
                doorBlocks.add(supplier.get());
            }
        }
        // create the tile entity type
        return BlockEntityType.Builder.of(TentDoorBlockEntity::new, doorBlocks.toArray(new Block[0])).build(null);
    });
}

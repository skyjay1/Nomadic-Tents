package nomadictents.dimension;

import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.DimensionType;
import nomadictents.NomadicTents;

/**
 * @author Commoble, used with permission.
 * https://gist.github.com/Commoble/7db2ef25f94952a4d2e2b7e3d4be53e0
 */
// a Dimension is just a DimensionType + a ChunkGenerator
// we can define the dimension type in a json at data/yourmod/worldgen/dimension_type/your_dimension_type.json
// but we'll need to create instances of the chunk generator at runtime since there's no json folder for them
public class DimensionFactory
{
	public static final ResourceKey<DimensionType> TYPE_KEY = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY,
		new ResourceLocation(NomadicTents.MODID, "tent"));
  
	public static LevelStem createDimension(MinecraftServer server, ResourceKey<LevelStem> key)
	{
		return new LevelStem(new Holder.Direct<>(getDimensionType(server)), new EmptyChunkGenerator(server));
	}
	
	public static DimensionType getDimensionType(MinecraftServer server)
	{
		return server.registryAccess() // get dynamic registries
			.registry(Registry.DIMENSION_TYPE_REGISTRY).get()
				.getOrThrow(TYPE_KEY);
	}
}

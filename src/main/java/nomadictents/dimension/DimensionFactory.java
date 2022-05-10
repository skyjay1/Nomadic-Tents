package nomadictents.dimension;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
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
	public static final RegistryKey<DimensionType> TYPE_KEY = RegistryKey.create(Registry.DIMENSION_TYPE_REGISTRY,
		new ResourceLocation(NomadicTents.MODID, "tent"));
  
	public static Dimension createDimension(MinecraftServer server, RegistryKey<Dimension> key)
	{
		return new Dimension(() -> getDimensionType(server), new EmptyChunkGenerator(server));
	}
	
	public static DimensionType getDimensionType(MinecraftServer server)
	{
		return server.registryAccess() // get dynamic registries
			.registry(Registry.DIMENSION_TYPE_REGISTRY).get()
				.getOrThrow(TYPE_KEY);
	}
}

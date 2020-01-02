package nomadictents.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import nomadictents.event.TentEventHandler;
import nomadictents.proxies.ClientProxy;
import nomadictents.proxies.CommonProxy;

@Mod(NomadicTents.MODID)
@Mod.EventBusSubscriber(modid = NomadicTents.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NomadicTents {
	
	public static final String MODID = "nomadictents";
	public static final String HWYLA = "waila";
	
	public static final CommonProxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(),
			() -> () -> new CommonProxy());

	public static final ItemGroup TAB = new ItemGroup(MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Content.ITEM_TENT);
		}
	};
	
	public static final Logger LOGGER = LogManager.getFormatterLogger(MODID);
	
	public NomadicTents() {
		// register and load config
		LOGGER.debug(MODID + ": RegisterConfig");
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TentConfig.SPEC);
		// TentConfig.loadConfig(TentConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve(MODID + "-server.toml"));
		// register event handlers
		LOGGER.debug(MODID + ": RegisterEventHandlers");
		MinecraftForge.EVENT_BUS.register(new TentEventHandler());
		// client-side registry
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			try {
				FMLJavaModLoadingContext.get().getModEventBus()
				.register(nomadictents.event.ClientTentEventHandler.class);
			} catch (final Exception e) {
				LOGGER.error("Caught exception while registering Client-Side event handler");
				LOGGER.error(e.getMessage());
			}
		});	
	}

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		LOGGER.debug(MODID + ": RegisterBlocks");
		PROXY.registerBlocks(event);
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		LOGGER.debug(MODID + ": RegisterItems");
		PROXY.registerItems(event);
	}
	
	@SubscribeEvent
	public static void registerTileEntity(final RegistryEvent.Register<TileEntityType<?>> event) {
		LOGGER.debug(MODID + ": RegisterTileEntityType");
		PROXY.registerTileEntity(event);
	}
	
	@SubscribeEvent
	public static void registerDimension(final RegistryEvent.Register<ModDimension> event) {
		LOGGER.debug(MODID + ": RegisterDimension");
		PROXY.registerDimension(event);
	}
	
	@SubscribeEvent
	public static void registerBiome(final RegistryEvent.Register<Biome> event) {
		LOGGER.debug(MODID + ": RegisterBiome");
		PROXY.registerBiome(event);
	}
	
	@SubscribeEvent
	public static void registerRecipeSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
		LOGGER.debug(MODID + ": RegisterRecipeSerializer");
		PROXY.registerRecipeSerializers(event);
	}
}

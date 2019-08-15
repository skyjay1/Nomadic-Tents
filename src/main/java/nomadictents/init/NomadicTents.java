package nomadictents.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

	public static final ItemGroup TAB = new ItemGroup("yurtMain") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Content.ITEM_TENT);
		}
	};
	
	public NomadicTents() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TentConfig.SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(NomadicTents::setup);
		MinecraftForge.EVENT_BUS.register(new TentEventHandler());
		//MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public static void setup(final RegisterDimensionsEvent event) {
		System.out.println("yurtmod: RegisterDimensionsEvent!");
		//DimensionManagerTent.setup(event);
	}
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		System.out.println("yurtmod: RegisterBlocks");
		PROXY.registerBlocks(event);
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		System.out.println("yurtmod: RegisterItems");
		PROXY.registerItems(event);
	}
	
	@SubscribeEvent
	public static void registerTileEntity(final RegistryEvent.Register<TileEntityType<? extends TileEntity>> event) {
		System.out.println("yurtmod: RegisterTileEntityType");
		PROXY.registerTileEntity(event);
	}
	
	@SubscribeEvent
	public static void registerDimension(final RegistryEvent.Register<ModDimension> event) {
		System.out.println("yurtmod: RegisterDimension");
		PROXY.registerDimension(event);
	}
	
	@SubscribeEvent
	public static void registerBiome(final RegistryEvent.Register<Biome> event) {
		System.out.println("yurtmod: RegisterBiome");
		PROXY.registerBiome(event);
	}
}

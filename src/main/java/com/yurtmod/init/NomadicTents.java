package com.yurtmod.init;

import org.apache.commons.lang3.tuple.Pair;

import com.yurtmod.dimension.DimensionManagerTent;
import com.yurtmod.event.TentEventHandler;
import com.yurtmod.proxies.ClientProxy;
import com.yurtmod.proxies.CommonProxy;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.ForgeConfigSpec;
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

@Mod(NomadicTents.MODID)
@Mod.EventBusSubscriber(modid = NomadicTents.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NomadicTents {
	public static final String MODID = "yurtmod";
	
	public static final String HWYLA = "waila";
	
	public static final CommonProxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(),
			() -> () -> new CommonProxy());
	
	public static TentConfig TENT_CONFIG = null;
	public static ForgeConfigSpec SERVER_CONFIG = null;

	public static final ItemGroup TAB = new ItemGroup("yurtMain") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Content.ITEM_TENT);
		}
	};
	
	public NomadicTents() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);
		setupConfig();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(new TentEventHandler());
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setup(final RegisterDimensionsEvent event) {
		DimensionManagerTent.setup(event);
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		System.out.println("yurtmod: RegisterItems");
		PROXY.registerItems(event);
	}
	
	@SubscribeEvent
	public static void registerModels(final ModelRegistryEvent event) {
		System.out.println("flintmod: RegisterModels");
		PROXY.registerRenders(event);
	}
	
	@SubscribeEvent
	public static void onLoadConfig(final ModConfig.Loading configEvent) {
		
	}
	
	@SubscribeEvent
	public void registerDimension(final RegistryEvent.Register<ModDimension> event) {
		PROXY.registerDimension(event);
	}
	
	@SubscribeEvent
	public static void registerBiome(final RegistryEvent.Register<Biome> event) {
		PROXY.registerBiome(event);
	}
	
	public static void setupConfig() {
		final Pair<TentConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(TentConfig::new);
		SERVER_CONFIG = specPair.getRight();
		TENT_CONFIG = specPair.getLeft();
	}

//	public void preInit(FMLPreInitializationEvent event) {
//		Content.mainRegistry();
//		TentDimension.preInit();
//	}

//	public void init(FMLInitializationEvent event) {
//		MinecraftForge.EVENT_BUS.register(new TentEventHandler());
//		TentDimension.init();
//		if (Loader.isModLoaded(HWYLA)) {
//			FMLInterModComms.sendMessage(HWYLA, "register",
//				"com.yurtmod.integration.WailaProvider.callbackRegister");
//	}
}

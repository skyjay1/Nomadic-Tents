package com.yurtmod.main;

import java.io.File;

import com.yurtmod.crafting.RecipeManager;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.proxies.CommonProxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = NomadicTents.MODID, name = NomadicTents.NAME, version = NomadicTents.VERSION, acceptedMinecraftVersions = NomadicTents.MCVERSION)
public class NomadicTents {
	public static final String MODID = "yurtmod";
	public static final String NAME = "Nomadic Tents Mod";
	public static final String VERSION = "1.12.1";
	public static final String MCVERSION = "1.7.10";
	public static final String WAILA_MODID = "Waila";

	@SidedProxy(clientSide = "com." + MODID + ".proxies.ClientProxy", serverSide = "com." + MODID
			+ ".proxies.CommonProxy")
	public static CommonProxy proxy;

	public static CreativeTabs tab;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		tab = new CreativeTabs("yurtMain") {
			@Override
			public Item getTabIconItem() {
				return Content.itemTent;
			}
		};
		String path = event.getSuggestedConfigurationFile().getAbsolutePath().replaceFirst(MODID + ".cfg",
				"NomadicTents.cfg");
		Config.mainRegistry(new Configuration(new File(path)));
		Content.mainRegistry();
		RecipeManager.mainRegistry();
		TentDimension.mainRegistry();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new TentEventHandler());
		FMLCommonHandler.instance().bus().register(new TentEventHandler());
		proxy.registerRenders();
		FMLInterModComms.sendMessage(WAILA_MODID, "register", "com.yurtmod.integration.WailaProvider.callbackRegister");
	}
}

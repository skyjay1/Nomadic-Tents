package com.yurtmod.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.event.TentEventHandler;
import com.yurtmod.proxies.CommonProxy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = NomadicTents.MODID, name = NomadicTents.NAME, version = NomadicTents.VERSION, acceptedMinecraftVersions = NomadicTents.MCVERSION)
public class NomadicTents {
	public static final String MODID = "yurtmod";
	public static final String NAME = "Nomadic Tents";
	public static final String VERSION = "9.5.2";
	public static final String MCVERSION = "1.12.2";
	
	public static final String HWYLA = "waila";
	
	@SidedProxy(clientSide = "com." + MODID + ".proxies.ClientProxy", serverSide = "com." + MODID
			+ ".proxies.CommonProxy")
	public static CommonProxy proxy;
	
	public static final Logger LOGGER = LogManager.getFormatterLogger(MODID);

	public static final CreativeTabs TAB = new CreativeTabs("yurtMain") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Content.ITEM_TENT);
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Content.mainRegistry();
		TentDimension.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new TentEventHandler());
		TentDimension.init();
		proxy.registerItemColors();
		if (Loader.isModLoaded(HWYLA)) {
			FMLInterModComms.sendMessage(HWYLA, "register",
				"com.yurtmod.integration.WailaProvider.callbackRegister");
		}
	}
}

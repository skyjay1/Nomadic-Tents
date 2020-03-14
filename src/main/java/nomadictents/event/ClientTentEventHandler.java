package nomadictents.event;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import nomadictents.init.Content;
import nomadictents.init.NomadicTents;
import nomadictents.structure.util.TentData;
import nomadictents.structure.util.TentType;

public class ClientTentEventHandler {
	
	@SubscribeEvent
	public static void registerItemColors(final net.minecraftforge.client.event.ColorHandlerEvent.Item event) {
		//// Shamiana Tent Colorization
		NomadicTents.LOGGER.debug(NomadicTents.MODID + ": RegisterColorHandler");
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			try {
				// try to register ItemColors
				net.minecraft.client.renderer.color.ItemColors colors = event.getItemColors();		
				if (colors != null) {
					colors.register((ItemStack stack, int tintIndex) -> {
						// begin IItemColor
						final TentData data = new TentData(stack);
						if(data.getTent() == TentType.SHAMIANA) {
							// check if it's black and recolor slightly lighter than actual black
							if(data.getColor() == DyeColor.BLACK) {
								return 0x303030;
							}
							final float[] rgbFloat = data.getColor().getColorComponentValues();
							return MathHelper.rgb(rgbFloat[0], rgbFloat[1], rgbFloat[2]);
						}
						return -1;
						// end IItemColor
					}, () -> Content.ITEM_TENT);
				}
			} catch(final Exception e) {
				// print exception
				NomadicTents.LOGGER.error("Caught exception while registering ItemColors");
				NomadicTents.LOGGER.error(e.getMessage());
			}
		});
	}
	
	@SubscribeEvent
	public static void clientSetup(final net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
	  NomadicTents.LOGGER.debug(NomadicTents.MODID + ": RegisterRenderType");
	  DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
      try {
        registerRenderCutout(Content.FRAME_BEDOUIN_ROOF, Content.FRAME_BEDOUIN_WALL, Content.FRAME_INDLU_WALL,
            Content.FRAME_SHAMIANA_WALL, Content.FRAME_TEPEE_WALL, Content.FRAME_YURT_ROOF, Content.FRAME_YURT_WALL);
      } catch(final Exception e) {
        // print exception
        NomadicTents.LOGGER.error("Caught exception while registering RenderType");
        NomadicTents.LOGGER.error(e.getMessage());
      }
	  });
	}
	
	private static void registerRenderCutout(final Block... blocks) {
	  for(final Block b : blocks) {
	    RenderTypeLookup.setRenderLayer(b, RenderType.getCutout());
	  }
	}
}

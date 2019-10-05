package nomadictents.proxies;

import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nomadictents.init.Content;
import nomadictents.structure.util.TentData;
import nomadictents.structure.util.TentType;

public class ClientProxy extends CommonProxy {
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerItemColors(final net.minecraftforge.client.event.ColorHandlerEvent.Item event) {
		//// Shamiana Tent Colorization
		net.minecraft.client.renderer.color.ItemColors colors = event.getItemColors();		
		if (colors != null) {
			colors.register((ItemStack stack, int tintIndex) -> {
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
				
			}, () -> Content.ITEM_TENT);
		}
	}
}

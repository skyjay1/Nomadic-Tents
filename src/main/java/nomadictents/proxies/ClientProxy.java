package nomadictents.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import nomadictents.init.Content;
import nomadictents.structure.util.TentData;
import nomadictents.structure.util.TentType;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerItemColors() {
		//// Shamiana Tent Colorization
		ItemColors colors = Minecraft.getInstance().getItemColors();		
		if (colors != null) {
			colors.register((ItemStack stack, int tintIndex) -> {
				final TentData data = new TentData(stack);
				if(data.getTent() == TentType.SHAMIANA) {
					// check if it's black and recolor slightly lighter than actual black
					if(data.getColor() == DyeColor.BLACK) {
						return 0x303030;
					}
					final float[] rgbFloat = data.getColor().getColorComponentValues();
					return MathHelper.rgb(rgbFloat[0] * 255F, rgbFloat[1] * 255F, rgbFloat[2] * 255F);
				}
				return -1;
				
			}, () -> Content.ITEM_TENT);
		}
	}
}

package nomadictents.item;

import net.minecraft.item.Item;
import nomadictents.init.NomadicTents;

public class ItemDepthUpgrade extends Item {
		
	public ItemDepthUpgrade(final String name) {
		super(new Item.Properties().group(NomadicTents.TAB));
		this.setRegistryName(NomadicTents.MODID, name);
	}
}

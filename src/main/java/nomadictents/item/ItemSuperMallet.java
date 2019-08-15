package nomadictents.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import nomadictents.init.TentConfig;

public class ItemSuperMallet extends ItemMallet {
	public ItemSuperMallet(IItemTier material) {
		super(material);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext cxt) {
		if (TentConfig.CONFIG.SUPER_MALLET_CREATIVE_ONLY.get() && cxt.getPlayer() != null && !cxt.getPlayer().isCreative()) {
			return ActionResultType.PASS;
		}
		return super.onItemUse(cxt);
	}
}

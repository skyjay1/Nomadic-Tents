package nomadictents.integration;

import java.util.List;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import nomadictents.block.BlockTentDoor;
import nomadictents.block.BlockTentFrame;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.init.Content;
import nomadictents.init.NomadicTents;
import nomadictents.structure.util.TentData;

/**
 * WAILA integration
 **/
@WailaPlugin(NomadicTents.MODID)
public final class WailaProvider implements IWailaPlugin, 
		IComponentProvider, IServerDataProvider<TileEntity> {
	
	public static final WailaProvider INSTANCE = new WailaProvider();

	private static final String KEY_STRUCTURE_DATA = "TentData";
	
	@Override
	public void register(final IRegistrar register) {
		register.registerBlockDataProvider(INSTANCE, BlockTentDoor.class);
		register.registerStackProvider(INSTANCE, BlockTentDoor.class);
		register.registerStackProvider(INSTANCE, BlockTentFrame.class);
		register.registerComponentProvider(INSTANCE, TooltipPosition.HEAD, BlockTentDoor.class);
		register.registerComponentProvider(INSTANCE, TooltipPosition.HEAD, BlockTentFrame.class);
		register.registerComponentProvider(INSTANCE, TooltipPosition.BODY, BlockTentFrame.class);
	}

	@Override
	public ItemStack getStack(final IDataAccessor accessor, final IPluginConfig config) {
		if (accessor.getBlock() instanceof BlockTentDoor) {
			return new TentData(accessor.getServerData().getCompound(KEY_STRUCTURE_DATA)).getDropStack();
		} else if (accessor.getBlock() instanceof BlockTentFrame) {
			return new ItemStack(((BlockTentFrame) accessor.getBlock()).getBlockToBecome().get().getBlock());
		}
		return accessor.getStack();
	}

	@Override
	public void appendHead(final List<ITextComponent> tooltip, final IDataAccessor accessor, final IPluginConfig config) {
		if(accessor.getBlock() instanceof BlockTentDoor) {
			final TentData data = accessor.getServerData() != null && accessor.getServerData().contains(KEY_STRUCTURE_DATA)
					? new TentData(accessor.getServerData().getCompound(KEY_STRUCTURE_DATA))
					: new TentData();
			final ITextComponent name = Content.ITEM_TENT.getDisplayName(data.getDropStack());
			tooltip.set(0, name.applyTextStyle(TextFormatting.WHITE));
		} else if (accessor.getBlock() instanceof BlockTentFrame) {
			String header = accessor.getBlock().getTranslationKey();
			tooltip.set(0, new TranslationTextComponent(header).applyTextStyle(TextFormatting.WHITE));
		}
	}

	@Override
	public void appendBody(final List<ITextComponent> tooltip, final IDataAccessor accessor, final IPluginConfig config) {
		if(accessor.getBlock() instanceof BlockTentFrame) {
			final float data = (float)accessor.getBlockState().get(BlockTentFrame.PROGRESS).intValue();
    		String progress = "waila.progress";
    		int percentInt = (int)((data / BlockTentFrame.MAX_META) * 100F);
    		String percent = percentInt + "%";
    		tooltip.add(new TranslationTextComponent(progress, percent).applyTextStyle(TextFormatting.GRAY));
    	}
	}

	@Override
	public void appendServerData(final CompoundNBT tag, final ServerPlayerEntity player, final World world, TileEntity tileEntity) {
		BlockState state = world.getBlockState(tileEntity.getPos());
		if (state != null && state.getBlock() instanceof BlockTentDoor
				&& state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
			tileEntity = world.getTileEntity(tileEntity.getPos().down(1));
		}

		if (tileEntity instanceof TileEntityTentDoor) {
			TileEntityTentDoor tetd = (TileEntityTentDoor) tileEntity;
			tag.put(KEY_STRUCTURE_DATA, tetd.getTentData().serializeNBT());
		}
	}
}

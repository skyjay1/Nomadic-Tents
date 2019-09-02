package nomadictents.integration;

import java.util.List;

import mcp.mobius.waila.api.IBlockDecorator;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import nomadictents.block.BlockTentDoor;
import nomadictents.block.BlockTentFrame;
import nomadictents.init.NomadicTents;
import nomadictents.structure.util.TentData;

/**
 * WAILA integration
 **/
@WailaPlugin(NomadicTents.MODID)
public final class WailaProvider implements IWailaPlugin, 
		IComponentProvider, IServerDataProvider<TileEntity>, IBlockDecorator {
	
	public static final WailaProvider INSTANCE = new WailaProvider();

	private static final String KEY_STRUCTURE_DATA = "TentData";


	@Override
	public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
		BlockState state = accessor.getWorld().getBlockState(accessor.getPosition());
		if (state != null) {
			if (state.getBlock() instanceof BlockTentDoor) {
				return new TentData(accessor.getServerData().getCompound(KEY_STRUCTURE_DATA)).getDropStack();
			} else if (state.getBlock() instanceof BlockTentFrame) {
				return new ItemStack(((BlockTentFrame) state.getBlock()).getEnumBlockToBecome().getBlock().getBlock());
			}
		}
		return accessor.getStack();
	}

	@Override
	public void appendHead(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
//		Block actualBlock = accessor.getBlock();
//		if (actualBlock instanceof BlockTentFrame) {
//			String header = actualBlock.getUnlocalizedName() + ".name";
//			tooltip.set(0, String.format(ConfigFormatting.blockFormat, I18n.format(header)));
//		}
	}

	@Override
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
		if(accessor.getBlock() instanceof BlockTentFrame) {
			final float data = (float)accessor.getBlockState().get(BlockTentFrame.PROGRESS).intValue();
    		String progress = "waila.progress";
    		int percentInt = (int)((data / BlockTentFrame.MAX_META) * 100F);
    		String percent = percentInt + "%";
    		tooltip.add(new TranslationTextComponent(progress, percent).applyTextStyle(TextFormatting.GRAY));
    	}
	}

	@Override
	public void register(IRegistrar register) {
		register.registerStackProvider(INSTANCE, BlockTentDoor.class);
		register.registerStackProvider(INSTANCE, BlockTentFrame.class);
		register.registerBlockDataProvider(INSTANCE, BlockTentDoor.class);
		register.registerDecorator(INSTANCE, BlockTentFrame.class);
	}

	@Override
	public void decorateBlock(ItemStack stack, IDataAccessor accessor, IPluginConfig config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void appendServerData(CompoundNBT tag, ServerPlayerEntity player, World world, TileEntity tileEntity) {
//		BlockState state = world.getBlockState();
//		if (state != null && state.getBlock() instanceof BlockTentDoor
//				&& state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
//			tileEntity = world.getTileEntity(pos.down(1));
//		}
//
//		if (tileEntity instanceof TileEntityTentDoor) {
//			TileEntityTentDoor tetd = (TileEntityTentDoor) tileEntity;
//			tag.put(KEY_STRUCTURE_DATA, tetd.getTentData().serializeNBT());
//		}
	}
}

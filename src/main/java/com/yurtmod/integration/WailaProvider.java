package com.yurtmod.integration;

import java.util.List;

import javax.annotation.Nonnull;

import com.yurtmod.block.BlockTentDoor;
import com.yurtmod.block.BlockTentFrame;
import com.yurtmod.block.BlockUnbreakable;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.structure.StructureType;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.config.FormattingConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

/**
 * WAILA integration -- using Hwyla:1.8.23-B38_1.12.
 **/
@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = NomadicTents.HWYLA)
public final class WailaProvider implements IWailaDataProvider {

	private static final String KEY_STRUCTURE_TYPE = "StructureTypeOrdinal";

	@Optional.Method(modid = NomadicTents.HWYLA)
	public static void callbackRegister(final IWailaRegistrar register) {
		WailaProvider instance = new WailaProvider();
		register.registerStackProvider(instance, BlockUnbreakable.class);
		register.registerNBTProvider(instance, BlockTentDoor.class);
		register.registerHeadProvider(instance, BlockTentFrame.class);
		register.registerBodyProvider(instance, BlockTentFrame.class);
	}

	@Nonnull
	@Override
	@Optional.Method(modid = NomadicTents.HWYLA)
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		IBlockState state = accessor.getWorld().getBlockState(accessor.getPosition());
		if (state != null) {
			if (state.getBlock() instanceof BlockTentDoor) {
				int ordinal = accessor.getNBTInteger(accessor.getNBTData(), KEY_STRUCTURE_TYPE);
				return StructureType.getDropStack(0, 0, 0, ordinal);
			} else if (state.getBlock() instanceof BlockTentFrame) {
				return new ItemStack(((BlockTentFrame) state.getBlock()).getEnumBlockToBecome().getBlock().getBlock());
			}
		}
		return accessor.getStack();
	}

	/**
	 * @param player The player requesting data synchronization (The owner of the
	 *               current connection).
	 * @param te     The TileEntity targeted for synchronization.
	 * @param tag    Current synchronization tag (might have been processed by other
	 *               providers and might be processed by other providers).
	 * @param world  TileEntity's World.
	 * @param pos    Position of the TileEntity.
	 * @return Modified input NBTTagCompound tag.
	 */
	@Nonnull
	@Override
	@Optional.Method(modid = NomadicTents.HWYLA)
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world,
			BlockPos pos) {
		TileEntity tileEntity = te;
		IBlockState state = world.getBlockState(pos);
		if (state != null && state.getBlock() instanceof BlockTentDoor
				&& state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
			tileEntity = world.getTileEntity(pos.down(1));
		}

		if (tileEntity instanceof TileEntityTentDoor) {
			TileEntityTentDoor tetd = (TileEntityTentDoor) tileEntity;
			tag.setInteger(KEY_STRUCTURE_TYPE, tetd.getStructureType().ordinal());
		}
		return tag;
	}

	/**
	 * Callback used to add lines to one of the three sections of the tooltip (Head,
	 * Body, Tail).</br>
	 * Will only be called if the implementing class is registered via
	 * {@link IWailaRegistrar#registerHeadProvider}.</br>
	 * You are supposed to always return the modified input tooltip.</br>
	 * <p>
	 * You may return null if you have not registered this as a head provider.
	 * However, you should return the provided list to be safe.
	 * <p>
	 * This method is only called on the client side. If you require data from the
	 * server, you should also implement
	 * {@link #getNBTData(EntityPlayerMP, TileEntity, NBTTagCompound, World, BlockPos)}
	 * and add the data to the {@link NBTTagCompound} there, which can then be read
	 * back using {@link IWailaDataAccessor#getNBTData()}. If you rely on the client
	 * knowing the data you need, you are not guaranteed to have the proper values.
	 *
	 * @param itemStack Current block scanned, in ItemStack form.
	 * @param tooltip   Current list of tooltip lines (might have been processed by
	 *                  other providers and might be processed by other providers).
	 * @param accessor  Contains most of the relevant information about the current
	 *                  environment.
	 * @param config    Current configuration of Waila.
	 * @return Modified input tooltip
	 */
	@Nonnull
	@Override
	@Optional.Method(modid = NomadicTents.HWYLA)
	public List<String> getWailaHead(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		Block actualBlock = accessor.getBlock();
		if (actualBlock instanceof BlockTentFrame) {
			String header = actualBlock.getUnlocalizedName() + ".name";
			tooltip.set(0, String.format(FormattingConfig.blockFormat, I18n.format(header)));
		}
		return tooltip;
	}

	/**
	 * @param itemStack Current block scanned, in ItemStack form.
	 * @param tooltip   Current list of tooltip lines (might have been processed by
	 *                  other providers and might be processed by other providers).
	 * @param accessor  Contains most of the relevant information about the current
	 *                  environment.
	 * @param config    Current configuration of Waila.
	 * @return Modified input tooltip
	 */
	@Nonnull
	@Override
	@Optional.Method(modid = NomadicTents.HWYLA)
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		if(accessor.getBlock() instanceof BlockTentFrame) {
    		String progress = "waila.progress";
    		int percentInt = (int)(((float)accessor.getMetadata() / (float)BlockTentFrame.MAX_META) * 100F);
    		String percent = percentInt + "%";
    		tooltip.add(TextFormatting.GRAY + I18n.format(progress, percent));
    	}
		return tooltip;
	}
}

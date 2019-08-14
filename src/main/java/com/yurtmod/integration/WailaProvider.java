package com.yurtmod.integration;

import java.util.List;

import javax.annotation.Nonnull;

import com.yurtmod.block.BlockTentDoor;
import com.yurtmod.block.BlockTentFrame;
import com.yurtmod.block.BlockUnbreakable;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.structure.util.StructureData;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.config.FormattingConfig;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DoorBlock.EnumDoorHalf;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * WAILA integration -- using Hwyla:1.8.23-B38_1.12.
 **/
@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = NomadicTents.HWYLA)
public final class WailaProvider implements IWailaDataProvider {

	private static final String KEY_STRUCTURE_DATA = "StructureData";

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
		BlockState state = accessor.getWorld().getBlockState(accessor.getPosition());
		if (state != null) {
			if (state.getBlock() instanceof BlockTentDoor) {
				return new StructureData(accessor.getNBTData().getCompoundTag(KEY_STRUCTURE_DATA)).getDropStack();
			} else if (state.getBlock() instanceof BlockTentFrame) {
				return new ItemStack(((BlockTentFrame) state.getBlock()).getEnumBlockToBecome().getBlock().getBlock());
			}
		}
		return accessor.getStack();
	}

	@Nonnull
	@Override
	@Optional.Method(modid = NomadicTents.HWYLA)
	public CompoundNBT getNBTData(ServerPlayerEntity player, TileEntity te, CompoundNBT tag, World world,
			BlockPos pos) {
		TileEntity tileEntity = te;
		BlockState state = world.getBlockState(pos);
		if (state != null && state.getBlock() instanceof BlockTentDoor
				&& state.get(DoorBlock.HALF) == EnumDoorHalf.UPPER) {
			tileEntity = world.getTileEntity(pos.down(1));
		}

		if (tileEntity instanceof TileEntityTentDoor) {
			TileEntityTentDoor tetd = (TileEntityTentDoor) tileEntity;
			tag.setTag(KEY_STRUCTURE_DATA, tetd.getTentData().serializeNBT());
		}
		return tag;
	}

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

package com.yurtmod.integration;

import java.util.List;

import javax.annotation.Nonnull;

import com.yurtmod.blocks.BlockTentDoor;
import com.yurtmod.blocks.BlockTentFrame;
import com.yurtmod.blocks.BlockUnbreakable;
import com.yurtmod.blocks.TileEntityTentDoor;
import com.yurtmod.main.NomadicTents;
import com.yurtmod.structure.StructureType;

import cpw.mods.fml.common.Optional;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;;

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaEntityProvider", modid = NomadicTents.WAILA_MODID)
public class WailaProvider implements IWailaDataProvider {
	
private static final String KEY_STRUCTURE_TYPE = "StructureTypeOrdinal";
	
	@Optional.Method(modid = NomadicTents.WAILA_MODID)
	public static void callbackRegister(final IWailaRegistrar register) {
		WailaProvider instance = new WailaProvider();
		register.registerStackProvider(instance, BlockUnbreakable.class);
		register.registerNBTProvider(instance, BlockTentDoor.class);
		register.registerHeadProvider(instance, BlockTentFrame.class);
		register.registerBodyProvider(instance, BlockTentFrame.class);
	}

	@Nonnull
	@Override
	@Optional.Method(modid = NomadicTents.WAILA_MODID)
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		Block state = accessor.getBlock();
		if(state instanceof BlockTentDoor) {
			int ordinal = accessor.getNBTInteger(accessor.getNBTData(), KEY_STRUCTURE_TYPE);
			return StructureType.getDropStack(0, 0, 0, ordinal);
		} else if(state instanceof BlockTentFrame) {
			return new ItemStack(((BlockTentFrame)state).getBlockToBecome());
		}
		return accessor.getStack();
	}
	
  
    @Nonnull
    @Override
	@Optional.Method(modid = NomadicTents.WAILA_MODID)
	public List<String> getWailaHead(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
    	Block actualBlock = accessor.getBlock();
    	if(actualBlock instanceof BlockTentFrame) {
    		String header = actualBlock.getUnlocalizedName() + ".name";
    		tooltip.set(0, (EnumChatFormatting.WHITE + I18n.format(header)));
    	}
		return tooltip;
	}

    @Nonnull
    @Override
	@Optional.Method(modid = NomadicTents.WAILA_MODID)
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
    	if(accessor.getBlock() instanceof BlockTentFrame) {
    		String progress = "waila.progress";
    		int percentInt = (int)(((float)accessor.getMetadata() / (float)BlockTentFrame.MAX_META) * 100F);
    		String percent = percentInt + "%";
    		currenttip.add(EnumChatFormatting.GRAY + I18n.format(progress, percent));
    	}
		return currenttip;
	}

    @Nonnull
    @Override
	@Optional.Method(modid = NomadicTents.WAILA_MODID)
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

    @Nonnull
    @Override
	@Optional.Method(modid = NomadicTents.WAILA_MODID)
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
			int y, int z) {
    	TileEntity tileEntity = te;
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if(block instanceof BlockTentDoor && meta % 2 == 1) {
			tileEntity = world.getTileEntity(x, y-1, z);
		}
		
		if(tileEntity instanceof TileEntityTentDoor) {
			TileEntityTentDoor tetd = (TileEntityTentDoor)tileEntity;
			tag.setInteger(KEY_STRUCTURE_TYPE, tetd.getStructureType().ordinal());
		}
        return tag;
	}

}

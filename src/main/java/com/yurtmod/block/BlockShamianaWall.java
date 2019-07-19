package com.yurtmod.block;

import com.yurtmod.block.Categories.IShamianaBlock;
import com.yurtmod.init.Content;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;

public class BlockShamianaWall extends BlockUnbreakable implements IShamianaBlock {
	
	private final EnumDyeColor color;
	
	public BlockShamianaWall(final EnumDyeColor colorIn) {
		super(Material.CLOTH, MapColor.WHITE_STAINED_HARDENED_CLAY);
		this.color = colorIn;
	}
	
	public EnumDyeColor getColor() {
		return this.color;
	}
	
	public static IBlockState getShamianaState(final EnumDyeColor color) {
		switch(color) {
		case BLACK:		return Content.SHAMIANA_WALL_BLACK.getDefaultState();
		case BLUE:		return Content.SHAMIANA_WALL_BLUE.getDefaultState();
		case BROWN:		return Content.SHAMIANA_WALL_BROWN.getDefaultState();
		case CYAN:		return Content.SHAMIANA_WALL_CYAN.getDefaultState();
		case GRAY:		return Content.SHAMIANA_WALL_GRAY.getDefaultState();
		case GREEN:		return Content.SHAMIANA_WALL_GREEN.getDefaultState();
		case LIGHT_BLUE:return Content.SHAMIANA_WALL_LIGHT_BLUE.getDefaultState();
		case LIME:		return Content.SHAMIANA_WALL_LIME.getDefaultState();
		case MAGENTA:	return Content.SHAMIANA_WALL_MAGENTA.getDefaultState();
		case ORANGE:	return Content.SHAMIANA_WALL_ORANGE.getDefaultState();
		case PINK:		return Content.SHAMIANA_WALL_PINK.getDefaultState();
		case PURPLE:	return Content.SHAMIANA_WALL_PURPLE.getDefaultState();
		case RED:		return Content.SHAMIANA_WALL_RED.getDefaultState();
		case SILVER:	return Content.SHAMIANA_WALL_SILVER.getDefaultState();
		case WHITE:		return Content.SHAMIANA_WALL_WHITE.getDefaultState();
		case YELLOW:	return Content.SHAMIANA_WALL_YELLOW.getDefaultState();
		default:
			return Content.SHAMIANA_WALL_WHITE.getDefaultState();
		}		
	}

}

package com.yurtmod.structure.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.yurtmod.structure.StructureBedouin;
import com.yurtmod.structure.StructureIndlu;
import com.yurtmod.structure.StructureTepee;
import com.yurtmod.structure.StructureYurt;

public final class Blueprints {

	private static final Map<TentKey, Blueprint> map = new HashMap<>();
	
	static {
		// ADD ALL BLUEPRINTS
		for(final StructureWidth width : StructureWidth.values()) {
			put(StructureTent.YURT, width, StructureYurt.makeBlueprints(width));
			put(StructureTent.TEPEE, width, StructureTepee.makeBlueprints(width));
			put(StructureTent.BEDOUIN, width, StructureBedouin.makeBlueprints(width));
			put(StructureTent.INDLU, width, StructureIndlu.makeBlueprints(width));
		}
	}
	
	private Blueprints() {
		// empty constructor
	}
	
	private static boolean put(final StructureTent tent, final StructureWidth width, final Blueprint bp) {
		final TentKey key = new TentKey(tent, width);
		map.put(key, bp);
		return true;
	}
	
	@Nullable
	public static Blueprint get(final StructureTent tent, final StructureWidth width) {
		final TentKey key = new TentKey(tent, width);
		if(map.containsKey(key)) {
			return map.get(key);
		} else {
			return null;
		}
	}
	
	private static final class TentKey {
		private final StructureTent tent;
		private final StructureWidth width;
		
		private TentKey(final StructureTent tentIn, final StructureWidth widthIn) {
			this.tent = tentIn;
			this.width = widthIn;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((tent == null) ? 0 : tent.hashCode());
			result = prime * result + ((width == null) ? 0 : width.hashCode());
			return result;
		}

		
		@Override
		public boolean equals(final Object other) {
			if(this == other) {
				return true;
			} else if(other instanceof TentKey && other.getClass() == this.getClass()) {
				TentKey tentKeyOther = (TentKey)other;
				return tentKeyOther.tent == this.tent && tentKeyOther.width == this.width;
			}
			return false;
		}
		
//		@Override
//		public int hashCode() {
//			final int tentId = this.tent != null ? (int) this.tent.getId() : 0;
//			final int widthId = this.width != null ? (int) this.width.getId() : 0;
//			return 37 + (tentId + widthId * 31);
//		}
	}
}

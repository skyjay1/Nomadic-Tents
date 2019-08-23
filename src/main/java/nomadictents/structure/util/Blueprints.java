package nomadictents.structure.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import nomadictents.structure.StructureBedouin;
import nomadictents.structure.StructureIndlu;
import nomadictents.structure.StructureShamiana;
import nomadictents.structure.StructureTepee;
import nomadictents.structure.StructureYurt;

public final class Blueprints {

	private static final Map<TentKey, Blueprint> map = new HashMap<>();
	
	static {
		// ADD ALL BLUEPRINTS
		for(final TentWidth width : TentWidth.values()) {
			put(TentType.YURT, width, StructureYurt.makeBlueprints(width));
			put(TentType.TEPEE, width, StructureTepee.makeBlueprints(width));
			put(TentType.BEDOUIN, width, StructureBedouin.makeBlueprints(width));
			put(TentType.INDLU, width, StructureIndlu.makeBlueprints(width));
			put(TentType.SHAMIANA, width, StructureShamiana.makeBlueprints(width));
		}
	}
	
	private Blueprints() {
		// empty constructor
	}
	
	private static boolean put(final TentType tent, final TentWidth width, final Blueprint bp) {
		final TentKey key = new TentKey(tent, width);
		map.put(key, bp);
		return true;
	}
	
	@Nullable
	public static Blueprint get(final TentType tent, final TentWidth width) {
		final TentKey key = new TentKey(tent, width);
		if(map.containsKey(key)) {
			return map.get(key);
		} else {
			return null;
		}
	}
	
	private static final class TentKey {
		private final TentType tent;
		private final TentWidth width;
		
		private TentKey(final TentType tentIn, final TentWidth widthIn) {
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
	}
}

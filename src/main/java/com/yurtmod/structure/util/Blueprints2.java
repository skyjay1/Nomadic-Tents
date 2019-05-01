package com.yurtmod.structure.util;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.annotation.Nullable;
//
//public class Blueprints {
//
//	private static final Map<TentKey, Blueprint> map = new HashMap<>();
//	
//	private Blueprints() {
//		// empty
//	}
//	
//	public static boolean put(final StructureTent tent, final StructureWidth width, final Blueprint bp) {
//		final TentKey key = new TentKey(tent, width);
//		if(map.containsKey(key)) {
//			return false;
//		}
//		map.put(key, bp);
//		return true;
//	}
//	
//	@Nullable
//	public static Blueprint get(final StructureTent tent, final StructureWidth width) {
//		final TentKey key = new TentKey(tent, width);
//		if(map.containsKey(key)) {
//			return map.get(key);
//		} else {
//			return null;
//		}
//	}
//	
//	private static final class TentKey {
//		private final StructureTent tent;
//		private final StructureWidth width;
//		
//		private TentKey(final StructureTent tentIn, final StructureWidth widthIn) {
//			this.tent = tentIn;
//			this.width = widthIn;
//		}
//		
//		@Override
//		public boolean equals(final Object other) {
//			if(other instanceof TentKey) {
//				TentKey tentKeyOther = (TentKey)other;
//				return tentKeyOther.tent == this.tent && tentKeyOther.width == this.width;
//			}
//			return false;
//		}
//		
//		@Override
//		public int hashCode() {
//			return (this.tent.getId() + this.width.getId() * 31);
//		}
//	}
//}

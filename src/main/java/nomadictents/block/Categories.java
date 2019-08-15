package nomadictents.block;

/**
 * Contains interfaces that tent blocks should implement
 * in order for other code to differentiate between tent
 * types and their blocks
 * @see Categories.IYurtBlock
 * @see Categories.ITepeeBlock
 * @see Categories.IBedouinBlock
 * @see Categories.IIndluBlock
 * @see Categories.IFrameBlock
 **/
public final class Categories  {
	
	private Categories () {
		//
	}
	
	/** Base interface for all other Interfaces in the surrounding class **/
	public static interface ITentBlockBase {}
	/** Blocks with this interface are formally used in Yurt Structures **/
	public static interface IYurtBlock extends ITentBlockBase {}
	/** Blocks with this interface are formally used in Tepee Structures **/
	public static interface ITepeeBlock extends ITentBlockBase {}
	/** Blocks with this interface are formally used in Bedouin Structures **/
	public static interface IBedouinBlock extends ITentBlockBase {}
	/** Blocks with this interface are formally used in Indlu Structures **/
	public static interface IIndluBlock extends ITentBlockBase {}
	/** Blocks with this interface are formally used in Shamiana Structures **/
	public static interface IShamianaBlock extends ITentBlockBase {}
	/** Blocks with this interface are formally used as tent frame blocks **/
	public static interface IFrameBlock extends ITentBlockBase {}
	
}

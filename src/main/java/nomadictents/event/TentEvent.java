package nomadictents.event;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.dimension.TentDimensionManager;
import nomadictents.structure.util.TentData;

/**
 * This Event can be used to trigger behavior in other mods.
 * It is never fired by itself, you should always handle one
 * of its child classes.
 * @see TentEvent.Deconstruct
 * @see TentEvent.PreEnter
 * @see TentEvent.PostEnter
 **/
public class TentEvent extends Event {
	
	private final TileEntityTentDoor teDoor;
	
	public TentEvent(final TileEntityTentDoor door) {
		this.teDoor = door;
	}
	
	/** @return the TETD associated with this event. May be null. **/
	public TileEntityTentDoor getDoor() {
		return this.teDoor;
	}
	
	/** 
	 * @return the TentData object contained in this event.
	 * @see TentData#getTent()
	 * @see TentData#getWidth()
	 * @see TentData#getDepth()
	 * @see TentData#getColor()
	 **/
	@Nullable
	public TentData getData() {
		return this.teDoor != null ? teDoor.getTentData() : null;
	}
	
	/** @return the BlockPos of the lower half of the door **/
	@Nullable
	public BlockPos getDoorPos() {
		return this.teDoor != null ? this.teDoor.getPos() : null;
	}
	
	/** 
	 * @return true if this event is taking place from inside the Tent Dimension 
	 * @see TentEvent#getDimensionId()
	 **/
	public boolean isInsideTent() {
		return this.teDoor != null && TentDimensionManager.isTent(this.teDoor.getWorld());
	}
	
	/** 
	 * @return the dimension in which this event is taking place.
	 * @see TentEvent#isInsideTent()
	 * @see Dimension#getType()
	 **/
	@Nullable
	public Dimension getDimensionId() {
		return this.teDoor != null ? this.teDoor.getWorld().getDimension() : null;
	}
	
	/**
	 * Fired after a player clicks on the tent door
	 * and triggers a tent de-construction. It has
	 * already been verified that the tent exists and
	 * that the player has permissions to pick up the
	 * tent. The tent has not yet been deconstructed.
	 **/
	public static class Deconstruct extends TentEvent {
		
		final PlayerEntity player;

		public Deconstruct(final TileEntityTentDoor door, final PlayerEntity playerIn) {
			super(door);
			this.player = playerIn;
		}
		
		/** 
		 * This method will be called by the Tent Door to
		 * determine what to give the player after deconstruction.
		 * Use {@link TentEvent#getData()} to alter the return of
		 * this method.
		 * @return a copy of an ItemStack holding this tent, using
		 * {@link TentData#getDropStack()}, or EMPTY if there's
		 * a problem.
		 **/
		public ItemStack getTentStack() {
			return this.getData() != null ? this.getData().getDropStack() : ItemStack.EMPTY;
		}
		
		/** @return the PlayerEntity who is picking up this tent **/
		public PlayerEntity getPlayer() {
			return this.player;
		}
	}
	
	/**
	 * Fired after the tent door is activated and
	 * a constructed tent is detected. The player or entity
	 * has not yet been teleported, but it has been
	 * verified that they are able to teleport and will
	 * do so immediately after this event. Called before
	 * any code is run in {@link TileEntityTentDoor#teleport(Entity)}.
	 * <br> This event is {@link Cancelable}. Canceling the event
	 * will result in the Entity not teleporting.
	 **/
	@Cancelable
	public static class PreEnter extends TentEvent {
		
		private final Entity entity;

		public PreEnter(final TileEntityTentDoor door, final Entity entityIn) {
			super(door);
			this.entity = entityIn;
		}
		
		/** @return the Entity who activated the tent door. May be null. **/
		public Entity getEntity() {
			return this.entity;
		}
		
		/** @return whether or not the Entity is a player **/
		public boolean isPlayer() {
			return this.entity instanceof PlayerEntity;
		}
	}
	
	/**
	 * Fired after the player has been teleported to the
	 * Tent Dimension. The tent has already been constructed
	 * or upgraded as needed. This event is fired after all
	 * tent-handling code has finished and includes an enum
	 * representation of what happened with the tent.
	 * @see TentEvent.TentResult
	 **/
	public static class PostEnter extends TentEvent {
		
		private final Entity entity;
		private final TentResult tentResult;

		public PostEnter(final TileEntityTentDoor door, final Entity entityIn, final TentResult resultIn) {
			super(door);
			this.entity = entityIn;
			this.tentResult = resultIn;
		}
		
		/** @return the Entity who activated the tent door. May be null. **/
		public Entity getEntity() {
			return this.entity;
		}
		
		/** @return whether or not the Entity is a player **/
		public boolean isPlayer() {
			return this.entity instanceof PlayerEntity;
		}
		
		/**
		 * Indicates what happened to the tent when the player
		 * entered:  it may have been newly built, upgraded, or
		 * entered as-is.
		 * @return The TentResult (NONE, BUILT_FIRST, UPGRADED)
		 * @see TentEvent.TentResult
		 **/
		public TentResult getTentResult() {
			return this.tentResult;
		}
	}
	
	/**
	 * Used to indicate what kind of teleportation and
	 * structure building has been used.
	 * <br>NONE = an existing tent was entered
	 * <br>BUILT_FIRST = an entirely new tent was built and entered
	 * <br>UPGRADED = an existing tent was modified and entered
	 * @see TentEvent.PostEnter#getTentResult()
	 **/
	public static enum TentResult {
		NONE,
		BUILT_FIRST,
		UPGRADED;
	}
}

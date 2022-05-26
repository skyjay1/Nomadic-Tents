package nomadictents.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import nomadictents.NTRegistry;
import nomadictents.block.FrameBlock;
import nomadictents.structure.TentPlacer;

import javax.annotation.Nullable;
import java.util.List;

public class MalletItem extends Item {

  private final boolean isInstant;

  public MalletItem(Tier material, boolean isInstant, Item.Properties properties) {
    super(properties.durability(material.getUses()));
    this.isInstant = isInstant;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
    list.add(new TranslatableComponent(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    BlockState state = context.getLevel().getBlockState(context.getClickedPos());
    if (state.getBlock() instanceof FrameBlock && NTRegistry.BlockReg.DOOR_FRAME != state.getBlock()) {
      // swing arm
      if (context.getPlayer() != null) {
        context.getPlayer().swing(context.getHand());
      }
      // instant
      if (isInstant) {
        useInstant(context.getItemInHand(), context.getLevel(), state, context.getClickedPos(), context);
        return InteractionResult.SUCCESS;
      }
      // interact with frame block
      int progress = state.getValue(FrameBlock.PROGRESS);
      int next = progress + getEffectiveness(context.getItemInHand(), context.getLevel(), state, context.getClickedPos(), context.getPlayer());
      next = Math.min(next, FrameBlock.MAX_PROGRESS);
      if (progress >= FrameBlock.MAX_PROGRESS || next >= FrameBlock.MAX_PROGRESS) {
        // use durability
        if (null != context.getPlayer()) {
          context.getItemInHand().hurtAndBreak(1, context.getPlayer(), p -> p.broadcastBreakEvent(p.getUsedItemHand()));
        }
        // place target block
        BlockState target = TentPlacer.getFrameTarget(state, context.getLevel(), context.getClickedPos());
        target = target.getBlock().getStateForPlacement(new BlockPlaceContext(context));
        context.getLevel().setBlock(context.getClickedPos(), target, Block.UPDATE_ALL);
      } else {
        // increase progress
        context.getLevel().setBlock(context.getClickedPos(), state.setValue(FrameBlock.PROGRESS, next), Block.UPDATE_ALL);
      }
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  private int getEffectiveness(final ItemStack stack, final Level level, final BlockState state, final BlockPos pos, @Nullable Player player) {
    return 2;
  }

  private void useInstant(final ItemStack stack, final Level level, final BlockState state, final BlockPos pos, UseOnContext context) {
    // place target block
    BlockState target = TentPlacer.getFrameTarget(state, level, pos);
    BlockHitResult targetRayTrace = new BlockHitResult(Vec3.atCenterOf(pos), context.getClickedFace(), pos, true);
    UseOnContext targetContext = new UseOnContext(context.getLevel(), context.getPlayer(), context.getHand(), stack, targetRayTrace);
    target = target.getBlock().getStateForPlacement(new BlockPlaceContext(targetContext));
    level.setBlock(pos, target, Block.UPDATE_ALL);
    // use durability
    if (null != context.getPlayer()) {
      stack.hurtAndBreak(1, context.getPlayer(), p -> p.broadcastBreakEvent(context.getHand()));
    }
    // scan nearby area (including diagonals) and call this method for each frame found
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        for (int k = -1; k < 2; k++) {
          BlockPos curPos = pos.offset(i, j, k);
          BlockState current = level.getBlockState(curPos);
          if (current.getBlock() instanceof FrameBlock) {
            useInstant(stack, level, current, curPos, context);
          }
        }
      }
    }
  }

}
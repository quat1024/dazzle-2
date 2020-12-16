package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.LampStyle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;

public abstract class LampBlock extends Block implements Opcodes {
	public LampBlock(LampStyle style, Settings settings) {
		super(settings);
		this.style = style;
		
		setDefaultState(getDefaultState().with(INVERTED, false));
	}
	
	public final LampStyle style;
	
	public static final BooleanProperty INVERTED = BooleanProperty.of("inverted");
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(appendMoreProperties(builder.add(INVERTED)));
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(player.getStackInHand(hand).getItem() == Items.REDSTONE_TORCH) {
			world.setBlockState(pos, state.cycle(INVERTED));
			return ActionResult.SUCCESS; //todo should it be the weird one that takes world.isclient for some reason
		}
		return ActionResult.PASS;
	}
	
	protected abstract StateManager.Builder<Block, BlockState> appendMoreProperties(StateManager.Builder<Block, BlockState> builder);
	
	@Override
	public abstract BlockState getPlacementState(ItemPlacementContext ctx);
	
	@Override
	public abstract void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify);
	
	public static class Analog extends LampBlock {
		public Analog(LampStyle style, Settings settings) {
			super(style, settings.luminance(Analog::getLightValueForState));
			setDefaultState(getDefaultState().with(POWER, 0));
		}
		
		public static final IntProperty POWER = IntProperty.of("power", 0, 15);
		
		@Override
		protected StateManager.Builder<Block, BlockState> appendMoreProperties(StateManager.Builder<Block, BlockState> builder) {
			return builder.add(POWER);
		}
		
		private static int getLightValueForState(BlockState state) {
			return state.get(INVERTED) ? 15 - state.get(POWER) : state.get(POWER);
		}
		
		@Override
		public BlockState getPlacementState(ItemPlacementContext ctx) {
			World world = ctx.getWorld();
			BlockPos pos = ctx.getBlockPos();
			return getDefaultState().with(POWER, world.getReceivedRedstonePower(pos));
		}
		
		@Override
		public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
			if(world.isClient) return;
			
			int newPower = world.getReceivedRedstonePower(pos);
			int oldPower = state.get(POWER);
			if(newPower != oldPower) {
				world.setBlockState(pos, state.with(POWER, newPower), 2);
			}
		}
	}
	
	public static class Digital extends LampBlock {
		public Digital(LampStyle style, Settings settings) {
			super(style, settings.luminance(Digital::getLightValueForState));
			setDefaultState(getDefaultState().with(POWERED, false));
		}
		
		public static final BooleanProperty POWERED = Properties.POWERED;
		
		@Override
		protected StateManager.Builder<Block, BlockState> appendMoreProperties(StateManager.Builder<Block, BlockState> builder) {
			return builder.add(POWERED);
		}
		
		public static int getLightValueForState(BlockState state) {
			return state.get(INVERTED) ^ state.get(POWERED) ? 15 : 0;
		}
		
		@Override
		public BlockState getPlacementState(ItemPlacementContext ctx) {
			World world = ctx.getWorld();
			BlockPos pos = ctx.getBlockPos();
			return getDefaultState().with(POWERED, world.isReceivingRedstonePower(pos));
		}
		
		@Override
		public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
			if(world.isClient) return;
			
			boolean newPowered = world.isReceivingRedstonePower(pos);
			boolean wasPowered = state.get(POWERED);
			if(newPowered != wasPowered) {
				world.setBlockState(pos, state.with(POWERED, newPowered), 2);
			}
		}
	}
}

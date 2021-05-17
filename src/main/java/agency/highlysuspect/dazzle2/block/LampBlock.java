package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.LampStyle;
import agency.highlysuspect.dazzle2.item.DazzleItemTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class LampBlock extends ColorHolderBlock.Simple {
	public LampBlock(LampStyle style, Settings settings) {
		super(style.color.color, settings);
		this.style = style;
		
		setDefaultState(getDefaultState().with(INVERTED, false));
	}
	
	public final LampStyle style;
	
	public static final BooleanProperty INVERTED = BooleanProperty.of("inverted");
	
	@Environment(EnvType.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return style.theme.isTransparent && (stateFrom.isOf(this) || super.isSideInvisible(state, stateFrom, direction));
	}
	
	@Override
	public abstract void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify);
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(player.getStackInHand(hand).getItem().isIn(DazzleItemTags.WRENCHES)) {
			boolean newInverted = !state.get(INVERTED);
			world.setBlockState(pos, state.with(INVERTED, newInverted));
			world.playSound(player, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.9f, newInverted ? 1.3f : 1f);
			return ActionResult.SUCCESS; //todo should it be the weird one that takes world.isclient for some reason
		}
		return ActionResult.PASS;
	}
	
	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1f;
	}
	
	protected abstract StateManager.Builder<Block, BlockState> appendMoreProperties(StateManager.Builder<Block, BlockState> builder);
	
	@Override
	public abstract BlockState getPlacementState(ItemPlacementContext ctx);
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(appendMoreProperties(builder.add(INVERTED)));
	}
	
	public abstract int lightFromState(BlockState state);
	
	public static class Analog extends LampBlock {
		public Analog(LampStyle style, Settings settings) {
			super(style, settings.luminance(Analog::lightFromStateStatic));
			setDefaultState(getDefaultState().with(POWER, 0));
		}
		
		public static final IntProperty POWER = IntProperty.of("power", 0, 15);
		
		//annoyingly this is used in reference to a block.settings in the super() call, where non-static method references are illegal.
		private static int lightFromStateStatic(BlockState state) {
			return state.get(INVERTED) ? 15 - state.get(POWER) : state.get(POWER);
		}
		
		@Override
		protected StateManager.Builder<Block, BlockState> appendMoreProperties(StateManager.Builder<Block, BlockState> builder) {
			return builder.add(POWER);
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
		
		@Override
		public int lightFromState(BlockState state) {
			return state.get(INVERTED) ? 15 - state.get(POWER) : state.get(POWER);
		}
	}
	
	public static class Digital extends LampBlock {
		public Digital(LampStyle style, Settings settings) {
			super(style, settings.luminance(Digital::lightFromStateStatic));
			setDefaultState(getDefaultState().with(POWERED, false));
		}
		
		public static final BooleanProperty POWERED = Properties.POWERED;
		
		public static int lightFromStateStatic(BlockState state) {
			return state.get(INVERTED) ^ state.get(POWERED) ? 15 : 0;
		}
		
		@Override
		protected StateManager.Builder<Block, BlockState> appendMoreProperties(StateManager.Builder<Block, BlockState> builder) {
			return builder.add(POWERED);
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
		
		@Override
		public int lightFromState(BlockState state) {
			return state.get(INVERTED) ^ state.get(POWERED) ? 15 : 0;
		}
	}
}

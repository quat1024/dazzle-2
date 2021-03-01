package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.block.entity.DazzleBlockEntityTypes;
import agency.highlysuspect.dazzle2.block.entity.NonplaceableHiddenLightBlockEntity;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HiddenLightBlock extends Block {
	public HiddenLightBlock(Settings settings) {
		super(settings);
		
		setDefaultState(getDefaultState().with(LIGHT, 15));
	}
	
	public static final IntProperty LIGHT = IntProperty.of("light", 1, 15);
	public static final VoxelShape SHAPE = VoxelShapes.cuboid(3 / 16d, 3 / 16d, 3 / 16d, 13 / 16d, 13 / 16d, 13 / 16d);
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(LIGHT));
	}
	
	public BlockState withLightLevel(int power) {
		if(power <= 0) {
			return Blocks.AIR.getDefaultState();
		} else return getDefaultState().with(LIGHT, power);
	}
	
	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack held = player.getStackInHand(hand);
		if(held.getItem() == Items.REDSTONE_TORCH) { //TODO use an item tag, and also other related uses of redstone torch item. also, wrenches?
			int currentLevel = state.get(LIGHT);
			int nextLevel = currentLevel + (hit.getSide().getAxis() == Direction.Axis.Y ? -1 : 1);
			
			if(nextLevel == 0) nextLevel = 15;
			if(nextLevel == 16) nextLevel = 1;
			
			world.setBlockState(pos, state.with(LIGHT, nextLevel));
			world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.6f, nextLevel / 15f + 0.5f);
			return ActionResult.SUCCESS;
		}
		
		else return super.onUse(state, world, pos, player, hand, hit);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}
	
	public static class Nonplaceable extends HiddenLightBlock implements BlockEntityProvider {
		public Nonplaceable(Settings settings) {
			super(settings);
		}
		
		@Override
		public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
			//No touchy!
			return ActionResult.PASS;
		}
		
		@Override
		public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
			check(world, pos);
		}
		
		@Override
		public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
			recursiveCheck(world, pos);
		}
		
		private static boolean checking = false;
		private void recursiveCheck(World world, BlockPos pos) {
			if(checking) return;
			
			try {
				checking = true;
				
				Deque<BlockPos> positionsToScan = new ArrayDeque<>();
				positionsToScan.addLast(pos);
				Set<BlockPos> positionsToCheck = new HashSet<>();
				
				while(!positionsToScan.isEmpty()) {
					BlockPos scan = positionsToScan.removeFirst();
					positionsToScan.remove(scan);
					positionsToCheck.add(scan);
					
					for(Direction d : Direction.values()) {
						BlockPos hoo = scan.offset(d);
						BlockState state = world.getBlockState(hoo);
						if(state.isOf(DazzleBlocks.PROJECTED_LIGHT_PANEL) || state.isOf(DazzleBlocks.NONPLACEABLE_HIDDEN_LIGHT)) {
							//                                     This sucks, it's a linear search
							if(!positionsToCheck.contains(hoo) && !positionsToScan.contains(hoo)) positionsToScan.add(hoo);
						}
					}
				}
				
				//and make them all check
				for(BlockPos hoo : positionsToCheck) {
					BlockState state = world.getBlockState(hoo);
					if(state.isOf(DazzleBlocks.PROJECTED_LIGHT_PANEL)) {
						DazzleBlocks.PROJECTED_LIGHT_PANEL.check(world, hoo, state);
					} else { //always a fellow projected light
						check(world, hoo);
					}
				}
				
			} finally {
				checking = false;
			}
		}
		
		private TriState check(World world, BlockPos pos) {
			NonplaceableHiddenLightBlockEntity be = DazzleBlockEntityTypes.NONPLACEABLE_HIDDEN_LIGHT.get(world, pos);
			if(be != null) return be.check();
			else return TriState.DEFAULT;
		}
		
		@Override
		public @Nullable BlockEntity createBlockEntity(BlockView world) {
			return DazzleBlockEntityTypes.NONPLACEABLE_HIDDEN_LIGHT.instantiate();
		}
		
		@Override
		public boolean canReplace(BlockState state, ItemPlacementContext context) {
			return true;
		}
		
		public void placeWithOwner(World world, BlockPos pos, int level, BlockPos ownerPos) {
			world.setBlockState(pos, withLightLevel(level), 2 | 16); //don't emit block updates | update listeners
			NonplaceableHiddenLightBlockEntity be = DazzleBlockEntityTypes.NONPLACEABLE_HIDDEN_LIGHT.get(world, pos);
			if(be != null) be.setOwner(ownerPos);
		}
	}
}

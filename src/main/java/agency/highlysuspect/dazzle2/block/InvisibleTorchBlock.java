package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.Junk;
import agency.highlysuspect.dazzle2.item.DazzleItemTags;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InvisibleTorchBlock extends Block {
	public InvisibleTorchBlock(Settings settings) {
		super(settings);
		
		setDefaultState(getDefaultState().with(LIGHT, 15).with(FACING, Direction.DOWN));
	}
	
	public static final IntProperty LIGHT = IntProperty.of("light", 1, 15);
	public static final DirectionProperty FACING = DirectionProperty.of("facing", d -> d != Direction.UP);
	public static final VoxelShape SHAPE = VoxelShapes.cuboid(3 / 16d, 3 / 16d, 3 / 16d, 13 / 16d, 13 / 16d, 13 / 16d);
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(LIGHT, FACING));
	}
	
	public BlockState makeInvisible(World world, BlockPos pos, BlockState torchState) {
		int light = world.getLightLevel(LightType.BLOCK, pos);
		Direction d = torchState.isOf(Blocks.WALL_TORCH) ? torchState.get(WallTorchBlock.FACING) : Direction.DOWN;
		return getDefaultState().with(LIGHT, light).with(FACING, d);
	}
	
	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		return Junk.whatWouldTorchusDo(ctx).map(direction -> getDefaultState().with(LIGHT, 14).with(FACING, direction)).orElse(null);
	}
	
	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack held = player.getStackInHand(hand);
		if(held.getItem().isIn(DazzleItemTags.WRENCHES)) {
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
		if(state.get(FACING) == Direction.DOWN) {
			return Blocks.TORCH.getOutlineShape(Blocks.TORCH.getDefaultState(), world, pos, context);
		} else {
			return Blocks.WALL_TORCH.getOutlineShape(Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, state.get(FACING)), world, pos, context);
		}
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}
	
	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return false;
	}
}

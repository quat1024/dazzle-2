package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.Junk;
import agency.highlysuspect.dazzle2.LampStyle;
import agency.highlysuspect.dazzle2.item.DazzleItemTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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

import java.util.List;

public class InvisibleTorchBlock extends Block {
	public InvisibleTorchBlock(Settings settings) {
		super(settings);
		
		setDefaultState(getDefaultState().with(LIGHT, 15).with(FACING, Direction.DOWN));
	}
	
	public static final IntProperty LIGHT = IntProperty.of("light", 1, 15);
	public static final DirectionProperty FACING = DirectionProperty.of("facing", d -> d != Direction.UP);
	
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
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(LIGHT, FACING));
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack held = player.getStackInHand(hand);
		
		if(Init.DEBUG && held.getItem() == Items.BEDROCK) {
			for(int i = 0; i < LampStyle.Theme.ALL.size(); i++) {
				LampStyle.Theme theme = LampStyle.Theme.ALL.get(i);
				int themeX = i * 4;
				for(int j = 0; j < LampStyle.Color.ALL.size(); j++) {
					LampStyle.Color color = LampStyle.Color.ALL.get(j);
					
					int colorX = j / 4;
					int colorZ = j % 4;
					
					world.setBlockState(pos.add(themeX + colorX, 0, colorZ), LampStyle.findLampBlockstate(color, theme, LampStyle.Mode.DIGITAL));
					world.setBlockState(pos.add(themeX + colorX, 0, colorZ + 4), LampStyle.findLampBlockstate(color, theme, LampStyle.Mode.DIGITAL).with(LampBlock.INVERTED, true));
					world.setBlockState(pos.add(themeX + colorX, 0, colorZ + 9), LampStyle.findLampBlockstate(color, theme, LampStyle.Mode.ANALOG));
					world.setBlockState(pos.add(themeX + colorX, 0, colorZ + 13), LampStyle.findLampBlockstate(color, theme, LampStyle.Mode.ANALOG).with(LampBlock.INVERTED, true));
				}
			}
		}
		
		if(held.getItem().isIn(DazzleItemTags.WRENCHES)) {
			int currentLevel = state.get(LIGHT);
			int nextLevel = currentLevel + (hit.getSide().getAxis() == Direction.Axis.Y ? -1 : 1);
			
			if(nextLevel == 0) nextLevel = 15;
			if(nextLevel == 16) nextLevel = 1;
			
			world.setBlockState(pos, state.with(LIGHT, nextLevel));
			world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.6f, nextLevel / 15f + 0.5f);
			return ActionResult.SUCCESS;
		} else return super.onUse(state, world, pos, player, hand, hit);
	}
	
	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}
	
	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return false;
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
	
	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
		tooltip.add(new TranslatableText("dazzle.craft_invisible_torch"));
	}
}

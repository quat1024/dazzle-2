package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.LampStyle;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import java.util.List;
import java.util.stream.Collectors;

public class DazzleBlocks {
	public static final List<LampBlock> LAMPS = LampStyle.ALL.stream()
		.map(style -> style.instantiateBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_LAMP)))
		.collect(Collectors.toList());
	
	public static final LightSensorBlock LIGHT_SENSOR = new LightSensorBlock(FabricBlockSettings.copyOf(Blocks.OBSERVER));
	public static final InvisibleTorchBlock INVISIBLE_TORCH = new InvisibleTorchBlock(FabricBlockSettings.copyOf(Blocks.TORCH)
		.nonOpaque().noCollision().breakByHand(true).breakInstantly()
		.luminance(state -> state.get(InvisibleTorchBlock.LIGHT))
		.suffocates((state, world, pos) -> false)
		.blockVision((state, world, pos) -> false)
	);
	public static final LightAirBlock LIGHT_AIR = new LightAirBlock(FabricBlockSettings.of(Material.AIR)
		.nonOpaque().noCollision().breakByHand(true).breakInstantly()
		.luminance(state -> state.get(LightAirBlock.LIGHT))
		.suffocates((state, world, pos) -> false)
		.blockVision((state, world, pos) -> false)
		.air() //is this a good idea?
		.dropsNothing()
		.ticksRandomly()
	);
	public static final ProjectedLightPanelBlock PROJECTED_LIGHT_PANEL = new ProjectedLightPanelBlock(FabricBlockSettings.copyOf(Blocks.BONE_BLOCK)
		.luminance(state -> state.get(ProjectedLightPanelBlock.POWER))
		.ticksRandomly()
	);
	
	public static final RedstoneTorchBlock DIM_REDSTONE_TORCH = new RedstoneTorchBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_TORCH)
		.luminance(state -> state.get(Properties.LIT) ? 2 : 0)
	){
		//Protected constructor lmao, also i need to override this anyways
		@Override
		public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
			return super.getWeakRedstonePower(state, world, pos, direction) == 15 ? 1 : 0;
		}
	};
	
	public static final RedstoneTorchBlock DIM_REDSTONE_WALL_TORCH = new WallRedstoneTorchBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_WALL_TORCH)
		.luminance(state -> state.get(Properties.LIT) ? 2 : 0)
		.dropsLike(DIM_REDSTONE_TORCH)
	){
		//Protected constructor lmao, also i need to override this anyways
		@Override
		public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
			return super.getWeakRedstonePower(state, world, pos, direction) == 15 ? 1 : 0;
		}
	};
	
	public static void onInitialize() {
		for(LampBlock lamp : LAMPS) {
			Registry.register(Registry.BLOCK, lamp.style.toIdentifier(), lamp);
		}
		
		Registry.register(Registry.BLOCK, Init.id("light_sensor"), LIGHT_SENSOR);
		Registry.register(Registry.BLOCK, Init.id("invisible_torch"), INVISIBLE_TORCH);
		Registry.register(Registry.BLOCK, Init.id("light_air"), LIGHT_AIR);
		Registry.register(Registry.BLOCK, Init.id("projected_light_panel"), PROJECTED_LIGHT_PANEL);
		
		Registry.register(Registry.BLOCK, Init.id("dim_redstone_torch"), DIM_REDSTONE_TORCH);
		Registry.register(Registry.BLOCK, Init.id("dim_redstone_wall_torch"), DIM_REDSTONE_WALL_TORCH);
	}
}

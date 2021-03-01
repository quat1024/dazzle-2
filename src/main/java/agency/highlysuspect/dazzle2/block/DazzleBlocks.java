package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.LampStyle;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

public class DazzleBlocks {
	public static final List<LampBlock> LAMPS = LampStyle.ALL.stream()
		.map(style -> style.instantiateBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_LAMP)))
		.collect(Collectors.toList());
	
	public static final LightSensorBlock LIGHT_SENSOR = new LightSensorBlock(FabricBlockSettings.copyOf(Blocks.OBSERVER));
	public static final HiddenLightBlock PLACEABLE_HIDDEN_LIGHT = new HiddenLightBlock(FabricBlockSettings.of(Material.AIR)
		.nonOpaque().noCollision().breakByHand(true).breakInstantly()
		.luminance(state -> state.get(HiddenLightBlock.LIGHT))
		.suffocates((state, world, pos) -> false)
		.blockVision((state, world, pos) -> false)
	);
	public static final HiddenLightBlock.Nonplaceable NONPLACEABLE_HIDDEN_LIGHT = new HiddenLightBlock.Nonplaceable(FabricBlockSettings.of(Material.AIR)
		.nonOpaque().noCollision().breakByHand(true).breakInstantly()
		.luminance(state -> state.get(HiddenLightBlock.LIGHT))
		.suffocates((state, world, pos) -> false)
		.blockVision((state, world, pos) -> false)
		.dropsNothing()
		.ticksRandomly()
	);
	public static final ProjectedLightPanelBlock PROJECTED_LIGHT_PANEL = new ProjectedLightPanelBlock(FabricBlockSettings.copyOf(Blocks.BONE_BLOCK)
		.luminance(state -> state.get(ProjectedLightPanelBlock.POWER))
		.ticksRandomly()
	);
	
	public static void onInitialize() {
		for(LampBlock lamp : LAMPS) {
			Registry.register(Registry.BLOCK, lamp.style.toIdentifier(), lamp);
		}
		
		Registry.register(Registry.BLOCK, Init.id("light_sensor"), LIGHT_SENSOR);
		Registry.register(Registry.BLOCK, Init.id("placeable_hidden_light"), PLACEABLE_HIDDEN_LIGHT);
		Registry.register(Registry.BLOCK, Init.id("nonplaceable_hidden_light"), NONPLACEABLE_HIDDEN_LIGHT);
		Registry.register(Registry.BLOCK, Init.id("projected_light_panel"), PROJECTED_LIGHT_PANEL);
	}
}

package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.LampStyle;
import agency.highlysuspect.dazzle2.block.entity.LightSensorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

public class DazzleBlocks {
	public static final List<LampBlock> LAMPS = LampStyle.ALL.stream()
		.map(style -> style.instantiateBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_LAMP)))
		.collect(Collectors.toList());
	
	public static final LightSensorBlock LIGHT_SENSOR = new LightSensorBlock(FabricBlockSettings.copyOf(Blocks.OBSERVER));
	
	public static void onInitialize() {
		for(LampBlock lamp : LAMPS) {
			Registry.register(Registry.BLOCK, lamp.style.toIdentifier(), lamp);
		}
		
		Registry.register(Registry.BLOCK, Init.id("light_sensor"), LIGHT_SENSOR);
		
		LightSensorBlockEntity.registerBlockEntityType();
	}
}

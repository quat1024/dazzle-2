package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.LampStyle;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

public class DazzleBlocks {
	public static final List<LampBlock> LAMPS = LampStyle.ALL.stream()
		.map(style -> style.instantiateBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_LAMP)))
		.collect(Collectors.toList());
	
	public static void onInitialize() {
		for(LampBlock lamp : LAMPS) {
			Registry.register(Registry.BLOCK, lamp.style.toIdentifier(), lamp);
		}
	}
}

package agency.highlysuspect.dazzle2.block.entity;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class DazzleBlockEntityTypes {
	public static final BlockEntityType<LightSensorBlockEntity> LIGHT_SENSOR = BlockEntityType.Builder.create(LightSensorBlockEntity::new, DazzleBlocks.LIGHT_SENSOR).build(null);
	public static final BlockEntityType<LightAirBlockEntity> LIGHT_AIR = BlockEntityType.Builder.create(LightAirBlockEntity::new, DazzleBlocks.LIGHT_AIR).build(null);
	
	public static void onInitialize() {
		Registry.register(Registry.BLOCK_ENTITY_TYPE, Init.id("light_sensor"), LIGHT_SENSOR);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, Init.id("light_air"), LIGHT_AIR);
	}
}

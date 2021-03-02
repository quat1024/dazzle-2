package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.Init;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;

public class DazzleBlockTags {
	public static final Tag<Block> MAKE_INVISIBLE_TORCH = TagRegistry.block(Init.id("make_invisible_torches"));
	
	public static void onInitialize() {
		//run static init
	}
}

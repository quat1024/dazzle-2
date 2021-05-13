package agency.highlysuspect.dazzle2.item;

import agency.highlysuspect.dazzle2.Init;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class DazzleItemTags {
	public static final Tag<Item> WRENCHES = TagRegistry.item(Init.id("wrenches"));
	
	public static void onInitialize() {
		//run static init
	}
}

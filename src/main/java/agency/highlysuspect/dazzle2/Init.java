package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import agency.highlysuspect.dazzle2.item.DazzleItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Init implements ModInitializer {
	public static final String MODID = "dazzle";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	@Override
	public void onInitialize() {
		DazzleBlocks.onInitialize();
		DazzleItems.onInitialize();
	}
	
	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
	
	public static void log(String msg, Object... fmt) {
		LOGGER.info(msg, fmt);
	}
}

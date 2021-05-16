package agency.highlysuspect.dazzle2;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

import java.util.HashMap;
import java.util.Map;

public class GenUtil {
	private static final Map<DyeColor, Item> COLORS_TO_ITEMS = new HashMap<>();
	
	static {
		COLORS_TO_ITEMS.put(DyeColor.WHITE, Items.WHITE_DYE);
		COLORS_TO_ITEMS.put(DyeColor.ORANGE, Items.ORANGE_DYE);
		COLORS_TO_ITEMS.put(DyeColor.MAGENTA, Items.MAGENTA_DYE);
		COLORS_TO_ITEMS.put(DyeColor.LIGHT_BLUE, Items.LIGHT_BLUE_DYE);
		COLORS_TO_ITEMS.put(DyeColor.YELLOW, Items.YELLOW_DYE);
		COLORS_TO_ITEMS.put(DyeColor.LIME, Items.LIME_DYE);
		COLORS_TO_ITEMS.put(DyeColor.PINK, Items.PINK_DYE);
		COLORS_TO_ITEMS.put(DyeColor.GRAY, Items.GRAY_DYE);
		COLORS_TO_ITEMS.put(DyeColor.LIGHT_GRAY, Items.LIGHT_GRAY_DYE);
		COLORS_TO_ITEMS.put(DyeColor.CYAN, Items.CYAN_DYE);
		COLORS_TO_ITEMS.put(DyeColor.PURPLE, Items.PURPLE_DYE);
		COLORS_TO_ITEMS.put(DyeColor.BLUE, Items.BLUE_DYE);
		COLORS_TO_ITEMS.put(DyeColor.BROWN, Items.BROWN_DYE);
		COLORS_TO_ITEMS.put(DyeColor.GREEN, Items.GREEN_DYE);
		COLORS_TO_ITEMS.put(DyeColor.RED, Items.RED_DYE);
		COLORS_TO_ITEMS.put(DyeColor.BLACK, Items.BLACK_DYE);
	}
	
	public static Item dyeForColor(DyeColor color) {
		return COLORS_TO_ITEMS.get(color);
	}
}

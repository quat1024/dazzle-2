package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.mixin.WallStandingBlockItemMixin;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class Junk {
	public static Identifier mapPath(Identifier id, UnaryOperator<String> mapper) {
		return new Identifier(id.getNamespace(), mapper.apply(id.getPath()));
	}
	
	public static Optional<Direction> whatWouldTorchusDo(ItemPlacementContext ctx) {
		WallStandingBlockItem wsbi = (WallStandingBlockItem) Items.TORCH;
		BlockState torchState = ((WallStandingBlockItemMixin) wsbi).funkyGetPlacementState(ctx);
		
		if(torchState == null) return Optional.empty();
		
		if(torchState.isOf(Blocks.WALL_TORCH)) {
			return Optional.of(torchState.get(WallTorchBlock.FACING));
		} else if(torchState.isOf(Blocks.TORCH)) {
			return Optional.of(Direction.DOWN);
		} else return Optional.empty();
	}
	
	public static String prettyPrintDyeColor(DyeColor color, boolean murica) {
		String nameLowercase = color.getName().replace('_', ' ');
		if(!murica) nameLowercase = nameLowercase.replaceAll("gray", "grey");
		return WordUtils.capitalizeFully(nameLowercase);
	}
	
	public static float rangeRemap(float value, float low1, float high1, float low2, float high2) {
		float value2 = MathHelper.clamp(value, low1, high1);
		return low2 + (value2 - low1) * (high2 - low2) / (high1 - low1);
	}
	
	private static final Map<DyeColor, Identifier> COLORS_TO_ITEM_IDS = new HashMap<>();
	
	static {
		COLORS_TO_ITEM_IDS.put(DyeColor.WHITE, new Identifier("minecraft", "white_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.ORANGE, new Identifier("minecraft", "orange_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.MAGENTA, new Identifier("minecraft", "magenta_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.LIGHT_BLUE, new Identifier("minecraft", "light_blue_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.YELLOW, new Identifier("minecraft", "yellow_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.LIME, new Identifier("minecraft", "lime_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.PINK, new Identifier("minecraft", "pink_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.GRAY, new Identifier("minecraft", "gray_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.LIGHT_GRAY, new Identifier("minecraft", "light_gray_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.CYAN, new Identifier("minecraft", "cyan_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.PURPLE, new Identifier("minecraft", "purple_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.BLUE, new Identifier("minecraft", "blue_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.BROWN, new Identifier("minecraft", "brown_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.GREEN, new Identifier("minecraft", "green_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.RED, new Identifier("minecraft", "red_dye"));
		COLORS_TO_ITEM_IDS.put(DyeColor.BLACK, new Identifier("minecraft", "black_dye"));
	}
	
	public static Optional<Identifier> itemIdForDye(DyeColor color) {
		if(COLORS_TO_ITEM_IDS.containsKey(color)) {
			return Optional.of(COLORS_TO_ITEM_IDS.get(color));
		}
		
		//I heard talk of mods that add their own dye colors by extending the enum.
		//Who knows if that will come to fruition, but let's assume this hypothetical mod uses the
		//same name format to the other minecraft dyes (that is, "somemod:flurple_dye", for example)
		//Also yes, this ridiculous hypothetical is the only reason this method returns an Optional
		String path = color.getName() + "_dye";
		for(ModContainer mc : FabricLoader.getInstance().getAllMods()) {
			Identifier maybeId = new Identifier(mc.getMetadata().getId(), path);
			if(Registry.ITEM.containsId(maybeId)) {
				COLORS_TO_ITEM_IDS.put(color, maybeId);
				return Optional.of(maybeId);
			}
		}
		
		Init.LOGGER.error("Can't find an item corresponding to the dye " + color);
		return Optional.empty();
	}
}

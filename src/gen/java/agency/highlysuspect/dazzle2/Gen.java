package agency.highlysuspect.dazzle2;

import com.google.gson.JsonObject;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Gen {
	public static void main(String[] args) throws IOException {
		System.out.println("RUNNING THE DATA GENERATOR!!!!!!!!!!!!!!!!!");
		
		System.out.println("arguments!!!");
		for(String arg : args) {
			System.out.print(arg);
			System.out.print(" ");
		}
		System.out.println();
		
		//Option parser doesn't work LOL
		
//		OptionParser uwu = new OptionParser();
//		OptionSpec<String> output = uwu.accepts("output", "Output file to put things into").withRequiredArg();
//		uwu.printHelpOn(System.out);
//		OptionSet set = uwu.parse(args);
//		if(set.hasOptions() && set.hasArgument(output)) {
//			Path p = Paths.get(output.value(set));
//			System.out.println("Running in path " + p);
//		} else {
//			System.out.println("NOT RUNNING!!!!!");
//			System.exit(50);
//		}
		Path output = Paths.get(args[0]).toAbsolutePath().normalize();
		System.out.println("OUTPUT PATH: " + output);
		
		//TRYING TO USE DATA GENERATORS BUT THEY'RE TOO SPAGHETTYO IN FABRIC, SO HAVE SOME HANDROLLED CRAP !!!!
		
		for(LampStyle style : LampStyle.ALL) {
			String jason = style.toName() + ".json";
			
			write(output, "assets/dazzle/blockstates/" + jason, lampBlockstate(style));
			write(output, "assets/dazzle/models/item/" + jason, lampItemModel(style));
			
			write(output, "data/dazzle/loot_tables/blocks/" + jason, simpleLootTable(style.toIdentifier().toString()));
			write(output, "data/dazzle/recipes/lamps/" + jason, lampRecipe(style));
			write(output, "data/dazzle/advancements/recipes/lamps/" + jason, lampRecipeAdvancement(style));
		}
		
		for(DyeColor color : DyeColor.values()) {
			String itemId = "dazzle:" + color.getName() + "_flare";
			String jason = color.getName() + "_flare.json";
			
			write(output, "assets/dazzle/blockstates/" + jason, flareBlockstate());
			write(output, "assets/dazzle/models/item/" + jason, flareItemModel());
			
			write(output, "data/dazzle/loot_tables/blocks/" + jason, simpleLootTable(itemId));
			write(output, "data/dazzle/recipes/flare/" + jason, flareRecipe(color, itemId));
			write(output, "data/dazzle/advancements/recipes/flare/" + jason, flareRecipeAdvancement(color));
		}
		
		String muricaLang = generateLang(true);
		String otherLang = generateLang(false);
		
		//Can't really merge the en_us contents into the existing en_us lang file.
		write(output, "en_us_include.json", muricaLang);
		
		write(output, "assets/dazzle/lang/en_au.json", otherLang);
		write(output, "assets/dazzle/lang/en_ca.json", otherLang);
		write(output, "assets/dazzle/lang/en_gb.json", otherLang);
		write(output, "assets/dazzle/lang/en_nz.json", otherLang);
		
		System.out.println("Don't forget to copy en_us_include.json into en_us.json!!!!!!!!!!!!!!");
	}
	
	private static void write(Path root, String next, String contents) {
		Path target = root.resolve(next);
		System.out.println("Writing to " + target);
		
		try {
			Files.createDirectories(target.getParent());
			Files.write(target, contents.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String lampBlockstate(LampStyle style) {
		String template = "{\n" +
			"\t\"variants\": {\n" +
			"\t\t\"\": {\n" +
			"\t\t\t\"model\": \"dazzle:block/lamp/XXX\"\n" +
			"\t\t}\n" +
			"\t}\n" +
			"}";
		
		return template.replace("XXX", style.theme.name);
	}
	
	private static String lampItemModel(LampStyle style) {
		String template = "{\n" +
			"\t\"parent\": \"dazzle:block/lamp/XXX\"\n" +
			"}";
		
		return template.replace("XXX", style.theme.name);
	}
	
	private static String simpleLootTable(String s) {
		String template = "{\n" +
			"\t\"type\": \"minecraft:block\",\n" +
			"\t\"pools\": [\n" +
			"\t\t{\n" +
			"\t\t\t\"rolls\": 1,\n" +
			"\t\t\t\"entries\": [\n" +
			"\t\t\t\t{\n" +
			"\t\t\t\t\t\"type\": \"minecraft:item\",\n" +
			"\t\t\t\t\t\"name\": \"XXX\"\n" +
			"\t\t\t\t}\n" +
			"\t\t\t],\n" +
			"\t\t\t\"conditions\": [\n" +
			"\t\t\t\t{\n" +
			"\t\t\t\t\t\"condition\": \"minecraft:survives_explosion\"\n" +
			"\t\t\t\t}\n" +
			"\t\t\t]\n" +
			"\t\t}\n" +
			"\t]\n" +
			"}";
		
		return template.replace("XXX", s);
	}
	
	private static String getRequiredItem(LampStyle style) {
		switch(style.theme.name) {
			case "icy": return "minecraft:ice";
			case "lantern": return "minecraft:prismarine_crystals";
			case "modern": return "minecraft:stone_pressure_plate";
			case "pulsating": return "minecraft:end_rod";
			default: throw new IllegalArgumentException(style.theme.name);
		}
	}
	
	@SuppressWarnings("DuplicateExpressions") //just fuck me up fam
	private static String lampRecipe(LampStyle style) {
		String noExtraItem = "{\n" +
			"\t\"type\": \"minecraft:crafting_shapeless\",\n" +
			"\t\"group\": \"GROUP\",\n" +
			"\t\"ingredients\": [\n" +
			"\t\t{\n" +
			"\t\t\t\"item\": \"IN\"\n" +
			"\t\t},\n" +
			"\t\t{\n" +
			"\t\t\t\"item\": \"minecraft:redstone_lamp\"\n" +
			"\t\t}\n" +
			"\t],\n" +
			"\t\"result\": {\n" +
			"\t\t\"item\": \"OUT\"\n" +
			"\t}\n" +
			"}\n";
		
		String hasExtraItem = "{\n" +
			"\t\"type\": \"minecraft:crafting_shapeless\",\n" +
			"\t\"group\": \"GROUP\",\n" +
			"\t\"ingredients\": [\n" +
			"\t\t{\n" +
			"\t\t\t\"item\": \"IN\"\n" +
			"\t\t},\n" +
			"\t\t{\n" +
			"\t\t\t\"item\": \"minecraft:redstone_lamp\"\n" +
			"\t\t},\n" +
			"\t\t{\n" +
			"\t\t\t\"item\": \"EXTRA\"\n" +
			"\t\t}\n" +
			"\t],\n" +
			"\t\"result\": {\n" +
			"\t\t\"item\": \"OUT\"\n" +
			"\t}\n" +
			"}\n";
		
		String convertToAnalog = "{\n" +
			"\t\"type\": \"minecraft:crafting_shapeless\",\n" +
			"\t\"group\": \"GROUP\",\n" +
			"\t\"ingredients\": [\n" +
			"\t\t{\n" +
			"\t\t\t\"item\": \"IN\"\n" +
			"\t\t},\n" +
			"\t\t{\n" +
			"\t\t\t\"item\": \"minecraft:comparator\"\n" +
			"\t\t}\n" +
			"\t],\n" +
			"\t\"result\": {\n" +
			"\t\t\"item\": \"OUT\"\n" +
			"\t}\n" +
			"}\n";
		
		if(style.mode == LampStyle.Mode.ANALOG) {
			return convertToAnalog
				.replace("GROUP", style.theme.name + '_' + style.mode.name + "_analog_convert")
				.replace("IN", style.withMode(LampStyle.Mode.DIGITAL).toIdentifier().toString())
				.replace("OUT", style.toIdentifier().toString());
		} else {
			if(style.theme == LampStyle.Theme.CLASSIC) {
				return noExtraItem
					.replace("GROUP", style.theme.name + '_' + style.mode.name)
					.replace("IN", style.color.findItemId().get().toString())
					.replace("OUT", style.toIdentifier().toString());
			} else {
				return hasExtraItem
					.replace("EXTRA", getRequiredItem(style))
					.replace("GROUP", style.theme.name + '_' + style.mode.name)
					.replace("IN", style.color.findItemId().get().toString())
					.replace("OUT", style.toIdentifier().toString());
			}
		}
	}
	
	private static String lampRecipeAdvancement(LampStyle style) {
		String noExtraItem = "{\n" +
			"\t\"parent\": \"dazzle:recipes/root\",\n" +
			"\t\"rewards\": {\n" +
			"\t\t\"recipes\": [\n" +
			"\t\t\t\"dazzle:lamps/OUT\"\n" +
			"\t\t]\n" +
			"\t},\n" +
			"\t\"criteria\": {\n" +
			"\t\t\"has_lamp\": {\n" +
			"\t\t\t\"trigger\": \"minecraft:inventory_changed\",\n" +
			"\t\t\t\"conditions\": {\n" +
			"\t\t\t\t\"items\": [\n" +
			"\t\t\t\t\t{\n" +
			"\t\t\t\t\t\t\"item\": \"redstone_lamp\"\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t]\n" +
			"\t\t\t}\n" +
			"\t\t},\n" +
			"\t\t\"has_dye\": {\n" +
			"\t\t\t\"trigger\": \"minecraft:inventory_changed\",\n" +
			"\t\t\t\"conditions\": {\n" +
			"\t\t\t\t\"items\": [\n" +
			"\t\t\t\t\t{\n" +
			"\t\t\t\t\t\t\"item\": \"IN\"\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t]\n" +
			"\t\t\t}\n" +
			"\t\t}\n" +
			"\t}\n" +
			"}";
		
		String hasExtraItem = "{\n" +
			"\t\"parent\": \"dazzle:recipes/root\",\n" +
			"\t\"rewards\": {\n" +
			"\t\t\"recipes\": [\n" +
			"\t\t\t\"dazzle:lamps/OUT\"\n" +
			"\t\t]\n" +
			"\t},\n" +
			"\t\"criteria\": {\n" +
			"\t\t\"has_lamp\": {\n" +
			"\t\t\t\"trigger\": \"minecraft:inventory_changed\",\n" +
			"\t\t\t\"conditions\": {\n" +
			"\t\t\t\t\"items\": [\n" +
			"\t\t\t\t\t{\n" +
			"\t\t\t\t\t\t\"item\": \"redstone_lamp\"\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t]\n" +
			"\t\t\t}\n" +
			"\t\t},\n" +
			"\t\t\"has_dye\": {\n" +
			"\t\t\t\"trigger\": \"minecraft:inventory_changed\",\n" +
			"\t\t\t\"conditions\": {\n" +
			"\t\t\t\t\"items\": [\n" +
			"\t\t\t\t\t{\n" +
			"\t\t\t\t\t\t\"item\": \"IN\"\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t]\n" +
			"\t\t\t}\n" +
			"\t\t},\n" +
			"\t\t\"has_special\": {\n" +
			"\t\t\t\"trigger\": \"minecraft:inventory_changed\",\n" +
			"\t\t\t\"conditions\": {\n" +
			"\t\t\t\t\"items\": [\n" +
			"\t\t\t\t\t{\n" +
			"\t\t\t\t\t\t\"item\": \"EXTRA\"\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t]\n" +
			"\t\t\t}\n" +
			"\t\t}\n" +
			"\t}\n" +
			"}";
		
		String convertToAnalog = "{\n" +
			"\t\"parent\": \"dazzle:recipes/root\",\n" +
			"\t\"rewards\": {\n" +
			"\t\t\"recipes\": [\n" +
			"\t\t\t\"dazzle:lamps/OUT\"\n" +
			"\t\t]\n" +
			"\t},\n" +
			"\t\"criteria\": {\n" +
			"\t\t\"has_lamp\": {\n" +
			"\t\t\t\"trigger\": \"minecraft:inventory_changed\",\n" +
			"\t\t\t\"conditions\": {\n" +
			"\t\t\t\t\"items\": [\n" +
			"\t\t\t\t\t{\n" +
			"\t\t\t\t\t\t\"item\": \"IN\"\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t]\n" +
			"\t\t\t}\n" +
			"\t\t},\n" +
			"\t\t\"has_comparator\": {\n" +
			"\t\t\t\"trigger\": \"minecraft:inventory_changed\",\n" +
			"\t\t\t\"conditions\": {\n" +
			"\t\t\t\t\"items\": [\n" +
			"\t\t\t\t\t{\n" +
			"\t\t\t\t\t\t\"item\": \"minecraft:comparator\"\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t]\n" +
			"\t\t\t}\n" +
			"\t\t}\n" +
			"\t}\n" +
			"}";
		
		if(style.mode == LampStyle.Mode.ANALOG) {
			return convertToAnalog
				.replace("IN", style.withMode(LampStyle.Mode.DIGITAL).toIdentifier().toString())
				.replace("OUT", style.toIdentifier().getPath());
		} else {
			if(style.theme == LampStyle.Theme.CLASSIC) {
				return noExtraItem
					.replace("IN", style.color.findItemId().get().toString())
					.replace("OUT", style.toIdentifier().getPath());
			} else {
				return hasExtraItem
					.replace("EXTRA", getRequiredItem(style))
					.replace("IN", style.color.findItemId().get().toString())
					.replace("OUT", style.toIdentifier().getPath());
			}
		}
	}
	
	private static String flareBlockstate() {
		return "{\n" +
			"\t\"variants\": {\n" +
			"\t\t\"\": {\n" +
			"\t\t\t\"model\": \"dazzle:block/flare\"\n" +
			"\t\t}\n" +
			"\t}\n" +
			"}";
	}
	
	private static String flareItemModel() {
		return "{\n" +
			"\t\"parent\": \"item/generated\",\n" +
			"\t\"textures\": {\n" +
			"\t\t\"layer0\": \"dazzle:item/flare/base\",\n" +
			"\t\t\"layer1\": \"dazzle:item/flare/color\"\n" +
			"\t}\n" +
			"}";
	}
	
	private static String flareRecipe(DyeColor color, String itemId) {
		String template = "{\n" +
			"\t\"type\": \"minecraft:crafting_shapeless\",\n" +
			"\t\"ingredients\": [\n" +
			"\t\t{\n" +
			"\t\t\t\"item\": \"minecraft:glowstone_dust\"\n" +
			"\t\t},\n" +
			"\t\t{\n" +
			"\t\t\t\"item\": \"minecraft:popped_chorus_fruit\"\n" +
			"\t\t},\n" +
			"\t\t{\n" +
			"\t\t\t\"item\": \"DYE\"\n" +
			"\t\t}\n" +
			"\t],\n" +
			"\t\"result\": {\n" +
			"\t\t\"item\": \"RESULT\"\n" +
			"\t}\n" +
			"}";
				
		return template.replace("DYE", Junk.itemIdForDye(color).get().toString())
			.replace("RESULT", itemId);
	}
	
	private static String flareRecipeAdvancement(DyeColor color) {
		String template = "{\n" +
			"\t\"parent\": \"dazzle:recipes/root\",\n" +
			"\t\"rewards\": {\n" +
			"\t\t\"recipes\": [\n" +
			"\t\t\t\"OUT\"\n" +
			"\t\t]\n" +
			"\t},\n" +
			"\t\"criteria\": {\n" +
			"\t\t\"has_glowstone\": {\n" +
			"\t\t\t\"trigger\": \"minecraft:inventory_changed\",\n" +
			"\t\t\t\"conditions\": {\n" +
			"\t\t\t\t\"items\": [\n" +
			"\t\t\t\t\t{\n" +
			"\t\t\t\t\t\t\"item\": \"minecraft:glowstone_dust\"\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t]\n" +
			"\t\t\t}\n" +
			"\t\t},\n" +
			"\t\t\"has_chorus\": {\n" +
			"\t\t\t\"trigger\": \"minecraft:inventory_changed\",\n" +
			"\t\t\t\"conditions\": {\n" +
			"\t\t\t\t\"items\": [\n" +
			"\t\t\t\t\t{\n" +
			"\t\t\t\t\t\t\"item\": \"minecraft:popped_chorus_fruit\"\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t]\n" +
			"\t\t\t}\n" +
			"\t\t},\n" +
			"\t\t\"has_dye\": {\n" +
			"\t\t\t\"trigger\": \"minecraft:inventory_changed\",\n" +
			"\t\t\t\"conditions\": {\n" +
			"\t\t\t\t\"items\": [\n" +
			"\t\t\t\t\t{\n" +
			"\t\t\t\t\t\t\"item\": \"DYE\"\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t]\n" +
			"\t\t\t}\n" +
			"\t\t}\n" +
			"\t}\n" +
			"}";
		
		return template.replace("DYE", Junk.itemIdForDye(color).get().toString()).replace("OUT", "dazzle:flare/" + color.getName() + "_flare");
	}
	
	private static String generateLang(boolean murica) {
		JsonObject lang = new JsonObject();
		
		if(murica) {
			for(LampStyle style : LampStyle.ALL) {
				lang.addProperty("block.dazzle." + style.toName(), style.englishLocalization(true));
			}
			
			for(DyeColor color : DyeColor.values()) {
				lang.addProperty("block.dazzle." + color.getName() + "_flare", Junk.prettyPrintDyeColor(color, true) + " Flare");
			}
		} else {
			for(LampStyle style : LampStyle.ALL) {
				String a = style.englishLocalization(true);
				String b = style.englishLocalization(false);
				if(!a.equals(b)) {
					lang.addProperty("block.dazzle." + style.toName(), b);
				}
			}
			
			for(DyeColor color : DyeColor.values()) {
				String a = Junk.prettyPrintDyeColor(color, true);
				String b = Junk.prettyPrintDyeColor(color, false);
				if(!a.equals(b)) {
					lang.addProperty("block.dazzle." + color.getName() + "_flare", b + " Flare");
				}
			}
		}
		
		return lang.toString();
	}
}

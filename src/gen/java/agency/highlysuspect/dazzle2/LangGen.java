package agency.highlysuspect.dazzle2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LangGen implements DataProvider {
	public LangGen(Path outRoot, Path en_usJson) {
		this.outRoot = outRoot;
		this.en_usJson = en_usJson;
	}
	
	private final Path outRoot;
	private final Path en_usJson;
	
	@Override
	public void run(DataCache cache) throws IOException {
		JsonObject en_us = new JsonObject();
		JsonObject otherEnglish = new JsonObject();
		
		for(LampStyle style : LampStyle.ALL) {
			localizeStyle(style, en_us, otherEnglish);
		}
		
		for(DyeColor color : DyeColor.values()) {
			localizeFlare(color, en_us, otherEnglish);
		}
		
		mergeEnUsJson(en_us);
		
		DataProvider.writeToPath(GenInit.GSON, cache, otherEnglish, outRoot.resolve("assets/dazzle/lang/en_au.json"));
		DataProvider.writeToPath(GenInit.GSON, cache, otherEnglish, outRoot.resolve("assets/dazzle/lang/en_ca.json"));
		DataProvider.writeToPath(GenInit.GSON, cache, otherEnglish, outRoot.resolve("assets/dazzle/lang/en_gb.json"));
		DataProvider.writeToPath(GenInit.GSON, cache, otherEnglish, outRoot.resolve("assets/dazzle/lang/en_nz.json"));
	}
	
	private String spellColorEnUs(DyeColor color) {
		//works for all dye colors
		return WordUtils.capitalizeFully(color.getName().replace('_', ' '));
	}
	
	private boolean americaSpecificColorSpelling(DyeColor color) {
		return color == DyeColor.GRAY || color == DyeColor.LIGHT_GRAY;
	}
	
	private String convert(String whatever) {
		return whatever.replaceAll("Gray", "Grey");
	}
	
	private void localizeStyle(LampStyle style, JsonObject en_us, JsonObject otherEnglish) {
		String key = "block.dazzle." + style.toName();
		
		String colorNameEnUs = spellColorEnUs(style.color.color);
		String themeName = WordUtils.capitalizeFully(style.theme.getName());
		String modeName = WordUtils.capitalizeFully(style.mode.getName());
		
		String valueEnUs = colorNameEnUs + " " + themeName + " " + modeName + " Lamp";
		
		en_us.addProperty(key, valueEnUs);
		
		if(americaSpecificColorSpelling(style.color.color)) {
			otherEnglish.addProperty(key, convert(valueEnUs));
		}
	}
	
	private void localizeFlare(DyeColor color, JsonObject en_us, JsonObject otherEnglish) {
		String key = "block.dazzle." + color.getName() + "_flare";
		
		String colorNameEnUs = spellColorEnUs(color);
		String valueEnUs = colorNameEnUs + " Flare";
		
		en_us.addProperty(key, valueEnUs);
		if(americaSpecificColorSpelling(color)) {
			otherEnglish.addProperty(key, convert(valueEnUs));
		}
	}
	
	private void mergeEnUsJson(JsonObject newStuff) throws IOException {
		//read original en_us.json file
		List<String> originalFile = Files.readAllLines(en_usJson);
		
		//hack: preserve blank lines by writing keys like
		//   "dummyBlank0": 0,
		AtomicInteger funny = new AtomicInteger(0);
		AtomicBoolean needsDummyLine = new AtomicBoolean(true);
		List<String> dummyLineFile = originalFile.stream().map(s -> {
			if(needsDummyLine.get() && s.trim().isEmpty()) {
				return "\t\"dummyBlank" + funny.incrementAndGet() + "\": " + funny.get() + ",";
			} else if(s.trim().equals("}")) {
				needsDummyLine.set(false);
			}
			return s;
		}).collect(Collectors.toList());
		
		JsonObject originalJson = GenInit.GSON.fromJson(String.join("\n", dummyLineFile), JsonObject.class);
		
		//merge in the new properties
		for(Map.Entry<String, JsonElement> newEntry : newStuff.entrySet()) {
			if(originalJson.has(newEntry.getKey())) {
				originalJson.remove(newEntry.getKey());
			}
			
			originalJson.add(newEntry.getKey(), newEntry.getValue());
		}
		
		//back to string
		String prettyPrint = GenInit.GSON_PRETTY.toJson(originalJson);
		
		//undo hack: remove blank-line markers
		//(also use tab indents, you can't configure gson's pretty printer for some reason)
		List<String> prettyPrintStripped = Arrays.stream(prettyPrint.split("\n")).map(s -> {
			if(s.contains("dummyBlank")) {
				return "\t";
			} else if(s.startsWith("  ")) { //two spaces
				return s.replaceFirst(" {2}", "\t");
			} else return s;
		}).collect(Collectors.toList());
		
		//and overwrite the existing en_us file.
		Files.write(en_usJson, prettyPrintStripped, StandardCharsets.UTF_8);
	}
	
	@Override
	public String getName() {
		return "dazzle 2 lang!!";
	}
}

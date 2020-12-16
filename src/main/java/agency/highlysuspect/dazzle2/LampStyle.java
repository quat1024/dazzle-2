package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.block.LampBlock;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LampStyle {
	private LampStyle(ColorProp color, ThemeProp theme, ModeProp mode) {
		this.color = color;
		this.theme = theme;
		this.mode = mode;
	}
	
	private LampStyle(List<Prop> list) {
		//Mfw no heterogenous tuples
		this((ColorProp) list.get(0), (ThemeProp) list.get(1), (ModeProp) list.get(2));
	}
	
	public static LampStyle fromName(String name) {
		return LOOKUP.get(name);
	}
	
	public static LampStyle fromIdentifier(Identifier id) {
		return LOOKUP.get(id.getPath());
	}
	
	public final ColorProp color;
	public final ThemeProp theme;
	public final ModeProp mode;
	
	public String toName() {
		return color.color.getName() + '_' + theme.name + '_' + mode.name + "_lamp";
	}
	
	public Identifier toIdentifier() {
		return Init.id(toName());
	}
	
	public LampBlock instantiateBlock(Block.Settings settings) {
		return mode.constructor.apply(this, settings);
	}
	
	public static List<LampStyle> ALL = Lists.cartesianProduct(ColorProp.ALL, ThemeProp.ALL, ModeProp.ALL).stream().map(LampStyle::new).collect(Collectors.toList());
	public static Map<String, LampStyle> LOOKUP = ALL.stream().collect(Collectors.toMap(LampStyle::toName, Function.identity()));
	
	public interface Prop {}
	
	public static class ColorProp implements Prop {
		public ColorProp(DyeColor color) {
			this.color = color;
		}
		
		public final DyeColor color;
		
		public static final List<ColorProp> ALL = Arrays.stream(DyeColor.values()).map(ColorProp::new).collect(Collectors.toList());
	}
	
	public static class ThemeProp implements Prop {
		public ThemeProp(String name, boolean isTransparent) {
			this.name = name;
			this.isTransparent = isTransparent;
		}
		
		public final String name;
		public final boolean isTransparent; //kind of a hack rn
		
		public static final ThemeProp CLASSIC = new ThemeProp("classic", false);
		public static final ThemeProp MODERN = new ThemeProp("modern", false);
		public static final ThemeProp LANTERN = new ThemeProp("lantern", false);
		public static final ThemeProp PULSATING = new ThemeProp("pulsating", false);
		public static final ThemeProp ICY = new ThemeProp("icy", true);
		
		public static final List<ThemeProp> ALL = ImmutableList.of(CLASSIC, MODERN, LANTERN, PULSATING, ICY);
	}
	
	public static class ModeProp implements Prop {
		public ModeProp(String name, BiFunction<LampStyle, Block.Settings, LampBlock> constructor) {
			this.name = name;
			this.constructor = constructor;
		}
		
		public final String name;
		public final BiFunction<LampStyle, Block.Settings, LampBlock> constructor;
		
		public static final ModeProp DIGITAL = new ModeProp("digital", LampBlock.Digital::new);
		public static final ModeProp ANALOG = new ModeProp("analog", LampBlock.Analog::new);
		
		public static final List<ModeProp> ALL = ImmutableList.of(DIGITAL, ANALOG);
	}
}

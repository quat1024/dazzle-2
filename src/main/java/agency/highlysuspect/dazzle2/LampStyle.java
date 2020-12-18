package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.block.LampBlock;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LampStyle {
	private LampStyle(Color color, Theme theme, Mode mode) {
		this.color = color;
		this.theme = theme;
		this.mode = mode;
	}
	
	private LampStyle(List<Prop> list) {
		//Mfw no heterogenous tuples
		this((Color) list.get(0), (Theme) list.get(1), (Mode) list.get(2));
	}
	
	public static LampStyle fromName(String name) {
		return LOOKUP.get(name);
	}
	
	public static LampStyle fromIdentifier(Identifier id) {
		return LOOKUP.get(id.getPath());
	}
	
	public final Color color;
	public final Theme theme;
	public final Mode mode;
	
	public String toName() {
		return color.color.getName() + '_' + theme.name + '_' + mode.name + "_lamp";
	}
	
	public Identifier toIdentifier() {
		return Init.id(toName());
	}
	
	public String toEnUsLocalization() {
		return color.toEnUsLocalization() + " " + theme.toEnUsLocalization() + " " + mode.toEnUsLocalization() + " Lamp";
	}
	
	public LampBlock instantiateBlock(Block.Settings settings) {
		return mode.constructor.apply(this, theme.processSettings(settings));
	}
	
	public static List<LampStyle> ALL = Lists.cartesianProduct(Color.ALL, Theme.ALL, Mode.ALL).stream().map(LampStyle::new).collect(Collectors.toList());
	public static Map<String, LampStyle> LOOKUP = ALL.stream().collect(Collectors.toMap(LampStyle::toName, Function.identity()));
	
	public interface Prop {}
	
	public static class Color implements Prop {
		public Color(DyeColor color) {
			this.color = color;
		}
		
		public final DyeColor color;
		
		public static final List<Color> ALL = Arrays.stream(DyeColor.values()).map(Color::new).collect(Collectors.toList());
		
		public String toEnUsLocalization() {
			//This is horrible btw
			return WordUtils.capitalizeFully(color.getName().replace('_', ' '));
		}
	}
	
	public static class Theme implements Prop {
		public Theme(String name, boolean isTransparent) {
			this.name = name;
			this.isTransparent = isTransparent;
		}
		
		public AbstractBlock.Settings processSettings(AbstractBlock.Settings in) {
			if(isTransparent) {
				return in.nonOpaque();
			} else return in;
		}
		
		public final String name;
		public final boolean isTransparent; //kind of a hack rn
		
		public static final Theme CLASSIC = new Theme("classic", false);
		public static final Theme MODERN = new Theme("modern", false);
		public static final Theme LANTERN = new Theme("lantern", false);
		public static final Theme PULSATING = new Theme("pulsating", false);
		public static final Theme ICY = new Theme("icy", true);
		
		public static final List<Theme> ALL = ImmutableList.of(CLASSIC, MODERN, LANTERN, PULSATING, ICY);
		
		public String toEnUsLocalization() {
			return WordUtils.capitalizeFully(name);
		}
	}
	
	public static class Mode implements Prop {
		public Mode(String name, BiFunction<LampStyle, Block.Settings, LampBlock> constructor) {
			this.name = name;
			this.constructor = constructor;
		}
		
		public final String name;
		public final BiFunction<LampStyle, Block.Settings, LampBlock> constructor;
		
		public static final Mode DIGITAL = new Mode("digital", LampBlock.Digital::new);
		public static final Mode ANALOG = new Mode("analog", LampBlock.Analog::new);
		
		public static final List<Mode> ALL = ImmutableList.of(DIGITAL, ANALOG);
		
		public String toEnUsLocalization() {
			return WordUtils.capitalizeFully(name);
		}
	}
}

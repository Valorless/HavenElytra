package valorless.havenelytra;

import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.ValorlessUtils.Utils;

public class Lang {
	
	public static Config messages;
		
	public static class Placeholders{
		public static String plugin = "§7[§aHaven§bElytra§7]§r";
	}
	
	public static String Parse(String text) {
		if(!Utils.IsStringNullOrEmpty(text)) {
			text = text.replace("&", "§");
			text = text.replace("\\n", "\n");
			if(text.contains("%plugin%")) { text = text.replace("%plugin%", Placeholders.plugin); }
		}
		return text;
	}

	public static String Get(String key) {
		return Parse(messages.GetString(key));
	}
}

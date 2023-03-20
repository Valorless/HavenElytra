package valorless.sakuraelytra;

import org.bukkit.plugin.java.JavaPlugin;
import valorless.valorlessutils.ValorlessUtils.Config;
import valorless.valorlessutils.ValorlessUtils.Utils;

public class Lang {
	
	public static class Placeholders{
		public static String plugin = "§7[§dSakura§bElytra§7]§r";
	}
	
	public static String Parse(String text) {
		if(!Utils.IsStringNullOrEmpty(text)) {
			text = text.replace("&", "§");
			text = text.replace("\\n", "\n");
			if(text.contains("%plugin%")) { text = text.replace("%plugin%", Placeholders.plugin); }
		}
		return text;
	}

	public static String Get(JavaPlugin caller, String key) {
		return Parse(Config.GetString(caller, key));
	}
}

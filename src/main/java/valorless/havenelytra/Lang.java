package valorless.havenelytra;

import valorless.valorlessutils.config.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.ValorlessUtils.Utils;

public class Lang {
	
	public enum Language { english, danish, german, spanish, turkish, french }
	
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
		return hex(text);
	}
	
	public static String Get(String key) {
		if(messages.Get(key) == null) {
			Log.Error(HavenElytra.plugin, String.format("Messages.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(messages.GetString(key));
	}
	
	public static String Get(String key, Object arg) {
		if(messages.Get(key) == null) {
			Log.Error(HavenElytra.plugin, String.format("Messages.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(String.format(messages.GetString(key), arg.toString()));
	}
	
	public static String Get(String key, Object arg1, Object arg2) {
		if(messages.Get(key) == null) {
			Log.Error(HavenElytra.plugin, String.format("Messages.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(String.format(messages.GetString(key), arg1.toString(), arg2.toString()));
	}
	
	public static String Translate(String text) {
		if(HavenElytra.config.GetString("language").equalsIgnoreCase("danish")) {
			text = text.replace("Leather Chestplate", "Lædertunika");
			text = text.replace("Iron Chestplate", "Jernbrystplade");
			text = text.replace("Golden Chestplate", "Guldbrystplade");
			text = text.replace("Chainmail Chestplate", "Brynjebrystplade");
			text = text.replace("Diamond Chestplate", "Diamantbrystplade");
			text = text.replace("Netherite Chestplate", "Netherritbrystplade");
		} else if(HavenElytra.config.GetString("language").equalsIgnoreCase("german")) {
			text = text.replace("Leather Chestplater", "Lederjacke");
			text = text.replace("Iron Chestplate", "Eisenharnisch");
			text = text.replace("Golden Chestplate", "Goldharnisch");
			text = text.replace("Chainmail Chestplate", "Kettenhemd");
			text = text.replace("Diamond Chestplate", "Diamantharnisch");
			text = text.replace("Netherite Chestplate", "Netheritharnisch");
		} else if(HavenElytra.config.GetString("language").equalsIgnoreCase("spanish")) {
			text = text.replace("Leather Chestplater", "Túnica de cuero");
			text = text.replace("Iron Chestplate", "Peto de hierro");
			text = text.replace("Golden Chestplate", "Peto de oro");
			text = text.replace("Chainmail Chestplate", "Peto de cota de mallas");
			text = text.replace("Diamond Chestplate", "Peto de diamante");
			text = text.replace("Netherite Chestplate", "Peto de netherita");
		} else if(HavenElytra.config.GetString("language").equalsIgnoreCase("turkish")) {
			text = text.replace("Leather Chestplater", "Deri Ceket");
			text = text.replace("Iron Chestplate", "Demir Göğüslük");
			text = text.replace("Golden Chestplate", "Altın Göğüslük");
			text = text.replace("Chainmail Chestplate", "Zincir Göğüslük");
			text = text.replace("Diamond Chestplate", "Elmas Göğüslük");
			text = text.replace("Netherite Chestplate", "Netherit Göğüslük");
		} else if(HavenElytra.config.GetString("language").equalsIgnoreCase("french")) {
			text = text.replace("Leather Chestplater", "Tunique en cuir");
			text = text.replace("Iron Chestplate", "Plastron en fer");
			text = text.replace("Golden Chestplate", "Plastron en or");
			text = text.replace("Chainmail Chestplate", "Cotte de mailles");
			text = text.replace("Diamond Chestplate", "Plastron en diamant");
			text = text.replace("Netherite Chestplate", "Plastron en Netherite");
		}
		return text;
	}
	
	public static String hex(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');
           
            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&" + c);
            }
           
            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}

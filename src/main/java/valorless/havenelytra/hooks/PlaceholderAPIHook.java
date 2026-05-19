package valorless.havenelytra.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.havenelytra.Main;
import valorless.valorlessutils.logging.Log;

public class PlaceholderAPIHook {
	
	public static void Hook() {
		JavaPlugin plugin = Main.plugin;
		
		Log.debug(plugin, "Attempting to hook PlaceholderAPI.");
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
    		Log.info(plugin, "PlaceholderAPI integrated!");
		}else {
			Log.debug(plugin, "PlaceholderAPI not detected.");
		}
	}
	
	public static boolean isHooked() {
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
    		return true;
		}else {
			return false;
		}
	}

}

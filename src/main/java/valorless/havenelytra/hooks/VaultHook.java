package valorless.havenelytra.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import valorless.havenelytra.Main;
import valorless.valorlessutils.ValorlessUtils.Log;

public class VaultHook {
	private static Economy economy;
	
	public static void Hook() {
		JavaPlugin plugin = Main.plugin;
		
		Log.Debug(plugin, "Attempting to hook Vault.");
		if (setupEconomy()) {
    		Log.Info(plugin, "Vault integrated!");
		}else {
			Log.Debug(plugin, "Vault not detected.");
		}
	}
	
	
	private static boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
	
	public static Economy getEconomy() {
        return economy;
    }
	
}

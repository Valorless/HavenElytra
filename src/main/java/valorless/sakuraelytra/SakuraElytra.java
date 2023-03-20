package valorless.sakuraelytra;

import valorless.valorlessutils.ValorlessUtils.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SakuraElytra extends JavaPlugin implements Listener {
	public static JavaPlugin plugin;
	//public static ItemMerge merger;
	String Name = "§7[§dSakura§bElytra§7]§r";
    
    public String[] commands = {
    		"sakuraelytra", "se"
    };
	
	public void onLoad() {
		plugin = this;
		CommandListener.plugin = this;
		ItemMerge.plugin = this;
		ItemSplit.plugin = this;
		ItemGUI.plugin = this;
	}
	
	@Override
    public void onEnable() {		
		Config.Load(plugin);
		
		//Config
		Config.AddValidationEntry(plugin, "combine", true);
		Config.AddValidationEntry(plugin, "separate", false);
		
		//Main GUI
		Config.AddValidationEntry(plugin, "main-sound", "ENTITY_PLAYER_LEVELUP");
		Config.AddValidationEntry(plugin, "main-gui-name", "&d&lSakyra Elytra");
		Config.AddValidationEntry(plugin, "main-gui-size", 45);
		Config.AddValidationEntry(plugin, "main-gui", "");

		//Combine
		Config.AddValidationEntry(plugin, "combine-success", "%plugin% &dCombination success!");
		Config.AddValidationEntry(plugin, "combine-fail", "%plugin% &dCombination failed!\nElytra missing, or is already combined.");
		Config.AddValidationEntry(plugin, "combine-sound", "ENTITY_PLAYER_LEVELUP");
		Config.AddValidationEntry(plugin, "combine-gui-name", "&d&lSakyra Elytra");
		Config.AddValidationEntry(plugin, "combine-gui-size", 45);
		Config.AddValidationEntry(plugin, "combine-gui", "");

		//Separate
		Config.AddValidationEntry(plugin, "separate-success", "%plugin% &dSeparation success!");
		Config.AddValidationEntry(plugin, "separate-fail", "%plugin% &dCombination failed!\nElytra missing, or is not combined.");
		Config.AddValidationEntry(plugin, "separate-sound", "ENTITY_PLAYER_LEVELUP");
		Config.AddValidationEntry(plugin, "separate-gui-name", "&d&lSakyra Elytra");
		Config.AddValidationEntry(plugin, "separate-gui-size", 45);
		Config.AddValidationEntry(plugin, "separate-gui", "");

		CommandListener.onEnable();
		
		getServer().getPluginManager().registerEvents(new CommandListener(), this);
		
		RegisterCommands();
    }
    
    @Override
    public void onDisable() {
    }
    
    public void RegisterCommands() {
    	for (int i = 0; i < commands.length; i++) {
    		getCommand(commands[i]).setExecutor(this);
    	}
    }
}

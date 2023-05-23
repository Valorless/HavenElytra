package valorless.havenelytra;

import valorless.valorlessutils.config.Config;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class HavenElytra extends JavaPlugin implements Listener {
	public static JavaPlugin plugin;
	//public static ItemMerge merger;
	String Name = "§7[§aHaven§bElytra§7]§r";
	public static Config config;
    
    public String[] commands = {
    		"havenelytra", "he"
    };
	
	public void onLoad() {
		plugin = this;
		config = new Config(this, "config.yml");
		
		Lang.messages = new Config(this, "messages.yml");
		
		CommandListener.plugin = this;
		
		GUI.plugin = this;
	}
	
	@Override
    public void onEnable() {		
		//Config
		config.AddValidationEntry("combine", true);
		config.AddValidationEntry("separate", true);
		
		//Main GUI
		Config main = new Config(this, "gui-main.yml");
		main.AddValidationEntry("sound", "ENTITY_PLAYER_LEVELUP");
		main.AddValidationEntry("gui-name", "&a&lHaven Elytra");
		main.AddValidationEntry("gui-size", 27);
		main.AddValidationEntry("gui", "");
		main.AddValidationEntry("filler", "default");

		//Combine
		Config combine = new Config(this, "gui-combine.yml");
		combine.AddValidationEntry("sound", "BLOCK_ANVIL_USE");
		combine.AddValidationEntry("gui-name", "&aCombination");
		combine.AddValidationEntry("gui", "");
		combine.AddValidationEntry("filler", "default");

		//Separate
		Config separate = new Config(this, "gui-separate.yml");
		separate.AddValidationEntry("sound", "BLOCK_ANVIL_USE");
		separate.AddValidationEntry("gui-name", "&cSeparation");
		separate.AddValidationEntry("gui", "");
		separate.AddValidationEntry("filler", "default");
		
		//Lang
		Lang.messages.AddValidationEntry("combine-success", "%plugin% &aCombination success!");
		Lang.messages.AddValidationEntry("combine-fail", "%plugin% &cCombination failed!\nElytra missing, or is already combined.");
		Lang.messages.AddValidationEntry("combine-disabled", "%plugin% &cCombination is disabled.");
		Lang.messages.AddValidationEntry("separate-success", "%plugin% &aSeparation success!");
		Lang.messages.AddValidationEntry("separate-fail", "%plugin% &cCombination failed!\nElytra missing, or is not combined.");
		Lang.messages.AddValidationEntry("separate-disabled", "%plugin% &cSeparation is disabled.");

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

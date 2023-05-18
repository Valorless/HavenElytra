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
		
		ItemMerge.plugin = this;
		ItemMerge.config = new Config(this, "gui-combine.yml");
		
		ItemSplit.plugin = this;
		ItemSplit.config = new Config(this, "gui-separate.yml");
		
		ItemGUI.plugin = this;
		ItemGUI.config = new Config(this, "gui-main.yml");
	}
	
	@Override
    public void onEnable() {		
		//Config
		config.AddValidationEntry("combine", true);
		config.AddValidationEntry("separate", false);
		
		//Main GUI
		ItemGUI.config.AddValidationEntry("sound", "ENTITY_PLAYER_LEVELUP");
		ItemGUI.config.AddValidationEntry("gui-name", "&a&lHaven Elytra");
		ItemGUI.config.AddValidationEntry("gui-size", 27);
		ItemGUI.config.AddValidationEntry("gui", "");
		ItemGUI.config.AddValidationEntry("filler", "default");

		//Combine
		ItemMerge.config.AddValidationEntry("sound", "BLOCK_ANVIL_USE");
		ItemMerge.config.AddValidationEntry("gui-name", "&aCombination");
		ItemMerge.config.AddValidationEntry("gui", "");
		ItemGUI.config.AddValidationEntry("filler", "default");

		//Separate
		ItemSplit.config.AddValidationEntry("sound", "BLOCK_ANVIL_USE");
		ItemSplit.config.AddValidationEntry("gui-name", "&cSeparation");
		ItemSplit.config.AddValidationEntry("gui", "");
		ItemGUI.config.AddValidationEntry("filler", "default");
		
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

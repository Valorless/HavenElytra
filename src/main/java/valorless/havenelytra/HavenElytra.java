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
		ItemGUI.config.AddValidationEntry("gui-size", 45);
		ItemGUI.config.AddValidationEntry("gui", "");

		//Combine
		ItemMerge.config.AddValidationEntry("sound", "ENTITY_PLAYER_LEVELUP");
		ItemMerge.config.AddValidationEntry("gui-name", "&a&lHaven Elytra");
		ItemMerge.config.AddValidationEntry("gui-size", 45);
		ItemMerge.config.AddValidationEntry("gui", "");

		//Separate
		ItemSplit.config.AddValidationEntry("sound", "ENTITY_PLAYER_LEVELUP");
		ItemSplit.config.AddValidationEntry("gui-name", "&a&lHaven Elytra");
		ItemSplit.config.AddValidationEntry("gui-size", 45);
		ItemSplit.config.AddValidationEntry("gui", "");
		
		//Lang
		Lang.messages.AddValidationEntry("combine-success", "%plugin% &aCombination success!");
		Lang.messages.AddValidationEntry("combine-fail", "%plugin% &cCombination failed!\nElytra missing, or is already combined.");
		Lang.messages.AddValidationEntry("separate-success", "%plugin% &aSeparation success!");
		Lang.messages.AddValidationEntry("separate-fail", "%plugin% &cCombination failed!\nElytra missing, or is not combined.");

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

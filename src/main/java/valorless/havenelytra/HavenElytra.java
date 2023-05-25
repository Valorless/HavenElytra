package valorless.havenelytra;

import valorless.valorlessutils.config.Config;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class HavenElytra extends JavaPlugin implements Listener {
	public static JavaPlugin plugin;
	//public static ItemMerge merger;
	String Name = "§7[§aHaven§bElytra§7]§r";
	public static Config config;
	public static Config main;
	public static Config combine;
	public static Config separate;
    
    public String[] commands = {
    		"havenelytra", "he"
    };
	
	public void onLoad() {
		plugin = this;
		config = new Config(this, "config.yml");
		main = new Config(this, "gui-main.yml");
		combine = new Config(this, "gui-combine.yml");
		separate = new Config(this, "gui-separate.yml");
		
		Lang.messages = new Config(this, "messages.yml");
		
		CommandListener.plugin = this;
		
		GUI.plugin = this;
	}
	
	@Override
    public void onEnable() {		
		//Config
		config.AddValidationEntry("language", "english");
		config.AddValidationEntry("combine", true);
		config.AddValidationEntry("separate", true);
		config.Validate();
		
		//Main GUI
		main.AddValidationEntry("sound", "ENTITY_PLAYER_LEVELUP");
		main.AddValidationEntry("gui-name", "&a&lHaven Elytra");
		main.AddValidationEntry("gui-size", 27);
		main.AddValidationEntry("gui", "");
		main.AddValidationEntry("filler", "BLACK_STAINED_GLASS_PANE");
		main.Validate();

		//Combine
		combine.AddValidationEntry("sound", "BLOCK_ANVIL_USE");
		combine.AddValidationEntry("gui-name", "&aCombination");
		combine.AddValidationEntry("gui-size", 27);
		combine.AddValidationEntry("gui", "");
		combine.AddValidationEntry("filler", "BLACK_STAINED_GLASS_PANE");
		combine.AddValidationEntry("leather", true);
		combine.AddValidationEntry("iron", true);
		combine.AddValidationEntry("gold", true);
		combine.AddValidationEntry("chainmail", true);
		combine.AddValidationEntry("diamond", true);
		combine.AddValidationEntry("netherite", true);
		combine.Validate();

		//Separate
		separate.AddValidationEntry("sound", "BLOCK_ANVIL_USE");
		separate.AddValidationEntry("gui-name", "&cSeparation");
		separate.AddValidationEntry("gui-size", 27);
		separate.AddValidationEntry("gui", "");
		separate.AddValidationEntry("filler", "BLACK_STAINED_GLASS_PANE");
		separate.Validate();
		
		//Lang
		Lang.messages.AddValidationEntry("no-permission", "%plugin% &cSorry, you do not have permission to do this.");
		Lang.messages.AddValidationEntry("combine-success", "%plugin% &aCombination success!");
		Lang.messages.AddValidationEntry("combine-fail", "%plugin% &cCombination failed!\nElytra missing, or is already combined.");
		Lang.messages.AddValidationEntry("combine-disabled", "%plugin% &cCombination is disabled.");
		Lang.messages.AddValidationEntry("combined-elytra-lore", "&7+ [%s&7]");
		Lang.messages.AddValidationEntry("separate-success", "%plugin% &aSeparation success!");
		Lang.messages.AddValidationEntry("separate-fail", "%plugin% &cCombination failed!\nElytra missing, or is not combined.");
		Lang.messages.AddValidationEntry("separate-disabled", "%plugin% &cSeparation is disabled.");
		Lang.messages.AddValidationEntry("material-disabled", "%plugin% &cThis type of chestplate cannot be used.");
		Lang.messages.Validate();

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

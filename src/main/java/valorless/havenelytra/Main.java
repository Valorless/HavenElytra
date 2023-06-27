package valorless.havenelytra;

import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.translate.Translator;

import java.io.File;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {
	public static JavaPlugin plugin;
	//public static ItemMerge merger;
	String Name = "§7[§aHaven§bElytra§7]§r";
	public static Config config;
	public static Config main;
	public static Config combine;
	public static Config separate;
	Boolean uptodate = true;
	String newupdate = null;
	public static Translator translator;
    
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
        // All you have to do is adding the following two lines in your onEnable method.
        // You can find the plugin ids of your plugins on the page https://bstats.org/what-is-my-plugin-id
        int pluginId = 18792; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        
		CreateTemplates();
		Log.Debug(plugin, "HavenElytra Debugging Enabled!");
		
		//Config
		config.AddValidationEntry("language", "en_us");
		config.AddValidationEntry("combine", true);
		config.AddValidationEntry("separate", true);
		Log.Debug(plugin, "Validating config.yml");
		config.Validate();
		
		//Main GUI
		main.AddValidationEntry("sound", "ENTITY_PLAYER_LEVELUP");
		main.AddValidationEntry("gui-name", "&a&lHaven Elytra");
		main.AddValidationEntry("gui-size", 27);
		main.AddValidationEntry("gui", "");
		main.AddValidationEntry("filler", "BLACK_STAINED_GLASS_PANE");
		Log.Debug(plugin, "Validating gui-main.yml");
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
		Log.Debug(plugin, "Validating gui-combine.yml");
		combine.Validate();

		//Separate
		separate.AddValidationEntry("sound", "BLOCK_ANVIL_USE");
		separate.AddValidationEntry("gui-name", "&cSeparation");
		separate.AddValidationEntry("gui-size", 27);
		separate.AddValidationEntry("gui", "");
		separate.AddValidationEntry("filler", "BLACK_STAINED_GLASS_PANE");
		Log.Debug(plugin, "Validating gui-separate.yml");
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
		Log.Debug(plugin, "Validating messages.yml");
		Lang.messages.Validate();

		CommandListener.onEnable();
		
		
		getServer().getPluginManager().registerEvents(new CommandListener(), this);
		
		RegisterCommands();
		
		translator = new Translator(config.GetString("language"));
		
		if(config.GetBool("check-updates") == true) {
			Log.Info(plugin, "Checking for updates..");
			new UpdateChecker(this, 109583).getVersion(version -> {

				newupdate = version;

				if (!getDescription().getVersion().equals(version)) {
					Log.Warning(plugin, String.format("An update has been found! (v%s, you are on v%s) \n", version, getDescription().getVersion()) + 
							"This could be bug fixes or additional features.\n" + 
							"Please update HavenBags at https://www.spigotmc.org/resources/109583/");
					
					uptodate = false;
				}
			});
		}
		
    }
    
    @Override
    public void onDisable() {
    }
    
    public void RegisterCommands() {
    	for (int i = 0; i < commands.length; i++) {
    		Log.Debug(plugin, "Registering Command: " + commands[i]);
    		getCommand(commands[i]).setExecutor(this);
    	}
    }
    
    public void CreateTemplates() {
		Log.Debug(plugin, "Checking if gui templates exist");
    	File templatesFolder = new File(plugin.getDataFolder() + "/templates");
		if(!templatesFolder.exists()) {
			Log.Warning(plugin, "No GUI templates found, creating new.");
			Log.Warning(plugin, "/plugins/HavenElytra/templates/");
        }
		
    	
    	File mainTemplate = new File(plugin.getDataFolder() + "/templates/gui-main.yml");
		if(!mainTemplate.exists()) {
			try {
				mainTemplate.getParentFile().mkdirs();
			}
			catch(Exception e){
			
			}
	        plugin.saveResource("templates/gui-main.yml", false);
        }
		
		File combineTemplate = new File(plugin.getDataFolder() + "/templates/gui-combine.yml");
		if(!combineTemplate.exists()) {
			try {
				combineTemplate.getParentFile().mkdirs();
			}
			catch(Exception e){
			
			}
	        plugin.saveResource("templates/gui-combine.yml", false);
        }
		
		File separateTemplate = new File(plugin.getDataFolder() + "/templates/gui-separate.yml");
		if(!separateTemplate.exists()) {
			try {
				separateTemplate.getParentFile().mkdirs();
			}
			catch(Exception e){
			
			}
	        plugin.saveResource("templates/gui-separate.yml", false);
        }
    }
}

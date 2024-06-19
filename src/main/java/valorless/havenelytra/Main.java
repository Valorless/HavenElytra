package valorless.havenelytra;

import valorless.havenelytra.hooks.PlaceholderAPIHook;
import valorless.havenelytra.hooks.VaultHook;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.translate.Translator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
	int newupdate = 9999999;
	String newVersion = null;
	public static Translator translator;
	public static List<GUI> openGUIs = new ArrayList<GUI>();
    
    public String[] commands = {
    		"havenelytra", "he"
    };
	
	public void onLoad() {
		plugin = this;
		config = new Config(this, "config.yml");
		main = new Config(this, "gui-main.yml");
		combine = new Config(this, "gui-combine.yml");
		separate = new Config(this, "gui-separate.yml");
		
		Lang.lang = new Config(this, "lang.yml");
		
		CommandListener.plugin = this;
		
		GUI.plugin = this;
	}
	
	@SuppressWarnings("unused")
	boolean ValorlessUtils() {
		Log.Debug(plugin, "Checking ValorlessUtils");
		
		int requiresBuild = 219;
		
		String ver = Bukkit.getPluginManager().getPlugin("ValorlessUtils").getDescription().getVersion();
		//Log.Debug(plugin, ver);
		String[] split = ver.split("[.]");
		int major = Integer.valueOf(split[0]);
		int minor = Integer.valueOf(split[1]);
		int hotfix = Integer.valueOf(split[2]);
		int build = Integer.valueOf(split[3]);
		
		if(build < requiresBuild) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
        		public void run() {
        			Log.Error(plugin, String.format("HavenBags requires ValorlessUtils build %s or newer, found %s. (%s)", requiresBuild, build, ver));
        			Log.Error(plugin, "https://www.spigotmc.org/resources/valorlessutils.109586/");
        			Bukkit.getPluginManager().disablePlugin(plugin);
        		}
    		}, 10);
			return false;
		}
		else return true;
	}
	
	@Override
    public void onEnable() {
		Log.Debug(plugin, "HavenElytra Debugging Enabled!");
		
		// Check if a correct version of ValorlessUtils is in use, otherwise don't run the rest of the code.
		if(!ValorlessUtils()) return;

		VaultHook.Hook();
		PlaceholderAPIHook.Hook();
				
		CreateTemplates();
		
		//Config
		config.AddValidationEntry("debug", false);
		config.AddValidationEntry("check-updates", true);
		config.AddValidationEntry("language", "en_us");
		config.AddValidationEntry("combine", true);
		config.AddValidationEntry("combine-cost", 0);
		config.AddValidationEntry("separate", true);
		config.AddValidationEntry("separate-cost", 0);
		config.AddValidationEntry("custommodeldata.enabled", false);
		config.AddValidationEntry("custommodeldata.combined", 12070);
		config.AddValidationEntry("custommodeldata.per-material.enabled", false);
		config.AddValidationEntry("custommodeldata.per-material.leather", 12071);
		config.AddValidationEntry("custommodeldata.per-material.iron", 12072);
		config.AddValidationEntry("custommodeldata.per-material.gold", 12073);
		config.AddValidationEntry("custommodeldata.per-material.chainmail", 12074);
		config.AddValidationEntry("custommodeldata.per-material.diamond", 12075);
		config.AddValidationEntry("custommodeldata.per-material.netherite", 12076);
		config.AddValidationEntry("item-damage.enabled", true);
		config.AddValidationEntry("item-damage.chestplate-damage", true);
		config.AddValidationEntry("item-damage.elytra-damage", false);
		Log.Debug(plugin, "Validating config.yml");
		config.Validate();
		
		//Main GUI
		main.AddValidationEntry("sound", "ENTITY_PLAYER_LEVELUP");
		main.AddValidationEntry("volume", 1.0);
		main.AddValidationEntry("pitch", 1.0);
		main.AddValidationEntry("gui-name", "&a&lHaven Elytra");
		main.AddValidationEntry("gui-size", 27);
		main.AddValidationEntry("gui", "");
		main.AddValidationEntry("filler", "BLACK_STAINED_GLASS_PANE");
		Log.Debug(plugin, "Validating gui-main.yml");
		main.Validate();

		//Combine
		combine.AddValidationEntry("sound", "BLOCK_ANVIL_USE");
		combine.AddValidationEntry("volume", 1.0);
		combine.AddValidationEntry("pitch", 1.0);
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
		separate.AddValidationEntry("volume", 1.0);
		separate.AddValidationEntry("pitch", 1.0);
		separate.AddValidationEntry("gui-name", "&cSeparation");
		separate.AddValidationEntry("gui-size", 27);
		separate.AddValidationEntry("gui", "");
		separate.AddValidationEntry("filler", "BLACK_STAINED_GLASS_PANE");
		Log.Debug(plugin, "Validating gui-separate.yml");
		separate.Validate();
		
		//Lang
		Lang.lang.AddValidationEntry("no-permission", "%plugin% &cSorry, you do not have permission to do this.");
		Lang.lang.AddValidationEntry("combine-success", "%plugin% &aCombination success!");
		Lang.lang.AddValidationEntry("combine-fail", "%plugin% &cCombination failed!\nElytra is already combined.");
		Lang.lang.AddValidationEntry("combine-disabled", "%plugin% &cCombination is disabled.");
		Lang.lang.AddValidationEntry("combined-elytra-lore", "&7+ [%s&7]");
		Lang.lang.AddValidationEntry("separate-success", "%plugin% &aSeparation success!");
		Lang.lang.AddValidationEntry("separate-fail", "%plugin% &cCombination failed!\nElytra is not combined.");
		Lang.lang.AddValidationEntry("separate-disabled", "%plugin% &cSeparation is disabled.");
		Lang.lang.AddValidationEntry("material-disabled", "%plugin% &cThis type of chestplate cannot be used.");
		Lang.lang.AddValidationEntry("not-repaired", "%plugin% &cOne or more items are damaged.");
		Lang.lang.AddValidationEntry("cannot-afford", "%plugin% &cYou do not have enough money.");
		Lang.lang.AddValidationEntry("can-afford", "%plugin% &a$%cost% has been taken from your balance.");
		Log.Debug(plugin, "Validating lang.yml");
		Lang.lang.Validate();

		CommandListener.onEnable();
				
		RegisterCommands();
		
		translator = new Translator(config.GetString("language"));
		
		if(config.GetBool("check-updates") == true) {
			Log.Info(plugin, "Checking for updates..");
			new UpdateChecker(this, 109583).getVersion(version -> {

				newVersion = version;
				String update = version.replace(".", "");
				newupdate = Integer.parseInt(update);
				String current = getDescription().getVersion().replace(".", "");;
				int v = Integer.parseInt(current);
				

				//if (!getDescription().getVersion().equals(version)) {
				if (v < newupdate) {
						Log.Warning(plugin, String.format("An update has been found! (v%s, you are on v%s) \n", version, getDescription().getVersion()) + 
							"This could be bug fixes or additional features.\n" + 
							"Please update HavenElytra at https://www.spigotmc.org/resources/109583/");
					
					uptodate = false;
				}else {
					Log.Info(plugin, "Up to date.");
				}
			});
		}
		
		// All you have to do is adding the following two lines in your onEnable method.
        // You can find the plugin ids of your plugins on the page https://bstats.org/what-is-my-plugin-id
        int pluginId = 18792; // <-- Replace with the id of your plugin!
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this, pluginId);
		
    }
    
    @Override
    public void onDisable() {
    	if(openGUIs.size() > 0) {
    		for(GUI gui : openGUIs) {
    			gui.Close();
    		}
    	}
    }
    
    public void RegisterCommands() {
    	for (int i = 0; i < commands.length; i++) {
    		Log.Debug(plugin, "Registering Command: " + commands[i]);
    		getCommand(commands[i]).setExecutor(new CommandListener());
    	}
    }


	@EventHandler
	public void UpdateNotification(PlayerJoinEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    public void run() {
		    	if (config.GetBool("check-updates") && e.getPlayer().isOp() && uptodate == false) {
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&7[&aHaven&bElytra&7] " + "&fAn update has been found.\nPlease download version&a " + newupdate
						+ ", &fyou are on version&a " + getDescription().getVersion() + "!"
						));
				}
		    }
		}, 5L);
		
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

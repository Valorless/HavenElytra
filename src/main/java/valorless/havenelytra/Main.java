package valorless.havenelytra;

import valorless.havenelytra.hooks.PlaceholderAPIHook;
import valorless.havenelytra.hooks.VaultHook;
import valorless.valorlessutils.logging.Log;
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
		Log.debug(plugin, "Checking ValorlessUtils");
		
		int requiresBuild = 219;
		
		String ver = Bukkit.getPluginManager().getPlugin("ValorlessUtils").getDescription().getVersion();
		//Log.Debug(plugin, ver);
		String[] split = ver.split("[.]");
		int major = Integer.parseInt(split[0]);
		int minor = Integer.parseInt(split[1]);
		int hotfix = Integer.parseInt(split[2]);
		int build = Integer.parseInt(split[3]);
		
		if(build < requiresBuild) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
        		public void run() {
        			Log.error(plugin, String.format("HavenBags requires ValorlessUtils build %s or newer, found %s. (%s)", requiresBuild, build, ver));
        			Log.error(plugin, "https://www.spigotmc.org/resources/valorlessutils.109586/");
        			Bukkit.getPluginManager().disablePlugin(plugin);
        		}
    		}, 10);
			return false;
		}
		else return true;
	}
	
	@Override
    public void onEnable() {
		Log.debug(plugin, "HavenElytra Debugging Enabled!");
		
		// Check if a correct version of ValorlessUtils is in use, otherwise don't run the rest of the code.
		if(!ValorlessUtils()) return;

		VaultHook.Hook();
		PlaceholderAPIHook.Hook();
				
		CreateTemplates();
		
		//Config
		config.addValidationEntry("debug", false);
		config.addValidationEntry("check-updates", true);
		config.addValidationEntry("language", "en_us");
		config.addValidationEntry("combine", true);
		config.addValidationEntry("combine-cost", 0);
		config.addValidationEntry("separate", true);
		config.addValidationEntry("separate-cost", 0);
		config.addValidationEntry("custommodeldata.enabled", false);
		config.addValidationEntry("custommodeldata.combined", 12070);
		config.addValidationEntry("custommodeldata.per-material.enabled", false);
		config.addValidationEntry("custommodeldata.per-material.leather", 12071);
		config.addValidationEntry("custommodeldata.per-material.iron", 12072);
		config.addValidationEntry("custommodeldata.per-material.gold", 12073);
		config.addValidationEntry("custommodeldata.per-material.chainmail", 12074);
		config.addValidationEntry("custommodeldata.per-material.diamond", 12075);
		config.addValidationEntry("custommodeldata.per-material.netherite", 12076);
		config.addValidationEntry("item-damage.enabled", true);
		config.addValidationEntry("item-damage.chestplate-damage", true);
		config.addValidationEntry("item-damage.elytra-damage", false);
		Log.debug(plugin, "Validating config.yml");
		config.validate();
		
		//Main GUI
		main.addValidationEntry("sound", "ENTITY_PLAYER_LEVELUP");
		main.addValidationEntry("volume", 1.0);
		main.addValidationEntry("pitch", 1.0);
		main.addValidationEntry("gui-name", "&a&lHaven Elytra");
		main.addValidationEntry("gui-size", 27);
		main.addValidationEntry("gui", "");
		main.addValidationEntry("filler", "BLACK_STAINED_GLASS_PANE");
		Log.debug(plugin, "Validating gui-main.yml");
		main.validate();

		//Combine
		combine.addValidationEntry("sound", "BLOCK_ANVIL_USE");
		combine.addValidationEntry("volume", 1.0);
		combine.addValidationEntry("pitch", 1.0);
		combine.addValidationEntry("gui-name", "&aCombination");
		combine.addValidationEntry("gui-size", 27);
		combine.addValidationEntry("gui", "");
		combine.addValidationEntry("filler", "BLACK_STAINED_GLASS_PANE");
		combine.addValidationEntry("leather", true);
		combine.addValidationEntry("iron", true);
		combine.addValidationEntry("gold", true);
		combine.addValidationEntry("chainmail", true);
		combine.addValidationEntry("diamond", true);
		combine.addValidationEntry("netherite", true);
		Log.debug(plugin, "Validating gui-combine.yml");
		combine.validate();

		//Separate
		separate.addValidationEntry("sound", "BLOCK_ANVIL_USE");
		separate.addValidationEntry("volume", 1.0);
		separate.addValidationEntry("pitch", 1.0);
		separate.addValidationEntry("gui-name", "&cSeparation");
		separate.addValidationEntry("gui-size", 27);
		separate.addValidationEntry("gui", "");
		separate.addValidationEntry("filler", "BLACK_STAINED_GLASS_PANE");
		Log.debug(plugin, "Validating gui-separate.yml");
		separate.validate();
		
		//Lang
		Lang.lang.addValidationEntry("no-permission", "%plugin% &cSorry, you do not have permission to do this.");
		Lang.lang.addValidationEntry("combine-success", "%plugin% &aCombination success!");
		Lang.lang.addValidationEntry("combine-fail", "%plugin% &cCombination failed!\nElytra is already combined.");
		Lang.lang.addValidationEntry("combine-disabled", "%plugin% &cCombination is disabled.");
		Lang.lang.addValidationEntry("combined-elytra-lore", "&7+ [%s&7]");
		Lang.lang.addValidationEntry("separate-success", "%plugin% &aSeparation success!");
		Lang.lang.addValidationEntry("separate-fail", "%plugin% &cCombination failed!\nElytra is not combined.");
		Lang.lang.addValidationEntry("separate-disabled", "%plugin% &cSeparation is disabled.");
		Lang.lang.addValidationEntry("material-disabled", "%plugin% &cThis type of chestplate cannot be used.");
		Lang.lang.addValidationEntry("not-repaired", "%plugin% &cOne or more items are damaged.");
		Lang.lang.addValidationEntry("cannot-afford", "%plugin% &cYou do not have enough money.");
		Lang.lang.addValidationEntry("can-afford", "%plugin% &a$%cost% has been taken from your balance.");
		Log.debug(plugin, "Validating lang.yml");
		Lang.lang.validate();

		CommandListener.onEnable();
				
		RegisterCommands();
		
		translator = new Translator(config.getString("language"));
		
		if(config.getBool("check-updates") == true) {
			Log.info(plugin, "Checking for updates..");
			new UpdateChecker(this, 109583).getVersion(version -> {

				newVersion = version;
				String update = version.replace(".", "");
				newupdate = Integer.parseInt(update);
				String current = getDescription().getVersion().replace(".", "");;
				int v = Integer.parseInt(current);
				

				//if (!getDescription().getVersion().equals(version)) {
				if (v < newupdate) {
						Log.warning(plugin, String.format("An update has been found! (v%s, you are on v%s) \n", version, getDescription().getVersion()) +
							"This could be bug fixes or additional features.\n" + 
							"Please update HavenElytra at https://www.spigotmc.org/resources/109583/");
					
					uptodate = false;
				}else {
					Log.info(plugin, "Up to date.");
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
    	if(!openGUIs.isEmpty()) {
    		for(GUI gui : openGUIs) {
    			gui.Close();
    		}
    	}
    }
    
    public void RegisterCommands() {
        for (String command : commands) {
            Log.debug(plugin, "Registering Command: " + command);
            getCommand(command).setExecutor(new CommandListener());
        }
    }


	@EventHandler
	public void UpdateNotification(PlayerJoinEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    public void run() {
		    	if (config.getBool("check-updates") && e.getPlayer().isOp() && uptodate == false) {
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&7[&aHaven&bElytra&7] " + "&fAn update has been found.\nPlease download version&a " + newupdate
						+ ", &fyou are on version&a " + getDescription().getVersion() + "!"
						));
				}
		    }
		}, 5L);
		
	}
    
    public void CreateTemplates() {
		Log.debug(plugin, "Checking if gui templates exist");
    	File templatesFolder = new File(plugin.getDataFolder() + "/templates");
		if(!templatesFolder.exists()) {
			Log.warning(plugin, "No GUI templates found, creating new.");
			Log.warning(plugin, "/plugins/HavenElytra/templates/");
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

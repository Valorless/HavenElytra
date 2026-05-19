package valorless.havenelytra;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.valorlessutils.logging.Log;
import valorless.valorlessutils.nbt.NBT;
import valorless.valorlessutils.tags.TagType;
import valorless.valorlessutils.tags.Tags;
import valorless.valorlessutils.translate.Translator;

public class CommandListener implements CommandExecutor {
	public static JavaPlugin plugin;
	String Name = "§7[§aHaven§bElytra§7]§r";
	
	public static void onEnable() {
	}
	
	/*@EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String[] args = event.getMessage().split("\\s+");
		CommandSender sender = event.getPlayer();
		ProcessCommand(args, sender, false);
	}
	
	@EventHandler
    public void onServerCommand(ServerCommandEvent event) {
		String[] args = event.getCommand().split("\\s+");
		CommandSender console = event.getSender();
		args[0] = "/" + args[0];
		ProcessCommand(args, console, true);
		
	}*/
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	Log.debug(Main.plugin, "Sender: " + sender.getName());
    	Log.debug(Main.plugin, "Command: " + command.toString());
    	Log.debug(Main.plugin, "Label: " + label);
    	for(String a : args) {
    		Log.debug(Main.plugin, "Argument: " + a);
    	}
			
		if(args.length == 0) {
			//ItemGUI gui = new ItemGUI();
			GUI gui = new GUI(Bukkit.getPlayer(sender.getName()));
			Bukkit.getServer().getPluginManager().registerEvents(gui, plugin);
			//gui.player = Bukkit.getPlayer(sender.getName());
	    	//SFX.Play(ItemGUI.config.GetString("sound"), 1f, 1f, Bukkit.getPlayer(sender.getName()));
			//gui.OpenInventory(Bukkit.getPlayer(sender.getName()));
			return true;
		}
		else if (args.length >= 1){
			if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("havenelytra.reload")) {
				Main.config.reload();
				Main.combine.reload();
				Main.separate.reload();
				Main.main.reload();
				Lang.lang.reload();
				Main.translator = new Translator(Main.config.getString("language"));
				sender.sendMessage(Name +" §aReloaded.");
				Log.info(plugin, Name + " §aReloaded!");
				return true;
			}
				
			if(args[0].equalsIgnoreCase("convert")) {
				Player player = (Player)sender;
				ItemStack item = player.getInventory().getItemInMainHand();
				if(item != null) {
					if(item.getType() == Material.ELYTRA && item.hasItemMeta()) {
						if(Tags.Has(plugin, item.getItemMeta().getPersistentDataContainer(), "combined", TagType.INTEGER)) {
							Log.debug(plugin, "Converting Elytra.");
							ConvertData(item, "combined");
							ConvertData(item, "chestplate-type");
							ConvertData(item, "chestplate-name");
							ConvertData(item, "elytra-meta");
							ConvertData(item, "chestplate-meta");
							return true;
						}
					}
				}	
				
			}
		}
		return false;
	}
	
	
	void ConvertData(ItemStack item, String key) {
		Log.debug(plugin, "Key: " + key);
		Log.debug(plugin, new NamespacedKey(plugin, key).toString());
		if(key.equalsIgnoreCase("combined")) {
			NBT.SetBool(item, "elytra-" + key, true);
		}else {
			NBT.SetString(item, "elytra-" + key, 
					(String)Tags.Get(plugin, item.getItemMeta().getPersistentDataContainer(), key, TagType.STRING)
					);
		}
		ItemMeta meta = item.getItemMeta();
		meta.getPersistentDataContainer().remove(new NamespacedKey(plugin, key));
		item.setItemMeta(meta);
	}

}

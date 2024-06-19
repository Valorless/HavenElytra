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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.valorlessutils.ValorlessUtils.*;
import valorless.valorlessutils.nbt.NBT;
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
    	Log.Debug(Main.plugin, "Sender: " + sender.getName());
    	Log.Debug(Main.plugin, "Command: " + command.toString());
    	Log.Debug(Main.plugin, "Label: " + label);
    	for(String a : args) {
    		Log.Debug(Main.plugin, "Argument: " + a);
    	}
			
			if(args.length == 0) {
				//sender.sendMessage(Name + " SakuraElytra by Valorless.");
				//ItemGUI gui = new ItemGUI();
				GUI gui = new GUI(Bukkit.getPlayer(sender.getName()));
				Bukkit.getServer().getPluginManager().registerEvents(gui, plugin);
				//gui.player = Bukkit.getPlayer(sender.getName());
	        	//SFX.Play(ItemGUI.config.GetString("sound"), 1f, 1f, Bukkit.getPlayer(sender.getName()));
				//gui.OpenInventory(Bukkit.getPlayer(sender.getName()));
				return true;
			}
			else 
			if (args.length >= 1){
				if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("havenelytra.reload")) {
					Main.config.Reload();
					Main.combine.Reload();
					Main.separate.Reload();
					Main.main.Reload();
					Lang.lang.Reload();
					Main.translator = new Translator(Main.config.GetString("language"));
					sender.sendMessage(Name +" §aReloaded.");
					Log.Info(plugin, Name + " §aReloaded!"); 
					return true;
				}
			}
				
			if(args[0].equalsIgnoreCase("convert")) {
				Player player = (Player)sender;
				ItemStack item = player.getInventory().getItemInMainHand();
				if(item != null) {
					if(item.getType() == Material.ELYTRA && item.hasItemMeta()) {
		        		if(Tags.Has(plugin, item.getItemMeta().getPersistentDataContainer(), "combined", PersistentDataType.INTEGER)) {
							Log.Debug(plugin, "Converting Elytra.");
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
		return false;
	}
	
	
	void ConvertData(ItemStack item, String key) {
		Log.Debug(plugin, "Key: " + key);
		Log.Debug(plugin, new NamespacedKey(plugin, key).toString());
		if(key.equalsIgnoreCase("combined")) {
			NBT.SetBool(item, "elytra-" + key, true);
		}else {
			NBT.SetString(item, "elytra-" + key, 
					(String)Tags.Get(plugin, item.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.STRING)
					);
		}
		ItemMeta meta = item.getItemMeta();
		meta.getPersistentDataContainer().remove(new NamespacedKey(plugin, key));
		item.setItemMeta(meta);
	}

}

package valorless.havenelytra;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.valorlessutils.ValorlessUtils.*;
import valorless.valorlessutils.config.Config;

public class CommandListener implements Listener {
	public static JavaPlugin plugin;
	String Name = "§7[§aHaven§bElytra§7]§r";
	
	public static void onEnable() {
	}
	
	@EventHandler
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
		
	}
	
	public void ProcessCommand(String[] args, CommandSender sender, Boolean console) {
		//for(int i = 0; i < args.length; i++) { Log.Severe(args[i]); }
		if(args[0].equalsIgnoreCase("/havenelytra") || args[0].equalsIgnoreCase("/he")) {
			
			if(args.length == 1) {
				//sender.sendMessage(Name + " SakuraElytra by Valorless.");
				//ItemGUI gui = new ItemGUI();
				GUI gui = new GUI(Bukkit.getPlayer(sender.getName()));
				Bukkit.getServer().getPluginManager().registerEvents(gui, plugin);
				//gui.player = Bukkit.getPlayer(sender.getName());
	        	//SFX.Play(ItemGUI.config.GetString("sound"), 1f, 1f, Bukkit.getPlayer(sender.getName()));
				//gui.OpenInventory(Bukkit.getPlayer(sender.getName()));
			}
			else 
			if (args.length >= 2){
				if(args[1].equalsIgnoreCase("reload") && sender.hasPermission("havenelytra.reload")) {
					HavenElytra.config.Reload();
					Config ItemMerge = new Config(plugin, "gui-combine.yml");
					ItemMerge.Reload();
					Config ItemSplit = new Config(plugin, "gui-separate.yml");
					ItemSplit.Reload();
					Config ItemGUI = new Config(plugin, "gui-main.yml");
					ItemGUI.Reload();
					Lang.messages.Reload();
					sender.sendMessage(Name +" §aReloaded.");
					if(!console) { Log.Info(plugin, Name + " §aReloaded!"); }
				}
			}
		}
	}

}

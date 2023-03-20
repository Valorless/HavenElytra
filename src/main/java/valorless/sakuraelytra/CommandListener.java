package valorless.sakuraelytra;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.valorlessutils.ValorlessUtils.*;

public class CommandListener implements Listener {
	public static JavaPlugin plugin;
	String Name = "§7[§dSakura§bElytra§7]§r";
	
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
		if(args[0].equalsIgnoreCase("/sakuraelytra") || args[0].equalsIgnoreCase("/se")) {
			
			if(args.length == 1) {
				//sender.sendMessage(Name + " SakuraElytra by Valorless.");
				ItemGUI gui = new ItemGUI();
				Bukkit.getServer().getPluginManager().registerEvents(gui, plugin);
				gui.player = Bukkit.getPlayer(sender.getName());
				gui.OpenInventory(Bukkit.getPlayer(sender.getName()));
			}
			else 
			if (args.length >= 2){
				if(args[1].equalsIgnoreCase("reload") && sender.hasPermission("sakuraelytra.reload")) {
					Config.Reload(plugin);
					sender.sendMessage(Name +" §aReloaded.");
					if(!console) { Log.Info(plugin, Name + " §aReloaded!"); }
				}
			}
		}
	}

}

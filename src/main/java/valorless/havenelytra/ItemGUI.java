package valorless.havenelytra;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.ValorlessUtils.Tags;
import valorless.valorlessutils.ValorlessUtils.Utils;

public class ItemGUI implements Listener {
	public static JavaPlugin plugin;
	private final Inventory inv;
	public Player player;
	private List<Items> items;
	public static Config config;
	public boolean filler;
	
	public class Items {
		public String name = "";
		public String item = "";
		public List<String> lore = new ArrayList<String>();
		public Boolean interactable = false;
		public int customModelData;
		public String tag;
	}

    public ItemGUI() {
    	InitializeLists();
    	
        inv = Bukkit.createInventory(player, config.GetInt("gui-size"), Lang.Parse(config.GetString("gui-name")));
        
        filler = config.GetBool("use-filler");
        InitializeItems();
    }
    
    public void InitializeLists() {
    	items = new ArrayList<Items>();
    	for(int i = 0; i < config.GetInt("gui-size"); i++) {
    		Items item = new Items();
    		item.name = config.GetString("gui." + i + ".name");
    		item.item = config.GetString("gui." + i + ".item");
    		item.lore = config.GetStringList("gui." + i + ".lore");
    		item.interactable = config.GetBool("gui." + i + ".interact");
    		if(config.GetInt("gui." + i + ".custom-model-data") != null) {
    			item.customModelData = config.GetInt("gui." + i + ".custom-model-data");
    		}
    		item.tag = config.GetString("gui." + i + ".tag");
        	items.add(item);
    	}
    }
    
	public void InitializeItems() {
    	for(int i = 0; i < items.size(); i++) {
    		if(!Utils.IsStringNullOrEmpty(items.get(i).item)) {
    			inv.setItem(i, CreateGuiItem(Material.getMaterial(items.get(i).item), items.get(i).name, items.get(i).interactable, items.get(i).tag, items.get(i).lore, items.get(i).customModelData));
    		} else if(filler == true) {
    			inv.setItem(i, CreateGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§f", false, "", null, 0));
    		}
    	}
    }

    protected ItemStack CreateGuiItem(final Material material, final String name, boolean interact, final String tag, final List<String> lore, int customModelData) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
        	meta.setDisplayName(name);

        	if(lore != null) {
        		meta.setLore(lore);
        	}
        	
        	Tags.Set(plugin, meta.getPersistentDataContainer(), "interact", interact ? 1 : 0, PersistentDataType.INTEGER);
        	Tags.Set(plugin, meta.getPersistentDataContainer(), "tag", tag, PersistentDataType.STRING);
        	
        	meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        	
        	if(customModelData != 0) {
        		meta.setCustomModelData(customModelData);
        	}
        	
        	item.setItemMeta(meta);
        }

        return item;
    }

    public void OpenInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
    	//Log.Info(String.valueOf(e.getRawSlot()));
        if (!e.getInventory().equals(inv)) return;

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem != null) {
        	if(clickedItem.hasItemMeta()) {
        		if(clickedItem.getItemMeta().getPersistentDataContainer().get(
            	new NamespacedKey(plugin,  "interact"), PersistentDataType.INTEGER) != null) {
        			if(clickedItem.getItemMeta().getPersistentDataContainer().get(
        			new NamespacedKey(plugin, "interact"), PersistentDataType.INTEGER) == 0) {
        				e.setCancelled(true);
        			}
        		}
        	}
        }

        if (clickedItem == null || clickedItem.getType().isAir()) return;
        Object tag = Tags.Get(plugin, clickedItem.getItemMeta().getPersistentDataContainer(), "tag", PersistentDataType.STRING);
        if(tag == null) return;
        if(!Utils.IsStringNullOrEmpty(tag.toString())) {
        	//Log.Info(plugin, tag);
        	if(tag.toString().equalsIgnoreCase("COMBINE") && HavenElytra.config.GetBool("combine")) { Combine(); }
        	if(tag.toString().equalsIgnoreCase("SEPARATE") && HavenElytra.config.GetBool("separate")) { Separate(); }
        }
        

        // Using slots click is a best option for your inventory click's
        //player.sendMessage("You clicked at slot " + e.getRawSlot());
    }
    
    public void Combine() {
    	//HandlerList.unregisterAll(this);
    	player.closeInventory();
		ItemMerge merger = new ItemMerge();
		Bukkit.getServer().getPluginManager().registerEvents(merger, plugin);
		merger.player = player;
		merger.OpenInventory(player);
    }
    
    public void Separate() {
    	//HandlerList.unregisterAll(this);
    	player.closeInventory();
    	ItemSplit splitter = new ItemSplit();
		Bukkit.getServer().getPluginManager().registerEvents(splitter, plugin);
    	splitter.player = player;
		splitter.OpenInventory(player);
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (!e.getInventory().equals(inv)) return;
    	//HandlerList.unregisterAll(this);
    }
}

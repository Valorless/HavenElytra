package valorless.sakuraelytra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.valorlessutils.ValorlessUtils.Config;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.ValorlessUtils.Tags;
import valorless.valorlessutils.ValorlessUtils.Utils;

public class ItemGUI implements Listener {
	public static JavaPlugin plugin;
	private final Inventory inv;
	public Player player;
	private List<Items> items;
	private List<String> slotTags;
	
	public class Items {
		public String name = "";
		public String item = "";
		public String lore1 = "";
		public String lore2 = "";
		public Boolean interactable = false;
		public String tag;
	}

    public ItemGUI() {
    	InitializeLists();
    	
        inv = Bukkit.createInventory(player, Config.GetInt(plugin, "main-gui-size"), Lang.Parse(Config.GetString(plugin, "main-gui-name")));

        InitializeItems();
    }
    
    public void InitializeLists() {
    	items = new ArrayList<Items>();
    	slotTags = new ArrayList<String>();
    	for(int i = 0; i < Config.GetInt(plugin, "main-gui-size"); i++) {
    		Items item = new Items();
    		item.name = Config.GetString(plugin, "main-gui." + i + ".name");
    		item.item = Config.GetString(plugin, "main-gui." + i + ".item");
    		item.lore1 = Config.GetString(plugin, "main-gui." + i + ".lore1");
    		item.lore2 = Config.GetString(plugin, "main-gui." + i + ".lore2");
    		item.interactable = Config.GetBool(plugin, "main-gui." + i + ".interact");
    		item.tag = Config.GetString(plugin, "main-gui." + i + ".tag");
        	items.add(item);
    	}
    }
    
	public void InitializeItems() {
    	for(int i = 0; i < items.size(); i++) {
    		if(!Utils.IsStringNullOrEmpty(items.get(i).item)) {
    			inv.setItem(i, CreateGuiItem(Material.getMaterial(items.get(i).item), items.get(i).name, items.get(i).interactable, items.get(i).tag, items.get(i).lore1, items.get(i).lore2));
    		}else {
    			inv.setItem(i, CreateGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§f", false, "", ""));
    		}
    	}
    }

    protected ItemStack CreateGuiItem(final Material material, final String name, boolean interact, final String tag, final String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
        	meta.setDisplayName(name);

        	if(lore.length != 0) {
        		meta.setLore(Arrays.asList(lore));
        	}
        	
        	Tags.Set(plugin, meta.getPersistentDataContainer(), "interact", interact ? 1 : 0, PersistentDataType.INTEGER);
        	Tags.Set(plugin, meta.getPersistentDataContainer(), "tag", tag, PersistentDataType.STRING);
        	
        	meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        	
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
        	if(tag.toString().equalsIgnoreCase("COMBINE") && Config.GetBool(plugin, "combine")) { Combine(); }
        	if(tag.toString().equalsIgnoreCase("SEPARATE") && Config.GetBool(plugin, "separate")) { Separate(); }
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

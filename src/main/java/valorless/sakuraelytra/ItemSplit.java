package valorless.sakuraelytra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
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

import valorless.sakuraelytra.ItemMerge.Items;
import valorless.valorlessutils.ValorlessUtils.*;

public class ItemSplit implements Listener {
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
	}

    public ItemSplit() {
    	InitializeLists();
    	
        inv = Bukkit.createInventory(player, Config.GetInt(plugin, "separate-gui-size"), Lang.Parse(Config.GetString(plugin, "separate-gui-name")));

        InitializeItems();
    }
    
    public void InitializeLists() {
    	items = new ArrayList<Items>();
    	slotTags = new ArrayList<String>();
    	for(int i = 0; i < Config.GetInt(plugin, "separate-gui-size"); i++) {
    		Items item = new Items();
    		item.name = Config.GetString(plugin, "separate-gui." + i + ".name");
    		item.item = Config.GetString(plugin, "separate-gui." + i + ".item");
    		item.lore1 = Config.GetString(plugin, "separate-gui." + i + ".lore1");
    		item.lore2 = Config.GetString(plugin, "separate-gui." + i + ".lore2");
    		item.interactable = Config.GetBool(plugin, "separate-gui." + i + ".interact");
        	items.add(item);
        	if(Config.GetString(plugin, "separate-gui." + i + ".tag") != null) {
        		slotTags.add(Config.GetString(plugin, "separate-gui." + i + ".tag"));
        	} else {
        		slotTags.add("null");
        	}
    	}
    }
    
	public void InitializeItems() {
    	for(int i = 0; i < items.size(); i++) {
    		if(!Utils.IsStringNullOrEmpty(items.get(i).item)) {
    			inv.setItem(i, CreateGuiItem(Material.getMaterial(items.get(i).item), items.get(i).name, items.get(i).interactable, items.get(i).lore1, items.get(i).lore2));
    		}else {
    			inv.setItem(i, CreateGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§f", false, "", ""));
    		}
    	}
    }

    protected ItemStack CreateGuiItem(final Material material, final String name, boolean interact, final String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
        	meta.setDisplayName(name);

        	if(lore.length != 0) {
        		meta.setLore(Arrays.asList(lore));
        	}
        	
        	Tags.Set(plugin, meta.getPersistentDataContainer(), "interact", interact ? 1 : 0, PersistentDataType.INTEGER);
        	
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
        Separate(e);
        

        // Using slots click is a best option for your inventory click's
       // p.sendMessage("You clicked at slot " + e.getRawSlot());
    }
    
    public void Separate(final InventoryClickEvent e) {
    	List<Integer> slots = new ArrayList<Integer>();
        if(e.getRawSlot() <= slotTags.size()) {
        	if(slotTags.get(e.getRawSlot()).equalsIgnoreCase("CONFIRM")) {
        		for (int i = 0; i < e.getInventory().getSize(); i++) {
        	    	if(slotTags.get(i).equalsIgnoreCase("PLAYER")) {
        	    		slots.add(i);
        	    	}
        	    }
        		
    			ItemStack elytra = e.getInventory().getItem(slots.get(0));
        		if(ValidateElytra(elytra)) {
        			String chestplateName = Tags.Get(plugin, elytra.getItemMeta().getPersistentDataContainer(), "chestplate-name", PersistentDataType.STRING).toString();
        			Material chestplateType = Material.getMaterial(Tags.Get(plugin, elytra.getItemMeta().getPersistentDataContainer(), "chestplate-type", PersistentDataType.STRING).toString());
        			ItemStack chestplate = new ItemStack(chestplateType);
        			ItemStack replacementElytra = new ItemStack(Material.ELYTRA);
        			ItemMeta replacementMeta = replacementElytra.getItemMeta();
        			replacementMeta.setDisplayName(elytra.getItemMeta().getDisplayName());
        			
    				List<String> lore = new ArrayList<String>();
        			lore = elytra.getItemMeta().getLore();
        			lore.remove(lore.size()-1);
        			ItemMeta chestMeta = elytra.getItemMeta();
        			chestMeta.setDisplayName(chestplateName);
        			chestMeta.setLore(lore);
        			chestMeta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
        			chestMeta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS);
        			chestMeta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        			Tags.Set(plugin, chestMeta.getPersistentDataContainer(), "combined", 0, PersistentDataType.INTEGER);
        			Tags.Set(plugin, replacementMeta.getPersistentDataContainer(), "combined", 0, PersistentDataType.INTEGER);
        			
        			if(Tags.Get(plugin, chestMeta.getPersistentDataContainer(), "MENDING", PersistentDataType.INTEGER) != null && (int)Tags.Get(plugin, chestMeta.getPersistentDataContainer(), "MENDING", PersistentDataType.INTEGER) != 0) {
        				replacementMeta.addEnchant(Enchantment.MENDING, (int)Tags.Get(plugin, chestMeta.getPersistentDataContainer(), "MENDING", PersistentDataType.INTEGER), false);
            			Tags.Set(plugin, chestMeta.getPersistentDataContainer(), "MENDING", 0, PersistentDataType.INTEGER);
        			}
        			if(Tags.Get(plugin, chestMeta.getPersistentDataContainer(), "UNBREAKING", PersistentDataType.INTEGER) != null && (int)Tags.Get(plugin, chestMeta.getPersistentDataContainer(), "UNBREAKING", PersistentDataType.INTEGER) != 0) {
        				replacementMeta.addEnchant(Enchantment.DURABILITY, (int)Tags.Get(plugin, chestMeta.getPersistentDataContainer(), "UNBREAKING", PersistentDataType.INTEGER), false);
        				Tags.Set(plugin, chestMeta.getPersistentDataContainer(), "UNBREAKING", 0, PersistentDataType.INTEGER);
        			}
        			replacementElytra.setItemMeta(replacementMeta);
        			
        			chestplate.setItemMeta(chestMeta);
        			e.getInventory().setItem(slots.get(0), chestplate);
        			e.getInventory().setItem(slots.get(1), replacementElytra);
        			player.sendMessage(Lang.Parse(Config.GetString(plugin, "separate-success")));
        			SFX.Play(Config.GetString(plugin, "separate-sound"), 1f, 1f, player);
        		}
        	}
        }
    }
    
    boolean ValidateElytra(ItemStack elytra) {
    	if(elytra == null) return false;
    	if(elytra.getType() == Material.ELYTRA) { 
    		if(Tags.Get(plugin, elytra.getItemMeta().getPersistentDataContainer(), "combined", PersistentDataType.INTEGER) != null) {
    			if((Integer)Tags.Get(plugin, elytra.getItemMeta().getPersistentDataContainer(), "combined", PersistentDataType.INTEGER) == 1) {
    				return true;
    			} else {
    				player.sendMessage(Lang.Parse(Config.GetString(plugin, "separate-fail")));
    				return false;
    			}
    		}
    		else {
    			return true;
    		}
    	}
    	else { 
			player.sendMessage(Lang.Parse(Config.GetString(plugin, "separate-fail")));
    		return false;
    	}
    }
    
    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (!e.getInventory().equals(inv)) return;
        for (int i = 0; i < e.getInventory().getSize(); i++) {
        	//if(e.getInventory().getItem(i) != null) {
        	//	Log.Warning(i + " - " + e.getInventory().getItem(i).getType().toString() + " - " + slotTags.get(i));
        	//}else {
        	//	Log.Warning(i + " - null - " + slotTags.get(i));
        	//}
        	
        	if(inv.getItem(i) != null && slotTags.get(i).equalsIgnoreCase("PLAYER")) {
        		player.getInventory().addItem(inv.getItem(i));
        		//e.getPlayer().sendMessage("Returned item to you (§f" + e.getInventory().getItem(i).getItemMeta().getDisplayName() + "§f)");
        	}
        }
    	//HandlerList.unregisterAll(this);
    }
}

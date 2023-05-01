package valorless.havenelytra;

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
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.valorlessutils.ValorlessUtils.*;
import valorless.valorlessutils.config.Config;

public class ItemMergeSmithingTable implements Listener {
	public static JavaPlugin plugin;
	private final Inventory inv;
	private List<Items> items;
	private List<String> slotTags;
	public static Config config;
	
	public class Items {
		public String name = "";
		public String item = "";
		public List<String> lore = new ArrayList<String>();
		public Boolean interactable = false;
	}

    public ItemMergeSmithingTable(Player player) {
    	InitializeLists();
    	
        //inv = Bukkit.createInventory(null, Config.GetInt(plugin, "gui-size"), Config.GetString(plugin, "gui-name"));
    	inv =  Bukkit.createInventory(null, InventoryType.SMITHING, config.GetString("gui-name"));

        InitializeItems();
    }
    
    public void InitializeLists() {
    	items = new ArrayList<Items>();
    	slotTags = new ArrayList<String>();
    	for(int i = 0; i < config.GetInt("gui-size"); i++) {
    		Items item = new Items();
    		item.name = config.GetString("gui." + i + ".name");
    		item.item = config.GetString("gui." + i + ".item");
    		item.lore = config.GetStringList("gui." + i + ".lore");
    		item.interactable = config.GetBool("gui." + i + ".interact");
        	items.add(item);
        	if(config.GetString("gui." + i + ".tag") != null) {
        		slotTags.add(config.GetString("gui." + i + ".tag"));
        	} else {
        		slotTags.add("null");
        	}
    	}
    }
    
	public void InitializeItems() {
		
    }
	
	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent e) {
		
	}

    protected ItemStack CreateGuiItem(final Material material, final String name, boolean interact, final String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
        	meta.setDisplayName(name);

        	if(!Utils.IsStringNullOrEmpty(lore[0]) || !Utils.IsStringNullOrEmpty(lore[1])) {
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
        if (!e.getInventory().equals(inv)) return;

        for(ItemStack item : e.getInventory().getContents()) {
        	if(item != null) Log.Info(plugin, item.getType().toString());
        }
    	Log.Info(plugin, String.valueOf(e.getRawSlot()));
		ItemStack helmet = e.getInventory().getItem(0);
		//Log.Info(plugin, helmet.getType().toString());
		ItemStack hat = e.getInventory().getItem(1);
		//Log.Info(plugin, hat.getType().toString());
		if(e.getInventory().getItem(0) != null && e.getInventory().getItem(1) != null) {
    		if(ValidateHelmet(helmet) && ValidateHat(hat)) {
    			ItemStack result = new ItemStack(Material.CARVED_PUMPKIN);
    			Integer hatID = hat.getItemMeta().getCustomModelData();
    			String helmetName;
    			if(!Utils.IsStringNullOrEmpty(helmet.getItemMeta().getDisplayName())) {
    				helmetName = helmet.getItemMeta().getDisplayName();
    			} else {
    				helmetName = "§e" + FixName(helmet.getType().name().toLowerCase());
    			}
    			String hatName = hat.getItemMeta().getDisplayName();
				List<String> lore = new ArrayList<String>();
    			if(helmet.getItemMeta().getLore() != null) { 
    				lore = helmet.getItemMeta().getLore();
    			}
    			lore.add("§r");
    			lore.add(helmetName);
    			hat.setItemMeta(helmet.getItemMeta());
    			ItemMeta hatMeta = hat.getItemMeta();
    			hatMeta.setCustomModelData(hatID);
    			hatMeta.setDisplayName(hatName);
    			hatMeta.setLore(lore);
    			hatMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, GetModifier(helmet, Attribute.GENERIC_ARMOR));
    			hatMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, GetModifier(helmet, Attribute.GENERIC_ARMOR_TOUGHNESS));
    			hatMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, GetModifier(helmet, Attribute.GENERIC_KNOCKBACK_RESISTANCE));
    			result.setItemMeta(hatMeta);
    			e.getInventory().setItem(2, result);
    			//e.getInventory().clear(slots.get(0));
    		}
		}/*
		

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
        
        List<Integer> slots = new ArrayList<Integer>();
        if(e.getRawSlot() <= slotTags.size()) {
        	if(slotTags.get(e.getRawSlot()).equalsIgnoreCase("CONFIRM")) {
        		for (int i = 0; i < e.getInventory().getSize(); i++) {
        	    	if(slotTags.get(i).equalsIgnoreCase("PLAYER")) {
        	    		slots.add(i);
        	    	}
        	    }
        		ItemStack helmet = e.getInventory().getItem(slots.get(0));
    			ItemStack hat = e.getInventory().getItem(slots.get(1));
        		if(ValidateHelmet(helmet) && ValidateHat(hat)) {
        			Integer hatID = hat.getItemMeta().getCustomModelData();
        			String helmetName;
        			if(!Utils.IsStringNullOrEmpty(helmet.getItemMeta().getDisplayName())) {
        				helmetName = helmet.getItemMeta().getDisplayName();
        			} else {
        				helmetName = "§e" + FixName(helmet.getType().name().toLowerCase());
        			}
        			String hatName = hat.getItemMeta().getDisplayName();
    				List<String> lore = new ArrayList<String>();
        			if(helmet.getItemMeta().getLore() != null) { 
        				lore = helmet.getItemMeta().getLore();
        			}
        			lore.add("§r");
        			lore.add(helmetName);
        			hat.setItemMeta(helmet.getItemMeta());
        			ItemMeta hatMeta = hat.getItemMeta();
        			hatMeta.setCustomModelData(hatID);
        			hatMeta.setDisplayName(hatName);
        			hatMeta.setLore(lore);
        			hatMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, GetModifier(helmet, Attribute.GENERIC_ARMOR));
        			hatMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, GetModifier(helmet, Attribute.GENERIC_ARMOR_TOUGHNESS));
        			hatMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, GetModifier(helmet, Attribute.GENERIC_KNOCKBACK_RESISTANCE));
        			hat.setItemMeta(hatMeta);
        			e.getInventory().clear(slots.get(0));
        		}
        	}
        }

        // Using slots click is a best option for your inventory click's
       // p.sendMessage("You clicked at slot " + e.getRawSlot());
        */
    }
    
    public AttributeModifier GetModifier(ItemStack item, Attribute attribute) {
    	Double amount = 0.0;
    	if(attribute == Attribute.GENERIC_ARMOR) {
    		if(item.getType() == Material.LEATHER_HELMET) {
    			amount = 1.0;
    		} else if(item.getType() == Material.IRON_HELMET) {
    			amount = 1.0;
    		} else if(item.getType() == Material.GOLDEN_HELMET) {
    			amount = 2.0;
    		} else if(item.getType() == Material.CHAINMAIL_HELMET) {
    			amount = 2.0;
    		} else if(item.getType() == Material.DIAMOND_HELMET) {
    			amount = 3.0;
    		} else if(item.getType() == Material.NETHERITE_HELMET) {
    			amount = 3.0;
    		} else {
    			amount = 0.0;
    		}
    		return new AttributeModifier(UUID.randomUUID(), "generic.armor", amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
    	}
    	if(attribute == Attribute.GENERIC_ARMOR_TOUGHNESS) {
    		if(item.getType() == Material.DIAMOND_HELMET) {
    			amount = 2.0;
    		} else if(item.getType() == Material.NETHERITE_HELMET) {
    			amount = 3.0;
    		} else {
    			amount = 0.0;
    		}
    		return new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
    	}
    	if(attribute == Attribute.GENERIC_KNOCKBACK_RESISTANCE) {
    		if(item.getType() == Material.NETHERITE_HELMET) {
    			amount = 0.1;
    		} else {
    			amount = 0.0;
    		}
    		return new AttributeModifier(UUID.randomUUID(), "generic.knockback.resistance", amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
    	}
    	return null;
    }
    
    
    boolean ValidateHelmet(ItemStack helmet) {
    	if(helmet == null) return false;
    	if(helmet.getType() == Material.LEATHER_HELMET
    			|| helmet.getType() == Material.IRON_HELMET
    			|| helmet.getType() == Material.GOLDEN_HELMET
    			|| helmet.getType() == Material.CHAINMAIL_HELMET
    			|| helmet.getType() == Material.DIAMOND_HELMET
    			|| helmet.getType() == Material.NETHERITE_HELMET) { 
    		return true;
    	}
    	else return false;
    }
    
    boolean ValidateHat(ItemStack hat) {
    	if(hat == null) return false;
    	if(hat.getType() == Material.CARVED_PUMPKIN && hat.hasItemMeta()) { 
    		return true;
    	}
    	else return false;
    }
    
    String FixName(String string) {
    	string = string.replace('_', ' ');
    	
        char[] charArray = string.toCharArray();
        boolean foundSpace = true;

        for(int i = 0; i < charArray.length; i++) {

          if(Character.isLetter(charArray[i])) {

            if(foundSpace) {

              charArray[i] = Character.toUpperCase(charArray[i]);
              foundSpace = false;
            }
          }

          else {
            foundSpace = true;
          }
        }

        string = String.valueOf(charArray);
    	
    	return string;
    }
    
    /*
    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
          e.setCancelled(true);
        }
    }*/

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        for (int i = 0; i < e.getInventory().getSize(); i++) {
        	//if(e.getInventory().getItem(i) != null) {
        	//	Log.Warning(i + " - " + e.getInventory().getItem(i).getType().toString() + " - " + slotTags.get(i));
        	//}else {
        	//	Log.Warning(i + " - null - " + slotTags.get(i));
        	//}
        	
        	if(e.getInventory().getItem(i) != null && slotTags.get(i).equalsIgnoreCase("PLAYER")) {
        		e.getPlayer().getInventory().addItem(inv.getItem(i));
        		//e.getPlayer().sendMessage("Returned item to you (§f" + e.getInventory().getItem(i).getItemMeta().getDisplayName() + "§f)");
        	}
        }
    	HandlerList.unregisterAll(this);
    }
}

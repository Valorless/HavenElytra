package valorless.havenelytra;

import java.util.ArrayList;
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

import com.google.gson.Gson;

import valorless.valorlessutils.ValorlessUtils.*;
import valorless.valorlessutils.config.Config;

public class ItemMerge implements Listener {
	public static JavaPlugin plugin;
	private final Inventory inv;
	public Player player;
	private List<Items> items;
	private List<String> slotTags;
	public static Config config;
	
	public class Items {
		public String name = "";
		public String item = "";
		public List<String> lore = new ArrayList<String>();
		public Boolean interactable = false;
	}

    public ItemMerge() {
    	InitializeLists();
    	
        inv = Bukkit.createInventory(player, config.GetInt("gui-size"), Lang.Parse(config.GetString("gui-name")));

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
    	for(int i = 0; i < items.size(); i++) {
    		if(!Utils.IsStringNullOrEmpty(items.get(i).item)) {
    			inv.setItem(i, CreateGuiItem(Material.getMaterial(items.get(i).item), items.get(i).name, items.get(i).interactable, items.get(i).lore));
    		}else {
    			inv.setItem(i, CreateGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§f", false, null));
    		}
    	}
    }

    protected ItemStack CreateGuiItem(final Material material, final String name, boolean interact, final List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
        	meta.setDisplayName(name);

        	if(lore != null) {
        		meta.setLore(lore);
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
            	new NamespacedKey(plugin, "interact"), PersistentDataType.INTEGER) != null) {
        			if(clickedItem.getItemMeta().getPersistentDataContainer().get(
        			new NamespacedKey(plugin, "interact"), PersistentDataType.INTEGER) == 0) {
        				e.setCancelled(true);
        			}
        		}
        	}
        }

        if (clickedItem == null || clickedItem.getType().isAir()) return;
        Combine(e);
        

        // Using slots click is a best option for your inventory click's
       // p.sendMessage("You clicked at slot " + e.getRawSlot());
    }
    
    public void Combine(final InventoryClickEvent e) {
    	List<Integer> slots = new ArrayList<Integer>();
        if(e.getRawSlot() <= slotTags.size()) {
        	if(slotTags.get(e.getRawSlot()).equalsIgnoreCase("CONFIRM")) {
        		for (int i = 0; i < e.getInventory().getSize(); i++) {
        	    	if(slotTags.get(i).equalsIgnoreCase("PLAYER")) {
        	    		slots.add(i);
        	    	}
        	    }
        		ItemStack chestplate = e.getInventory().getItem(slots.get(0));
    			ItemStack elytra = e.getInventory().getItem(slots.get(1));
        		if(ValidateChestplate(chestplate) && ValidateElytra(elytra)) {
        			
        			ItemMeta eMeta = elytra.getItemMeta();
        			
        			String chestplateName;
        			if(!Utils.IsStringNullOrEmpty(chestplate.getItemMeta().getDisplayName())) {
        				chestplateName = chestplate.getItemMeta().getDisplayName();
        			} else {
        				chestplateName = "§e" + FixName(chestplate.getType().name().toLowerCase());
        			}
        			String elytraName = elytra.getItemMeta().getDisplayName();
    				List<String> lore = new ArrayList<String>();
        			if(chestplate.getItemMeta().getLore() != null) { 
        				lore = chestplate.getItemMeta().getLore();
        			}
        			lore.add("§r");
        			lore.add("§d+ " + chestplateName);
        			elytra.setItemMeta(chestplate.getItemMeta());
        			ItemMeta elytraMeta = elytra.getItemMeta();
        			elytraMeta.setDisplayName(elytraName);
        			elytraMeta.setLore(lore);
        			elytraMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, GetModifier(chestplate, Attribute.GENERIC_ARMOR));
        			elytraMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, GetModifier(chestplate, Attribute.GENERIC_ARMOR_TOUGHNESS));
        			elytraMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, GetModifier(chestplate, Attribute.GENERIC_KNOCKBACK_RESISTANCE));
        			Tags.Set(plugin, elytraMeta.getPersistentDataContainer(), "combined", 1, PersistentDataType.INTEGER);
        			Tags.Set(plugin, elytraMeta.getPersistentDataContainer(), "chestplate-type", chestplate.getType().toString(), PersistentDataType.STRING);
        			Tags.Set(plugin, elytraMeta.getPersistentDataContainer(), "chestplate-name", chestplateName, PersistentDataType.STRING);
        			//String jsonLore = new Gson().toJson(chestplate.getItemMeta().getLore());
        			//Tags.Set(plugin, elytraMeta.getPersistentDataContainer(), "lore", jsonLore, PersistentDataType.STRING);

        			Map<Enchantment, Integer> enchants = eMeta.getEnchants();
        			if(enchants.containsKey(Enchantment.MENDING)) {
        				Tags.Set(plugin, elytraMeta.getPersistentDataContainer(), "MENDING", enchants.get(Enchantment.MENDING), PersistentDataType.INTEGER);
        				elytraMeta.removeEnchant(Enchantment.MENDING);
        			}
        			if(enchants.containsKey(Enchantment.DURABILITY)) {
        				Tags.Set(plugin, elytraMeta.getPersistentDataContainer(), "UNBREAKING", enchants.get(Enchantment.DURABILITY), PersistentDataType.INTEGER);
        				elytraMeta.removeEnchant(Enchantment.DURABILITY);
        			}
        			
        			Map <Enchantment, Integer> enc = chestplate.getItemMeta().getEnchants();
        			for(Map.Entry<Enchantment, Integer> enchant : enc.entrySet()) {
        				elytraMeta.addEnchant(enchant.getKey(), enchant.getValue(), true);
        			}
        			
        			elytra.setItemMeta(elytraMeta);
        			e.getInventory().clear(slots.get(0));
        			player.sendMessage(Lang.Get("combine-success"));
        			SFX.Play(config.GetString("sound"), 1f, 1f, player);
        		}
        	}
        }
    }
    
    public AttributeModifier GetModifier(ItemStack item, Attribute attribute) {
    	Double amount = 0.0;
    	if(attribute == Attribute.GENERIC_ARMOR) {
    		if(item.getType() == Material.LEATHER_CHESTPLATE) {
    			amount = 3.0;
    		} else if(item.getType() == Material.IRON_CHESTPLATE) {
    			amount = 6.0;
    		} else if(item.getType() == Material.GOLDEN_CHESTPLATE) {
    			amount = 5.0;
    		} else if(item.getType() == Material.CHAINMAIL_CHESTPLATE) {
    			amount = 5.0;
    		} else if(item.getType() == Material.DIAMOND_CHESTPLATE) {
    			amount = 8.0;
    		} else if(item.getType() == Material.NETHERITE_CHESTPLATE) {
    			amount = 8.0;
    		} else {
    			amount = 0.0;
    		}
    		return new AttributeModifier(UUID.randomUUID(), "generic.armor", amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
    	}
    	if(attribute == Attribute.GENERIC_ARMOR_TOUGHNESS) {
    		if(item.getType() == Material.DIAMOND_CHESTPLATE) {
    			amount = 2.0;
    		} else if(item.getType() == Material.NETHERITE_CHESTPLATE) {
    			amount = 3.0;
    		} else {
    			amount = 0.0;
    		}
    		return new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
    	}
    	if(attribute == Attribute.GENERIC_KNOCKBACK_RESISTANCE) {
    		if(item.getType() == Material.NETHERITE_CHESTPLATE) {
    			amount = 0.1;
    		} else {
    			amount = 0.0;
    		}
    		return new AttributeModifier(UUID.randomUUID(), "generic.knockback.resistance", amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
    	}
    	return null;
    }
    
    
    boolean ValidateChestplate(ItemStack chestplate) {
    	if(chestplate == null) return false;
    	if(chestplate.getType() == Material.LEATHER_CHESTPLATE
    			|| chestplate.getType() == Material.IRON_CHESTPLATE
    			|| chestplate.getType() == Material.GOLDEN_CHESTPLATE
    			|| chestplate.getType() == Material.CHAINMAIL_CHESTPLATE
    			|| chestplate.getType() == Material.DIAMOND_CHESTPLATE
    			|| chestplate.getType() == Material.NETHERITE_CHESTPLATE) { 
    		return true;
    	}
    	else return false;
    }
    
    boolean ValidateElytra(ItemStack elytra) {
    	if(elytra == null) return false;
    	if(elytra.getType() == Material.ELYTRA) { 
    		if(Tags.Get(plugin, elytra.getItemMeta().getPersistentDataContainer(), "combined", PersistentDataType.INTEGER) != null) {
    			if((Integer)Tags.Get(plugin, elytra.getItemMeta().getPersistentDataContainer(), "combined", PersistentDataType.INTEGER) == 0) {
    				return true;
    			} else {
    				player.sendMessage(Lang.Get("combine-fail"));
    				return false;
    			}
    		}
    		else {
    			return true;
    		}
    	}
    	else { 
			player.sendMessage(Lang.Get("combine-fail"));
    		return false;
    	}
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
        if (!e.getInventory().equals(inv)) return;
        for (int i = 0; i < inv.getSize(); i++) {
        	//if(e.getInventory().getItem(i) != null) {
        	//	Log.Warning(i + " - " + e.getInventory().getItem(i).getType().toString() + " - " + slotTags.get(i));
        	//}else {
        	//	Log.Warning(i + " - null - " + slotTags.get(i));
        	//}
        	
        	if(inv.getItem(i) != null && slotTags.get(i).equalsIgnoreCase("PLAYER")) {
        		//e.getPlayer().getInventory().addItem(inv.getItem(i));
        		player.getInventory().addItem(inv.getItem(i));
        		//e.getPlayer().sendMessage("Returned item to you (§f" + e.getInventory().getItem(i).getItemMeta().getDisplayName() + "§f)");
        	}
        }
    	//HandlerList.unregisterAll(this);
    }
}

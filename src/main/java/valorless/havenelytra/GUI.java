package valorless.havenelytra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import com.google.common.collect.Multimap;

import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.ValorlessUtils.Tags;
import valorless.valorlessutils.ValorlessUtils.Utils;
import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.json.JsonUtils;

enum Menu { main, combine, separate }
public class GUI implements Listener {
	public static JavaPlugin plugin;
	private Inventory inv;
	public Player player;
	private List<Items> items;
	public Config config;
	public String filler;
	public Menu menu;
	public final int size;
	
	public class Items {
		public String name = "";
		public String item = "";
		public List<String> lore = new ArrayList<String>();
		public Boolean interactable = false;
		public int customModelData;
		public String tag;
	}

	public GUI(Player player) {
		this.player = player;
		this.config = new Config(plugin, "gui-main.yml");
		this.size = config.GetInt("gui-size");
		inv = Bukkit.createInventory(player, config.GetInt("gui-size"), Lang.Parse(config.GetString("gui-name")));
		UpdateGUI(Menu.main);
		SFX.Play(config.GetString("sound"), 1f, 1f, player);
        OpenInventory(player);
    }
	
	public void UpdateGUI(Menu menu) {
		//Log.Info(plugin, menu.toString());
		
		try {
			this.menu = menu;
			if(items != null) items.clear();
			if(inv != null) inv.clear();
			config = new Config(plugin, "gui-" + menu.toString() + ".yml");
			//Rename(player, Lang.Parse(config.GetString("gui-name")));
    		InitializeLists();
    		//while(inv != null) { inv = null; }
    		inv = Bukkit.createInventory(player, config.GetInt("gui-size"), Lang.Parse(config.GetString("gui-name")));
    		player.openInventory(inv);
    		//inv = Bukkit.createInventory(player, config.GetInt("gui-size"), Lang.Parse(config.GetString("gui-name")));
        	filler = config.GetString("filler");
        	InitializeItems();
		} catch(Exception e) {
			Log.Error(plugin, e.getMessage());
		}
		
	}
	
	public void InitializeLists() {
    	items = new ArrayList<Items>();
    	for(int i = 0; i < size; i++) {
    		Items item = new Items();
    		item.name = config.GetString("gui." + i + ".name");
    		item.item = config.GetString("gui." + i + ".item");
    		item.lore = config.GetStringList("gui." + i + ".lore");
    		item.interactable = config.GetBool("gui." + i + ".interact");
    		if(config.GetInt("gui." + i + ".custom-model-data") != null) {
    			item.customModelData = config.GetInt("gui." + i + ".custom-model-data");
    		}
    		item.tag = config.GetString("gui." + i + ".tag");
    		if(Utils.IsStringNullOrEmpty(config.GetString("gui." + i + ".tag"))) {
    			item.tag = "";
    		}
        	items.add(item);
    	}
    }
    
	public void InitializeItems() {
		int g = 0;
    	for(int i = 0; i < items.size(); i++) {
    		if(!Utils.IsStringNullOrEmpty(items.get(i).item)) {
    			inv.setItem(i, CreateGuiItem(Material.getMaterial(items.get(i).item), items.get(i).name, items.get(i).interactable, items.get(i).tag, items.get(i).lore, items.get(i).customModelData));
    			g++;
    		} else {
    			inv.setItem(i, CreateGuiItem(Material.getMaterial(filler.toUpperCase()), "§f", false, "", null, 80000));
    			
    		}
    	}
    	if(g == 0) {
    		Log.Error(plugin, String.format("GUI of 'gui-%s.yml' is empty. Consider using one of the templates!", menu));
    		Log.Error(plugin, "This is not an error with the plugin, you have not set up the GUI like adviced on Spigot.");
    	}
    }
	
	protected ItemStack CreateGuiItem(final Material material, final String name, boolean interact, final String tag, final List<String> lore, int customModelData) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
        	meta.setDisplayName(Lang.Parse(name));
        	if(lore != null) {
        		for(String entry : lore) {
        			entry = Lang.Parse(entry);
        		}
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
        
        if(menu == Menu.main) {
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
            }/* else {
            	if(player.getItemOnCursor() != null || !player.getItemOnCursor().getType().isAir()) {
            		//Log.Error(plugin, String.valueOf(e.getRawSlot()));
            		if(e.getRawSlot() < items.size() && e.getRawSlot() != -999) {
            			if(Utils.IsStringNullOrEmpty(items.get(e.getRawSlot()).item)) {
                    		//Log.Error(plugin, "pain");
            				ItemStack i = player.getItemOnCursor();
            				e.setCancelled(true);
            				inv.setItem(e.getRawSlot(), null);
            				player.setItemOnCursor(null);
            				player.getInventory().addItem(i);
            			}
            		}
            	}
            }*/
            if (clickedItem == null || clickedItem.getType().isAir()) return;
            
            Object tag = Tags.Get(plugin, clickedItem.getItemMeta().getPersistentDataContainer(), "tag", PersistentDataType.STRING);
            if(tag == null) return;
            if(!Utils.IsStringNullOrEmpty(tag.toString())) {
            	//Log.Info(plugin, tag);
            	if(tag.toString().equalsIgnoreCase("COMBINE")){
            		if(player.hasPermission("havenelytra.combine")) {
            			if(HavenElytra.config.GetBool("combine")) { UpdateGUI(Menu.combine); return; }
            			else { 
            				player.sendMessage(Lang.Get("combine-disabled"));
            			}
            		}else {
            			player.sendMessage(Lang.Get("no-permission"));
            		}
            	}
            	if(tag.toString().equalsIgnoreCase("SEPARATE")) {
            		if(player.hasPermission("havenelytra.separate")) {
            			if(HavenElytra.config.GetBool("separate")) { UpdateGUI(Menu.separate); return; }
            			else { 
            			player.sendMessage(Lang.Get("separate-disabled"));
            			}
            		}else {
            			player.sendMessage(Lang.Get("no-permission"));
            		}
            	}
            }
        }
        
        if(menu == Menu.combine) {
        	ItemStack clickedItem = e.getCurrentItem();
        	if(e.getRawSlot() >= items.size()) { return; }
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
            }/* else {
            	if(player.getItemOnCursor() != null || !player.getItemOnCursor().getType().isAir()) {
            		//Log.Error(plugin, String.valueOf(e.getRawSlot()));
            		if(e.getRawSlot() < items.size() && e.getRawSlot() != -999) {
            			if(Utils.IsStringNullOrEmpty(items.get(e.getRawSlot()).item)) {
                    		//Log.Error(plugin, "pain");
            				ItemStack i = player.getItemOnCursor();
            				e.setCancelled(true);
            				inv.setItem(e.getRawSlot(), null);
            				player.setItemOnCursor(null);
            				player.getInventory().addItem(i);
            			}
            		}
            	}
            }*/
            if (clickedItem == null || clickedItem.getType().isAir()) return;
            
        	List<Integer> slots = new ArrayList<Integer>();
            if(e.getRawSlot() < items.size()) {
            	if(items.get(e.getRawSlot()).tag.equalsIgnoreCase("BACK")) {
                    ReturnItems();
            		UpdateGUI(Menu.main);
            	}
            	
            	if(items.get(e.getRawSlot()).tag.equalsIgnoreCase("CONFIRM")) {
            		for (int i = 0; i < e.getInventory().getSize(); i++) {
            			if(items.get(i).tag.equalsIgnoreCase("PLAYER") || items.get(i).tag.equalsIgnoreCase("RESULT")) {
            	    		slots.add(i);
            	    		//Log.Warning(plugin, items.get(i).name);
            	    	}
            	    }
            		ItemStack chestplate = e.getInventory().getItem(slots.get(0));
        			ItemStack elytra = e.getInventory().getItem(slots.get(1));
            		if(ValidateChestplate(chestplate) && ValidateElytra(elytra)) {
            			ItemStack tempItem = new ItemStack(Material.ELYTRA);
            			ItemMeta tempStorage = tempItem.getItemMeta();
            			Tags.Set(plugin, tempStorage.getPersistentDataContainer(), "elytra-meta", JsonUtils.toJson(elytra.getItemMeta()), PersistentDataType.STRING);
            			Tags.Set(plugin, tempStorage.getPersistentDataContainer(), "chestplate-meta", JsonUtils.toJson(chestplate.getItemMeta()), PersistentDataType.STRING);
            			        			            			
            			String chestplateName;
            			if(!Utils.IsStringNullOrEmpty(chestplate.getItemMeta().getDisplayName())) {
            				chestplateName = chestplate.getItemMeta().getDisplayName();
            			} else {
            				chestplateName = TranslateChestplateName(FixName(chestplate.getType().name().toLowerCase()));
            			}
            			String elytraName = elytra.getItemMeta().getDisplayName();
        				List<String> lore = new ArrayList<String>();
            			if(chestplate.getItemMeta().getLore() != null) {
            				for(String str : chestplate.getItemMeta().getLore()) {
            					lore.add(Lang.Parse(str));
            				}
            			}
            			if(elytra.getItemMeta().getLore() != null) {
            				lore.add("§r");
            				for(String str : elytra.getItemMeta().getLore()) {
            					lore.add(str);
            				}
            			}
            			lore.add("§r");
            			lore.add(Lang.Get("combined-elytra-lore", chestplateName));
            			//lore.add("§7+ [" + chestplateName + "§7]");
            			ItemMeta chestMeta = chestplate.getItemMeta();
            			chestMeta.setDisplayName(elytraName);
            			chestMeta.setLore(lore);
            			
            			//chestMeta.setAttributeModifiers(chestplate.getItemMeta().getAttributeModifiers());
            			Multimap<Attribute, AttributeModifier> attr = chestMeta.getAttributeModifiers(EquipmentSlot.CHEST);
            			while(attr == null) {
            				//Basically to wait an extra frame to ensure it's not null.
            				attr = chestMeta.getAttributeModifiers(EquipmentSlot.CHEST);
            			}
            			
            			for (Entry<Attribute, AttributeModifier> attribute : attr.entries()) {
            				try {
            					chestMeta.addAttributeModifier(attribute.getKey(), attribute.getValue());
            				} catch(Exception ex) {}
            			}
            			
            			if(!attr.containsKey(Attribute.GENERIC_ARMOR)) {
            				chestMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, GetModifier(chestplate, Attribute.GENERIC_ARMOR));
            			}
            			if(!attr.containsKey(Attribute.GENERIC_ARMOR_TOUGHNESS)) {
            				chestMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, GetModifier(chestplate, Attribute.GENERIC_ARMOR_TOUGHNESS));
            			}
            			if(!attr.containsKey(Attribute.GENERIC_KNOCKBACK_RESISTANCE)) {
            				chestMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, GetModifier(chestplate, Attribute.GENERIC_KNOCKBACK_RESISTANCE));
            			}
            			
            			
            			//chestMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, GetModifier(chestplate, Attribute.GENERIC_ARMOR));
            			//chestMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, GetModifier(chestplate, Attribute.GENERIC_ARMOR_TOUGHNESS));
            			//chestMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, GetModifier(chestplate, Attribute.GENERIC_KNOCKBACK_RESISTANCE));
            			Tags.Set(plugin, chestMeta.getPersistentDataContainer(), "combined", 1, PersistentDataType.INTEGER);
            			Tags.Set(plugin, chestMeta.getPersistentDataContainer(), "chestplate-type", chestplate.getType().toString(), PersistentDataType.STRING);
            			Tags.Set(plugin, chestMeta.getPersistentDataContainer(), "chestplate-name", chestplateName, PersistentDataType.STRING);
            			chestMeta.setCustomModelData(null);
            			if(elytra.getItemMeta().hasCustomModelData()) {
            				chestMeta.setCustomModelData(elytra.getItemMeta().getCustomModelData());
            			}
            			Tags.Set(plugin, chestMeta.getPersistentDataContainer(), "elytra-meta", 
            					Tags.Get(plugin, tempStorage.getPersistentDataContainer(), "elytra-meta", PersistentDataType.STRING).toString()
            					, PersistentDataType.STRING);
            			Tags.Set(plugin, chestMeta.getPersistentDataContainer(), "chestplate-meta", 
            					Tags.Get(plugin, tempStorage.getPersistentDataContainer(), "chestplate-meta", PersistentDataType.STRING).toString()
            					, PersistentDataType.STRING);
            			
            			chestplate.setItemMeta(chestMeta);
            			chestplate.setType(Material.ELYTRA);
            			e.getInventory().clear(slots.get(0));
            			e.getInventory().clear(slots.get(1));
            			e.getInventory().setItem(slots.get(2), chestplate);
            			player.sendMessage(Lang.Get("combine-success"));
            			SFX.Play(config.GetString("sound"), 1f, 1f, player);
            		}
            	}
            }
        }
        
        if(menu == Menu.separate) {
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
            }/* else {
            	if(player.getItemOnCursor() != null || !player.getItemOnCursor().getType().isAir()) {
            		//Log.Error(plugin, String.valueOf(e.getRawSlot()));
            		if(e.getRawSlot() < items.size() && e.getRawSlot() != -999) {
            			if(Utils.IsStringNullOrEmpty(items.get(e.getRawSlot()).item)) {
                    		//Log.Error(plugin, "pain");
            				ItemStack i = player.getItemOnCursor();
            				e.setCancelled(true);
            				inv.setItem(e.getRawSlot(), null);
            				player.setItemOnCursor(null);
            				player.getInventory().addItem(i);
            			}
            		}
            	}
            }*/
            if (clickedItem == null || clickedItem.getType().isAir()) return;
            
        	List<Integer> slots = new ArrayList<Integer>();
            if(e.getRawSlot() < items.size()) {
            	if(items.get(e.getRawSlot()).tag.equalsIgnoreCase("BACK")) {
                    ReturnItems();
            		UpdateGUI(Menu.main);
            	}
            	if(items.get(e.getRawSlot()).tag.equalsIgnoreCase("CONFIRM")) {
            		for (int i = 0; i < e.getInventory().getSize(); i++) {
            			if(items.get(i).tag.equalsIgnoreCase("PLAYER") || items.get(i).tag.equalsIgnoreCase("RESULT")) {
            	    		slots.add(i);
            	    	}
            	    }
        			ItemStack elytra = e.getInventory().getItem(slots.get(0));
            		if(ValidateElytra(elytra)) {
            			Material chestplateType = Material.getMaterial(Tags.Get(plugin, elytra.getItemMeta().getPersistentDataContainer(), "chestplate-type", PersistentDataType.STRING).toString());
            			ItemStack chestplate = new ItemStack(chestplateType);
            			ItemStack replacementElytra = new ItemStack(Material.ELYTRA);
            			Map <Enchantment, Integer> enchants = elytra.getItemMeta().getEnchants();
            			replacementElytra.setItemMeta((ItemMeta)JsonUtils.fromJson(Tags.Get(plugin, elytra.getItemMeta().getPersistentDataContainer(), "elytra-meta", PersistentDataType.STRING).toString()));
            			chestplate.setItemMeta((ItemMeta)JsonUtils.fromJson(Tags.Get(plugin, elytra.getItemMeta().getPersistentDataContainer(), "chestplate-meta", PersistentDataType.STRING).toString()));
            			ItemMeta chestMeta = chestplate.getItemMeta();
            			for(Map.Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            				chestMeta.addEnchant(enchant.getKey(), enchant.getValue(), true);
            			}
            			chestplate.setItemMeta(chestMeta); //Reapply again to add enchants.
            			e.getInventory().clear(slots.get(0));
            			e.getInventory().setItem(slots.get(1), replacementElytra);
            			e.getInventory().setItem(slots.get(2), chestplate);
            			player.sendMessage(Lang.Get("separate-success"));
            			SFX.Play(config.GetString("sound"), 1f, 1f, player);
            		}
            	}
            }
        }
        // Using slots click is a best option for your inventory click's
        //player.sendMessage("You clicked at slot " + e.getRawSlot());
    }
    
    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (!e.getInventory().equals(inv)) return;
        ReturnItems();
    }
    
    void ReturnItems() {
    	for (int i = 0; i < inv.getSize(); i++) {
        	if(inv.getItem(i) != null) {
        		if(items.get(i).tag.toString().equalsIgnoreCase("PLAYER") || items.get(i).tag.toString().equalsIgnoreCase("RESULT")) {
        			player.getInventory().addItem(inv.getItem(i));
        		}
        	}
        }
    }
    
    // UTILS
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
    
    public AttributeModifier GetModifier(ItemStack item, AttributeModifier attribute) {
    	return new AttributeModifier(attribute.getUniqueId(), attribute.getName(), attribute.getAmount(), attribute.getOperation(), attribute.getSlot());
    }
    
    boolean ValidateChestplate(ItemStack chestplate) {
    	if(chestplate == null) {
    		return false;
    	}
    	if(chestplate.getType() == Material.LEATHER_CHESTPLATE && config.GetBool("leather") == false) {
    		player.sendMessage(Lang.Get("material-disabled"));
    		return false;
    	}else if(chestplate.getType() == Material.IRON_CHESTPLATE && config.GetBool("iron") == false) {
    		player.sendMessage(Lang.Get("material-disabled"));
    		return false;
    	}else if(chestplate.getType() == Material.GOLDEN_CHESTPLATE && config.GetBool("gold") == false) {
    		player.sendMessage(Lang.Get("material-disabled"));
    		return false;
    	}else if(chestplate.getType() == Material.CHAINMAIL_CHESTPLATE && config.GetBool("chainmail") == false) {
    		player.sendMessage(Lang.Get("material-disabled"));
    		return false;
    	}else if(chestplate.getType() == Material.DIAMOND_CHESTPLATE && config.GetBool("diamond") == false) {
    		player.sendMessage(Lang.Get("material-disabled"));
    		return false;
    	}else if(chestplate.getType() == Material.NETHERITE_CHESTPLATE && config.GetBool("netherite") == false) {
    		player.sendMessage(Lang.Get("material-disabled"));
    		return false;
    	}else {
    		return true;
    	}
    }
    
    boolean ValidateElytra(ItemStack elytra) {
    	if(elytra == null) return false;
    	if(menu == Menu.combine) {
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
    	}else if(menu == Menu.separate) {
    		if(elytra.getType() == Material.ELYTRA) { 
        		if(Tags.Get(plugin, elytra.getItemMeta().getPersistentDataContainer(), "combined", PersistentDataType.INTEGER) != null) {
        			if((Integer)Tags.Get(plugin, elytra.getItemMeta().getPersistentDataContainer(), "combined", PersistentDataType.INTEGER) == 1) {
        				return true;
        			} else {
        				player.sendMessage(Lang.Get("separate-fail"));
        				return false;
        			}
        		}
        		else {
        			player.sendMessage(Lang.Get("separate-fail"));
        			return false;
        		}
        	}
        	else { 
    			player.sendMessage(Lang.Get("separate-fail"));
        		return false;
        	}
    	}else return false;
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
    
    String TranslateChestplateName(String name) {
    	return Lang.Translate(name);
    }
    
    
}
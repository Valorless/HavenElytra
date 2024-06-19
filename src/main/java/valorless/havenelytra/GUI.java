package valorless.havenelytra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

import valorless.valorlessutils.utils.Utils;
import valorless.havenelytra.hooks.VaultHook;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.ValorlessUtils.Tags;
import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.json.JsonUtils;
import valorless.valorlessutils.nbt.NBT;
import valorless.valorlessutils.sound.SFX;

enum Menu { main, combine, separate }
enum GUIAction { NULL, CONFIRM, BACK, COMBINE, SEPARATE }
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
		public int customModelData;
		public String tag;
	}

	public GUI(Player player) {
		this.player = player;
		this.config = new Config(plugin, "gui-main.yml");
		this.size = config.GetInt("gui-size");
		inv = Bukkit.createInventory(player, config.GetInt("gui-size"), Lang.Parse(config.GetString("gui-name"), null));
		UpdateGUI(Menu.main);
		SFX.Play(config.GetString("sound"), config.GetFloat("volume").floatValue(), config.GetFloat("pitch").floatValue(), player);
        OpenInventory(player);
        Main.openGUIs.add(this);
    }
	
	public void UpdateGUI(Menu menu) {
		//Log.Info(plugin, menu.toString());
		
		try {
			this.menu = menu;
			if(items != null) items.clear();
			if(inv != null) inv.clear();
			config = new Config(plugin, "gui-" + menu.toString() + ".yml");
    		InitializeLists();
    		inv = Bukkit.createInventory(player, config.GetInt("gui-size"), Lang.Parse(config.GetString("gui-name"), null));
    		player.openInventory(inv);
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
    			inv.setItem(i, CreateGuiItem(Material.getMaterial(items.get(i).item), items.get(i).name, items.get(i).tag, items.get(i).lore, items.get(i).customModelData));
    			g++;
    		} else {
    			inv.setItem(i, CreateGuiItem(Material.getMaterial(filler.toUpperCase()), "§f", null, null, 80000));
    			
    		}
    	}
    	if(g == 0) {
    		Log.Error(plugin, String.format("GUI of 'gui-%s.yml' is empty. Consider using one of the templates!", menu));
    		Log.Error(plugin, "This is not an error with the plugin, you have not set up the GUI like adviced on Spigot.");
    	}
    }
	
	protected ItemStack CreateGuiItem(final Material material, final String name, final String tag, final List<String> lore, int customModelData) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
        	meta.setDisplayName(Lang.Parse(name, null));
        	if(lore != null) {
        		for(String entry : lore) {
        			entry = Lang.Parse(entry, null);
        		}
        		meta.setLore(lore);
        	}
        	
        	meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        	if(customModelData != 0) {
        		meta.setCustomModelData(customModelData);
        	}
        	item.setItemMeta(meta);
        	
        	NBT.SetBool(item, "GUI", true);
        	if(!Utils.IsStringNullOrEmpty(tag)) {
        		NBT.SetString(item, "action", tag);
        	}
        }
        return item;
    }

    public void OpenInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
    	//Log.Info(String.valueOf(e.getRawSlot()));
        if (!e.getInventory().equals(inv)) return;
    	ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;
        
        List<Placeholder> placeholders = new ArrayList<Placeholder>();
        
        if(NBT.Has(clickedItem, "GUI") && !NBT.Has(clickedItem, "action")) {
           	e.setCancelled(true);
           	return;
        }
        
        GUIAction action = GUIAction.NULL;
        try {
        	action = GUIAction.valueOf(NBT.GetString(clickedItem, "action"));
        }catch (Exception E) {
        	return;
        }
        
        
        
        if(menu == Menu.main) {        
            if(action == GUIAction.NULL) return;
            	//Log.Info(plugin, tag);
            	if(action == GUIAction.COMBINE){
            		if(player.hasPermission("havenelytra.combine")) {
            			if(Main.config.GetBool("combine")) { UpdateGUI(Menu.combine); return; }
            			else { 
                			player.sendMessage(Lang.Parse(Lang.Get("combine-disabled"),null));
            			}
            		}else {
            			player.sendMessage(Lang.Parse(Lang.Get("no-permission"),null));
            		}
            	}
            	if(action == GUIAction.SEPARATE) {
            		if(player.hasPermission("havenelytra.separate")) {
            			if(Main.config.GetBool("separate")) { UpdateGUI(Menu.separate); return; }
            			else { 
                			player.sendMessage(Lang.Parse(Lang.Get("separate-disabled"),null));
            			}
            		}else {
            			player.sendMessage(Lang.Parse(Lang.Get("no-permission"),null));
            		}
            	}
            
        }
        
        if(menu == Menu.combine) {
        	if(e.getRawSlot() >= items.size()) { return; }
        	
            
        	List<Integer> slots = new ArrayList<Integer>();
            if(e.getRawSlot() < items.size()) {
            	if(action == GUIAction.BACK) {
                    ReturnItems();
            		UpdateGUI(Menu.main);
            	}
            	
            	if(action == GUIAction.CONFIRM) {
            		e.setCancelled(true);
            		for (int i = 0; i < e.getInventory().getSize(); i++) {
            			if(items.get(i).tag.equalsIgnoreCase("PLAYER") || items.get(i).tag.equalsIgnoreCase("RESULT")) {
            	    		slots.add(i);
            	    		//Log.Warning(plugin, items.get(i).name);
            	    	}
            	    }
            		ItemStack chestplate = e.getInventory().getItem(slots.get(0));
        			ItemStack elytra = e.getInventory().getItem(slots.get(1));
        			if(elytra == null) return;
        			if(Tags.Has(plugin, elytra.getItemMeta().getPersistentDataContainer(), "combined", PersistentDataType.INTEGER)) {
        				player.sendMessage("Please convert this elytra with §e/havenelytra convert§r.");
                		e.setCancelled(true);
                		return;
        			}
        			
            		if(ValidateChestplate(chestplate) && ValidateElytra(elytra)) {
            			
            			if(IDamage.IsItemDamaged(chestplate) || IDamage.IsItemDamaged(elytra)) {
                			player.sendMessage(Lang.Parse(Lang.Get("not-repaired"),null));
            				e.setCancelled(true);
            				return;
            			}
            			
            			if(!Buy(Menu.combine)) {
            				e.setCancelled(true);
            				return;
            			}
            			
            			ItemStack tempItem = new ItemStack(Material.ELYTRA);
            			ItemMeta tempStorage = tempItem.getItemMeta();
            			NBT.SetString(tempItem, "elytra-elytra-meta", JsonUtils.toJson(elytra.getItemMeta()));
            			NBT.SetString(tempItem, "elytra-chestplate-meta", JsonUtils.toJson(chestplate.getItemMeta()));
            			        			            			
            			String chestplateName;
            			if(!Utils.IsStringNullOrEmpty(chestplate.getItemMeta().getDisplayName())) {
            				chestplateName = chestplate.getItemMeta().getDisplayName();
            			} else {
            				chestplateName = Main.translator.Translate(chestplate.getType().getTranslationKey());
            			}
            			String elytraName = elytra.getItemMeta().getDisplayName();
        				List<String> lore = new ArrayList<String>();
            			if(chestplate.getItemMeta().getLore() != null) {
            				for(String str : chestplate.getItemMeta().getLore()) {
            					lore.add(Lang.Parse(str, null));
            				}
            			}
            			if(elytra.getItemMeta().getLore() != null) {
            				lore.add("§r");
            				for(String str : elytra.getItemMeta().getLore()) {
            					lore.add(str);
            				}
            			}
            			lore.add("§r");
            			placeholders.add(new Placeholder("%chestplate%", chestplateName));
            			lore.add(Lang.Parse(Lang.Get("combined-elytra-lore"), placeholders));
            			//lore.add("§7+ [" + chestplateName + "§7]");
            			ItemMeta chestMeta = chestplate.getItemMeta();
            			chestMeta.setDisplayName(elytraName);
            			chestMeta.setLore(lore);
            			
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
            			
            			chestMeta.setCustomModelData(null);
            			if(elytra.getItemMeta().hasCustomModelData()) {
            				chestMeta.setCustomModelData(elytra.getItemMeta().getCustomModelData());
            			}
            			
            			SetCustomModelData(elytra, chestplate.getType(), chestMeta);
            			
            			chestplate.setItemMeta(chestMeta);
            			
            			NBT.SetInt(chestplate, "elytra-combined", 1);
            			NBT.SetString(chestplate, "elytra-chestplate-type", chestplate.getType().toString());
            			NBT.SetString(chestplate, "elytra-chestplate-name", chestplateName);
            			
            			NBT.SetString(chestplate, "elytra-elytra-meta", NBT.GetString(tempItem, "elytra-elytra-meta"));
            			NBT.SetString(chestplate, "elytra-chestplate-meta", NBT.GetString(tempItem, "elytra-chestplate-meta"));

            			//FixEnchants(elytra, chestplate);
            			
            			chestplate.setType(Material.ELYTRA);
            			
            			e.getInventory().clear(slots.get(0));
            			e.getInventory().clear(slots.get(1));
            			e.getInventory().setItem(slots.get(2), chestplate);
            			player.sendMessage(Lang.Parse(Lang.Get("combine-success"),null));
            			SFX.Play(config.GetString("sound"), config.GetFloat("volume").floatValue(), config.GetFloat("pitch").floatValue(), player);
            		}
            	}
            }
        }
        
        if(menu == Menu.separate) {            
        	List<Integer> slots = new ArrayList<Integer>();
            if(e.getRawSlot() < items.size()) {
            	if(action == GUIAction.BACK) {
                    ReturnItems();
            		UpdateGUI(Menu.main);
            	}
            	
            	if(action == GUIAction.CONFIRM) {
            		e.setCancelled(true);
            		for (int i = 0; i < e.getInventory().getSize(); i++) {
            			if(items.get(i).tag.equalsIgnoreCase("PLAYER") || items.get(i).tag.equalsIgnoreCase("RESULT")) {
            	    		slots.add(i);
            	    	}
            	    }
        			ItemStack elytra = e.getInventory().getItem(slots.get(0));
        			if(elytra == null) return;
        			if(Tags.Has(plugin, elytra.getItemMeta().getPersistentDataContainer(), "combined", PersistentDataType.INTEGER)) {
        				player.sendMessage("Please convert this elytra with §e/havenelytra convert§r.");
                		e.setCancelled(true);
                		return;
        			}
        			
        			/*if(IDamage.IsItemDamaged(elytra)) {
            			player.sendMessage(Lang.Parse(Lang.Get("not-repaired"),null));
        				e.setCancelled(true);
        				return;
        			}*/
        			
        			if(!Buy(Menu.separate)) {
        				e.setCancelled(true);
        				return;
        			}
        			
            		if(ValidateElytra(elytra)) {
            			Material chestplateType = Material.getMaterial(NBT.GetString(elytra, "elytra-chestplate-type"));
            			ItemStack chestplate = new ItemStack(chestplateType);
            			ItemStack replacementElytra = new ItemStack(Material.ELYTRA);
            			Map <Enchantment, Integer> enchants = elytra.getItemMeta().getEnchants();
            			replacementElytra.setItemMeta((ItemMeta)JsonUtils.fromJson(NBT.GetString(elytra, "elytra-elytra-meta")));
            			chestplate.setItemMeta((ItemMeta)JsonUtils.fromJson(NBT.GetString(elytra, "elytra-chestplate-meta")));
            			if(Main.config.GetBool("item-damage.enabled")) {
        					int damage = replacementElytra.getDurability() - elytra.getDurability();
            				if(Main.config.GetBool("item-damage.damage-chestplate")) {
            					chestplate.setDurability((short) (chestplate.getDurability() - damage));
            				}
            				if(Main.config.GetBool("item-damage.damage-elytra")) {
            					replacementElytra.setDurability(elytra.getDurability());
            				}
            			}
            			ItemMeta chestMeta = chestplate.getItemMeta();
            			for(Map.Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            				chestMeta.addEnchant(enchant.getKey(), enchant.getValue(), true);
            			}
            			chestplate.setItemMeta(chestMeta); //Reapply again to add enchants.
            			e.getInventory().clear(slots.get(0));
            			e.getInventory().setItem(slots.get(1), replacementElytra);
            			e.getInventory().setItem(slots.get(2), chestplate);
            			player.sendMessage(Lang.Parse(Lang.Get("separate-success"),null));
            			SFX.Play(config.GetString("sound"), config.GetFloat("volume").floatValue(), config.GetFloat("pitch").floatValue(), player);
            		}
            	}
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (!e.getInventory().equals(inv)) return;
        Close();
    }
    
    public void Close() {
    	if(Main.openGUIs.contains(this)) {
    		ReturnItems();
        	Main.openGUIs.remove(this);
        	player.closeInventory();
    	}
    }
    
    void ReturnItems() {
    	for (int i = 0; i < inv.getSize(); i++) {
        	if(inv.getItem(i) != null) {
        		if(items.get(i).tag.toString().equalsIgnoreCase("PLAYER") || items.get(i).tag.toString().equalsIgnoreCase("RESULT")) {
        			if(player.getInventory().firstEmpty() != -1) {
        				player.getInventory().addItem(inv.getItem(i));
        	    	} else {
        	    		player.getWorld().dropItem(player.getLocation(), inv.getItem(i));
        	    	}
        			
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
    		player.sendMessage(Lang.Parse(Lang.Get("material-disabled"),null));
    		return false;
    	}else if(chestplate.getType() == Material.IRON_CHESTPLATE && config.GetBool("iron") == false) {
    		player.sendMessage(Lang.Parse(Lang.Get("material-disabled"),null));
    		return false;
    	}else if(chestplate.getType() == Material.GOLDEN_CHESTPLATE && config.GetBool("gold") == false) {
    		player.sendMessage(Lang.Parse(Lang.Get("material-disabled"),null));
    		return false;
    	}else if(chestplate.getType() == Material.CHAINMAIL_CHESTPLATE && config.GetBool("chainmail") == false) {
    		player.sendMessage(Lang.Parse(Lang.Get("material-disabled"),null));
    		return false;
    	}else if(chestplate.getType() == Material.DIAMOND_CHESTPLATE && config.GetBool("diamond") == false) {
    		player.sendMessage(Lang.Parse(Lang.Get("material-disabled"),null));
    		return false;
    	}else if(chestplate.getType() == Material.NETHERITE_CHESTPLATE && config.GetBool("netherite") == false) {
    		player.sendMessage(Lang.Parse(Lang.Get("material-disabled"),null));
    		return false;
    	}else {
    		return true;
    	}
    }
    
    boolean ValidateElytra(ItemStack elytra) {
    	if(elytra == null) return false;
    	if(menu == Menu.combine) {
    		if(elytra.getType() == Material.ELYTRA) { 
    			if(NBT.Has(elytra, "elytra-combined")) {
            		player.sendMessage(Lang.Parse(Lang.Get("combine-fail"),null));
    				return false;
    			}else {
        			return true;
    			}
    		}
    		else { 
    			player.sendMessage(Lang.Parse(Lang.Get("combine-fail"),null));
    			return false;
    		}
    	}else if(menu == Menu.separate) {
    		if(elytra.getType() == Material.ELYTRA) { 
    			if(NBT.Has(elytra, "elytra-combined")) {
    				return true;
    			}else {
        			player.sendMessage(Lang.Parse(Lang.Get("separate-fail"),null));
        			return false;
    			}
        	}
        	else { 
    			player.sendMessage(Lang.Parse(Lang.Get("separate-fail"),null));
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
    
    public boolean Buy(Menu menu) {
        Double cost = Main.config.GetFloat(menu.name() + "-cost");
        if(cost == 0 || cost == 0.0) return true; //Skip
        
        if(VaultHook.getEconomy() == null) {
    		Log.Error(plugin, "Something went wrong while attempting to use Vault.");
        	Log.Error(plugin, "is it up to date?");
        	return true;
        }
        
        double balance = VaultHook.getEconomy().getBalance(player);
        
        if (balance >= cost) {
        	try {
        		VaultHook.getEconomy().withdrawPlayer(player, cost);
        		player.sendMessage(Lang.Parse(Lang.Get("can-afford").replace("%cost%", cost.toString()),null));
            	return true;
        	} catch (Exception e) {
        		player.sendMessage("§cSomething went wrong..");
        		Log.Error(plugin, "Something went wrong while attempting to use Vault.");
        		return false;
        	}
        }else {
			player.sendMessage(Lang.Parse(Lang.Get("cannot-afford"),null));
        	return false;
        }
    }
    
    public void FixEnchants(ItemStack elytra, ItemStack chestplate) {
    	ItemMeta chestMeta = chestplate.getItemMeta();
    	if(elytra.getItemMeta().getEnchantLevel(Enchantment.DURABILITY) > chestMeta.getEnchantLevel(Enchantment.DURABILITY)) {
			chestMeta.addEnchant(Enchantment.DURABILITY, elytra.getItemMeta().getEnchantLevel(Enchantment.DURABILITY), true);
		}
		if(elytra.getItemMeta().getEnchantLevel(Enchantment.MENDING) > chestMeta.getEnchantLevel(Enchantment.MENDING)) {
			chestMeta.addEnchant(Enchantment.MENDING, elytra.getItemMeta().getEnchantLevel(Enchantment.MENDING), true);
		}
		chestplate.setItemMeta(chestMeta);
    }
    
    void SetCustomModelData(ItemStack elytra, Material type, ItemMeta chestMeta) {
    	if(!Main.config.GetBool("custommodeldata.enabled")) return;
    	
    	if(!Main.config.GetBool("custommodeldata.per-material.enabled")) {
    		chestMeta.setCustomModelData(Main.config.GetInt("custommodeldata.combined"));
    	} else {
    		switch(type) {
    			case LEATHER_CHESTPLATE:
    	    		chestMeta.setCustomModelData(Main.config.GetInt("custommodeldata.per-material.leather"));
    				break;
    			case IRON_CHESTPLATE:
    	    		chestMeta.setCustomModelData(Main.config.GetInt("custommodeldata.per-material.iron"));
    				break;
    			case GOLDEN_CHESTPLATE:
    	    		chestMeta.setCustomModelData(Main.config.GetInt("custommodeldata.per-material.gold"));
    				break;
    			case CHAINMAIL_CHESTPLATE:
    	    		chestMeta.setCustomModelData(Main.config.GetInt("custommodeldata.per-material.chainmail"));
    				break;
    			case DIAMOND_CHESTPLATE:
    	    		chestMeta.setCustomModelData(Main.config.GetInt("custommodeldata.per-material.diamond"));
    				break;
    			case NETHERITE_CHESTPLATE:
    	    		chestMeta.setCustomModelData(Main.config.GetInt("custommodeldata.per-material.netherite"));
    				break;
    				
    		}
    	}
    	
    	
    }
    
}
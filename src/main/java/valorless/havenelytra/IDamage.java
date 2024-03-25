package valorless.havenelytra;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class IDamage {

	public static boolean IsItemDamaged(ItemStack item) {
		Damageable dmg = (Damageable) item.getItemMeta();
		return dmg.hasDamage();
	}
}

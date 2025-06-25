package femboys.are.cute.reportsystem.util;

import femboys.are.cute.reportsystem.ReportSystem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;


public final class GuiUtils {

	private GuiUtils() {
		throw new AssertionError("Utility class should not be instantiated");
	}

	public static String getInfo() {
		String info = ReportSystem.getInstance().getLang().getString("PluginInfo",
				"&5=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=&r\n" +
						"&aPlugin: CityReports&r\n" +
						"&6Author: FemBoysAreCut3&r\n" +
						"&9Website: https://citypvp.de/home.html&r\n" +
						"&5=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=&r");
		return ChatColor.translateAlternateColorCodes('&', info);
	}

	public static ItemStack getFillerItem() {
		ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta meta = Objects.requireNonNull(filler.getItemMeta());
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiFillerName", "&0 ")));
		filler.setItemMeta(meta);
		return filler;
	}
}
package femboys.are.cute.reportsystem.guis;

import femboys.are.cute.reportsystem.ReportSystem;
import femboys.are.cute.reportsystem.util.GuiUtils;
import femboys.are.cute.reportsystem.util.Report;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;


public final class GuiReports {

	private static final int INVENTORY_SIZE = 9 * 6;
	private static final int REPORTS_PER_PAGE = 9 * 4;

	private GuiReports() {
		throw new AssertionError("Utility class should not be instantiated");
	}


	public static void show(Player player, int page) {
		if (player == null || page < 0) {
			return;
		}

		String title = ReportSystem.getInstance().getLang().getString("GuiReportsTitle", "&4Reports &8/ &2") + (page + 1);
		Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, ChatColor.translateAlternateColorCodes('&', title));

		// Set filler items
		for (int i = 0; i < 8; i++) {
			inventory.setItem(i, GuiUtils.getFillerItem());
		}
		for (int i = 0; i < 7; i++) {
			inventory.setItem((9 * 5) + i, GuiUtils.getFillerItem());
		}

		// Close button
		ItemStack close = new ItemStack(Material.BARRIER);
		ItemMeta closeMeta = Objects.requireNonNull(close.getItemMeta());
		closeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiClose", "&4Close")));
		close.setItemMeta(closeMeta);
		inventory.setItem(8, close);

		// Previous page button
		ItemStack prevPage = new ItemStack(Material.ARROW);
		ItemMeta prevMeta = Objects.requireNonNull(prevPage.getItemMeta());
		prevMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiPrevPage", "&bPrevious Page")));
		prevPage.setItemMeta(prevMeta);
		inventory.setItem((9 * 5) + 7, prevPage);

		// Next page button
		ItemStack nextPage = new ItemStack(Material.SPECTRAL_ARROW);
		ItemMeta nextMeta = Objects.requireNonNull(nextPage.getItemMeta());
		nextMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiNextPage", "&bNext Page")));
		nextPage.setItemMeta(nextMeta);
		inventory.setItem((9 * 5) + 8, nextPage);

		// Load and display reports
		File reportsDir = new File(ReportSystem.getInstance().getConfig().getString("ReportsPath", "reports/"));
		File[] reportFiles = reportsDir.listFiles((dir, name) -> name.endsWith(".json"));
		if (reportFiles != null) {
			int startIndex = page * REPORTS_PER_PAGE;
			int endIndex = Math.min(startIndex + REPORTS_PER_PAGE, reportFiles.length);

			for (int i = startIndex; i < endIndex; i++) {
				try (FileReader reader = new FileReader(reportFiles[i])) {
					Report report = Report.deserialize(ReportSystem.getInstance().getConfig().getConfigurationSection(
							reportFiles[i].getName().replace(".json", "")).getValues(true));
					ItemStack item = createReportItem(report, i);
					inventory.addItem(item);
				} catch (IOException | IllegalArgumentException e) {
					ReportSystem.getInstance().getLogger().log(Level.WARNING, "Failed to load report file: " + reportFiles[i].getName(), e);
				}
			}
		}

		player.openInventory(inventory);
	}

	private static ItemStack createReportItem(Report report, int index) {
		Material material = switch (report.getState()) {
			case FREE -> Material.REDSTONE_BLOCK;
			case CLAIMED -> Material.GOLD_BLOCK;
			case COMPLETED -> Material.EMERALD_BLOCK;
		};

		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiPropState", "&5State: ") + report.getState().getDisplayName()));
		lore.add(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiPropCreator", "&6Creator: ") +
						Objects.requireNonNull(Bukkit.getServer().getOfflinePlayer(report.getCreator()).getName())));
		lore.add(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiPropDate", "&bDate: ") +
						report.getFormattedTimestamp().split("_")[0]));
		lore.add(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiPropTime", "&2Time: ") +
						report.getFormattedTimestamp().split("_")[1]));
		lore.add(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiPropReason", "&9Reason: ") + report.getReason()));
		lore.add(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiPropFile", "&cFile: ") +
						report.getFormattedTimestamp() + ".json"));

		ItemStack item = new ItemStack(material);
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				"&a" + Objects.requireNonNull(Bukkit.getServer().getOfflinePlayer(report.getReportedPlayer()).getName())));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
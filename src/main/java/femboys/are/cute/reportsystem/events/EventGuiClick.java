package femboys.are.cute.reportsystem.events;

import femboys.are.cute.reportsystem.ReportSystem;
import femboys.are.cute.reportsystem.guis.GuiManageReport;
import femboys.are.cute.reportsystem.guis.GuiReports;
import femboys.are.cute.reportsystem.util.Report;
import femboys.are.cute.reportsystem.util.ReportState;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public class EventGuiClick implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player) || event.getCurrentItem() == null) {
			event.setCancelled(true);
			return;
		}

		String title = event.getView().getTitle().trim();
		String prefix = ReportSystem.getInstance().getLang().getString("Prefix", "&8[&4&lReportSystem&r&8] &r");

		if (title.startsWith(ReportSystem.getInstance().getLang().getString("GuiReportsTitle", "&4Reports &8/ &2"))) {
			if (!player.hasPermission(ReportSystem.getInstance().getConfig().getString("ReportsPermission", "reportsystem.reports"))) {
				player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ReportsGUIPermission", "&cYou need the Permission \"reportsystem.reports\" to use this GUI!"));
				player.closeInventory();
				return;
			}
			handleGuiReports(event, player, prefix);
		} else if (title.startsWith(ReportSystem.getInstance().getLang().getString("GuiManageReportTitle", "&2Report: &c"))) {
			if (!player.hasPermission(ReportSystem.getInstance().getConfig().getString("ManagePermission", "reportsystem.report.manage"))) {
				player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ManagePermission", "&cYou need the Permission \"reportsystem.report.manage\" to free Reports!"));
				player.closeInventory();
				return;
			}
			handleGuiManageReport(event, player, prefix);
		}
		event.setCancelled(true);
	}

	private void handleGuiReports(InventoryClickEvent event, Player player, String prefix) {
		ItemMeta meta = event.getCurrentItem().getItemMeta();
		if (meta == null || !meta.hasDisplayName()) {
			return;
		}

		String itemName = meta.getDisplayName().trim();
		String reportsPath = ReportSystem.getInstance().getConfig().getString("ReportsPath", "reports/");

		if (itemName.startsWith(ChatColor.stripColor(ReportSystem.getInstance().getLang().getString("GuiClose", "&4Close")))) {
			player.closeInventory();
		} else if (itemName.startsWith(ChatColor.stripColor(ReportSystem.getInstance().getLang().getString("GuiPrevPage", "&bPrevious Page")))) {
			int page = Integer.parseInt(event.getView().getTitle().replace(
					ReportSystem.getInstance().getLang().getString("GuiReportsTitle", "&4Reports &8/ &2"), "").trim()) - 1;
			if (page >= 0) {
				player.closeInventory();
				GuiReports.show(player, page);
			}
		} else if (itemName.startsWith(ChatColor.stripColor(ReportSystem.getInstance().getLang().getString("GuiNextPage", "&bNext Page")))) {
			int page = Integer.parseInt(event.getView().getTitle().replace(
					ReportSystem.getInstance().getLang().getString("GuiReportsTitle", "&4Reports &8/ &2"), "").trim());
			player.closeInventory();
			GuiReports.show(player, page + 1);
		} else if (Bukkit.getServer().getPlayer(ChatColor.stripColor(itemName)) != null) {
			if (!player.hasPermission(ReportSystem.getInstance().getConfig().getString("ManagePermission", "reportsystem.report.manage"))) {
				player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ManagePermission", "&cYou need the Permission \"reportsystem.report.manage\" to free Reports!"));
				player.closeInventory();
				return;
			}

			for (String lore : meta.getLore()) {
				if (lore.contains(ReportSystem.getInstance().getLang().getString("GuiPropFile", "&cFile: "))) {
					String fileName = lore.split(" ")[lore.split(" ").length - 1].replace(".json", "");
					try (FileReader reader = new FileReader(new File(reportsPath + fileName + ".json"))) {
						Report report = Report.deserialize(ReportSystem.getInstance().getConfig().getConfigurationSection(fileName).getValues(true));
						player.closeInventory();
						GuiManageReport.show(player, report);
					} catch (IOException | IllegalArgumentException ex) {
						ReportSystem.getInstance().getLogger().log(Level.WARNING, "Failed to load report: " + fileName, ex);
						player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&4Error getting Report(Check Console)!"));
					}
					break;
				}
			}
		}
	}

	private void handleGuiManageReport(InventoryClickEvent event, Player player, String prefix) {
		ItemMeta meta = event.getCurrentItem().getItemMeta();
		if (meta == null || !meta.hasDisplayName()) {
			return;
		}

		String itemName = meta.getDisplayName().trim();
		String fileName = event.getView().getTitle().trim().replace(
				ReportSystem.getInstance().getLang().getString("GuiManageReportTitle", "&2Report: &c"), "");
		String reportsPath = ReportSystem.getInstance().getConfig().getString("ReportsPath", "reports/");
		Report report = null;

		try (FileReader reader = new FileReader(new File(reportsPath + fileName + ".json"))) {
			report = Report.deserialize(ReportSystem.getInstance().getConfig().getConfigurationSection(fileName).getValues(true));
		} catch (IOException | IllegalArgumentException ex) {
			ReportSystem.getInstance().getLogger().log(Level.WARNING, "Failed to load report: " + fileName, ex);
		}

		if (report == null) {
			player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&4Error loading Report(Check Console)!"));
			player.closeInventory();
			return;
		}

		if (itemName.startsWith(ChatColor.stripColor(ReportSystem.getInstance().getLang().getString("GuiClose", "&4Close")))) {
			player.closeInventory();
		} else if (itemName.startsWith(ChatColor.stripColor(ReportSystem.getInstance().getLang().getString("GuiDelete", "&4Delete")))) {
			handleDeleteReport(player, report, prefix, fileName);
		} else if (itemName.startsWith(ChatColor.stripColor(ReportSystem.getInstance().getLang().getString("GuiFree", "&4Free")))) {
			handleStateChange(player, report, ReportState.FREE, prefix, fileName, "FreePermission", "ReportFreed");
		} else if (itemName.startsWith(ChatColor.stripColor(ReportSystem.getInstance().getLang().getString("GuiClaimed", "&6Claimed")))) {
			handleStateChange(player, report, ReportState.CLAIMED, prefix, fileName, "ClaimPermission", "ReportClaimed");
		} else if (itemName.startsWith(ChatColor.stripColor(ReportSystem.getInstance().getLang().getString("GuiCompleted", "&aCompleted")))) {
			handleStateChange(player, report, ReportState.COMPLETED, prefix, fileName, "CompletePermission", "ReportCompleted");
		} else if (itemName.startsWith(ChatColor.stripColor(ReportSystem.getInstance().getLang().getString("GuiBan", "&cBAN")))) {
			handleBanPlayer(player, report, prefix, fileName);
		}
	}

	private void handleDeleteReport(Player player, Report report, String prefix, String fileName) {
		String permission = ReportSystem.getInstance().getConfig().getString("DeletePermission", "reportsystem.report.delete");
		if (!player.hasPermission(permission)) {
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("DeletePermission",
					"&cYou need the Permission \"reportsystem.report.delete\" to delete Reports!"));
			player.closeInventory();
			return;
		}

		File file = new File(ReportSystem.getInstance().getConfig().getString("ReportsPath", "reports/") + fileName + ".json");
		if (file.delete()) {
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ReportDeleted", "&4Report Deleted!"));
		} else {
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ExceptionDelete", "&4Report Deletion Failed!"));
			ReportSystem.getInstance().getLogger().warning("Failed to delete report file: " + fileName);
		}
		player.closeInventory();
	}

	private void handleStateChange(Player player, Report report, ReportState state, String prefix, String fileName,
								   String permissionKey, String successMessageKey) {
		String permission = ReportSystem.getInstance().getConfig().getString(permissionKey, "reportsystem.report." + permissionKey.toLowerCase());
		if (!player.hasPermission(permission)) {
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString(permissionKey,
					"&cYou need the Permission \"" + permission + "\" to perform this action!"));
			player.closeInventory();
			return;
		}

		try {
			report.setState(state);
			File file = new File(ReportSystem.getInstance().getConfig().getString("ReportsPath", "reports/") + fileName + ".json");
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				writer.write(ReportSystem.getInstance().getConfig().createSection(fileName, report.serialize()).toString());
				player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString(successMessageKey,
						"&a" + successMessageKey.replace("Permission", "")));
			}
		} catch (IllegalStateException | IOException ex) {
			ReportSystem.getInstance().getLogger().log(Level.WARNING, "Failed to update report state: " + fileName, ex);
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ExceptionSetState",
					"&4Error changing Report state(Check Console)!"));
		}
		player.closeInventory();
	}

	private void handleBanPlayer(Player player, Report report, String prefix, String fileName) {
		String permission = ReportSystem.getInstance().getConfig().getString("BanPermission", "reportsystem.report.ban");
		if (!player.hasPermission(permission)) {
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("BanPermission",
					"&cYou need the Permission \"reportsystem.report.ban\" to ban Players!"));
			player.closeInventory();
			return;
		}

		String banMessage = ReportSystem.getInstance().getLang().getString("BanMessage",
						"\n\n%PREFIX%\n&cYou have been banned!\n&cReason: %REASON%\n&5Banned by: &6%BANBY% &8(&6%BANBYUUID%&8)\n&aReport ID: %REPORTID%\n&0")
				.replace("%PREFIX%", prefix)
				.replace("%REASON%", report.getReason())
				.replace("%BANBY%", player.getName())
				.replace("%BANBYUUID%", player.getUniqueId().toString())
				.replace("%REPORTID%", fileName);

		Bukkit.getBanList(BanList.Type.NAME).addBan(
				Bukkit.getOfflinePlayer(report.getReportedPlayer()).getName(),
				banMessage,
				null, // Permanent ban
				player.getName()
		);
		player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("PlayerBanned", "&cPlayer Banned!"));

		if (ReportSystem.getInstance().getConfig().getBoolean("CompleteReportOnBan", true)) {
			try {
				report.setState(ReportState.COMPLETED);
				File file = new File(ReportSystem.getInstance().getConfig().getString("ReportsPath", "reports/") + fileName + ".json");
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
					writer.write(ReportSystem.getInstance().getConfig().createSection(fileName, report.serialize()).toString());
				}
			} catch (IllegalStateException | IOException ex) {
				ReportSystem.getInstance().getLogger().log(Level.WARNING, "Failed to update report state on ban: " + fileName, ex);
				player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ExceptionSetState",
						"&4Error changing Report state(Check Console)!"));
			}
		}
		player.closeInventory();
	}
}
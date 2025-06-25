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
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GuiManageReport {

	private static final int INVENTORY_SIZE = 9 * 3;

	private GuiManageReport() {
		throw new AssertionError("Utility class should not be instantiated");
	}

	public static void show(Player player, Report report) {
		if (player == null || report == null) {
			return;
		}

		String title = ReportSystem.getInstance().getLang().getString("GuiManageReportTitle", "&2Report: &c") +
				report.getFormattedTimestamp();
		Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, ChatColor.translateAlternateColorCodes('&', title));

		for (int i = 0; i < 7; i++) {
			inventory.setItem(i, GuiUtils.getFillerItem());
		}
		for (int i = 0; i < 9; i++) {
			inventory.setItem((9 * 2) + i, GuiUtils.getFillerItem());
		}

		ItemStack close = new ItemStack(Material.BARRIER);
		ItemMeta closeMeta = Objects.requireNonNull(close.getItemMeta());
		closeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiClose", "&4Close")));
		close.setItemMeta(closeMeta);
		inventory.setItem(8, close);

		ItemStack delete = new ItemStack(Material.COMPOSTER);
		ItemMeta deleteMeta = Objects.requireNonNull(delete.getItemMeta());
		deleteMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiDelete", "&4Delete")));
		delete.setItemMeta(deleteMeta);
		inventory.setItem(7, delete);

		ItemStack freeState = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta freeMeta = Objects.requireNonNull(freeState.getItemMeta());
		freeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiFree", "&4Free")));
		freeState.setItemMeta(freeMeta);
		inventory.setItem(10, freeState);

		ItemStack claimState = new ItemStack(Material.GOLD_BLOCK);
		ItemMeta claimMeta = Objects.requireNonNull(claimState.getItemMeta());
		claimMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiClaimed", "&6Claimed")));
		claimState.setItemMeta(claimMeta);
		inventory.setItem(11, claimState);

		ItemStack completeState = new ItemStack(Material.EMERALD_BLOCK);
		ItemMeta completeMeta = Objects.requireNonNull(completeState.getItemMeta());
		completeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiCompleted", "&aCompleted")));
		completeState.setItemMeta(completeMeta);
		inventory.setItem(12, completeState);

		ItemStack ban = new ItemStack(Material.ANVIL);
		ItemMeta banMeta = Objects.requireNonNull(ban.getItemMeta());
		banMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiBan", "&cBAN")));
		ban.setItemMeta(banMeta);
		inventory.setItem(14, ban);

		inventory.setItem(9, GuiUtils.getFillerItem());
		inventory.setItem(13, GuiUtils.getFillerItem());
		inventory.setItem(15, GuiUtils.getFillerItem());
		inventory.setItem(17, GuiUtils.getFillerItem());

		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&',
				ReportSystem.getInstance().getLang().getString("GuiPropState", "&5State: ") +
						report.getState().getDisplayName()));
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

		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta headMeta = (SkullMeta) Objects.requireNonNull(head.getItemMeta());
		headMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				"&6" + Objects.requireNonNull(Bukkit.getServer().getOfflinePlayer(report.getReportedPlayer()).getName())));
		headMeta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(report.getReportedPlayer()));
		headMeta.setLore(lore);
		head.setItemMeta(headMeta);
		inventory.setItem(16, head);

		player.openInventory(inventory);
	}
}
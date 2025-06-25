package femboys.are.cute.reportsystem.commands;

import femboys.are.cute.reportsystem.ReportSystem;
import femboys.are.cute.reportsystem.guis.GuiReports;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReports implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("This command can only be executed by a player.");
			return false;
		}

		String prefix = ReportSystem.getInstance().getLang().getString("Prefix", "&8[&4&lReportSystem&r&8] &r");
		String permission = ReportSystem.getInstance().getConfig().getString("ReportsPermission", "reportsystem.reports");

		if (!player.hasPermission(permission)) {
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ReportsPermission",
					"&cYou need the Permission \"reportsystem.reports\" to use this Command!"));
			return false;
		}

		GuiReports.show(player, 0);
		return true;
	}
}
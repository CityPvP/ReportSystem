package femboys.are.cute.reportsystem.commands;

import femboys.are.cute.reportsystem.ReportSystem;
import femboys.are.cute.reportsystem.util.GuiUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandReportSystem implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("This command can only be executed by a player.");
			return false;
		}

		String prefix = ReportSystem.getInstance().getLang().getString("Prefix", "&8[&4&lReportSystem&r&8] &r");

		if (args.length == 1) {
			switch (args[0].toLowerCase()) {
				case "info":
					if (!player.hasPermission(ReportSystem.getInstance().getConfig().getString("InfoPermission", "reportsystem.info"))) {
						player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("InfoPermission",
								"&cYou need the Permission \"reportsystem.info\" to use this Command!"));
						return false;
					}
					player.sendMessage(GuiUtils.getInfo());
					break;

				case "help":
					if (!player.hasPermission(ReportSystem.getInstance().getConfig().getString("HelpPermission", "reportsystem.help"))) {
						player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("HelpPermission",
								"&cYou need the Permission \"reportsystem.help\" to use this Command!"));
						return false;
					}
					player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("HelpMessage",
							"&6Usage:\n&2/report [PLAYER] [REASON] -- Report a Player\n/reports -- Shows all Reports\n" +
									"/reportsystem -- Shows Plugin Info\n/reportsystem info -- Shows Plugin Info\n" +
									"/reportsystem help -- Shows Help Message\n/reportsystem reload -- Reloads the Plugin"));
					break;

				case "reload":
					if (!player.hasPermission(ReportSystem.getInstance().getConfig().getString("ReloadPermission", "reportsystem.reload"))) {
						player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ReloadPermission",
								"&cYou need the Permission \"reportsystem.reload\" to use this Command!"));
						return false;
					}
					ReportSystem.getInstance().reloadConfig();
					ReportSystem.getInstance().getLang().options().copyDefaults(true);
					player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ReloadMessage", "&aConfigs reloaded!"));
					break;

				default:
					player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&cUnknown subcommand. Use /reportsystem help for usage."));
					return false;
			}
		} else {
			if (!player.hasPermission(ReportSystem.getInstance().getConfig().getString("InfoPermission", "reportsystem.info"))) {
				player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("InfoPermission",
						"&cYou need the Permission \"reportsystem.info\" to use this Command!"));
				return false;
			}
			player.sendMessage(GuiUtils.getInfo());
		}
		return true;
	}
}
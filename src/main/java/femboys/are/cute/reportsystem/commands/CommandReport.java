package femboys.are.cute.reportsystem.commands;

import femboys.are.cute.reportsystem.ReportSystem;
import femboys.are.cute.reportsystem.util.Report;
import femboys.are.cute.reportsystem.util.ReportState;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;


public class CommandReport implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("This command can only be executed by a player.");
			return false;
		}

		String prefix = ReportSystem.getInstance().getLang().getString("Prefix", "&8[&4&lReportSystem&r&8] &r");
		String permission = ReportSystem.getInstance().getConfig().getString("ReportPermission", "reportsystem.report");

		if (!player.hasPermission(permission)) {
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ReportPermission",
					"&cYou need the Permission \"reportsystem.report\" to use this Command!"));
			return false;
		}

		if (args.length < 2) {
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("InvalidUsage",
					"&4Invalid Usage!\nUse /report [PLAYER] [REASON]"));
			return false;
		}

		Player target = Bukkit.getServer().getPlayer(args[0]);
		if (target == null) {
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("InvalidPlayer",
					"&4Invalid Player!"));
			return false;
		}

		String reason = String.join(" ", args).substring(args[0].length()).trim();
		if (reason.isEmpty()) {
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("InvalidUsage",
					"&4Invalid Usage!\nUse /report [PLAYER] [REASON]"));
			return false;
		}

		try {
			Report report = new Report(
					target.getUniqueId(),
					player.getUniqueId(),
					reason,
					LocalDateTime.now(),
					ReportState.FREE
			);
			File reportFile = new File(ReportSystem.getInstance().getConfig().getString("ReportsPath", "reports/") +
					report.getFormattedTimestamp() + ".json");
			if (!reportFile.getParentFile().exists()) {
				reportFile.getParentFile().mkdirs();
			}
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
				writer.write(ReportSystem.getInstance().getConfig().createSection(
						report.getFormattedTimestamp(), report.serialize()).toString());
			}
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("Reported",
					"&aPlayer reported!"));
		} catch (IllegalArgumentException | IOException ex) {
			ReportSystem.getInstance().getLogger().log(Level.WARNING, "Failed to create report for player: " + args[0], ex);
			player.sendMessage(prefix + ReportSystem.getInstance().getLang().getString("ReportError",
					"&4Error reporting Player!\nPlease report this issue to an Admin!"));
		}

		return true;
	}
}
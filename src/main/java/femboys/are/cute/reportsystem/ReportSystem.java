package femboys.are.cute.reportsystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import femboys.are.cute.reportsystem.commands.CommandReport;
import femboys.are.cute.reportsystem.commands.CommandReports;
import femboys.are.cute.reportsystem.commands.CommandReportSystem;
import femboys.are.cute.reportsystem.events.EventGuiClick;
import femboys.are.cute.reportsystem.util.GuiUtils;

public class ReportSystem extends JavaPlugin {
	private static ReportSystem instance;
	private FileConfiguration config;
	private FileConfiguration lang;
	private Path configPath;
	private Path langPath;
	private Path reportsPath;
	public static SimpleDateFormat TimestampFormat;

	public static ReportSystem getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		setupDirectories();
		loadConfig();
		loadLang();
		registerCommandsAndEvents();
		getLogger().info("ReportSystem enabled successfully.");
		getServer().getConsoleSender().sendMessage(GuiUtils.getInfo());
	}

	private void setupDirectories() {
		try {
			configPath = getDataFolder().toPath().resolve("config.yml");
			langPath = getDataFolder().toPath().resolve("lang.yml");
			reportsPath = getDataFolder().toPath().resolve("reports");
			Files.createDirectories(getDataFolder().toPath());
			Files.createDirectories(reportsPath);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Failed to create plugin directories", e);
		}
	}

	private void loadConfig() {
		config = YamlConfiguration.loadConfiguration(configPath.toFile());
		if (!configPath.toFile().exists()) {
			config.set("ReportsPath", "reports/");
			config.set("LangPath", "lang.yml");
			config.set("ReportPermission", "reportsystem.report");
			config.set("ReportsPermission", "reportsystem.reports");
			config.set("InfoPermission", "reportsystem.info");
			config.set("HelpPermission", "reportsystem.help");
			config.set("ReloadPermission", "reportsystem.reload");
			config.set("FreePermission", "reportsystem.report.free");
			config.set("ClaimPermission", "reportsystem.report.claim");
			config.set("CompletePermission", "reportsystem.report.complete");
			config.set("BanPermission", "reportsystem.report.ban");
			config.set("DeletePermission", "reportsystem.report.delete");
			config.set("ManagePermission", "reportsystem.report.manage");
			config.set("CompleteReportOnBan", true);
			config.set("TimestampFormat", "yyyy-MM-dd_HH-mm-ss");
			saveConfigFile();
		}
		TimestampFormat = new SimpleDateFormat(config.getString("TimestampFormat", "yyyy-MM-dd_HH-mm-ss"));
	}

	private void loadLang() {
		lang = YamlConfiguration.loadConfiguration(langPath.toFile());
		if (!langPath.toFile().exists()) {
			lang.set("Prefix", "&8[&4&lReportSystem&r&8] &r");
			lang.set("ReportPermission", "&cYou need the Permission \"reportsystem.report\" to use this Command!");
			lang.set("ReportsPermission", "&cYou need the Permission \"reportsystem.reports\" to use this Command!");
			lang.set("ReportsGUIPermission", "&cYou need the Permission \"reportsystem.reports\" to use this GUI!");
			lang.set("InfoPermission", "&cYou need the Permission \"reportsystem.info\" to use this Command!");
			lang.set("HelpPermission", "&cYou need the Permission \"reportsystem.help\" to use this Command!");
			lang.set("ReloadPermission", "&cYou need the Permission \"reportsystem.reload\" to use this Command!");
			lang.set("Reported", "&aPlayer reported!");
			lang.set("ReportError", "&4Error reporting Player!\nPlease report this issue to an Admin!");
			lang.set("InvalidPlayer", "&4Invalid Player!");
			lang.set("InvalidUsage", "&4Invalid Usage!\nUse /report [PLAYER] [REASON]");
			lang.set("ReportFreed", "&4Report Freed!");
			lang.set("ReportClaimed", "&6Report Claimed!");
			lang.set("ReportCompleted", "&aReport Completed!");
			lang.set("PlayerBanned", "&cPlayer Banned!");
			lang.set("ReportDeleted", "&4Report Deleted!");
			lang.set("ExceptionDelete", "&4Report Deleted!");
			lang.set("ExceptionSetState", "&4Error changing Report state(Check Console)!");
			lang.set("BanMessage", "\n\n%PREFIX%\n&cYou have been banned!\n&cReason: %REASON%\n&5Banned by: &6%BANBY% &8(&6%BANBYUUID%&8)\n&aReport ID: %REPORTID%\n&0                                                                                                                                                                                                      &0");
			lang.set("HelpMessage", "&6Usage:\n&2/report [PLAYER] [REASON] -- Report a Player\n/reports -- Shows all Reports\n/reportsystem -- Shows Plugin Info\n/reportsystem info -- Shows Plugin Info\n/reportsystem help -- Shows Help Message\n/reportsystem reload -- Reloads the Plugin");
			lang.set("ReloadMessage", "&aConfigs reloaded!");
			lang.set("GuiReportsTitle", "&4Reports &8/ &2");
			lang.set("GuiManageReportTitle", "&2Report: &c");
			lang.set("GuiClose", "&4Close");
			lang.set("GuiCompleted", "&aCompleted");
			lang.set("GuiClaimed", "&6Claimed");
			lang.set("GuiFree", "&4Free");
			lang.set("GuiPrevPage", "&bPrevious Page");
			lang.set("GuiNextPage", "&bNext Page");
			lang.set("GuiBan", "&cBAN");
			lang.set("GuiDelete", "&4Delete");
			lang.set("GuiPropState", "&5State: ");
			lang.set("GuiPropCreator", "&6Creator: ");
			lang.set("GuiPropDate", "&bDate: ");
			lang.set("GuiPropTime", "&2Time: ");
			lang.set("GuiPropReason", "&9Reason: ");
			lang.set("GuiPropFile", "&cFile: ");
			lang.set("DeletePermission", "&cYou need the Permission \"reportsystem.report.delete\" to delete Reports!");
			lang.set("BanPermission", "&cYou need the Permission \"reportsystem.report.ban\" to ban Players!");
			lang.set("CompletePermission", "&cYou need the Permission \"reportsystem.report.complete\" to complete Reports!");
			lang.set("ClaimPermission", "&cYou need the Permission \"reportsystem.report.claim\" to claim Reports!");
			lang.set("FreePermission", "&cYou need the Permission \"reportsystem.report.free\" to free Reports!");
			lang.set("ManagePermission", "&cYou need the Permission \"reportsystem.report.manage\" to free Reports!");
			saveLangFile();
		}
		translateLangColors();
	}

	private void translateLangColors() {
		for (String key : lang.getKeys(false)) {
			String value = lang.getString(key);
			if (value != null) {
				lang.set(key, ChatColor.translateAlternateColorCodes('&', value));
			}
		}
	}

	private void saveConfigFile() {
		try {
			config.save(configPath.toFile());
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Failed to save config.yml", e);
		}
	}

	private void saveLangFile() {
		try {
			lang.save(langPath.toFile());
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Failed to save lang.yml", e);
		}
	}

	private void registerCommandsAndEvents() {
		getCommand("report").setExecutor(new CommandReport());
		getCommand("reports").setExecutor(new CommandReports());
		getCommand("reportsystem").setExecutor(new CommandReportSystem());
		getServer().getPluginManager().registerEvents(new EventGuiClick(), this);
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public FileConfiguration getLang() {
		return lang;
	}
}
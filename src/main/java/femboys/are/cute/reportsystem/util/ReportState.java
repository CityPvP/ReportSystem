package femboys.are.cute.reportsystem.util;

import femboys.are.cute.reportsystem.ReportSystem;
import org.bukkit.ChatColor;

public enum ReportState {
	COMPLETED("GuiCompleted", "&aCompleted"),
	CLAIMED("GuiClaimed", "&6Claimed"),
	FREE("GuiFree", "&4Free");

	private final String langKey;
	private final String defaultDisplayName;

	ReportState(String langKey, String defaultDisplayName) {
		this.langKey = langKey;
		this.defaultDisplayName = defaultDisplayName;
	}

	public String getDisplayName() {
		String langValue = ReportSystem.getInstance().getLang().getString(langKey, defaultDisplayName);
		return ChatColor.translateAlternateColorCodes('&', langValue);
	}


	public static ReportState fromString(String name) {
		if (name == null) {
			return null;
		}
		try {
			return valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}


	public boolean isModifiable() {
		return this != COMPLETED;
	}
}
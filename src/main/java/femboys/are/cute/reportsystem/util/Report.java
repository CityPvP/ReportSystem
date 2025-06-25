package femboys.are.cute.reportsystem.util;

import femboys.are.cute.reportsystem.ReportSystem;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("Report")
public class Report implements ConfigurationSerializable {
	private final UUID reportedPlayer;
	private final UUID creator;
	private final String reason;
	private final LocalDateTime timestamp;
	private ReportState state;

	public Report(UUID reportedPlayer, UUID creator, String reason, LocalDateTime timestamp, ReportState state) {
		if (reportedPlayer == null || creator == null || reason == null || reason.trim().isEmpty() || timestamp == null || state == null) {
			throw new IllegalArgumentException("Report fields cannot be null or empty");
		}
		this.reportedPlayer = reportedPlayer;
		this.creator = creator;
		this.reason = reason;
		this.timestamp = timestamp;
		this.state = state;
	}


	public UUID getReportedPlayer() {
		return reportedPlayer;
	}


	public UUID getCreator() {
		return creator;
	}


	public String getReason() {
		return reason;
	}


	public LocalDateTime getTimestamp() {
		return timestamp;
	}


	public String getFormattedTimestamp() {
		return timestamp.format(DateTimeFormatter.ofPattern(ReportSystem.TimestampFormat.toPattern()));
	}


	public ReportState getState() {
		return state;
	}


	public void setState(ReportState state) {
		if (!isModifiable()) {
			throw new IllegalStateException("Cannot modify a completed report");
		}
		if (state == null) {
			throw new IllegalArgumentException("State cannot be null");
		}
		this.state = state;
	}


	public boolean isModifiable() {
		return state.isModifiable();
	}

	public String getDisplayString() {
		String format = ReportSystem.getInstance().getLang().getString("ReportDisplayFormat",
				"&cReport by %CREATOR%: &6%REASON% &8[&b%TIMESTAMP%&8] (%STATE%)");
		return format
				.replace("%CREATOR%", creator.toString())
				.replace("%REASON%", reason)
				.replace("%TIMESTAMP%", getFormattedTimestamp())
				.replace("%STATE%", state.getDisplayName());
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<>();
		data.put("reportedPlayer", reportedPlayer.toString());
		data.put("creator", creator.toString());
		data.put("reason", reason);
		data.put("timestamp", timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		data.put("state", state.name());
		return data;
	}

	public static Report deserialize(Map<String, Object> data) {
		UUID reportedPlayer = UUID.fromString((String) data.get("reportedPlayer"));
		UUID creator = UUID.fromString((String) data.get("creator"));
		String reason = (String) data.get("reason");
		LocalDateTime timestamp = LocalDateTime.parse((String) data.get("timestamp"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		ReportState state = ReportState.fromString((String) data.get("state"));
		return new Report(reportedPlayer, creator, reason, timestamp, state);
	}
}
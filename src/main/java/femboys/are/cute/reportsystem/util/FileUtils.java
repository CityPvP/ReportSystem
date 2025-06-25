package femboys.are.cute.reportsystem.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class FileUtils {

	private FileUtils() {
		throw new AssertionError("Utility class should not be instantiated");
	}

	public static String readFromInputStream(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new NullPointerException("InputStream cannot be null");
		}
		StringBuilder result = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append('\n');
			}
		}
		return result.toString();
	}
}
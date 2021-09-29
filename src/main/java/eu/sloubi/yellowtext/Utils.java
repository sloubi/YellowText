package eu.sloubi.yellowtext;

import java.util.Optional;

public class Utils {

	private Utils() {
		throw new IllegalStateException("Utility class");
	}

	public static String getFileExtension(String fileName) {
		return Optional.ofNullable(fileName)
				.filter(f -> f.contains("."))
				.map(f -> f.substring(fileName.lastIndexOf(".") + 1))
				.orElse("");
	}

	public static String getFileNameWithoutExtension(String fileName) {
		if (fileName.contains(".")) {
			return fileName.substring(0, fileName.lastIndexOf("."));
		}
		return fileName;
	}

}

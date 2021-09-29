package eu.sloubi.yellowtext;

import java.util.Map;
import java.util.prefs.Preferences;

import static java.util.Map.entry;

public class PrefUtils {

	public static final Preferences prefs = Preferences.userNodeForPackage(App.class);

	private static final Map<String, Object> defaultValues = Map.ofEntries(
			entry("noteDir", ""),
			entry("newNoteDir", ""),
			entry("newFileExtension", ".md"),
			entry("hideExtension", false)
	);

	private static void checkKey(String key) {
		if (!defaultValues.containsKey(key)) {
			throw new IllegalArgumentException("Unfound default value for key : " + key);
		}
	}

	public static String get(String key) {
		checkKey(key);

		return prefs.get(key, (String) defaultValues.get(key));
	}

	public static boolean getBoolean(String key) {
		checkKey(key);

		return prefs.getBoolean(key, (boolean) defaultValues.get(key));
	}
}

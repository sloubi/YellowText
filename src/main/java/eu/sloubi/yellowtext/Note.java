package eu.sloubi.yellowtext;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Note {

	private Path path;
	private String fileName;
	private String name;
	private boolean opened;

	public Note(Path path) {
		this.path = path;
		fileName = path.getFileName().toString();
		name = fileName;

		if (PrefUtils.getBoolean("hideExtension")) {
			name = Utils.getFileNameWithoutExtension(fileName);
		}
	}

	public Ikon getIconClass() {
		if ("md".equals(Utils.getFileExtension(fileName))) {
			return BootstrapIcons.MARKDOWN;
		}
		return BootstrapIcons.FILE_EARMARK_TEXT;
	}

	public String read() {
		String content;
		try {
			byte[] bytes = Files.readAllBytes(path);
			content = new String(bytes, StandardCharsets.UTF_8);
		} catch (IOException e) {
			content = "Erreur en lisant le fichier";
		}
		return content;
	}

	public void create(String text) throws IOException {
		Files.createFile(path);
		write(text);
	}

	public void write(String text) throws IOException {
		Files.writeString(path, text);
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public Path getPath() {
		return path;
	}

	public String getName() {
		return name;
	}
}

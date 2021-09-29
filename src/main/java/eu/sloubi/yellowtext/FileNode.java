package eu.sloubi.yellowtext;

import javax.swing.tree.DefaultMutableTreeNode;
import java.nio.file.Path;

public class FileNode extends DefaultMutableTreeNode {
	private transient Note note;

	public FileNode(Path path) {
		super(path);
	}

	public FileNode(Note note) {
		super(note.getPath());
		this.note = note;
	}

	@Override
	public String toString() {
		return path().getFileName().toString();
	}

	public Path path() {
		return (Path) getUserObject();
	}

	public boolean hasNote() {
		return note != null;
	}

	public Note getNote() {
		return note;
	}
}

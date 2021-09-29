package eu.sloubi.yellowtext;

import javax.swing.*;

public class FileTab extends JScrollPane {

	private JTextArea editor = new JTextArea();
	private transient Note note;

	public FileTab(Note note) {
		super();
		this.note = note;

		editor.setText(note.read());
		editor.setLineWrap(true);

		setViewportView(editor);
	}

	public String getText() {
		return editor.getText();
	}

	public Note getNote() {
		return note;
	}
}

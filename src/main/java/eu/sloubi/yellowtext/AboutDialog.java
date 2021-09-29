package eu.sloubi.yellowtext;

import com.github.weisj.darklaf.graphics.ThemedColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutDialog extends JDialog {

	JLabel link;
	JLabel icon;
	JPanel textPane;
	JPanel buttonPane;

	public AboutDialog() {
		initLink();
		initIcon();
		initTextPane();
		initButtonPane();

		setLayout(new BorderLayout());
		add(icon, BorderLayout.WEST);
		add(textPane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.PAGE_END);

		setTitle("About");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(380, 255);
		setVisible(true);
	}

	private void initIcon() {
		var imageIcon = new ImageIcon(getClass().getResource("/icons/icon128.png"));
		icon = new JLabel(imageIcon);
		icon.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
	}

	private void initLink() {
		link = new JLabel(String.format("<html><u>%s</u></html>", App.AUTHOR));
		link.setForeground(new ThemedColor("palette.blue"));
		link.setCursor(new Cursor(Cursor.HAND_CURSOR));

		link.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(App.URL));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// not needed
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// not needed
			}
		});
	}

	private void initTextPane() {
		JLabel title = new JLabel(String.format("%s %s", App.NAME, App.VERSION));

		JPanel authorPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		JLabel by = new JLabel("By ");
		authorPane.add(by);
		authorPane.add(link);

		JLabel createdAt = new JLabel(String.format("Created in %s", App.CREATED));
		createdAt.setForeground(new ThemedColor("palette.gray"));

		textPane = new JPanel();
		textPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		textPane.add(title, gbc);
		textPane.add(authorPane, gbc);
		textPane.add(new JLabel(" "), gbc);
		textPane.add(createdAt, gbc);
	}

	private void initButtonPane() {
		JButton close = new JButton("Close");
		close.addActionListener(event -> dispose());

		buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(close);
	}
}

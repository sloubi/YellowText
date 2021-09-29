package eu.sloubi.yellowtext;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.components.border.DarkBorders;
import com.github.weisj.darklaf.graphics.ThemedColor;
import com.github.weisj.darklaf.settings.ThemeSettings;
import com.github.weisj.darklaf.ui.tree.DarkTreeUI;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.swing.FontIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.weisj.darklaf.LafManager.getPreferredThemeStyle;

public class App implements WindowListener {

	private JTree tree;
	private CloseableTabbedPane tabbedPane;
	private JButton openDirButton;
	private JButton newNoteButton;
	private JButton aboutButton;
	private JButton optionsButton;
	private JSplitPane splitPane;
	private JPanel mainPanel;
	private JFrame mainFrame;
	private JPanel openDirPanel;

	public static final String VERSION = "0.1";
	public static final String NAME = "YellowText";
	public static final String URL = "https://sloubi.eu";
	public static final String AUTHOR = "Sloubi";
	public static final String CREATED = "September 2021";

	/**
	 * The main method: starting point of this application.
	 *
	 * @param arguments the unused command-line arguments.
	 */
	public static void main(final String[] arguments) {
		// Disable log for JNativeHook
		LogManager.getLogManager().reset();
		var logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);

		new App().run();
	}

	/**
	 * Schedule a job for the event-dispatching thread: create and show this
	 * application's GUI.
	 */
	private void run() {
		SwingUtilities.invokeLater(() -> {
			LafManager.themeForPreferredStyle(getPreferredThemeStyle());
			LafManager.setDecorationsEnabled(false);
			LafManager.install();

			// Set the event dispatcher to a swing safe executor service.
			GlobalScreen.setEventDispatcher(new SwingDispatchService());

			createAndShowGui();
		});
	}

	/**
	 * Create the simple GUI for this application and make it visible.
	 */
	private void createAndShowGui() {
		initComponents();
		attachListeners();

		// Create the main frame
		mainFrame = new JFrame(NAME);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.getContentPane().add(mainPanel);
		mainFrame.setSize(800, 300);
		mainFrame.setLocationByPlatform(true);
		mainFrame.addWindowListener(this);
		mainFrame.setIconImages(List.of(
				new ImageIcon(getClass().getResource("/icons/icon32.png")).getImage(),
				new ImageIcon(getClass().getResource("/icons/icon128.png")).getImage()
		));
		mainFrame.setVisible(true);
	}

	private void initComponents() {
		var theme = ThemeSettings.getInstance().getTheme();
		var accentColor = theme.getAccentColorRule().getAccentColor();
		if (accentColor == null) {
			accentColor = new ThemedColor("palette.blue");
		}

		// Buttons
		openDirButton = new JButton("Open a folder");
		newNoteButton = new JButton(FontIcon.of(BootstrapIcons.FILE_EARMARK_PLUS_FILL, 32, accentColor).toImageIcon());
		newNoteButton.setFocusable(false);
		aboutButton = new JButton(FontIcon.of(BootstrapIcons.QUESTION_CIRCLE_FILL, 32, accentColor).toImageIcon());
		aboutButton.setFocusable(false);
		optionsButton = new JButton(FontIcon.of(BootstrapIcons.GEAR_FILL, 32, accentColor).toImageIcon());
		optionsButton.setFocusable(false);

		// Toolbar
		var toolBar = new JPanel();
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));
		toolBar.setBorder(BorderFactory.createCompoundBorder(DarkBorders.createRightBorder(),
				new EmptyBorder(10, 0, 10, 0)));
		toolBar.add(newNoteButton);
		toolBar.add(Box.createVerticalGlue());
		toolBar.add(aboutButton);
		toolBar.add(optionsButton);

		// Left sidebar
		openDirPanel = new JPanel();
		openDirPanel.setLayout(new GridBagLayout());
		openDirPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
		openDirPanel.add(openDirButton, new GridBagConstraints());
		tree = new JTree();

		// Tabs
		tabbedPane = new CloseableTabbedPane();

		// Splitter
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, tabbedPane);
		refreshSidebar();

		// Main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(toolBar, BorderLayout.WEST);
		mainPanel.add(splitPane, BorderLayout.CENTER);
	}

	private void attachListeners() {
		Action newNoteAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newNote();
			}
		};

		Action saveNoteAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveNote();
			}
		};

		aboutButton.addActionListener(e -> about());

		optionsButton.addActionListener(e -> ThemeSettings.showSettingsDialog(mainFrame));

		newNoteButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), newNoteAction);
		newNoteButton.getActionMap().put(newNoteAction, newNoteAction);
		newNoteButton.addActionListener(newNoteAction);

		mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), saveNoteAction);
		mainPanel.getActionMap().put(saveNoteAction, saveNoteAction);

		openDirButton.addActionListener(e -> openDir());

		if (tree != null) {
			// Open a tab when a tree node is double clicked
			tree.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent event) {
					var treePath = tree.getSelectionPath();
					if (treePath != null && event.getClickCount() == 2) {
						var fileNode = (FileNode) treePath.getLastPathComponent();
						if (fileNode.hasNote() && !fileNode.getNote().isOpened()) {
							// Add tab
							openNote(fileNode.getNote());
						}
					}
				}
			});
		}

		// Close the note when the tab is closed
		tabbedPane.addCloseableTabbedPaneListener(tabIndexToClose -> {
			var fileTab = (FileTab) tabbedPane.getComponentAt(tabIndexToClose);
			fileTab.getNote().setOpened(false);
			return true;
		});
	}

	private void refreshSidebar() {
		if (PrefUtils.get("noteDir").equals("")) {
			splitPane.setLeftComponent(openDirPanel);
		} else {
			splitPane.setLeftComponent(createTree());
		}
	}

	private void openDir() {
		var fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
			PrefUtils.prefs.put("noteDir", fileChooser.getSelectedFile().getAbsolutePath());
			refreshSidebar();
		}
	}

	private Component createTree() {
		var rootPath = getNoteDirPath();
		var root = new FileNode(rootPath);
		listDir(rootPath).forEach(path -> createChildFileNodes(path, root));

		tree = new JTree(root);
		tree.putClientProperty(DarkTreeUI.KEY_LINE_STYLE, DarkTreeUI.STYLE_NONE);
		return new JScrollPane(tree);
	}

	private void about() {
		new AboutDialog();
		mainFrame.requestFocus();
	}

	private void saveNote() {
		int index = tabbedPane.getModel().getSelectedIndex();
		if (index != -1) {
			var fileTab = (FileTab) tabbedPane.getComponentAt(index);
			try {
				fileTab.getNote().write(fileTab.getText());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error while writing the file : " + e.getLocalizedMessage(), "New note", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private List<Path> listDir(Path dirPath) {
		try (Stream<Path> stream = Files.list(dirPath)) {
			return stream
					.filter(path -> !path.getFileName().toString().startsWith("."))
					.sorted((a, b) -> {
						if (Files.isDirectory(a) && Files.isDirectory(b)
								|| Files.isRegularFile(a) && Files.isRegularFile(b)) {
							return a.getFileName().toString().toLowerCase().compareTo(b.getFileName().toString().toLowerCase());
						} else if (Files.isDirectory(a) && !Files.isDirectory(b)) {
							return -1;
						}
						return 1;
					})
					.collect(Collectors.toList());
		} catch (IOException ioException) {
			return new ArrayList<>();
		}
	}

	private void createChildFileNodes(Path path, DefaultMutableTreeNode parent) {
		if (Files.isDirectory(path)) {
			var node = new FileNode(path);
			parent.add(node);
			listDir(path).forEach(p -> createChildFileNodes(p, node));
		} else {
			var node = new FileNode(new Note(path));
			parent.add(node);
		}
	}

	public void windowOpened(WindowEvent e) {
		// Initialze native hook.
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();

			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(new GlobalKeyListener(this));
	}

	public void newNote() {
		// When coming from a global shortcut, we need to bring back focus to the frame
		mainFrame.requestFocus();

		String fileName = JOptionPane.showInputDialog(mainFrame, "File name", "New note", JOptionPane.QUESTION_MESSAGE);
		if (fileName != null) {
			createAddAndOpenNote(getNewNotePath(fileName));
		}
	}

	private Path getNoteDirPath() {
		return Paths.get(PrefUtils.get("noteDir"));
	}

	private Path getNewNoteDirPath() {
		return getNoteDirPath().resolve(PrefUtils.get("newNoteDir"));
	}


	private Path getNewNotePath(String fileName) {
		return Paths.get(String.format("%s/%s%s", getNewNoteDirPath(), fileName, PrefUtils.get("newFileExtension")));
	}

	private FileNode findNode(FileNode root, Path path) {
		Enumeration<TreeNode> e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			FileNode node = (FileNode) e.nextElement();
			if (node.path().toString().equals(path.toString())) {
				return node;
			}
		}
		return null;
	}

	private boolean addFileNode(FileNode newFileNode) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		FileNode root = (FileNode) model.getRoot();
		var parent = findNode(root, getNewNoteDirPath());

		if (parent == null) {
			return false;
		}

		model.insertNodeInto(newFileNode, parent, parent.getChildCount());
		return true;
	}

	private void createAddAndOpenNote(Path path) {
		try {
			var note = new Note(path);
			note.create("");

			var fileNode = new FileNode(note);

			if (addFileNode(fileNode)) {
				openNote(note);
			}
		} catch (FileAlreadyExistsException e) {
			JOptionPane.showMessageDialog(null, "This file already exists.", "New note", JOptionPane.WARNING_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error while creating the file : " + e.getMessage(), "New note", JOptionPane.ERROR_MESSAGE);
		}
	}


	private void openNote(Note note) {
		var fileTab = new FileTab(note);
		tabbedPane.addTab(note.getName(), fileTab);
		tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(fileTab));

		note.setOpened(true);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// not needed
	}

	public void windowClosed(WindowEvent event) {
		//Clean up the native hook.
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) {
			System.err.println("There was a problem unregistering the native hook.");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		System.exit(0);
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// not needed
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// not needed
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// not needed
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// not needed
	}
}

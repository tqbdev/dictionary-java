package com.tqbdev.screens;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.tqbdev.components.JListFilterDecorator;
import com.tqbdev.funcs.*;

public class MainScreen extends JFrame implements ActionListener, ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3873953090810156808L;	
	private static final String titleProg = "Dictionary English Vietnamese";
	
	private TypeDict typeDict = TypeDict.envi;
	private String typeDictStr = "English Dictionary";
	private JLabel textDictType;

	private JTextPane meaningTextPane;
	private JToggleButton btnLike;

	private HashMap<String, String> dict;
	private String selectedWordStr;

	private FavoriteWordList favoriteList;

	private JPanel leftPane;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception weTried) {
				}

				try {
					MainScreen frame = new MainScreen();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainScreen() {
		prepareData();

		setTitle(titleProg);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setIconImage((new ImageIcon(getClass().getResource("/icons/main.png"))).getImage());
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		leftPane = new JPanel();
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.Y_AXIS));
		leftPane.add(this.prepareControlPanel());

		textDictType = new JLabel(typeDictStr);
		textDictType.setFont(new Font("Serif", Font.PLAIN, 20));
		JPanel jPanel = new JPanel(new FlowLayout());
		jPanel.add(textDictType);
		leftPane.add(jPanel);

		leftPane.add(this.prepareSearchPanel());
		leftPane.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
		contentPane.add(leftPane);

		meaningTextPane = new JTextPane();
		meaningTextPane.setFont(new Font("Serif", Font.PLAIN, 20));
		meaningTextPane.setEditable(false);
		JScrollPane displayMeaning = new JScrollPane(meaningTextPane);
		displayMeaning.setMinimumSize(new Dimension(300, Integer.MAX_VALUE));
		contentPane.add(displayMeaning);

		btnLike = new JToggleButton();
		btnLike.setEnabled(false);
		btnLike.setBorderPainted(false);
		btnLike.setBorder(null);
		btnLike.setActionCommand("like");
		btnLike.addActionListener(this);

		try {
			btnLike.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/icons/star.png"))));
			btnLike.setSelectedIcon(new ImageIcon(ImageIO.read(getClass().getResource("/icons/star-filled.png"))));
		} catch (IOException e) {
			showErrorMessage(e.getMessage(), "ERROR");
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}

		contentPane.add(btnLike);
		setContentPane(contentPane);

		setMinimumSize(new Dimension(700, 500));

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				saveFavList();
			}
		});
	}

	private void saveFavList() {
		try {
			switch (typeDict) {
			case vien:
				favoriteList.saveListToFile("favoriteWord-VI.txt");
				break;
			case envi:
				favoriteList.saveListToFile("favoriteWord-EN.txt");
				break;
			}
		} catch (IOException e) {
			showWarning(e.getMessage(), "Save Favorite List Warning");
		}
	}

	private void prepareData() {
		favoriteList = new FavoriteWordList();

		this.loadFavList();
		this.loadDict();
	}

	private void loadFavList() {
		try {
			switch (typeDict) {
			case vien:
				favoriteList.loadListFromFile("favoriteWord-VI.txt");
				break;
			case envi:
				favoriteList.loadListFromFile("favoriteWord-EN.txt");
				break;
			}
		} catch (IOException e) {
			showWarning(e.getMessage(), "Load Favorite List Warning");
		}
	}

	private void loadDict() {
		try {
			switch (typeDict) {
			case vien:
				dict = LoadDicFromXML.loadFromFile("Viet_Anh.xml");
				break;
			case envi:
				dict = LoadDicFromXML.loadFromFile("Anh_Viet.xml");
				break;
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			showErrorMessage(e.getMessage(), "ERROR Load Dict");
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	private JPanel prepareSearchPanel() {
		JPanel searchChoicePane;

		List<String> words = new ArrayList<String>();
		words.addAll(dict.keySet());
		DefaultListModel<String> listWordModel = new DefaultListModel<>();
		words.forEach(listWordModel::addElement);
		JList<String> jList = new JList<>(listWordModel);

		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jList.addListSelectionListener(this);

		searchChoicePane = JListFilterDecorator.decorate(jList, MainScreen::wordFilter);
		searchChoicePane.setBorder(new EmptyBorder(0, 0, 0, 5));
		searchChoicePane.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

		return searchChoicePane;
	}

	private JPanel prepareControlPanel() {
		JPanel controlButtons = new JPanel();
		controlButtons.setBorder(new EmptyBorder(5, 0, 5, 0));
		controlButtons.setLayout(new BoxLayout(controlButtons, BoxLayout.X_AXIS));

		JButton changeDict = new JButton("vi -> en");
		changeDict.setActionCommand("change");
		changeDict.addActionListener(this);
		controlButtons.add(changeDict);

		JButton favList = new JButton("List Favorite");
		favList.setActionCommand("fav");
		favList.addActionListener(this);
		controlButtons.add(favList);

		JButton freq = new JButton("Frequent");
		freq.setActionCommand("freq");
		freq.addActionListener(this);
		controlButtons.add(freq);

		return controlButtons;
	}

	@Override
	public void valueChanged(ListSelectionEvent le) {
		if (!le.getValueIsAdjusting()) {
			btnLike.setEnabled(true);
			@SuppressWarnings("unchecked")
			JList<String> jLst = (JList<String>) le.getSource();
			selectedWordStr = jLst.getSelectedValue();

			if (favoriteList.hasWord(selectedWordStr)) {
				btnLike.setSelected(true);
			} else {
				btnLike.setSelected(false);
			}
			
			try {
				switch (typeDict) {
				case vien:
					FrequentWordXML.addWord(selectedWordStr, "history-VI.xml");
					break;
				case envi:
					FrequentWordXML.addWord(selectedWordStr, "history-EN.xml");
					break;
				}
			} catch (Exception e) {
				showWarning(e.getMessage(), "Save History Failed");
			}
			
			meaningTextPane.setText(dict.get(selectedWordStr));
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String actionCommand = evt.getActionCommand();

		switch (actionCommand) {
		case "change": {
			this.saveFavList();
			
			AbstractButton abstractButton = (AbstractButton) evt.getSource();

			switch (typeDict) {
			case vien:
				abstractButton.setText("vi -> en");
				typeDict = TypeDict.envi;
				typeDictStr = "English Dictionary";
				break;
			case envi:
				abstractButton.setText("en -> vi");
				typeDict = TypeDict.vien;
				typeDictStr = "Vietnamese Dictionary";
				break;
			}

			this.loadDict();
			this.loadFavList();

			//JLabel textDictType = (JLabel) ((JPanel) leftPane.getComponent(1)).getComponent(0);
			textDictType.setText(typeDictStr);

			leftPane.remove(2);
			leftPane.add(this.prepareSearchPanel());

			meaningTextPane.setText("");
			btnLike.setSelected(false);
			btnLike.setEnabled(false);
		}
			break;
		case "fav":
			FavListScreen favListScreen = new FavListScreen(this, "Favorite List Word for " + typeDictStr, favoriteList.getList());
			favListScreen.setLocationRelativeTo(this);
			favListScreen.setVisible(true);

			break;
		case "freq":
			FreqWordScreen freqWordScreen = new FreqWordScreen(this, "Frequent Word Search for " + typeDictStr, typeDict);
			freqWordScreen.setLocationRelativeTo(this);
			freqWordScreen.setVisible(true);
			break;
		case "like": {
			AbstractButton abstractButton = (AbstractButton) evt.getSource();
			boolean selected = abstractButton.getModel().isSelected();

			if (selected) {
				favoriteList.addWord(selectedWordStr);
			} else {
				favoriteList.removeWord(selectedWordStr);
			}
		}
		}
	}

	private void showErrorMessage(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(this, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
	}

	private void showWarning(String warnMessage, String warnTitle) {
		JOptionPane.showMessageDialog(this, warnMessage, warnTitle, JOptionPane.WARNING_MESSAGE);
	}

	private static boolean wordFilter(String word, String str) {
		return word.toLowerCase().contains(str.toLowerCase());
	}
}

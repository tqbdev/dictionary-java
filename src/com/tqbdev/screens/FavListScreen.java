package com.tqbdev.screens;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class FavListScreen extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6429530604264500008L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public FavListScreen(JFrame parent, String title, List<String> listWord) {
		super(parent, title, true);
		setBounds(100, 100, 450, 300);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		DefaultListModel<String> listWordModel = new DefaultListModel<>();
		listWord.forEach(listWordModel::addElement);
		JList<String> jList = new JList<>(listWordModel);
		
		JScrollPane displayMeaning = new JScrollPane(jList);
		contentPane.add(displayMeaning);
		
		setContentPane(contentPane);
	}

}

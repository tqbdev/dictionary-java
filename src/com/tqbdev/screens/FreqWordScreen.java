package com.tqbdev.screens;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jdatepicker.JDatePicker;
import org.jdatepicker.UtilDateModel;

import com.tqbdev.components.FrequentTableModel;
import com.tqbdev.funcs.FrequentWordXML;
import com.tqbdev.funcs.TypeDict;

public class FreqWordScreen extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1575038834842528117L;
	private JTable tableFrequentOfWord;
	private JDatePicker datePickerFrom, datePickerTo;
	private TypeDict typeDict;

	/**
	 * Create the frame.
	 */
	public FreqWordScreen(JFrame parent, String title, TypeDict typeDict) {		
		super(parent, title, true);
		
		this.typeDict = typeDict;
		setBounds(100, 100, 450, 300);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		contentPane.add(new JLabel("From date"));
		datePickerFrom = this.createDatePicker();
		contentPane.add(datePickerFrom);

		contentPane.add(new JLabel("To date"));
		datePickerTo = this.createDatePicker();
		contentPane.add(datePickerTo);
		
		tableFrequentOfWord = new JTable();
		tableFrequentOfWord.setModel(new FrequentTableModel());
		tableFrequentOfWord.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableFrequentOfWord.setAutoCreateRowSorter(true);
		tableFrequentOfWord.setShowVerticalLines(true);
		tableFrequentOfWord.setShowHorizontalLines(true);
		tableFrequentOfWord.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableFrequentOfWord.getColumnModel().getColumn(0).setResizable(false);
		tableFrequentOfWord.getTableHeader().setReorderingAllowed(false);
		
		JScrollPane tableScroll = new JScrollPane(tableFrequentOfWord, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableScroll.getViewport().setBackground(Color.WHITE);
		
		contentPane.add(tableScroll);
		
		loadFreq();

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadFreq();
			}
		};

		datePickerTo.addActionListener(actionListener);
		datePickerFrom.addActionListener(actionListener);

		setContentPane(contentPane);
	}
	
	private void loadFreq() {
		FrequentTableModel frequentTableModel = (FrequentTableModel) tableFrequentOfWord.getModel();
		
		Date from = (Date) datePickerFrom.getModel().getValue();
		Date to = (Date) datePickerTo.getModel().getValue();

		if (from.compareTo(to) > 0) {
			showWarning("From date > To date", "ERROR");
			frequentTableModel.setFreq(new HashMap<>());
		}

		HashMap<String, Integer> freq = null;
		try {
			switch (typeDict) {
			case vien:
				freq = FrequentWordXML.getListFrequent(from, to, "history-VI.xml");
				break;
			case envi:
				freq = FrequentWordXML.getListFrequent(from, to, "history-EN.xml");
				break;
			}
								
			frequentTableModel.setFreq(freq);
			resizeColumnWidth(tableFrequentOfWord);
		} catch (Exception e) {
			showWarning(e.getMessage(), "Loading frequent of word warning");
		}
	}
	
	private void showWarning(String warnMessage, String warnTitle) {
		JOptionPane.showMessageDialog(this, warnMessage, warnTitle, JOptionPane.WARNING_MESSAGE);
	}
	
	private void resizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			int width = 15; // Min width
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width + 1, width);
			}
			if (width < 50 && columnModel.getColumn(column).getHeaderValue() != "")
				width = 50;
			if (width > 300)
				width = 300;
			columnModel.getColumn(column).setPreferredWidth(width + 10);
		}
	}

	private JDatePicker createDatePicker() {
		UtilDateModel model = new UtilDateModel();
		LocalDate nowDate = LocalDate.now();
		model.setDate(nowDate.getYear(), nowDate.getMonthValue() - 1, nowDate.getDayOfMonth());
		model.setSelected(true);

		return new JDatePicker(model);
	}
}

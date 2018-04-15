package com.tqbdev.components;

import java.util.HashMap;
import javax.swing.table.AbstractTableModel;

public class FrequentTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6705986070517256588L;
	private HashMap<String, Integer> freq;
	private String[] words;
	private String[] columns = { "Word", "Frequent" };

	public FrequentTableModel() {
		this(new HashMap<>());
	}

	public FrequentTableModel(HashMap<String, Integer> freq) {
		this.freq = freq;
		this.words = (String[]) (freq.keySet().toArray(new String[freq.size()]));
	}

	public Object getValueAt(int row, int column) {
		String word = words[row];
		switch (column) {
		case 0:
			return word;
		case 1:
			return freq.get(word).toString();
		default:
			System.err.println("Logic Error");
		}
		return "";
	}

	public int getColumnCount() {
		return columns.length;
	}

	public Class<?> getColumnClass(int column) {
		switch (column) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		}
		return String.class;
	}

	public String getColumnName(int column) {
		return columns[column];
	}

	public int getRowCount() {
		return words.length;
	}

	public String getWord(int row) {
		return words[row];
	}

	public void setFreq(HashMap<String, Integer> freq) {
		this.freq = freq;
		this.words = (String[]) (freq.keySet().toArray(new String[freq.size()]));
		fireTableDataChanged();
	}
}

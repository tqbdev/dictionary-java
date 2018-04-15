package com.tqbdev.funcs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class FavoriteWordList {
	private TreeSet<String> wordList;

	public FavoriteWordList() {
		Collator viCollator = Collator.getInstance(new Locale("vi"));
		wordList = new TreeSet<>(viCollator);
	}

	public void loadListFromFile(String fileName) throws IOException {
		this.clearList();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			return;
		}

		while (true) {
			String strLine = br.readLine();
			
			if (strLine == null || strLine == "" || strLine.length() == 0) {
				break;
			}

			strLine = strLine.trim();
			
			this.addWord(strLine);
		}

		br.close();
	}

	public void saveListToFile(String fileName) throws IOException {
		BufferedWriter bw = null;

		bw = new BufferedWriter(new FileWriter(fileName));
		
		for (String str : wordList) {
			bw.write(str);
			bw.newLine();
		}

		bw.close();
	}

	public void clearList() {
		wordList.clear();
	}

	public boolean hasWord(String word) {
		if (word == null) return false;
		return wordList.contains(word);
	}

	public List<String> getList() {
		return new ArrayList<String>(wordList);
	}

	public boolean addWord(String word) {
		return wordList.add(word);
	}

	public boolean removeWord(String word) {
		return wordList.remove(word);
	}
}
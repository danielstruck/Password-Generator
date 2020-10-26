import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class IndexedStringArray {
	private ArrayList<String> lines;
	private int index;

	
	public IndexedStringArray() {
		index = -1;
		lines = new ArrayList<String>();
	}
	public IndexedStringArray(File file) {
		this();
		
		try (Scanner sc = new Scanner(new FileInputStream(file));){
			while (sc.hasNextLine()) {
				lines.add(sc.nextLine());
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error occured when reading file", JOptionPane.ERROR_MESSAGE);
		}
	}
	public IndexedStringArray(IndexedStringArray toClone) {
		this();
		
		this.lines = toClone.getLines();
	}
	
	
	public ArrayList<String> getLines() {
		return lines;
	}
	
	public void setStringAt(int index, String value) {
		if (indexWithinBounds(index))
			lines.set(index, value);
	}
	
	
	public String getNext() {
		increment();
		if (!isEndOfFile())
			return lines.get(index);
		else
			return null;
	}
	public Integer getNextOfInt() {
		String line = getNext();
		
		return Integer.parseInt(line);
	}
	
	
	private void increment() {
		if (!isEndOfFile())
			++index;
	}
	
	
	public boolean isEndOfFile() {
		return index == lines.size();
	}
	public boolean indexWithinBounds(int index) {
		return index > 0 && index < lines.size();
	}
}

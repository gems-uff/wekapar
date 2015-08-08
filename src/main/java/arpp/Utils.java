package arpp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 * A collection of some useful methods.
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class Utils {
	
	/**
	 * Searches for a {@link Pattern} in {@link String} <code>s</code>
	 * 
	 * @param pattern a regex to search
	 * @param s the string to search in
	 * @return index of pattern in s or -1, if not found.
	 */
	public static int indexOf(String pattern, String s) {
		Pattern p = Pattern.compile(pattern);
	    Matcher m = p.matcher(s);
	    return m.find() ? m.start() : -1;
	}
	
	/**
	 * Get the maximum value of a {@link TableColumn}.
	 * 
	 * @param table the {@link JTable} component
	 * @param colIndex the index of column
	 * @return the maximum value
	 */
	public static double getColumnMaxValue(JTable table, int colIndex) {

		List<Double> list = new ArrayList<>();
		for (int i = 0; i < table.getRowCount(); i++) {
			list.add((double) table.getValueAt(i, colIndex));
		}
		
		return list != null ? Collections.max(list) : 0;
		
	}
	
	/**
	 * Get the minimum value of a {@link TableColumn}.
	 * 
	 * @param table the {@link JTable} component
	 * @param colIndex the index of column
	 * @return the minimum value
	 */
	public static double getColumnMinValue(JTable table, int colIndex) {

		List<Double> list = new ArrayList<>();
		for (int i = 0; i < table.getRowCount(); i++) {
			list.add((double) table.getValueAt(i, colIndex));
		}
		
		return list != null ? Collections.min(list) : 0;
		
	}

}

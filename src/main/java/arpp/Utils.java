package arpp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import weka.core.SystemInfo;

/**
 * A collection of some useful stuff.
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class Utils {
	
	/** The java class version */
	public static double JAVA_CLASS_VERSION = getJavaClassVersion();
	
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
	 * Get content from each cell of a {@link TableColumn}.
	 * 
	 * @param table the {@link JTable} component
	 * @param colIndex the index of column
	 * @return the list with all column's values
	 */
	private static List<Double> getColumnValues(JTable table, int colIndex) {
		
		List<Double> list = new ArrayList<Double>();
		TableModel tableModel = table.getModel();
		int modelIndex = table.convertColumnIndexToModel(colIndex);
		
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			list.add((Double) tableModel.getValueAt(i, modelIndex));
		}
		
		return list;
		
	}
	
	/**
	 * Get the maximum value of a {@link TableColumn}.
	 * 
	 * @param table the {@link JTable} component
	 * @param colIndex the index of column
	 * @return the maximum value; or 0 if no value was found
	 */
	public static double getColumnMaxValue(JTable table, int colIndex) {
		
		List<Double> list = getColumnValues(table, colIndex);
		
		return !list.isEmpty() ? Collections.max(list) : 0;
		
	}
	
	/**
	 * Get the minimum value of a {@link TableColumn}.
	 * 
	 * @param table the {@link JTable} component
	 * @param colIndex the index of column
	 * @return the minimum value; or 0 if no value was found
	 */
	public static double getColumnMinValue(JTable table, int colIndex) {
		
		List<Double> list = getColumnValues(table, colIndex);
		
		return !list.isEmpty() ? Collections.min(list) : 0;
		
	}
	
	/**
	 * Get plugin version
	 * 
	 * @throws FileNotFoundException 
	 */
	public static String getVersion() throws FileNotFoundException {
		
		String version = "";
		
		File file = new File("src/main/java/arpp/version.txt");
		Scanner scanner = new Scanner(file);
		version = scanner.nextLine();
		scanner.close();
		
		return version;
		
	}
	
	/**
	 * Get java class version
	 */	
	private static double getJavaClassVersion() {
		
		SystemInfo sysInfo = new SystemInfo();
		String classVersion = sysInfo.getSystemInfo().get("java.class.version");
		double version = Double.parseDouble(classVersion);
				
		return version;
		
	}

}

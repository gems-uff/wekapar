package wekapar.table;

import java.text.DecimalFormat;

import javax.swing.table.DefaultTableModel;

/**
 * The default table model for {@link RulesTable}.
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class RulesTableModel extends DefaultTableModel {

	/** for serialization */
	private static final long serialVersionUID = -7024122542359856593L;
	
	/** Minimum number of decimal places */
	public static final int DECIMAL_PLACES_MIN = 2;
	
	/** Maximum number of decimal places */
	public static final int DECIMAL_PLACES_MAX = 10;

	/** The pattern for decimal format */
	public static final String DECIMAL_PATTERN = "#0.";
	
	/** The formatter to apply the decimal format for a value */
	private DecimalFormat formatter = new DecimalFormat(DECIMAL_PATTERN + (new String(new char[DECIMAL_PLACES_MIN])).replace("\0", "0"));
	
	/**
	 * Returns the correct class to be applied to a column according to its
	 * content's runtime class to provide the sorter with the means of correct
	 * sorting.
	 * 
	 * @return the runtime class for column
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		Class<?> columnClass = null;

		if (columnIndex < getColumnCount()) {
			columnClass = super.getValueAt(0, columnIndex).getClass();
		}
		
		return columnClass;
		
	}
	
	/**
	 * Disable cell's editing capability.
	 * 
	 * @return <code>false</code> for disabling editing
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	/**
	 * Returns an attribute value for the cell at <code>row</code> and
	 * <code>column</code>, and applies the appropriate format in case of a
	 * {@link Double} type value.
	 * 
	 * @param row the row whose value is to be queried
	 * @param column the column whose value is to be queried
	 * @return the value {@link Object} at the specified cell
	 */
	@Override
	public Object getValueAt(int row, int column) {
		
		Object value = super.getValueAt(row, column);
		
		if (value.getClass().equals(Double.class)) {
			value = formatter.format((Number) value);
			value = ((String) value).replaceAll(",", ".");
			value = Double.parseDouble((String) value);
		}
		
		return value;
		
	}
	
	/**
	 * Sets decimal places for column's values
	 * 
	 * @param n	the number of decimal places
	 * */
	public void setDecimals(int n) {
		
		if (n >= DECIMAL_PLACES_MIN && n <= DECIMAL_PLACES_MAX) {
			String pattern = DECIMAL_PATTERN + (new String(new char[n])).replace("\0", "0");
			formatter = new DecimalFormat(pattern);
		}
		
	};
	
	/**
	 * Returns the formatter to apply the decimal format for a value.
	 * 
	 * @return the {@link DecimalFormat} object
	 */
	public DecimalFormat getFormatter() {
		return formatter;
	}
	
}

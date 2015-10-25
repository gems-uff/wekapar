package arpp.table;

import java.text.DecimalFormat;

import javax.swing.table.DefaultTableModel;

public class RulesTableModel extends DefaultTableModel {

	/** for serialization */
	private static final long serialVersionUID = -7024122542359856593L;
	
	public static final int DECIMAL_PLACES_MIN = 2;
	
	public static final int DECIMAL_PLACES_MAX = 10;

	public static final String DECIMAL_PATTERN = "#0.";
			
	private DecimalFormat formatter = new DecimalFormat(DECIMAL_PATTERN + (new String(new char[DECIMAL_PLACES_MIN])).replace("\0", "0"));
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		Class<?> columnClass = null;
		
		if ((columnIndex >= 0) && (columnIndex < getColumnCount())) {
			columnClass = super.getValueAt(0, columnIndex).getClass();
		} else {
			columnClass = Object.class;
		}
		
		return columnClass;
		
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
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
	
	public DecimalFormat getFormatter() {
		return formatter;
	}
	
}

package arpp.table;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * <p>
 * Extends {@link DefaultTableCellRenderer} to display decimal numbers in table
 * cells.
 * </p>
 * 
 * <p>
 * The values are formatted to a {@link DecimalFormat} defined by table model.
 * </p>
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class DecimalCellRenderer extends DefaultTableCellRenderer {

	/** for serialization */
	private static final long serialVersionUID = -5952628278736902476L;
	
	/**
	 * Applies a format to values according to {@link DecimalFormat} and returns
	 * the {@link TableCellRenderer}.
	 * 
	 * @return the custom {@link TableCellRenderer} component
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		DecimalFormat formatter = ((RulesTableModel) table.getModel()).getFormatter();
				
		if (value instanceof String) {
			value = Double.valueOf((String) value);
		}
		value = formatter.format((Number) value);
		
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
	}

}

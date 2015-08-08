package arpp;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class DecimalFormatRenderer extends DefaultTableCellRenderer {

	/** for serialization */
	private static final long serialVersionUID = -5952628278736902476L;
	
	private static final DecimalFormat formatter = new DecimalFormat( "#0.00" );
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (value instanceof String) {
			value = Double.valueOf((String) value);
		}
		value = formatter.format((Number)value);
		
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
	}

}

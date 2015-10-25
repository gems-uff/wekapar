package arpp.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class RulesTableCellRenderer extends DefaultTableCellRenderer {

	/** for serialization */
	private static final long serialVersionUID = -8786887441345389283L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		JLabel c = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setToolTipText(c.getText());
		
		return c;
		
	}

}

package arpp.table;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * <p>
 * The default cell renderer for antecedent and consequent in {@link RulesTable}.
 * </p>
 * 
 * <p>
 * Displays a tooltip text with cell's content.
 * </p>
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class RulesTableCellRenderer extends DefaultTableCellRenderer {

	/** for serialization */
	private static final long serialVersionUID = -8786887441345389283L;
	
	/**
	 * Returns the {@link TableCellRenderer} component as a {@link JLabel} with a tooltip
	 * text.
	 * 
	 * @return the custom {@link TableCellRenderer} component
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setToolTipText(c.getText());
		
		return c;
		
	}

}

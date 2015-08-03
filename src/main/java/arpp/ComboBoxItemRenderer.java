/**
 * 
 */
package arpp;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * @author Daniel
 *
 */
public class ComboBoxItemRenderer extends BasicComboBoxRenderer {

	/** for serialization */
	private static final long serialVersionUID = -5295559523977929719L;
	
	/* (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicComboBoxRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@SuppressWarnings("rawtypes")
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		if (value instanceof ComboBoxItem) {
			ComboBoxItem item = (ComboBoxItem) value;
			setText(item.getLabel());
		}
		return this;
	}
	
}

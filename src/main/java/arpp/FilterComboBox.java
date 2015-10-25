package arpp;

import javax.swing.JComboBox;

/**
 * A customized JComboBox for filtering in postprocess panel
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class FilterComboBox extends JComboBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1588653220496311641L;

	/**
	 * Adds item at first position in list
	 * 
	 * @param item the item to add
	 * @see javax.swing.JComboBox#addItem(java.lang.Object)
	 */
	@Override
	public void addItem(Object item) {

		insertItemAt(item, 0);
		
	}
	
	/**
	 * Insert <code>item</code> if not exists. Otherwise, if
	 * <code>item</code> was found, it will be moved from current
	 * position to new <code>index</code> position.
	 * 
	 * @param item the item to insert
	 * @param index position where to insert the element
	 * @see javax.swing.JComboBox#insertItemAt(java.lang.Object, int)
	 */
	@Override
	public void insertItemAt(Object item, int index) {
		
		/* Remove if item was found in element list */
		for (int i = 0; i < getModel().getSize(); i++) {
			if (item.equals(getItemAt(i))) {
				removeItemAt(i);
			}
		}
		
		/* Adds item at index position */
		super.insertItemAt(item, index);
		
	}

}

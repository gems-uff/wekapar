package arpp;

import javax.swing.table.DefaultTableModel;

public class RulesTableModel extends DefaultTableModel {

	/** for serialization */
	private static final long serialVersionUID = -7024122542359856593L;
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		Class<?> columnClass = null;
		
		if ((columnIndex >= 0) && (columnIndex < getColumnCount())) {
			columnClass = getValueAt(0, columnIndex).getClass();
		} else {
			columnClass = Object.class;
		}
		
		return columnClass;
		
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

}

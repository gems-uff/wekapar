package arpp;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class RulesTableColumnModel extends DefaultTableColumnModel {
	
	/** for serialization */
	private static final long serialVersionUID = 656843853519023037L;
	
	private Map<String, RulesTableIndexedColumn> hiddenColumns = new HashMap<>();
	
	public void hideColumn(String columnName) {
		
		int index = getColumnIndex(columnName);
		TableColumn column = getColumn(index);
		RulesTableIndexedColumn indexedColumn = new RulesTableIndexedColumn(index, column);
		hiddenColumns.put(columnName, indexedColumn);
		removeColumn(column);

	}
	
	public void showColumn(String columnName) {
		
		RulesTableIndexedColumn indexedColumn = hiddenColumns.remove(columnName);
		if (indexedColumn != null) {
			addColumn(indexedColumn.getColumn());
			int lastColumn = getColumnCount() - 1;
			if (indexedColumn.getIndex() < lastColumn) {
				moveColumn(lastColumn, indexedColumn.getIndex());
			}
		}
		
	}

}

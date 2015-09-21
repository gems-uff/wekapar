package arpp;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

// TODO: Wrong scale for progress cell renderer after hide/show column
public class RulesTableColumnModel extends DefaultTableColumnModel {
	
	/** for serialization */
	private static final long serialVersionUID = 656843853519023037L;
	
	private Map<String, RulesTableIndexedColumn> hiddenColumns = new HashMap<String, RulesTableIndexedColumn>();
	
	public void hideColumn(String columnName) {
		
		int index = getColumnIndex(columnName);
		TableColumn column = getColumn(index);
		int modelIndex = column.getModelIndex();
		RulesTableIndexedColumn indexedColumn = new RulesTableIndexedColumn(modelIndex, column);
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
	
	public boolean hasColumn(Object identifier) {
		
		boolean found = false;
		
		for (TableColumn tableColumn : tableColumns) {
			if (tableColumn.getIdentifier().equals(identifier)) {
				found = true;
				break;
			}
		}
		
		return found;
		
	}

}

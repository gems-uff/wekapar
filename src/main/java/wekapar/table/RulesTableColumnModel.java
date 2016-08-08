package wekapar.table;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 * The default column model for {@link RulesTable} with hide/show columns
 * capability.
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class RulesTableColumnModel extends DefaultTableColumnModel {
	
	/** for serialization */
	private static final long serialVersionUID = 656843853519023037L;
	
	/** The list of hidden columns indexed by column name  */
	private Map<String, RulesTableIndexedColumn> hiddenColumns = new HashMap<String, RulesTableIndexedColumn>();
	
	/**
	 * Hides a table column. The hidden column is mapped to a
	 * {@link RulesTableIndexedColumn} object with the column object itself and
	 * the model index. The {@link Map} object maps column names as keys.
	 * 
	 * @param columnName the column name
	 */
	public void hideColumn(String columnName) {
		
		int index = getColumnIndex(columnName);
		TableColumn column = getColumn(index);
		int modelIndex = column.getModelIndex();
		RulesTableIndexedColumn indexedColumn = new RulesTableIndexedColumn(modelIndex, column);
		hiddenColumns.put(columnName, indexedColumn);
		removeColumn(column);

	}
	
	/**
	 * Show up a hidden table column. The hidden column is removed from
	 * {@link Map} object, added to column model, and moved to its presviously
	 * position.
	 * 
	 * @param columnName the column name
	 */
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
	
	/**
	 * Checks if a column exists in column model.
	 * 
	 * @param identifier the identifier object of a table column
	 * @return <code>true</code> if column exists, <code>false</code> otherwise
	 */
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

package wekapar.table;

import javax.swing.table.TableColumn;

/**
 * A simple data structure to store a table column object and an index.
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class RulesTableIndexedColumn {
	
	/** The index of the column */
	private int index;
	
	/** The table column object */
	private TableColumn column;
	
	/**
	 * Constructs a {@link RulesTableIndexedColumn} that is initialized with
	 * index as the index of the column and column as the table column object.
	 * 
	 * @param index the index of the column
	 * @param column the table column object
	 */
	public RulesTableIndexedColumn(int index, TableColumn column) {
		
		this.index = index;
		this.column = column;
		
	}
	
	/**
	 * Gets the {@link TableColumn}.
	 * 
	 * @return the table column object
	 */
	public TableColumn getColumn() {
		return column;
	}
	
	/**
	 * Set a {@link TableColumn}.
	 * 
	 * @param column the table column object
	 */
	public void setColumn(TableColumn column) {
		this.column = column;
	}
	
	/**
	 * Gets the index of the column.
	 * 
	 * @return a <code>int</code> value representing the column index
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Set a index for column.
	 * 
	 * @param index a <code>int</code> value representing the column index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

}

package arpp.table;

import javax.swing.table.TableColumn;

public class RulesTableIndexedColumn {
	
	private int index;
	private TableColumn column;
	
	public RulesTableIndexedColumn(int index, TableColumn column) {
		
		this.index = index;
		this.column = column;
		
	}
	
	public TableColumn getColumn() {
		return column;
	}
	
	public void setColumn(TableColumn column) {
		this.column = column;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

}

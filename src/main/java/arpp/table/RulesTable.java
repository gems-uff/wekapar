package arpp.table;

import javax.swing.JTable;

public class RulesTable extends JTable {
	
	/** for serialization */
	private static final long serialVersionUID = 4628909788780809236L;
	
	@Override
	public RulesTableColumnModel getColumnModel() {
		return (RulesTableColumnModel) super.getColumnModel();
	}

	@Override
	protected RulesTableHeader createDefaultTableHeader() {
		return new RulesTableHeader(getColumnModel());
	}
	
	@Override
	protected RulesTableColumnModel createDefaultColumnModel() {
		return new RulesTableColumnModel();
	}

}

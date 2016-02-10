package arpp.table;

import javax.swing.JTable;

/**
 * A modified {@link JTable} to display rules and metrics values
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class RulesTable extends JTable {
	
	/** for serialization */
	private static final long serialVersionUID = 4628909788780809236L;
	
	/**
	 * Gets the column model as a {@link RulesTableColumnModel}
	 * 
	 * @return the column model object
	 */
	@Override
	public RulesTableColumnModel getColumnModel() {
		return (RulesTableColumnModel) super.getColumnModel();
	}
	
	/**
	 * Creates the default table header as a {@link RulesTableHeader}
	 * 
	 * @return the default table header object
	 */
	@Override
	protected RulesTableHeader createDefaultTableHeader() {
		return new RulesTableHeader(getColumnModel());
	}
	
	/**
	 * Creates the default column model as a {@link RulesTableColumnModel}
	 * 
	 * @return the default column model object
	 */
	@Override
	protected RulesTableColumnModel createDefaultColumnModel() {
		return new RulesTableColumnModel();
	}

}

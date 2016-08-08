package wekapar.table;

import javax.swing.table.JTableHeader;

/**
 * The default table header for {@link RulesTable}.
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class RulesTableHeader extends JTableHeader {
	
	/** for serialization */
	private static final long serialVersionUID = -3511685227514492557L;
	
	/**
	 * Constructs a {@link RulesTableHeader} that is initialized with
	 * columnModel as the table column model.
	 * 
	 * @param columnModel a {@link RulesTableColumnModel} object
	 */
	public RulesTableHeader(RulesTableColumnModel columnModel) {
		super(columnModel);
	}

	/**
	 * Disable column's reordering capability.
	 * 
	 * @return <code>false</code> for disabling reordering
	 */
	@Override
	public boolean getReorderingAllowed() {
		return false;
	}

}

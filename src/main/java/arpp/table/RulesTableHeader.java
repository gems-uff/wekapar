package arpp.table;

import javax.swing.table.JTableHeader;

public class RulesTableHeader extends JTableHeader {
	
	/** for serialization */
	private static final long serialVersionUID = -3511685227514492557L;
	
	public RulesTableHeader(RulesTableColumnModel columnModel) {
		super(columnModel);
	}

	@Override
	public boolean getReorderingAllowed() {
		return false;
	}

}

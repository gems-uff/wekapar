package weka.gui.explorer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import weka.associations.AssociationRule;
import weka.associations.AssociationRules;
import weka.associations.Item;
import weka.core.Instances;
import weka.gui.explorer.Explorer.ExplorerPanel;

import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;

import javax.swing.JList;

/**
 * A JPanel to visualize association rules
 *
 * @author Daniel Silva (danielnsilva{[at]}gmail{[dot]}com)
 */
public class PostprocessPanel extends JPanel implements ExplorerPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3114490118265884877L;
	
	/** The parent frame */
	protected Explorer m_Explorer = null;

	/** Component where rules are loaded */
	protected static JTable table;

	/** TableModel component to load rules */
	protected static DefaultTableModel model;

	/**
	 * Create the postprocess panel.
	 */
	public PostprocessPanel() {
		
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneTable = new JScrollPane();
		scrollPaneTable.setBorder(new TitledBorder(null, "Rules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		table = new JTable(
				new DefaultTableModel(
						new Object[][]{},
						new String[]{
								"Premise",
								"Consequent",
								"Premise Support",
								"Consequent Support",
								"Total Support",
								"Metrics"
						}
				)
		);
		
		scrollPaneTable.setViewportView(table);
		
		JScrollPane scrollPaneList = new JScrollPane();
		scrollPaneList.setBorder(new TitledBorder(null, "Result list", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JList<String> list = new JList<String>();
		
		scrollPaneList.setViewportView(list);
		
		add(scrollPaneTable);
//		add(scrollPaneList, BorderLayout.WEST);

	}

	/**
	 * Loads rules into a JTable
	 *
	 * @param  rules  the association rules
	 */
	public static void loadRules(AssociationRules rules) {
		
		List<AssociationRule> rulesList = rules.getRules();
		
		List<String> tableHead = new ArrayList<String>();
		tableHead.add("Premise");
		tableHead.add("Consequent");
		tableHead.add("Premise Support");
		tableHead.add("Consequent Support");
		tableHead.add("Total Support");
		
		String[] metrics = rulesList.get(0).getMetricNamesForRule();
		for (String m : metrics) {
			tableHead.add(m);
		}
		
		table.setModel(
			new DefaultTableModel(
				new Object[][]{},
				tableHead.toArray()
			)
		);
		
		model = (DefaultTableModel) table.getModel();
		
		for (AssociationRule r : rulesList) {
	
			String premise = "";
			for (Item p : r.getPremise()) {
				premise += p + " ";
			}
			
			String consequent = "";
			for (Item c : r.getConsequence()) {
				consequent += c + " ";
			}
			
			int premiseSupport = r.getPremiseSupport();
			int consequentSupport = r.getConsequenceSupport();
			int totalSupport = r.getTotalSupport();
			
			List<Object> values = new ArrayList<Object>();
			values.add(premise);
			values.add(consequent);
			values.add(premiseSupport);
			values.add(consequentSupport);
			values.add(totalSupport);
			
			for (String m: metrics) {
				try {
					values.add(String.format("%.2f", r.getNamedMetricValue(m)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			model.addRow(values.toArray());
			
		}
		
	}

	/** Unused */
	@Override
	public void setInstances(Instances arg0) {}

	/**
	 * Sets the Explorer to use as parent frame
	 * 
	 * @param parent the parent frame
	 * */
	@Override
	public void setExplorer(Explorer parent) {
		m_Explorer = parent;
	}

	/**
	 * Returns the parent Explorer frame
	 * 
	 * @return the parent frame
	 * */
	@Override
	public Explorer getExplorer() {
		return m_Explorer;
	}

	/**
	 * Returns the title for the tab in the Explorer
	 * 
	 * @return the tab title
	 * */
	@Override
	public String getTabTitle() {
		return "Postprocess associations";
	}

	/** 
	 * Returns the tooltip for the tab in the Explorer
	 * 
	 * @return the tab tooltip
	 * */
	@Override
	public String getTabTitleToolTip() {
		return "Load/Filter/Save associator output";
	}

}

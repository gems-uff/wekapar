package tcc;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import weka.associations.AssociationRule;
import weka.associations.AssociationRules;
import weka.associations.Item;

import javax.swing.JTable;
import javax.swing.JScrollPane;

/**
 * A JFrame to visualize association rules
 *
 * @author Daniel Silva (danielnsilva{[at]}gmail{[dot]}com)
 */
public class TCCFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4006358412145990848L;

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel model;
	private JScrollPane scrollPane;
	
	private List<AssociationRule> rulesList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TCCFrame frame = new TCCFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TCCFrame() {
		
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
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
		
		scrollPane.setViewportView(table);
		
	}
	
	/**
	 * Loads rules into a JTable
	 *
	 * @param  rules  the association rules
	 */
	public void loadRules(AssociationRules rules) {
		
		rulesList = rules.getRules();
		
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

}

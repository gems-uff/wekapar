package weka.gui.explorer;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import weka.associations.AssociationRule;
import weka.associations.AssociationRules;
import weka.associations.Item;
import weka.core.Instances;
import weka.gui.Logger;
import weka.gui.SysErrLog;
import weka.gui.explorer.Explorer.ExplorerPanel;
import weka.gui.explorer.Explorer.LogHandler;

import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.RowFilter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import arpp.ComboBoxItem;
import arpp.FilterComboBox;
import arpp.FilterMap;
import arpp.FilterMapAttribute;
import arpp.Utils;

import javax.swing.JLabel;

import java.awt.FlowLayout;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * A JPanel to visualize association rules
 *
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class PostprocessAssociationsPanel extends JPanel implements ExplorerPanel, LogHandler {

	/** for serialization */
	private static final long serialVersionUID = 3114490118265884877L;
	
	/** The parent frame */
	protected Explorer m_Explorer = null;
		
	/** The destination for log/status messages */
	protected static Logger m_Log = new SysErrLog();

	/** Component where rules are loaded */
	protected static JTable table;
	
	protected static TableRowSorter<TableModel> sorter;
	
	protected List<String> parseFilterList;
	protected String parseFilterTemp = "";

	/** TableModel component to load rules */
//	protected static DefaultTableModel model;
	
	protected static FilterMap[] filterMapList;
	
	protected static JLabel lblTotalRulesValue;
	
	protected static JLabel lblFilteredRulesValue;
	
	protected static JComboBox<String> comboLogicalOperator;
	
	protected static JComboBox<ComboBoxItem> comboTableColumn;
	
	protected static JComboBox<String> comboComparisonOperator;
	
	protected static JComboBox<ComboBoxItem> comboAttribute;
	
	protected static JComboBox<String> comboValue;

	protected static JButton btnAdd;
	
	protected static FilterComboBox comboFilter = new FilterComboBox();
	
	protected JTextField comboFilterComponent = (JTextField) comboFilter.getEditor().getEditorComponent();
	
	protected static JButton btnApply;
	
	protected static JButton btnClear;
	private JPanel panel;
	private JPanel rulesPanel;
	private JPanel actionPanel;
	private JButton btnSave;
	private JButton btnOpen;
	private JPanel resultListPanel;
	private JButton btnExport;
	private JButton btnLoadFromClipboard;
	private JPanel actionPanelNorth;
	private JPanel actionPanelSouth;

	/**
	 * Create the postprocess panel.
	 */
	public PostprocessAssociationsPanel() {
		
		setLayout(new BorderLayout(0, 0));
		comboFilterComponent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
//					Pattern p = Pattern.compile("^(.+|\\(.+)\\[(.*?)(\\]|\\]\\))");
//				    Matcher m = p.matcher(comboFilterComponent.getText());
//					comboLogicalOperator.setEnabled(!m.find() ? false : true);
					comboLogicalOperator.setEnabled(comboFilterComponent.getText().isEmpty() ? false : true);
				}
			}
		});
		
		panel = new JPanel();
		add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));
		
		actionPanel = new JPanel();
		panel.add(actionPanel, BorderLayout.NORTH);
		actionPanel.setLayout(new BorderLayout(0, 0));
		
		actionPanelNorth = new JPanel();
		actionPanel.add(actionPanelNorth, BorderLayout.NORTH);
		actionPanelNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnSave = new JButton("Save...");
		actionPanelNorth.add(btnSave);
		btnSave.setEnabled(false);
		
		btnOpen = new JButton("Open...");
		actionPanelNorth.add(btnOpen);
		
		btnExport = new JButton("Export...");
		btnExport.setEnabled(false);
		actionPanelNorth.add(btnExport);
		
		actionPanelSouth = new JPanel();
		actionPanel.add(actionPanelSouth, BorderLayout.CENTER);
		actionPanelSouth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnLoadFromClipboard = new JButton("Load from clipboard");
		btnLoadFromClipboard.setPreferredSize(new Dimension(225, 23));
		actionPanelSouth.add(btnLoadFromClipboard);
		
		resultListPanel = new JPanel();
		resultListPanel.setBorder(new TitledBorder(null, "Result list", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(resultListPanel, BorderLayout.CENTER);
		resultListPanel.setLayout(new BorderLayout(0, 0));
		
		JList<String> list = new JList<String>();
		
		JScrollPane scrollPaneList = new JScrollPane();
		resultListPanel.add(scrollPaneList);
		scrollPaneList.setPreferredSize(new Dimension(1, 1));
		
		scrollPaneList.setViewportView(list);
		
		rulesPanel = new JPanel();
		add(rulesPanel, BorderLayout.CENTER);
		rulesPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel tablePanel = new JPanel();
		rulesPanel.add(tablePanel, BorderLayout.CENTER);
		tablePanel.setLayout(new BorderLayout(0, 0));
		tablePanel.setBorder(new TitledBorder(null, "Rules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JScrollPane scrollPaneTable = new JScrollPane();
		tablePanel.add(scrollPaneTable);
		
		table = new JTable();
		
		scrollPaneTable.setViewportView(table);
		
		JPanel infoPanel = new JPanel();
		tablePanel.add(infoPanel, BorderLayout.SOUTH);
		infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblTotalRules = new JLabel("Total Rules:");
		infoPanel.add(lblTotalRules);
		
		lblTotalRulesValue = new JLabel("");
		infoPanel.add(lblTotalRulesValue);
		
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(2, 15));
		separator.setOrientation(SwingConstants.VERTICAL);
		infoPanel.add(separator);
		
		JLabel lblFilteredRules = new JLabel("Filtered Rules:");
		infoPanel.add(lblFilteredRules);
		
		lblFilteredRulesValue = new JLabel("");
		infoPanel.add(lblFilteredRulesValue);
		
		JPanel filterPanel = new JPanel();
		rulesPanel.add(filterPanel, BorderLayout.NORTH);
		filterPanel.setBorder(new TitledBorder(null, "Filter", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		filterPanel.setLayout(new BorderLayout(5, 3));
		
		JPanel filterPanelNorth = new JPanel();
		filterPanel.add(filterPanelNorth, BorderLayout.NORTH);
		FlowLayout fl_filterPanelNorth = new FlowLayout(FlowLayout.LEFT, 5, 0);
		filterPanelNorth.setLayout(fl_filterPanelNorth);
		
		comboLogicalOperator = new JComboBox<String>();
		comboLogicalOperator.setEnabled(false);
		comboLogicalOperator.setModel(new DefaultComboBoxModel<String>(new String[] {"AND", "OR"}));
		filterPanelNorth.add(comboLogicalOperator);
		
		comboTableColumn = new JComboBox<ComboBoxItem>();
		comboTableColumn.setEnabled(false);
		comboTableColumn.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (comboTableColumn.getSelectedIndex() > -1) {
					comboAttribute.removeAllItems();
					int key = (int) ((ComboBoxItem) comboTableColumn.getSelectedItem()).getKey();
					for (FilterMapAttribute f : filterMapList[key].getAttributes()) {
						comboAttribute.addItem(new ComboBoxItem(f, f.getAttribute()));
					}
				}
			}
		});
		filterPanelNorth.add(comboTableColumn);
		
		comboComparisonOperator = new JComboBox<String>();
		comboComparisonOperator.setEnabled(false);
		comboComparisonOperator.setModel(new DefaultComboBoxModel<String>(new String[] {"CONTAINS", "EQUALS"}));
		filterPanelNorth.add(comboComparisonOperator);
		
		comboAttribute = new JComboBox<ComboBoxItem>();
		comboAttribute.setEnabled(false);
		comboAttribute.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (comboAttribute.getSelectedIndex() > -1) {
					comboValue.removeAllItems();
					comboValue.addItem("All");
					FilterMapAttribute selected = (FilterMapAttribute) ((ComboBoxItem) comboAttribute.getSelectedItem()).getKey();
					for (String v : selected.getValues()) {
						comboValue.addItem(v);
					}
				}
			}
		});
		filterPanelNorth.add(comboAttribute);
		
		comboValue = new JComboBox<String>();
		comboValue.setEnabled(false);
		filterPanelNorth.add(comboValue);
		
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addFilter();
			}
		});
		btnAdd.setEnabled(false);
		filterPanelNorth.add(btnAdd);
		
		JPanel filterPanelSouth = new JPanel();
		filterPanel.add(filterPanelSouth, BorderLayout.SOUTH);
		filterPanelSouth.setLayout(new BoxLayout(filterPanelSouth, BoxLayout.X_AXIS));
		comboFilter.setEnabled(false);
		comboFilter.setEditable(true);
		comboFilter.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				comboLogicalOperator.setEnabled(comboFilterComponent.getText().isEmpty() ? false : true);
			}
		});
		filterPanelSouth.add(comboFilter);
		
		btnApply = new JButton("Apply");
		btnApply.setEnabled(false);
		filterPanelSouth.add(btnApply);
		
		btnClear = new JButton("Clear");
		btnClear.setEnabled(false);
		filterPanelSouth.add(btnClear);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboFilter.setSelectedIndex(-1);
				sorter.setRowFilter(null);
				lblFilteredRulesValue.setText(String.valueOf(table.getRowCount()));
				btnClear.setEnabled(false);
				comboLogicalOperator.setEnabled(false);
			}
		});
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filter = comboFilterComponent.getText().trim();
				if (filter.length() == 0) {
					sorter.setRowFilter(null);
				} else {
					System.out.println(filter);
					sorter.setRowFilter(buildFilter(filter));
					comboFilter.addItem(filter);
					comboFilter.setSelectedIndex(0);
					lblFilteredRulesValue.setText(String.valueOf(table.getRowCount()));
					btnClear.setEnabled(true);
					comboTableColumn.setSelectedIndex(0);
					comboComparisonOperator.setSelectedIndex(0);
					comboAttribute.setSelectedIndex(0);
					comboValue.setSelectedIndex(0);
				}
			}
		});

	}

	private void addFilter() {

		Pattern p;
	    Matcher m = null;
		String column = ((ComboBoxItem) comboTableColumn.getSelectedItem()).getValue();
		String comparisonOperator = (String) comboComparisonOperator.getSelectedItem();
		String logicalOperator = (String) comboLogicalOperator.getSelectedItem();
		String attribute = comboAttribute.getSelectedItem().toString();
		String value = (String) comboValue.getSelectedItem();
		String componentText = comboFilterComponent.getText();
		String filter = "";
		boolean found = false;
		
		filter += column;
		if (comparisonOperator.equals("EQUALS")) {
			p = Pattern.compile(column+"\\[\\^(.*?)\\$\\]");
		    m = p.matcher(componentText);
		    found = m.find();
		    if (found) {
				filter = "";
			} else {
				filter += "[^";
			}
		} else {
			filter += "[";
		}
	    filter += found ? "|" : "";
		filter += attribute;
		filter += (!value.equals("All")) ? "=" + value : "";
		if (value.equals("All") && comparisonOperator.equals("EQUALS")) {
			filter += "=(";
			int size = comboValue.getModel().getSize();
			for (int i = 1; i < size; i++) {
				filter += comboValue.getItemAt(i);
				filter += (i + 1) < size ? "|" : "";
			}
			filter += ")";
			filter += found ? "( |)" : "";
		}
		if (found) {
			int lastIndex = componentText.lastIndexOf(m.group());
			int start = componentText.indexOf("^", lastIndex);
			int end = componentText.indexOf("$", lastIndex);
			String content = componentText.substring(start+1, end);
			content = content.replaceAll("\\{(.*?)\\}", "");
			String[] splitContent = content.split("\\( \\|\\)\\|");
			if (splitContent.length > 1) {
				content = new StringBuilder(content).insert(content.length() - 1, filter).toString();
				content = new StringBuilder(content).insert(content.length(), "{"+(splitContent.length+1)+"}").toString();
			} else {
				content = "(" + content + "( |)";
				content += filter + "){2}";
			}
			comboFilterComponent.setText(new StringBuilder(componentText).replace(start+1, end, content).toString());
		} else {
			
			filter += comparisonOperator.equals("EQUALS") ? "$]" : "]";
			
			if (!componentText.contains(filter)) {
				if (!componentText.isEmpty()) {
					if (!componentText.contains(column + "[")) {
						String text = "";
						if (column.equals("X")) {
							text = "(" + filter + ") ==> (" + componentText + ")";
						} else  {
							text = "(" + componentText + ") ==> (" + filter + ")";
						}
						comboFilterComponent.setText(text);
					} else {
						filter = " " + logicalOperator + " " + filter;
						p = Pattern.compile("( |\\()"+column+"\\[(.*?)\\]\\)");
					    m = p.matcher(componentText);
					    if (m.find()) {
					    	int lastIndex = componentText.lastIndexOf(m.group());
					    	comboFilterComponent.setText(new StringBuilder(componentText).insert(lastIndex + m.group().length() - 1, filter).toString());
					    } else {
					    	comboFilterComponent.setText(componentText + filter);
					    }
					}
				} else {
					comboFilterComponent.setText(componentText + filter);
				}
			
			}
		
			comboLogicalOperator.setEnabled(true);
		}
		
	}

	/**
	 * Builds RowFilter joining each filter from list.
	 * The result is a compound of all individuals filters.
	 * 
	 * @param filter the string with content to filter
	 * @return the compound {@link RowFilter}
	 */
	private RowFilter<? super TableModel, ? super Integer> buildFilter(String filter) {

		RowFilter<Object, Object> rowFilter = RowFilter.regexFilter(".*");
		List<RowFilter<Object, Object>> filters = new ArrayList<>();
		List<String> operators = new ArrayList<>();
		
		/* The filter has similar form of an association rule. The symbol
		 * between LHS and RHS means an "AND" operator.
		 */
		filter = filter.replace(" ==> ", " AND ");
		
		/* Adds parentheses to prevent parsing errors */
		filter = "(" + filter + ")";
		
		/* Initalize list of filters used by parser */
		parseFilterList = new ArrayList<>();
		
		/* Parses content to filter */
		filterParser(filter, 0, false, 0);
		
		for (String f : parseFilterList) {
			
			String[] splitOr = f.split(" OR ");
			List<RowFilter<Object, Object>> filterList = new ArrayList<>();
			for (String itemOr : splitOr) {
				System.out.println("OR:" + itemOr);
				if (!itemOr.equals("")) {
					String[] splitAnd = itemOr.split(" AND ");
					if (splitAnd.length > 1) {
						List<RowFilter<Object, Object>> andFilters = new ArrayList<>();
						for (String itemAnd : splitAnd) {
							System.out.println("AND:" + itemAnd);
							if (!itemAnd.equals("")) {
								andFilters.add(filterItemParser(itemAnd));
							}
						}
						filterList.add(RowFilter.andFilter(andFilters));
					} else if (splitAnd.length == 1) {
						filterList.add(filterItemParser(itemOr));
					}
				}
			}
			if (splitOr.length > 1) {
				for (RowFilter<Object, Object> rr : filterList) {
					System.out.println(rr);
				}
				rowFilter = RowFilter.orFilter(filterList);
			} else if (splitOr.length == 1 && !f.trim().equals("AND")) {
				System.out.println("entrou aqui");
				for (RowFilter<Object, Object> rr : filterList) {
					System.out.println(rr);
				}
				rowFilter = RowFilter.andFilter(filterList);
			} else {
				operators.add(f.trim());
				filters.add(rowFilter);
				rowFilter = RowFilter.regexFilter(".*");
			}
			
		}
		
		if (operators.size() > 0) {
			filters.add(rowFilter);
			List<RowFilter<Object, Object>> tempList = new ArrayList<>();
			tempList.add(filters.get(0));
			for (int i = 0; i < operators.size(); i++) {
				tempList.add(filters.get(i+1));
				if (operators.get(i).equals("AND")) {
					rowFilter = RowFilter.andFilter(tempList);
				} else if (operators.get(i).equals("OR")) {
					rowFilter = RowFilter.orFilter(tempList);
				}
				tempList = new ArrayList<>();
				tempList.add(rowFilter);
			}
		}
		
		return rowFilter;
		
	}
	
	/**
	 * <p>A recursive method to parse filter string.</p>
	 * 
	 * <p>Search for parentheses (brackets) recursively and adds
	 * the content to list ordered from inner to outer.</p>
	 * 
	 * @param s the filter string
	 * @param i the incremental attribute to control char position in string
	 * @param move used to adjust filter position in list
	 * @param filterEnd end delimiter position of a filter; initial value must be 0
	 * @return the incremental attribute
	 */
	private int filterParser(String s, int i, boolean move, int filterEnd) {
		
		while (i < s.length()) {
			
			String current = String.valueOf(s.charAt(i));
			
			/* Look for filter's begin delimiter. */
			if (current.equals("[")) {
				
				boolean end = false;
				int pos = i;
				String quote = "";
				String currentTemp = current;
				
				/* Ends loop when end delimiter is found. */
				while (!end) {
					
					currentTemp = String.valueOf(s.charAt(pos));
					
					/* A quoted string must be escaped. */
					if (!quote.isEmpty()) {
						if (currentTemp.equals(quote)) {
							quote = "";
						}
					} else if (currentTemp.matches("(\'|\")")) {
						quote = currentTemp;
					}

					/* The filter end delimiter must be outer from quotation
					 * and is not an escaped string. */
					if (currentTemp.equals("]")
						&& !s.substring(pos-2, pos).equals("\\]")
						&& quote.isEmpty()) {
						end = true;
						filterEnd = pos;
					}
					
					pos++;
					
				}
				
			}
			
			if (current.equals("(") && filterEnd == 0) {
				if (!parseFilterTemp.isEmpty()) {
					Pattern p = Pattern.compile("^(X|Y)\\[(.*?)\\]( AND | OR )$");
					Matcher m = p.matcher(parseFilterTemp);
					if (m.find()) {
						String[] splitTemp = parseFilterTemp.split(" ");
						String strTemp = " ";
						for (int j = splitTemp.length - 1; j >= 0; j--) {
							strTemp += splitTemp[j] + " ";
						}
						parseFilterList.add(strTemp.replaceAll("\\s+$", ""));
						move = true;
					} else {
						parseFilterList.add(parseFilterTemp);
					}
					parseFilterTemp = "";
				}
				i = filterParser(s, i + 1, move, filterEnd);
			} else if (current.equals(")") && filterEnd == 0) {
				if (!parseFilterTemp.isEmpty()) {
					if (move) {
						parseFilterList.add(parseFilterList.size() - 1, parseFilterTemp);
						move = false;
					} else {
						parseFilterList.add(parseFilterTemp);
					}
					parseFilterTemp = "";
				}
				return i + 1;
			} else {
				parseFilterTemp += current;
				i += 1;
				filterEnd = (filterEnd == i) ? 0 : filterEnd;
			}
		}
		
		return i;
		
	}
	
	/**
	 * Parses filter's item string and creates a {@link RowFilter}
	 * with regex support.
	 * 
	 * @param filterItem the filter's item
	 * @return the {@link RowFilter} 
	 * @see RowFilter
	 */
	private RowFilter<Object, Object> filterItemParser(String filterItem) {
		
		String columnName = filterItem.substring(0, filterItem.indexOf("["));
		
		int beginIndex = filterItem.indexOf("[") + 1;
		int endIndex = filterItem.lastIndexOf("]");
		String regex = filterItem.substring(beginIndex, endIndex);
		
		int quoteBegin = Utils.indexOf("(\'|\")", regex);
		if (quoteBegin > -1) {
			String quoteString = String.valueOf(regex.charAt(quoteBegin));
			int quoteEnd = regex.lastIndexOf(quoteString);
			StringBuilder buildRegex = new StringBuilder(regex);
			regex = buildRegex.insert(quoteBegin + 1, "\\Q").toString();
			regex = buildRegex.insert(quoteEnd + 2, "\\E").toString();
		}
		System.out.println(regex);
		
		int index = columnName.equals("X") ? 0 : 1;
		
		return RowFilter.regexFilter(regex, index);
		
	}

	/**
	 * Loads rules into a JTable
	 *
	 * @param  rules  the association rules
	 */
	public static void loadRules(AssociationRules rules) {
		
		List<AssociationRule> rulesList = rules.getRules();
		
		List<String> tableHead = new ArrayList<String>();
		tableHead.add("Antecedent (X)");
		tableHead.add("Consequent (Y)");
		tableHead.add("Support");
		
		String[] metrics = rulesList.get(0).getMetricNamesForRule();
		for (String m : metrics) {
			tableHead.add(m);
		}
		
		/* Set FilterMap instance for antecedent and consquent columns */
		filterMapList = new FilterMap[2];
		filterMapList[0] = new FilterMap();
		filterMapList[1] = new FilterMap();
		
		table.setModel(
			new DefaultTableModel(
				new Object[][]{},
				tableHead.toArray()
			){

				/**
				 * 
				 */
				private static final long serialVersionUID = -535403917799608179L;
				
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					
					Class<?> columnClass = null;
					
					if ((columnIndex >= 0) && (columnIndex < getColumnCount())) {
						columnClass = getValueAt(0, columnIndex).getClass();
					} else {
						columnClass = Object.class;
					}
					
					return columnClass;
					
				}
				
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
				
			}
		);
		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);
		
		for (AssociationRule r : rulesList) {
	
			String antecedent = "";
			for (Item p : r.getPremise()) {
				antecedent += p + " ";
				filterMapList[0].addAttributeValue(p.toString());
			}
			
			String consequent = "";
			for (Item c : r.getConsequence()) {		
				consequent += c + " ";
				filterMapList[1].addAttributeValue(c.toString());	
			}

			double support = ((double) r.getTotalSupport()) / r.getTotalTransactions();
			
			List<Object> values = new ArrayList<Object>();
			values.add(antecedent.trim());
			values.add(consequent.trim());
			values.add(support);
			
			for (String m: metrics) {
				try {
					values.add(r.getNamedMetricValue(m));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			model.addRow(values.toArray());
			
		}
		
		lblTotalRulesValue.setText(String.valueOf(rulesList.size()));
		lblFilteredRulesValue.setText(String.valueOf(table.getRowCount()));
		
		comboTableColumn.removeAllItems();
		comboTableColumn.addItem(new ComboBoxItem(0, "Antecedent (X)", "X"));
		comboTableColumn.addItem(new ComboBoxItem(1, "Consequent (Y)", "Y"));
		
		comboTableColumn.setEnabled(true);
		comboComparisonOperator.setEnabled(true);
		comboAttribute.setEnabled(true);
		comboValue.setEnabled(true);
		comboFilter.setEnabled(true);
		btnAdd.setEnabled(true);
		btnApply.setEnabled(true);
		btnClear.setEnabled(true);
		
	}

	/** Unused */
	@Override
	public void setInstances(Instances arg0) {}

	/**
	 * Sets the Explorer to use as parent frame
	 * 
	 * @param parent the parent frame
	 */
	@Override
	public void setExplorer(Explorer parent) {
		m_Explorer = parent;
	}

	/**
	 * Returns the parent Explorer frame
	 * 
	 * @return the parent frame
	 */
	@Override
	public Explorer getExplorer() {
		return m_Explorer;
	}

	/**
	 * Returns the title for the tab in the Explorer
	 * 
	 * @return the tab title
	 */
	@Override
	public String getTabTitle() {
		return "Postprocess associations";
	}

	/** 
	 * Returns the tooltip for the tab in the Explorer
	 * 
	 * @return the tab tooltip
	 */
	@Override
	public String getTabTitleToolTip() {
		return "Load/Filter/Save associator output";
	}

	/* (non-Javadoc)
	 * @see weka.gui.explorer.Explorer.LogHandler#setLog(weka.gui.Logger)
	 */
	@Override
	public void setLog(Logger newLog) {
		// TODO Auto-generated method stub
		
	}

}

package weka.gui.explorer;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowFilter.ComparisonType;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import weka.associations.AssociationRule;
import weka.associations.AssociationRules;
import weka.associations.Item;
import weka.core.Instances;
import weka.gui.JTableHelper;
import weka.gui.Logger;
import weka.gui.SysErrLog;
import weka.gui.explorer.Explorer.ExplorerPanel;
import weka.gui.explorer.Explorer.LogHandler;
import weka.gui.scripting.JythonScript;
import arpp.ComboBoxItem;
import arpp.DecimalFormatRenderer;
import arpp.FilterComboBox;
import arpp.FilterMap;
import arpp.FilterMapAttribute;
import arpp.MetricSpinner;
import arpp.ProgressTableCellRenderer;
import arpp.RulesTableColumnModel;
import arpp.RulesTableModel;
import arpp.Utils;

import java.awt.GridLayout;
import java.awt.Component;

import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.UIManager;

import java.awt.SystemColor;

/**
 * A JPanel to visualize association rules
 *
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class PostprocessAssociationsPanel extends JPanel implements ExplorerPanel, LogHandler {

	/** for serialization */
	private static final long serialVersionUID = 3114490118265884877L;
	
	/** The parent frame */
	protected Explorer explorer = null;
		
	/** The destination for log/status messages */
	protected static Logger log = new SysErrLog();

	/** Component where rules are loaded */
	protected static JTable table;
	
	protected static TableRowSorter<TableModel> sorter;
	
	protected static List<String> parseFilterList;
	protected static String parseFilterTemp = "";
	
	protected static FilterMap[] filterMapList;
	
	protected static JLabel lblTotalRulesValue;
	
	protected static JLabel lblFilteredRulesValue;
	
	protected static JComboBox<String> comboLogicalOperator;
	
	protected static JComboBox<ComboBoxItem> comboTableColumn;
	
	protected static JComboBox<String> comboComparisonType;
	
	protected static JComboBox<ComboBoxItem> comboAttribute;
	
	protected static JComboBox<String> comboLabel;

	protected static JButton btnAdd;
	
	protected static FilterComboBox comboFilter = new FilterComboBox();
	
	protected static JTextField comboFilterComponent = (JTextField) comboFilter.getEditor().getEditorComponent();
	
	protected static JButton btnApply;
	
	protected static JButton btnClear;
	private JPanel rulesPanel;
	private JPanel buttonsPanel;
	protected JPanel filterPanel;
	protected JPanel tablePanel;
	protected static JButton btnSave;
	private JButton btnOpen;
	protected static JButton btnExport;

	private static AssociationRules associationRules;
	private JButton btnLoadFromClipboard;
	
	/** The file chooser for save/open rules and applied filters. */
	private JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
	private JPanel rulesFilterPanel;
	private JPanel metricsFilterPanel;
	
	/** JCheckBox for metric Support */
	private static JCheckBox chckbxSupport;
	
	/** JCheckBox for metric Confidence */
	private static JCheckBox chckbxConfidence;
	
	/** JCheckBox for metric Lift */
	private static JCheckBox chckbxLift;
	
	/** JCheckBox for metric Leverage */
	private static JCheckBox chckbxLeverage;
	
	/** JCheckBox for metric Conviction */
	private static JCheckBox chckbxConviction;
	
	/** JSpinner for metric Support */
	private static JSpinner spinSupport;
	
	/** JSpinner for metric Confidence */
	private static JSpinner spinConfidence;
	
	/** JSpinner for metric Lift */
	private static JSpinner spinLift;
	
	/** JSpinner for metric Leverage */
	private static JSpinner spinLeverage;
	
	/** JSpinner for metric Conviction */
	private static JSpinner spinConviction;
	
	/** HashMap for metric's JSpiner components */
	private static HashMap<String, JSpinner> metricSpinnerMap = new HashMap<>();
	
	/** Reset metric's minimum values */
	private static JButton btnReset;
	private JPanel panel;
	private JLabel lblLogicalOperator;
	private JLabel lblXy;
	private JLabel lblComparisonType;
	private JLabel lblAttribute;
	private JLabel lblLabel;
	private JPanel rulesFilterNorth;
	private JPanel rulesFilterSouth;
	private JPanel logicalOperatorPanel;
	private JPanel tableColumnPanel;
	private JPanel comparisonTypePanel;
	private JPanel attributePanel;
	private JPanel labelPanel;
	private JPanel addPanel;
	private JLabel lblAdd;

	/**
	 * Create the postprocess panel.
	 */
	public PostprocessAssociationsPanel() {
				
		setLayout(new BorderLayout(0, 0));
		comboFilterComponent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
					comboLogicalOperator.setEnabled(comboFilterComponent.getText().isEmpty() ? false : true);
				}
			}
		});
		
		rulesPanel = new JPanel();
		add(rulesPanel, BorderLayout.CENTER);
		rulesPanel.setLayout(new BorderLayout(0, 0));
		
		filterPanel = new JPanel();
		rulesPanel.add(filterPanel, BorderLayout.NORTH);
		filterPanel.setLayout(new BorderLayout(2, 0));
		
		panel = new JPanel();
		filterPanel.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		buttonsPanel = new JPanel();
		buttonsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(buttonsPanel, BorderLayout.NORTH);
		buttonsPanel.setLayout(new GridLayout(1, 4, 5, 5));
		
		btnSave = new JButton("Save...");
		buttonsPanel.add(btnSave);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveRules();
			}
		});
		btnSave.setEnabled(false);
		
		btnOpen = new JButton("Open...");
		buttonsPanel.add(btnOpen);
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openRules();
			}
		});
		
		btnLoadFromClipboard = new JButton("Load from clipboard");
//		btnLoadFromClipboard.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				Toolkit toolkit = Toolkit.getDefaultToolkit();
//				Clipboard clipboard = toolkit.getSystemClipboard();
//				try {
//					String result = (String) clipboard.getData(DataFlavor.stringFlavor);
////					AssociationRule rule;
////					List<AssociationRule> rulesList = new ArrayList<>();
//					Pattern rulePattern = Pattern.compile("(\\d+).\\s+(.+)\\s+(\\d+)\\s+==>\\s+(.+)\\s+(\\d+)\\s+(.*)");
//					Pattern p;
//					Matcher m;
//					String line;
//					Scanner scanner = new Scanner(result);
//					while (scanner.hasNextLine()) {
//						line = (String) scanner.nextLine();
//						m = rulePattern.matcher(line);
//						if (m.find()) {
//							p = Pattern.compile("(\\d. )(.+)( \\d+) ==>");
//							m = p.matcher(line);
//							if (m.find()) {
//								String premisse = m.group(2);
//								
//								String[] consequent;
//							}
//						}
//						
//					}
//					scanner.close();
////					AssociationRules rules = new AssociationRules(rulesList);
//				} catch (UnsupportedFlavorException | IOException ex) {
//					ex.printStackTrace();
//				}
//			}
//		});
		buttonsPanel.add(btnLoadFromClipboard);
		
		btnExport = new JButton("Export...");
		buttonsPanel.add(btnExport);
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportToCsv();
			}
		});
		btnExport.setEnabled(false);
		
		rulesFilterPanel = new JPanel();
		panel.add(rulesFilterPanel, BorderLayout.SOUTH);
		rulesFilterPanel.setBorder(new TitledBorder(null, "Filter for rules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rulesFilterPanel.setLayout(new BorderLayout(0, 0));
		
		rulesFilterNorth = new JPanel();
		rulesFilterPanel.add(rulesFilterNorth, BorderLayout.NORTH);
		
		logicalOperatorPanel = new JPanel();
		logicalOperatorPanel.setLayout(new BoxLayout(logicalOperatorPanel, BoxLayout.Y_AXIS));
		
		lblLogicalOperator = new JLabel("Logical Operator:");
		lblLogicalOperator.setAlignmentX(Component.RIGHT_ALIGNMENT);
		lblLogicalOperator.setVerticalAlignment(SwingConstants.TOP);
		logicalOperatorPanel.add(lblLogicalOperator);
		
		comboLogicalOperator = new JComboBox<String>();
		lblLogicalOperator.setLabelFor(comboLogicalOperator);
		logicalOperatorPanel.add(comboLogicalOperator);
		comboLogicalOperator.setEnabled(false);
		comboLogicalOperator.setModel(new DefaultComboBoxModel<String>(new String[] {"AND", "OR"}));
		FlowLayout fl_rulesFilterNorth = new FlowLayout(FlowLayout.LEFT, 5, 5);
		rulesFilterNorth.setLayout(fl_rulesFilterNorth);
		rulesFilterNorth.add(logicalOperatorPanel);
		
		tableColumnPanel = new JPanel();
		rulesFilterNorth.add(tableColumnPanel);
		tableColumnPanel.setLayout(new BoxLayout(tableColumnPanel, BoxLayout.Y_AXIS));
		
		lblXy = new JLabel("Antecedent / Consequent:");
		tableColumnPanel.add(lblXy);
		lblXy.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblXy.setLabelFor(comboTableColumn);
		
		comboTableColumn = new JComboBox<ComboBoxItem>();
		tableColumnPanel.add(comboTableColumn);
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
		
		comparisonTypePanel = new JPanel();
		rulesFilterNorth.add(comparisonTypePanel);
		comparisonTypePanel.setLayout(new BoxLayout(comparisonTypePanel, BoxLayout.Y_AXIS));
		
		lblComparisonType = new JLabel("Comparison Type:");
		lblComparisonType.setAlignmentX(Component.CENTER_ALIGNMENT);
		comparisonTypePanel.add(lblComparisonType);
		
		comboComparisonType = new JComboBox<String>();
		comparisonTypePanel.add(comboComparisonType);
		comboComparisonType.setEnabled(false);
		comboComparisonType.setModel(new DefaultComboBoxModel<String>(new String[] {"CONTAINS", "EQUALS"}));
		
		attributePanel = new JPanel();
		attributePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rulesFilterNorth.add(attributePanel);
		attributePanel.setLayout(new BoxLayout(attributePanel, BoxLayout.Y_AXIS));
		
		lblAttribute = new JLabel("Attribute:");
		attributePanel.add(lblAttribute);
		
		comboAttribute = new JComboBox<ComboBoxItem>();
		comboAttribute.setAlignmentX(Component.LEFT_ALIGNMENT);
		attributePanel.add(comboAttribute);
		comboAttribute.setEnabled(false);
		comboAttribute.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (comboAttribute.getSelectedIndex() > -1) {
					comboLabel.removeAllItems();
					comboLabel.addItem("All");
					FilterMapAttribute selected = (FilterMapAttribute) ((ComboBoxItem) comboAttribute.getSelectedItem()).getKey();
					for (String v : selected.getValues()) {
						comboLabel.addItem(v);
					}
				}
			}
		});
		
		labelPanel = new JPanel();
		rulesFilterNorth.add(labelPanel);
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		
		lblLabel = new JLabel("Label:");
		labelPanel.add(lblLabel);
		
		comboLabel = new JComboBox<String>();
		comboLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelPanel.add(comboLabel);
		comboLabel.setEnabled(false);
		
		addPanel = new JPanel();
		rulesFilterNorth.add(addPanel);
		addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.Y_AXIS));
		
		lblAdd = new JLabel("Add");
		lblAdd.setForeground(SystemColor.menu);
		addPanel.add(lblAdd);
		
		btnAdd = new JButton("Add");
		addPanel.add(btnAdd);
		btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addFilter();
			}
		});
		btnAdd.setEnabled(false);
		
		rulesFilterSouth = new JPanel();
		rulesFilterPanel.add(rulesFilterSouth);
		rulesFilterSouth.setLayout(new BoxLayout(rulesFilterSouth, BoxLayout.X_AXIS));
		rulesFilterSouth.add(comboFilter);
		comboFilter.setEnabled(false);
		comboFilter.setEditable(true);
		
		btnApply = new JButton("Apply");
		rulesFilterSouth.add(btnApply);
		btnApply.setEnabled(false);
		
		btnClear = new JButton("Clear");
		rulesFilterSouth.add(btnClear);
		btnClear.setEnabled(false);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboFilter.setSelectedIndex(-1);
				sorter.setRowFilter(null);
				lblFilteredRulesValue.setText(String.valueOf(table.getRowCount()));
				comboLogicalOperator.setEnabled(false);
			}
		});
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filter = comboFilterComponent.getText().trim();
				if (filter.length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(buildRulesFilter(filter));
					comboFilter.addItem(filter);
					comboFilter.setSelectedIndex(0);
					lblFilteredRulesValue.setText(String.valueOf(table.getRowCount()));
					comboTableColumn.setSelectedIndex(0);
					comboComparisonType.setSelectedIndex(0);
					comboAttribute.setSelectedIndex(0);
					comboLabel.setSelectedIndex(0);
				}
			}
		});
		comboFilter.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				comboLogicalOperator.setEnabled(comboFilterComponent.getText().isEmpty() ? false : true);
			}
		});
		
		metricsFilterPanel = new JPanel();
		metricsFilterPanel.setBorder(new TitledBorder(null, "Minimum values for metrics", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		filterPanel.add(metricsFilterPanel, BorderLayout.EAST);
		GridBagLayout gbl_metricsFilterPanel = new GridBagLayout();
		gbl_metricsFilterPanel.columnWidths = new int[] {90, 70, 90, 70};
		gbl_metricsFilterPanel.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0};
		gbl_metricsFilterPanel.rowWeights = new double[]{1.0, 1.0, 1.0};
		metricsFilterPanel.setLayout(gbl_metricsFilterPanel);
		
		chckbxSupport = new JCheckBox("Support");
		chckbxSupport.setEnabled(false);
		chckbxSupport.setSelected(true);
		GridBagConstraints gbc_chckbxSupport = new GridBagConstraints();
		gbc_chckbxSupport.fill = GridBagConstraints.BOTH;
		gbc_chckbxSupport.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxSupport.gridx = 0;
		gbc_chckbxSupport.gridy = 0;
		metricsFilterPanel.add(chckbxSupport, gbc_chckbxSupport);
		
		spinSupport = new MetricSpinner();
		spinSupport.setPreferredSize(new Dimension(29, 25));
		spinSupport.setEnabled(false);
		spinSupport.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(0.01)));
		GridBagConstraints gbc_spinSupport = new GridBagConstraints();
		gbc_spinSupport.insets = new Insets(0, 0, 0, 5);
		gbc_spinSupport.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinSupport.gridx = 1;
		gbc_spinSupport.gridy = 0;
		metricsFilterPanel.add(spinSupport, gbc_spinSupport);
		metricSpinnerMap.put("Support", spinSupport);
		
		chckbxConfidence = new JCheckBox("Confidence");
		chckbxConfidence.setEnabled(false);
		chckbxConfidence.setSelected(true);
		GridBagConstraints gbc_chckbxConfidence = new GridBagConstraints();
		gbc_chckbxConfidence.fill = GridBagConstraints.BOTH;
		gbc_chckbxConfidence.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxConfidence.gridx = 0;
		gbc_chckbxConfidence.gridy = 1;
		metricsFilterPanel.add(chckbxConfidence, gbc_chckbxConfidence);
		
		spinConfidence = new MetricSpinner();
		spinConfidence.setPreferredSize(new Dimension(29, 25));
		spinConfidence.setEnabled(false);
		spinConfidence.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(0.01)));
		GridBagConstraints gbc_spinConfidence = new GridBagConstraints();
		gbc_spinConfidence.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinConfidence.insets = new Insets(0, 0, 0, 5);
		gbc_spinConfidence.gridx = 1;
		gbc_spinConfidence.gridy = 1;
		metricsFilterPanel.add(spinConfidence, gbc_spinConfidence);
		metricSpinnerMap.put("Confidence", spinConfidence);
		
		chckbxLift = new JCheckBox("Lift");
		chckbxLift.setEnabled(false);
		chckbxLift.setSelected(true);
		GridBagConstraints gbc_chckbxLift = new GridBagConstraints();
		gbc_chckbxLift.fill = GridBagConstraints.BOTH;
		gbc_chckbxLift.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxLift.gridx = 0;
		gbc_chckbxLift.gridy = 2;
		metricsFilterPanel.add(chckbxLift, gbc_chckbxLift);
		
		spinLift = new MetricSpinner();
		spinLift.setPreferredSize(new Dimension(29, 25));
		spinLift.setEnabled(false);
		spinLift.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(0.01)));
		GridBagConstraints gbc_spinLift = new GridBagConstraints();
		gbc_spinLift.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinLift.insets = new Insets(0, 0, 0, 5);
		gbc_spinLift.gridx = 1;
		gbc_spinLift.gridy = 2;
		metricsFilterPanel.add(spinLift, gbc_spinLift);
		metricSpinnerMap.put("Lift", spinLift);
		
		chckbxLeverage = new JCheckBox("Leverage");
		chckbxLeverage.setEnabled(false);
		chckbxLeverage.setSelected(true);
		GridBagConstraints gbc_chckbxLeverage = new GridBagConstraints();
		gbc_chckbxLeverage.fill = GridBagConstraints.BOTH;
		gbc_chckbxLeverage.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxLeverage.gridx = 2;
		gbc_chckbxLeverage.gridy = 0;
		metricsFilterPanel.add(chckbxLeverage, gbc_chckbxLeverage);
		
		spinLeverage = new MetricSpinner();
		spinLeverage.setPreferredSize(new Dimension(29, 25));
		spinLeverage.setEnabled(false);
		spinLeverage.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(0.01)));
		GridBagConstraints gbc_spinLeverage = new GridBagConstraints();
		gbc_spinLeverage.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinLeverage.insets = new Insets(0, 0, 0, 5);
		gbc_spinLeverage.gridx = 3;
		gbc_spinLeverage.gridy = 0;
		metricsFilterPanel.add(spinLeverage, gbc_spinLeverage);
		metricSpinnerMap.put("Leverage", spinLeverage);
		
		chckbxConviction = new JCheckBox("Conviction");
		chckbxConviction.setEnabled(false);
		chckbxConviction.setSelected(true);
		GridBagConstraints gbc_chckbxConviction = new GridBagConstraints();
		gbc_chckbxConviction.fill = GridBagConstraints.BOTH;
		gbc_chckbxConviction.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxConviction.gridx = 2;
		gbc_chckbxConviction.gridy = 1;
		metricsFilterPanel.add(chckbxConviction, gbc_chckbxConviction);
		
		spinConviction = new MetricSpinner();
		spinConviction.setPreferredSize(new Dimension(29, 25));
		spinConviction.setEnabled(false);
		spinConviction.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(0.01)));
		GridBagConstraints gbc_spinConviction = new GridBagConstraints();
		gbc_spinConviction.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinConviction.insets = new Insets(0, 0, 0, 5);
		gbc_spinConviction.gridx = 3;
		gbc_spinConviction.gridy = 1;
		metricsFilterPanel.add(spinConviction, gbc_spinConviction);
		metricSpinnerMap.put("Conviction", spinConviction);
		
		btnReset = new JButton("Reset");
		btnReset.setEnabled(false);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetMinMetricValues();
			}
		});
		GridBagConstraints gbc_btnReset = new GridBagConstraints();
		gbc_btnReset.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnReset.gridwidth = 2;
		gbc_btnReset.insets = new Insets(0, 0, 0, 5);
		gbc_btnReset.gridx = 2;
		gbc_btnReset.gridy = 2;
		metricsFilterPanel.add(btnReset, gbc_btnReset);
		
		tablePanel = new JPanel();
		rulesPanel.add(tablePanel, BorderLayout.CENTER);
		tablePanel.setLayout(new BorderLayout(0, 0));
		tablePanel.setBorder(new TitledBorder(null, "Rules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel infoPanel = new JPanel();
		tablePanel.add(infoPanel, BorderLayout.NORTH);
		infoPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JLabel lblTotalRules = new JLabel("Total Rules:");
		infoPanel.add(lblTotalRules);
		
		lblTotalRulesValue = new JLabel("0");
		infoPanel.add(lblTotalRulesValue);
		
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(2, 15));
		separator.setOrientation(SwingConstants.VERTICAL);
		infoPanel.add(separator);
		
		JLabel lblFilteredRules = new JLabel("Filtered Rules:");
		infoPanel.add(lblFilteredRules);
		
		lblFilteredRulesValue = new JLabel("0");
		infoPanel.add(lblFilteredRulesValue);
		
		JScrollPane scrollPaneTable = new JScrollPane();
		tablePanel.add(scrollPaneTable);
		
		table = new JTable();
		
		scrollPaneTable.setViewportView(table);

	}

	private void exportToCsv() {

		String csvData = "";
		FileFilter fileFilter = new FileNameExtensionFilter("CSV file: comma separated files", new String[] {"csv"});
//		fileChooser.get
		fileChooser.addChoosableFileFilter(fileFilter);
		fileChooser.setFileFilter(fileFilter);
//		fileChooser.setSelectedFile(new File("teste.csv"));
		int option = fileChooser.showSaveDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			String fileName = file.getName();
			if (!fileName.endsWith(".csv")) {
				fileName += ".csv";
			}
			
			List<String> visibleColumns = new ArrayList<>();
			TableModel tableModel = table.getModel();
			int columnCount = tableModel.getColumnCount();
			int modelRowCount = tableModel.getRowCount();
			int tableRowCount = table.getRowCount();
			
			Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
			while (columns.hasMoreElements()) {
				TableColumn tableColumn = (TableColumn) columns.nextElement();
				visibleColumns.add(tableColumn.getHeaderValue().toString());
			}
			
			for (int i = 0; i < columnCount; i++) {
				csvData += i > 0 ? "," : "";
				String columnName = tableModel.getColumnName(i);
				if (visibleColumns.contains(columnName)) {
					csvData += "\"" + columnName + "\"";
				}
				if (csvData.endsWith(",")) {
					csvData = csvData.substring(0, csvData.length() - 1);
				}
			}
			csvData += "\r\n";
			for (int i = 0; i < tableRowCount; i++) {
				for (int j = 0; j < columnCount; j++) {
					csvData += j > 0 ? "," : "";
					String columnName = tableModel.getColumnName(j);
					if (visibleColumns.contains(columnName)) {
						if (tableRowCount < modelRowCount) {
							csvData += "\"" + tableModel.getValueAt(table.convertColumnIndexToModel(i), j) + "\"";
						} else {
							csvData += "\"" + tableModel.getValueAt(i, j) + "\"";
						}
					}
					if (csvData.endsWith(",")) {
						csvData = csvData.substring(0, csvData.length() - 1);
					}
				}
				csvData += "\r\n";
			}
			try {
				FileWriter w = new FileWriter(file);
				w.write(csvData);
				w.flush();
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	private static void onMetricValueChange() {

		/* Lists for filters */
		List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>();
		
		/* Rule's filter */
		RowFilter<Object, Object> rulesFilter = (RowFilter<Object, Object>) buildRulesFilter(comboFilterComponent.getText().trim());
		if (rulesFilter != null) {
			filters.add(rulesFilter);
		}
		
		/* Filter for metrics */
		RowFilter<Object, Object> metricsFilter = (RowFilter<Object, Object>) buildMetricsFilter();
		filters.add(metricsFilter);
		
		/* Join rule and metric filters */
		RowFilter<Object, Object> andFilter = RowFilter.andFilter(filters);
		
		/* Apply filter to table */
		sorter.setRowFilter(andFilter);
		
		/* Update counting of filtered values */
		lblFilteredRulesValue.setText(String.valueOf(table.getRowCount()));
		
	}

	private static RowFilter<? super TableModel, ? super Integer> buildMetricsFilter() {

		RowFilter<Object, Object> rowFilter = RowFilter.regexFilter(".*");
		List<RowFilter<Object, Object>> metricFilters = new ArrayList<RowFilter<Object, Object>>();
		List<RowFilter<Object, Object>> metricFilter;
		RowFilter<Object, Object> filterEqual;
		RowFilter<Object, Object> filterAfter;
		double value;
		int columnIndex;
		
		for (Map.Entry<String, JSpinner> entry : metricSpinnerMap.entrySet()) {
			metricFilter = new ArrayList<RowFilter<Object, Object>>();
			value = (double) (entry.getValue()).getValue();
			columnIndex = table.getColumnModel().getColumnIndex(entry.getKey());
			filterEqual = RowFilter.numberFilter(ComparisonType.EQUAL, value, table.convertColumnIndexToModel(columnIndex));
			filterAfter = RowFilter.numberFilter(ComparisonType.AFTER, value, table.convertColumnIndexToModel(columnIndex));
			metricFilter.add(filterEqual);
			metricFilter.add(filterAfter);
			metricFilters.add(RowFilter.orFilter(metricFilter));
		}
		
		rowFilter = RowFilter.andFilter(metricFilters);
		
		return rowFilter;
		
	}

	private static void onMetricVisibilityChange(ActionEvent e) {

		JCheckBox chckbx = (JCheckBox) e.getSource();
		String metricName = chckbx.getText();
		MetricSpinner spinner = (MetricSpinner) metricSpinnerMap.get(metricName);
		RulesTableColumnModel columnModel = (RulesTableColumnModel) table.getColumnModel();
		
		if (spinner != null) {
			spinner.setEnabled(chckbx.isSelected());
			spinner.reset();
		}
		if (chckbx.isSelected()) {
			columnModel.showColumn(metricName);
			if (!sorter.getSortKeys().isEmpty()) {
				int keyColumn = sorter.getSortKeys().get(0).getColumn();
				if (keyColumn < columnModel.getColumnCount()) {
					if (!table.getColumnName(keyColumn).equals(metricName)) {
						int columnIndex = columnModel.getColumnIndex(metricName);
						columnModel.getColumn(columnIndex).setCellRenderer(new DecimalFormatRenderer());
					}
				}
			}
		} else {
			columnModel.hideColumn(metricName);
		}
		
	}

	private void resetMinMetricValues() {
		
		for (Map.Entry<String, JSpinner> entry : metricSpinnerMap.entrySet()) {
			((MetricSpinner) entry.getValue()).reset();
		}
		
	}

	@SuppressWarnings("unchecked")
	private void openRules() {

		int option = fileChooser.showSaveDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);	
				associationRules = (AssociationRules) ois.readObject();
				loadRules(associationRules);
				comboFilter.setModel((ComboBoxModel<Object>) ois.readObject());
				ois.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void saveRules() {

		int option = fileChooser.showSaveDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				FileOutputStream fos = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(associationRules);
				oos.writeObject(comboFilter.getModel());
				oos.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void addFilter() {

		Pattern p;
	    Matcher m = null;
		String column = ((ComboBoxItem) comboTableColumn.getSelectedItem()).getValue();
		String comparisonOperator = (String) comboComparisonType.getSelectedItem();
		String logicalOperator = (String) comboLogicalOperator.getSelectedItem();
		String attribute = comboAttribute.getSelectedItem().toString();
		String value = (String) comboLabel.getSelectedItem();
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
			int size = comboLabel.getModel().getSize();
			for (int i = 1; i < size; i++) {
				filter += comboLabel.getItemAt(i);
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
							text = "(" + filter + ") ==> (" + componentText.replaceAll("(=+)>", "").trim() + ")";
						} else  {
							text = "(" + componentText.replaceAll("(=+)>", "").trim() + ") ==> (" + filter + ")";
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
	private static RowFilter<? super TableModel, ? super Integer> buildRulesFilter(String filter) {

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
				rowFilter = RowFilter.orFilter(filterList);
			} else if (splitOr.length == 1 && !f.trim().equals("AND")) {
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
	private static int filterParser(String s, int i, boolean move, int filterEnd) {
		
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
	 * Parses filter's item and creates a {@link RowFilter}
	 * with regex support.
	 * 
	 * @param filterItem the filter's item
	 * @return the {@link RowFilter} 
	 * @see RowFilter
	 */
	private static RowFilter<Object, Object> filterItemParser(String filterItem) {
		
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
		
		int index = columnName.equals("X") ? 0 : 1;
		
		return RowFilter.regexFilter(regex, index);
		
	}

	/**
	 * Loads rules into a JTable
	 *
	 * @param  rules  the association rules
	 */
	public static void loadRules(AssociationRules rules) {
		
		table.setModel(new RulesTableModel());
		table.setColumnModel(new RulesTableColumnModel());
		
		associationRules = rules;
		
		List<AssociationRule> rulesList = rules.getRules();
		
		/* Get table model */
		final DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

		/* Metric names */
		String[] metrics = rulesList.get(0).getMetricNamesForRule();
		
		/* Columns */
		tableModel.addColumn("Antecedent (X)");
		tableModel.addColumn("Consequent (Y)");
		tableModel.addColumn("Support");
		for (String m : metrics) {
			tableModel.addColumn(m);
		}
		
		/* Get column model */
		final TableColumnModel columnModel = table.getColumnModel();
		
		/* Set minimum widths for antecedent and consequent columns */
		columnModel.getColumn(0).setMinWidth(220);
		columnModel.getColumn(1).setMinWidth(120);
		
		for (int i = 2; i < table.getColumnCount(); i++) {
			
			/* Set custom renderer for metric's cells */
			columnModel.getColumn(i).setCellRenderer(new DecimalFormatRenderer());
			
			/* Set minimum size values for metric's columns */
			columnModel.getColumn(i).setPreferredWidth(105);
			columnModel.getColumn(i).setMinWidth(70);
			columnModel.getColumn(i).setMaxWidth(120);
			
		}
		
		/* Set FilterMap instance for antecedent and consequent columns */
		filterMapList = new FilterMap[2];
		filterMapList[0] = new FilterMap();
		filterMapList[1] = new FilterMap();
		
		/* Set table's RowSorter for sorting and filterig */
		sorter = new TableRowSorter<TableModel>(tableModel);
		table.setRowSorter(sorter);
		table.getRowSorter().addRowSorterListener(new RowSorterListener() {
			
			@Override
			public void sorterChanged(RowSorterEvent e) {
				
				List<?> sortKeys = e.getSource().getSortKeys();
				
				if (sortKeys.size() > 0) {
					
					RowSorter.SortKey key = (SortKey) sortKeys.get(0);
					int keyColumn = key.getColumn();
					
					for (int i = 2; i < columnModel.getColumnCount(); i++) {
						columnModel.getColumn(i).setCellRenderer(new DecimalFormatRenderer());
					}
					
					String columnName = tableModel.getColumnName(keyColumn);
					int columnIndex = table.getColumnModel().getColumnIndex(columnName);
					if (columnIndex > 1) {
						columnModel.getColumn(columnIndex).setCellRenderer(new ProgressTableCellRenderer());
					}
					
				}
				
			}
			
		});
		
		/* Adds rules to table */
		for (AssociationRule r : rulesList) {
			
			/* Antecedent */
			String antecedent = "";
			for (Item p : r.getPremise()) {
				antecedent += p + " ";
				filterMapList[0].addAttributeValue(p.toString());
			}
			
			/* Consequent */
			String consequent = "";
			for (Item c : r.getConsequence()) {		
				consequent += c + " ";
				filterMapList[1].addAttributeValue(c.toString());	
			}

			/* Support */
			double supportTemp = ((double) r.getTotalSupport()) / r.getTotalTransactions();
			double support = weka.core.Utils.roundDouble(supportTemp, 2);
			
			/* List containing each column's content to be added to table */
			List<Object> row = new ArrayList<Object>();
			
			/* Adds antecedent, consequent and support */
			row.add(antecedent.trim());
			row.add(consequent.trim());
			row.add(support);
			
			/* Adds metric values */
			for (String m: metrics) {
				try {
					double metricValue = r.getNamedMetricValue(m);
					double roundedValue = weka.core.Utils.roundDouble(metricValue, 2);
					row.add(roundedValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			/* Adds a row with values to table */
			tableModel.addRow(row.toArray());
			
		}
		
		/* Get total values for results */
		lblTotalRulesValue.setText(String.valueOf(rulesList.size()));
		lblFilteredRulesValue.setText(String.valueOf(table.getRowCount()));
		
		/* Initializes attribute's component */
		comboTableColumn.removeAllItems();
		comboTableColumn.addItem(new ComboBoxItem(0, "Antecedent (X)", "X"));
		comboTableColumn.addItem(new ComboBoxItem(1, "Consequent (Y)", "Y"));
		
		comboTableColumn.setEnabled(true);
		comboComparisonType.setEnabled(true);
		comboAttribute.setEnabled(true);
		comboLabel.setEnabled(true);
		comboFilter.setEnabled(true);
		btnAdd.setEnabled(true);
		btnApply.setEnabled(true);
		btnClear.setEnabled(true);
		btnSave.setEnabled(true);
		btnExport.setEnabled(true);
		
		for (Map.Entry<String, JSpinner> entry : metricSpinnerMap.entrySet()) {
			
			JSpinner spinner = (entry.getValue());
			
			/* Maximum and minimum values for metrics */
			
			SpinnerNumberModel spinModel = (SpinnerNumberModel) spinner.getModel();
			int colIndex = table.getColumnModel().getColumnIndex(entry.getKey());
			double spinModelMax = Utils.getColumnMaxValue(table, colIndex);
			double spinModelMin = Utils.getColumnMinValue(table, colIndex);
			spinModel.setMaximum(spinModelMax);
			spinModel.setMinimum(spinModelMin);
			spinModel.setValue(spinModelMin);

			/* Set listeners for spinners to filter minimum metric values */
			
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					onMetricValueChange();
				}
			});
			
			/* Enables component */
			
			spinner.setEnabled(true);
			
		}
		
		ActionListener metricVisibilityListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onMetricVisibilityChange(e);
			}
		};

		chckbxSupport.addActionListener(metricVisibilityListener);
		chckbxConfidence.addActionListener(metricVisibilityListener);
		chckbxLift.addActionListener(metricVisibilityListener);
		chckbxLeverage.addActionListener(metricVisibilityListener);
		chckbxConviction.addActionListener(metricVisibilityListener);
		
		chckbxSupport.setEnabled(true);
		chckbxConfidence.setEnabled(true);
		chckbxLift.setEnabled(true);
		chckbxLeverage.setEnabled(true);
		chckbxConviction.setEnabled(true);
		
		btnReset.setEnabled(true);
		
	}

	/** Unused */
	@Override
	public void setInstances(Instances inst) {}

	/**
	 * Sets the Explorer to use as parent frame
	 * 
	 * @param parent the parent frame
	 */
	@Override
	public void setExplorer(Explorer parent) {
		explorer = parent;
	}

	/**
	 * Returns the parent Explorer frame
	 * 
	 * @return the parent frame
	 */
	@Override
	public Explorer getExplorer() {
		return explorer;
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

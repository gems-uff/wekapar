package weka.gui.explorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterEvent.Type;
import javax.swing.event.RowSorterListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import weka.associations.AssociationRule;
import weka.associations.AssociationRules;
import weka.associations.Item;
import weka.core.Instances;
import weka.gui.Logger;
import weka.gui.SysErrLog;
import weka.gui.TaskLogger;
import weka.gui.explorer.Explorer.ExplorerPanel;
import weka.gui.explorer.Explorer.LogHandler;
import wekapar.ComboBoxItem;
import wekapar.DecimalSpinner;
import wekapar.FilterComboBox;
import wekapar.FilterMap;
import wekapar.FilterMapAttribute;
import wekapar.MetricSpinner;
import wekapar.Utils;
import wekapar.table.DecimalCellRenderer;
import wekapar.table.ProgressCellRenderer;
import wekapar.table.RulesTable;
import wekapar.table.RulesTableCellRenderer;
import wekapar.table.RulesTableColumnModel;
import wekapar.table.RulesTableModel;

/**
 * A JPanel to visualize association rules
 *
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class PostprocessAssociationsPanel extends JPanel implements ExplorerPanel, LogHandler {

	/** for serialization */
	private static final long serialVersionUID = 3114490118265884877L;
	
	/** Enabling support for PropertyChangeListener */
	private static PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(PostprocessAssociationsPanel.class);
	
	/** Message for busy thread */
	private static final String THREAD_BUSY_MESSAGE = "Can't execute at this time. Currently busy with other operation.";
	
	/** Dialog title for busy thread */
	private static final String THREAD_BUSY_TITLE = "Operation in progress";
	
	/** A thread for all operations (IO, filters, etc.) */
	private static Thread thread;
	
	/** The parent frame */
	protected Explorer explorer = null;
		
	/** The destination for log/status messages */
	protected static Logger log = new SysErrLog();
	
	/** A list of filter's strings used by parser */
	protected static List<String> parseFilterList;
	
	/** Keeps filter's string temporarily until add to list */
	protected static String parseFilterTemp = "";
	
	/** Maps all attributes to be used in filter */
	protected static FilterMap[] filterMap = new FilterMap[2];
	
	/** Maps attributes of the antecedent and its labels */
	protected static FilterMap antecedentMap = filterMap[0] = new FilterMap();
	
	/** Maps attributes of the consequent and its labels */
	protected static FilterMap consequentMap = filterMap[1] = new FilterMap();
	
	/** A label to display the total number of rules */
	protected static JLabel lblTotalRulesValue;
	
	/** A label to display the total number of filtered rules */
	protected static JLabel lblFilteredRulesValue;
	
	/** Component to select the logical operator to be used in filter */
	protected static JComboBox comboLogicalOperator;
	
	/** Component to select antecedent or consequent column in which filter will be applied */
	protected static JComboBox comboRuleSide;
	
	/** Component to select the comparison type to be used in filter  */
	protected static JComboBox comboComparisonType;
	
	/** Component to select an attribute of a rule to be used in filter */
	protected static JComboBox comboAttribute;
	
	/** Component to select a label of an attribute to be used in filter */
	protected static JComboBox comboLabel;

	/** The {@link JButton} to add a criteria to filter */
	protected static JButton btnAdd;
	
	/** Component to store a list of recently applied filters */
	protected static FilterComboBox comboFilter = new FilterComboBox();
	
	/** The editor for current filter */
	protected static JTextField comboFilterComponent = (JTextField) comboFilter.getEditor().getEditorComponent();
	
	/** The {@link JButton} to apply a filter to data */
	protected static JButton btnApply;
	
	/** The {@link JButton} to clear the applied filter */
	protected static JButton btnClear;
	
	/** The main {@link JPanel} */
	private static JPanel rulesPanel;
	
	/** Just a {@link JPanel} to help with layout */
	private JPanel panel;
	
	/** The {@link JPanel} to display the command buttons */
	private JPanel buttonsPanel;
	
	/** The {@link JPanel} to display the filtering components */
	protected JPanel filterPanel;
	
	/** The {@link JButton} to save current work */
	protected static JButton btnSave;
	
	/** The {@link JButton} to open a previously saved work */
	private JButton btnOpen;
	
	/** The {@link JButton} to export data */
	protected static JButton btnExport;

	/** The association rules */
	private static AssociationRules associationRules;
	
	/** The file chooser for save/open rules and apply filters. */
	private JFileChooser fileChooserSaveOpen = new JFileChooser(new File(System.getProperty("user.dir")));
	
	/** The file chooser for export rules. */
	private JFileChooser fileChooserExport = new JFileChooser(new File(System.getProperty("user.dir")));
	
	/** The {@link JPanel} to display the metric's filtering components */
	private static JPanel metricsFilterPanel;
	
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
	private static HashMap<String, JSpinner> metricSpinnerMap = new HashMap<String, JSpinner>();
	
	/** Apply metric's minimum values to rules */
	private static JButton btnApplyMetric;

	/** Reset metric's minimum values */
	private static JButton btnResetMetric;
	
	/** The {@link JPanel} for filtering rules */
	private JPanel rulesFilterPanel;
	
	private JLabel lblLogicalOperator;
	private JLabel lblRuleSide;
	private JLabel lblComparisonType;
	private JLabel lblAttribute;
	private JLabel lblLabel;
	
	/** Listener for metric spinner components */
	private static ChangeListener metricChangeListener;
	
	/** The {@link JPanel} for metric's command buttons */
	private JPanel metricButtonsPanel;
	
	/** The {@link JPanel} to display the table with association rules and its metrics */
	protected JPanel tablePanel;

	/** Component where rules are loaded */
	protected static RulesTable table;
	
	/** For sorting and filtering */
	protected static TableRowSorter<TableModel> sorter;
	
	private JLabel lblDisplaying;
	private JLabel lblOf;
	private JLabel lblRules;
	private JLabel lblDecimalPlaces;
	
	/** Cell renderer for columns with numeric values */
	private static DecimalCellRenderer decimalCellRenderer = new DecimalCellRenderer();
	
	/** Select number of decimal places to format cell's values */
	private static DecimalSpinner spinDecimal;
	
	/**
	 * Create the postprocess panel.
	 */
	public PostprocessAssociationsPanel() {
		
		setLayout(new BorderLayout(0, 0));
		setFocusable(true);
		
		/* Set extesion filter to CSV files when exporting rules */
		FileFilter fileFilter = new FileNameExtensionFilter("CSV file: comma separated files", new String[] {"csv"});
		fileChooserExport.addChoosableFileFilter(fileFilter);
		fileChooserExport.setFileFilter(fileFilter);
		
		/* Force tab selection every time associationRules's value is changed,
		 * i.e every time a new set of rules is loading.
		 */		
		addPropertyChangeListener("associationRules", new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				selectTab();
			}
			
		});
		
		comboFilterComponent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				onComboFilterStateChanged();
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
		
		/* Buttons */
		
		btnSave = new JButton("Save...");
		buttonsPanel.add(btnSave);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		btnSave.setEnabled(false);
		
		btnOpen = new JButton("Open...");
		buttonsPanel.add(btnOpen);
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});
		
		btnExport = new JButton("Export...");
		btnExport.setToolTipText("Export the filtered rules in CSV format");
		buttonsPanel.add(btnExport);
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportToCsv();
			}
		});
		btnExport.setEnabled(false);
		
		/* Filter for rules */
		
		rulesFilterPanel = new JPanel();
		panel.add(rulesFilterPanel, BorderLayout.CENTER);
		rulesFilterPanel.setBorder(new TitledBorder(null, "Filter for rules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gbl_rulesFilterPanel = new GridBagLayout();
		gbl_rulesFilterPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_rulesFilterPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_rulesFilterPanel.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_rulesFilterPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		rulesFilterPanel.setLayout(gbl_rulesFilterPanel);
		
		lblLogicalOperator = new JLabel("Logical Operator:");
		GridBagConstraints gbc_lblLogicalOperator = new GridBagConstraints();
		gbc_lblLogicalOperator.anchor = GridBagConstraints.WEST;
		gbc_lblLogicalOperator.insets = new Insets(0, 0, 5, 5);
		gbc_lblLogicalOperator.gridx = 0;
		gbc_lblLogicalOperator.gridy = 0;
		rulesFilterPanel.add(lblLogicalOperator, gbc_lblLogicalOperator);
		lblLogicalOperator.setAlignmentX(Component.RIGHT_ALIGNMENT);
		lblLogicalOperator.setVerticalAlignment(SwingConstants.TOP);
		
		lblRuleSide = new JLabel("Rule side:");
		GridBagConstraints gbc_lblRuleSide = new GridBagConstraints();
		gbc_lblRuleSide.anchor = GridBagConstraints.WEST;
		gbc_lblRuleSide.insets = new Insets(0, 0, 5, 5);
		gbc_lblRuleSide.gridx = 1;
		gbc_lblRuleSide.gridy = 0;
		rulesFilterPanel.add(lblRuleSide, gbc_lblRuleSide);
		lblRuleSide.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblRuleSide.setLabelFor(comboRuleSide);
		
		lblComparisonType = new JLabel("Comparison Type:");
		GridBagConstraints gbc_lblComparisonType = new GridBagConstraints();
		gbc_lblComparisonType.anchor = GridBagConstraints.WEST;
		gbc_lblComparisonType.insets = new Insets(0, 0, 5, 5);
		gbc_lblComparisonType.gridx = 2;
		gbc_lblComparisonType.gridy = 0;
		rulesFilterPanel.add(lblComparisonType, gbc_lblComparisonType);
		lblComparisonType.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		lblAttribute = new JLabel("Attribute:");
		GridBagConstraints gbc_lblAttribute = new GridBagConstraints();
		gbc_lblAttribute.anchor = GridBagConstraints.WEST;
		gbc_lblAttribute.insets = new Insets(0, 0, 5, 5);
		gbc_lblAttribute.gridx = 3;
		gbc_lblAttribute.gridy = 0;
		rulesFilterPanel.add(lblAttribute, gbc_lblAttribute);
		
		lblLabel = new JLabel("Label:");
		GridBagConstraints gbc_lblLabel = new GridBagConstraints();
		gbc_lblLabel.anchor = GridBagConstraints.WEST;
		gbc_lblLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblLabel.gridx = 4;
		gbc_lblLabel.gridy = 0;
		rulesFilterPanel.add(lblLabel, gbc_lblLabel);
		lblLogicalOperator.setLabelFor(comboLogicalOperator);
		
		comboLogicalOperator = new JComboBox();
		GridBagConstraints gbc_comboLogicalOperator = new GridBagConstraints();
		gbc_comboLogicalOperator.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboLogicalOperator.insets = new Insets(0, 0, 5, 5);
		gbc_comboLogicalOperator.gridx = 0;
		gbc_comboLogicalOperator.gridy = 1;
		rulesFilterPanel.add(comboLogicalOperator, gbc_comboLogicalOperator);
		comboLogicalOperator.setEnabled(false);
		comboLogicalOperator.setModel(new DefaultComboBoxModel(new String[] {"AND", "OR"}));
		
		comboRuleSide = new JComboBox();
		GridBagConstraints gbc_comboTableColumn = new GridBagConstraints();
		gbc_comboTableColumn.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboTableColumn.insets = new Insets(0, 0, 5, 5);
		gbc_comboTableColumn.gridx = 1;
		gbc_comboTableColumn.gridy = 1;
		rulesFilterPanel.add(comboRuleSide, gbc_comboTableColumn);
		comboRuleSide.setEnabled(false);
		comboRuleSide.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (comboRuleSide.getSelectedIndex() > -1) {
					comboAttribute.removeAllItems();
					int key = (Integer) ((ComboBoxItem) comboRuleSide.getSelectedItem()).getKey();
					for (FilterMapAttribute f : filterMap[key].getAttributes()) {
						comboAttribute.addItem(new ComboBoxItem(f, f.getAttribute()));
					}
				}
			}
		});
		
		comboComparisonType = new JComboBox();
		GridBagConstraints gbc_comboComparisonType = new GridBagConstraints();
		gbc_comboComparisonType.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboComparisonType.insets = new Insets(0, 0, 5, 5);
		gbc_comboComparisonType.gridx = 2;
		gbc_comboComparisonType.gridy = 1;
		rulesFilterPanel.add(comboComparisonType, gbc_comboComparisonType);
		comboComparisonType.setEnabled(false);
		comboComparisonType.setModel(new DefaultComboBoxModel(new String[] {"CONTAINS", "EQUALS"}));
		
		comboAttribute = new JComboBox();
		GridBagConstraints gbc_comboAttribute = new GridBagConstraints();
		gbc_comboAttribute.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboAttribute.insets = new Insets(0, 0, 5, 5);
		gbc_comboAttribute.gridx = 3;
		gbc_comboAttribute.gridy = 1;
		rulesFilterPanel.add(comboAttribute, gbc_comboAttribute);
		comboAttribute.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboAttribute.setEnabled(false);
		comboAttribute.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (comboAttribute.getSelectedIndex() > -1) {
					comboLabel.removeAllItems();
					comboLabel.addItem("All");
					FilterMapAttribute selected = (FilterMapAttribute) ((ComboBoxItem) comboAttribute.getSelectedItem()).getKey();
					for (String v : selected.getLabels()) {
						comboLabel.addItem(v);
					}
				}
			}
		});
		
		comboLabel = new JComboBox();
		GridBagConstraints gbc_comboLabel = new GridBagConstraints();
		gbc_comboLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboLabel.insets = new Insets(0, 0, 5, 5);
		gbc_comboLabel.gridx = 4;
		gbc_comboLabel.gridy = 1;
		rulesFilterPanel.add(comboLabel, gbc_comboLabel);
		comboLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboLabel.setEnabled(false);
		
		btnAdd = new JButton("Add");
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAdd.gridwidth = 2;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 5;
		gbc_btnAdd.gridy = 1;
		rulesFilterPanel.add(btnAdd, gbc_btnAdd);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addFilter();
			}
		});
		btnAdd.setEnabled(false);
		
		GridBagConstraints gbc_comboFilter = new GridBagConstraints();
		gbc_comboFilter.gridwidth = 5;
		gbc_comboFilter.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboFilter.insets = new Insets(0, 0, 0, 5);
		gbc_comboFilter.gridx = 0;
		gbc_comboFilter.gridy = 2;
		rulesFilterPanel.add(comboFilter, gbc_comboFilter);
		comboFilter.setEnabled(false);
		comboFilter.setEditable(true);
		comboFilter.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				onComboFilterStateChanged();
			}
		});
		
		btnApply = new JButton("Apply");
		GridBagConstraints gbc_btnApply = new GridBagConstraints();
		gbc_btnApply.insets = new Insets(0, 0, 0, 5);
		gbc_btnApply.gridx = 5;
		gbc_btnApply.gridy = 2;
		rulesFilterPanel.add(btnApply, gbc_btnApply);
		btnApply.setEnabled(false);
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!comboFilterComponent.getText().trim().equals("")) {
					applyRulesFilter();
				}
			}
		});
		
		btnClear = new JButton("Clear");
		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.gridx = 6;
		gbc_btnClear.gridy = 2;
		rulesFilterPanel.add(btnClear, gbc_btnClear);
		btnClear.setEnabled(false);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearRulesFilter();
			}
		});
		
		/* Filter for metrics */
		
		metricsFilterPanel = new JPanel();
		metricsFilterPanel.setBorder(new TitledBorder(null, "Minimum values for metrics", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		filterPanel.add(metricsFilterPanel, BorderLayout.EAST);
		GridBagLayout gbl_metricsFilterPanel = new GridBagLayout();
		gbl_metricsFilterPanel.columnWidths = new int[] {90, 70, 90, 70};
		gbl_metricsFilterPanel.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0};
		gbl_metricsFilterPanel.rowWeights = new double[]{1.0, 1.0, 0.0};
		metricsFilterPanel.setLayout(gbl_metricsFilterPanel);
		
		chckbxSupport = new JCheckBox("Support");
		chckbxSupport.setEnabled(false);
		chckbxSupport.setSelected(true);
		GridBagConstraints gbc_chckbxSupport = new GridBagConstraints();
		gbc_chckbxSupport.fill = GridBagConstraints.BOTH;
		gbc_chckbxSupport.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxSupport.gridx = 0;
		gbc_chckbxSupport.gridy = 0;
		metricsFilterPanel.add(chckbxSupport, gbc_chckbxSupport);
		
		spinSupport = new MetricSpinner();
		spinSupport.setPreferredSize(new Dimension(29, 25));
		spinSupport.setEnabled(false);
		spinSupport.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(0.01)));
		GridBagConstraints gbc_spinSupport = new GridBagConstraints();
		gbc_spinSupport.insets = new Insets(0, 0, 5, 5);
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
		gbc_chckbxConfidence.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxConfidence.gridx = 0;
		gbc_chckbxConfidence.gridy = 1;
		metricsFilterPanel.add(chckbxConfidence, gbc_chckbxConfidence);
		
		spinConfidence = new MetricSpinner();
		spinConfidence.setPreferredSize(new Dimension(29, 25));
		spinConfidence.setEnabled(false);
		spinConfidence.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(0.01)));
		GridBagConstraints gbc_spinConfidence = new GridBagConstraints();
		gbc_spinConfidence.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinConfidence.insets = new Insets(0, 0, 5, 5);
		gbc_spinConfidence.gridx = 1;
		gbc_spinConfidence.gridy = 1;
		metricsFilterPanel.add(spinConfidence, gbc_spinConfidence);
		metricSpinnerMap.put("Confidence", spinConfidence);
		
		chckbxLift = new JCheckBox("Lift");
		chckbxLift.setEnabled(false);
		chckbxLift.setSelected(true);
		GridBagConstraints gbc_chckbxLift = new GridBagConstraints();
		gbc_chckbxLift.fill = GridBagConstraints.BOTH;
		gbc_chckbxLift.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxLift.gridx = 0;
		gbc_chckbxLift.gridy = 2;
		metricsFilterPanel.add(chckbxLift, gbc_chckbxLift);
		
		spinLift = new MetricSpinner();
		spinLift.setPreferredSize(new Dimension(29, 25));
		spinLift.setEnabled(false);
		spinLift.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(0.01)));
		GridBagConstraints gbc_spinLift = new GridBagConstraints();
		gbc_spinLift.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinLift.insets = new Insets(0, 0, 5, 5);
		gbc_spinLift.gridx = 1;
		gbc_spinLift.gridy = 2;
		metricsFilterPanel.add(spinLift, gbc_spinLift);
		metricSpinnerMap.put("Lift", spinLift);
		
		chckbxLeverage = new JCheckBox("Leverage");
		chckbxLeverage.setEnabled(false);
		chckbxLeverage.setSelected(true);
		GridBagConstraints gbc_chckbxLeverage = new GridBagConstraints();
		gbc_chckbxLeverage.fill = GridBagConstraints.BOTH;
		gbc_chckbxLeverage.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxLeverage.gridx = 2;
		gbc_chckbxLeverage.gridy = 0;
		metricsFilterPanel.add(chckbxLeverage, gbc_chckbxLeverage);
		
		spinLeverage = new MetricSpinner();
		spinLeverage.setPreferredSize(new Dimension(29, 25));
		spinLeverage.setEnabled(false);
		spinLeverage.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(0.01)));
		GridBagConstraints gbc_spinLeverage = new GridBagConstraints();
		gbc_spinLeverage.insets = new Insets(0, 0, 5, 0);
		gbc_spinLeverage.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinLeverage.gridx = 3;
		gbc_spinLeverage.gridy = 0;
		metricsFilterPanel.add(spinLeverage, gbc_spinLeverage);
		metricSpinnerMap.put("Leverage", spinLeverage);
		
		chckbxConviction = new JCheckBox("Conviction");
		chckbxConviction.setEnabled(false);
		chckbxConviction.setSelected(true);
		GridBagConstraints gbc_chckbxConviction = new GridBagConstraints();
		gbc_chckbxConviction.fill = GridBagConstraints.BOTH;
		gbc_chckbxConviction.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxConviction.gridx = 2;
		gbc_chckbxConviction.gridy = 1;
		metricsFilterPanel.add(chckbxConviction, gbc_chckbxConviction);
		
		spinConviction = new MetricSpinner();
		spinConviction.setPreferredSize(new Dimension(29, 25));
		spinConviction.setEnabled(false);
		spinConviction.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(0.01)));
		GridBagConstraints gbc_spinConviction = new GridBagConstraints();
		gbc_spinConviction.insets = new Insets(0, 0, 5, 0);
		gbc_spinConviction.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinConviction.gridx = 3;
		gbc_spinConviction.gridy = 1;
		metricsFilterPanel.add(spinConviction, gbc_spinConviction);
		metricSpinnerMap.put("Conviction", spinConviction);
		
		metricButtonsPanel = new JPanel();
		GridBagConstraints gbc_metricButtonsPanel = new GridBagConstraints();
		gbc_metricButtonsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_metricButtonsPanel.gridwidth = 2;
		gbc_metricButtonsPanel.gridx = 2;
		gbc_metricButtonsPanel.gridy = 2;
		metricsFilterPanel.add(metricButtonsPanel, gbc_metricButtonsPanel);
		metricButtonsPanel.setLayout(new GridLayout(1, 2, 5, 0));
		
		btnApplyMetric = new JButton("Apply");
		metricButtonsPanel.add(btnApplyMetric);
		btnApplyMetric.setEnabled(false);
		btnApplyMetric.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyMetricsFilter();
			}
		});
		
		btnResetMetric = new JButton("Reset");
		metricButtonsPanel.add(btnResetMetric);
		btnResetMetric.setEnabled(false);
		btnResetMetric.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetMetricsFilter();
			}
		});
		
		/* Setting listeners to filter for metrics */
		
		metricChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (thread == null) {
					onMetricValueChange();
				}
			}
		};
		
		for (Component c : metricsFilterPanel.getComponents()) {
			
			if (c instanceof JCheckBox) {
				
				JCheckBox chkBox = (JCheckBox) c;
				
				/* Listen to JCheckBox checked status to show/hide columns */
				chkBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						onMetricVisibilityChange(e);
					}
				});
				
				/* Listen to JCheckBox enabled property to
				 * change respective JSpinner enabled property
				 */
				chkBox.addPropertyChangeListener("enabled", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						onMetricPropertyChange(e);
					}
				});
				
			} else if (c instanceof MetricSpinner) {
				
				MetricSpinner spinner = (MetricSpinner) c;
				
				/* Set listeners for spinners */
				spinner.addChangeListener(metricChangeListener);
				
			}
			
		}
		
		/* Table for displaying rules. */
		
		tablePanel = new JPanel();
		rulesPanel.add(tablePanel, BorderLayout.CENTER);
		tablePanel.setLayout(new BorderLayout(0, 0));
		tablePanel.setBorder(new TitledBorder(null, "Rules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel infoPanel = new JPanel();
		tablePanel.add(infoPanel, BorderLayout.NORTH);
		infoPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		spinDecimal = new DecimalSpinner();
		spinDecimal.setPreferredSize(new Dimension(40, 20));
		spinDecimal.setModel(new SpinnerNumberModel(new Integer(0), null, null, new Integer(1)));
		SpinnerNumberModel spinModel = (SpinnerNumberModel) spinDecimal.getModel();
		int spinModelMax = RulesTableModel.DECIMAL_PLACES_MAX;
		int spinModelMin = RulesTableModel.DECIMAL_PLACES_MIN;
		spinModel.setMaximum(spinModelMax);
		spinModel.setMinimum(spinModelMin);
		spinModel.setValue(spinModelMin);
		spinDecimal.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				((RulesTableModel) table.getModel()).setDecimals((Integer) spinDecimal.getValue());
				table.repaint();
			}
		});
		
		lblDecimalPlaces = new JLabel("Decimal places:");
		infoPanel.add(lblDecimalPlaces);
		infoPanel.add(spinDecimal);
		
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(2, 15));
		separator.setOrientation(SwingConstants.VERTICAL);
		infoPanel.add(separator);
		
		lblDisplaying = new JLabel("Displaying");
		infoPanel.add(lblDisplaying);
		
		lblFilteredRulesValue = new JLabel("0");
		lblFilteredRulesValue.setFont(new Font("Tahoma", Font.BOLD, 11));
		infoPanel.add(lblFilteredRulesValue);
		
		lblOf = new JLabel("of");
		infoPanel.add(lblOf);
		
		lblTotalRulesValue = new JLabel("0");
		lblTotalRulesValue.setFont(new Font("Tahoma", Font.BOLD, 11));
		infoPanel.add(lblTotalRulesValue);
		
		lblRules = new JLabel("rules");
		infoPanel.add(lblRules);
		
		JScrollPane scrollPaneTable = new JScrollPane();
		tablePanel.add(scrollPaneTable);
		
		JPopupMenu tablePopupMenu = new JPopupMenu();
		
		JMenuItem menuItemSubSet = new JMenuItem("Find subsets for this rule");
		menuItemSubSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				filterSubSet(table, row);
			}
		});
		tablePopupMenu.add(menuItemSubSet);
		
		JMenuItem menuItemInverse = new JMenuItem("Find inverse rule");
		menuItemInverse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				filterInverseRule(table, row);
			}
		});
		tablePopupMenu.add(menuItemInverse);
		
		table = new RulesTable();
		table.setComponentPopupMenu(tablePopupMenu);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
		        Point point = e.getPoint();
		        int currentRow = table.rowAtPoint(point);
		        table.setRowSelectionInterval(currentRow, currentRow);
			}
		});
		
		scrollPaneTable.setViewportView(table);

	}

	/**
	 * Add a PropertyChangeListener for a specific property to the listener list.
	 * 
	 * @param propertyName the name of the property to listen on
	 * @param listener the PropertyChangeListener to be added
	 */
	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
		
	}
	
	/**
	 * This method disables the button for apllying filter and the component for
	 * selecting logical operator when the filter editor is empty.
	 */
	private void onComboFilterStateChanged() {

		boolean isEmpty = comboFilterComponent.getText().isEmpty();
		comboLogicalOperator.setEnabled(!isEmpty);
		btnApply.setEnabled(!isEmpty);
		
	}

	/**
	 * Set enabled property to true for "apply" and "reset" buttons in metrics
	 * filter.
	 */
	private static void onMetricValueChange() {
		
		btnApplyMetric.setEnabled(true);
		btnResetMetric.setEnabled(true);
		
	}

	// TODO: Getting thread busy message when selecting/deselecting a metric
	// TODO: On click twice gets thread busy message but still hide column and when click again
	//		get error message because column is hidden.
	/**
	 * This method must be called when a {@link JCheckBox} component is clicked
	 * to enable or disable respective column in table and re-apply filter.
	 * 
	 * @param e the action event object
	 */
	private static void onMetricVisibilityChange(final ActionEvent e) {
	
		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					
					if (log instanceof TaskLogger) {
		            	((TaskLogger) log).taskStarted();
		            }
										
					JCheckBox chckbx = (JCheckBox) e.getSource();
					String metricName = chckbx.getText();
					MetricSpinner spinner = (MetricSpinner) metricSpinnerMap.get(metricName);
					RulesTableColumnModel columnModel = (RulesTableColumnModel) table.getColumnModel();
					
					if (spinner != null) {
						spinner.setEnabled(chckbx.isSelected());
						spinner.reset();
					}
										
					if (chckbx.isSelected()) {
						log.statusMessage("Enabling column...");
						columnModel.showColumn(metricName);
					} else {
						log.statusMessage("Disabling column...");
						columnModel.hideColumn(metricName);
						if (sorter.getSortKeys().contains(metricName)) {
							List<SortKey> sortKeys = Collections.emptyList();
							sorter.setSortKeys(sortKeys);
						}
					}
					
					applyFilter();
					
					thread = null;
					log.statusMessage("OK");
					if (log instanceof TaskLogger) {
		            	((TaskLogger) log).taskFinished();
		            }
					btnApplyMetric.setEnabled(false);
					btnResetMetric.setEnabled(false);
					
				}
				
			};
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		} else {
			JOptionPane.showMessageDialog(null, THREAD_BUSY_MESSAGE, THREAD_BUSY_TITLE, JOptionPane.WARNING_MESSAGE);
		}
		
	}

	/**
	 * This method must be called when a {@link JCheckBox} component changes its
	 * "selected" property to enable or disable respective {@link JSpinner}
	 * component.
	 * 
	 * @param e the property change event object
	 */
	private static void onMetricPropertyChange(PropertyChangeEvent e) {
	
		JCheckBox chkbx = (JCheckBox) e.getSource();
		MetricSpinner spinner = (MetricSpinner) metricSpinnerMap.get(chkbx.getText());
		if (chkbx.isSelected()) {
			spinner.setEnabled(chkbx.isEnabled());
		} else {
			spinner.setEnabled(false);
		}
		
	}

	/**
	 * Exports the data to a file in CSV format. This method considers only
	 * visible data. So hidden lines or columns will not be exported.
	 */
	private void exportToCsv() {
		
		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					
					int option = fileChooserExport.showSaveDialog(rulesPanel);
					if (option == JFileChooser.APPROVE_OPTION) {

						if (log instanceof TaskLogger) {
    		            	((TaskLogger) log).taskStarted();
    		            }
						
						log.statusMessage("Saving to file...");
						
						StringBuilder csvData = new StringBuilder();
						
						/* Choose file */
						File file = fileChooserExport.getSelectedFile();
						String filePath = file.getPath();
						if (!filePath.toLowerCase().endsWith(".csv")) {
							filePath += ".csv";
							file = new File(filePath);
						}
						
						List<String> visibleColumns = new ArrayList<String>();
						TableModel tableModel = table.getModel();
						int columnCount = tableModel.getColumnCount();
						int modelRowCount = tableModel.getRowCount();
						int tableRowCount = table.getRowCount();
						int currentRow;
						TableColumn tableColumn;
						String columnName;
						
						/* Column names */
						Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
						while (columns.hasMoreElements()) {
							tableColumn = (TableColumn) columns.nextElement();
							columnName = tableColumn.getHeaderValue().toString();
							csvData.append("\"" + columnName + "\"");
							csvData.append(columns.hasMoreElements() ? "," : "");
							visibleColumns.add(columnName);
						}
						csvData.append("\r\n");
						
						/* Rules data */
						for (int row = 0; row < tableRowCount; row++) {
							for (int col = 0; col < columnCount; col++) {
								columnName = tableModel.getColumnName(col);
								if (visibleColumns.contains(columnName)) {
									currentRow = row;
									if (tableRowCount < modelRowCount) {
										currentRow = table.convertRowIndexToModel(row);
									}
									csvData.append("\"" + tableModel.getValueAt(currentRow, col) + "\"");
								}
								csvData.append(col + 1 < columnCount ? "," : "");
							}
							csvData.append("\r\n");
						}
						
						/* Write to file */
						try {
							FileWriter w = new FileWriter(file);
							w.write(csvData.toString());
							w.flush();
							w.close();
							log.statusMessage("OK");
						} catch (IOException e) {
							log.statusMessage("FAILED! See log.");
							log.logMessage(e.getMessage());
						}
						
					}
					
					thread = null;
					
					if (log instanceof TaskLogger) {
		            	((TaskLogger) log).taskFinished();
		            }
					
				}
			};
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		} else {
			JOptionPane.showMessageDialog(this, THREAD_BUSY_MESSAGE, THREAD_BUSY_TITLE, JOptionPane.WARNING_MESSAGE);
		}
		
	}

	/**
	 * Loads a previously saved file.
	 */
	private void open() {
		
		if (thread == null) {
			thread = new Thread() {
				
				@Override
				public void run() {
					
					int option = fileChooserSaveOpen.showOpenDialog(rulesPanel);
					if (option == JFileChooser.APPROVE_OPTION) {

						if (log instanceof TaskLogger) {
    		            	((TaskLogger) log).taskStarted();
    		            }
						
						log.statusMessage("Choosing file...");
						File file = fileChooserSaveOpen.getSelectedFile();
						
						try {
							
							log.statusMessage("Reading from file...");
							
							FileInputStream fis = new FileInputStream(file);
							BufferedInputStream bis = new BufferedInputStream(fis);
							ObjectInputStream ois = new ObjectInputStream(bis);
							
							/* Skip plugin version */
							ois.readObject();
							
							/* Load association rules */
							associationRules = (AssociationRules) ois.readObject();
							log.statusMessage("Loading rules...");
							loadRules();
							
							/* Load list of applied filters */
							comboFilter.setModel((ComboBoxModel) ois.readObject());
							
							ois.close();
							log.statusMessage("OK");
							
						} catch (Exception e) {
							
							log.statusMessage("FAILED! See log.");
							log.logMessage(e.getMessage());
							
						}
						
					}
					
					thread = null;

					if (log instanceof TaskLogger) {
		            	((TaskLogger) log).taskFinished();
		            }
					
				}
				
			};
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		} else {
			JOptionPane.showMessageDialog(this, THREAD_BUSY_MESSAGE, THREAD_BUSY_TITLE, JOptionPane.WARNING_MESSAGE);
		}
		
	}

	/**
	 * Saves data to a file. This method considers all data, including hidden
	 * lines and columns, and apllied filters.
	 */
	private void save() {
		
		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					int option = fileChooserSaveOpen.showSaveDialog(rulesPanel);
					if (option == JFileChooser.APPROVE_OPTION) {

						if (log instanceof TaskLogger) {
    		            	((TaskLogger) log).taskStarted();
    		            }
						
						log.statusMessage("Saving to file...");
						
						File file = fileChooserSaveOpen.getSelectedFile();
						try {
											
							FileOutputStream fos = new FileOutputStream(file);
							BufferedOutputStream bos = new BufferedOutputStream(fos);
							ObjectOutputStream oos = new ObjectOutputStream(bos);
							
							/* Plugin version */
							oos.writeObject(Utils.getVersion());
							
							/* Association rules */
							oos.writeObject(associationRules);
							
							/* List of applied filters */
							oos.writeObject(comboFilter.getModel());
							
							/* Need to be reinitialized to prevent unexpected behavior from
							 * JComboBox component after calling getModel method.
							 */
							comboFilterComponent = (JTextField) comboFilter.getEditor().getEditorComponent();
							
							oos.close();
							
							log.statusMessage("OK");
							
						} catch (Exception e) {
							log.statusMessage("FAILED! See log.");
							log.logMessage(e.getMessage());
						}
						
					}
					
					thread = null;
					
					if (log instanceof TaskLogger) {
		            	((TaskLogger) log).taskFinished();
		            }
					
				}
			};
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		} else {
			JOptionPane.showMessageDialog(this, THREAD_BUSY_MESSAGE, THREAD_BUSY_TITLE, JOptionPane.WARNING_MESSAGE);
		}
		
	}
	
	/**
	 * Select this tab. Used by visualization
	 * menu item in associate tab.
	 */
	private void selectTab() {
		
		getExplorer().getTabbedPane().setSelectedComponent(this);
		
	}

	/**
	 * Initializes filter parameters for metrics.
	 */
	private static void initMetrics() {
		
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
			
		}	
		
	}

	/**
	 * Adds a filter item to editor by getting selected options
	 */
	private void addFilter() {
		
		String column = ((ComboBoxItem) comboRuleSide.getSelectedItem()).getValue();
		String comparisonOperator = (String) comboComparisonType.getSelectedItem();
		String logicalOperator = (String) comboLogicalOperator.getSelectedItem();
		String attribute = comboAttribute.getSelectedItem().toString();
		String label = (String) comboLabel.getSelectedItem();
		
		addFilter(column, comparisonOperator, logicalOperator, attribute, label);
		
	}

	/**
	 * Adds a filter item to editor
	 * 
	 * @param column a {@link String} representing the column
	 * @param comparisonOperator the comparison operator
	 * @param logicalOperator the logical operator
	 * @param attribute the attribute name
	 * @param label the label for attribute
	 */
	private void addFilter(String column, String comparisonOperator, String logicalOperator, String attribute, String label) {

		Pattern p;
	    Matcher m = null;
		String componentText = comboFilterComponent.getText();
		String allLabels = "";
		String filter = "";
		boolean found = false;

		/*
		 * The pattern for a filter item begins with the string representation
		 * of the column
		 */
		filter += column;
		
		if (comparisonOperator.equals("EQUALS")) {
			p = Pattern.compile(".*("+column+"\\[\\^.*?\\$\\])");
		    m = p.matcher(componentText);
		    found = m.find();
		    if (found && logicalOperator.equals("AND")) {
				filter = "";
			} else {
				filter += "[^";
			}
		} else {
			filter += "[";
		}
		
	    filter += found && logicalOperator.equals("AND") ? "|" : "";
	    
	    /* At this point concatanate the attribute name */
		filter += attribute;
		
		/*
		 * Concatenate the label only if a valid option has been selected. The
		 * "All" option means any label.
		 */
		filter += (!label.equals("All")) ? "=" + label : "";
		
		/*
		 * All labels must be concatenated in case of selecting the "EQUALS"
		 * operator.
		 */
		if (label.equals("All") && comparisonOperator.equals("EQUALS")) {
			allLabels = "";
			allLabels += "=(";
			int size = comboLabel.getModel().getSize();
			for (int i = 1; i < size; i++) {
				allLabels += comboLabel.getItemAt(i);
				allLabels += (i + 1) < size ? "|" : "";
			}
			allLabels += ")";
			filter += allLabels;
		}
		
		filter += found && logicalOperator.equals("AND") ? "( |)" : "";
		
		if (found && logicalOperator.equals("AND")) {
			
			int lastIndex = componentText.indexOf(m.group(1));
			int start = componentText.indexOf("^", lastIndex - 1);
			int end = componentText.indexOf("$", lastIndex);
			String content = componentText.substring(start+1, end);
			boolean contains = false;
			
			if (label.equals("All")) {
				contains = content.contains(attribute + allLabels);
			} else {
				contains = content.contains(attribute + "=" + label);
			}
			
			/* Insert in editor only if not exists yet */
			if (!contains) {
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
			}
			
		} else {
			
			filter += comparisonOperator.equals("EQUALS") ? "$]" : "]";
			
			/* Insert in editor only if not exists yet */
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
			btnApply.setEnabled(true);
			btnClear.setEnabled(true);
			
		}
		
	}

	/**
	 * Builds a {@link RowFilter} according to selected metrics minimum values.
	 * 
	 * @return the row filter object
	 */
	private static RowFilter<? super TableModel, ? super Integer> buildMetricsFilter() {
			
		RowFilter<Object, Object> rowFilter = RowFilter.regexFilter(".*");
		List<RowFilter<Object, Object>> metricFilters = new ArrayList<RowFilter<Object, Object>>();
		List<RowFilter<Object, Object>> metricFilter;
		RowFilter<Object, Object> filterEqual;
		RowFilter<Object, Object> filterAfter;
		RulesTableColumnModel columnModel = (RulesTableColumnModel) table.getColumnModel();
		MetricSpinner spinner;
		String key;
		double value;
		int columnIndex;
		
		for (Map.Entry<String, JSpinner> entry : metricSpinnerMap.entrySet()) {
			metricFilter = new ArrayList<RowFilter<Object, Object>>();
			key = entry.getKey();
			spinner = (MetricSpinner) entry.getValue();
			value = (Double) spinner.getValue();
			if (columnModel.hasColumn(key)) {
				if (!spinner.isMinimumSelected()) {
					columnIndex = table.getColumnModel().getColumnIndex(key);
					filterEqual = RowFilter.numberFilter(ComparisonType.EQUAL, value, table.convertColumnIndexToModel(columnIndex));
					filterAfter = RowFilter.numberFilter(ComparisonType.AFTER, value, table.convertColumnIndexToModel(columnIndex));
					metricFilter.add(filterEqual);
					metricFilter.add(filterAfter);
					metricFilters.add(RowFilter.orFilter(metricFilter));
				}
			}
		}
		
		rowFilter = RowFilter.andFilter(metricFilters);
		
		return rowFilter;
		
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
		
		/* Remove empty spaces */
		filter = filter.trim();
		
		/* Once there is an empty string, the filter will be removed. */
		if (filter.length() == 0) {
			
			rowFilter = null;
		
		/* If the pattern has not found, no treatment will be applied. */
		} else if (!Pattern.compile("(X|Y)\\[.+\\]").matcher(filter).find()) {
			
			rowFilter = RowFilter.regexFilter(filter, 0, 1);
		
		/* Otherwise... */
		} else {
			
			List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>();
			List<String> operators = new ArrayList<String>();
			
			/* The filter has similar form of an association rule. The symbol
			 * between LHS and RHS means an "AND" operator.
			 */
			filter = filter.replace(" ==> ", " AND ");
			
			/* Adds parentheses to prevent parsing errors */
			filter = "(" + filter + ")";
			
			/* Initalize list of filters used by parser */
			parseFilterList = new ArrayList<String>();
			
			/* Parses content to filter */
			filterParser(filter, 0, false, 0);
			
			for (String f : parseFilterList) {
				
				String[] splitOr = f.split(" OR ");
				List<RowFilter<Object, Object>> filterList = new ArrayList<RowFilter<Object, Object>>();
				
				for (String itemOr : splitOr) {
					if (!itemOr.equals("")) {
						String[] splitAnd = itemOr.split(" AND ");
						if (splitAnd.length > 1) {
							List<RowFilter<Object, Object>> andFilters = new ArrayList<RowFilter<Object, Object>>();
							for (String itemAnd : splitAnd) {
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
				List<RowFilter<Object, Object>> tempList = new ArrayList<RowFilter<Object, Object>>();
				tempList.add(filters.get(0));
				for (int i = 0; i < operators.size(); i++) {
					tempList.add(filters.get(i+1));
					if (operators.get(i).equals("AND")) {
						rowFilter = RowFilter.andFilter(tempList);
					} else if (operators.get(i).equals("OR")) {
						rowFilter = RowFilter.orFilter(tempList);
					}
					tempList = new ArrayList<RowFilter<Object, Object>>();
					tempList.add(rowFilter);
				}
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

					/* The filter end delimiter must be out from quotation
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
		String regexString = filterItem.substring(beginIndex, endIndex);
		
		Set<String> labels = new HashSet<String>();
		labels.addAll(antecedentMap.getUniqueLabels());
		labels.addAll(consequentMap.getUniqueLabels());
		
		Pattern p;
		Matcher m;
		String group;
		String substring;
		String quoted;
		StringBuilder buildRegex;
				
		for (String label : labels) {
			p = Pattern.compile("(=|\\||\\()\\Q" + label + "\\E(\\(|\\)|\\$|\\||$)");
			m = p.matcher(regexString);
			while (m.find()) {
				group = m.group();
				beginIndex = regexString.indexOf(group) + 1;
				endIndex = beginIndex + group.length() - 2;
				substring = regexString.substring(beginIndex, endIndex);
				quoted = Pattern.quote(substring);
				buildRegex = new StringBuilder(regexString);
				regexString = buildRegex.replace(beginIndex, endIndex, quoted).toString();
			}
		}
						
		int index = columnName.equals("X") ? 0 : 1;
				
		return RowFilter.regexFilter(regexString, index);
		
	}

	/**
	 * Applies filter to find a subset of rules as a result from
	 * generated subsets of a initial set of the antecedents in
	 * selected row keeping same consequent.
	 * 
	 * @param target the JTable component
	 * @param row the selected row in target
	 */
	private void filterSubSet(JTable target, int row) {
		
		/*
		 * Dmytro Paukov's combinatoricslib used in
		 * this method supports only Java 7 or newer.
		 * */
		if (Utils.JAVA_CLASS_VERSION < 51) {
			JOptionPane.showMessageDialog(this, "This feature is only compatible with Java 7 or later!", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
	
		comboFilterComponent.setText("");

		String[] attributeSplit;
		String attributeName;
		String attributeLabel;
		
		String attributeNames;
		String attributeLabels;
		
		String pattern;
		Pattern p;
		Matcher m;
		
		/* Get values of selected row */
		String antecedent = (String) target.getValueAt(row, 0);
		String consequent = (String) target.getValueAt(row, 1);
		
		/* Get the lists of attributes and labels from antecedent */
		List<FilterMapAttribute> antecedentAttributes = antecedentMap.getAttributes();
		List<String> antecedentLabels = antecedentMap.getUniqueLabels();
		
		/* Get all attributes */
		attributeNames = "";
		for (int i = 0; i < antecedentAttributes.size(); i++) {
			attributeNames += antecedentAttributes.get(i).getAttribute();
			attributeNames += (i + 1) < antecedentAttributes.size() ? "|" : "";
		}
		
		/* Get all labels */			
		attributeLabels = "";
		for (int i = 0; i < antecedentLabels.size(); i++) {
			// Quotes labels
			attributeLabels += Pattern.quote(antecedentLabels.get(i));
			attributeLabels += (i + 1) < antecedentLabels.size() ? "|" : "";
		}
		
		/* Find attributes in string and adds to list */
		List<String> antecedents = new ArrayList<String>();
		pattern = "((" + attributeNames + ")=(" + attributeLabels + "))";
		p = Pattern.compile(pattern);
		m = p.matcher(antecedent);
		while (m.find()) {
			antecedents.add(m.group());
		}
		
		/* Create the initial set of antecedents */
		ICombinatoricsVector<String> antecedentSet = Factory.createVector(antecedents);
		
		/* Create an instance of the subset generator */
		Generator<String> antecedentGen = Factory.createSubSetGenerator(antecedentSet);
		
		/* Logical operators for filter (OR/AND) */
		String logicalOperator = "";
		
		/* A generated subset */
		ICombinatoricsVector<String> subSet;
		
		/* Loop list in reverse order */
		for (int i = (int) antecedentGen.getNumberOfGeneratedObjects() - 1; i > 0; i--) {
			
			/* Current subset */
			subSet = antecedentGen.generateAllObjects().get(i);
			
			/* Loop subset to add its attributes in filter */
			for (int j = 0; j < subSet.getSize(); j++) {
				
				/* Get attribute and label */
				attributeSplit = subSet.getValue(j).split("=", 2);
				attributeName = attributeSplit[0];
				attributeLabel = attributeSplit[1];
				
				/* Each subset begins with a "OR" filter
				 * for grouping its attributes in filter.
				 */
				logicalOperator = j == 0 ? "OR" : "AND";
				
				/* Adds filter */
				addFilter("X", "EQUALS", logicalOperator, attributeName, attributeLabel);
				
			}
		}
		
		/* Get the lists of attributes and labels from consequent */
		List<FilterMapAttribute> consequentAttributes = consequentMap.getAttributes();
		List<String> consequentLabels = consequentMap.getUniqueLabels();
		
		/* Get all attributes */
		attributeNames = "";
		for (int i = 0; i < consequentAttributes.size(); i++) {
			attributeNames += consequentAttributes.get(i).getAttribute();
			attributeNames += (i + 1) < consequentAttributes.size() ? "|" : "";
		}
		
		/* Get all labels */			
		attributeLabels = "";
		for (int i = 0; i < consequentLabels.size(); i++) {
			// Quote labels
			attributeLabels += Pattern.quote(consequentLabels.get(i));
			attributeLabels += (i + 1) < consequentLabels.size() ? "|" : "";
		}
		
		/* Find attributes in string and adds to filter */
		pattern = "((" + attributeNames + ")=(" + attributeLabels + "))";
		p = Pattern.compile(pattern);
		m = p.matcher(consequent);
		while (m.find()) {
			
			/* Get attribute and label */
			attributeSplit = m.group().split("=", 2);
			attributeName = attributeSplit[0];
			attributeLabel = attributeSplit[1];
			
			/* Adds filter */
			addFilter("Y", "EQUALS", "AND", attributeName, attributeLabel);
			
		}
		
		applyRulesFilter();
		
	}
	
	/**
	 * Applies filter to find the inverse rule.
	 * 
	 * @param target the JTable component
	 * @param row the selected row in target
	 */
	private void filterInverseRule(RulesTable target, int row) {
	
		comboFilterComponent.setText("");

		String[] attributeSplit;
		String attributeName;
		String attributeLabel;
		
		String attributeNames;
		String attributeLabels;
		
		String pattern;
		Pattern p;
		Matcher m;
		
		/* Get values of selected row */
		String antecedent = (String) target.getValueAt(row, 0);
		String consequent = (String) target.getValueAt(row, 1);
		
		/* Join lists of attributes */
		Set<FilterMapAttribute> attributes = new HashSet<FilterMapAttribute>();
		attributes.addAll(antecedentMap.getAttributes());
		attributes.addAll(consequentMap.getAttributes());
		
		/* Join lists of labels */
		Set<String> labels = new HashSet<String>();
		labels.addAll(antecedentMap.getUniqueLabels());
		labels.addAll(consequentMap.getUniqueLabels());
		
		/* Get all attributes */
		attributeNames = "";
		for (Iterator<FilterMapAttribute> i = attributes.iterator(); i.hasNext();) {
			attributeNames += Pattern.quote(i.next().toString());
			attributeNames += i.hasNext() ? "|" : "";
		}
		
		/* Get all labels */
		attributeLabels = "";
		for (Iterator<String> i = labels.iterator(); i.hasNext();) {
			attributeLabels += Pattern.quote(i.next().toString());
			attributeLabels += i.hasNext() ? "( |$)|" : "";
		}
		
		/* Find attributes in string and adds to list */
		List<String> antecedents = new ArrayList<String>();
		pattern = "((" + attributeNames + ")=(" + attributeLabels + "))";
		p = Pattern.compile(pattern);
		m = p.matcher(antecedent);
		while (m.find()) {
			antecedents.add(m.group());
		}
		
		/* Logical operators for filter (OR/AND) */
		String logicalOperator = "";
		
		for (int i = 0; i < antecedents.size(); i++) {
			
			/* Get attribute and label */
			attributeSplit = antecedents.get(i).split("=", 2);
			attributeName = attributeSplit[0];
			attributeLabel = attributeSplit[1];
			
			/* Each subset begins with a "OR" filter
			 * for grouping its attributes in filter.
			 */
			logicalOperator = i == 0 ? "OR" : "AND";
			
			/* Adds filter */
			addFilter("X", "EQUALS", logicalOperator, attributeName, attributeLabel);
			addFilter("Y", "EQUALS", logicalOperator, attributeName, attributeLabel);
			
		}
		
		/* Find attributes in string and adds to list */
		List<String> consequents = new ArrayList<String>();
		pattern = "((" + attributeNames + ")=(" + attributeLabels + "))";
		p = Pattern.compile(pattern);
		m = p.matcher(consequent);
		while (m.find()) {
			consequents.add(m.group());
		}
		
		for (int i = 0; i < consequents.size(); i++) {
			
			/* Get attribute and label */
			attributeSplit = consequents.get(i).split("=", 2);
			attributeName = attributeSplit[0];
			attributeLabel = attributeSplit[1];
			
			/* Each subset begins with a "OR" filter
			 * for grouping its attributes in filter.
			 */
			logicalOperator = i == 0 ? "OR" : "AND";
			
			/* Adds filter */
			addFilter("X", "EQUALS", logicalOperator, attributeName, attributeLabel);
			addFilter("Y", "EQUALS", logicalOperator, attributeName, attributeLabel);
			
		}
		
		applyRulesFilter();
		
	}

	/**
	 * Apply filter for rules
	 */
	private void applyRulesFilter() {
			
			boolean proceed = true;
	        
			// TODO: Not working (ilegal access error) since 3.8.1
			// Cause: new classloader scheme
	        /*if (Explorer.m_Memory.memoryIsLow()) {
	        	proceed = Explorer.m_Memory.showMemoryIsLow();
	        }*/
		    
		    if (proceed) {
				
				if (thread == null) {
					thread = new Thread() {
						@Override
						public void run() {
							try {
								btnApply.setEnabled(false);
								if (log instanceof TaskLogger) {
		    		            	((TaskLogger) log).taskStarted();
		    		            }
								log.statusMessage("Applying filter...");
								applyFilter();
								comboFilter.addItem(comboFilterComponent.getText().trim());
								comboFilter.setSelectedIndex(0);
								comboRuleSide.setSelectedIndex(0);
								comboComparisonType.setSelectedIndex(0);
								comboAttribute.setSelectedIndex(0);
								comboLabel.setSelectedIndex(0);
								log.statusMessage("OK");
								btnClear.setEnabled(true);
							} catch (Exception e) {
								btnApply.setEnabled(true);
								log.statusMessage("FAILED! See log.");
								log.logMessage(e.getMessage());
							}
							thread = null;
							if (log instanceof TaskLogger) {
	    		            	((TaskLogger) log).taskFinished();
	    		            }
						}
					};
					thread.setPriority(Thread.MIN_PRIORITY);
					thread.start();
				} else {
					JOptionPane.showMessageDialog(this, THREAD_BUSY_MESSAGE, THREAD_BUSY_TITLE, JOptionPane.WARNING_MESSAGE);
				}
				
			}
			
		}

	/**
	 * Clear applied filter for rules
	 */
	private void clearRulesFilter() {
		
		boolean proceed = true;
        
		// TODO: Not working (ilegal access error) since 3.8.1
		// Cause: new classloader scheme
        /*if (Explorer.m_Memory.memoryIsLow()) {
        	proceed = Explorer.m_Memory.showMemoryIsLow();
        }*/
	    
	    if (proceed) {
	
			if (thread == null) {
				thread = new Thread() {
					@Override
					public void run() {
						
						btnApply.setEnabled(false);
						btnClear.setEnabled(false);
						
						if (log instanceof TaskLogger) {
    		            	((TaskLogger) log).taskStarted();
    		            }
						
						log.statusMessage("Clearing filter...");
						
						comboFilter.setSelectedIndex(-1);
						comboFilterComponent.setText("");
						
						applyFilter();
						
						log.statusMessage("OK");
						
						if (log instanceof TaskLogger) {
    		            	((TaskLogger) log).taskFinished();
    		            }
						
						thread = null;
						
						comboLogicalOperator.setEnabled(false);
						
					}
				};
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			} else {
				JOptionPane.showMessageDialog(this, THREAD_BUSY_MESSAGE, THREAD_BUSY_TITLE, JOptionPane.WARNING_MESSAGE);
			}
			
		}
		
	}

	/**
	 * Apply filter for rule's metrics
	 */
	private static void applyMetricsFilter() {
		
		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					btnApplyMetric.setEnabled(false);
					btnResetMetric.setEnabled(false);
					if (log instanceof TaskLogger) {
		            	((TaskLogger) log).taskStarted();
		            }
					log.statusMessage("Applying minimum values for metrics...");
					applyFilter();
					btnResetMetric.setEnabled(true);
					thread = null;
					if (log instanceof TaskLogger) {
		            	((TaskLogger) log).taskFinished();
		            }
					log.statusMessage("OK");
				}
			};
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		} else {
			JOptionPane.showMessageDialog(null, THREAD_BUSY_MESSAGE, THREAD_BUSY_TITLE, JOptionPane.WARNING_MESSAGE);
		}
		
	}

	/**
	 * Reset metrics to their minimum values and
	 * clears applied filter for rule's metrics.
	 */
	private void resetMetricsFilter() {
		
		boolean proceed = true;
        
		// TODO: Not working (ilegal access error) since 3.8.1
		// Cause: new classloader scheme
        /*if (Explorer.m_Memory.memoryIsLow()) {
        	proceed = Explorer.m_Memory.showMemoryIsLow();
        }*/
	    
	    if (proceed) {
	
			if (thread == null) {
				thread = new Thread() {
					@Override
					public void run() {
						if (log instanceof TaskLogger) {
    		            	((TaskLogger) log).taskStarted();
    		            }
						log.statusMessage("Reseting minimum values for metrics...");		
						for (Map.Entry<String, JSpinner> entry : metricSpinnerMap.entrySet()) {
							((MetricSpinner) entry.getValue()).reset();
						}
						btnApplyMetric.setEnabled(false);
						btnResetMetric.setEnabled(false);
						applyFilter();
						log.statusMessage("OK");
						if (log instanceof TaskLogger) {
    		            	((TaskLogger) log).taskFinished();
    		            }
						thread = null;
					}
				};
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			} else {
				JOptionPane.showMessageDialog(this, THREAD_BUSY_MESSAGE, THREAD_BUSY_TITLE, JOptionPane.WARNING_MESSAGE);
			}
			
		}
		
	}
	
	/**
	 * Apply filter for rules and metrics
	 */
	@SuppressWarnings("unchecked")
	private static void applyFilter() {

		/* Lists for filters */
		List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>();
		
		/* Rule's filter */
		RowFilter<Object, Object> rulesFilter = (RowFilter<Object, Object>) buildRulesFilter(comboFilterComponent.getText().trim());
		if (rulesFilter != null) {
			filters.add(rulesFilter);
		}
		
		/* Filter for metrics */
		RowFilter<Object, Object> metricsFilter = (RowFilter<Object, Object>) buildMetricsFilter();
		if (metricsFilter != null) {
			filters.add(metricsFilter);
		}
		
		/* Join rule and metric filters */
		RowFilter<Object, Object> andFilter = RowFilter.andFilter(filters);
		
		/* Apply filter to table */
		sorter.setRowFilter(andFilter);
		
		/* Update counting of filtered values */
		lblFilteredRulesValue.setText(String.valueOf(table.getRowCount()));
		
		/* Update max value reference for ProgressCellRenderer */
		
		List<?> sortKeys = sorter.getSortKeys();
		
		if (sortKeys.size() > 0) {
									
			RowSorter.SortKey key = (SortKey) sortKeys.get(0);
			TableModel tableModel = table.getModel();
			RulesTableColumnModel columnModel = table.getColumnModel();
			String columnName = tableModel.getColumnName(key.getColumn());
			if (columnModel.hasColumn(columnName)) {
				int columnIndex = columnModel.getColumnIndex(columnName);
				TableCellRenderer cellRenderer = columnModel.getColumn(columnIndex).getCellRenderer();
				if (cellRenderer instanceof ProgressCellRenderer) {
					((ProgressCellRenderer) cellRenderer).updateMaxValue(table, columnIndex);
				}
			}
			
		}
	}

	/**
	 * Loads rules
	 *
	 * @param  rules  the association rules
	 */
	public static void loadRules(final AssociationRules rules) {
		
		boolean proceed = true;
		
		if (table.getRowCount() > 0) {
			int response = JOptionPane.showConfirmDialog(
					null
					, "A set of rules is already loaded. Do you want to continue?"
					, "Warning"
					, JOptionPane.YES_NO_OPTION
					, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.NO_OPTION) {
				proceed = false;
			}
		}
        
		// TODO: Not working (ilegal access error) since 3.8.1
		// Cause: new classloader scheme
        /*if (Explorer.m_Memory.memoryIsLow()) {
        	proceed = Explorer.m_Memory.showMemoryIsLow();
        }*/
		
		if (proceed) {
			
			if (thread == null) {
				thread = new Thread() {
					@Override
					public void run() {
						
						try {

	    		            if (log instanceof TaskLogger) {
	    		            	((TaskLogger) log).taskStarted();
	    		            }
	    					
	    					log.statusMessage("Loading rules...");
			
	    					if (setAssociationRules(rules)) {
								loadRules();
							} else {
								JOptionPane.showMessageDialog(
										null
										, "This set of rules is already loaded. No action will be applied."
										, "Warning"
										, JOptionPane.WARNING_MESSAGE);
							}
							
							thread = null;
	    					
	    					log.statusMessage("OK");
	    					
	    		            if (log instanceof TaskLogger) {
	    		            	((TaskLogger) log).taskFinished();
	    		            }
							
						} catch (Exception e) {

							log.statusMessage("FAILED! See log.");
							log.logMessage(e.getMessage());
	    					
	    		            if (log instanceof TaskLogger) {
	    		            	((TaskLogger) log).taskFinished();
	    		            }
							thread = null;
							
						}
						
					};
				};
				thread.setPriority(Thread.MIN_PRIORITY);
    			thread.start();
			} else {
				JOptionPane.showMessageDialog(null, THREAD_BUSY_MESSAGE, THREAD_BUSY_TITLE, JOptionPane.WARNING_MESSAGE);
			}
		
		}
		
	}
	
	/**
	 * Loads rules into a JTable
	 */
	public static void loadRules() {
		
		/* Clears table model by setting a new empty one */
		table.setModel(new RulesTableModel());
		
		/* Disable components while running loading process */
		setEnabledAll(rulesPanel, false);
		
		/* Reset components values */
		comboFilter.removeAllItems();
		antecedentMap.removeAll();
		consequentMap.removeAll();	
		spinDecimal.reset();
		
		/* Reset counters */
		lblTotalRulesValue.setText("0");
		lblFilteredRulesValue.setText("0");
		
		/* The association rules */
		List<AssociationRule> rulesList = associationRules.getRules();
		
		/* Create a new table model */
		final RulesTableModel tableModel = new RulesTableModel();
	
		/* Metric names */
		String[] metrics = rulesList.get(0).getMetricNamesForRule();
		
		/* Columns */
		tableModel.addColumn("Antecedent (X)");
		tableModel.addColumn("Consequent (Y)");
		tableModel.addColumn("Support");
		for (String m : metrics) {
			tableModel.addColumn(m);
		}
		
		/* Adds rules to table */
		
		String antecedent = "";
		String consequent = "";
		double support;
		double metricValue;
				
		for (AssociationRule r : rulesList) {
			
			/* Antecedent */
			antecedent = "";
			for (Item p : r.getPremise()) {
				antecedent += p + " ";
				antecedentMap.addAttribute(p.toString());
			}
			
			/* Consequent */
			consequent = "";
			for (Item c : r.getConsequence()) {		
				consequent += c + " ";
				consequentMap.addAttribute(c.toString());
			}
	
			/* Support */
			support = ((double) r.getTotalSupport()) / r.getTotalTransactions();
			
			/* List containing each column's content to be added to table */
			List<Object> row = new ArrayList<Object>();
			
			/* Adds antecedent, consequent and support */
			row.add(antecedent.trim());
			row.add(consequent.trim());
			row.add(support);
			
			/* Adds metric values */
			for (String m: metrics) {
				try {
					metricValue = r.getNamedMetricValue(m);
					row.add(metricValue);
				} catch (Exception e) {
					log.statusMessage("FAILED! See log.");
					log.logMessage(e.getMessage());
				}
			}
			
			/* Adds a row with values to table */
			tableModel.addRow(row.toArray());
			
		}
		
		/* Set TableModel */
		table.setModel(tableModel);
		
		/* Get column model */
		final RulesTableColumnModel columnModel = (RulesTableColumnModel) table.getColumnModel();
		
		/* Set minimum widths for antecedent and consequent columns */
		columnModel.getColumn(0).setMinWidth(220);
		columnModel.getColumn(1).setMinWidth(120);
		
		/* Set custom renderer for antecedent and consequent cells */
		columnModel.getColumn(0).setCellRenderer(new RulesTableCellRenderer());
		columnModel.getColumn(1).setCellRenderer(new RulesTableCellRenderer());
		
		for (int i = 2; i < table.getColumnCount(); i++) {
			
			/* Set custom renderer for metric's cells */
			columnModel.getColumn(i).setCellRenderer(decimalCellRenderer);
			
			/* Set minimum size values for metric's columns */
			columnModel.getColumn(i).setPreferredWidth(105);
			columnModel.getColumn(i).setMinWidth(70);
			columnModel.getColumn(i).setMaxWidth(120);
			
		}
				
		/* Set table's RowSorter for sorting and filterig */
		sorter = new TableRowSorter<TableModel>(tableModel);
		table.setRowSorter(sorter);
		table.getRowSorter().addRowSorterListener(new RowSorterListener() {
			
			// TODO: Improve sorting performance.
			@Override
			public void sorterChanged(RowSorterEvent e) {
				
				if (e.getType() == Type.SORTED) {
					
					Enumeration<TableColumn> columns = columnModel.getColumns();
					TableColumn column;
					while (columns.hasMoreElements()) {
						column = columns.nextElement();
						if (column.getCellRenderer() instanceof ProgressCellRenderer) {
							column.setCellRenderer(decimalCellRenderer);
						}
					}
					
					List<?> sortKeys = e.getSource().getSortKeys();
									
					if (sortKeys.size() > 0) {
												
						RowSorter.SortKey key = (SortKey) sortKeys.get(0);
						int keyColumn = key.getColumn();
						String columnName = tableModel.getColumnName(keyColumn);
						if (columnModel.hasColumn(columnName)) {
							int columnIndex = columnModel.getColumnIndex(columnName);
							if (tableModel.getColumnClass(columnIndex) == Double.class) {
								columnModel.getColumn(columnIndex).setCellRenderer(new ProgressCellRenderer(table, columnIndex));
							}
						}
						
					}
					
				}
				
			}
			
		});
		
		/* Get total values for results */
		lblTotalRulesValue.setText(String.valueOf(rulesList.size()));
		lblFilteredRulesValue.setText(String.valueOf(table.getRowCount()));
		
		/* Initializes attribute's component */
		comboRuleSide.removeAllItems();
		comboRuleSide.addItem(new ComboBoxItem(0, "Antecedent (X)", "X"));
		comboRuleSide.addItem(new ComboBoxItem(1, "Consequent (Y)", "Y"));
		
		/* Initializes filter parameters for metrics */
		initMetrics();
		
		setEnabledAll(rulesPanel, true);
		comboLogicalOperator.setEnabled(false);
		btnApply.setEnabled(false);
		btnClear.setEnabled(false);
		btnApplyMetric.setEnabled(false);
		btnResetMetric.setEnabled(false);
		
	}

	/**
	 * Returns the {@link AssociationRules} object
	 * 
	 * @return the rules
	 */
	public static AssociationRules getAssociationRules() {
		
		return associationRules;
		
	}

	/**
	 * Sets the AssociationRules object
	 * 
	 * @param associationRules the rules
	 * @return <code>true</code> if parameter has new value, <code>false</code> otherwise
	 */
	public static boolean setAssociationRules(AssociationRules associationRules) {
		
		AssociationRules oldValue = PostprocessAssociationsPanel.associationRules;
		AssociationRules newValue = associationRules;
		boolean isNew = false;
		
		if (!newValue.equals(oldValue)) {
			PostprocessAssociationsPanel.associationRules = associationRules;
			propertyChangeSupport.firePropertyChange("associationRules", oldValue, associationRules);
			isNew = true;
		}
		
		return isNew;
		
	}
	
	/**
	 * Enable/disable all child {@link Component} in a {@link Container}.
	 * {@link Component} type must be equals to one of types in list.
	 * 
	 * @param container the {@link Container} component
	 * @param enabled true/false for enable or disable each component
	 */
	private static void setEnabledAll(Container container, boolean enabled) {
		
		List<Class<?>> types = new ArrayList<Class<?>>();
		types.add(JComboBox.class);
		types.add(JButton.class);
		types.add(JCheckBox.class);
		types.add(FilterComboBox.class);
		types.add(DecimalSpinner.class);
		
		for (Component c : container.getComponents()) {
			
			if (c instanceof Container) {
				setEnabledAll((Container) c, enabled);
			}
			
			if (types.contains(c.getClass())) {
				c.setEnabled(enabled);
			}
			
		}
		
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

	/**
	 * Sets the Logger to receive informational messages
	 * 
	 * @param newLog the Logger that will now get info messages
	 */
	@Override
	public void setLog(Logger newLog) {
		log = newLog;
	}

}

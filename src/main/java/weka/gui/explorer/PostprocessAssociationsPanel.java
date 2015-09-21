package weka.gui.explorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.util.List;
import java.util.Map;
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
import javax.swing.table.DefaultTableModel;
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
import arpp.ComboBoxItem;
import arpp.DecimalFormatCellRenderer;
import arpp.FilterComboBox;
import arpp.FilterMap;
import arpp.FilterMapAttribute;
import arpp.MetricSpinner;
import arpp.ProgressCellRenderer;
import arpp.RulesCellRenderer;
import arpp.RulesTableColumnModel;
import arpp.RulesTableModel;
import arpp.Utils;

/**
 * A JPanel to visualize association rules
 *
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class PostprocessAssociationsPanel extends JPanel implements ExplorerPanel, LogHandler {

	/** for serialization */
	private static final long serialVersionUID = 3114490118265884877L;
	
	/** Message for busy thread */
	private static final String THREAD_BUSY_MESSAGE = "Can't execute at this time. Currently busy with other operation.";
	
	/** Dialog title for busy thread */
	private static final String THREAD_BUSY_TITLE = "Operation in progress";
	
	/** A thread for loading/saving/exporting rules and others */
	private static Thread thread;
	
	/** The parent frame */
	protected Explorer explorer = null;
		
	/** The destination for log/status messages */
	protected static Logger log = new SysErrLog();

	/** Component where rules are loaded */
	protected static JTable table;
	
	/** For sorting and filtering */
	protected static TableRowSorter<TableModel> sorter;
	
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
	
	/** Component to select logical operator to be used in filter */
	protected static JComboBox<String> comboLogicalOperator;
	
	/** Component to select antecedent or consequent column in which filter will be applied */
	protected static JComboBox<ComboBoxItem> comboTableColumn;
	
	/**  */
	protected static JComboBox<String> comboComparisonType;
	
	protected static JComboBox<ComboBoxItem> comboAttribute;
	
	protected static JComboBox<String> comboLabel;

	protected static JButton btnAdd;
	
	protected static FilterComboBox comboFilter = new FilterComboBox();
	
	protected static JTextField comboFilterComponent = (JTextField) comboFilter.getEditor().getEditorComponent();
	
	protected static JButton btnApply;
	
	protected static JButton btnClear;
	private static JPanel rulesPanel;
	private JPanel buttonsPanel;
	protected JPanel filterPanel;
	protected JPanel tablePanel;
	protected static JButton btnSave;
	private JButton btnOpen;
	protected static JButton btnExport;

	/** the association rules */
	private static AssociationRules associationRules;
	
	/** The file chooser for save/open rules and apply filters. */
	private JFileChooser fileChooserSaveOpen = new JFileChooser(new File(System.getProperty("user.dir")));
	
	/** The file chooser for export rules. */
	private JFileChooser fileChooserExport = new JFileChooser(new File(System.getProperty("user.dir")));
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
	private JPanel panel;
	private JLabel lblLogicalOperator;
	private JLabel lblXy;
	private JLabel lblComparisonType;
	private JLabel lblAttribute;
	private JLabel lblLabel;
	private JPanel rulesFilterPanel;
	
	private static ChangeListener metricChangeListener;
	private JPanel metricButtonsPanel;
	
	/**
	 * Create the postprocess panel.
	 */
	public PostprocessAssociationsPanel() {
		
		setLayout(new BorderLayout(0, 0));
		setFocusable(true);
		
		FileFilter fileFilter = new FileNameExtensionFilter("CSV file: comma separated files", new String[] {"csv"});
		fileChooserExport.addChoosableFileFilter(fileFilter);
		fileChooserExport.setFileFilter(fileFilter);
		
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
		
		lblXy = new JLabel("Antecedent / Consequent:");
		GridBagConstraints gbc_lblXy = new GridBagConstraints();
		gbc_lblXy.anchor = GridBagConstraints.WEST;
		gbc_lblXy.insets = new Insets(0, 0, 5, 5);
		gbc_lblXy.gridx = 1;
		gbc_lblXy.gridy = 0;
		rulesFilterPanel.add(lblXy, gbc_lblXy);
		lblXy.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblXy.setLabelFor(comboTableColumn);
		
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
		
		comboLogicalOperator = new JComboBox<String>();
		GridBagConstraints gbc_comboLogicalOperator = new GridBagConstraints();
		gbc_comboLogicalOperator.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboLogicalOperator.insets = new Insets(0, 0, 5, 5);
		gbc_comboLogicalOperator.gridx = 0;
		gbc_comboLogicalOperator.gridy = 1;
		rulesFilterPanel.add(comboLogicalOperator, gbc_comboLogicalOperator);
		comboLogicalOperator.setEnabled(false);
		comboLogicalOperator.setModel(new DefaultComboBoxModel<String>(new String[] {"AND", "OR"}));
		
		comboTableColumn = new JComboBox<ComboBoxItem>();
		GridBagConstraints gbc_comboTableColumn = new GridBagConstraints();
		gbc_comboTableColumn.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboTableColumn.insets = new Insets(0, 0, 5, 5);
		gbc_comboTableColumn.gridx = 1;
		gbc_comboTableColumn.gridy = 1;
		rulesFilterPanel.add(comboTableColumn, gbc_comboTableColumn);
		comboTableColumn.setEnabled(false);
		comboTableColumn.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (comboTableColumn.getSelectedIndex() > -1) {
					comboAttribute.removeAllItems();
					int key = (Integer) ((ComboBoxItem) comboTableColumn.getSelectedItem()).getKey();
					for (FilterMapAttribute f : filterMap[key].getAttributes()) {
						comboAttribute.addItem(new ComboBoxItem(f, f.getAttribute()));
					}
				}
			}
		});
		
		comboComparisonType = new JComboBox<String>();
		GridBagConstraints gbc_comboComparisonType = new GridBagConstraints();
		gbc_comboComparisonType.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboComparisonType.insets = new Insets(0, 0, 5, 5);
		gbc_comboComparisonType.gridx = 2;
		gbc_comboComparisonType.gridy = 1;
		rulesFilterPanel.add(comboComparisonType, gbc_comboComparisonType);
		comboComparisonType.setEnabled(false);
		comboComparisonType.setModel(new DefaultComboBoxModel<String>(new String[] {"CONTAINS", "EQUALS"}));
		
		comboAttribute = new JComboBox<ComboBoxItem>();
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
		
		comboLabel = new JComboBox<String>();
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
		btnApplyMetric.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyMetricsFilter();
			}
		});
		btnApplyMetric.setEnabled(false);
		metricButtonsPanel.add(btnApplyMetric);
		
		btnResetMetric = new JButton("Reset");
		metricButtonsPanel.add(btnResetMetric);
		btnResetMetric.setEnabled(false);
		btnResetMetric.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetMetricsFilter();
			}
		});
		
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
		
		table = new JTable();
		table.setComponentPopupMenu(tablePopupMenu);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = table.getSelectedRow();
					filterSubSet(table, row);
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
		        Point point = e.getPoint();
		        int currentRow = table.rowAtPoint(point);
		        table.setRowSelectionInterval(currentRow, currentRow);
			}
		});
		
		/* Forces tab selection every time the data model in table is changed.
		 * I need to select this tab after click on visualization menu item
		 * at associate tab. It's the only way i've found to do that.
		 * Probabily is not the better way.
		 */
		table.addPropertyChangeListener("model", new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				selectTab();
			}
			
		});
		
		scrollPaneTable.setViewportView(table);

	}
	
	private void onComboFilterStateChanged() {

		boolean isEmpty = comboFilterComponent.getText().isEmpty();
		comboLogicalOperator.setEnabled(!isEmpty);
		btnApply.setEnabled(!isEmpty);
		
	}

	private static void onMetricValueChange() {
		
		btnApplyMetric.setEnabled(true);
		btnResetMetric.setEnabled(true);
		
	}

	// TODO: Getting thread busy message when selecting/deselecting a metric
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
						if (sorter.getSortKeys().contains(metricName)) {
							List<SortKey> sortKeys = Collections.emptyList();
							sorter.setSortKeys(sortKeys);
						}
						columnModel.hideColumn(metricName);
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

	private static void onMetricPropertyChange(PropertyChangeEvent e) {
	
		JCheckBox chkbx = (JCheckBox) e.getSource();
		MetricSpinner spinner = (MetricSpinner) metricSpinnerMap.get(chkbx.getText());
		if (chkbx.isSelected()) {
			spinner.setEnabled(chkbx.isEnabled());
		} else {
			spinner.setEnabled(false);
		}
		
	}

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
						
						log.statusMessage("Exporting rules...");
						
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
						// TODO: Write each row to file instead write all data once
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

	@SuppressWarnings("unchecked")
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
							
							log.statusMessage("Reading file...");
							FileInputStream fis = new FileInputStream(file);
							ObjectInputStream ois = new ObjectInputStream(fis);
							associationRules = (AssociationRules) ois.readObject();
							log.statusMessage("Loading rules...");
							loadRules();
							comboFilter.setModel((ComboBoxModel<Object>) ois.readObject());
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
						
						log.statusMessage("Saving rules...");
						
						File file = fileChooserSaveOpen.getSelectedFile();
						try {
											
							FileOutputStream fos = new FileOutputStream(file);
							ObjectOutputStream oos = new ObjectOutputStream(fos);
							oos.writeObject(associationRules);
							oos.writeObject(comboFilter.getModel());
							oos.writeObject(Utils.getVersion());
							
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
		
		metricChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				onMetricValueChange();
			}
		};
		
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
			
			spinner.addChangeListener(metricChangeListener);
			
		}
		
		for (Component c : metricsFilterPanel.getComponents()) {
			
			if (c instanceof JCheckBox) {
				
				/* Listen to JCheckBox checked status to show/hide columns */
				((JCheckBox) c).addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						onMetricVisibilityChange(e);
					}
				});
				
				/* Listen to JCheckBox enabled property to
				 * change respective JSpinner enabled property
				 */
				((JCheckBox) c).addPropertyChangeListener("enabled", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						onMetricPropertyChange(e);
					}
				});
				
			}
			
		}
	
		
	}

	private void addFilter() {
		
		String column = ((ComboBoxItem) comboTableColumn.getSelectedItem()).getValue();
		String comparisonOperator = (String) comboComparisonType.getSelectedItem();
		String logicalOperator = (String) comboLogicalOperator.getSelectedItem();
		String attribute = comboAttribute.getSelectedItem().toString();
		String label = (String) comboLabel.getSelectedItem();
		
		addFilter(column, comparisonOperator, logicalOperator, attribute, label);
		
	}

	/**
	 * @param column
	 * @param comparisonOperator
	 * @param logicalOperator
	 * @param attribute
	 * @param label
	 */
	private void addFilter(String column, String comparisonOperator, String logicalOperator, String attribute, String label) {

		Pattern p;
	    Matcher m = null;
		String componentText = comboFilterComponent.getText();
		String allLabels = "";
		String filter = "";
		boolean found = false;
		
		filter += column;
		if (comparisonOperator.equals("EQUALS")) {
			p = Pattern.compile(".*"+column+"\\[\\^(.*?)\\$\\]");
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
		filter += attribute;
		filter += (!label.equals("All")) ? "=" + label : "";
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
			int lastIndex = componentText.lastIndexOf(m.group(m.groupCount()));
			int start = componentText.indexOf("^", lastIndex - 1);
			int end = componentText.indexOf("$", lastIndex);
			String content = componentText.substring(start+1, end);
			boolean contains = false;
			if (label.equals("All")) {
				contains = content.contains(attribute + allLabels);
			} else {
				contains = content.contains(attribute + "=" + label);
			}
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

	private static RowFilter<? super TableModel, ? super Integer> buildMetricsFilter() {
			
			RowFilter<Object, Object> rowFilter = RowFilter.regexFilter(".*");
			List<RowFilter<Object, Object>> metricFilters = new ArrayList<RowFilter<Object, Object>>();
			List<RowFilter<Object, Object>> metricFilter;
			RowFilter<Object, Object> filterEqual;
			RowFilter<Object, Object> filterAfter;
			RulesTableColumnModel columnModel = (RulesTableColumnModel) table.getColumnModel();
			String key;
			double value;
			int columnIndex;
			
			for (Map.Entry<String, JSpinner> entry : metricSpinnerMap.entrySet()) {
				metricFilter = new ArrayList<RowFilter<Object, Object>>();
				key = entry.getKey();
				value = (Double) (entry.getValue()).getValue();
				if (columnModel.hasColumn(key)) {
					columnIndex = table.getColumnModel().getColumnIndex(key);
					filterEqual = RowFilter.numberFilter(ComparisonType.EQUAL, value, table.convertColumnIndexToModel(columnIndex));
					filterAfter = RowFilter.numberFilter(ComparisonType.AFTER, value, table.convertColumnIndexToModel(columnIndex));
					metricFilter.add(filterEqual);
					metricFilter.add(filterAfter);
					metricFilters.add(RowFilter.orFilter(metricFilter));
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
	 * Applies filter to find a subset of rules as a result from
	 * generated subsets of a initial set of the antecedents in
	 * selected row keeping same consequent.
	 * @param table 
	 * 
	 * @param e	Mouse event
	 */
	private void filterSubSet(JTable target, int row) {
	
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
			attributeLabels += antecedentLabels.get(i);
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
		List<FilterMapAttribute> consequentAttributes = antecedentMap.getAttributes();
		List<String> consequentLabels = antecedentMap.getUniqueLabels();
		
		/* Get all attributes */
		attributeNames = "";
		for (int i = 0; i < consequentAttributes.size(); i++) {
			attributeNames += consequentAttributes.get(i).getAttribute();
			attributeNames += (i + 1) < consequentAttributes.size() ? "|" : "";
		}
		
		/* Get all labels */			
		attributeLabels = "";
		for (int i = 0; i < consequentLabels.size(); i++) {
			attributeLabels += consequentLabels.get(i);
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
		
		applyMetricsFilter();
		
	}

	/**
	 * Apply filter for rules
	 */
	private void applyRulesFilter() {
			
			boolean proceed = true;
		
		    if (Explorer.m_Memory.memoryIsLow()) {
		    	proceed = Explorer.m_Memory.showMemoryIsLow();
		    }
		    
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
								comboTableColumn.setSelectedIndex(0);
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
	
	    if (Explorer.m_Memory.memoryIsLow()) {
	    	proceed = Explorer.m_Memory.showMemoryIsLow();
	    }
	    
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
		
	    if (Explorer.m_Memory.memoryIsLow()) {
	    	proceed = Explorer.m_Memory.showMemoryIsLow();
	    }
	    
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
						
	}

	public static void loadRules(final AssociationRules rules) {
		
		boolean proceed = true;
		
		if (table.getRowCount() > 0) {
			int response = JOptionPane.showConfirmDialog(
					null
					, "A set rules is already loaded. Do you want to continue?"
					, "Warning"
					, JOptionPane.YES_NO_OPTION
					, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.NO_OPTION) {
				proceed = false;
			}
		}

        if (Explorer.m_Memory.memoryIsLow()) {
        	proceed = Explorer.m_Memory.showMemoryIsLow();
        }
		
		if (proceed) {
			
			if (thread == null) {
				Utils.setContainerEnabled(rulesPanel, false);
				thread = new Thread() {
					@Override
					public void run() {
						
						try {

	    		            if (log instanceof TaskLogger) {
	    		            	((TaskLogger) log).taskStarted();
	    		            }
	    					
	    					log.statusMessage("Loading rules...");
			
							associationRules = rules;
							loadRules();
							
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
	 *
	 * @param  rules  the association rules
	 */
	public static void loadRules() {
		
		Utils.setContainerEnabled(rulesPanel, false);
		
		comboFilter.removeAllItems();
		
		for (Component c : metricsFilterPanel.getComponents()) {
			if (c instanceof JSpinner) {
				((JSpinner) c).removeChangeListener(metricChangeListener);
			}
		}
		
		lblTotalRulesValue.setText("0");
		lblFilteredRulesValue.setText("0");
		
		table.setModel(new RulesTableModel());
		table.setColumnModel(new RulesTableColumnModel());
		table.getTableHeader().setReorderingAllowed(false);
		    					
		List<AssociationRule> rulesList = associationRules.getRules();
		
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
		final RulesTableColumnModel columnModel = (RulesTableColumnModel) table.getColumnModel();
		
		/* Set minimum widths for antecedent and consequent columns */
		columnModel.getColumn(0).setMinWidth(220);
		columnModel.getColumn(1).setMinWidth(120);
		
		/* Set custom renderer for antecedent and consequent cells */
		columnModel.getColumn(0).setCellRenderer(new RulesCellRenderer());
		columnModel.getColumn(1).setCellRenderer(new RulesCellRenderer());
		
		for (int i = 2; i < table.getColumnCount(); i++) {
			
			/* Set custom renderer for metric's cells */
			columnModel.getColumn(i).setCellRenderer(new DecimalFormatCellRenderer());
			
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
							column.setCellRenderer(new DecimalFormatCellRenderer());
						}
					}
					
					List<?> sortKeys = e.getSource().getSortKeys();
									
					if (sortKeys.size() > 0) {
												
						RowSorter.SortKey key = (SortKey) sortKeys.get(0);
						int keyColumn = key.getColumn();
						
						// TODO: Too slow performance when scrolling table with 'ProgresCellRenderer' enabled.
						String columnName = tableModel.getColumnName(keyColumn);
						if (columnModel.hasColumn(columnName)) {
							int columnIndex = columnModel.getColumnIndex(columnName);
							if (tableModel.getColumnClass(columnIndex) == Double.class) {
								columnModel.getColumn(columnIndex).setCellRenderer(new ProgressCellRenderer());
							}
						}
						
					}
					
				}
				
			}
			
		});
		
		/* Adds rules to table */
		
		String antecedent = "";
		String consequent = "";
		double supportTemp;
		double support;
		double metricValue;
		double roundedValue;
		
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
			supportTemp = ((double) r.getTotalSupport()) / r.getTotalTransactions();
			support = weka.core.Utils.roundDouble(supportTemp, 2);
			
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
					roundedValue = weka.core.Utils.roundDouble(metricValue, 2);
					row.add(roundedValue);
				} catch (Exception e) {
					log.statusMessage("FAILED! See log.");
					log.logMessage(e.getMessage());
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
		
		/* Initializes filter parameters for metrics */
		initMetrics();
		
		Utils.setContainerEnabled(rulesPanel, true);
		comboLogicalOperator.setEnabled(false);
		btnApply.setEnabled(false);
		btnClear.setEnabled(false);
		btnApplyMetric.setEnabled(false);
		btnResetMetric.setEnabled(false);
		
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
		log = newLog;
	}

}

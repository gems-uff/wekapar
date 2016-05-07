package arpp.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import arpp.Utils;

/**
 * <p>
 * Extends {@link JProgressBar} and implements {@link TableCellRenderer} to
 * display a bar representing the current value relative to max value.
 * </p>
 * 
 * <p>
 * The values are formatted to a {@link DecimalFormat} defined by table model.
 * </p>
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class ProgressCellRenderer extends JProgressBar implements TableCellRenderer {

	/** for serialization */
	private static final long serialVersionUID = -5466571908339798798L;
	
	/** Max value of column */
	private double maxValue;
	
	/**
	 * Constructs a {@link ProgressCellRenderer} that is initialized with table
	 * as the {@link JTable} component, column as the target {@link TableColumn}
	 * and sets {@link JProgressBar} properties.
	 * 
	 * @param table the {@link JTable} component
	 * @param column the target {@link TableColumn}
	 */
	public ProgressCellRenderer(JTable table, int column) {

		super();
		
		maxValue = Utils.getColumnMaxValue(table, column);
		
		setStringPainted(true);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setBackground(SystemColor.textHighlightText);
        
        setUI(new BasicProgressBarUI() {
        	@Override
        	protected Color getSelectionBackground() {
        		return Color.BLACK;
        	}
        });
		
	}

	/**
	 * Sets the progress bar's current value, applies a format to values
	 * according to {@link DecimalFormat} and returns the
	 * {@link TableCellRenderer}.
	 * 
	 * @return the custom {@link TableCellRenderer} component
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		int progress = (int) Math.round(((Math.abs((Double) value) / maxValue)) * 100);
        setValue(progress);
		
		if (value instanceof String) {
			value = Double.valueOf((String) value);
		}
		
		if ((Double) value < 0) {
			setForeground(Color.RED);
		} else {
			setForeground(SystemColor.textHighlight);
		}
        
		DecimalFormat formatter = ((RulesTableModel) table.getModel()).getFormatter();
		value = formatter.format((Number) value);
        setString(value.toString());
        
        return this;
        
	}

}

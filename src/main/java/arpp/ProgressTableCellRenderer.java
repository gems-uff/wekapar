package arpp;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.TableCellRenderer;

public class ProgressTableCellRenderer extends JProgressBar implements TableCellRenderer {

	/** for serialization */
	private static final long serialVersionUID = -5466571908339798798L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		double maxValue = Utils.getColumnMaxValue(table, column);
		int progress = (int) Math.round((((double) value / maxValue)) * 100);
        setValue(progress);
        setString(value.toString());
        
        setStringPainted(true);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setBackground(Color.WHITE);
        setForeground(Color.BLUE);
        
        setUI(new BasicProgressBarUI() {
        	@Override
        	protected Color getSelectionBackground() {
        		return Color.BLACK;
        	}
        });
        
        return this;
        
	}

}

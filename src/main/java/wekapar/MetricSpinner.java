package wekapar;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * An extended {@link JSpinner} to select metric values
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class MetricSpinner extends JSpinner {
	
	/** for serialization */
	private static final long serialVersionUID = -4269839150816724708L;
	
	/**
	 * Sets a {@link NumberEditor} with support for two decimal places
	 * 
	 * @param editor Unused
	 */
	@Override
	public void setEditor(JComponent editor) {

		super.setEditor(new JSpinner.NumberEditor(this, "#0.00"));
		
	}

	/**
	 * Resets to minimum value
	 */
	public void reset() {
		
		SpinnerNumberModel model = (SpinnerNumberModel) getModel();
		double min = (Double) model.getMinimum();
		setValue(min);
		
	}
	
	/**
	 * Checks if minimum value is selected
	 * 
	 * @return <code>true</code> if the minimum value is selected, <code>false</code> otherwise.
	 */
	public boolean isMinimumSelected() {
				
		SpinnerNumberModel spinModel = (SpinnerNumberModel) getModel();
		double spinModelValue = (Double) spinModel.getValue();
		double spinModelMin = (Double) spinModel.getMinimum();
		
		return (spinModelValue == spinModelMin);
		
	}

}

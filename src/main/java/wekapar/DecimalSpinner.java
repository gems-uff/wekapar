package wekapar;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * A simple extended {@link JSpinner} to select decimal places
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class DecimalSpinner extends JSpinner {

	/** for serialization */
	private static final long serialVersionUID = 1768214831759246226L;

	/**
	 * Resets to minimum value
	 */
	public void reset() {
		
		SpinnerNumberModel model = (SpinnerNumberModel) getModel();
		int min = (Integer) model.getMinimum();
		setValue(min);
		
	}

}

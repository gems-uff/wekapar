package arpp;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class DecimalSpinner extends JSpinner {

	/** for serialization */
	private static final long serialVersionUID = 1768214831759246226L;

	public void reset() {
		
		SpinnerNumberModel model = (SpinnerNumberModel) getModel();
		int min = (Integer) model.getMinimum();
		setValue(min);
		
	}

}

package arpp;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class MetricSpinner extends JSpinner {
	
	/** for serialization */
	private static final long serialVersionUID = -4269839150816724708L;
	
	@Override
	public void setEditor(JComponent editor) {

		super.setEditor(new JSpinner.NumberEditor(this, "#0.00"));
		
	}

	public void reset() {
		
		SpinnerNumberModel model = (SpinnerNumberModel) getModel();
		double min = (double) model.getMinimum();
		setValue(min);
		
	}

}

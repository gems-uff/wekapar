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
	
	public boolean isMinimumSelected() {
		
		boolean isMinimum = true;
		
		SpinnerNumberModel spinModel = (SpinnerNumberModel) getModel();
		double spinModelValue = (double) spinModel.getValue();
		double spinModelMin = (double) spinModel.getMinimum();
		
		if (spinModelValue > spinModelMin) {
			isMinimum = false;
		}
		
		return isMinimum;
		
	}

}

/**
 * 
 */
package arpp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel
 *
 */
public class FilterMapAttribute implements Serializable {
	
	/** for serialization */
	private static final long serialVersionUID = 1630080590430238566L;
	private String attribute;
	private List<String> labels = new ArrayList<String>();
	
	/**
	 * @param attribute2
	 */
	public FilterMapAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getAttribute() {
		return attribute;
	}
	
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	public List<String> getLabels() {
		return labels;
	}
	
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	
	public void addLabel(String label) {
		if (!labels.contains(label)) {
			labels.add(label);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return attribute.equals(((FilterMapAttribute) o).attribute);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString(java.lang.Object)
	 */
	@Override
	public String toString() {
		return getAttribute();
	}

}

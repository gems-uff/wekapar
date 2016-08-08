package wekapar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data structure to store an attribute and its labels of antecedent or
 * consequent of the rule.
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class FilterMapAttribute implements Serializable {
	
	/** for serialization */
	private static final long serialVersionUID = 1630080590430238566L;
	
	/** The attribute name */
	private String attribute;
	
	/** The list of attribute labels */
	private List<String> labels = new ArrayList<String>();
	
	/**
	 * Constructs a {@link FilterMapAttribute} that is initialized with the
	 * attribute name
	 * 
	 * @param attribute the attribute name
	 */
	public FilterMapAttribute(String attribute) {
		this.attribute = attribute;
	}

	/**
	 * Gets the attribute name
	 * 
	 * @return the attribute name
	 */
	public String getAttribute() {
		return attribute;
	}
	
	/**
	 * Sets the attribute name
	 * 
	 * @param attribute the attribute name
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	/**
	 * Gets the list of attribute labels
	 * 
	 * @return the list of attribute labels
	 */
	public List<String> getLabels() {
		return labels;
	}
	
	/**
	 * Sets the list of attribute labels
	 * 
	 * @param labels the list of attribute labels
	 */
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	
	/**
	 * Adds a label to the list of attribute labels, if not exists.
	 * 
	 * @param label the label
	 */
	public void addLabel(String label) {
		if (!labels.contains(label)) {
			labels.add(label);
		}
	}
	
	/**
	 * Compares this attribute to the specified object
	 * 
	 * @param o the object to compare this attribute against
	 * @return <code>true</code> if the given object is equivalent to this
	 *         attribute, <code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object o) {
		return attribute.equals(((FilterMapAttribute) o).attribute);
	}
	
	/**
	 * Returns the attribute name as string representation of the object
	 * 
	 * @return the attribute name
	 */
	@Override
	public String toString() {
		return getAttribute();
	}

}

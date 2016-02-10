package arpp;

import java.util.ArrayList;
import java.util.List;

/**
 * A data structure to manage attributes and its labels of antecedent or
 * consequent of the rule as a {@link FilterMapAttribute}. This way all
 * attributes can be mapped to be used for filtering purposes.
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class FilterMap {
	
	/** List of attributes */
	private List<FilterMapAttribute> attributes = new ArrayList<FilterMapAttribute>();
	
	/**
	 * Returns the list of attributes
	 * 
	 * @return the list of attributes
	 */
	public List<FilterMapAttribute> getAttributes() {
		
		return attributes;
		
	}
	
	/**
	 * Gets a list of labels with unique values
	 * 
	 * @return the list of labels
	 */
	public List<String> getUniqueLabels() {
		
		List<String> uniqueLabels = new ArrayList<String>();
		
		for (FilterMapAttribute attribute : attributes) {
			for (String label : attribute.getLabels()) {
				if (!uniqueLabels.contains(label)) {
					uniqueLabels.add(label);
				}
			}
		}
		
		return uniqueLabels;
		
	}
	
	/**
	 * Sets the lits of attributes
	 */
	public void setAttributes(List<FilterMapAttribute> attributes) {
		
		this.attributes = attributes;
		
	}
	
	/**
	 * Adds an attribute to the list if not exists yet, otherwise the label list
	 * is updated.
	 */
	public void addAttribute(String attribute) {
		
		String[] splitAttribute = attribute.split("=", 2);
		
		if (splitAttribute.length > 1) {
			
			String attributeName = splitAttribute[0];
			String attributeLabel = splitAttribute[1];
			
			FilterMapAttribute filterMapAttribute = new FilterMapAttribute(attributeName);
			
			if (!attributes.contains(filterMapAttribute)) {
				filterMapAttribute.addLabel(attributeLabel);
				attributes.add(filterMapAttribute);
			} else {
				for (FilterMapAttribute a : attributes) {
					if (attributeName.equals(a.getAttribute())) {
						a.addLabel(attributeLabel);
						break;
					}
				}
			}
			
		}
		
	}
	
	/**
	 * Removes all attributes
	 * 
	 * @return <code>true</code> if all elements of list were removed
	 */
	public boolean removeAll() {

		return attributes.removeAll(attributes);
		
	}

}

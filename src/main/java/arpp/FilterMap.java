/**
 * 
 */
package arpp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel
 *
 */
public class FilterMap {
	
	private List<FilterMapAttribute> attributes = new ArrayList<FilterMapAttribute>();
		
	public List<FilterMapAttribute> getAttributes() {
		
		return attributes;
		
	}
	
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
	
	public void setAttributes(List<FilterMapAttribute> attributes) {
		
		this.attributes = attributes;
		
	}
	
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

}

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
	
//	private int column;
	private List<FilterMapAttribute> attributes = new ArrayList<>();
	
//	public int getColumn() {
//		return column;
//	}
//	
//	public void setColumn(int column) {
//		this.column = column;
//	}
	
	public List<FilterMapAttribute> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(List<FilterMapAttribute> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttributeValue(String attributeValue) {
		
		String[] splitAttributeValue = attributeValue.split("=");
		String attribute = splitAttributeValue[0];
		String value = splitAttributeValue[1];
		
		FilterMapAttribute filterMapAttribute = new FilterMapAttribute(attribute);
		
		if (!attributes.contains(filterMapAttribute)) {
			filterMapAttribute.addValue(value);
			attributes.add(filterMapAttribute);
		} else {
			for (FilterMapAttribute a : attributes) {
				if (attribute.equals(a.getAttribute())) {
					a.addValue(value);
					break;
				}
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
//	@Override
//	public boolean equals(Object o) {
//		return (column == ((FilterMap) o).column);
//	}

}

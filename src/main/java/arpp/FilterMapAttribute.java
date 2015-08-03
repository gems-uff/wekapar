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
public class FilterMapAttribute {
	
	private String attribute;
	private List<String> values = new ArrayList<>();
	
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
	
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	public void addValue(String value) {
		if (!values.contains(value)) {
			values.add(value);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return attribute.equals(((FilterMapAttribute) o).attribute);
	}

}

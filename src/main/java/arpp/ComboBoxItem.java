/**
 * 
 */
package arpp;

/**
 * @author Daniel
 *
 */
public class ComboBoxItem {
	
	private Object key;
	private String label;
	private String value;
	
	public ComboBoxItem(Object key, String label) {
		this.key = key;
		this.label = label;
	}
	
	public ComboBoxItem(Object key, String label, String value) {
		this.key = key;
		this.label = label;
		this.value = value;
	}
	
	public ComboBoxItem(String label, String value) {
		this.label = label;
		this.value = value;
	}
	
	public Object getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return label;
	}

}

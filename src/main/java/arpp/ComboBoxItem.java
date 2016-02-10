package arpp;

import java.io.Serializable;

import javax.swing.JComboBox;

/**
 * A custom {@link JComboBox} item with support for 3 attributes: key, label and
 * value
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class ComboBoxItem implements Serializable {
	
	/** for serialization */
	private static final long serialVersionUID = 3659464095024332565L;
	
	/** The item key */
	private Object key;
	
	/** The item label displayed in component */
	private String label;
	
	/** The item value */
	private String value;
	
	/**
	 * Constructs a {@link ComboBoxItem} that is initialized with key as the
	 * item key, label as the item label, and a null item value.
	 * 
	 * @param key the item key
	 * @param label the item label
	 */
	public ComboBoxItem(Object key, String label) {
		this(key, label, null);
	}
	
	/**
	 * Constructs a {@link ComboBoxItem} that is initialized with a null
	 * item key, label as the item label, and value as the item value.
	 * 
	 * @param label the item label
	 * @param value the item value
	 */
	public ComboBoxItem(String label, String value) {
		this(null, label, value);
	}
	
	/**
	 * Constructs a {@link ComboBoxItem} that is initialized with key as the
	 * item key, label as the item label, and value as the item value.
	 * 
	 * @param key the item key
	 * @param label the item label
	 * @param value the item value
	 */
	public ComboBoxItem(Object key, String label, String value) {
		this.key = key;
		this.label = label;
		this.value = value;
	}
	
	/**
	 * Returns the item key
	 * 
	 * @return the item key
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * Returns the item label
	 * 
	 * @return the item label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns the item value
	 * 
	 * @return the item value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Returns the item label as string representation of the object
	 * 
	 * @return the item label
	 */
	@Override
	public String toString() {
		return label;
	}

}

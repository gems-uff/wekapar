package arpp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of some useful methods.
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class Utils {
	
	/**
	 * Searches for a {@link Pattern} in {@link String} <code>s</code>
	 * 
	 * @param pattern a regex to search
	 * @param s the string to search in
	 * @return index of pattern in s or -1, if not found.
	 */
	public static int indexOf(String pattern, String s) {
		Pattern p = Pattern.compile(pattern);
	    Matcher m = p.matcher(s);
	    return m.find() ? m.start() : -1;
	}

}

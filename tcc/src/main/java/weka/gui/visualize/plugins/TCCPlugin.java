package weka.gui.visualize.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JMenuItem;

import tcc.TCCFrame;
import weka.associations.AssociationRules;
import weka.gui.visualize.plugins.AssociationRuleVisualizePlugin;

/**
 * Implements 
 * weka.gui.visualize.plugins.AssociationRuleVisualizePlugin
 * to create a JMenuItem and provides an alternative visualization
 * for association rules.
 * 
 * @author Daniel Silva (danielnsilva{[at]}gmail{[dot]}com)
 */
public class TCCPlugin implements Serializable, AssociationRuleVisualizePlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6193071884158893385L;

	@Override
	public JMenuItem getVisualizeMenuItem(final AssociationRules rules, final String name) {
		
		JMenuItem menuItem = new JMenuItem("TCCPlugin");
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				final TCCFrame frame = new TCCFrame();
				frame.loadRules(rules);
				frame.setTitle(name);
				frame.setVisible(true);
				
			}
		});
		return menuItem;
		
	}

}

package weka.gui.visualize.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JMenuItem;

import weka.associations.AssociationRules;
import weka.gui.explorer.PostprocessAssociationsPanel;

/**
 * Implements weka.gui.visualize.plugins.AssociationRuleVisualizePlugin
 * to create a JMenuItem and loads associator output in postprocess tab.
 * 
 * @author Daniel Silva (danielnsilva@gmail.com)
 */
public class PostprocessAssociationsPlugin implements Serializable, AssociationRuleVisualizePlugin {

	/** for serialization */
	private static final long serialVersionUID = 6193071884158893385L;

	@Override
	public JMenuItem getVisualizeMenuItem(final AssociationRules rules, final String name) {
		
		final JMenuItem menuItem = new JMenuItem("View in postprocess tab");
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				PostprocessAssociationsPanel.loadRules(rules);
				
			}
			
		});
		return menuItem;
		
	}

}
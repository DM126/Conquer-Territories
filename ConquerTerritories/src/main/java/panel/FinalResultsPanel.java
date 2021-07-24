package panel;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import map.*;

/**
 * Displays the results of a game.
 */
public class FinalResultsPanel extends JPanel
{
	private ConquerFrame parent;
	private JButton returnToMenu;
	private JButton exit;
	
	public FinalResultsPanel(ConquerFrame parent, ArrayList<Country> countries)
	{
		this.parent = parent;
		
		ListSorter.sortCountries(countries, ComparisonMethods.ALPHABETICAL);
		
		//(Create new arraylists so that alphabetical ordering will be used as a tie breaker)
		ResultsDisplayPanel endSizesPanel = new ResultsDisplayPanel("End Sizes", 
																	new ArrayList<Country>(countries),
																	ComparisonMethods.SIZE);
		
		ResultsDisplayPanel peakSizesPanel = new ResultsDisplayPanel("Peak Sizes", 
																	new ArrayList<Country>(countries), 
																	ComparisonMethods.PEAK_SIZE);
		
		ResultsDisplayPanel vanquishesPanel = new ResultsDisplayPanel("Vanquishes", 
																	new ArrayList<Country>(countries), 
																	ComparisonMethods.VANQUISHES);
		
		ResultsDisplayPanel largestAttackPanel = new ResultsDisplayPanel("Largest Attack", 
																	new ArrayList<Country>(countries), 
																	ComparisonMethods.LARGEST_ATTACK);
		
		ButtonListener listener = new ButtonListener();
		exit = ComponentFactory.createButton("Exit Game", "Close the program", listener, true);
		returnToMenu = ComponentFactory.createButton("Main Menu", "Return to the main menu", listener, true);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(exit);
		buttonPanel.add(returnToMenu);
		buttonPanel.setPreferredSize(new Dimension(returnToMenu.getPreferredSize().width * 2 + 20,
													returnToMenu.getPreferredSize().height + 10));
		
		add(endSizesPanel);
		add(peakSizesPanel);
		add(vanquishesPanel);
		add(largestAttackPanel);
		add(buttonPanel);
		
		setPreferredSize(new Dimension(endSizesPanel.getPreferredSize().width * 2 + 40, 
									endSizesPanel.getPreferredSize().height * 2 + buttonPanel.getPreferredSize().height + 20));
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (event.getSource() == exit)
			{
				System.exit(0);
			}
			else if (event.getSource() == returnToMenu)
			{
				parent.returnToMenu();
			}
		}
	}
}

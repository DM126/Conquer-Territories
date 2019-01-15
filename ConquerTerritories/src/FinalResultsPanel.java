import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

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
		
		ButtonListener listener = new ButtonListener();
		exit = ComponentFactory.createButton("Exit Game", "Close the program", listener, true);
		returnToMenu = ComponentFactory.createButton("Main Menu", "Return to the main menu", listener, true);
		
		//Write the end sizes in the endResults text area
		//(country) -> country.getSizeAsString());
		
		//Sort countries by peak size and write the results in the peakSizes text area
		//(country) -> country.getPeakSizeAsString());
		
		//Sort countries by number of vanquishes and write the results in the vanquishes text area
		//(country) -> country.getVanquishesAsString());
		
		//Sort countries by largest attack and write the results in the vanquishes text area
		//(country) -> country.getLargestAttackAsString());
		
		ResultsDisplayPanel endSizesPanel = new ResultsDisplayPanel("End Sizes", countries, ComparisonMethods.SIZE);
		ResultsDisplayPanel peakSizesPanel = new ResultsDisplayPanel("Peak Sizes", countries, ComparisonMethods.PEAK_SIZE);
		ResultsDisplayPanel vanquishesPanel = new ResultsDisplayPanel("Vanquishes", countries, ComparisonMethods.VANQUISHES);
		ResultsDisplayPanel largestAttackPanel = new ResultsDisplayPanel("Largest Attack", countries, ComparisonMethods.LARGEST_ATTACK);
		
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
		
		setPreferredSize(new Dimension(endSizesPanel.getPreferredSize().width * 4 + 40, 
									endSizesPanel.getPreferredSize().height + buttonPanel.getPreferredSize().height + 20));
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

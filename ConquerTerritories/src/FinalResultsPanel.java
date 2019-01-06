import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * Displays the results of a game: ending size and peak size.
 */
public class FinalResultsPanel extends JPanel
{
	private ConquerFrame parent;
	private JTextArea endResults;
	private JTextArea peakSizes;
	private JButton returnToMenu;
	private JButton exit;
	
	public FinalResultsPanel(ConquerFrame parent, ArrayList<Country> countries)
	{
		this.parent = parent;
		
		endResults = createTextArea();
		peakSizes = createTextArea();
		
		JScrollPane resultsScroll = new JScrollPane();
		resultsScroll.setViewportView(endResults);
		JScrollPane peakScroll = new JScrollPane();
		peakScroll.setViewportView(peakSizes);
		
		ButtonListener listener = new ButtonListener();
		exit = ComponentFactory.createButton("Exit Game", "Close the program", listener, true);
		returnToMenu = ComponentFactory.createButton("Main Menu", "Return to the main menu", listener, true);
		
		//Write the end sizes in the endResults text area
		writeToTextArea(endResults, countries, (country) -> country.getSize());
		
		//Sort countries by peak size
		ListSorter.sortCountries(countries, ListSorter.Methods.PEAK_SIZE);
		
		//Write the peak sizes in the peakSizes text area
		writeToTextArea(peakSizes, countries, (country) -> country.getPeakSize());
		
		Font titleFont = new Font("Arial", Font.PLAIN, 24);
		
		JPanel endResultsPanel = new JPanel();
		JLabel endResultsTitle = new JLabel("End Results");
		endResultsTitle.setFont(titleFont);
		endResultsPanel.add(endResultsTitle);
		endResultsPanel.add(resultsScroll);
		endResultsPanel.setPreferredSize(new Dimension(resultsScroll.getPreferredSize().width + 20, resultsScroll.getPreferredSize().height + endResultsTitle.getPreferredSize().height + 20));
		
		JPanel peakSizesPanel = new JPanel();
		JLabel peakSizesTitle = new JLabel("Peak Sizes");
		peakSizesTitle.setFont(titleFont);
		peakSizesPanel.add(peakSizesTitle);
		peakSizesPanel.add(peakScroll);
		peakSizesPanel.setPreferredSize(endResultsPanel.getPreferredSize());

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(exit);
		buttonPanel.add(returnToMenu);
		buttonPanel.setPreferredSize(new Dimension(returnToMenu.getPreferredSize().width + 10,
													returnToMenu.getPreferredSize().height * 2 + 10));
		
		add(endResultsPanel);
		add(peakSizesPanel);
		add(buttonPanel);
		
		setPreferredSize(new Dimension(endResultsPanel.getPreferredSize().width * 2 + 16, 
										endResultsPanel.getPreferredSize().height + buttonPanel.getPreferredSize().height + 30));
		
		//Display the top of the text areas first
		endResults.setCaretPosition(0);
		peakSizes.setCaretPosition(0);
	}
	
	//Functional Interface for a lambda expression
	private interface Size
	{
		/**
		 * Determines which size to write to a text area (current size or peak size)
		 * 
		 * @param country the country whose size to return
		 * @return the current or peak size of the country
		 */
		int size(Country country);
	}
	
	/**
	 * Writes the list of countries and their size (current or peak) to a JTextArea
	 * 
	 * @param textArea the JTextArea to write to
	 * @param countries the list of countries to display
	 * @param sizeMethod the method that returns a country's size (Country.getSize() or Country.getPeakSize())
	 */
	private void writeToTextArea(JTextArea textArea, ArrayList<Country> countries, Size sizeMethod)
	{
		for (int i = countries.size() - 1; i >= 0; i--)
		{
			Country c = countries.get(i);
			textArea.append((countries.size() - i) + ". " + c.getName() + " - ");
			int size = sizeMethod.size(c);
			if (size == 1)
			{
				textArea.append(size + " province\n");
			}
			else if (size > 1)
			{
				textArea.append(size + " provinces\n");
			}
			else //size == 0
			{
				textArea.append("Vanquished!\n");
			}
		}
	}
	
	/**
	 * create a new JTextArea to be uneditable with plain size 20 arial font,
	 * 20 rows and 20 columns.
	 * 
	 * @return a new JTextArea
	 */
	private JTextArea createTextArea()
	{
		JTextArea text = new JTextArea(20, 20);
		text.setEditable(false);
		text.setFont(new Font("Arial", Font.PLAIN, 20));
		text.setText("");
		
		return text;
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

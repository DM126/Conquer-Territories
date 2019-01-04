import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

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
		
		endResults = new JTextArea(20, 20);
		peakSizes = new JTextArea(20, 20);
		initializeTextArea(endResults);
		initializeTextArea(peakSizes);
		
		JScrollPane resultsScroll = new JScrollPane();
		resultsScroll.setViewportView(endResults);
		JScrollPane peakScroll = new JScrollPane();
		peakScroll.setViewportView(peakSizes);
		
		exit = new JButton("Exit Game");
		exit.addActionListener(new ButtonListener());
		
		returnToMenu = new JButton("Main Menu");
		returnToMenu.addActionListener(new ButtonListener());
		
		//Write the end sizes in the endResults text area
		for (int i = countries.size() - 1; i >= 0; i--)
		{
			Country c = countries.get(i);
			endResults.append((countries.size() - i) + ". " + c.getName() + " - ");
			int size = c.getSize();
			if (size == 1)
			{
				endResults.append(c.getSize() + " province\n");
			}
			else if (size > 1)
			{
				endResults.append(c.getSize() + " provinces\n");
			}
			else //size == 0
			{
				endResults.append("Vanquished!\n");
			}
		}
		
		sortByPeakSize(countries);
		
		//Write the peak sizes in the peakSizes text area
		for (int i = 0; i < countries.size(); i++)
		{
			Country country = countries.get(i);
			peakSizes.append((i+1) + ". " + country + " - ");
			int peak = country.getPeakSize();
			if (peak > 1)
			{
				peakSizes.append(peak + " provinces\n");
			}
			else //peak == 1
			{
				peakSizes.append(peak + " province\n");
			}
		}
		
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
		
		endResults.setCaretPosition(0);
		peakSizes.setCaretPosition(0);
	}
	
	/**
	 * sort the list of countries by peak size using a bubble sort (I think?).
	 * 
	 * @param countries the list of all the countries
	 */
	private void sortByPeakSize(ArrayList<Country> countries)
	{
		boolean swapped = true;
		for (int i = 0; i < countries.size() - 1 && swapped; i++)
		{	
			swapped = false;
			for (int j = countries.size() - 1; j > i; j--)
			{
				if (countries.get(j).getPeakSize() > countries.get(j-1).getPeakSize())
				{
					Collections.swap(countries, j, j-1);
					swapped = true;
				}
			}
		}
	}
	
	/**
	 * Initialize a JTextArea to be uneditable with plain size 20 arial font.
	 * 
	 * @param text the JTextArea to initialize.
	 */
	private void initializeTextArea(JTextArea text)
	{
		text.setEditable(false);
		text.setFont(new Font("Arial", Font.PLAIN, 20));
		text.setText("");
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

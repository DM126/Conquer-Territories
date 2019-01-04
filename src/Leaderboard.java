import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Leaderboard extends JPanel
{
	private JLabel title;
	private ArrayList<Country> remainingCountries; //Countries still alive
	private ArrayList<Country> vanquishedCountries; //countries removed from the game
	private JTextArea scores; //Displays the number of provinces of all remaining countries in order
	
	public Leaderboard(ArrayList<Country> countriesList)
	{
		remainingCountries = new ArrayList<Country>(countriesList.size());
		for (Country c: countriesList)
		{
			remainingCountries.add(c);
		}
		
		vanquishedCountries = new ArrayList<Country>(countriesList.size());
		
		title = new JLabel("Leaderboard");
		title.setFont(new Font("Arial", Font.PLAIN, 20));
		
		scores = new JTextArea(25, 20);
		scores.setEditable(false);
		scores.setFont(new Font("Arial", Font.PLAIN, 16));
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(scores);
		
		add(title);
		add(scroll);
		
		sortList();
		setLeaderboardText();
		
		setPreferredSize(new Dimension(scroll.getPreferredSize().width, 
										title.getPreferredSize().height + scroll.getPreferredSize().height + 20));
	}
	
	/**
	 * Removes the specified country from the leaderboard.
	 * 
	 * @param c the country to remove
	 */
	public void removeCountry(Country c)
	{
		remainingCountries.remove(c);
		vanquishedCountries.add(c);
	}
	
	/**
	 * Sets the text area to display the countries in order of size.
	 */
	public void setLeaderboardText()
	{
		scores.setText("");
		
		for (int i = 0; i < remainingCountries.size(); i++)
		{
			scores.append((i+1) + ". " + remainingCountries.get(i).getName() + " - " + remainingCountries.get(i).getSize() + " provinces\n");
		}
		
		scores.setCaretPosition(0);
	}
	
	/**
	 * Sorts the list of countries into descending order using a bubble sort.
	 */
	public void sortList()
	{
		boolean swapped = true;
		for (int i = 0; i < remainingCountries.size() - 1 && swapped; i++)
		{	
			swapped = false;
			for (int j = remainingCountries.size() - 1; j > i; j--)
			{
				if (remainingCountries.get(j).getSize() > remainingCountries.get(j-1).getSize())
				{
					Collections.swap(remainingCountries, j, j-1);
					swapped = true;
				}
			}
		}
	}
	
	/**
	 * @return true if the array list of countries is in order by size
	 */
	public boolean isInOrder()
	{
		for (int i = 0; i < remainingCountries.size() - 1; i++)
		{
			if (remainingCountries.get(i).getSize() < remainingCountries.get(i+1).getSize())
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns a vanquished country to the list of remaining countries.
	 * 
	 * @param revived the vanquished country to add back to the game
	 */
	public void reviveCountry(Country revived)
	{
		remainingCountries.add(revived); //LAZY CODE, REDO
		vanquishedCountries.remove(revived);
		sortList();
		setLeaderboardText();
	}
	
	/**
	 * Creates a list of all countries in the game in order of their vanquishing,
	 * or current size if still alive.
	 * 
	 * @return the list of countries in order of vanquishing
	 */
	public ArrayList<Country> getResults()
	{
		ArrayList<Country> finalResults = new ArrayList<Country>(vanquishedCountries);
		
		for (int i = remainingCountries.size() - 1; i >= 0; i--)
		{
			finalResults.add(remainingCountries.get(i));
		}
		
		return finalResults;
	}
}

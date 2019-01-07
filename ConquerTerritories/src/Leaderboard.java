import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Displays the countries in order of number of provinces.
 */
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
	 * Removes the specified country from the leaderboard display.
	 * 
	 * @param c the country to remove
	 */
	public void removeCountry(Country c)
	{
		remainingCountries.remove(c);
		vanquishedCountries.add(c);
	}
	
	public void sortList()
	{
		ListSorter.sortCountries(remainingCountries, ListSorter.Methods.SIZE);
	}
	
	/**
	 * Sets the text area to display the countries in order of size.
	 */
	public void setLeaderboardText()
	{
		scores.setText("");
		ComponentFactory.writeToTextArea(scores, remainingCountries, (country) -> country.getSize());
		scores.setCaretPosition(0);
	}
	
	/**
	 * Returns a vanquished country to the list of remaining countries.
	 * 
	 * @param revived the vanquished country to add back to the game
	 */
	public void reviveCountry(Country revived)
	{
		remainingCountries.add(revived);
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
		
		for (int i = 0; i < remainingCountries.size(); i++)
		{
			finalResults.add(remainingCountries.get(i));
		}
		
		return finalResults;
	}
	
	/**
	 * Saves all the information about a game to a text file to be scanned upon loading
	 * 
	 * @param settings the settings to save
	 * @throws IOException if there was an error writing to the save file
	 */
	public void saveGame(Settings settings) throws IOException
	{
		File saveData = new File("Saved Games/GameData.save");
		saveData.createNewFile();
		PrintWriter writer = new PrintWriter(saveData);
		
		//Write the game info to the text file
		writer.println(settings.toString());
		saveCountries(remainingCountries, writer);
		saveCountries(vanquishedCountries, writer);
		
		writer.close();
	}
	
	/**
	 * Saves an arraylist of countries to a text file.
	 * 
	 * @param writer writer to a text file
	 * @param countries a list of countries
	 */
	private void saveCountries(ArrayList<Country> countries, PrintWriter writer)
	{
		for (Country country : countries)
		{
			writer.println(country.getSaveData());
			writer.println(country.getPeakSize());
			writer.println(country.getVanquishes());
		}
	}
}

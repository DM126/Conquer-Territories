import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;

public class ConquerFrame extends JFrame
{
	/**
	 * Creates the frame and displays the main menu
	 */
	public ConquerFrame()
	{
		super("Conquer Territories");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setPanel(new MainMenu(this));
		
		setLocationRelativeTo(null);
		setFocusable(true);
	}
	
	/**
	 * Displays a new screen.
	 * 
	 * @param newPanel the panel to display
	 */
	private void setPanel(JPanel newPanel)
	{
		getContentPane().removeAll();
		getContentPane().add(newPanel);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
	}
	
	/**
	 * Starts the game. Called from teamSelectPanel.
	 * This version will take the arraylist of countries as an argument.
	 * 
	 * @param countries
	 * @param settings
	 */
	public void startGame(ArrayList<Country> countries, Settings settings)
	{
		setPanel(new SimulationPanel(this, countries, settings));
		//setExtendedState(JFrame.MAXIMIZED_BOTH); 
	}
	
	/**
	 * Starts the game. Called from main menu.
	 * This version will create the arraylist of countries using the chosen settings.
	 * 
	 * @param settings
	 */
	public void startGame(Settings settings)
	{
		ArrayList<Country> countries = getCountries(settings.getGame());
		startGame(countries, settings);
	}
	
	/**
	 * Takes the user to the team select screen.
	 * 
	 * @param settings the settings chosen by the user in the main menu
	 */
	public void selectTeams(Settings settings)
	{
		ArrayList<Country> countries = getCountries(settings.getGame());
		setPanel(new TeamSelectPanel(this, countries, settings));
	}
	
	/**
	 * Displays every country sorted by ending size and peak size.
	 * 
	 * @param countries the list of every country (sorted by size and order of vanquishing)
	 */
	public void showFinalResults(ArrayList<Country> countries)
	{
		setPanel(new FinalResultsPanel(this, countries));
	}
	
	/**
	 * Returns to the main menu.
	 */
	public void returnToMenu()
	{
		setPanel(new MainMenu(this));
	}
	
	/**
	 * Creates a list of countries depending on the game chosen.
	 * 
	 * @param game the game chosen by the user
	 * @return a list of every country in the chosen game
	 */
	private ArrayList<Country> getCountries(Game game)
	{	
		ArrayList<Country> countries = new ArrayList<Country>();
		File countriesFile = new File("Map Data/" + game.getCountriesFileName());
		
		try
		{
			Scanner scan = new Scanner(countriesFile);
			
			//get the countries
			while (scan.hasNext())
			{
				countries.add(new Country(scan.nextLine()));
			}
			
			scan.close();
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, "Could not find " + game.getCountriesFileName(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		return countries;
	}
}

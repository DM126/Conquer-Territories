import java.awt.Color;
import java.util.ArrayList;

/**
 * Used to represent a team on the team select screen.
 * 
 * Teams in game are simply represented as countries, this class
 * is for keeping the instance data together before creating the country.
 */
public class Team
{
	private ArrayList<Country> team;
	private String name;
	private Color color;
	
	/**
	 * Creates a team.
	 * 
	 * @param num the number used for the generic team name (i.e. 'Team 2')
	 */
	public Team(String name, Color color)
	{
		setName(name);
		setColor(color);
		team = new ArrayList<Country>();
	}
	
	public ArrayList<Country> getCountries()
	{
		return team;
	}
	
	//TODO: DUPLICATE CODE IN TeamSelectPanel!!!
	/**
	 * Adds a country to this team in the correct alphabetical location
	 * 
	 * @param country the country to add to this team
	 */
	public void addCountry(Country country)
	{
		//TODO: binary search?
		boolean added = false;
		for (int i = 0; !added && i < team.size(); i++)
		{
			if (team.get(i).getName().compareToIgnoreCase(country.getName()) > 0)
			{
				team.add(i, country);
				added = true;
			}
		}
		
		if (!added)
		{
			team.add(country);
		}
	}
	
	public boolean removeCountry(Country country)
	{
		return team.remove(country);
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String newName)
	{
		name = newName;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setColor(Color newColor)
	{
		color = newColor;
	}
	
	/**
	 * Return the name of this team.
	 */
	public String toString()
	{
		return name;
	}
}

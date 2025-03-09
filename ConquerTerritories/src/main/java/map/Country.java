package map;

import java.awt.Color;
import java.util.*;

import exception.*;
import panel.Team;

//Countries.txt format:
//name/r/g/b/provinces

public class Country
{
	private ArrayList<Province> provinces;
	private String name;
	private Color color;
	private int peakSize;
	private int vanquishes; //number of countries vanquished by this one
	private Color highlightColor;
	private int largestAttack;
	//private final double LIGHTEN = 1.25; //Scale to lighten the color when highlighting
	
	//TODO: maybe think about keeping this in an array in the worldbuilder?
	private String provinceIDs;
	
	/**
	 * Creates a country by reading from the Countries.txt file
	 * 
	 * @param data the data from the text file including the name and color
	 * @throws InvalidCountryDataException if the country's data was entered incorrectly in the text file
	 */
	public Country(String data) throws InvalidCountryDataException
	{
		provinces = new ArrayList<Province>();
		
		Scanner scan = new Scanner(data);
		scan.useDelimiter("/");
		
		//scan the name, color, and province ids
		name = scan.next();

		try
		{
			int red = scan.nextInt();
			int green = scan.nextInt();
			int blue = scan.nextInt();
			setColor(new Color(red, green, blue));
		}
		catch (InputMismatchException e)
		{
			scan.close();
			throw new InvalidCountryDataException(name);
		}
		
		if (scan.hasNext())
		{
			provinceIDs = scan.next();
		}
		else
		{
			provinceIDs = "";
		}
		
		scan.close();
		
		peakSize = 0;
		vanquishes = 0;
		largestAttack = 0;
	}
	
	/**
	 * Loads the country's data from the save game file.
	 * 
	 * @param saveData a string including the name and color to be sent to the other constructor
	 * @param peakSize the peak size of this country
	 * @param vanquishes the number of times this country vanquished another
	 * @param largestAttack the the most provinces taken in a single attack
	 */
	public Country(String saveData, int peakSize, int vanquishes, int largestAttack)
	{
		this(saveData);
		
		this.peakSize = peakSize;
		this.vanquishes = vanquishes;
		this.largestAttack = largestAttack;
	}
	
	//TODO Put this in Team class?
	/**
	 * Creates a team/alliance of countries under a single country object.
	 * 
	 * @param team the team data from the team select screen
	 */
	public Country(Team teamData)
	{
		provinces = new ArrayList<>();
		name = teamData.getName();
		color = teamData.getColor();
		
		StringBuilder provinceIDsSB = new StringBuilder();
		for (int i = 0; i < teamData.getCountries().size(); i++)
		{
			provinceIDsSB.append(teamData.getCountries().get(i).getProvinceIDs());
			if (i < teamData.getCountries().size() - 1)
			{
				provinceIDsSB.append(",");
			}
		}
		provinceIDs = provinceIDsSB.toString();
		
		peakSize = 0;
		vanquishes = 0;
		largestAttack = 0;
	}
	
	/**
	 * scans the provinceIDs taken from the text file and adds the provinces to this country.
	 * 
	 * @param worldMap the list of all provinces in the world
	 */
	public void scanProvinces(List<Province> worldMap) throws InvalidCountryDataException
	{
		Scanner scan = new Scanner(provinceIDs);
		scan.useDelimiter(",");

		int id = -1;
		try
		{
			while (scan.hasNext())
			{
				id = scan.nextInt();
				addProvince(worldMap.get(id - 1));
			}
		}
		catch (InputMismatchException e)
		{
			throw new InvalidCountryDataException(name);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new InvalidProvinceIDException(name, id);
		}
		finally
		{
			scan.close();
		}
		
		provinceIDs = "";
	}
	
	/**
	 * Adds a province to this country's list of provinces and sets
	 * this country as the owner of that province.
	 * 
	 * USE THIS FOR CHANGING PROVINCE OWNERS
	 * 
	 * @param p the province to add
	 */
	public void addProvince(Province p)
	{
		if (!provinces.contains(p))
		{
			provinces.add(p);
			
			p.setOwner(this);
			
			if (provinces.size() > peakSize)
			{
				peakSize = provinces.size();
			}
		}
	}
	
	/**
	 * Removes a province from this country's possession.
	 * 
	 * @param p the province to remove
	 */
	public void removeProvince(Province p)
	{
		provinces.remove(p);
	}
	
	/**
	 * @return a list of all the provinces belonging to this country
	 */
	public ArrayList<Province> getProvinces()
	{
		return provinces;
	}
	
	/**
	 * @return an alphabetically sorted list of countries that share a border with this one
	 */
	public ArrayList<Country> getNeighbors()
	{
		ArrayList<Country> neighboringCountries = new ArrayList<>();
		for (Province p : provinces)
		{
			for (Province adjacentProvince : p.getNeighbors())
			{
				Country provinceOwner = adjacentProvince.getOwner();
				if (provinceOwner != null && !provinceOwner.equals(this) && !neighboringCountries.contains(provinceOwner))
				{
					neighboringCountries.add(provinceOwner);
				}
			}
		}

		ListSorter.sortCountries(neighboringCountries, ComparisonMethods.ALPHABETICAL);
		return neighboringCountries;
	}
	
	/**
	 * @return the number of provinces owned by this country
	 */
	public int getSize()
	{
		return provinces.size();
	}
	
	/**
	 * @return the highest number of provinces owned by this country at any one point
	 */
	public int getPeakSize()
	{
		return peakSize;
	}
	
	/**
	 * @return the most provinces taken in a single attack by this country
	 */
	public int getLargestAttack()
	{
		return largestAttack;
	}
	
	/**
	 * @return the number of countries finished off by this country
	 */
	public int getVanquishes()
	{
		return vanquishes;
	}
	
	/**
	 * Reset the country's data to before a move.
	 * this country is the one that gained provinces in the move.
	 * 
	 * @param lastMove the last move to be undone
	 */
	public void undoMove(Move lastMove)
	{
		if (this.equals(lastMove.getNewOwner()))
		{
			peakSize = lastMove.getPeakSize();
			
			//Reset the peak size if it was changed
			if (peakSize > lastMove.getPeakSize())
			{
				peakSize = lastMove.getPeakSize();
			}
			
			if (lastMove.wasVanquishing())
			{
				vanquishes--;
			}
			
			//Reset the largest number of provinces taken if the last move broke the record
			if (largestAttack > lastMove.getLargestAttack())
			{
				largestAttack = lastMove.getLargestAttack();
			}
		}
	}
	
	/**
	 * Redo the last undone move and reset the country's data.
	 * 
	 * @param lastMove the move that was undone
	 */
	public void redoMove(Move lastMove)
	{
		if (this.equals(lastMove.getNewOwner()))
		{	
			if (provinces.size() > peakSize)
			{
				peakSize = provinces.size();
			}
			
			if (lastMove.wasVanquishing())
			{
				vanquishes++;
			}
			
			//Reset the largest number of provinces taken if the last move broke the record
			if (largestAttack > lastMove.getLargestAttack())
			{
				largestAttack = lastMove.getLargestAttack();
			}
		}
	}
	
	/**
	 * @return the string containing the province ids of this country, used for instantiation
	 */
	public String getProvinceIDs()
	{
		return provinceIDs;
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
		highlightColor = Color.BLACK; //lighten(red, green, blue);
	}
	
	public Color getHighlightColor()
	{
		return highlightColor;
	}
	
	/**
	 * Creates a string for representing this country's size in a textbox
	 * along with other countries. i.e: "20 provinces", "1 province", etc.
	 * 
	 * @return a string representation of this country's size
	 */
	public String getSizeAsString()
	{
		String output;
		int size = getSize();
		if (size > 1)
		{
			output = size + " provinces\n";
		}
		else if (getSize() == 1)
		{
			output = size + " province\n";
		}
		else
		{
			output = " vanquished!\n";
		}
		
		return output;
	}
	
	/**
	 * Creates a string for representing this country's peak size in a textbox
	 * along with other countries. i.e: "20 provinces", "1 province", etc.
	 * 
	 * @return a string representation of this country's peak size
	 */
	public String getPeakSizeAsString()
	{
		String output;
		if (peakSize == 1)
		{
			output = peakSize + " province\n";
		}
		else
		{
			output = peakSize + " provinces\n";
		}
		
		return output;
	}
	
	/**
	 * Creates a string for representing this country's number of vanquishes
	 * in a textbox along with other countries. 
	 * i.e: "7 countries", "1 country", etc.
	 * 
	 * @return a string representation of this country's vanquishes
	 */
	public String getVanquishesAsString()
	{
		String output;
		if (vanquishes == 1)
		{
			output = vanquishes + " country\n";
		}
		else
		{
			output = vanquishes + " countries\n";
		}
		
		return output;
	}
	
	/**
	 * Creates a string for representing this country's largest attack
	 * in a textbox along with other countries. i.e: "20 provinces", "1 province", etc.
	 * 
	 * @return a string representation of this country's peak size
	 */
	public String getLargestAttackAsString()
	{
		String output;
		if (largestAttack == 1)
		{
			output = largestAttack + " province\n";
		}
		else
		{
			output = largestAttack + " provinces\n";
		}
		
		return output;
	}
	
//	private Color lighten(int r, int g, int b)
//	{
//		int lightRed = (int)(r * LIGHTEN) % 255;
//		int lightGreen = (int)(g * LIGHTEN) % 255;
//		int lightBlue = (int)(b * LIGHTEN) % 255;
//		
//		return new Color(lightRed, lightGreen, lightBlue);
//	}
	
	/**
	 * Returns true if this country still exists.
	 * 
	 * @return true if this country owns at least 1 province
	 */
	public boolean hasProvinces()
	{
		return !provinces.isEmpty();
	}
	
	/**
	 * Take all bordering provinces of another country.
	 * 
	 * @param other the country to be attacked
	 * @param times the number of times to take each layer of bordering provinces
	 * @return a record of all provinces taken during this attack
	 */
	public Move attack(Country other, int times)
	{
		Move move = new Move(other, this);
		
		for (int i = 0; i < times; i++)
		{
			ArrayList<Province> border = getBorderingProvinces(other);
			
			//add the border provinces to this country
			for (Province borderProvince : border)
			{
				addProvince(borderProvince);
				move.add(borderProvince);
			}
		}
		
		if (move.wasVanquishing())
		{
			vanquishes++;
		}
		
		if (move.getNumberOfProvincesTaken() > largestAttack)
		{
			largestAttack = move.getNumberOfProvincesTaken();
		}
		
		return move;
	}
	
	/**
	 * Take a list of specific provinces in an attack.
	 * Differs from attack() in that this method can take any provinces,
	 * not just provinces along the border.
	 * 
	 * @param selectedProvinces the list of provinces to take
	 * @return a record of the attack
	 */
	public Move takeProvinces(List<Province> selectedProvinces)
	{
		Move move = new Move(selectedProvinces.get(0).getOwner(), this);
		
		for (Province province : selectedProvinces)
		{
			addProvince(province);
			move.add(province);
		}
		
		if (move.wasVanquishing())
		{
			vanquishes++;
		}
		
		if (move.getNumberOfProvincesTaken() > largestAttack)
		{
			largestAttack = move.getNumberOfProvincesTaken();
		}
		
		return move;
	}
	
	/**
	 * Finds all the provinces of the other country that border this one.
	 * 
	 * @param other the country to find the border with
	 * @return a list of all provinces of the other country that are adjacent to provinces of this country
	 */
	public ArrayList<Province> getBorderingProvinces(Country other)
	{
		//TODO: perhaps keep track of the border at all times rather than finding a specific border every attack?
		ArrayList<Province> border = new ArrayList<>();
		//p = a province of the defending country
		//TODO: check the provinces of the country with less provinces
		for (Province p : other.getProvinces())
		{
			if (p.bordersCountry(this))
			{
				border.add(p);
			}
		}
		
		return border;
	}
	
	/**
	 * Takes every province from a different country
	 * 
	 * @param other the country to vanquish
	 * @return a record of all provinces taken
	 */
	public Move vanquish(Country other)
	{
		Move move = new Move(other, this);
		
		while (other.hasProvinces())
		{
			Province p = other.getProvinces().get(other.getSize() - 1);
			addProvince(p);
			move.add(p);
		}
		
		vanquishes++;
		
		if (move.getNumberOfProvincesTaken() > largestAttack)
		{
			largestAttack = move.getNumberOfProvincesTaken();
		}
		
		return move;
	}
	
	/**
	 * Checks if this country shares a border with another.
	 * 
	 * @param otherCountry the other country to compare with this one
	 * @return true if any provinces of this country has a neighbor belonging to the other country
	 */
	public boolean borders(Country otherCountry)
	{
		for (Province p : provinces)
		{
			if (p.bordersCountry(otherCountry))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets the country's data in a string of format: 
	 * name/red/green/blue/provinces
	 * 
	 * @return a string representing the data of the country to be saved.
	 */
	public String getSaveData()
	{
		//get the name and rgb color values
		StringBuilder data = new StringBuilder(name + "/" + color.getRed() + "/" + color.getGreen() + "/" + color.getBlue() + "/");
		
		//get the provinces
		for (int i = 0; i < provinces.size(); i++)
		{
			data.append(provinces.get(i).getID());
			if (i < provinces.size() - 1)
			{
				data.append(",");
			}
		}
		
		return data.toString();
	}
	
	public String toString()
	{
		return name;
	}
}

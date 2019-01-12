import java.awt.Color;
import java.util.*;

//Countries.txt format:
//name/r/g/b/provinces

public class Country
{
	private ArrayList<Province> provinces;
	private String name;
	private Color color;
	private int peakSize;
	private int vanquishes; //number of countries vanquished by this one //TODO: display after game
	private Color highlightColor;
	private int largestAttack;
	//private final double LIGHTEN = 1.25; //Scale to lighten the color when highlighting
	
	//TODO: maybe think about keeping this in an array in the worldbuilder?
	private String provinceIDs;
	
	/**
	 * Creates a country by reading from the Countries.txt file
	 * 
	 * @param data the data from the text file including the name and color
	 */
	public Country(String data)
	{
		provinces = new ArrayList<Province>();
		
		Scanner scan = new Scanner(data);
		scan.useDelimiter("/");
		
		//scan the name, color, and province ids
		name = scan.next();
		int red = scan.nextInt();
		int green = scan.nextInt();
		int blue = scan.nextInt();
		setColor(new Color(red, green, blue));
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
	 * @param savedata a string including the name and color to be sent to the other constructor
	 * @param peakSize the peak size of this country
	 * @param vanquishes the number of times this country vanquished another
	 */
	public Country(String savedata, int peakSize, int vanquishes, int largestAttack)
	{
		this(savedata);
		
		this.peakSize = peakSize;
		this.vanquishes = vanquishes;
		this.largestAttack = largestAttack;
	}
	
	/**
	 * Creates a team/alliance of countries under a single country object.
	 * 
	 * @param team the team data from the team select screen
	 */
	public Country(Team teamData)
	{
		provinces = new ArrayList<Province>();
		name = teamData.getName();
		color = teamData.getColor();
		
		provinceIDs = "";
		for (int i = 0; i < teamData.getCountries().size(); i++)
		{
			provinceIDs += teamData.getCountries().get(i).getProvinceIDs();
			if (i < teamData.getCountries().size() - 1)
			{
				provinceIDs += ",";
			}
		}
		
		peakSize = 0;
		vanquishes = 0;
		largestAttack = 0;
	}
	
	/**
	 * scans the provinceIDs taken from the text file and adds the provinces to this country.
	 * 
	 * @param worldMap the list of all provinces in the world
	 */
	public void scanProvinces(ArrayList<Province> worldMap)
	{
		Scanner scan = new Scanner(provinceIDs);
		scan.useDelimiter(",");

		while (scan.hasNext())
		{
			addProvince(worldMap.get(scan.nextInt() - 1));
		}
		
		scan.close();
		
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
	 * @return a list of countries that share a border with this one
	 */
	public ArrayList<Country> getNeighbors()
	{
		ArrayList<Country> neighboringCountries = new ArrayList<Country>();
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

		ListSorter.sortCountries(neighboringCountries, ListSorter.Methods.ALPHABETICAL);
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
	 * Take a list of specific province in an attack.
	 * Differs from attack() in that this method can take any provinces,
	 * not just provinces along the border.
	 * 
	 * @param selectedProvinces the list of provinces to take
	 * @return a record of the attack
	 */
	public Move takeProvinces(ArrayList<Province> selectedProvinces)
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
		ArrayList<Province> border = new ArrayList<Province>();
		//p = a province of the defending country
		//TODO: check the provinces of the country with less provinces
		for (Province p : other.getProvinces())
		{
			//TODO: put this in Province class?
			boolean adjacent = false;
			for (int j = 0; j < p.getNeighbors().size() && !adjacent; j++)
			{
				//check if each of the other country's individual provinces borders a province of this country
				Country neighborCountry = p.getNeighbors().get(j).getOwner();
				if (neighborCountry != null && neighborCountry.equals(this))
				{
					adjacent = true;
					border.add(p);
				}
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
			//check the neighbors of each province to see if their owner matches the parameter
			for (Province neighbor : p.getNeighbors())//Refactor?
			{
				Country neighborCountry = neighbor.getOwner();
				if (neighborCountry != null && neighborCountry.equals(otherCountry))
				{
					return true;
				}
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
		String data = name + "/" + color.getRed() + "/" + color.getGreen() + "/" + color.getBlue() + "/";
		
		//get the provinces
		for (int i = 0; i < provinces.size(); i++)
		{
			data += provinces.get(i).getID();
			if (i < provinces.size() - 1)
			{
				data += ",";
			}
		}
		
		return data;
	}
	
	public String toString()
	{
		return name;
	}
}

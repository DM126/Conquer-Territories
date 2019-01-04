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
	private int vanquishes; //number of countries vanquished by this one
	private Color highlightColor;
	//private final double LIGHTEN = 1.25; //Scale to lighten the color when highlighting
	
	//TODO: maybe think about keeping this in an array in the worldbuilder?
	private String provinceIDs;
	
	/**
	 * Creates a country by reading from the Countries.txt file
	 * 
	 * @param data the data from the text file including the name and color (Hex)
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
		provinceIDs = scan.next();
		
		scan.close();
		
		peakSize = 0;
		vanquishes = 0;
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
	
	public ArrayList<Province> getProvinces()
	{
		return provinces;
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
	 * @return the number of countries finished off by this country
	 */
	public int getVanquishes()
	{
		return vanquishes;
	}
	
	/**
	 * Undo a vanquish after bringing a vanquished country back to life.
	 * Should only ever be called during Move.undo()
	 * 
	 * @param lastMove the previous move
	 */
	public void undoVanquish()
	{
		vanquishes--;
	}
	
	/**
	 * Resets the peak size to what it was before an attack.
	 * Should only ever be called during Move.undo()
	 * 
	 * @param lastMove the previous move
	 */
	public void resetPeakSize(Move lastMove)
	{
		if (this == lastMove.getNewOwner()) //purposefully comparing memory address here.
		{
			peakSize = lastMove.getPeakSize();
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
		
		return move;
	}
	
	/**
	 * Take a single province in an attack.
	 * (Not to be used for simply adding a province, use addProvince() instead)
	 * 
	 * @param province the province to take
	 * @return a record of the attack
	 */
	public Move takeProvince(Province province)
	{
		Move move = new Move(province.getOwner(), this);
		
		addProvince(province);
		move.add(province);
		
		if (move.wasVanquishing())
		{
			vanquishes++;
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
			boolean adjacent = false;
			for (int j = 0; j < p.getNeighbors().size() && !adjacent; j++)
			{
				//check if each of the other country's individual provinces borders a province of this country
				if (p.getNeighbors().get(j).getOwner().equals(this))
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
				if (neighbor.getOwner().equals(otherCountry))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Returns a string containing all the data of this country for debug purposes.
	 * 
	 * @return the name, color, and provinces of this country as a string
	 */
	public String getData()
	{
		String data = name + ": " + color + ". ";
		for (Province province : provinces)
		{
			data += province + ", ";
		}
		
		return data;
	}
	
	public String toString()
	{
		return name;
	}
}

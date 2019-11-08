import java.awt.*;
import java.util.*;

//Provinces.txt format:
//id/r/g/b/name/sea-adjacencies

public class Province
{
	private Polygon polygon;
	private Country owner;
	private Color bmpColor;
	private ArrayList<Province> neighbors;
	private int id;
	private String name;
	private boolean isHighlighted;
	
	//TODO: Maybe think of keeping this in an array in the worldbuilder rather than holding onto it?
	private String adjacenciesData; //sea-adjacent province IDs
	
	/**
	 * Creates a province with data from a text file.
	 * 
	 * @param data the data from the text file
	 * @throws NoSuchElementException if the text file data is missing the id, color, or name
	 */
	public Province(String data) throws NoSuchElementException
	{
		neighbors = new ArrayList<Province>();
		
		Scanner scan = new Scanner(data);
		scan.useDelimiter("/");
		
		id = scan.nextInt();
		bmpColor = new Color(scan.nextInt(), scan.nextInt(), scan.nextInt());
		name = scan.next();
		
		if (scan.hasNext())
		{
			adjacenciesData = scan.next();
		}
		
		scan.close();
		
		isHighlighted = false;
	}
	
	/**
	 * Sets the shape of this province. Only called from the worldbuilder.
	 * 
	 * @param border the list of points along the border of this province
	 */
	public void setPolygon(ArrayList<MapPoint> border)
	{
		int[] borderX = new int[border.size()];
		int[] borderY = new int[border.size()];
		for (int j = 0; j < border.size(); j++)
		{
			borderX[j] = border.get(j).getX();
			borderY[j] = border.get(j).getY();
		}
		
		polygon = new Polygon(borderX, borderY, border.size());
		
		//DEBUG - for seeing if the polygons work
		//for (int i = 0; i < shape.xpoints.length; i++)
		//{
		//	System.out.print(shape.xpoints[i] + ", ");
		//}
		//System.out.println();
	}
	
	/**
	 * Uses data scanned from the text file to set the sea adjacencies.
	 * Only called in the MapPanel constructor.
	 * 
	 * @param provinces the list of all provinces on the map
	 */
	public void setSeaAdjacencies(ArrayList<Province> provinces)
	{
		if (adjacenciesData != null)
		{
			//System.out.println(this.toString());
			Scanner scan = new Scanner(adjacenciesData);
			scan.useDelimiter(",");
			
			while (scan.hasNext())
			{
				int provinceID = scan.nextInt() - 1;
				neighbors.add(provinces.get(provinceID));
			}
			
			scan.close();
			
			adjacenciesData = "";
		}
	}
	
	/**
	 * Compares colors found adjacent on the image file to colors in the list of
	 * provinces to set the adjacent provinces.
	 * 
	 * @param worldProvinces the list of all provinces on the map
	 * @param adjColors the list of all colors adjacent to this province in the image
	 */
	public void setAdjacencies(HashMap<Integer, Province> worldProvinces, HashSet<Integer> adjColors)
	{
		for (Integer rgb : adjColors)
		{
			neighbors.add(worldProvinces.get(rgb));
		}
	}
	
	/**
	 * Used for drawing the province on the MapPanel.
	 * For the color of this province on the bitmap file, use getMapColor().
	 * 
	 * @return the color of this province's owner
	 */
	public Color getColor()
	{	
		if (isHighlighted)
		{
			return owner.getHighlightColor();
		}
		
		return owner.getColor();
	}

	/**
	 * Used for determining the shape of the province to create the map.
	 * For drawing this province on screen use getColor().
	 * 
	 * @return the color of this province on the map.bmp image file
	 */
	public Color getBMPColor()
	{
		return bmpColor;
	}
	
	/**
	 * @return the unique ID of this province
	 */
	public int getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return a list of provinces adjacent to this one.
	 */
	public ArrayList<Province> getNeighbors()
	{
		return neighbors;
	}
	
	/**
	 * @return the Country that currently owns this province
	 */
	public Country getOwner()
	{
		return owner;
	}
	
	public Polygon getPolygon()
	{
		return polygon;
	}
	
	/**
	 * DO NOT USE THIS FOR CHANGING PROVINCES DIRECTLY,
	 * USE Country.addProvince() INSTEAD
	 * 
	 * @param newOwner the new owner of this province
	 */
	public void setOwner(Country newOwner)
	{
		if (owner != null)
		{
			owner.removeProvince(this);
		}
		
		owner = newOwner;
	}
	
	/**
	 * @return true if this province is being highlighted on the map.
	 */
	public boolean isHighlighted()
	{
		return isHighlighted;
	}
	
	/**
	 * sets whether this province is highlighted or not.
	 * 
	 * @param state the new highlight state of this province
	 */
	public void setHighlighted(boolean state)
	{
		isHighlighted = state;
	}
	
	/**
	 * Returns true if this province is on the border of the specified country,
	 * i.e. It could be taken in the next attack.
	 * 
	 * @param country
	 */
	public boolean bordersCountry(Country country)
	{
		if (owner != country)
		{
			for (Province neighbor : neighbors)
			{
				if (neighbor.getOwner() != null && neighbor.getOwner().equals(country))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * @return the province id and name of the province. Used for debugging.
	 */
	public String toString()
	{
		return id + ": " + name;
	}
}

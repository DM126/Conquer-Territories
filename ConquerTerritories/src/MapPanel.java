import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Class used to display the map within the game panel.
 */
public class MapPanel extends JPanel
{
	private static final int MIN_WIDTH = 600;
	private static final int MIN_HEIGHT = 600;
	private static final int MAX_WIDTH = 1500;
	private static final int MAX_HEIGHT = 800;
	
	private ArrayList<Country> countries;
	private ArrayList<Province> provinces;
	
	//TODO: comment this explaning where the exceptions can come from
	public MapPanel(ArrayList<Country> countries, Game game) throws IOException, ColorNotFoundException
	{
		this.countries = countries;
		
		//Create the list of provinces by reading the text file.
		File provinceFile = new File("Map Data/" + game.getProvincesFileName());
		Scanner scan = new Scanner(provinceFile);
		provinces = new ArrayList<Province>();
		while (scan.hasNext())
		{
			provinces.add(new Province(scan.nextLine()));
		}
		scan.close();
		
		//Add the provinces to each country.
		for (Country country : countries)
		{
			country.scanProvinces(provinces);
		}
		
		for (Province province : provinces)
		{
			province.setSeaAdjacencies(provinces);
		}
		
		//Create polygons and set adjacencies by reading the map image file
		WorldBuilder wb = new WorldBuilder(provinces, game);
		
		setPreferredSize(wb.getDimensions());
		setBackground(Color.WHITE);
		
		repaint();
	}
	
	public void paintComponent(Graphics page)
	{
		super.paintComponent(page);
		
		//Draw provinces
		for (Province p : provinces)
		{
			//If an exception is thrown here it means you forgot to update the map image file, 
			//or you left a province out of the countries text file
			//try
			//{
				page.setColor(p.getColor());
			//}
			//catch (NullPointerException e)
			//{
			//	System.out.println(p.getID());
			//}
			page.drawPolygon(p.getPolygon());
		}
	}
	
	/**
	 * @return the list of countries currently being displayed on the map
	 */
	public ArrayList<Country> getCountries()
	{
		return countries;
	}
	
	/**
	 * Returns a vanquished country to the list of countries.
	 */
	public void reviveCountry(Country c)
	{
		ListSorter.addToCorrectLocation(countries, c, ListSorter.Methods.ALPHABETICAL);
	}
	
	/**
	 * @return the preferred size to display this panel within a JScrollPane
	 */
	public Dimension getViewportSize()
	{
		int mapWidth = getPreferredSize().width;
		int mapHeight = getPreferredSize().height;
		
		if (mapWidth > MAX_WIDTH || mapHeight > MAX_HEIGHT)
		{
			return new Dimension(MAX_WIDTH, MAX_HEIGHT);
		}
		else if (mapWidth < MIN_WIDTH || mapHeight < MIN_HEIGHT)
		{
			return new Dimension(MIN_WIDTH, MIN_HEIGHT);
		}
		else
		{
			return new Dimension(mapWidth, mapHeight);
		}
	}
}

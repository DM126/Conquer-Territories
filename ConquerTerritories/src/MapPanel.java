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
	private static final Color NO_OWNER = Color.LIGHT_GRAY; //Used to draw provinces that don't belong to any country
	
	private ArrayList<Country> countries;
	private ArrayList<Province> provinces;
	
	/**
	 * Creates the panel to display the map and instantiates the provinces.
	 * 
	 * @param countries the list of every country on the map
	 * @param game the chosen game
	 * @throws IOException If there is an issue reading the province text file
	 * @throws ColorNotFoundException if a province's color is entered incorrectly in the text file
	 * @throws NoSuchElementException if a province's data in the text file is missing information
	 */
	public MapPanel(ArrayList<Country> countries, Game game) throws IOException, ColorNotFoundException, NoSuchElementException
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
			//If an exception is thrown here it means you forgot to update the map image file.
			if (p.getOwner() != null)
			{
				page.setColor(p.getColor());
			}
			else
			{
				page.setColor(NO_OWNER);
				//DEBUG:
				//System.out.println(p);
			}
			page.drawPolygon(p.getPolygon());
		}
	}
	
	/**
	 * @return the list of countries currently on the map
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
		ListSorter.addToCorrectLocation(countries, c, ComparisonMethods.ALPHABETICAL);
	}
	
	/**
	 * @return the preferred size to display this panel within a JScrollPane
	 */
	public Dimension getViewportSize()
	{
		//The +5 offsets are to prevent a scrollbar from showing up on a size within the valid range
		int mapWidth = getValidLength(getPreferredSize().width + 5, MIN_WIDTH, MAX_WIDTH);
		int mapHeight = getValidLength(getPreferredSize().height + 5, MIN_HEIGHT, MAX_HEIGHT);
		
		return new Dimension(mapWidth, mapHeight);
	}
	
	/**
	 * Fits a side length to be within a minimum or maximum value.
	 * If the original value is greater than the maximum, the maximum will be returned.
	 * If the original value is less than the minimum, the minimum will be returned.
	 * otherwise the original value is valid and will be returned
	 * 
	 * @param originalValue the side length to fit within the range
	 * @param maxValue the maximum side length
	 * @param minValue the minimum side length
	 * @return a value within the specified range
	 */
	private int getValidLength(int originalValue, int minValue, int maxValue)
	{
		if (originalValue >= maxValue)
		{
			return maxValue;
		}
		else if (originalValue <= minValue)
		{
			return minValue;
		}
		
		return originalValue;
	}
}
